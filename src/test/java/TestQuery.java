import de.smiles.querybuilder.Q;
import de.smiles.querybuilder.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(H2Extension.class)
class TestQuery {

    @Test
    void testWithDatabase(Connection connection) throws SQLException {
        connection.prepareStatement("CREATE TABLE test_bean (id bigint, foo text, bar boolean)").execute();
        connection.prepareStatement("CREATE TABLE foo (id bigint, rate text)").execute();

        String query = Q.table(TestBean.class).query(((q, t) ->
                q.insertInto(t[0]).values(a -> "?")));
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setBoolean(1, false);
        ps.setString(2, "test");
        ps.setLong(3, 10);
        ps.execute();

        ResultSet rs = connection.createStatement().executeQuery(Q.table(TestBean.class, FooBean.class).query(((q, t) ->
                q.select(t[0])
                        .from(t[0])
                        .orderBy(t[0] + ".id", SortOrder.ASC)
                        .offset(0)
                        .limit(30))));
        rs.first();
        assertEquals(false, rs.getBoolean("bar"));
        assertEquals("test", rs.getString("foo"));
        assertEquals(10, rs.getLong("id"));
    }
}
