import de.smiles.querybuilder.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;

import static de.smiles.querybuilder.pattern.Table.ref;
import static de.smiles.querybuilder.pattern.Table.select;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
@ExtendWith(H2Extension.class)
class TestBuilder {

    private final String table1 = "table1";
    private final String table2 = "table1";
    private final String foo = "foo";
    private final String bar = "bar";
    private final String t1 = "t1";
    private final String allee = "allee";

    @Test
    void testBuildSimple() {
        final String query = select(
                    ref(table1).as(foo),
                    ref(table2).as(bar))
                .from(
                        ref(t1),
                        select("*").from(ref(allee)).where(allee + ".id = 2").as(table2))
                .where(table1 + ".value = 12")
                .toString();

        assertEquals("SELECT table1 \"foo\", table1 \"bar\" FROM t1, (SELECT * FROM allee WHERE allee.id = 2) table1 WHERE table1.value = 12", query);
    }

    @Test
    void testSimple(Connection connection) throws SQLException {
        connection.prepareStatement("CREATE TABLE " + allee + " ( id bigint, value integer )").executeUpdate();
        connection.prepareStatement("INSERT INTO " + allee + " VALUES (1, 12)").executeUpdate();

        var ps3 = connection.prepareStatement(select(ref("id"), ref("value").as("foo")).from(ref(allee)).where("id = 1").toString());
        var res = ps3.executeQuery();
        res.first();
        assertEquals(1, res.getLong("id"));
        assertEquals(12, res.getInt("foo"));
    }
}
