package de.smiles.querybuilder.pattern;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class NamedSelect implements Table {

    private final String parent;
    private final String name;

    NamedSelect(String parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public String toString() {
        return "(" + parent + ") " + name;
    }
}
