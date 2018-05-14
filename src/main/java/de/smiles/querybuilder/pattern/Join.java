package de.smiles.querybuilder.pattern;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class Join {

    private final String parent;
    private final Join.Type type;
    private final Table table;

    Join(String parent, Type type, Table table) {
        this.parent = parent;
        this.type = type;
        this.table = table;
    }

    public On on(String expr) {
        return new On(toString(), expr);
    }

    @Override
    public String toString() {
        return parent + " " + type.name() + " JOIN " + table.toString();
    }

    enum Type {
        LEFT, RIGHT, INNER, OUTER;
    }

    public static class On {

        private final String parent;
        private final String expr;

        On(String parent, String expr) {
            this.parent = parent;
            this.expr = expr;
        }

        @Override
        public String toString() {
            return parent + " ON " + expr;
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
    }
}
