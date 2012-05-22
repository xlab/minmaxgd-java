package hse.kcvc.jminmaxgd;

import java.util.ArrayList;

/**
 * User: Kouprianov Maxim
 * Date: 10.04.12
 * Time: 14:01
 * Contact: me@kc.vc
 */
public class Series {
    Polynomial p;
    Polynomial q;
    Monomial r;
    boolean canonical;

    public boolean isCanonical() {
        return canonical;
    }

    public Polynomial getP() {
        return p;
    }

    public Polynomial getQ() {
        return q;
    }

    public Monomial getR() {
        return r;
    }

    public Series() {
        //s = epsilon + epsilon (0,0)*
        this.r = new Monomial(0, 0);
        this.canonical = false;
    }

    public Series(Polynomial p, Polynomial q, Monomial r) {
        this(p, q, r, false);
    }

    public Series(Polynomial p, Polynomial q, Monomial r, boolean canonical) {
        if (r.getDelta() < 0 || r.getGamma() < 0) {
            throw (new ArithmeticException("r must have positive shifts"));
        }

        this.p = p;
        this.q = q;
        this.r = r;
        this.canonical = canonical;
    }

    public Series(Polynomial p) {
        p.sortSimplify();

        this.p = p.getRange(0, p.getCount() - 2);
        this.q = new Polynomial(p.getElement(p.getCount() - 1));
        this.r = new Monomial(0, 0);
        this.canonical = true;
    }

    public Series(Series s2) {
        this.p = new Polynomial(s2.p);
        this.q = new Polynomial(s2.q);
        this.r = new Monomial(s2.r);
        this.canonical = s2.canonical;
    }

    public Series(Monomial gd) {
        this.p = new Polynomial();
        this.q = new Polynomial(gd);
        this.r = new Monomial(0, 0);
        this.canonical = true;
    }

