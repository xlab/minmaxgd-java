package hse.kcvc.jminmaxgd;

/**
 * User: Kouprianov Maxim
 * Date: 22.05.12
 * Time: 1:06
 * Contact: me@kc.vc
 */
class Tools {

    public static int gcd(int a, int b) {
        int r;

        while (b > 0) {
            r = a % b;
            a = b;
            b = r;
        }
        return (a);
    }

    public static int lcm(int a, int b) {

        int a_saves, b_saves;

        a_saves = a;
        b_saves = b;

        a = gcd(a, b);

        return ((a_saves * b_saves) / a);

    }
}
