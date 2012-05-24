package hse.kcvc.jminmaxgd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Создал: Максим Куприянов,
 * 272ПИ, НИУ-ВШЭ
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
 * Класс для представления полинома в алгебре MinMaxGD.
 * <p/>
 * Содержит реализацию базовых операций:
 * сложение, умножение, звезда Клини.
 */
public final class Polynomial {
    private ArrayList<Monomial> data;

    /**
     * Проверка является ли текущая
     * форма полинома минимальной
     *
     * @return true, если минимальная, false иначе
     */
    public boolean isSimple() {
        return simple;
    }

    private boolean simple;

    /**
     * Сортировка элементов полинома по возрастанию
     */
    void sort() {
        Collections.sort(data);
        Collections.reverse(data);
    }

    /**
     * Подсчёт количества слагаемых в полиноме
     *
     * @return количество
     */
    public int getCount() {
        return data.size();
    }

    /**
     * Получить элемент полинома по заданному
     * порядковому номеру
     *
     * @param n номер (отсчёт с нуля)
     * @return соответствующий моном
     */
    public Monomial getElement(int n) {
        if (n < 0 || n >= data.size()) {
            return new Monomial();
        }
        return data.get(n);
    }

    /**
     * Заменить элемент полинома по заданному
     * порядковому номеру
     *
     * @param n  номер (отсчёт с нуля)
     * @param gd моном, который нкжно поместить под номером n
     */
    public void setElement(int n, Monomial gd) {
        data.set(n, gd);
    }

    /**
     * Конструктор - инициализация полинома
     * по массиву слагаемых [m1, m2, ..., mn]:
     * p = m1 + m2 + m3 + ... + mn
     *
     * @param list массив мономов
     */
    public Polynomial(List<Monomial> list) {
        this();

        if (list.size() >= 1) {
            this.data = new ArrayList<Monomial>(list);
            simple = false;
            sortSimplify();
        }
    }

    /**
     * Конструктор копирования - делает глубокую
     * копию указанного прототипа
     *
     * @param p2 прототип для копирования
     */
    public Polynomial(Polynomial p2) {
        this();

        this.data = (ArrayList<Monomial>) p2.data.clone();
        this.simple = p2.simple;
    }

    /**
     * Пустой контсруктор - пустой полином
     * p = epsilon
     */
    public Polynomial() {
        this.data = new ArrayList<Monomial>(Constants.POLY_SIZE);
        this.data.add(new Monomial());
        this.simple = true;
    }

    /**
     * Конструктор - инициализация
     * полинома с одним элементом
     * p = gd
     *
     * @param gd элемент
     */
    public Polynomial(final Monomial gd) {
        this.data = new ArrayList<Monomial>(Constants.POLY_SIZE);
        this.data.add(gd);
        this.simple = true;
    }

    /**
     * Сортировка, а затем
     * упрощение формы полинома
     */
    public void sortSimplify() {
        if (simple) return;
        if (data != null) {
            sort();
            simplify();
        }
    }

