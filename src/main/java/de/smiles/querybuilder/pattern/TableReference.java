package de.smiles.querybuilder.pattern;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class TableReference implements Table{

    private final String tableIdentifier;

    TableReference(String tableName) {
        this.tableIdentifier = tableName;
    }

    public NamedTableReference as(String name) {
        return new NamedTableReference(toString(), name);
    }

    @Override
    public String toString() {
        return tableIdentifier;
    }

    public static class NamedTableReference implements Table {

        private final String parent;
        private final String name;

        NamedTableReference(String parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public String toString() {
            return parent + " \"" + name + "\"";
        }
    }
}
