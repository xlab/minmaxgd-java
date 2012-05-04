package hse.kcvc.jminmaxgd;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Kouprianov Maxim
 * Date: 10.04.12
 * Time: 14:01
 * Contact: me@kc.vc
 */
public class Polynomial {
    private ArrayList<Monomial> data;

    public boolean isSimple() {
        return simple;
    }

    private boolean simple;

    public void sort() {
        Collections.sort(data);
        Collections.reverse(data);
    }

    public int getCount() {
        return data.size();
    }

    public Monomial getElement(int n) {
        return data.get(n);
    }

    public Polynomial(final ArrayList<Monomial> list) {
        this();

        if (list.size() > 1) {
            this.data = new ArrayList<Monomial>(list);
            simple = false;
            sortSimplify();
        }
    }

    public Polynomial() {
        this.data = new ArrayList<Monomial>(Constants.POLY_SIZE);
        this.data.add(new Monomial());
        this.simple = true;
    }

    public Polynomial(final Monomial gd) {
        this.data = new ArrayList<Monomial>(Constants.POLY_SIZE);
        this.data.add(gd);
        this.simple = true;
    }

    public void sortSimplify() {
        if (simple) return;
        if (data != null) {
            sort();
            simplify();
        }
    }

    public void simplify() {
        if (data != null) {
            int i = 0;
            for (int j = 1; j < data.size(); ++j) {
                if (data.get(j).getDelta() > data.get(i).getDelta()) {
                    ++i;
                    data.set(i, data.get(j));
                }
            }

            //first item is on top, by sort
            data = new ArrayList<Monomial>(data.subList(0, i + 1));

            if (data.get(0).getGamma() == Constants._INFINITY)   // if poly begins with g = -∞
            {
                if (data.get(0).getDelta() != Constants._INFINITY) {
                    data = new ArrayList<Monomial>(Constants.POLY_SIZE);
                    data.add(new Monomial());
                    return;
                } else // -∞,-∞ <=> +∞,-∞
                {
                    while (data.get(0).getGamma() == Constants._INFINITY) data.remove(0);
                }
            }

            // Check if last element is epsilon && not alone, then pop him
            if (data.get(data.size() - 1).compareTo(new Monomial()) == 0 && data.size() > 1)
                data.remove(data.size() - 1);

            // Check if last element (+∞, x) <=> +∞,-∞
            if (data.get(data.size() - 1).getGamma() == Constants.INFINITY &&
                    data.get(data.size() - 1).getDelta() != Constants._INFINITY) data.remove(data.size() - 1);

            // Check if first element (x, -∞) <=> +∞,-∞
            if (data.get(0).getGamma() != Constants.INFINITY &&
                    data.get(0).getDelta() == Constants._INFINITY) data.remove(0);

            this.simple = true;
        }
    }

    public void addElement(final Monomial gd) {
        if (data.size() > 1) {
            data.add(gd);
        } else {
            if (data.get(0).compareTo(new Monomial()) == 0) data.set(0, gd);
            else {
                data.add(gd);
            }
        }

        this.simple = false;
    }

    public Polynomial oplus(final Polynomial poly2) {
        ArrayList<Monomial> list = new ArrayList<Monomial>(data);
        list.addAll(poly2.data);

        return new Polynomial(list);
    }

    public Polynomial oplus(final Monomial gd) {
        Polynomial result = new Polynomial(data);
        result.addElement(gd);
        result.sortSimplify();

        return result;
    }

    public Polynomial otimes(Polynomial poly2) {
        Polynomial temp;
        Polynomial poly1;
        int halfsize = 0;
        ArrayList<ArrayList<Monomial>> halfbuffer;
        ArrayList<ArrayList<Monomial>> results;
        int[] polysizes;

        if (!this.simple) {
            temp = new Polynomial(this.data);
            temp.simplify();
            poly1 = temp;
        } else {
            poly1 = this;
        }

        if (!this.simple) {
            temp = new Polynomial(poly2.data);
            temp.simplify();
            poly2 = temp;
        }

        //convert to small * big instead of big * small
        if (poly1.data.size() > poly2.data.size()) {
            temp = poly1;
            poly1 = poly2;
            poly2 = temp;
        }

        int polysize1 = poly1.data.size();
        int polysize2 = poly2.data.size();

        halfsize = (polysize1 + 1) / 2;
        halfbuffer = new ArrayList<ArrayList<Monomial>>(halfsize);
        results = new ArrayList<ArrayList<Monomial>>(polysize1);

        for (int j = 0; j < polysize1; ++j) {
            results.add(new ArrayList<Monomial>(polysize2));

        }

        for (int j = 0; j < halfsize; ++j) {
            halfbuffer.add(new ArrayList<Monomial>(polysize2));
        }

        for (int j = 0; j < polysize1; j++)
            for (int k = 0; k < polysize2; k++)
                results.get(j).add(poly1.getElement(j).otimes(poly2.getElement(k)));

        int nbpoly = polysize1;

        while (nbpoly > 1) {
            int i = 0;
            int j = 0;
            int k;

            while (i < nbpoly) {
                k = i + 1;
                if (k < nbpoly) {
                    halfbuffer.set(j, new ArrayList<Monomial>(results.get(i)));
                    halfbuffer.get(j).addAll(results.get(k));
                } else {
                    halfbuffer.set(j, results.get(i));
                }
                i += 2;
                ++j;
            }

            nbpoly = j;
            halfsize = (nbpoly + 1) / 2;

            for (j = 0; j < nbpoly; ++j) {
                results.set(j, halfbuffer.get(j));
            }


            for (j = 0, k = 0; j < halfsize; ++j) {
                halfbuffer.set(j, null);

                if (k + 1 < nbpoly)
                    halfbuffer.set(j, new ArrayList<Monomial>(results.get(k).size() + results.get(k + 1).size()));
                else
                    halfbuffer.set(j, new ArrayList<Monomial>(results.get(k).size()));

                k += 2;
            }

        }

        return new Polynomial(results.get(0));
    }

    public Polynomial otimes(Monomial gd) {
        return new Polynomial(gd).otimes(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() != this.getClass()) return false;

        Polynomial poly2 = (Polynomial) obj;
        int i = 0;
        if (poly2 == null || poly2.data.size() != data.size()) return false;
        else
            while (i < this.getCount()) {
                if (data.get(i) != poly2.data.get(i)) return false;
                ++i;
            }
        return true;
    }

    @Override
    public String toString() {
        int last = data.size() - 1;
        String out = "";
        for (int i = 0; i < last; ++i) {
            out += data.get(i).toString() + " + ";
        }

        out += data.get(last).toString();
        return out;
    }
}
