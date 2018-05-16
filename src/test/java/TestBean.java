import de.smiles.querybuilder.Column;

/**
 * @author Leon Camus
 * @since 14.04.2018
 */
public class TestBean {

    private long id;

    @Column("bar_foo")
    private String foo;

    private boolean bar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public boolean isBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }
}
