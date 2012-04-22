package hse.kcvc.jminmaxgd;

/**
 * User: Kouprianov Maxim
 * Date: 10.04.12
 * Time: 13:57
 * Contact: me@kc.vc
 */
public class GD implements Comparable<GD> {
    final private int g;
    final private int d;


    public String getG() {
        return (this.g == Constants.Infinity) ? ("inf") : ((this.g == Constants._Infinity) ? ("-inf") : ("" + this.g));
    }

    public String getD() {
        return (this.d == Constants.Infinity) ? ("inf") : ((this.d == Constants._Infinity) ? ("-inf") : ("" + this.d));
    }

    /**
     * Constructor 0: (g,d) = (+∞,-∞)
     */
    public GD() {
        this.g = Constants.Infinity;
        this.d = Constants._Infinity;
    }

    /**
     * Constructor 1: initialize by prototype
     *
     * @param gd Target GD to copy from
     */
    public GD(GD gd) {
        this.g = gd.g;
        this.d = gd.d;
    }

    /**
     * Constructor 2: initialize by two integers
     *
     * @param g Gamma to set
     * @param d Delta to set
     */
    public GD(final int g, final int d) {
        this.g = g;
        this.d = d;
    }

    public int compareTo(GD gd2) {
        if (this.g == gd2.g && this.d == gd2.d)
            return 0;
        else if (this.g < gd2.g && this.d > gd2.d)
            return 1;
        else
            return -1;

    }

    public GD inf(GD gd2) {
        int g, d;
        if (gd2.g > this.g)
            g = gd2.g;
        else
            g = Constants.Infinity;

        if (gd2.d < this.d)
            d = gd2.d;
        else
            d = Constants._Infinity;


        return new GD(g, d);
    }

    public GD otimes(GD gd2) {
        int g, d;
        if (this.g == Constants._Infinity || gd2.g == Constants._Infinity)
            g = Constants._Infinity;
        else if (this.g == Constants.Infinity || gd2.g == Constants.Infinity)
            g = Constants.Infinity;
        else
            g = this.g + gd2.g;

        if (this.d == Constants._Infinity || gd2.d == Constants._Infinity)
            d = Constants._Infinity;
        else if (this.d == Constants.Infinity || gd2.d == Constants.Infinity)
            d = Constants.Infinity;
        else d = this.d + gd2.d;

        return new GD(g, d);
    }

    public GD frac(GD gd2) {
        int g, d;
        switch (this.g) {
            case Constants._Infinity:
                g = Constants._Infinity;
                break;
            case Constants.Infinity:
                if (gd2.g == Constants.Infinity) g = Constants._Infinity;
                else g = Constants.Infinity;
                break;
            default:
                switch (gd2.g) {
                    case Constants.Infinity:
                        g = Constants._Infinity;
                        break;
                    case Constants._Infinity:
                        g = Constants.Infinity;
                        break;
                    default:
                        g = this.g - gd2.g;
                }
        }

        switch (this.d) {
            case Constants.Infinity:
                d = Constants.Infinity;
                break;
            case Constants._Infinity:
                if (gd2.d == Constants._Infinity) d = Constants.Infinity;
                else d = Constants._Infinity;
                break;
            default:
                switch (gd2.d) {
                    case Constants._Infinity:
                        d = Constants.Infinity;
                        break;
                    case Constants.Infinity:
                        d = Constants._Infinity;
                        break;
                    default:
                        d = this.d - gd2.d;
                }
        }

        return new GD(g, d);
    }

    @Override
    public String toString() {
        return " g^" + this.getG() + " d^" + this.getD();
    }
}
