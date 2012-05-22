package hse.kcvc.jminmaxgd;

import org.junit.Test;

import java.util.ArrayList;

/**
 * User: Kouprianov Maxim
 * Date: 22.04.12
 * Time: 20:18
 * Contact: me@kc.vc
 */
public class SeriesTest {
    @Test
    public void test() throws Exception {

        ArrayList<Monomial> p = new ArrayList<Monomial>();
        p.add(new Monomial(1, 2));
        p.add(new Monomial(3, 4));
        p.add(new Monomial(5, 6));

        ArrayList<Monomial> q = new ArrayList<Monomial>();
        q.add(new Monomial(4, 5));
        q.add(new Monomial(7, 9));

        Series s1 = new Series(new Polynomial(p), new Polynomial(q), new Monomial(1, 6));
        Series s2 = new Series(new Polynomial(q), new Polynomial(p), new Monomial(3, 2));
        s1.canonize();
        s1 = s1.star();

        s2.canonize();
        s2 = s2.star();
        System.out.println(s1);
        System.out.println(s2);
        //System.out.println(s1);
        // System.out.println(p);
        // System.out.println(q);
        //System.out.println(r);
        //System.out.println(new Polynomial(new Monomial(1,1)).equals(new Polynomial(new Monomial(1,1))));
    }
}
