package de.smiles.querybuilder;

import de.smiles.querybuilder.pattern.Select;
import de.smiles.querybuilder.pattern.Table;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class Query {

    public static Select select(Object... args) {
        return Table.select(args);
    }
}
