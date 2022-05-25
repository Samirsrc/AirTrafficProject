import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class AllocationDeCreneauxDeDecollageModele1 {
    // j'utilise cette variable pour calculer les distances pour mon petit exemple dans un plan cartésien
    static boolean geodesique;

    public static void SolveProblem(ArrayList<Vol> vols) {
        //nombre de dij
        int dij_count = ((vols.size() - 1) * vols.size()) / 2;  // Par exemple si on a 4 vols, la comparaison va se faire entre les vols (1 et 2) (1 et 3) (1 et 4) (2 et 3) (2 et 4) (3 et 4) donc ça nous fait 6 dij
        //compteur pour le remplissage du tableau dij
        int dij_index = 0;
        //retard max, je l'ai limité à 120 secondes (8 instants) pour pas que le nombre de solutions soit trop grand
        int retardMax = 8;

        // distance separation en NM
        float distanceSeparation = 20;
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
        for (int i = 0; i <= vols.size(); i++) {
            for (int j = i + 1; j < vols.size(); j++) {
                dij[dij_index].eq(retards[j].sub(retards[i])).post();
                dij_index++;
            }
        }
        //contrainte : dij =! Tki − Tlj
        dij_index = 0;

        if (geodesique == false) distanceSeparation = 5;

        for (int i = 0; i <= vols.size(); i++) {

            for (int j = i + 1; j < vols.size(); j++) {

                for (int k = 0; k < vols.get(i).trajectoire4D.points.size(); k++) {


                    for (int l = 0; l < vols.get(j).trajectoire4D.points.size(); l++) {

                        // si la distance entre les deux points < 5 mille nautique alors il a conflit donc il faut que l'instant de passage soit différent
                        if (obtenirDistance(vols.get(i).trajectoire4D.points.get(k).longitude, vols.get(i).trajectoire4D.points.get(k).latitude, vols.get(j).trajectoire4D.points.get(l).longitude, vols.get(j).trajectoire4D.points.get(l).latitude) < distanceSeparation) {
                            System.out.println("Nous sommes en conflit : avion " + (i + 1) + " point " + (k) + " avec avion " + (j + 1) + " point " + (l));

                            dij[dij_index].ne((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)).post();

                        }
                    }
                }

                dij_index++;
            }
        }


        Solver solver = model.getSolver();
        while (solver.solve()) {
            System.out.println(solver.findSolution());
        }


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
