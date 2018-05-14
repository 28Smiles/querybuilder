package de.smiles.querybuilder.pattern;

import java.util.Arrays;
import java.util.List;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class From {

    private final String parent;
    private final List<Table> args;

    From(String parent, Table... args) {
        this.parent = parent;
        this.args = Arrays.asList(args);
    }

    public Join leftJoin(Table table) {
        return new Join(toString(), Join.Type.LEFT, table);
    }

    public Join rightJoin(Table table) {
        return new Join(toString(), Join.Type.RIGHT, table);
    }

    public Join innerJoin(Table table) {
        return new Join(toString(), Join.Type.INNER, table);
    }

    public Join outerJoin(Table table) {
        return new Join(toString(), Join.Type.OUTER, table);
    }

    public Where where(String expr) {
        return new Where(toString(), expr);
    }

    public NamedSelect as(String name) {
        return new NamedSelect(toString(), name);
    }

    @Override
    public String toString() {
        return parent + " FROM " + args.stream().map(Object::toString).reduce("", (a, ts) -> a + ", " + ts).substring(2);
    }

    public String build() {
        return toString();
    }
}
