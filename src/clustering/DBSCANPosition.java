package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe qui représente l'algorithme de clustering DBSCAN
 */
public class DBSCANPosition implements AlgoClustering {


    // Attributs
    private int eps; // Taille du rayon de voisinage
    private int minPts; // Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"



    /**
     * Constructeur de la classe pour initialiser les paramètres
     * @param e Taille du rayon de voisinage
     * @param minP Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"
     */
    public DBSCANPosition(int e, int minP) {
        this.eps = e;
        this.minPts = minP;
    }




    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac) {

        int C = 0;
        int nbPoints = list_carac.size();
        boolean[] obj_traite = new boolean[nbPoints];
        ArrayList<Integer> list_num_cluster = new ArrayList<>(Collections.nCopies(nbPoints, -1));

        for (int n = 0; n < nbPoints; n++) {

            System.out.println("Pixel -> " + n);

            if (!obj_traite[n]) {

                obj_traite[n] = true;
                ArrayList<Integer> voisins = regionQuery(n, list_carac);

                if (voisins.size() >= minPts) {
                    C++;
                    expandCluster(n, voisins, obj_traite, C, list_num_cluster, list_carac);
                } else {
                    list_num_cluster.set(n, 0); // 0 pour bruit
                }

            }
        }

        return list_num_cluster;

    }




    private int distance(ArrayList<Integer> coo1, ArrayList<Integer> coo2) {

        return (int) Math.sqrt(Math.pow(coo2.get(0) - coo1.get(0), 2) + Math.pow(coo2.get(1) - coo1.get(1), 2));

    }


    /**
     * Méthode qui calcul et retourne la liste des points situés dans la zone de voisinage
     * @param index_point Index du point de référence duquel on part pour rechercher ses voisins
     * @param list_carac Liste de point représenté par une liste de caractéristiques (R, G, B)
     * @return Liste de points dans la zone de voisinage
     */
    private ArrayList<Integer> regionQuery(int index_point, ArrayList<ArrayList<Integer>> list_carac) {

        ArrayList<Integer> V = new ArrayList<>();
        ArrayList<Integer> coo1 = list_carac.get(index_point);

        for (int i = 0; i < list_carac.size(); i++) {

            ArrayList<Integer> coo2 = list_carac.get(i);
            if (i != index_point && this.distance(coo1, coo2) <= eps) {
                V.add(i);
            }
        }

        return V;
    }



    /**
     * Méthode qui ajoute au cluster les points qui valide toutes les conditions pour y rentrer
     * @param index_point Index du point initial du nouveau cluster
     * @param ptsVoisin Liste des points voisins au point initiale
     * @param obj_traite Liste indiquant quel objet (point) à déjà été traité
     * @param numCluster Numéro du nouveau cluster
     * @param list_num_cluster Liste qui contient le numéro de cluster pour chaque point
     * @param list_carac Liste de point représenté par une liste de caractéristiques (R, G, B)
     */
    private void expandCluster(int index_point, ArrayList<Integer> ptsVoisin, boolean[] obj_traite,
                               int numCluster, ArrayList<Integer> list_num_cluster, ArrayList<ArrayList<Integer>> list_carac) {

        list_num_cluster.set(index_point, numCluster);

        for (int i = 0; i < ptsVoisin.size(); i++) {

            int index_voisin = ptsVoisin.get(i);

            if (!obj_traite[index_voisin]) {

                obj_traite[index_voisin] = true;
                ArrayList<Integer> voisins2 = regionQuery(index_voisin, list_carac);

                if (voisins2.size() > minPts) {

                    for (int v : voisins2) {
                        if (!ptsVoisin.contains(v)) {
                            ptsVoisin.add(v);
                        }
                    }

                }
            }

            if (list_num_cluster.get(index_voisin) == -1) {
                list_num_cluster.set(index_voisin, numCluster);
            }
        }
    }


}
