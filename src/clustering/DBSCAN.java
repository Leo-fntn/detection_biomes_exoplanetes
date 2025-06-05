package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Classe qui représente l'algorithme de clustering DBSCAN
 */
public class DBSCAN implements AlgoClustering {


    // Attributs
    private int eps; // Taille du rayon de voisinage
    private int minPts; // Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"
    private HashMap<ArrayList<Integer>[], Double> list_combinaisons_distance = new HashMap<>();




    /**
     * Constructeur de la classe pour initialiser les paramètres
     * @param e Taille du rayon de voisinage
     * @param minP Nombre minimum de point dans le rayon de voisinage d'un point pour le qualifié de "core point"
     */
    public DBSCAN(int e, int minP) {
        this.eps = e;
        this.minPts = minP;
    }


    /**
     * Méthode qui calcul la distance entre chaque couleur de l'image
     * @param list_carac
     */
    public void getAllDist(ArrayList<ArrayList<Integer>> list_carac) {

        // Initialisation des variables
        HashSet<ArrayList<Integer>> list_colors = new HashSet<>();

        // On récupère chaque couleur différente
        list_colors.addAll(list_carac);

        System.out.println("Nombre de couleur différente trouvée : " + list_colors.size());

        NormeCIELAB norme = new NormeCIELAB();

        int i = 0;

        System.out.println("Début du calcul de la distance entre chaque combinaison de couleur possible...");
        // On parcour chaque couleur différente
        for (ArrayList<Integer> color1 : list_colors) {
            for (ArrayList<Integer> color2 : list_colors) {

                // On récupère la couleur
                // On transforme les données R, G, B des points en objet Color
                Color c1 = new Color(color1.get(0), color1.get(1), color1.get(2));
                Color c2 = new Color(color2.get(0), color2.get(1), color2.get(2));

                // On calcul la distance entre ces 2 couleurs
                double dist = norme.distanceCouleur(c1, c2);

                // On l'ajoute à la Map
                this.list_combinaisons_distance.put(new ArrayList[]{color1, color2}, dist);

            }
        }

        System.out.println(this.list_combinaisons_distance);

    }



    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac) {

        // Initialisation de toutes les distances possibles entre toutes les couleurs
        this.getAllDist(list_carac);

        // Initialisation des variables
        int num_cluster = 0;
        boolean[] obj_traite = new boolean[list_carac.size()];

        // Initialisation de la liste contenant pour chaque point, son numéro de cluster
        ArrayList<Integer> list_num_cluster = new ArrayList<>(list_carac.size());
        for (int i = 0; i < list_carac.size(); i++) {
            list_num_cluster.add(-1);
        }

        // On parcour chaque point
        for (int i = 0; i < list_carac.size(); i++) {

            int nb_false = 0;
            for (boolean b : obj_traite) {
                if (!b) { nb_false++; }
            }
            System.out.println("Pixel -> " + i + "/" + nb_false);

            // Si le point n'a pas déjà été traité, on le marque comme traité
            if (!obj_traite[i]) { obj_traite[i] = true; }

            // On récupère la liste des points qui sont dans le rayon de voisinage
            ArrayList<ArrayList<Integer>> pts_voisin = this.regionQuery(i, list_carac);

            // Si il y a plus de points voisin que le nombre minimal défini
            if (pts_voisin.size() >= this.minPts) {

                // On créé un nouveau cluster et on ajoute à l'intérieur tous ses voisins qui peuvent rentrer dans ce cluster
                num_cluster += 1;
                this.expandCluster(i, pts_voisin, obj_traite, num_cluster, list_num_cluster, list_carac);

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
     * @param index_point Index du point de référence duquel on part pour rechercher ses voisins
     * @param list_carac Liste de point représenté par une liste de caractéristiques (R, G, B)
     * @return Liste de points dans la zone de voisinage
     */
    private ArrayList<ArrayList<Integer>> regionQuery(int index_point, ArrayList<ArrayList<Integer>> list_carac) {

        // Initialisation des variables
        NormeCIELAB norme = new NormeCIELAB();
        ArrayList<Integer> point = list_carac.get(index_point);

        // On créé et remplie la liste avec des null
        ArrayList<ArrayList<Integer>> list_points_voisins = new ArrayList<>(list_carac.size());
        for (int i = 0; i < list_carac.size(); i++) {
            list_points_voisins.add(null);
        }




        // On parcour chaque point
        for (ArrayList<Integer> p : list_carac) {


            // System.out.println("Pixel (regionQuery) -> " + p);

            // On transforme les données R, G, B des points en objet Color
            Color p_color = new Color(p.get(0), p.get(1), p.get(2));
            Color point_color = new Color(point.get(0), point.get(1), point.get(2));

            // Si on ne compare pas le même point et que la distance entre les deux couleurs est inférieur à la taille du rayon
            if (p != point && norme.distanceCouleur(p_color, point_color) <= this.eps) {

                // On ajoute ce point à la liste des voisins
                list_points_voisins.set(index_point, p);

            }

        }

        // On retourne la liste des voisins
        return list_points_voisins;

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
    private void expandCluster(int index_point, ArrayList<ArrayList<Integer>> ptsVoisin, boolean[] obj_traite, int numCluster, ArrayList<Integer> list_num_cluster, ArrayList<ArrayList<Integer>> list_carac) {

        // On ajoute le point initial au nouveau cluster
        list_num_cluster.set(index_point, numCluster);

        for (int i = 0; i < ptsVoisin.size(); i++) {

            // On récupère le point
            ArrayList<Integer> p = ptsVoisin.get(i);

            // Si le point n'est pas null
            if (p != null) {

                // System.out.println("Pixel (expandCluster) -> " + i);

                // Si le point n'a pas été traité
                if (!obj_traite[i]) {

                    // On marque le point comme traité
                    obj_traite[i] = true;

                    // On récupère la liste des points situé dans sa zone de voisinage
                    ArrayList<ArrayList<Integer>> list_voisins_p = this.regionQuery(i, list_carac);

                    // Si la taille de cette liste est supérieur au minimum de point demandé
                    if (list_voisins_p.size() > this.minPts) {

                        // On concatène la liste des voisins des points du point que l'on regarde à la liste des points voisins du point initial
                        ptsVoisin.addAll(list_voisins_p);

                    }


                }

                // Si ce point n'est dans aucun cluster
                if (list_num_cluster.get(i) == -1) {

                    // On ajoute ce point dans le cluster
                    list_num_cluster.set(i, numCluster);

                }
            }

        }

    }

}
