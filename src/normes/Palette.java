package normes;

import java.awt.*;
import java.util.Random;

public class Palette {

    Color[] colors;
    NormeCouleurs norme;

    public Palette(Color[] colors) {
        this.colors = colors;
    }

    public Palette(Color[] colors, NormeCouleurs norme){
        this.colors = colors;
        this.norme = norme;
    }

    public Color getPlusProche(Color c){
        Color closestColor = colors[0];
        double minDistance = Double.MAX_VALUE;

        for (Color color : colors) {
            double distance;

            if (norme != null) {
                distance = norme.distanceCouleur(c, color);
            }
            else {
                distance = Math.sqrt(Math.pow(c.getRed() - color.getRed(), 2) +
                        Math.pow(c.getGreen() - color.getGreen(), 2) +
                        Math.pow(c.getBlue() - color.getBlue(), 2));
            }
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }
        return closestColor;
    }

    public static Color[] getRandomColors(int n){
        Random rand = new Random();
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
        }
        return colors;
    }
}
