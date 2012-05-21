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

    public void setElement(int n, Monomial gd) {
        data.set(n, gd);
    }

    public Polynomial(final ArrayList<Monomial> list) {
        this();

        if (list.size() >= 1) {
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
        ArrayList<Monomial> result;
        result = new ArrayList<Monomial>(this.data.size() * poly2.data.size());

        for (Monomial m1 : this.data)
            for (Monomial m2 : poly2.data)
                result.add(m1.otimes(m2));
        return new Polynomial(result);
    }

    public Polynomial otimes(Monomial gd) {
        return new Polynomial(gd).otimes(this);
    }

    public Series star() {
        unsigned int i;
        int j, nj, k, nb_pente_inferieure, n;
        long numax = infinity, k1;
        long gammakmax, gammakmin;
        long ki, kmin, a;
        int*tabki = NULL;
        gd * tabgd = NULL;

        double pente, pente1, test1, test2;


        gd monome, rtemp;
        poly temp;    // tableau temporaire
        serie result;
        poly qtemp;
        gd epsilon;


        poly1.simpli();
        // Les cas d����
        //si un ��ent  vaut Top
        if (poly1.getpol(0).getg() == _infinity) {
            result.p.init(infinity, _infinity);
            result.q.init(_infinity, infinity);
            result.r.init(0, 0);
            result.canonise = 1;
            return (result);
        }


        for (i = 0; i < poly1.getn(); i++) // on ote les elements dont l'�oile vaut e
        {
            if (poly1.getpol(i).getg() == infinity || poly1.getpol(i).getd() == 0) {
                if (poly1.getn() > 1) poly1.popj(i);
                else // le resultat est la serie est : epsilon+ e .(e)*
                {        // car il n'y qu'un �ement qui est nul dasn le polyn�e
                    result.r.init(0, 0);
                    result.p.init(infinity, _infinity);
                    result.q.init(0, 0);
                    result.canonise = 1;
                    return (result);  // on retourne (epsilon+ e .(e)*
                }
            }
        }


        for (i = 0; i < poly1.getn(); i++) {
            // on regarde si l'�oile d'un des ��ents vaut (delta)*
            if (poly1.getpol(i).getg() == 0 && poly1.getpol(i).getd() > 0) {
                result.r.init(0, infinity);
                result.q.init(0, infinity);
                result.p.init(infinity, _infinity);
                result.canonise = 1;

                return (result);  // on retourne (delta)*
            }


            if (poly1.getpol(i).getd() == infinity)  // on sauvegarde le nui associ��un taui=infinity
            {
                if (poly1.getpol(i).getg() < numax) numax = poly1.getpol(i).getg();
            }

        } // fin de la boucle for


        // on traite les cas ou au moins 1 des taui vaut infinity
        if (numax != infinity) {
            result.p.init(0, 0);

            for (i = 0; i < poly1.getn(); i++)  // pour chaque ��ent on �end jusqu'�numax si n��saire
            {
                j = 1;
                while (j * poly1.getpol(i).getg() < numax) {
                    monome.init(j * poly1.getpol(i).getg(), j * poly1.getpol(i).getd());
                    result.p.add(monome);
                    j++;
                }

            }

            result.p.simpli();
            result.q.init(numax, infinity);
            result.r.init(0, infinity);
            result.canonise = 1;
            return (result);

        }

        /*** Le cas non d����******/
        // recherche de la plus petite pente ayant le plus petit nu
        pente = infinity;
        nj = 0;
        for (i = 0; i < poly1.getn(); i++)

        {
            pente1 = (double) poly1.getpol(i).getg() / poly1.getpol(i).getd();
            if (pente1 < pente) {
                pente = pente1;

                nj = i;
            }
        }

        // on traite toutes les �oiles dont la pente est inf�ieure �la pente retenue ci dessus
        rtemp.init(poly1.getpol(nj).getg(), poly1.getpol(nj).getd());
        k1 = rtemp.getg() * rtemp.getd();

        // on recherche tout d'abord le kmax qui est une borne sup de l'extension des polyn�es
        // ceci reduit la taille du transitoire r�ultant

        gammakmax = 0;

        nb_pente_inferieure = 0; //pour savoir combien de monomes ont une pente inf�ieure

        if ((tabki = new int[poly1.getn()]) == NULL) {
            mem_limite l (13);
            throw (l);
        }
        // on r�erve un tableau pour sauvegerder les ki pour chaque monome de pente < rtemp

        for (i = 0; i < poly1.getn(); i++) {
            pente1 = (double) poly1.getpol(i).getg() / poly1.getpol(i).getd();
            if (pente1 > pente) {
                ki = rtemp.getd() * poly1.getpol(i).getg() - rtemp.getg() * poly1.getpol(i).getd();

                kmin = MAX(k1, 0);
                kmin = (long) ceil(((double) kmin) / ki);

                a = (long) floor(((double) kmin * poly1.getpol(i).getg()) / rtemp.getg());
                test1 = rtemp.getd() * a;
                test2 = poly1.getpol(i).getd() * kmin;
                while (test1 >= test2 && kmin > 0) {
                    kmin--;
                    a = (long) floor(((double) kmin * poly1.getpol(i).getg()) / rtemp.getg());
                    test1 = (rtemp.getd() * a);
                    test2 = (poly1.getpol(i).getd() * kmin);
                }

                kmin++;
                gammakmin = kmin * poly1.getpol(i).getg();

                if (gammakmin > gammakmax) gammakmax = gammakmin;

                tabki[nb_pente_inferieure] = kmin;
                nb_pente_inferieure++;

            }
        }

        result.p.init(infinity, _infinity);
        result.r = rtemp;
        result.q.init(0, 0);

        n = 0;  // indice pour tabki
        if ((tabgd = new gd[gammakmax + 1]) == NULL) {
            mem_limite l (14);
            throw (l);
        }
        tabgd[0].init(0, 0);

        for (i = 0; i < poly1.getn(); i++)     // on traite toutes les �oiles dont la pente est inf�ieure �la pente retenue ci dessus

        {
            pente1 = (double) poly1.getpol(i).getg() / poly1.getpol(i).getd();
            if (pente1 > pente) {    // on commence par �endre le polyn�e


                qtemp.init(0, 0);

                for (j = 1; j < (int) tabki[n]; j++) //tabki[n] contient le kmin correspondant
                {
                    monome.init(poly1.getpol(i).getg() * j, poly1.getpol(i).getd() * j);
                    qtemp.add(monome);
                }
                n++;
                // puis on fait le produit jusqu'�gammakmax c'est suffisant

                for (j = 0; j < (int) qtemp.getn(); j++) {
                    for (k = 0; k < (int) result.q.getn(); k++) {
                        monome = otimes(qtemp.getpol(j), result.q.getpol(k));
                        if (monome.getg() < gammakmax) {
                            if (monome >= tabgd[monome.getg()]) tabgd[monome.getg()] = monome;
                            else ;
                        } else k = result.q.getn();
                    }

                }

                result.q = tabgd[0];

                for (k = 1; k < gammakmax; k++) {
                    if (tabgd[k] != epsilon) result.q.add(tabgd[k]);
                }

                poly1.popj(i); // on ote l'��ent du polyn�e il est trait�		if ((int)i<nj) nj--;	//l'��ent nj est d�lac�si n�essaire
                i--;


            } // fin du if sur la pente
        } // fin du for sur i

        delete[] tabgd;
        delete[] tabki;


        result.q.simpli();


        //pente identique, il y a surement mieux �faire mais...

        for (i = nj + 1; i < poly1.getn(); i++) // on commence au dela de nj
        {
            for (k = nj; k < (int) i; k++) {
                if ((poly1.getpol(i).getg() % poly1.getpol(k).getg()) == 0) {
                    poly1.popj(i); // on ote l'��ent du polynome car il sera domin�				if ((int)i<nj) nj--;	//l'��ent nj est d�lac�				i--;
                    k = i; // on sort de la boucle for k
                }
            }
        }


        for (i = nj + 1; i < poly1.getn(); i++) // on commence au dela de nj
        // Calcul de la pente
        {
            result.r.init(lcm(result.r.getg(), poly1.getpol(i).getg()),
                    lcm(result.r.getd(), poly1.getpol(i).getd()));

        }

        for (i = nj; i < poly1.getn(); i++) {      //reste ��endre chacun des ��ents autant que necesaire

            qtemp.init(0, 0);
            ki = (long) (result.r.getg() / poly1.getpol(i).getg());

            for (j = 1; j < (int) ki; j++) {
                monome.init(poly1.getpol(i).getg() * j, poly1.getpol(i).getd() * j);
                qtemp.add(monome);
            }

            result.q = otimes(result.q, qtemp);
        }

        result.canon();

        return (result);
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

    public void pop() {
        data.remove(data.size() - 1);
    }

    public void popSome(int n) {
        data = new ArrayList<Monomial>(data.subList(0, data.size() - 1 - n));
    }

    public Polynomial getRange(int start, int end) {
        return new Polynomial((ArrayList<Monomial>) this.data.subList(start, end));
    }

}
