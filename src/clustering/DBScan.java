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

    private ArrayList<int[]> caracteristics; // Liste des caractéristiques des objets

    public DBScan(int e, int minP) {
        this.eps = e;
        this.minPts = minP;
    }

    private void buildGrid() {
        grid = new HashMap<>();
        cellSize = eps;

        for (int i = 0; i < caracteristics.size(); i++) {
            int x = caracteristics.get(i)[0];
            int y = caracteristics.get(i)[1];
            String key = (x / cellSize) + ":" + (y / cellSize);

            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }
    }

    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<int[]> list_carac) {
        this.caracteristics = list_carac;

        buildGrid();

        int C = 0;
        int nbPoints = list_carac.size();
        boolean[] obj_traite = new boolean[nbPoints];
        ArrayList<Integer> list_num_cluster = new ArrayList<>(Collections.nCopies(nbPoints, -1));

        for (int n = 0; n < nbPoints; n++) {

            if (!obj_traite[n]) {

                obj_traite[n] = true;
                ArrayList<Integer> voisins = regionQuery(n);

                if (voisins.size() >= minPts) {
                    C++;
                    expandCluster(n, voisins, obj_traite, C, list_num_cluster);
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
    private ArrayList<Integer> regionQuery(int index_point) {
        ArrayList<Integer> neighbors = new ArrayList<>();

        int[] coord1 = caracteristics.get(index_point);
        int x = coord1[0];
        int y = coord1[1];

        int cellX = x / cellSize;
        int cellY = y / cellSize;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                String key = (cellX + dx) + ":" + (cellY + dy);
                ArrayList<Integer> cellPoints = grid.get(key);
                if (cellPoints == null) continue;

                for (int i : cellPoints) {
                    if (i == index_point) continue;

                    int[] coord2 = caracteristics.get(i);
                    double dist = Math.hypot(x - coord2[0], y - coord2[1]);
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
                               int numCluster, ArrayList<Integer> list_num_cluster) {

        Set<Integer> ptsVoisinSet = new HashSet<>(ptsVoisin);

        list_num_cluster.set(index_point, numCluster);

        for (int i = 0; i < ptsVoisin.size(); i++) {
            int index_voisin = ptsVoisin.get(i);

            if (!obj_traite[index_voisin]) {
                obj_traite[index_voisin] = true;
                ArrayList<Integer> voisins2 = regionQuery(index_voisin);

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