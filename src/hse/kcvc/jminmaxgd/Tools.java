package hse.kcvc.jminmaxgd;

/**
 * Создал: Максим Куприянов,
 * Факультет Бизнес-информатики
 * Отделение Программной инженерии
 * 2 курс, группа 272ПИ, НИУ-ВШЭ
 *
 * Проект: Курсовая работа 2011-2012гг
 *
 * Тема: "Программа выполнения операций в
 * идемпотентном полукольце конус-ограниченных
 * множеств."
 *
 * Программа: libMinMaxGD
 *
 * Связь: me@kc.vc
 */

/**
 * Вспомогательный класс с 2 полезными
 * для вычислений функциями.
 */
class Tools {

    /**
     * Вычисляет НОД двух чисел
     *
     * @param a число
     * @param b число
     * @return НОД(a, b)
     */
    public static int gcd(int a, int b) {
        int r;

        while (b > 0) {
            r = a % b;
            a = b;
            b = r;
        }
        return (a);
    }

    /**
     * Вычисляет НОК двух чисел
     *
     * @param a число
     * @param b число
     * @return НОК(a, b)
     */
    public static int lcm(int a, int b) {

        int a_saves, b_saves;

        a_saves = a;
        b_saves = b;

        a = gcd(a, b);

        return ((a_saves * b_saves) / a);

    }
}
