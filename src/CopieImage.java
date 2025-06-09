import clustering.*;
import normes.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CopieImage {
    private BufferedImage image;

    public void saveImage(String imagePath) {
        try {
            // Load the image
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImageFlou(String outputPath, NormeFlou flou) {
        int width = image.getWidth();
        int height = image.getHeight();
        int taille = flou.getTaille();
        int demi = taille / 2;

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = demi; i < width - demi; i += taille) {
            for (int j = demi; j < height - demi; j += taille) {
                int[] rgbZone = flou.getRGB(image, i, j);
                for (int dx = -demi; dx <= demi; dx++) {
                    for (int dy = -demi; dy <= demi; dy++) {
                        int x = i + dx;
                        int y = j + dy;

                        if (x >= 0 && y >= 0 && x < width && y < height) {
                            int r = rgbZone[0];
                            int g = rgbZone[1];
                            int b = rgbZone[2];
                            int rgb = (r << 16) | (g << 8) | b;
                            newImage.setRGB(x, y, rgb);
                        }
                    }
                }
            }
        }

        try {
            ImageIO.write(newImage, "jpg", new File(outputPath));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    public void copyImageClosestColor(String outputPath, Palette p){
        // create and save a new image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                Color c = new Color(rgb);
                Color closestColor = p.getPlusProche(c);
                newImage.setRGB(i, j, closestColor.getRGB());
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<int[]> getData() {

        ArrayList<int[]> datas = new ArrayList<>();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                int rgb = image.getRGB(j, i);
                int[] tabColor = OutilCouleur.getTabColor(rgb);

                datas.add(tabColor);

            }
        }

        return datas;

    }

    /**
     * methode pour afficher un biome par son cluster
     * @param clusters
     * @param num
     */
    public void afficherBiome(String outputPath, ArrayList<Integer> clusters,int num){

        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int[] tab = OutilCouleur.getTabColor(rgb);
                int iter = 0;
                for(int x:tab){
                    tab[iter] = Math.round(x + (75f/100f)*(255-x));
                    iter++;
                }
                Color c = new Color(tab[0],tab[1],tab[2]);
                newImage.setRGB(i, j, c.getRGB());
            }
        }

        for(int i=0;i<clusters.size();i++){
            if(clusters.get(i)==num){
                int x = i%image.getWidth();
                int y = i/image.getWidth();
                newImage.setRGB(x,y,image.getRGB(x, y));
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * methode pour generer un cluster pour tester la methode afficherBiome
     * @param width
     * @param height
     * @param nbBiomes
     * @return
     */
    public static ArrayList<Integer> genererClusters(int width, int height, int nbBiomes) {
        int[] clusters = new int[width * height];

        // nombre de blocs (par ligne et colonne)
        int blocsParLigne = (int) Math.sqrt(nbBiomes); // ex: sqrt(100) = 10
        int blocW = width / blocsParLigne;
        int blocH = height / blocsParLigne;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int blocX = j / blocW;
                int blocY = i / blocH;
                int biomeNum = blocY * blocsParLigne + blocX;
                clusters[i * width + j] = biomeNum;
            }
        }

        Integer[] boxed = Arrays.stream(clusters).boxed().toArray(Integer[]::new);
        return new ArrayList<>(Arrays.asList(boxed));
    }


    /**
     * Récupère les positions des pixels pour un biome spécifique
     * @param clusters
     * @param biomeId
     * @return
     */
    public ArrayList<int[]> getPositionsForBiome(ArrayList<Integer> clusters, int biomeId) {
        ArrayList<int[]> positions = new ArrayList<>();

        int width = image.getWidth();

        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i) == biomeId) {
                int x = i % width;
                int y = i / width;
                int[] point = new int[2];
                point[0] = x;
                point[1] = y;
                positions.add(point);
            }
        }
        return positions;
    }


    /**
     * Affiche les écosystèmes sur l'image en utilisant les positions et les clusters
     * @param outputPath Chemin de sortie pour l'image résultante
     * @param positions Liste des positions des pixels
     * @param ecosClusters Liste des clusters d'écosystèmes
     */
    public void afficherEcosystemes(String outputPath, ArrayList<int[]> positions, ArrayList<Integer> ecosClusters) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Color[] palette = Palette.getRandomColors(100); // Génère 100 couleurs distinctes


        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int[] tab = OutilCouleur.getTabColor(rgb);
                int iter = 0;
                for(int x:tab){
                    tab[iter] = Math.round(x + (75f/100f)*(255-x));
                    iter++;
                }
                Color c = new Color(tab[0],tab[1],tab[2]);
                newImage.setRGB(i, j, c.getRGB());
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            int[] point = positions.get(i);
            int clusterId = ecosClusters.get(i);
            if (clusterId == -1) continue; // bruit

            Color c = palette[clusterId % palette.length];
            newImage.setRGB(point[0], point[1], c.getRGB());
        }

        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Associe les clusters aux biomes en fonction de la moyenne des couleurs (etiquettes)
     * @param clusters
     * @param biomes
     * @return
     */
    public Map<Integer, String> associerClustersBiomes(ArrayList<Integer> clusters, Map<String, ArrayList<Integer>> biomes) {
        int width = image.getWidth();

        // Étape 1 : moyenne des couleurs par cluster
        Map<Integer, ArrayList<Integer>> clusterToSum = new HashMap<>();
        Map<Integer, Integer> clusterCount = new HashMap<>();

        for (int i = 0; i < clusters.size(); i++) {
            int clusterId = clusters.get(i);
            if (clusterId < 0) continue; // ignore bruit ou valeurs invalides

            int rgb = image.getRGB(i % width, i / width);
            int[] color = OutilCouleur.getTabColor(rgb);

            clusterToSum.putIfAbsent(clusterId, new ArrayList<>(Arrays.asList(0, 0, 0)));
            clusterCount.put(clusterId, clusterCount.getOrDefault(clusterId, 0) + 1);

            ArrayList<Integer> sum = clusterToSum.get(clusterId);
            sum.set(0, sum.get(0) + color[0]);
            sum.set(1, sum.get(1) + color[1]);
            sum.set(2, sum.get(2) + color[2]);
        }

        // Étape 2 : calculer la moyenne
        Map<Integer, ArrayList<Integer>> clusterToAvg = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : clusterToSum.entrySet()) {
            int clusterId = entry.getKey();
            ArrayList<Integer> sum = entry.getValue();
            int count = clusterCount.get(clusterId);
            clusterToAvg.put(clusterId, new ArrayList<>(Arrays.asList(
                    sum.get(0) / count,
                    sum.get(1) / count,
                    sum.get(2) / count
            )));
        }

        // Étape 3 : associer au biome le plus proche
        Map<Integer, String> clusterToBiome = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : clusterToAvg.entrySet()) {
            int clusterId = entry.getKey();
            ArrayList<Integer> avgColor = entry.getValue();

            String bestBiome = null;
            double minDist = Double.MAX_VALUE;

            for (Map.Entry<String, ArrayList<Integer>> biome : biomes.entrySet()) {
                ArrayList<Integer> biomeColor = biome.getValue();
                double dist = Math.sqrt(
                        Math.pow(avgColor.get(0) - biomeColor.get(0), 2) +
                                Math.pow(avgColor.get(1) - biomeColor.get(1), 2) +
                                Math.pow(avgColor.get(2) - biomeColor.get(2), 2)
                );

                if (dist < minDist) {
                    minDist = dist;
                    bestBiome = biome.getKey();
                }
            }

            clusterToBiome.put(clusterId, bestBiome);
        }

        return clusterToBiome;
    }

    /**
     * Redimensionne l'image si nécessaire pour qu'elle ne dépasse pas 750x750 pixels
     * fonction qui n'est plus utilisée dans la version actuelle
     */
    public void resizeImage() {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width <= 750 && height <= 750) return; // pas besoin de redimensionner

        double ratio = Math.min(750.0 / width, 750.0 / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        this.image = resized;
    }



    public static void main(String[] args) {
        CopieImage copieImage = new CopieImage();

        // On demande le nom de l'image à l'utilisateur
        Scanner sc = new Scanner(System.in);
        System.out.print("Entrez le nom de l'image (avec extension) : ");

        String entree = sc.nextLine();

        // On construit le chemin d'entrée de l'image
        String inputPath = "cartes/"+entree;

        // On recupère le nom de l'image
        String nomImage = entree.substring(0, entree.lastIndexOf(".")).trim().replaceAll(" ", "_");
        String extension = "png";

        // On demande l'extension voulue par l'utilisateur pour l'image de sortie
        System.out.println("Souhaitez-vous une extension précise pour l'image de sortie ? (png par défaut)");
        entree = sc.nextLine();
        if (!entree.isEmpty()) {
            extension = entree;
        }

        String flouPath = "cartes2/"+nomImage+"_flou."+extension;
        String outputPathColor = "cartes2/"+nomImage+"_couleur."+extension;


        // On charge l'image
        copieImage.saveImage(inputPath);

        // On applique un flou gaussien à l'image
        FlouGauss flou = new FlouGauss(3);
        copieImage.copyImageFlou(flouPath, flou);

        // On charge l'image flou
        copieImage.saveImage(flouPath);



        // On créer la palette de couleurs
        Color[] colors = {
                new Color(0, 0, 0),       new Color(255, 255, 255), new Color(255, 0, 0),     new Color(0, 255, 0),
                new Color(0, 0, 255),     new Color(255, 255, 0),   new Color(0, 255, 255),   new Color(255, 0, 255),
                new Color(128, 0, 0),     new Color(0, 128, 0),     new Color(0, 0, 128),     new Color(128, 128, 0),
                new Color(128, 0, 128),   new Color(0, 128, 128),   new Color(192, 192, 192), new Color(128, 128, 128),
                new Color(64, 64, 64),    new Color(32, 32, 32),    new Color(224, 224, 224), new Color(160, 160, 160),
                new Color(255, 128, 0),   new Color(255, 0, 128),   new Color(128, 255, 0),   new Color(0, 255, 128),
                new Color(0, 128, 255),   new Color(128, 0, 255),   new Color(255, 128, 128), new Color(128, 255, 255),
                new Color(128, 255, 128), new Color(255, 255, 128), new Color(255, 128, 255), new Color(192, 0, 192),
                new Color(64, 0, 0),      new Color(0, 64, 0),      new Color(0, 0, 64),      new Color(64, 64, 0),
                new Color(64, 0, 64),     new Color(0, 64, 64),     new Color(255, 200, 200), new Color(200, 255, 200),
                new Color(200, 200, 255), new Color(200, 200, 0),   new Color(0, 200, 200),   new Color(200, 0, 200),
                new Color(240, 128, 128), new Color(135, 206, 235), new Color(255, 215, 0),   new Color(75, 0, 130),
                new Color(255, 20, 147),  new Color(0, 191, 255),   new Color(107, 142, 35),  new Color(210, 105, 30),
                new Color(255, 69, 0),    new Color(154, 205, 50),  new Color(123, 104, 238), new Color(199, 21, 133),
                new Color(100, 149, 237), new Color(255, 160, 122), new Color(244, 164, 96),  new Color(173, 255, 47),
                new Color(72, 61, 139),   new Color(0, 100, 0),     new Color(47, 79, 79),    new Color(0, 0, 205),
                new Color(105, 105, 105), new Color(119, 136, 153), new Color(176, 196, 222), new Color(255, 182, 193),
                new Color(255, 228, 196), new Color(255, 250, 205), new Color(250, 250, 210), new Color(245, 245, 220),
                new Color(255, 239, 213), new Color(240, 255, 240), new Color(255, 228, 225), new Color(240, 255, 255),
                new Color(255, 240, 245), new Color(255, 245, 238), new Color(245, 222, 179), new Color(255, 250, 240),
                new Color(230, 230, 250), new Color(216, 191, 216), new Color(255, 248, 220), new Color(250, 235, 215),
                new Color(220, 220, 220), new Color(210, 180, 140), new Color(188, 143, 143), new Color(205, 133, 63),
                new Color(176, 224, 230), new Color(152, 251, 152), new Color(175, 238, 238), new Color(255, 222, 173),
                new Color(144, 238, 144), new Color(221, 160, 221), new Color(127, 255, 212), new Color(238, 232, 170)
        };


        Palette p = new Palette(colors);

        // On copie l'image en utilisant la palette de couleurs pour réduire le nombre de couleurs
        copieImage.copyImageClosestColor(outputPathColor, p);
        copieImage.saveImage(outputPathColor);


        // Définition des biomes avec leurs couleurs moyennes
        Map<String,ArrayList<Integer>> biomes = new HashMap<>();
        biomes.put("Tundra",new ArrayList<>(Arrays.asList(71,70,61)));
        biomes.put("Taïga",new ArrayList<>(Arrays.asList(43,50,35)));
        biomes.put("Forêt tempérée",new ArrayList<>(Arrays.asList(59,66,43)));
        biomes.put("Forêt tropicale",new ArrayList<>(Arrays.asList(46,64,34)));
        biomes.put("Savane",new ArrayList<>(Arrays.asList(84,106,70)));
        biomes.put("Prairie",new ArrayList<>(Arrays.asList(104,95,82)));
        biomes.put("Désert",new ArrayList<>(Arrays.asList(152,140,120)));
        biomes.put("Glacier",new ArrayList<>(Arrays.asList(200,200,200)));
        biomes.put("Eau peu profonde",new ArrayList<>(Arrays.asList(49,83,100)));
        biomes.put("Eau profonde",new ArrayList<>(Arrays.asList(12,31,47)));
        biomes.put("Montagne",new ArrayList<>(Arrays.asList(210, 188, 147)));



        // On récupère les données de l'image (R, G, B) sous forme de liste
        ArrayList<int[]> list_data = copieImage.getData();

        // On crée un objet KMeans pour le clustering avec 7 clusters au maximum (configurable)
        KMeans kmeans = new KMeans(7);

        System.out.println("Début du calcul des clusters...");
        long startTime = System.currentTimeMillis();

        // On calcule les clusters à partir des données de l'image
        ArrayList<Integer> list_pixel_cluster = kmeans.calculate_clusters(list_data);

        long endTime = System.currentTimeMillis();


        System.out.println("\n### RESULTAT ### ");
        System.out.println("Temps d'exécution : " + (endTime - startTime) / 1000 + "s\n");

        // Affichage des clusters
        HashMap<Integer, Integer> allNumCluster = new HashMap<>();
        for (int numCluster : list_pixel_cluster) {

            if (!allNumCluster.containsKey(numCluster)) { allNumCluster.put(numCluster, 1); }
            else { allNumCluster.put(numCluster, allNumCluster.get(numCluster) + 1); }

        }
        System.out.println("Numéro cluster / Nombre de pixel associé");
        System.out.println(allNumCluster);


        Map<Integer,String> etiquettes = copieImage.associerClustersBiomes(list_pixel_cluster,biomes);
        System.out.println(etiquettes);

        startTime = System.currentTimeMillis();

        for (int biomeId : etiquettes.keySet()) {
            String biomeName = etiquettes.get(biomeId);
            System.out.println("\nBiome " + etiquettes.get(biomeId) + " : " + allNumCluster.get(biomeId));

            // Affichage du biome
            String outputBiomePath = "cartes2/" + nomImage + "_" + biomeName + "."+ extension;
            copieImage.afficherBiome(outputBiomePath,list_pixel_cluster, biomeId);

            // On crée un objet DBSCANPosition pour le clustering
            DBScan dbscanPos = new DBScan(2, 9);

            // On récupère les positions des pixels pour le biome actuel
            ArrayList<int[]> positions = copieImage.getPositionsForBiome(list_pixel_cluster, biomeId);

            // On affiche le nombre de positions
            System.out.println("Début du calcul des clusters... ("+biomeName+")");
            ArrayList<Integer> ecoClusters = dbscanPos.calculate_clusters(positions);

            String destFichier = "cartes2/" + nomImage+"_"+biomeName+"_clusters" + "."+extension;
            System.out.println("Copie de l'image dans "+destFichier);

            // Affichage des écosystèmes
            copieImage.afficherEcosystemes(destFichier, positions, ecoClusters);
        }

        endTime = System.currentTimeMillis();

        System.out.println("Temps d'exécution : " + (endTime - startTime) / 1000 + "s");


    }

}
