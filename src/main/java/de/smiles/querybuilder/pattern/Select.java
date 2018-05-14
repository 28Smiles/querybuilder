package de.smiles.querybuilder.pattern;

import java.util.Arrays;
import java.util.List;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class Select {

    private final List<Object> args;

    Select(Object... args) {
        this.args = Arrays.asList(args);
    }

    public From from(Table... tables) {
        return new From(toString(), tables);
    }

    @Override
    public String toString() {
        return "SELECT " + args.stream().map(t -> {
            if (t instanceof TableReference || t instanceof TableReference.NamedTableReference || t instanceof String) {
                return t.toString();
            } else {
                return "(" + t.toString() + ")";
            }
        }).reduce("", (a, ts) -> a + ", " + ts).substring(2);
    }
}
