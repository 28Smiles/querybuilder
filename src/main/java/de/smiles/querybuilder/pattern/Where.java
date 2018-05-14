package de.smiles.querybuilder.pattern;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class Where {

    private final String parent;
    private final String expr;

    Where(String parent, String expr) {
        this.parent = parent;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return parent + " WHERE " + expr;
    }

    private Where and(String expr) {
        return new Where(toString(), " AND " + expr);
    }

    private Where or(String expr) {
        return new Where(toString(), " OR " + expr);
    }

    public NamedSelect as(String name) {
        return new NamedSelect(toString(), name);
    }
}
