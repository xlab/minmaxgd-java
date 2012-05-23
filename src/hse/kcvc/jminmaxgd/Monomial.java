package hse.kcvc.jminmaxgd;

/**
 * User: Kouprianov Maxim
 * Date: 10.04.12
 * Time: 13:57
 * Contact: me@kc.vc
 */
public class Monomial implements Comparable<Monomial> {
    final private int g;
    final private int d;


    public int getGamma() {
        return g;
    }

    public int getDelta() {
        return d;
    }

    /**
     * Constructor 0: (g,d) = (+∞,-∞)
     */
    public Monomial() {
        this.g = Constants.INFINITY;
        this.d = Constants._INFINITY;
    }

    /**
     * Constructor 1: initialize by prototype
     *
     * @param gd Target Monomial to copy from
     */
    public Monomial(final Monomial gd) {
        this.g = gd.g;
        this.d = gd.d;
    }

    /**
     * Constructor 2: initialize by two integers
     *
     * @param g Gamma to set
     * @param d Delta to set
     */
    public Monomial(final int g, final int d) {
        this.g = g;
        this.d = d;
    }

    public int compareTo(Monomial gd2) {
        if (this.g == gd2.g && this.d == gd2.d)
            return 0;

        if (g < gd2.g) return 1;
        if (g == gd2.g)
            if (d > gd2.d) return 1;

        return -1;

    }

    public Monomial inf(final Monomial gd2) {
        final int g, d;
        if (gd2.g > this.g)
            g = gd2.g;
        else
            g = Constants.INFINITY;

        if (gd2.d < this.d)
            d = gd2.d;
        else
            d = Constants._INFINITY;


        return new Monomial(g, d);
    }

    public Monomial otimes(final Monomial gd2) {
        final int g, d;
        if (this.g == Constants._INFINITY || gd2.g == Constants._INFINITY)
            g = Constants._INFINITY;
        else if (this.g == Constants.INFINITY || gd2.g == Constants.INFINITY)
            g = Constants.INFINITY;
        else
            g = this.g + gd2.g;

        if (this.d == Constants._INFINITY || gd2.d == Constants._INFINITY)
            d = Constants._INFINITY;
        else if (this.d == Constants.INFINITY || gd2.d == Constants.INFINITY)
            d = Constants.INFINITY;
        else d = this.d + gd2.d;

        return new Monomial(g, d);
    }

    public Series star() {
        Series result = new Series();

        result.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
        result.q = new Polynomial(new Monomial(0, 0));
        result.canonical = true;

        if (this.getGamma() == Constants.INFINITY || this.getDelta() == 0) {
            result.r = new Monomial(0, 0);
            return (result);
        }
        if (this.getGamma() == 0 && this.getDelta() > 0) {
            result.r = new Monomial(0, Constants.INFINITY);
            return (result);
        }

        if (this.getDelta() == Constants.INFINITY) {
            result.p = new Polynomial(new Monomial(0, 0));
            result.q = new Polynomial(new Monomial(this.getGamma(), Constants.INFINITY));
            result.r = new Monomial(this.getGamma(), Constants.INFINITY);
            return (result);
        }

        result.r = new Monomial(this.getGamma(), this.getDelta());
        return (result);
    }

    public Monomial frac(final Monomial gd2) {
        final int g, d;
        switch (this.g) {
            case Constants._INFINITY:
                g = Constants._INFINITY;
                break;
            case Constants.INFINITY:
                if (gd2.g == Constants.INFINITY) g = Constants._INFINITY;
                else g = Constants.INFINITY;
                break;
            default:
                switch (gd2.g) {
                    case Constants.INFINITY:
                        g = Constants._INFINITY;
                        break;
                    case Constants._INFINITY:
                        g = Constants.INFINITY;
                        break;
                    default:
                        g = this.g - gd2.g;
                }
        }

        switch (this.d) {
            case Constants.INFINITY:
                d = Constants.INFINITY;
                break;
            case Constants._INFINITY:
                if (gd2.d == Constants._INFINITY) d = Constants.INFINITY;
                else d = Constants._INFINITY;
                break;
            default:
                switch (gd2.d) {
                    case Constants._INFINITY:
                        d = Constants.INFINITY;
                        break;
                    case Constants.INFINITY:
                        d = Constants._INFINITY;
                        break;
                    default:
                        d = this.d - gd2.d;
                }
        }

        return new Monomial(g, d);
    }

    @Override
    public String toString() {
        return " g^" + ((this.g == Constants.INFINITY) ? ("inf") : ((this.g == Constants._INFINITY) ? ("-inf") : ("" + this.g)))
                + " d^" + ((this.d == Constants.INFINITY) ? ("inf") : ((this.d == Constants._INFINITY) ? ("-inf") : ("" + this.d)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Monomial)) return false;
        Monomial monomial = (Monomial) o;

        return d == monomial.d && g == monomial.g;
    }

    @Override
    public int hashCode() {
        int result = g;
        result = 31 * result + d;
        return result;
    }
}
