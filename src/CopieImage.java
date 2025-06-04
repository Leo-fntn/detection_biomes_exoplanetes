import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public void copyImageFlou(String outputPath, NormeFlou flou){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int[] rgb = flou.getRGB(image, i, j);
                int r = rgb[0];
                int g = rgb[1];
                int b = rgb[2];
                newImage.setRGB(i, j, (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(newImage, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CopieImage copieImage = new CopieImage();
        String inputPath = "cartes/Planete 1.jpg";
        String outputPath = "cartes2/Planete1_flou.png";
        String outputPath2 = "cartes2/Planete1_flou2.png";

        // Load and save the image
        copieImage.saveImage(inputPath);

        FlouMoyen flou = new FlouMoyen(3);

        // Write the image to a new file
        copieImage.copyImageFlou(outputPath, flou);

    }
}
