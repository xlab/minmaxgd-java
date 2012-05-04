package hse.kcvc.jminmaxgd;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Kouprianov Maxim
 * Date: 22.04.12
 * Time: 20:18
 * Contact: me@kc.vc
 */
public class PolynomialTest {
    @Test
    public void testOtimes() throws Exception {
        ArrayList<Monomial> list1 = new ArrayList<Monomial>();
        ArrayList<Monomial> list2 = new ArrayList<Monomial>();
        for (int i = 0; i < 100; ++i) {
            int a, b, c, d;
            a = new Random().nextInt(20);
            b = new Random().nextInt(20);
            c = new Random().nextInt(20);
            d = new Random().nextInt(20);

            list1.add(new Monomial(a, b));
            list2.add(new Monomial(c, d));

            // System.out.println("m1["+i+"] = gd("+a+","+b+");");
            // System.out.println("m2["+i+"] = gd("+c+","+d+");");
        }

        Polynomial poly1 = new Polynomial(list1);
        Polynomial poly2 = new Polynomial(list2);
        Polynomial poly3 = poly1.otimes(poly2);

        System.out.println("==============================");
        System.out.println(poly3.getCount());
        System.out.println("==============================");
        //System.out.println(poly3.toString());
    }
}
