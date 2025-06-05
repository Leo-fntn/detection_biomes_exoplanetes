import clustering.DBSCANColor;
import clustering.DBSCANPosition;
import normes.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    public void writeImage(String outputPath) {
        try {
            // Save the image in a different format
            ImageIO.write(image, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImage(String outputPath) {
        // copy the image pixel by pixel
        // create and save a new image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                newImage.setRGB(i, j, image.getRGB(i, j));
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImageBlackAndWhite(String outputPath) {
        // create and save a new image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int[] tabColor = OutilCouleur.getTabColor(rgb);
                int grayValue = (tabColor[0] + tabColor[1] + tabColor[2]) / 3;
                newImage.setRGB(i, j, (grayValue << 16) | (grayValue << 8) | grayValue);
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImageRed(String outputPath){
        // create and save a new image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int red = rgb & 0xFF0000;
                newImage.setRGB(i, j, red);
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImageGreenBlue(String outputPath) {
        // create and save a new image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                int gb = rgb & 0x00FFFF;
                newImage.setRGB(i, j, gb);
            }
        }
        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyImageFlou(String outputPath, NormeFlou flou) {
        int width = image.getWidth();
        int height = image.getHeight();
        int taille = flou.getTaille();
        int demi = taille / 2;

        BufferedImage newImage = new BufferedImage(width/taille, height/taille, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = demi; i < width - demi; i += taille) {
            for (int j = demi; j < height - demi; j += taille) {
                int[] rgbZone = flou.getRGB(image, i, j);

//                for (int dx = -demi; dx <= demi; dx++) {
//                    for (int dy = -demi; dy <= demi; dy++) {
//                        int x = i + dx;
//                        int y = j + dy;
//
//                        if (x >= 0 && y >= 0 && x < width && y < height) {
//                            int r = rgbZone[0];
//                            int g = rgbZone[1];
//                            int b = rgbZone[2];
//                            int rgb = (r << 16) | (g << 8) | b;
//                            newImage.setRGB(x, y, rgb);
//                        }
//                    }
//                }
                int r = rgbZone[0];
                int g = rgbZone[1];
                int b = rgbZone[2];
                int rgb = (r << 16) | (g << 8) | b;
                newImage.setRGB(i/taille, j/taille, rgb);
            }
        }

        try {
            ImageIO.write(newImage, "jpg", new File(outputPath));
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    public ArrayList<ArrayList<Integer>> getData() {

        ArrayList<ArrayList<Integer>> datas = new ArrayList<>();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                ArrayList<Integer> rbg_pixel = new ArrayList<>();

                int rgb = image.getRGB(j, i);
                int[] tabColor = OutilCouleur.getTabColor(rgb);

                for (int value : tabColor) {
                    rbg_pixel.add(value);
                }

                datas.add(rbg_pixel);

            }
        }

        System.out.println(datas);

        return datas;

    }


    /**
     * methode pour afficher un biome par son cluster
     * @param clusters
     * @param num
     */
    public void afficherBiome(ArrayList<Integer> clusters,int num){

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
            ImageIO.write(newImage, "png", new File("cartes2/test.png"));
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

    public ArrayList<ArrayList<Integer>> getPositionsForBiome(ArrayList<Integer> clusters, int biomeId) {
        ArrayList<ArrayList<Integer>> positions = new ArrayList<>();

        int width = image.getWidth();

        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i) == biomeId) {
                int x = i % width;
                int y = i / width;
                ArrayList<Integer> point = new ArrayList<>();
                point.add(x);
                point.add(y);
                positions.add(point);
            }
        }
        return positions;
    }

    public void afficherEcosystemes(String outputPath, ArrayList<ArrayList<Integer>> positions, ArrayList<Integer> ecosClusters) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Color[] palette = Palette.getRandomColors(100); // Génère 100 couleurs distinctes

        for (int i = 0; i < positions.size(); i++) {
            ArrayList<Integer> point = positions.get(i);
            int clusterId = ecosClusters.get(i);
            if (clusterId == -1) continue; // bruit

            Color c = palette[clusterId % palette.length];
            newImage.setRGB(point.get(0), point.get(1), c.getRGB());
        }

        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {


        CopieImage copieImage = new CopieImage();



        String inputPath = "cartes/Planete 4.jpg";
        String outputPath = "cartes2/Planete4_flou.png";
        String outputPath2 = "cartes2/Planete4_flou_gauss.png";


        /*String inputPath = "cartes/carte_test.jpg";
        String outputPath = "cartes2/carte_test_flou.png";
        String outputPath2 = "cartes2/carte_testflou_gauss.png";*/




        // Load and save the image
        copieImage.saveImage(inputPath);

        FlouMoyen flou = new FlouMoyen(3);
        FlouGauss flou2 = new FlouGauss(9);

        // Write the image to a new file
        copieImage.copyImageFlou(outputPath, flou);
        copieImage.copyImageFlou(outputPath2, flou2);

        // On charge l'image flou
        copieImage.saveImage(outputPath2);
        //copieImage.saveImage("cartes/carte_test.jpg");


        /*
         * CLUSTERING DBSCAN
         */

        ArrayList<ArrayList<Integer>> list_data = copieImage.getData();
        DBSCANColor dbscan = new DBSCANColor(100, 5); //7 - 5
        ArrayList<Integer> list_pixel_cluster = dbscan.calculate_clusters(list_data);



        System.out.println("\n### RESULTAT ### ");

        HashMap<Integer, Integer> allNumCluster = new HashMap<>();
        for (int numCluster : list_pixel_cluster) {

            if (!allNumCluster.containsKey(numCluster)) { allNumCluster.put(numCluster, 1); }
            else { allNumCluster.put(numCluster, allNumCluster.get(numCluster) + 1); }

        }
        System.out.println("Numéro cluster / Nombre de pixel associé");
        System.out.println(allNumCluster);


        copieImage.afficherBiome(list_pixel_cluster, 5);

        int width = copieImage.image.getWidth();
        int height = copieImage.image.getHeight();
        //ArrayList<Integer> biomeClusters = CopieImage.genererClusters(width, height, 10); // 100 biomes

        int biomeId = 2;

        copieImage.afficherBiome(list_pixel_cluster, 2);
        DBSCANPosition dbEcos = new DBSCANPosition(40, 3); // <- CORRIGÉ
        ArrayList<ArrayList<Integer>> positions = copieImage.getPositionsForBiome(list_pixel_cluster,biomeId);
        ArrayList<Integer> ecoClusters = dbEcos.calculate_clusters(positions);
        copieImage.afficherEcosystemes("cartes2/biome_" + biomeId + "_ecos.png", positions, ecoClusters);

    }

}
