package clustering;

import normes.NormeCIELAB;

import java.awt.*;
import java.util.ArrayList;

public class KMeans implements AlgoClustering{

    private int k; // Nombre de clusters
    private ArrayList<Color> centroids; // Liste des centroïdes

    public KMeans(int k){
        this.k = k;
    }

    @Override
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac) {

        NormeCIELAB norme = new NormeCIELAB();

        init_centroids(list_carac);

        boolean maj = true;
        // On initialise les groupes
        ArrayList<ArrayList<Integer>> groupes = new ArrayList<>();

        while(maj){
            maj = false;

            // On initialise les groupes
            groupes = new ArrayList<>();

            // On construit les groupes avec les centroïdes
            for (int i = 0; i < k; i++) {
                groupes.add(new ArrayList<>());
            }
            // On parcourt chaque centroïde pour vérifier de quel centroïde chaque point est le plus proche
            for (ArrayList<Integer> carac : list_carac) {
                int index = 0;
                double minDist = Double.MAX_VALUE;

                // On parcourt les centroïdes pour trouver le plus proche
                for (int i = 0; i < centroids.size(); i++) {
                    double dist = norme.distanceCouleur(centroids.get(i), new Color(carac.get(0), carac.get(1), carac.get(2)));
                    if (dist < minDist) {
                        minDist = dist;
                        index = i;
                    }
                }

                // On ajoute l'index du point dans le groupe correspondant
                groupes.get(index).add(list_carac.indexOf(carac));
            }

            // On met à jour les centroïdes si il le faut
            for (int i = 0; i < k; i++) {
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
                if (!centroids.get(i).equals(newCentroid)) {
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

    public void init_centroids(ArrayList<ArrayList<Integer>> list_carac) {
        // On initialise les centroïdes en prenant les k premières couleurs de la liste
        centroids = new ArrayList<>();
        for (int i = 0; i < k && i < list_carac.size(); i++) {
            ArrayList<Integer> carac = list_carac.get(i);
            Color color = new Color(carac.get(0), carac.get(1), carac.get(2));
            centroids.add(color);
        }
    }
}
