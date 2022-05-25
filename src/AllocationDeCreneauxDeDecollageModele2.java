import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class AllocationDeCreneauxDeDecollageModele2 {
    // j'utilise cette variable pour calculer les distances pour mon petit exemple dans un plan cartésien
    static boolean geodesique;

    public static void SolveProblem(ArrayList<Vol> vols) {

        //nombre de dij
        int dij_count = ((vols.size() - 1) * vols.size()) / 2;  // Par exemple si on a 4 vols, la comparaison va se faire entre les vols (1 et 2) (1 et 3) (1 et 4) (2 et 3) (2 et 4) (3 et 4) donc ça nous fait 6 dij
        //compteur pour le remplissage du tableau dij
        int dij_index = 0;
        //retard max
        int retardMax = 8;

        //appeler la methode getBornes qui va nous retourner les intervalles
        ArrayList<ArrayList<BornesLbUb>> leslbubPourChaqueDijGotten = getBornes(vols);
        //modele
        /*  CSP {
            X = { retardi,  i ∈ [1, n] } ∪ {dij ,(i, j) ∈ [1, n] , i < j }
            D =  [0,retardmax] ∪ [-retardmax,retardmax]
            C = {(dij = retardj − retardi) ∧ (dij !∈ Cij), (i, j) ∈ [1, n], i < j}
            }
            (dij !∈ Cij) ⇔ dij =! Tki − Tlj  */
        Model model = new Model("allocation de creneaux de decollage");
        //variables
        //retardi,  i ∈ [1, n]
        IntVar[] retards = model.intVarArray("retard avion", vols.size(), 0, retardMax);
        //dij ,(i, j) ∈ [1, n] , i < j
        IntVar[] dij = model.intVarArray("dij", dij_count, -retardMax, retardMax);

        //contraintes
        //contrainte : dij = retardj − retardi
        dij_index = 0;
        for (int i = 0; i <= vols.size(); i++) {
            for (int j = i + 1; j < vols.size(); j++) {
                dij[dij_index].eq(retards[j].sub(retards[i])).post();
                dij_index++;
            }
        }

        //contrainte : dij !∈ Cij
        // on applique la contrainte à tout les dij
        for (int i = 0; i < dij_count; i++) {

            //Cette liste de contraintes on y met
            ArrayList<Constraint> cs = new ArrayList<Constraint>();
            for (int j = 0; j < leslbubPourChaqueDijGotten.get(i).size(); j++) {
                Constraint c1 = model.arithm(dij[i], "<", leslbubPourChaqueDijGotten.get(i).get(j).lb);
                Constraint c2 = model.arithm(dij[i], ">", leslbubPourChaqueDijGotten.get(i).get(j).ub);
                cs.add(model.or(c1, c2));
            }
            if (cs.size() > 1) {
                Constraint cand = model.and(cs.get(0), cs.get(1));
                for (int j = 2; j < cs.size(); j++) {
                    cand = model.and(cand, cs.get(j));
                }
                cand.post();
            } else {
                if (cs.size()==0){

                }else {
                cs.get(0).post();}
            }

        }

        //afficher toutes les solutions
        Solver solver = model.getSolver();
        while (solver.solve()) {
            System.out.println(solver.findAllSolutions());
        }


    }

    //methode pour obtenir les intervalles
    public static ArrayList getBornes(ArrayList<Vol> vols) {

        //Les intervalles des conflits pour chaque dij
        ArrayList<ArrayList<BornesLbUb>> leslbubPourChaqueDij = new ArrayList<ArrayList<BornesLbUb>>();
        // distance separation en NM
        float distanceSeparation = 20;
        if (geodesique == false) distanceSeparation = 5;
        int conflit_index = 0;
        int dij_index = 0;
        int kk = 0;
        boolean firstk = false;

        //pour chaque dij
        for (int i = 0; i < vols.size(); i++) {
            for (int j = i + 1; j < vols.size(); j++) {
                leslbubPourChaqueDij.add(new ArrayList());
                //parcourir toutes les paires
                for (int k = 0; k < vols.get(i).trajectoire4D.points.size(); k++) {
                    for (int l = 0; l < vols.get(j).trajectoire4D.points.size(); l++) {

                        // si la distance entre les deux points < 5 mille nautique alors il a conflit donc il faut que l'instant de passage soit différent
                        if (obtenirDistance(vols.get(i).trajectoire4D.points.get(k).longitude, vols.get(i).trajectoire4D.points.get(k).latitude, vols.get(j).trajectoire4D.points.get(l).longitude, vols.get(j).trajectoire4D.points.get(l).latitude) < distanceSeparation) {
                            //    System.out.println("Nous sommes en conflit : avion " + (i + 1) + " point " + (k) + " avec avion " + (j + 1) + " point " + (l));
                            System.out.println("Nous sommes en conflit : avion " + (i + 1) + " point " + (k) + " " + vols.get(i).trajectoire4D.points.get(k).longitude + " " + vols.get(i).trajectoire4D.points.get(k).latitude + " avec avion " + (j + 1) + " point " + (l) + " " + vols.get(j).trajectoire4D.points.get(l).longitude + " " + vols.get(j).trajectoire4D.points.get(l).latitude);

                            // premier k, premier point de conflit
                            if (firstk == false) {
                                kk = k;
                                firstk = true;
                                //initialisation de LB et UB à 0
                                leslbubPourChaqueDij.get(dij_index).add(new BornesLbUb(0, 0));

                            }
                            // passage au conflit suivant
                            if (k > kk + 1) {
                                conflit_index++;
                                //ajout de l'intervalle du conflit suivant au meme dij
                                leslbubPourChaqueDij.get(dij_index).add(new BornesLbUb(0, 0));
                            }
                            kk = k;

                            if (leslbubPourChaqueDij.get(dij_index).get(conflit_index).getLb() > (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)) {
                                leslbubPourChaqueDij.get(dij_index).get(conflit_index).setLb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));
                            }
                            if (leslbubPourChaqueDij.get(dij_index).get(conflit_index).getUb() < (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)) {
                                leslbubPourChaqueDij.get(dij_index).get(conflit_index).setUb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));
                            }

                        } else {

                        }
                    }
                }
                dij_index++;
                firstk = false;
                conflit_index = 0;
                //        System.out.println(" dijindex" + dij_index);
            }
        }
        System.out.println(leslbubPourChaqueDij);
        System.out.println(leslbubPourChaqueDij.size());

        return leslbubPourChaqueDij;
    }


    public static double obtenirDistance(double aX, double aY, double bX, double bY) {
        if (geodesique == false) {
            double xDiff = aX - bX;
            double yDiff = aY - bY;
            return (double) Math.sqrt(xDiff * xDiff + yDiff * yDiff);

        } else {

            int idsommet0;
            int idsommet1;
            //  Sommet sommet0 = numerotation.elementAt(idsommet0);
            //   Sommet sommet1 = numerotation.elementAt(idsommet1);
            double lat = aY;
            double lon = aX;
            double lat2 = bY;
            double lon2 = bX;
            int R = 6371; // km
            double dLat = Math.toRadians(Math.abs(lat - lat2));
            double dLon = Math.toRadians(Math.abs(lon - lon2));

            double ta = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double tc = 2 * Math.atan2(Math.sqrt(ta), Math.sqrt(1 - ta));
            double td = (double) (R * tc);
            td = td * 0.539957;
            return td;
        }


    }


}
