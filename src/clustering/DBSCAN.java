package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Classe qui représente l'algorithme de clustering DBSCAN
 */
public class DBSCAN implements AlgoClustering {


    // Attributs
    private int eps; // Taille du rayon de voisinage
    private int minPts; // Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"



    /**
     * Constructeur de la classe pour initialiser les paramètres
     * @param e Taille du rayon de voisinage
     * @param minP Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"
     */
    public DBSCAN(int e, int minP) {
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



    /**
     * Méthode qui calcul et retourne la liste des points situés dans la zone de voisinage
     * @param index_point Index du point de référence duquel on part pour rechercher ses voisins
     * @param list_carac Liste de point représenté par une liste de caractéristiques (R, G, B)
     * @return Liste de points dans la zone de voisinage
     */
    private ArrayList<Integer> regionQuery(int index_point, ArrayList<ArrayList<Integer>> list_carac) {

        NormeCIELAB norme = new NormeCIELAB();
        ArrayList<Integer> V = new ArrayList<>();

        Color c1 = new Color(list_carac.get(index_point).get(0), list_carac.get(index_point).get(1), list_carac.get(index_point).get(2));

        for (int i = 0; i < list_carac.size(); i++) {
            Color c2 = new Color(list_carac.get(i).get(0), list_carac.get(i).get(1), list_carac.get(i).get(2));
            if (i != index_point && norme.distanceCouleur(c1, c2) <= eps) {
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
