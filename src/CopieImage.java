import clustering.DBSCAN;
import normes.*;

import javax.imageio.ImageIO;
import javax.naming.InsufficientResourcesException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {

                ArrayList<Integer> rbg_pixel = new ArrayList<>();

                int rgb = image.getRGB(i, j);
                int[] tabColor = OutilCouleur.getTabColor(rgb);

                for (int value : tabColor) {
                    rbg_pixel.add(value);
                }

                datas.add(rbg_pixel);

            }
        }

        return datas;

    }


    public static void main(String[] args) {


        CopieImage copieImage = new CopieImage();

        /*
        String inputPath = "cartes/Planete 4.jpg";
        String outputPath = "cartes2/Planete4_flou.png";
        String outputPath2 = "cartes2/Planete4_flou_gauss.png";
        */

        String inputPath = "cartes/carte_test.jpg";
        String outputPath = "cartes2/carte_test_flou.png";
        String outputPath2 = "cartes2/carte_testflou_gauss.png";




        // Load and save the image
        copieImage.saveImage(inputPath);

        FlouMoyen flou = new FlouMoyen(3);
        FlouGauss flou2 = new FlouGauss(9);

        // Write the image to a new file
        copieImage.copyImageFlou(outputPath, flou);
        copieImage.copyImageFlou(outputPath2, flou2);

        // On charge l'image flou
        copieImage.saveImage(outputPath2);


        /*
         * CLUSTERING DBSCAN
         */

        // On récupère la liste des données de l'image (couleur RGB pour chaque pixel)
        ArrayList<ArrayList<Integer>> list_data = copieImage.getData();

        DBSCAN dbscan = new DBSCAN(2, 5);

        ArrayList<Integer> list_pixel_cluster = dbscan.calculate_clusters(list_data);

        System.out.println("Résulat :");
        System.out.println(list_pixel_cluster);



    }

}