    public void canonize() {
        Monomial epsilon = new Monomial();
        Monomial Top = new Monomial(Constants._INFINITY, Constants.INFINITY);
        if (this.canonical) return;

        p.sortSimplify();
        q.sortSimplify();

        if (p.getElement(0) == Top || q.getElement(0) == Top) {
            this.p = new Polynomial(epsilon);
            this.q = new Polynomial(Top);
            this.r = new Monomial(0, 0);
            this.canonical = true;
            return;
        }

        if (q.getElement(0) == epsilon) {
            this.q = new Polynomial(p.getElement(p.getCount() - 1));
            this.p.pop();
            this.r = new Monomial(0, 0);
            this.canonical = true;
            return;
        }

        if (q.getElement(q.getCount() - 1).getDelta() == Constants.INFINITY ||
                p.getElement(p.getCount() - 1).getDelta() == Constants.INFINITY) {
            this.p = p.oplus(q);
            this.q = new Polynomial(p.getElement(p.getCount() - 1));
            p.pop();
            this.r = new Monomial(0, Constants.INFINITY);
            this.canonical = true;
            return;
        }

        if (r.getGamma() == Constants.INFINITY || r.getDelta() == 0) {
            this.p = p.oplus(q);
            this.q = new Polynomial(p.getElement(p.getCount() - 1));
            p.pop();
            this.r = new Monomial(0, 0);
            this.canonical = true;
            return;
        }

        if (r.getGamma() > 0 && r.getDelta() == Constants.INFINITY) {

            this.p = p.oplus(q);
            this.q = new Polynomial(r.otimes(q.getElement(0)));
            if (q.getElement(0).getGamma() <= p.getElement(p.getCount() - 1).getGamma()) {
                this.p = p.oplus(q);
                p.pop();
            }
            this.r = new Monomial(0, Constants.INFINITY);
            this.canonical = true;
            return;
        }

        if (r.getDelta() > 0 && r.getGamma() == 0) {
            this.r = new Monomial(0, Constants.INFINITY);
            this.q = new Polynomial(r.otimes(q.getElement(0)));
            this.p = p.oplus(q);
            p.pop();
            this.canonical = true;
            return;
        }

        int i, k;
        int j = q.getCount() - 1;

        while (j > 0) {
            i = 0;
            while (i < j) {
                k = (q.getElement(j).getGamma() - q.getElement(i).getGamma()) / r.getGamma();
                if ((k >= 1) && ((q.getElement(i).getDelta() + k * r.getDelta()) >= q.getElement(j).getDelta())) {
                    q.popSome(j);
                    j--;
                    i = 0;
                } else i++;
            }
            j--;
        }

        j = q.getCount() - 1;
        int nb_g = q.getElement(j).getGamma() - q.getElement(0).getGamma();


        if ((nb_g >= r.getGamma()) || ((q.getElement(j).getDelta() - q.getElement(0).getDelta()) >= r.getDelta())) {
            ArrayList<Monomial> periodique = new ArrayList<Monomial>(q.getCount());
            periodique.set(0, q.getElement(j));

            int nb_max = 1 + (q.getElement(j).getGamma() - q.getElement(0).getGamma()) / r.getGamma();
            ArrayList<Monomial> transitoire = new ArrayList<Monomial>(nb_max * q.getCount());

            k = 0;
            for (i = 0; i < j; ++i) {
                transitoire.set(k, q.getElement(i));
                int nbcoups = (q.getElement(j).getGamma() - q.getElement(i).getGamma() - 1) / r.getGamma();

                for (int n = 1; n <= nbcoups; n++) {
                    transitoire.set(k + n, r.otimes(transitoire.get(k + n - 1)));
                }
                k = k + nbcoups;

                periodique.set(i + 1, r.otimes(transitoire.get(k)));
                k++;
            }

            for (i = 0; i < k; ++i) {
                p.addElement(transitoire.get(i));
            }

            p.sortSimplify();
            this.q = new Polynomial((ArrayList<Monomial>) (periodique.subList(0, q.getCount() - 1)));
        }

        int index;
        if (r.getGamma() <= r.getDelta()) {
            index = r.getGamma();
        } else {
            index = r.getDelta();
        }

        while (index >= 2 && q.getCount() > 1) {
            if ((r.getGamma() % index) == 0 && (r.getDelta() % index) == 0) {
                int nu = r.getGamma() / index;
                int tau = r.getDelta() / index;
                Polynomial assembled = new Polynomial(q.getElement(0));
                i = 1;

                while (i < q.getCount() - 1 &&
                        (q.getElement(i).getGamma() - q.getElement(0).getGamma()) < nu &&
                        (q.getElement(i).getDelta() - q.getElement(0).getDelta()) < tau) {
                    assembled.addElement(q.getElement(i));
                    i++;
                }

                Polynomial extended = assembled;
                for (i = 1; i < index; i++) {
                    Monomial nutau = new Monomial(nu * i, tau * i);
                    extended = extended.oplus(assembled.otimes(nutau));
                }

                boolean equal = false;
                if (extended.getCount() == q.getCount()) {
                    i = assembled.getCount();
                    equal = true;
                    while (equal && i < q.getCount()) {
                        if (!extended.getElement(i).equals(q.getElement(i))) {
                            equal = false;
                        }
                        i++;
                    }
                }

                if (equal) {
                    this.q = assembled;
                    this.r = new Monomial(nu, tau);
                    if (r.getGamma() <= r.getDelta()) {
                        index = r.getGamma();
                    } else {
                        index = r.getDelta();
                    }
                } else {
                    index--;
                }
            } else {
                index--;
            }
        }

        i = p.getCount() - 1;

        boolean dominant;
        do {
            dominant = false;
            j = 0;
            do {
                if (p.getElement(i).compareTo(q.getElement(j)) <= 0 && !p.getElement(i).equals(epsilon)) {
                    p.pop();
                    i = p.getCount() - 1;
                    dominant = true;
                } else j++;
            } while (j < q.getCount() && !dominant);

        } while (dominant);

        i = p.getCount() - 1;
        if (!p.getElement(i).equals(epsilon)) {
            Polynomial temp = new Polynomial(epsilon);
            while (((p.getElement(i).getGamma() >= q.getElement(0).getGamma()) ||
                    (p.getElement(i).getDelta() >= q.getElement(0).getDelta())) && !q.equals(new Polynomial(epsilon))) {
                for (j = 0; j < q.getCount(); j++) {
                    temp.addElement(q.getElement(j));
                }
                q = q.otimes(r);
            }
            p = p.oplus(temp);
        }

        while (q.getElement(q.getCount() - 1).equals(r.otimes(p.getElement(p.getCount() - 1))) && !r.otimes(p.getElement(p.getCount() - 1)).equals(epsilon)) {
            for (i = (q.getCount() - 1); i > 0; i--)
                q.setElement(i, q.getElement(i - 1));
            q.setElement(0, p.getElement(p.getCount() - 1));
            p.pop();
        }

        this.canonical = true;
    }

