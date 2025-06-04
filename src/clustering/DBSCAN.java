package clustering;

import java.util.ArrayList;

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

        // Initialisation des variables
        ArrayList<Integer> list_num_cluster = new ArrayList<>(list_carac.size());
        int num_cluster = 0;
        boolean[] obj_traite = new boolean[list_carac.size()];

        // On parcour chaque point
        for (int i = 0; i < list_carac.size(); i++) {

            // On récupère le point que l'on regarde (ses caractéristiques)
            ArrayList<Integer> point = list_carac.get(i);

            // Si le point n'a pas déjà été traité, on le marque comme traité
            if (!obj_traite[i]) { obj_traite[i] = true; }

            // On récupère la liste des points qui sont dans le rayon de voisinage
            ArrayList<Integer> ptsVoisin = this.regionQuery(point);

            // Si il y a plus de points voisin que le nombre minimal défini
            if (ptsVoisin.size() >= this.minPts) {

                // On créé un nouveau cluster et on ajoute à l'intérieur tous ses voisins qui peuvent rentrer dans ce cluster
                num_cluster += 1;
                this.expandCluster(point, ptsVoisin, num_cluster);

            }

            // Sinon, on marque le point comme un noise point
            else {
                list_num_cluster.set(i, -1); // -1 indique que le point est un "Noise Point"
            }

        }

        // On retourne la liste des numéros de clusters
        return list_num_cluster;

    }


    /**
     * Méthode qui calcul et retourne la liste des points situés dans la zone de voisinage
     * @param point Point de référence duquel on part pour rechercher ses voisins
     * @return Liste de points dans la zone de voisinage
     */
    private ArrayList<Integer> regionQuery(ArrayList<Integer> point) {

        return new ArrayList<>(); // TODO

    }


    /**
     * Méthode qui ajoute au cluster les points qui valide toutes les conditions pour y rentrer
     * @param point Point initiale du nouveau cluster
     * @param ptsVoisin Liste des points voisins au point initiale
     * @param numCluster Numéro du nouveau cluster
     */
    private void expandCluster(ArrayList<Integer> point, ArrayList<Integer> ptsVoisin, int numCluster) {

        // TODO

    }

}
