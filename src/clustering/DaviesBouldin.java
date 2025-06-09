package clustering;

import normes.NormeCIELAB;
import normes.NormeCouleurs;

import java.awt.*;
import java.util.ArrayList;

public class DaviesBouldin {
    private ArrayList<Integer> clusters; // liste des indices de clusters pour chaque point
    private ArrayList<Color> points;    // liste des couleurs pour chaque point
    private ArrayList<Color> centroids; // centroïdes de chaque cluster

    private final NormeCouleurs norme = new NormeCIELAB(); // norme utilisée pour le calcul des distances


    public DaviesBouldin(ArrayList<Integer> clusters, ArrayList<Color> points, ArrayList<Color> centroids) {
        this.clusters = clusters;
        this.points = points;
        this.centroids = centroids;
    }

    /**
     * Calcul la dispersion de tous les clusters
     * @return Un tableau de dispersion pour chaque cluster
     */
    private double[] calculerDispersion() {
        double[] S = new double[centroids.size()];
        int[] clusterSizes = new int[centroids.size()];

        // On parcourt la liste des points associés à chaque cluster
        for (int i = 0; i < clusters.size(); i++) {
            int clusterIndex = clusters.get(i);
            Color pointColor = points.get(i);

            // Calcule la distance entre le point et le centroïde du cluster
            double distance = norme.distanceCouleur(pointColor, centroids.get(clusterIndex));

            S[clusterIndex] += distance * distance * distance; // On accumule la distance au cube pour ne pas privilégier les points éloignés

            clusterSizes[clusterIndex]++; // On compte le nombre de points dans le cluster
        }

        // On normalise la dispersion par le nombre de points dans chaque cluster
        for (int i = 0; i < S.length; i++) {
            if (clusterSizes[i] > 0) {
                S[i] = S[i] / clusterSizes[i]; // Dispersion moyenne
            } else {
                S[i] = 0; // Si le cluster est vide, on met la dispersion à 0
            }
        }

        return S;
    }

    public double calculerDaviesBouldin() {
        int k = centroids.size();
        double[] S = calculerDispersion();

        double dbiSum = 0;

        for (int i = 0; i < k; i++) {
            double maxRij = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    double Mij = norme.distanceCouleur(centroids.get(i), centroids.get(j));
                    if (Mij == 0 || Double.isNaN(Mij)) continue;
                    double Rij = (S[i] + S[j]) / Mij;
                    if (!Double.isNaN(Rij) && !Double.isInfinite(Rij)) {
                        maxRij = Math.max(maxRij, Rij);
                    }
                }
            }

            if (maxRij != Double.NEGATIVE_INFINITY) {
                dbiSum += maxRij;
            } else {
                k--; // on n'incrémente pas ce cluster car aucune comparaison valide
            }
        }

        return (k == 0) ? Double.POSITIVE_INFINITY : dbiSum / k;

    }
}
