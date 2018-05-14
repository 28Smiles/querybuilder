# querybuilder
A simple libary for building sql querys in java, with less stringyness and more flexybillity

You want to use SQL instead of JPA but still have some modularity in your querys? Then use this, elsewise if you are a fan of Hibernate and JPA please pass on.

```
select(  
    ref(table1).as(foo),  
    ref(table2).as(bar))  
.from(  
        ref(t1),  
        select("*").from(ref(allee)).where(allee + ".id = 2").as(table2))  
.where(table1 + ".value = 12")  
.toString()  
```
```
SELECT table1 "foo", table1 "bar" FROM t1, (SELECT * FROM allee WHERE allee.id = 2) table1 WHERE table1.value = 12
```
