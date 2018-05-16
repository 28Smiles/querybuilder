import de.smiles.querybuilder.Table;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
@Table("foo")
public class FooBean {

    private long id;

    private String rate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
