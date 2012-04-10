package hse.kcvc.jminmaxgd;

/**
 * User: Kouprianov Maxim
 * Date: 10.04.12
 * Time: 13:57
 * Contact: me@kc.vc
 */
public class GD {
    private int g;
    private int d;

    /**
     * Set Gamma and Delta parameters of current monomial
     * @param g Gamma to set
     * @param d Delta to set
     */
    void setGD(int g, int d) {
        this.g = g;
        this.d = d;
    }

    /**
     * Constructor 0: (g,d) = (+∞,-∞)
     */
    public GD() {
        setGD(g, d);
    }

    /**
     * Constructor 1: initialize by prototype
     * @param gd Target GD to copy from
     */
    public GD(GD gd) {
        setGD(gd.g, gd.d);
    }

    /**
     * Constructor 2: initialize by two integers
     *
     * @param g Gamma to set
     * @param d Delta to set
     */
    public GD(final int g, final int d) {
        setGD(g, d);
    }
}
