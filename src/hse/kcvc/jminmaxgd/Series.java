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

    public Series(Monomial gd) {
        this.p = new Polynomial();
        this.q = new Polynomial(gd);
        this.r = new Monomial(0, 0);
        this.canonical = true;
    }

    public void canonize() {


        //long nb_g, index, nu, tau;

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
                if ((k >= 1) && ((q.getElement(i).getDelta() + (int) k * r.getDelta()) >= q.getElement(j).getDelta())) {
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
                if (extended.getCount() == q.getCount()) // si ils ont la meme taille
                {
                    i = assembled.getCount();    // les n premiers points sont forcement egaux pas necessaire de les tester
                    equal = true;
                    while (equal && i < q.getCount()) {
                        if (extended.getElement(i) != q.getElement(i)) {
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
                if (p.getElement(i).compareTo(q.getElement(j)) == -1 && p.getElement(i) != epsilon) {
                    p.pop();
                    i = p.getCount() - 1;
                    dominant = true;
                } else j++;
            } while (j < q.getCount() && !dominant);

        } while (dominant);

        i = p.getCount() - 1;
        if (p.getElement(i) != epsilon) {
            Polynomial temp = new Polynomial(epsilon);
            while ((p.getElement(i).getGamma() >= q.getElement(0).getGamma()) ||
                    (p.getElement(i).getDelta() >= q.getElement(0).getDelta())) {
                for (j = 0; j < q.getCount(); j++) {
                    temp.addElement(q.getElement(j));
                }
                q = q.otimes(r);
            }
            p = p.oplus(temp);
        }

        while (q.getElement(q.getCount() - 1) == r.otimes(p.getElement(p.getCount() - 1))) {
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
        Series tampon;
        Monomial gd;
        double slope1, slope2;

        int i;
        long k1, k2, k, t2;
        Polynomial p, q;
        Monomial r, epsilon;
        Series ads1 = this;
        Series ads2 = s2;

        Monomial Top = new Monomial(Constants._INFINITY, Constants.INFINITY);


        if (!this.canonical) this.canonize();
        if (!s2.canonical) s2.canonize();

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

            q = this.q;

            for (i = 1; i <= k1 - 1; i++)
                for (j = 0; j < this.q.getCount(); j++) {
                    Monomial monome = new Monomial(i * this.r.getGamma(), i * this.r.getDelta());
                    monome = monome.otimes(this.q.getElement(j));
                    q.addElement(monome);
                }


            for (i = 0; i <= k2 - 1; i++)
                for (j = 0; j < s2.q.getCount(); j++) {
                    Monomial monome = new Monomial(s2.q.getElement(j).getGamma() + i * s2.r.getGamma(), s2.q.getElement(j).getDelta() + i * s2.r.getDelta());
                    q.addElement(monome);
                }

            result.p = p;
            result.q = q;
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
                    p.addElement(new Monomial(ads2.q.getElement(j).getGamma() + i * ads2.r.getGamma(), ads2.q.getElement(j).getDelta() + i * ads2.r.getDelta());
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

    public Series otimes(Series s2) {
        serie * ads1 =&s1;
        serie * ads2 =&s2;

        gd monome;
        poly p1, q1;
        int i, j;
        long int a;
        long k1, k2, teta;
        double pente1, pente2, test1, test2;
        double tau;
        serie temp1, result, tampon;
        gd epsilon;

        gd Top (_infinity, infinity);


        if (s1.canonise == 0) s1.canon();
        if (s2.canonise == 0) s2.canon();

        //** si l'une des s�ies vaut epsilon

        if (s1.q.getpol(0).getg() == infinity || s2.q.getpol(0).getg() == infinity) {
            result.p = epsilon;
            result.q = epsilon;
            result.r.init(0, 0);
            result.canonise = 1;
            return (result);
        }


        //** si l'une des s�ies vaut Top

        if (s1.q.getpol(0).getg() == _infinity || s2.q.getpol(0).getg() == _infinity) {
            result.p = epsilon;
            result.q = Top;
            result.r.init(0, 0);
            result.canonise = 1;
            return (result);
        }


        //(p1 + q1r1*)(p2 + q2r2*)=p1p2 +p1 q2 r2* + p2 q1 r1* + q1 q2 r1* r2 *
        result.canonise = 0;
        result.p = otimes(s1.p, s2.p); // p1 p2

        result.q = otimes(s1.p, s2.q); // p1 q2
        result.r = s2.r;                // r2
        result.canon();

        temp1.q = otimes(s2.p, s1.q);    // p2 q1
        temp1.p.init(infinity, _infinity);
        temp1.r = s1.r;                // r1
        temp1.canon();

        result = oplus(result, temp1); // p1p2 +p1 q2 r2* + p2 q1 r1*

        //*****		Traitement de q1 q2 r1* r2 *				*****//
        temp1.canonise = 0;
        temp1.q = otimes(s1.q, s2.q);        // q1 q2

        /**** Les cas d����	 *******/
        if (s1.r.getd() == 0 && s2.r.getd() == 0) {
            result = oplus(result, temp1);
            return (result);
        }

        if ((s1.r.getg() == 0 && s1.r.getd() == infinity) || (s2.r.getg() == 0 && s2.r.getd() == infinity)) {   //monome.init(0,infinity);
            //temp1.q.add(monome);
            //	temp1.q.simpli();
            temp1.p.init(infinity, _infinity);
            temp1.r.init(0, infinity);
            result = oplus(result, temp1);
            return (result);
        }

        if (s1.r.getd() == 0 && s1.r.getg() == 0 && s2.r.getg() != 0 && s2.r.getd() != 0 && s2.r.getd() != infinity) {// inversion pour traitement symetrique apr�
            ads1 =&s2;
            ads2 =&s1;
        }


        if (( * ads2).r.getd() == 0 && ( * ads2).r.getg() == 0 && ( * ads1).r.getg() != 0 && ( * ads1).
        r.getd() != 0 && ( * ads1).r.getd() != infinity)
        {
            temp1.r = ( * ads1).r;
            temp1.p.init(infinity, _infinity);
            temp1.canon();
            result = oplus(result, temp1);

            return (result);
        }

        /**** le cas non d���� ****/
        pente1 = (double) s1.r.getg() / s1.r.getd();
        pente2 = (double) s2.r.getg() / s2.r.getd();

        if (pente1 == pente2) {
            k1 = gcd(s1.r.getg(), s2.r.getg());
            k2 = gcd(s1.r.getd(), s2.r.getd());

            temp1.r.init(k1, k2); // la pente de r1* . r2*
            tau = (double) k1 / k2;

            k1 = (long) ((double) (s1.r.getg() - k1) * (s2.r.getg() - k1)) / k1;

            k2 = (long) ((double) (s1.r.getd() - k2) * (s2.r.getd() - k2)) / k2;

            p1.init(0, 0);
            i = 0;
            j = 1;
            teta = s2.r.getd();
            while (teta < k2) {
                while (teta < k2) {
                    monome.init((long) (tau * teta), teta);
                    p1.add(monome);
                    j++;
                    teta = i * s1.r.getd() + j * s2.r.getd();
                }
                i++;
                j = 0;
                teta = i * s1.r.getd() + j * s2.r.getd();
            }

            p1.simpli();    // le transitoire de r1* . r*
            temp1.p = otimes(p1, temp1.q); // q1*q2 * transitoire de r1*.r2*
            monome.init(k1, k2);
            temp1.q = otimes(temp1.q, monome); // q1*q2* motif de r1*.r2
            temp1.canon();

        } else {
            if (pente1 > pente2) {
                ads1 =&s2;
                ads2 =&s1;

            }

            k1 = ( * ads1).r.getg() * ( * ads1).r.getd();
            k2 = ( * ads1).r.getd() * ( * ads2).r.getg() - ( * ads1).r.getg() * ( * ads2).r.getd();
            k1 = MAX((long) ceil((double) k1 / k2), 0);


            a = (long) floor(((double) k1 * ( * ads2).r.getg()) / ( * ads1).r.getg());
            test1 = (( * ads1).r.getd() * a);
            test2 = (( * ads2).r.getd() * k1);
            while (test1 >= test2 && k1 > 0) {
                k1--;
                a = (long) floor(((double) k1 * ( * ads2).r.getg()) / ( * ads1).r.getg());
                test1 = (( * ads1).r.getd() * a);
                test2 = (( * ads2).r.getd() * k1);
            }

            k1++;


            q1.init(0, 0);
            for (j = 1; j < k1; j++) {
                monome.init(( * ads2).r.getg() * j, ( * ads2).r.getd() * j);
                q1.add(monome);
            }
            temp1.q = otimes(temp1.q, q1);
            temp1.p.init(infinity, _infinity);
            temp1.r = ( * ads1).r;
            temp1.canon();
        }


        result = oplus(result, temp1);

        return (result);

    }

    public Series star() {
        serie result, temp;
        gd monome;

        if (s1.canonise == 0) s1.canon();

        monome.init(0, 0);

        result.q = oplus(s1.q, s1.r);        //(q+r)
        result = star(result.q);    // (q+r)*

        result = oplus(monome, temp = otimes(s1.q, result)); // e + q .(q+r)*

        temp = star(s1.p); // p*

        result = otimes(temp, result);
        return (result);
    }
}