    public Series oplus(Series s2) {
        int j;
        Series result;
        double slope1, slope2;

        int i;
        int k1, k2, k, t2;
        Polynomial p, q;
        Monomial r;
        Series ads1 = this;
        Series ads2 = s2;

        Monomial epsilon = new Monomial(0, 0);
        Monomial Top = new Monomial(Constants._INFINITY, Constants.INFINITY);
        result = new Series();

        if (!this.canonical) {
            this.canonize();
        }

        if (!s2.canonical) {
            s2.canonize();
        }

        if (this.q.getElement(0).getGamma() == Constants._INFINITY || s2.q.getElement(0).getGamma() == Constants._INFINITY) {
            result.p = new Polynomial(epsilon);
            result.q = new Polynomial(Top);
            result.r = new Monomial(0, 0);
            result.canonical = true;
            return (result);
        }

        if (this.q.getElement(0).getGamma() == Constants.INFINITY) return (s2);
        if (s2.q.getElement(0).getGamma() == Constants.INFINITY) return (this);


        if (this.r.getDelta() == 0 && s2.r.getDelta() == 0) {
            result.p = this.q.oplus(this.p).oplus(s2.p.oplus(s2.q));
            result = new Series(result.p);
            result.canonical = true;
            return (result);
        }

        if (this.r.getDelta() == 0 && s2.r.getDelta() != 0) {
            ads2 = this;
            ads1 = s2;
        }

        if (ads1.r.getDelta() != 0 && ads2.r.getDelta() == 0) {
            if (ads1.r.getGamma() == 0) {
                result.p = new Polynomial(new Monomial(ads1.q.getElement(0).getGamma(), Constants.INFINITY));
                result.p = ads1.p.oplus(ads2.p).oplus(ads2.q.oplus(result.p));
                result = new Series(result.p);
                result.canonical = true;

            } else {
                result.p = ads1.p.oplus(ads2.p.oplus(ads2.q));
                result.q = ads1.q;
                result.r = ads1.r;
                result.canonize();
            }
            return (result);
        }

        if (this.r.getDelta() != 0 && s2.r.getDelta() != 0 && this.r.getGamma() == 0 && s2.r.getGamma() == 0) {
            result.p = new Polynomial(new Monomial(this.q.getElement(0).getGamma(), Constants.INFINITY));
            result.p = result.p.oplus(this.p.oplus(s2.p));
            result.p = result.p.oplus(new Monomial(s2.q.getElement(0).getGamma(), Constants.INFINITY));
            result = new Series(result.p);
            result.canonical = true;

            return (result);
        }


        if (this.r.getDelta() != 0 && s2.r.getDelta() != 0 && this.r.getGamma() != 0 && s2.r.getGamma() == 0) {
            result.p = this.q;
            i = 1;
            while (result.p.getElement(result.p.getCount() - 1).getGamma() <= s2.q.getElement(0).getGamma()) {
                result.p = result.p.oplus(this.q.otimes(new Monomial(this.r.getGamma() * i, this.r.getDelta() * i)));
                i++;
            }

            result.p = s2.p.oplus(this.p.oplus(result.p)).oplus(new Monomial(s2.q.getElement(0).getGamma(), Constants.INFINITY));
            result = new Series(result.p);
            result.r = new Monomial(0, Constants.INFINITY);
            result.canonical = true;

            return (result);
        }

        if (this.r.getDelta() != 0 && s2.r.getDelta() != 0 && this.r.getGamma() == 0 && s2.r.getGamma() != 0) {
            result.p = s2.q;
            i = 1;
            while (result.p.getElement(result.p.getCount() - 1).getGamma() <= this.q.getElement(0).getGamma()) {
                result.p = result.p.oplus(s2.q.otimes(new Monomial(s2.r.getGamma() * i, s2.r.getDelta() * i)));
                i++;
            }

            result.p = this.p.oplus(s2.p.oplus(result.p)).oplus(new Monomial(this.q.getElement(0).getGamma(), Constants.INFINITY));
            result = new Series(result.p);
            result.r = new Monomial(0, Constants.INFINITY);
            result.canonical = true;

            return (result);
        }

        slope1 = ((double) this.r.getGamma() / this.r.getDelta());
        slope2 = ((double) s2.r.getGamma() / s2.r.getDelta());
        if (slope1 == slope2) {
            p = this.p.oplus(s2.p);


            r = new Monomial(Tools.lcm(this.r.getGamma(), s2.r.getGamma()),
                    Tools.lcm(this.r.getDelta(), s2.r.getDelta()));

            k1 = r.getGamma() / this.r.getGamma();
            k2 = r.getGamma() / s2.r.getGamma();

            for (i = 1; i <= k1 - 1; i++)
                for (j = 0; j < this.q.getCount(); j++) {
                    Monomial monome = new Monomial(i * this.r.getGamma(), i * this.r.getDelta());
                    monome = monome.otimes(this.q.getElement(j));
                    this.q.addElement(monome);
                }

            int count = s2.q.getCount();
            for (i = 0; i <= k2 - 1; i++)
                for (j = 0; j < count; j++) {
                    Monomial monome = new Monomial(s2.q.getElement(j).getGamma() + i * s2.r.getGamma(), s2.q.getElement(j).getDelta() + i * s2.r.getDelta());
                    this.q.addElement(monome);
                }

            result.p = p;
            result.q = this.q;
            result.r = r;
            result.canonical = false;
            result.canonize();
        } else {
            if (slope1 > slope2) {
                ads1 = s2;
                ads2 = this;
            }

            t2 = ads2.q.getElement(ads2.q.getCount() - 1).getDelta();

            k1 = ads1.r.getGamma() * (t2 - ads1.q.getElement(0).getDelta() + ads1.r.getDelta())
                    + ads1.r.getDelta() * (ads1.q.getElement(0).getGamma() - ads2.q.getElement(0).getGamma());

            k2 = ads1.r.getDelta() * ads2.r.getGamma() - ads1.r.getGamma() * ads2.r.getDelta();
            k = Math.max(Math.max((int) Math.ceil((double) k1 / k2), 0), (int) Math.ceil((double) (ads1.q.getElement(0).getGamma() - ads2.q.getElement(0).getGamma()) / ads2.
                    r.getGamma()));

            p = ads1.p.oplus(ads2.p);

            for (i = 0; i < k; i++) {
                for (j = 0; j < ads2.q.getCount(); j++) {
                    p.addElement(new Monomial(ads2.q.getElement(j).getGamma() + i * ads2.r.getGamma(), ads2.q.getElement(j).getDelta() + i * ads2.r.getDelta()));
                }
            }

            q = ads1.q;
            r = ads1.r;

            result.p = p;
            result.q = q;
            result.r = r;
            result.canonical = false;
            result.canonize();
        }

        return (result);
    }