    /**
     * Сам метод упрощения (при условии,
     * что элементы полинома отсортированы по-возрастанию)
     */
    private void simplify() {
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
            this.simple = true;
        }
    }

    /**
     * Добавление слагамеого в полином
     * без упрощения результата
     *
     * @param gd моном для добавления
     */
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

    /**
     * Вычисление суммы полиномов
     * p = this + poly2
     *
     * @param poly2 слагаемое
     * @return новый полином - сумма двух
     */
    public Polynomial oplus(final Polynomial poly2) {
        ArrayList<Monomial> list = new ArrayList<Monomial>(data);
        list.addAll(poly2.data);

        return new Polynomial(list);
    }

    /**
     * Вычисление суммы
     * p = this + gd
     *
     * @param gd слагаемое
     * @return новый полином - сумма исходного с мономом
     */
    public Polynomial oplus(final Monomial gd) {
        Polynomial result = new Polynomial(data);
        result.addElement(gd);
        result.sortSimplify();

        return result;
    }

    /**
     * Вычисление произведения двух полиномов
     * p = this * poly2
     *
     * @param poly2 сомножитель
     * @return новый полином - произведение двух
     */
    public Polynomial otimes(Polynomial poly2) {
        ArrayList<Monomial> result;
        result = new ArrayList<Monomial>(this.data.size() * poly2.data.size());

        for (Monomial m1 : this.data)
            for (Monomial m2 : poly2.data)
                result.add(m1.otimes(m2));
        return new Polynomial(result);
    }

    /**
     * Вычисление произведения
     * полинома и произвольного монома
     * p = this * gd
     *
     * @param gd сомножитель
     * @return новый полином - произведение
     */
    public Polynomial otimes(Monomial gd) {
        return new Polynomial(gd).otimes(this);
    }

    /**
     * Звезда Клини
     *
     * @return p = (this)*
     */
    public Series star() {

        int i, k1;
        int j, nj, k, nb_lower_slope, n;
        int numax = Constants.INFINITY;
        int gammakmax, gammakmin;
        int ki, kmin, a;
        int[] tabki;
        Monomial[] tabgd;
        Monomial rtemp;
        double pente, pente1, test1, test2;

        Series result = new Series();
        Polynomial operation = new Polynomial(this);
        Polynomial qtemp;
        Monomial epsilon = new Monomial();


        operation.sortSimplify();

        if (operation.getElement(0).getGamma() == Constants._INFINITY) {
            result.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
            result.q = new Polynomial(new Monomial(Constants._INFINITY, Constants.INFINITY));
            result.r = new Monomial(0, 0);
            result.canonical = true;
            return (result);
        }


        for (i = 0; i < operation.getCount(); i++) {
            if (operation.getElement(i).getGamma() == Constants.INFINITY || operation.getElement(i).getDelta() == 0) {
                if (operation.getCount() > 1) {
                    operation.popSome(i);
                } else {
                    result.r = new Monomial(0, 0);
                    result.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
                    result.q = new Polynomial(new Monomial(0, 0));
                    result.canonical = true;
                    return (result);
                }
            }
        }


        for (i = 0; i < operation.getCount(); i++) {
            if (operation.getElement(i).getGamma() == 0 && operation.getElement(i).getDelta() > 0) {
                result.r = new Monomial(0, Constants.INFINITY);
                result.q = new Polynomial(new Monomial(0, Constants.INFINITY));
                result.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
                result.canonical = true;

                return (result);
            }


            if (operation.getElement(i).getDelta() == Constants.INFINITY) {
                if (operation.getElement(i).getGamma() < numax) {
                    numax = operation.getElement(i).getGamma();
                }
            }

        }

        if (numax != Constants.INFINITY) {
            result.p = new Polynomial(new Monomial(0, 0));

            for (i = 0; i < operation.getCount(); i++) {
                j = 1;
                while (j * operation.getElement(i).getGamma() < numax) {
                    result.p.addElement(new Monomial(j * operation.getElement(i).getGamma(), j * operation.getElement(i).getDelta()));
                    j++;
                }

            }

            result.p.sortSimplify();
            result.q = new Polynomial(new Monomial(numax, Constants.INFINITY));
            result.r = new Monomial(0, Constants.INFINITY);
            result.canonical = true;
            return (result);
        }

        pente = Constants.INFINITY;
        nj = 0;
        for (i = 0; i < operation.getCount(); i++) {
            pente1 = (double) operation.getElement(i).getGamma() / operation.getElement(i).getDelta();
            if (pente1 < pente) {
                pente = pente1;

                nj = i;
            }
        }

        rtemp = new Monomial(operation.getElement(nj).getGamma(), operation.getElement(nj).getDelta());
        k1 = rtemp.getGamma() * rtemp.getDelta();
        gammakmax = 0;

        nb_lower_slope = 0;
        tabki = new int[operation.getCount()];


        for (i = 0; i < operation.getCount(); i++) {
            pente1 = (double) operation.getElement(i).getGamma() / operation.getElement(i).getDelta();
            if (pente1 > pente) {
                ki = rtemp.getDelta() * operation.getElement(i).getGamma() - rtemp.getGamma() * operation.getElement(i).getDelta();

                kmin = Math.max(k1, 0);
                kmin = (int) Math.ceil(((double) kmin) / ki);

                a = (int) Math.floor(((double) kmin * operation.getElement(i).getGamma()) / rtemp.getGamma());
                test1 = rtemp.getDelta() * a;
                test2 = operation.getElement(i).getDelta() * kmin;
                while (test1 >= test2 && kmin > 0) {
                    kmin--;
                    a = (int) Math.floor(((double) kmin * operation.getElement(i).getGamma()) / rtemp.getGamma());
                    test1 = (rtemp.getDelta() * a);
                    test2 = (operation.getElement(i).getDelta() * kmin);
                }

                kmin++;
                gammakmin = kmin * operation.getElement(i).getGamma();

                if (gammakmin > gammakmax) gammakmax = gammakmin;

                tabki[nb_lower_slope] = kmin;
                nb_lower_slope++;
            }
        }

        result.p = new Polynomial(new Monomial(Constants.INFINITY, Constants._INFINITY));
        result.r = rtemp;
        result.q = new Polynomial(new Monomial(0, 0));

        n = 0;
        tabgd = new Monomial[gammakmax + 1];
        for (i = 0; i < tabgd.length; ++i) {
            tabgd[i] = epsilon;
        }
        tabgd[0] = new Monomial(0, 0);

        for (i = 0; i < operation.getCount(); i++) {
            pente1 = (double) operation.getElement(i).getGamma() / operation.getElement(i).getDelta();
            if (pente1 > pente) {
                qtemp = new Polynomial(new Monomial(0, 0));

                for (j = 1; j < tabki[n]; j++) {
                    qtemp.addElement(new Monomial(operation.getElement(i).getGamma() * j, operation.getElement(i).getDelta() * j));
                }

                n++;

                for (j = 0; j < qtemp.getCount(); j++) {
                    for (k = 0; k < result.q.getCount(); k++) {
                        Monomial monome = qtemp.getElement(j).otimes(result.q.getElement(k));
                        if (monome.getGamma() < gammakmax) {
                            if (monome.compareTo(tabgd[monome.getGamma()]) >= 0) tabgd[monome.getGamma()] = monome;
                        } else k = result.q.getCount();
                    }

                }

                result.q = new Polynomial(tabgd[0]);

                for (k = 1; k < gammakmax; k++) {
                    if (!tabgd[k].equals(epsilon)) result.q.addElement(tabgd[k]);
                }

                operation.popSome(i);
                i--;
            }
        }

        result.q.sortSimplify();

        for (i = nj + 1; i < operation.getCount(); i++) {
            for (k = nj; k < i; k++) {
                if ((operation.getElement(i).getGamma() % operation.getElement(k).getGamma()) == 0) {
                    operation.popSome(i);
                    k = i;
                }
            }
        }


        for (i = nj + 1; i < operation.getCount(); i++) {
            result.r = new Monomial(Tools.lcm(result.r.getGamma(), operation.getElement(i).getGamma()),
                    Tools.lcm(result.r.getDelta(), operation.getElement(i).getDelta()));

        }

        for (i = nj; i < operation.getCount(); i++) {

            qtemp = new Polynomial(new Monomial(0, 0));
            ki = result.r.getGamma() / operation.getElement(i).getGamma();

            for (j = 1; j < ki; j++) {
                qtemp.addElement(new Monomial(operation.getElement(i).getGamma() * j, operation.getElement(i).getDelta() * j));
            }

            result.q = result.q.otimes(qtemp);
        }

        result.canonize();
        return (result);
    }

    /**
     * Проверяет равенство полиномов
     *
     * @param obj полином для сравнения
     * @return true если равны, false иначе
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() != this.getClass())
            return false;

        Polynomial poly2 = (Polynomial) obj;
        int i = 0;
        if (poly2 == null || poly2.data.size() != data.size())
            return false;
        else
            while (i < this.getCount()) {
                if (!data.get(i).equals(poly2.data.get(i)))
                    return false;
                ++i;
            }
        return true;
    }

    /**
     * Преобразует полином в сроку,
     * которую можно вывести для отладки
     *
     * @return обычная строка
     */
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

    /**
     * Удаление последнего
     * слагаемого полинома
     */
    public void pop() {
        if (data.size() > 1) {
            data.remove(data.size() - 1);
        } else {
            data = new ArrayList<Monomial>();
            data.add(new Monomial());
        }
    }

    /**
     * Удаление j последних
     * слагаемых полинома
     *
     * @param j кол-во слагаемых для удаления
     */
    public void popSome(int j) {
        int n = data.size();
        if (data.size() > 0) {
            if (j < (n - 1)) for (int i = j; i < (n - 1); i++) data.set(i, data.get(i + 1));
            if (j < n) pop();
        }
    }

    /**
     * Получение полинома, который содержит
     * диапазон мономов другого полинома
     *
     * @param start позиция для начала
     * @param end   позиция конца
     * @return новый полином размера (end - start) слагаемых
     */
    public Polynomial getRange(int start, int end) {
        end++;
        ArrayList<Monomial> list = new ArrayList<Monomial>(end - start + 5);
        for (int i = start; i < end; ++i) {
            list.add(this.data.get(i));
        }
        if (list.size() > 0) {
            return new Polynomial(list);
        } else {
            return new Polynomial(new Monomial());
        }
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (simple ? 1 : 0);
        return result;
    }


    /**
     * Метод для получения коллеции
     * мономов полинома
     *
     * @return коллекция мономов в полиноме
     */
    public ArrayList<Monomial> getData() {
        return data;
    }
}
