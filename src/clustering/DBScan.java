package clustering;

import java.awt.*;
import java.util.*;

/**
 * Classe DBSCAN qui fait le clustering sur la POSITION (x, y) des pixels.
 */
public class DBScan implements AlgoClustering {

    private int eps;     // Distance maximale (en pixels) pour être voisin
    private int minPts;  // Nombre minimum de voisins pour être un core point
    private Map<String, ArrayList<Integer>> grid;
    private int cellSize;

    public DBScan(int e, int minP) {
        this.eps = e;
        this.minPts = minP;
    }

    private void buildGrid(ArrayList<ArrayList<Integer>> list_carac) {
        grid = new HashMap<>();
        cellSize = eps;

        for (int i = 0; i < list_carac.size(); i++) {
            int x = list_carac.get(i).get(0);
            int y = list_carac.get(i).get(1);
            String key = (x / cellSize) + ":" + (y / cellSize);

            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
    }

    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac) {
        buildGrid(list_carac);
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
        ArrayList<Integer> neighbors = new ArrayList<>();

        ArrayList<Integer> coord1 = list_carac.get(index_point);
        int x = coord1.get(0);
        int y = coord1.get(1);

        int cellX = x / cellSize;
        int cellY = y / cellSize;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                String key = (cellX + dx) + ":" + (cellY + dy);
                ArrayList<Integer> cellPoints = grid.get(key);
                if (cellPoints == null) continue;

                for (int i : cellPoints) {
                    if (i == index_point) continue;

                    ArrayList<Integer> coord2 = list_carac.get(i);
                    double dist = Math.hypot(x - coord2.get(0), y - coord2.get(1));
                    if (dist <= eps) {
                        neighbors.add(i);
                    }
                }
            }
        }
        return neighbors;
    }


    /**
     * Regroupe les voisins dans le cluster si la condition est remplie
     */
    private void expandCluster(int index_point, ArrayList<Integer> ptsVoisin, boolean[] obj_traite,
                               int numCluster, ArrayList<Integer> list_num_cluster, ArrayList<ArrayList<Integer>> list_carac) {

        Set<Integer> ptsVoisinSet = new HashSet<>(ptsVoisin);

        list_num_cluster.set(index_point, numCluster);

        for (int i = 0; i < ptsVoisin.size(); i++) {
            int index_voisin = ptsVoisin.get(i);

            if (!obj_traite[index_voisin]) {
                obj_traite[index_voisin] = true;
                ArrayList<Integer> voisins2 = regionQuery(index_voisin, list_carac);

                if (voisins2.size() >= minPts) {
                    for (int v : voisins2) {
                        if (!ptsVoisinSet.contains(v)) {
                            ptsVoisin.add(v);
                            ptsVoisinSet.add(v);
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