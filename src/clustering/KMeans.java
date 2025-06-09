package clustering;

import java.awt.*;
import java.util.*;
import java.util.List;

import normes.NormeCIELAB;

public class KMeans implements AlgoClustering{

    /**
     * Attibuts :
     * - k : nombre maximal de clusters
     * - ArrayList<Color> centroids : liste des centroïdes (R, G, B)
     * - ArrayList<Color> caracteristics : liste des caractéristiques (R, G, B) de chaque objet
     * - Map<Color, Integer> caracteristicsMap : map des caractéristiques pour un accès rapide (nombre d'objets par caractéristique)
     * - List<Set<Color>> groupes : liste des groupes d'objets associés à chaque centroïde
     */

    // Attributs fixe (même en changeant le nombre de clusters)
    private int k; // nombre maximal de clusters
    private ArrayList<Color> caracteristics; // liste des caractéristiques (R, G, B) de chaque objet
    private Map<Color, Integer> colorMap; // map des caractéristiques

    private ArrayList<Color> centroids; // centroïdes [r,g,b]
    private List<Set<Color>> groupes; // liste des groupes d'objets associés à chaque centroïde



    private final NormeCIELAB norme = new NormeCIELAB(); // norme utilisée pour le calcul des distances


    /**
     * Constructeur de la classe KMeans2
     * @param k Nombre maximal de clusters
     */
    public KMeans(int k) {
        this.k = k;
        this.centroids = new ArrayList<>();
        this.caracteristics = new ArrayList<>();
    }


    /**
     * Méthode qui créer la map des caractéristiques
     */
    public void create_caracteristics_map(ArrayList<int[]> list_carac) {
        // On crée une map pour stocker les couleurs et leur fréquence
        this.colorMap = new HashMap<>();

        // On parcourt la liste des caractéristiques
        // On créer l'objet Color pour chaque triplet (R, G, B)
        for (int[] carac : list_carac) {
            Color color = new Color(carac[0], carac[1], carac[2]);
            // On ajoute la couleur à la liste des caractéristiques
            caracteristics.add(color);

            // On met à jour la map des caractéristiques
            if (colorMap.containsKey(color)) {
                // Si la couleur est déjà présente, on incrémente son compteur
                colorMap.put(color, colorMap.get(color) + 1);
            } else {
                // Sinon, on l'ajoute avec un compteur de 1
                colorMap.put(color, 1);
            }
        }
    }


    /**
     * Méthode qui permet de calculer des clusters à partir d'une liste de caractéristiques pour chaque objet
     * @param list_carac Liste d'objet représenté par une liste de caractéristiques (R, G, B)
     * @return Liste des indices des clusters pour chaque objet
     */
    public ArrayList<Integer> calculate_clusters(ArrayList<int[]> list_carac) {
        // On crée la map des caractéristiques
        this.create_caracteristics_map(list_carac);

        // On initialise les meileures valeurs
        ArrayList<Integer> bestClusters = new ArrayList<>();
        double bestDaviesBouldin = Double.MAX_VALUE;

        for (int nbCluster = 3; nbCluster <= k; nbCluster++) {
            // on initialise les centroïdes
            this.init_centroids(nbCluster);

            // On associe les objets à leurs centroïdes respectifs
            this.groupes = assign_objects_to_centroids();

            int nbIterations = 0;

            // On itère jusqu'à ce que les centroïdes ne changent plus ou jusqu'à 100 itérations
            while (calculate_new_centroids() && ++nbIterations < 100) {
                this.groupes = assign_objects_to_centroids();
            }

            // On récupère les clusters
            ArrayList<Integer> clusters = get_clusters();

            // On calcule le Davies-Bouldin pour évaluer la qualité du clustering
            DaviesBouldin db = new DaviesBouldin(clusters, caracteristics, centroids);
            double dbi = db.calculerDaviesBouldin();
            System.out.println("Nombre de clusters : " + nbCluster + ", Davies-Bouldin : " + dbi);

            // Si le Davies-Bouldin est meilleur que le meilleur précédent, on met à jour les meilleurs clusters
            if (dbi < bestDaviesBouldin) {
                bestDaviesBouldin = dbi;
                bestClusters = clusters; // On copie la liste des clusters
            }

        }

        return bestClusters; // Retourne la liste des clusters
    }


    /**
     * Méthode qui initialise les centroïdes
     * @param nb Nombre de centroïdes à initialiser
     */
    public void init_centroids(int nb) {
        // On initialise les centroïdes avec des couleurs aléatoires
        this.centroids.clear();
        for (int i = 0; i < nb; i++) {
            // On génère une couleur aléatoire
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            Color color = new Color(r, g, b);

            // On ajoute la couleur à la liste des centroïdes
            centroids.add(color);
        }
    }


