package hse.kcvc.jminmaxgd;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Kouprianov Maxim
 * Date: 22.04.12
 * Time: 20:18
 * Contact: me@kc.vc
 */
public class SeriesTest {
    @Test
    public void test() throws Exception {

        Polynomial p1 = new Polynomial(new ArrayList<Monomial>(Arrays.asList(
                new Monomial[]{
                        new Monomial(1, 1),
                        new Monomial(2, 3),
                        new Monomial(4, 5),
                }
        )));

        Polynomial p2 = new Polynomial(new ArrayList<Monomial>(Arrays.asList(
                new Monomial[]{
                        new Monomial(1, 3),
                        new Monomial(3, 3),
                        new Monomial(8, 4),
                }
        )));

        Polynomial q1 = new Polynomial(new ArrayList<Monomial>(Arrays.asList(
                new Monomial[]{
                        new Monomial(10, 11),
                        new Monomial(12, 15),
                }
        )));

        Polynomial q2 = new Polynomial(new ArrayList<Monomial>(Arrays.asList(
                new Monomial[]{
                        new Monomial(10, 5),
                        new Monomial(12, 7),
                        new Monomial(13, 9),
                }
        )));

        Monomial r1 = new Monomial(2, 3);
        Monomial r2 = new Monomial(4, 4);

        Series s1 = new Series(p1, q1, r1);
        Series s2 = new Series(p2, q2, r2);

        System.out.println(s2.otimes(s1));
    }
}
