package de.smiles.querybuilder

import de.smiles.querybuilder.Query.Companion.tableNameOf
import java.beans.Introspector
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

class Query(val prefixMapper: (TableType) -> String = { tt -> tt.tablePrefix() }) {
    companion object {
        fun tableNameOf(clazz: Class<*>): String =
                clazz.getAnnotation(Table::class.java)?.value ?: clazz.name.toSnakeCase()

        fun tableOf(clazz: Class<*>): TableType =
                TableType(tableNameOf(clazz), argsOf(clazz))
    }

    fun select(vararg args: Any): Select = Select(this, args as Array<Any>)

    fun insertInto(arg: Any): Insert = Insert(arg)
}

annotation class Column(val value: String)
annotation class Table(val value: String)

class Arg(val column: String, val name: String = column) {
    fun withPrefix(tableName: String, prefix: String, concat: String = "."): String = "$tableName.$column AS \"$prefix$concat$name\""

    override fun toString(): String = column
}

open class TableType(val tableName: String, val column: List<Arg>, val parent: TableType? = null) {

    fun tablePrefix(concat: String = "."): String = (parent?.let { this.tablePrefix() + concat } ?: "") + tableName

    open fun toSelectHead(prefixMapper: (TableType) -> String): String = column.map { it.withPrefix(tableName, prefixMapper.invoke(this)) }
            .reduce({ acc, s -> acc.plus(", ").plus(s) })

    fun name(name: String): TableType = TableType(name, column, parent)
    fun prefix(prefix: String): TableType = PrefixedTableType(tableName, column, prefix)

    override fun toString(): String = tableName

    fun name(): String = tableName
}

class PrefixedTableType(tableName: String, column: List<Arg>, val prefix: String) : TableType(tableName, column, null) {
    override fun toSelectHead(prefixMapper: (TableType) -> String): String = column.map { it.withPrefix(tableName, prefix) }
            .reduce({ acc, s -> acc.plus(", ").plus(s) })
}

interface SQL {
    fun toSQL(): String
}

interface Selection : SQL

fun argsOf(clazz: Class<*>): List<Arg> =
        Introspector.getBeanInfo(clazz)!!.propertyDescriptors.filter { it.name != "class" }.map {
            val column: String = listOf(it.readMethod, it.writeMethod)
                    .map(Method::getAnnotations).map(Arrays::asList).map {
                        it.firstOrNull { it.javaClass == Column::class.java } as Column?
                    }.firstOrNull()?.value ?: it.displayName
            Arg(column, it.name.toSnakeCase())
        }

fun String.toSnakeCase(): String {
    var sc = "" + Character.toLowerCase(this[0])
    this.substring(1).forEach { sc += if (it.isUpperCase()) "_" + Character.toLowerCase(it) else it }
    return sc
}

class Select(private val query: Query, val args: Array<Any>) {
    companion object {
        private val tableInformation: ConcurrentHashMap<Class<*>, TableType> = ConcurrentHashMap()
    }

    private val head: ArrayList<TableType> = ArrayList()

    init {
        for (arg in args) when (arg) {
            is Class<*> -> head.add(tableInformation.computeIfAbsent(arg, { Query.tableOf(it) }))
            is TableType -> head.add(arg)
            is Arg -> head.add(TableType("", listOf(arg)))
            else -> head.add(TableType("", listOf(Arg(arg.toString()))))
        }
    }

    fun from(vararg args: Any): From = From(this, args as Array<Any>)

    fun toSQL(): String = "SELECT " + head.map { it.toSelectHead(query.prefixMapper) }.reduce({ acc, s -> acc.plus(", ").plus(s) })
}

class Insert(val arg: Any) {

    val tableType: TableType = when (arg) {
        is Class<*> -> Query.tableOf(arg)
        is TableType -> arg
        is Arg -> TableType("", listOf(arg))
        else -> TableType("", listOf(Arg(arg.toString())))
    }

    fun toSQL(): String = "INSERT INTO " + tableType.tableName + "(" + tableType.column.map { it.toString() }.reduce { acc, a -> acc.plus(", ").plus(a) } + ")"

