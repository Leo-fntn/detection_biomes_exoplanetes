package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class KMeans implements AlgoClustering{

    private int k; // Nombre de clusters maximaux
    private int nbClusters; //nombre de clusters, 0 mode auto
    private ArrayList<Color> centroids; // Liste des centroïdes

    public KMeans(int k,int c){
        this.k = k;
        this.nbClusters = c;
    }

    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac) {
        double bestScore = Double.MAX_VALUE;
        ArrayList<Integer> bestClustering = null;

        if(this.nbClusters==0) {
            for (int nb = 3; nb <= k; nb++) {
                ArrayList<Integer> clustering = calculate_single_cluster(list_carac, nb);

                // Regrouper les indices par cluster
                ArrayList<ArrayList<Integer>> groupes = new ArrayList<>();
                for (int i = 0; i < k; i++) groupes.add(new ArrayList<>());
                for (int i = 0; i < clustering.size(); i++) {
                    int clusterId = clustering.get(i);
                    if (clusterId >= 0) groupes.get(clusterId).add(i);
                }

                while (groupes.size() != centroids.size()) {
                    if (groupes.size() < centroids.size()) {
                        groupes.add(new ArrayList<>()); // Ajoute un groupe vide si on a moins de groupes que de centroïdes
                    } else {
                        // Si on a plus de groupes que de centroïdes, on ne peut pas utiliser les centroïdes initiaux
                        centroids.add(new Color(0, 0, 0)); // Ajoute un centroïde vide pour éviter l'erreur
                    }
                }
                DaviesBouldin db = new DaviesBouldin(groupes, list_carac, centroids);
                double score = db.calculerDaviesBouldin();
                System.out.println("k=" + nb + " → DBI=" + score);

                if (score < bestScore) {
                    bestScore = score;
                    bestClustering = clustering;
                }
            }
        } else {
            ArrayList<Integer> clustering = calculate_single_cluster(list_carac, nbClusters);

            // Regrouper les indices par cluster
            ArrayList<ArrayList<Integer>> groupes = new ArrayList<>();
            for (int i = 0; i < nbClusters; i++) groupes.add(new ArrayList<>());

            for (int i = 0; i < clustering.size(); i++) {
                int clusterId = clustering.get(i);
                if (clusterId >= 0 && clusterId < nbClusters) {
                    groupes.get(clusterId).add(i);
                }
            }

            // Synchroniser les centroïdes (au cas où)
            while (groupes.size() != centroids.size()) {
                if (groupes.size() < centroids.size()) {
                    groupes.add(new ArrayList<>());
                } else {
                    centroids.add(new Color(0, 0, 0));
                }
            }

            DaviesBouldin db = new DaviesBouldin(groupes, list_carac, centroids);
            double score = db.calculerDaviesBouldin();
            System.out.println("k=" + nbClusters + " (fixe) → DBI=" + score);

            return clustering;
        }

        System.out.println("Meilleur score DBI: " + bestScore);
        return bestClustering;
    }

    public void init_centroids(ArrayList<ArrayList<Integer>> list_carac, int nb) {
        // On initialise les centroïdes en prenant les k premières couleurs de la liste
        centroids = new ArrayList<>();
        // on copie la liste des caractéristiques pour ne pas la modifier
        // puis on mélange pour avoir des centroïdes aléatoires
        ArrayList<ArrayList<Integer>> shuffledList = new ArrayList<>(list_carac);
        Collections.shuffle(shuffledList);

        for (int i = 0; i < nb && i < list_carac.size(); i++) {
            ArrayList<Integer> carac = list_carac.get(i);
            centroids.add(new Color(carac.get(0), carac.get(1), carac.get(2)));
        }
    }

    public ArrayList<Integer> calculate_single_cluster(ArrayList<ArrayList<Integer>> list_carac, int nb) {
        NormeCIELAB norme = new NormeCIELAB();

        init_centroids(list_carac, nb);

        boolean maj = true;
        // On initialise les groupes
        ArrayList<ArrayList<Integer>> groupes = new ArrayList<>();

        int maxIterations = 100;
        int iteration = 0;

        while(maj && iteration < maxIterations) {
            maj = false;
            iteration++;

            // On initialise les groupes
            groupes = new ArrayList<>();

            // On construit les groupes avec les centroïdes
            for (int i = 0; i < nb; i++) {
                groupes.add(new ArrayList<>());
            }
            // On parcourt chaque centroïde pour vérifier de quel centroïde chaque point est le plus proche
            for (int idx = 0; idx < list_carac.size(); idx++) {
                ArrayList<Integer> carac = list_carac.get(idx);

                Color color = new Color(carac.get(0), carac.get(1), carac.get(2));
                int bestIndex = 0;
                double bestDist = Double.MAX_VALUE;

                for (int i = 0; i < centroids.size(); i++) {
                    double dist = norme.distanceCouleur(color, centroids.get(i));
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestIndex = i;
                    }
                }
                groupes.get(bestIndex).add(idx);
            }


            // On met à jour les centroïdes si il le faut
            for (int i = 0; i < nb; i++) {
                ArrayList<Integer> groupe = groupes.get(i);
                if (groupe.isEmpty()) continue;

                // On calcule la nouvelle couleur du centroïde
                int r = 0, g = 0, b = 0;
                for (Integer index : groupe) {
                    ArrayList<Integer> carac = list_carac.get(index);
                    r += carac.get(0);
                    g += carac.get(1);
                    b += carac.get(2);
                }
                r /= groupe.size();
                g /= groupe.size();
                b /= groupe.size();

                Color newCentroid = new Color(r, g, b);

                // Si le centroïde a changé, on met à jour et on marque qu'il y a eu une mise à jour
                if (norme.distanceCouleur(centroids.get(i), newCentroid) > 1.0) {
                    centroids.set(i, newCentroid);
                    maj = true;
                }
            }
        }

        // On crée la liste des clusters
        ArrayList<Integer> list_num_cluster = new ArrayList<>(list_carac.size());
        for (int i = 0; i < list_carac.size(); i++) {
            list_num_cluster.add(-1); // -1 indique que le point n'est pas encore assigné à un cluster
        }
        // On assigne chaque point à son cluster
        for (int i = 0; i < groupes.size(); i++) {
            ArrayList<Integer> groupe = groupes.get(i);
            for (Integer index : groupe) {
                list_num_cluster.set(index, i);
            }
        }
        return list_num_cluster;
    }
}