package de.smiles.querybuilder;

import java.util.function.BiFunction;

public class Q {

    private final Class<?>[] args;

    private Q(Class<?>[] args) {
        this.args = args;
    }

    public static Q table(Class<?>... args) {
        return new Q(args);
    }

    public String query(BiFunction<Query, TableType[], SQL> provider) {
        Query query = new Query();
        TableType[] tt = new TableType[args.length];
        for (int i = 0; i < args.length; i++) tt[i] = Query.Companion.tableOf(args[i]);
        return provider.apply(new Query(), tt).toSQL();
    }
}