    fun values(mapper: (Arg) -> String = { arg -> ":" + arg.name }): Values = Values(this, mapper)
}

class Values(val parent: Insert, val mapper: (Arg) -> String) : SQL {

    override fun toSQL(): String = parent.toSQL() + " VALUES (" + parent.tableType.column.map { mapper.invoke(it) }.reduce { acc, s -> acc.plus(", ").plus(s) } + ")"
}

class From(private val parent: Select, val args: Array<Any>) : Selection {

    private val head: ArrayList<String> = ArrayList()

    init {
        for (arg in args) when (arg) {
            is Class<*> -> head.add(tableNameOf(arg))
            is TableType -> head.add(arg.tableName)
            else -> head.add(arg.toString())
        }
    }

    fun leftJoin(table: Any) = Join(this, Join.Type.LEFT, table)
    fun rightJoin(table: Any) = Join(this, Join.Type.RIGHT, table)
    fun innerJoin(table: Any) = Join(this, Join.Type.INNER, table)
    fun outerJoin(table: Any) = Join(this, Join.Type.OUTER, table)
    fun where(expr: String) = Where(this, expr)
    fun orderBy(table: Any, sortOrder: SortOrder = SortOrder.ASC) = Order(this, table, sortOrder)
    fun limit(limit: Int): Limit = Limit(this, limit)
    fun offset(offset: Int): Offset = Offset(this, offset)

    override fun toSQL(): String = parent.toSQL() + " FROM " + head.reduce({ acc, s -> acc.plus(", ").plus(s) })
}

class Join(private val parent: Selection, private val type: Type, private val table: Any) : Selection {

    private val tableName: String = when (table) {
        is Class<*> -> tableNameOf(table)
        is TableType -> table.tableName
        else -> table.toString()
    }

    fun on(expr: String): On = On(this, expr)
    fun where(expr: String) = Where(this, expr)
    fun orderBy(table: Any, sortOrder: SortOrder = SortOrder.ASC) = Order(this, table, sortOrder)
    fun limit(limit: Int): Limit = Limit(this, limit)
    fun offset(offset: Int): Offset = Offset(this, offset)

    enum class Type(val sql: String) {
        LEFT("LEFT"), RIGHT("RIGHT"), INNER("INNER"), OUTER("FULL OUTER")
    }

    override fun toSQL(): String = parent.toSQL() + " " + type.sql + " JOIN " + tableName
}

class On(private val parent: Join, private val expr: String) : Selection {
    fun where(expr: String) = Where(this, expr)
    fun orderBy(table: Any, sortOrder: SortOrder = SortOrder.ASC) = Order(this, table, sortOrder)
    fun limit(limit: Int): Limit = Limit(this, limit)
    fun offset(offset: Int): Offset = Offset(this, offset)

    override fun toSQL(): String = parent.toSQL() + " ON " + expr
}

class Where(private val parent: Selection, private val expr: String) : Selection {
    override fun toSQL(): String = parent.toSQL() + " WHERE " + expr

    fun orderBy(table: Any, sortOrder: SortOrder = SortOrder.ASC) = Order(this, table, sortOrder)
    fun limit(limit: Int): Limit = Limit(this, limit)
    fun offset(offset: Int): Offset = Offset(this, offset)
}

class Order(private val parent: Selection, table: Any, private val sortOrder: SortOrder) : Selection {

    private val tableName: String = table.toString()

    fun limit(limit: Int): Limit = Limit(this, limit)
    fun offset(offset: Int): Offset = Offset(this, offset)

    override fun toSQL(): String = parent.toSQL() + " ORDER BY " + tableName + " " + sortOrder.sql
}

enum class SortOrder(val sql: String) {
    ASC("ASC"), DESC("DESC")
}

class Offset(private val parent: Selection, private val offset: Int) : Selection {
    override fun toSQL(): String = parent.toSQL() + " OFFSET " + offset

    fun limit(limit: Int): Limit = Limit(this, limit)
}

class Limit(private val parent: Selection, private val limit: Int) : Selection {
    override fun toSQL(): String = parent.toSQL() + " LIMIT " + limit
}