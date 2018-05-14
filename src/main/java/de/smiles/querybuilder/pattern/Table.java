package de.smiles.querybuilder.pattern;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public interface Table {

    static TableReference ref(String table) {
        return new TableReference(table);
    }

    static Select select(Object... args) {
        return new Select(args);
    }

    String toString();
}