    public Series otimes(Monomial gd2) {
        Series s2 = new Series();
        s2.p = new Polynomial(new Monomial());
        s2.q = new Polynomial(new Monomial(gd2.getGamma(), gd2.getDelta()));
        s2.r = new Monomial(0, 0);
        s2.canonical = true;
        return (this.otimes(s2));
    }

    public Series otimes(Polynomial p2) {
        Series s2 = new Series();
        s2.p = p2;
        s2.q = new Polynomial(s2.p.getElement(s2.p.getCount() - 1));
        s2.p.pop();
        s2.r = new Monomial(0, 0);
        s2.canonical = true;
        return (this.otimes(s2));
    }

    public Series oplus(Monomial gd2) {
        Series s2 = new Series();
        s2.p = new Polynomial(new Monomial());
        s2.q = new Polynomial(new Monomial(gd2.getGamma(), gd2.getDelta()));
        s2.r = new Monomial(0, 0);
        s2.canonical = true;
        return (this.oplus(s2));
    }

    public Series oplus(Polynomial p2) {
        Series s2 = new Series();
        s2.p = p2;
        s2.q = new Polynomial(s2.p.getElement(s2.p.getCount() - 1));
        s2.p.pop();
        s2.r = new Monomial(0, 0);
        s2.canonical = true;
        return (this.oplus(s2));
    }