    /**
     * Méthode qui permet associes les objets à leurs centroides respectifs
     * @return Liste des groupes d'objets associés à chaque centroïde
     */
    public List<Set<Color>> assign_objects_to_centroids() {
        // On crée une liste de groupes de couleurs associés aux centroïdes
        List<Set<Color>> groupes = new ArrayList<>();

        // On initialise les groupes
        for (int i = 0; i < centroids.size(); i++) {
            groupes.add(new HashSet<>());
        }

        // On parcourt la map des caractéristiques pour associer les couleurs à leurs centroïdes respectifs
        for (Color color : colorMap.keySet()) {
            int closestCentroidIndex = find_closest_centroid(color);

            if (closestCentroidIndex < 0 || closestCentroidIndex >= groupes.size()) {
                System.err.println("Erreur : Index de centroïde invalide " + closestCentroidIndex);
                continue; // On ignore les couleurs qui n'ont pas de centroïde valide
            }
            groupes.get(closestCentroidIndex).add(color); // Ajouter la couleur au groupe du centroïde le plus proche
        }

        return groupes;
    }


    /**
     * Méthode qui permet de trouver l'index du centroïde le plus proche d'un objet
     * @param color Caractéristiques de l'objet (R, G, B)
     * @return Index du centroïde le plus proche
     */
    public int find_closest_centroid(Color color) {
        // on initialise l'index du centroïde le plus proche et la distance minimale
        int closestIndex = -1;
        double minDistance = Double.MAX_VALUE;

        // On parcourt les centroïdes pour trouver le plus proche
        for (int i = 0; i < centroids.size(); i++) {
            Color centroid = centroids.get(i);

            // Calcul de la distance entre l'objet et le centroïde
            double distance = norme.distanceCouleur(color, centroid);

            // Si la distance est plus petite que la distance minimale, on met à jour l'index et la distance minimale
            if (distance < minDistance || minDistance == Double.MAX_VALUE) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }



    /**
     * Méthode qui permet de calculer les nouveaux centroïdes à partir des groupes d'objets
     * @return true si les centroïdes ont été recalculés, false sinon
     */
    public boolean calculate_new_centroids() {
        // On initialise un booléen pour savoir si au moins un centroïde a changé
        boolean centroidsChanged = false;

        // On parcourt chaque groupe pour recalculer les centroïdes
        for (int i = 0; i < groupes.size(); i++) {
            // On récupère le groupe d'objets associés au centroïde i
            Set<Color> groupe = groupes.get(i);
            if (groupe.isEmpty()) continue;

            // On initialise le nouveau centroïde
            int[] newCentroid = new int[3]; // [R, G, B]
            int size = 0;

            // On parcourt les couleurs du groupe et on calcule la moyenne
            // On utilise la map des caractéristiques pour savoir combien d'objets sont associés à chaque couleur
            for (Color color : groupe) {
                int count = colorMap.get(color);
                newCentroid[0] += color.getRed() * count;
                newCentroid[1] += color.getGreen() * count;
                newCentroid[2] += color.getBlue() * count;
                size += count; // On compte le nombre total d'objets pour la moyenne
            }

            // Moyenne des valeurs
            newCentroid[0] /= size;
            newCentroid[1] /= size;
            newCentroid[2] /= size;

            // Vérification si le centroïde a changé
            Color newColor = new Color(newCentroid[0], newCentroid[1], newCentroid[2]);
            if (!centroids.get(i).equals(newColor)) {
                centroids.set(i, newColor);
                centroidsChanged = true;
            }
        }

        return centroidsChanged;
    }


    /**
     * Méthode qui créer et retourne la liste des clusters
     */
    public ArrayList<Integer> get_clusters() {
        // On crée la liste de clustering
        ArrayList<Integer> clustering = new ArrayList<>(Collections.nCopies(caracteristics.size(), -1));

        // On parcourt les groupes de couleurs pour remplir la liste de clustering en fonction de l'index du centroïde
        // On vérifie toutes les valeurs de la liste des caractéristiques pour l'associer à la couleur qui lui correspond
        // On doit trouver le groupe dans lequel la couleur est présente
        for (int i = 0; i < caracteristics.size(); i++) {
            Color c = caracteristics.get(i); // On récupère la couleur de l'objet
            for (int j = 0; j < groupes.size(); j++) {
                Set<Color> groupe = groupes.get(j);
                if (groupe.contains(c)) {
                    clustering.set(i, j); // On associe l'index du centroïde au point
                    break; // On sort de la boucle dès qu'on a trouvé le groupe
                }
            }
        }
        return clustering;
    }


}
