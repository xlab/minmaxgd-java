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
 * Класс для представления монома в алгебре MinMaxGD.
 * <p/>
 * Содержит реализацию базовых операций:
 * сложение, умножение, звезда Клини.
 */
public class Monomial implements Comparable<Monomial> {
    final private int g;
    final private int d;

    /**
     * Получить сдвиг по счётчику
     *
     * @return gamma
     */
    public int getGamma() {
        return g;
    }

    /**
     * Получить сдвиг по времени
     *
     * @return delta
     */
    public int getDelta() {
        return d;
    }

    /**
     * Пустой конструктор - эпсилон = (∞, -∞)
     */
    public Monomial() {
        this.g = Constants.INFINITY;
        this.d = Constants._INFINITY;
    }

    /**
     * Конструктор копирования прототипа
     *
     * @param gd Целевой прототип для копирования
     */
    public Monomial(final Monomial gd) {
        this.g = gd.g;
        this.d = gd.d;
    }

    /**
     * Конструктор инициализации по счётчико-времени
     *
     * @param g Gamma счётчик
     * @param d Delta время
     */
    public Monomial(final int g, final int d) {
        this.g = g;
        this.d = d;
    }

    /**
     * Сравнение с другим мономом
     *
     * @param gd2 другой моном для сравнения
     * @return 1 если больше другого, 0 если равны, -1 иначе
     */
    public int compareTo(Monomial gd2) {
        if (this.g == gd2.g && this.d == gd2.d)
            return 0;

        if (g < gd2.g) return 1;
        if (g == gd2.g)
            if (d > gd2.d) return 1;

        return -1;

    }

    /**
     * Умножение мономов
     *
     * @param gd2 сомножитель
     * @return произведение m = this x gd2
     */
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

    /**
     * Звезда Клини
     *
     * @return m = (this)*
     */
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

    /**
     * Преобразует моном в сроку,
     * которую можно вывести для отладки
     *
     * @return обычная строка
     */
    @Override
    public String toString() {
        return " g^" + ((this.g == Constants.INFINITY) ? ("inf") : ((this.g == Constants._INFINITY) ? ("-inf") : ("" + this.g)))
                + " d^" + ((this.d == Constants.INFINITY) ? ("inf") : ((this.d == Constants._INFINITY) ? ("-inf") : ("" + this.d)));
    }

    /**
     * Проверяет равенство с другим мономом
     *
     * @param o моном для проверки
     * @return true, если равны, false иначе
     */
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
    
    public Polynomial plus(Monomial gd2)
    {
        Polynomial result = new Polynomial(this);
        result = result.oplus(gd2);
        return result;
    }
    
    public Polynomial plus(Polynomial p2)
    {
        Polynomial result = new Polynomial(this);
        result = result.oplus(p2);
        return result;
    }
    
    public Series plus(Series s2)
    {
        Series result = new Series(this);
        result = result.oplus(s2);
        return result;
    }
    
    public Polynomial multiply(Monomial gd2)
    {
        Polynomial result = new Polynomial(this);
        result = result.otimes(gd2);
        return result;
    }
    
    public Polynomial multiply(Polynomial p2)
    {
        Polynomial result = new Polynomial(this);
        result = result.otimes(p2);
        return result;
    }
    
    public Series multiply(Series s2)
    {
        Series result = new Series(this);
        result = result.otimes(s2);
        return result;
    }
}
