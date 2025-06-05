package clustering;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe DBSCAN qui fait le clustering sur la POSITION (x, y) des pixels.
 */
public class DBSCANPosition implements AlgoClustering {

    private int eps;     // Distance maximale (en pixels) pour être voisin
    private int minPts;  // Nombre minimum de voisins pour être un core point

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

            if (!obj_traite[n]) {

                obj_traite[n] = true;
                ArrayList<Integer> voisins = regionQuery(n, list_carac);

                if (voisins.size() >= minPts) {
                    C++;
                    expandCluster(n, voisins, obj_traite, C, list_num_cluster, list_carac);
                } else {
                    list_num_cluster.set(n, 0); // Bruit
                }
            }
        }

        return list_num_cluster;
    }

    /**
     * Calcule la liste des voisins proches spatialement (en fonction de x et y)
     */
    private ArrayList<Integer> regionQuery(int index_point, ArrayList<ArrayList<Integer>> list_carac) {

        ArrayList<Integer> V = new ArrayList<>();
        ArrayList<Integer> coord1 = list_carac.get(index_point);
        int x1 = coord1.get(0);
        int y1 = coord1.get(1);

        for (int i = 0; i < list_carac.size(); i++) {
            if (i != index_point) {
                ArrayList<Integer> coord2 = list_carac.get(i);
                int x2 = coord2.get(0);
                int y2 = coord2.get(1);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                if (distance <= eps) {
                    V.add(i);
                }
            }
        }

        return V;
    }

    /**
     * Regroupe les voisins dans le cluster si la condition est remplie
     */
    private void expandCluster(int index_point, ArrayList<Integer> ptsVoisin, boolean[] obj_traite,
                               int numCluster, ArrayList<Integer> list_num_cluster, ArrayList<ArrayList<Integer>> list_carac) {

        list_num_cluster.set(index_point, numCluster);

        for (int i = 0; i < ptsVoisin.size(); i++) {
            int index_voisin = ptsVoisin.get(i);

            if (!obj_traite[index_voisin]) {
                obj_traite[index_voisin] = true;
                ArrayList<Integer> voisins2 = regionQuery(index_voisin, list_carac);

                if (voisins2.size() >= minPts) {
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