    public Series otimes(Series s2) {
        Series ads1 = this;
        Series ads2 = s2;

        Polynomial p1, q1;
        int i, j;
        int a;
        int k1, k2, teta;
        double slope1, slope2, test1, test2;
        double tau;
        Series temp1, result;

        temp1 = new Series();
        result = new Series();

        Monomial epsilon = new Monomial();
        Monomial Top = new Monomial(Constants._INFINITY, Constants.INFINITY);


        if (!this.canonical) this.canonize();
        if (!s2.canonical) s2.canonize();

        if (this.q.getElement(0).getGamma() == Constants.INFINITY || s2.q.getElement(0).getGamma() == Constants.INFINITY) {
            result.p = new Polynomial(epsilon);
            result.q = new Polynomial(epsilon);
            result.r = new Monomial(0, 0);
            result.canonical = true;
            return (result);
        }

        if (this.q.getElement(0).getGamma() == Constants._INFINITY || s2.q.getElement(0).getGamma() == Constants._INFINITY) {
            result.p = new Polynomial(epsilon);
            result.q = new Polynomial(Top);
            result.r = new Monomial(0, 0);
            result.canonical = true;
            return (result);
        }

        result.canonical = false;
        result.p = this.p.otimes(s2.p);


        result.q = this.p.otimes(s2.q);
        result.r = s2.r;
        result.canonize();

        //System.out.println(result.r);

        temp1.q = s2.p.otimes(this.q);
        temp1.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
        temp1.r = this.r;
        temp1.canonize();

        result = result.oplus(temp1);

        temp1.canonical = false;
        temp1.q = this.q.otimes(s2.q);


        if (this.r.getDelta() == 0 && s2.r.getDelta() == 0) {
            result = result.oplus(temp1);
            return (result);
        }

        if ((this.r.getGamma() == 0 && this.r.getDelta() == Constants.INFINITY) ||
                (s2.r.getGamma() == 0 && s2.r.getDelta() == Constants.INFINITY)) {

            temp1.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
            temp1.r = new Monomial(0, Constants.INFINITY);
            result = result.oplus(temp1);
            return (result);
        }

        if (this.r.getDelta() == 0 && this.r.getGamma() == 0 &&
                s2.r.getGamma() != 0 && s2.r.getDelta() != 0 &&
                s2.r.getDelta() != Constants.INFINITY) {
            ads1 = s2;
            ads2 = this;
        }

        if (ads2.r.getDelta() == 0 && ads2.r.getGamma() == 0 && ads1.r.getGamma() != 0 && ads1.
                r.getDelta() != 0 && ads1.r.getDelta() != Constants.INFINITY) {

            temp1.r = ads1.r;
            temp1.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
            temp1.canonize();
            result = result.oplus(temp1);

            return (result);
        }

        slope1 = (double) this.r.getGamma() / this.r.getDelta();
        slope2 = (double) s2.r.getGamma() / s2.r.getDelta();

        if (slope1 == slope2) {
            k1 = Tools.gcd(this.r.getGamma(), s2.r.getGamma());
            k2 = Tools.gcd(this.r.getDelta(), s2.r.getDelta());

            temp1.r = new Monomial(k1, k2);
            tau = (double) k1 / k2;

            k1 = (int) ((double) (this.r.getGamma() - k1) * (s2.r.getGamma() - k1)) / k1;
            k2 = (int) ((double) (this.r.getDelta() - k2) * (s2.r.getDelta() - k2)) / k2;
            p1 = new Polynomial(new Monomial(0, 0));

            i = 0;
            j = 1;
            teta = s2.r.getDelta();
            while (teta < k2) {
                while (teta < k2) {
                    p1.addElement(new Monomial((int) (tau * teta), teta));
                    j++;
                    teta = i * this.r.getDelta() + j * s2.r.getDelta();
                }
                i++;
                j = 0;
                teta = i * this.r.getDelta() + j * s2.r.getDelta();
            }

            p1.sortSimplify();
            temp1.p = p1.otimes(temp1.q);
            temp1.q = temp1.q.otimes(new Monomial(k1, k2));
            temp1.canonize();

        } else {
            if (slope1 > slope2) {
                ads1 = s2;
                ads2 = this;

            }

            k1 = ads1.r.getGamma() * ads1.r.getDelta();
            k2 = ads1.r.getDelta() * ads2.r.getGamma() - ads1.r.getGamma() * ads2.r.getDelta();
            k1 = Math.max((int) Math.ceil((double) k1 / k2), 0);


            a = (int) Math.floor(((double) k1 * ads2.r.getGamma()) / ads1.r.getGamma());
            test1 = (ads1.r.getDelta() * a);
            test2 = (ads2.r.getDelta() * k1);
            while (test1 >= test2 && k1 > 0) {
                k1--;
                a = (int) Math.floor(((double) k1 * ads2.r.getGamma()) / ads1.r.getGamma());
                test1 = (ads1.r.getDelta() * a);
                test2 = (ads2.r.getDelta() * k1);
            }

            k1++;


            q1 = new Polynomial(new Monomial(0, 0));
            for (j = 1; j < k1; j++) {
                q1.addElement(new Monomial(ads2.r.getGamma() * j, ads2.r.getDelta() * j));
            }
            temp1.q = temp1.q.otimes(q1);
            temp1.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
            temp1.r = ads1.r;
            temp1.canonize();
        }

        result = result.oplus(temp1);
        return (result);
    }

