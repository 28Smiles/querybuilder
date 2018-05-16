# querybuilder
A simple libary for building sql querys in java, with less stringyness and more flexybillity

You want to use SQL instead of JPA but still have some modularity in your querys? Then use this, elsewise if you are a fan of Hibernate and JPA please pass on.

```
Q.table(TestBean.class, FooBean.class).query(((q, t) ->
                q.select(t[0])
                        .from(t[0])
                        .orderBy(t[0] + ".id", SortOrder.ASC)
                        .offset(0)
                        .limit(30))));
```
```
SELECT test_bean.bar AS "test_bean.bar", test_bean.foo AS "test_bean.foo", test_bean.id AS "test_bean.id" FROM test_bean ORDER BY test_bean.id ASC OFFSET 0 LIMIT 30
```
