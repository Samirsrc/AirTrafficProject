import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMiddle;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.chocosolver.solver.search.strategy.Search.minDomUBSearch;

public class AllocationDeCreneauxDeDecollageModele3 {
    // j'utilise cette variable pour calculer les distances pour mon petit exemple dans un plan cartésien
    static boolean geodesique;

    public static void SolveProblem(ArrayList<Vol> vols) {

        //compteur pour le remplissage du tableau dij
        int dij_index = 0;
        //retard max
        int retardMax = 8;

        //appeler la methode getBornes qui va nous retourner les intervalles
        HashMap<String, ArrayList<BornesLbUb>> lesintervallesGotten = getBornes(vols);

        //modele
        Model model = new Model("allocation de creneaux de decollage");

        //variables
        //retardi,  i ∈ [1, n]
        IntVar[] retards = model.intVarArray("retard avion", vols.size(), 0, retardMax);
        //dij


        //contraintes
        dij_index = 0;

ArrayList<IntVar> tmpVarArray = new ArrayList<IntVar>();

        IntVar tmpDij;
        //contrainte : dij !∈ aux intervalles
        //on applique la contrainte à tout les dij
        for (Map.Entry m : lesintervallesGotten.entrySet()) {

          //  dij = new IntVar[dij_index + 1];
            // dij[dij_index]
            tmpDij   = model.intVar(m.getKey().toString(), -retardMax, retardMax);
            String splitdij[] = m.getKey().toString().split("D");
            String splitdij2[] = splitdij[1].split("#");
            // dij[dij_index]
            tmpDij.eq(retards[Integer.parseInt(splitdij2[1])].sub(retards[Integer.parseInt(splitdij2[0])])).post();
            dij_index++;
            tmpVarArray.add(tmpDij);
            Constraint c = new Constraint("MyConstraint", new IntervallesPropagator(tmpDij, lesintervallesGotten.get(m.getKey())));
            c.post();
        }

        IntVar[] dij = new IntVar[tmpVarArray.size()];
        for(int i = 0; i < tmpVarArray.size(); i++){
            dij [i] = tmpVarArray.get(i);
        }


        //afficher toutes les solutions
      /*  Solver solver = model.getSolver();
        while (solver.solve()) {
            System.out.println(solver.findAllSolutions());
        }*/

        // IntVar[] max = model.intVarArray("max", vols.size(),0, retardMax);
        //  model.max

        /*System.out.println("MAX_MIN ");
        model.max(max, clusterCapacity).post();
        model.setObjective(Model.MINIMIZE, max);*/
        // max = model.intVar("max", alpha, beta);
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

    //methode pour obtenir les intervalles
    public static HashMap<String, ArrayList<BornesLbUb>> getBornes(ArrayList<Vol> vols) {

        //Les intervalles des conflits pour chaque dij
        HashMap<String, ArrayList<BornesLbUb>> lesintervalles = new HashMap<String, ArrayList<BornesLbUb>>();

        // distance separation en NM
        float distanceSeparation = 20;
        if (geodesique == false) distanceSeparation = 5;
        int conflit_index = 0;
        int dij_index = 0;
        int kk = 0;
        boolean firstk = false;
        boolean dij_bool = false;

        //pour chaque dij
        for (int i = 0; i < vols.size(); i++) {
            for (int j = i + 1; j < vols.size(); j++) {
                //       leslbubPourChaqueDij.add(new ArrayList());
                //parcourir toutes les paires
                for (int k = 0; k < vols.get(i).trajectoire4D.points.size(); k++) {
                    for (int l = 0; l < vols.get(j).trajectoire4D.points.size(); l++) {

                        // si la distance entre les deux points < 5 mille nautique alors il a conflit donc il faut que l'instant de passage soit différent
                        if (obtenirDistance(vols.get(i).trajectoire4D.points.get(k).longitude, vols.get(i).trajectoire4D.points.get(k).latitude, vols.get(j).trajectoire4D.points.get(l).longitude, vols.get(j).trajectoire4D.points.get(l).latitude) < distanceSeparation) {

                            System.out.println("vol(" + i + ") point(" + k + ") (lon: " + vols.get(i).trajectoire4D.points.get(k).longitude + " | lat: " + vols.get(i).trajectoire4D.points.get(k).latitude + " | instant: " + vols.get(i).trajectoire4D.points.get(k).instantDePassage + " )   en conflit avec   vol(" + j + ") point(" + l + ") (lon: " + vols.get(j).trajectoire4D.points.get(l).longitude + " | lat: " + vols.get(j).trajectoire4D.points.get(l).latitude + " | instant: " + vols.get(j).trajectoire4D.points.get(l).instantDePassage + " )    || Tik-Tjl = " + (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));


                            if (dij_bool == false) {
                                lesintervalles.put(("D" + i + "#" + j), new ArrayList<BornesLbUb>());
                                dij_bool = true;
                                dij_index++;
                            }

                            // premier k, premier point de conflit
                            if (firstk == false) {
                                kk = k;
                                firstk = true;
                                //initialisation de LB et UB
                                lesintervalles.get("D" + i + "#" + j).add(new BornesLbUb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage), (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)));
                            }
                            // passage au conflit suivant
                            if (k > kk + 1) {
                                conflit_index++;
                                //ajout de l'intervalle du conflit suivant au meme dij
                                lesintervalles.get("D" + i + "#" + j).add(new BornesLbUb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage), (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)));
                            }
                            kk = k;

                            if (lesintervalles.get("D" + i + "#" + j).get(conflit_index).getLb() > (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)) {
                                lesintervalles.get("D" + i + "#" + j).get(conflit_index).setLb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));
                            }
                            if (lesintervalles.get("D" + i + "#" + j).get(conflit_index).getUb() < (vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage)) {
                                lesintervalles.get("D" + i + "#" + j).get(conflit_index).setUb((vols.get(i).trajectoire4D.points.get(k).instantDePassage - vols.get(j).trajectoire4D.points.get(l).instantDePassage));
                            }

                        } else {

                        }
                    }
                }
                dij_index++;
                firstk = false;
                conflit_index = 0;
                dij_bool = false;
            }
        }
        System.out.println(lesintervalles);
        System.out.println(lesintervalles.size());

        return lesintervalles;
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