    public Series star() {
        Series operation, result, temp;
        Monomial monome;
        result = new Series();

        if (!this.canonical) this.canonize();

        operation = new Series(this);
        monome = new Monomial(0, 0);

        result.q = operation.q.oplus(operation.r);
        result = result.q.star();

        result = result.otimes(operation.q);
        result = result.oplus(monome);

        temp = operation.p.star();

        result = result.otimes(temp);
        return (result);
    }

    @Override
    public String toString() {
        Monomial epsilon = new Monomial(0, 0);
        String flot = "";
        /* if (!(this.p.equals(new Polynomial(epsilon)))) flot += this.p + "+";
        if (!(this.q.equals(new Polynomial(epsilon)))) flot += "(" + this.q + ")[" + this.r + "]*";
        else flot += "eps";   */
        flot += this.p + "+";
        flot += "(" + this.q + ")[" + this.r + "]*";
        return flot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Series)) return false;

        Series series = (Series) o;

        if (canonical != series.canonical) return false;
        if (p != null ? !p.equals(series.p) : series.p != null) return false;
        if (q != null ? !q.equals(series.q) : series.q != null) return false;
        if (r != null ? !r.equals(series.r) : series.r != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = p != null ? p.hashCode() : 0;
        result = 31 * result + (q != null ? q.hashCode() : 0);
        result = 31 * result + (r != null ? r.hashCode() : 0);
        result = 31 * result + (canonical ? 1 : 0);
        return result;
    }
}
