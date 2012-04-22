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
    private boolean simple;

    public void sort() {
        Collections.sort(data);
    }

    public Polynomial(final Polynomial p2) {
        Collections.copy(this.data, p2.data);
        this.simple = p2.simple;
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
            int n = data.size();

            for (int j = 1; j < n; ++j) {
                if (data.get(j).getDelta() > data.get(j).getDelta()) {
                    ++i;
                    data.set(i, data.get(j));
                }
            }

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

            simple = true;
        }


    }
}
