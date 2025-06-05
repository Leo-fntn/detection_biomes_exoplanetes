package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;

public class DaviesBouldin {
    private ArrayList<ArrayList<Integer>> clusters;  // liste des indices de points pour chaque cluster
    private ArrayList<ArrayList<Integer>> points;    // liste des points RGB
    private ArrayList<Color> centroids;              // centroïdes de chaque cluster

    public DaviesBouldin(ArrayList<ArrayList<Integer>> clusters, ArrayList<ArrayList<Integer>> points, ArrayList<Color> centroids) {
        this.clusters = clusters;
        this.points = points;
        this.centroids = centroids;
    }

    private double calculerDispersion(int clusterIndex) {
        NormeCIELAB norme = new NormeCIELAB();
        ArrayList<Integer> cluster = clusters.get(clusterIndex);
        Color centroid = centroids.get(clusterIndex);
        double sum = 0;

        for (Integer pointIndex : cluster) {
            ArrayList<Integer> rgb = points.get(pointIndex);
            Color c = new Color(rgb.get(0), rgb.get(1), rgb.get(2));
            double dist = norme.distanceCouleur(c, centroid);
            if (Double.isNaN(dist)) {
                continue;
            } else {
                sum += dist;
            }
        }


        return cluster.isEmpty() ? 0 : sum / cluster.size();
    }

    public double calculerDaviesBouldin() {
        NormeCIELAB norme = new NormeCIELAB();

        int k = clusters.size();
        double[] S = new double[k];

        // Calcule la dispersion pour chaque cluster
        for (int i = 0; i < k; i++) {
            S[i] = calculerDispersion(i);
        }

        double dbiSum = 0;

        for (int i = 0; i < k; i++) {
            double maxRij = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    double Mij = norme.distanceCouleur(centroids.get(i), centroids.get(j));
                    if (Mij == 0 || Double.isNaN(Mij)) continue; // Évite division par 0
                    double Rij = (S[i] + S[j]) / Mij;
                    maxRij = Math.max(maxRij, Rij);
                }
            }

            dbiSum += maxRij;
        }

        return dbiSum / k;
    }
}
