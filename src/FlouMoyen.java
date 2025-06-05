import java.awt.image.BufferedImage;

public class FlouMoyen implements NormeFlou{
    private int taille;

    public FlouMoyen(int taille) {
        this.taille = taille;
    }

    @Override
    public int[] getRGB(BufferedImage image, int x, int y) {
        int demi = taille / 2;
        int sumR = 0, sumG = 0, sumB = 0;
        int count = 0;

        for (int dx = -demi; dx <= demi; dx++) {
            for (int dy = -demi; dy <= demi; dy++) {
                int xi = x + dx;
                int yj = y + dy;

                if (xi >= 0 && xi < image.getWidth() && yj >= 0 && yj < image.getHeight()) {
                    int rgb = image.getRGB(xi, yj);
                    sumR += (rgb >> 16) & 0xFF;
                    sumG += (rgb >> 8) & 0xFF;
                    sumB += rgb & 0xFF;
                    count++;
                }
            }
        }

        int avgR = sumR / count;
        int avgG = sumG / count;
        int avgB = sumB / count;

        int[] result = new int[3];

        result[0] = avgR;
        result[1]= avgG;
        result[2] = avgB;


        return result;
    }


    @Override
    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }
}
