import javax.swing.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int i = 0;
        int r = 0;
        boolean dataLu = false;
        while (i == 0) {
            int j = 0;
            while (j == 0) {
                try {
                    r = Integer.parseInt(JOptionPane.showInputDialog("Saisissez : \n[1] Pour quitter\n\n[2] Pour Lire la data json de deux petites trajectoires et résoudre avec le premier modèle\n[3] Pour Lire la data json de deux petites trajectoires et résoudre avec le deuxième modèle (avec les intervalles) \n[4] Pour Lire la data json de deux petites trajectoires et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)\n\n[5] Pour Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le premier modèle\n[6] Pour Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le deuxième modèle (avec les intervalles)\n[7] Pour Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)\n\n[8] Pour Lire la data excel de vols/trajectoires réels\n[9] Pour afficher la data excel de vols/trajectoires réels\n[10] Pour résoudre avec le premier modèle\n[11] Pour résoudre avec le deuxième modèle (avec les intervalles)\n[12] Pour résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)"));
                    j = 1;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir une valeur numérique");
                    j = 0;
                }
            }
            switch (r) {
                case 1:
                    //Quitter
                    i = 1;
                    break;
                //Lire la data json de deux petites trajectoires et résoudre avec le premier modèle
                case 2: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de deux petites trajectoires et résoudre avec le premier modèle");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/deuxPetitesTrajectoires");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele1.geodesique = false;
                    AllocationDeCreneauxDeDecollageModele1.SolveProblem(vols);
                    break;
                }
                //Lire la data json de deux petites trajectoires et résoudre avec le deuxième modèle (avec les intervalles)
                case 3: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de deux petites trajectoires et résoudre avec le deuxième modèle (avec les intervalles)");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/deuxPetitesTrajectoires");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele2.geodesique = false;
                    AllocationDeCreneauxDeDecollageModele2.SolveProblem(vols);
                    break;
                }
                //Lire la data json de deux petites trajectoires et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)
                case 4: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de deux petites trajectoires et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/deuxPetitesTrajectoires");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele3.geodesique = false;
                    AllocationDeCreneauxDeDecollageModele3.SolveProblem(vols);
                    break;
                }
                //Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le premier modèle
                case 5: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le premier modèle");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/benchmarkCyril/man_15ac_1err_0_vol3_vol5_vol12_manoeuvre0");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1 du bas vers le haut (vol3)
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2 du bas vers le haut (vol5)
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //vol 3 du haut vers le bas (vol12)
                    vols.add(new Vol(readJson.lestrajectoires.get(2)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele1.geodesique = true;
                    AllocationDeCreneauxDeDecollageModele1.SolveProblem(vols);
                    break;
                }

                //Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le deuxième modèle (avec les intervalles)
                case 6: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le deuxième modèle (avec les intervalles)");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/benchmarkCyril/man_15ac_1err_0_vol3_vol5_vol12_manoeuvre0");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1 du bas vers le haut (vol3)
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2 du bas vers le haut (vol5)
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //vol 3 du haut vers le bas (vol12)
                    vols.add(new Vol(readJson.lestrajectoires.get(2)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele2.geodesique = true;
                    AllocationDeCreneauxDeDecollageModele2.SolveProblem(vols);


                    break;
                }
                //Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)
                case 7: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data json de trois trajectoires réelles (vol 3, vol 5, vol 12, manœuvre 0, fichier 0) et résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)");
                    System.out.println("*******************************************************************");
                    ReadJson readJson = new ReadJson();
                    readJson.lireTrajectoire("data/benchmarkCyril/man_15ac_1err_0_vol3_vol5_vol12_manoeuvre0");
                    //creation d'une liste de vols et affectation d'une trajectoire à chaque vol
                    ArrayList<Vol> vols = new ArrayList<Vol>();
                    //vol 1 du bas vers le haut (vol3)
                    vols.add(new Vol(readJson.lestrajectoires.get(0)));
                    //vol 2 du bas vers le haut (vol5)
                    vols.add(new Vol(readJson.lestrajectoires.get(1)));
                    //vol 3 du haut vers le bas (vol12)
                    vols.add(new Vol(readJson.lestrajectoires.get(2)));
                    //appel du solveur avec la liste des vols en paramètre
                    AllocationDeCreneauxDeDecollageModele3.geodesique = true;
                    AllocationDeCreneauxDeDecollageModele3.SolveProblem(vols);


                    break;
                }
                //Lire la data excel de vols/trajectoires réels
                case 8: {
                    System.out.println("*******************************************************************");
                    System.out.println("Lire la data excel de vols/trajectoires réels");
                    System.out.println("*******************************************************************");
                    ReadExcel.lireTrajectoire();
                    dataLu = true;


                    break;
                }
                //Afficher la data excel de vols/trajectoires réels
                case 9: {

                    if (dataLu == true) {
                        System.out.println("*******************************************************************");
                        System.out.println("Afficher la data excel de vols/trajectoires réels");
                        System.out.println("*******************************************************************");
                        System.out.println(ReadExcel.vols);
                    } else {
                        JOptionPane.showMessageDialog(null, "Il faut d'abord lire la data excel");
                    }


                    break;
                }
                //Résoudre avec le premier modèle
                case 10: {
                    if (dataLu == true) {
                        System.out.println("*******************************************************************");
                        System.out.println("Résoudre avec le premier modèle ");
                        System.out.println("*******************************************************************");
                        AllocationDeCreneauxDeDecollageModele1.geodesique = true;
                        AllocationDeCreneauxDeDecollageModele1.SolveProblem(ReadExcel.vols);
                    } else {
                        JOptionPane.showMessageDialog(null, "Il faut d'abord lire la data excel");
                    }

                    break;
                }
                //Résoudre avec le deuxième modèle (avec les intervalles)
                case 11: {
                    if (dataLu == true) {
                        System.out.println("*******************************************************************");
                        System.out.println("Résoudre avec le deuxième modèle (avec les intervalles) ");
                        System.out.println("*******************************************************************");
                        AllocationDeCreneauxDeDecollageModele2.geodesique = true;
                        AllocationDeCreneauxDeDecollageModele2.SolveProblem(ReadExcel.vols);
                    } else {
                        JOptionPane.showMessageDialog(null, "Il faut d'abord lire la data excel");
                    }

                    break;
                }
                //Résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)
                case 12: {
                    if (dataLu == true) {
                        System.out.println("*******************************************************************");
                        System.out.println("Résoudre avec le troisième modèle (avec les intervalles et la contrainte globale)");
                        System.out.println("*******************************************************************");
                        AllocationDeCreneauxDeDecollageModele3.geodesique = true;
                        AllocationDeCreneauxDeDecollageModele3.SolveProblem(ReadExcel.vols);
                    } else {
                        JOptionPane.showMessageDialog(null, "Il faut d'abord lire la data excel");
                    }

                    break;
                }



                default:
                    JOptionPane.showMessageDialog(null, "Veuillez saisir une valeur numérique entre 1 et 12");
                    break;
            }
        }


    }


}


