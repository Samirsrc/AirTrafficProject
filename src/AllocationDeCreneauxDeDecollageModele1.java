import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMiddle;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;

public class AllocationDeCreneauxDeDecollageModele1 {
    // j'utilise cette variable pour calculer les distances pour mon petit exemple dans un plan cartésien
    static boolean geodesique;

    public static void SolveProblem(ArrayList<Vol> vols) {
        //compteur pour le remplissage du tableau dij
        int dij_index = 0;
        //retard max, je l'ai limité à 120 secondes (8 instants) pour pas que le nombre de solutions soit trop grand
        int retardMax = 8;
        //distance separation en NM
        float distanceSeparation = 20;

        //modele
        Model model = new Model("allocation de creneaux de decollage");

        //variables
        //retardi,  i ∈ [1, n]
        IntVar[] retards = model.intVarArray("retard avion", vols.size(), 0, retardMax);
        //Dij
        IntVar[] dij = new IntVar[0];


        //contraintes

        boolean dij_bool = false;
        if (geodesique == false) distanceSeparation = 5;

        for (int i = 0; i <= vols.size(); i++) {

            for (int j = i + 1; j < vols.size(); j++) {

                for (int k = 0; k < vols.get(i).trajectoire4D.points.size(); k++) {

                    for (int l = 0; l < vols.get(j).trajectoire4D.points.size(); l++) {

                        // si la distance entre les deux points < 5 mille nautique alors il a conflit donc il faut que l'instant de passage soit différent
                        if (obtenirDistance(vols.get(i).trajectoire4D.points.get(k).longitude, vols.get(i).trajectoire4D.points.get(k).latitude, vols.get(j).trajectoire4D.points.get(l).longitude, vols.get(j).trajectoire4D.points.get(l).latitude) < distanceSeparation) {
                            System.out.println("vol(" + i + ") point(" + k + ") (lon: " + vols.get(i).trajectoire4D.points.get(k).longitude + " | lat: " + vols.get(i).trajectoire4D.points.get(k).latitude + " | instant: " + vols.get(i).trajectoire4D.points.get(k).instantDePassage + " )   \nen conflit avec   \nvol(" + j + ") point(" + l + ") (lon: " + vols.get(j).trajectoire4D.points.get(l).longitude + " | lat: " + vols.get(j).trajectoire4D.points.get(l).latitude + " | instant: " + vols.get(j).trajectoire4D.points.get(l).instantDePassage + " )     \nTik-Tjl = " + (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));
                            System.out.println("-----------------------------------------------------------------------------------------");

                            //creation de la variable Dij
                            if (dij_bool == false) {
                                dij = new IntVar[dij_index + 1];
                                dij[dij_index] = model.intVar(("D" + i + "#" + j), -retardMax, retardMax);
                                dij_bool = true;
                                //contrainte dij = retard j - retard i
                                dij[dij_index].eq(retards[j].sub(retards[i])).post();

                                dij_index++;
                            }
                            //contrainte dij != Tik - Tjk
                            dij[dij_index - 1].ne((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)).post();

                        }
                    }
                }
                dij_bool = false;

            }
        }


       /* Solver solver = model.getSolver();
        while (solver.solve()) {
            System.out.println(solver.findSolution());

        }*/


        // to maximize X
        IntVar max = model.intVar("max", 0, retardMax);
        model.max(max, retards).post();
        model.setObjective(Model.MINIMIZE, max);
// or model.setObjective(Model.MINIMIZE, X); to minimize X
        Solver solver = model.getSolver();

        solver.setSearch(Search.intVarSearch(
                new VariableSelectorWithTies<>(
                        new FirstFail(model),
                        new Smallest()),
                new IntDomainMiddle(false),
                ArrayUtils.append(retards, dij))
        );

        //  solver.setSearch(minDomUBSearch(ArrayUtils.concat(dij, retards)));
        solver.showShortStatistics();
        while (solver.solve()) {
            //   solver.showSolutions();
            prettyPrint(model, retards, vols.size(), max);
            solver.printStatistics();
            System.out.println("Retard MAX = " + max.getValue());
        }

    }

    private static void prettyPrint(Model model, IntVar[] retards, int Vols, IntVar max) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < Vols; i++) {
            st.append(String.format("\tRetard du Vol %d est = %d ", i, retards[i].getValue()));
        }
        st.append("\n");
        st.append(String.format("Retard MAX est = %d ", max.getValue()));
        st.append("\n");

        System.out.println(st.toString());
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
