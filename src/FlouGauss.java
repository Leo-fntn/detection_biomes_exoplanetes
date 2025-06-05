import java.awt.image.BufferedImage;

public class FlouGauss implements NormeFlou {
    private int taille;
    private double sigma;

    public FlouGauss(int taille) {
        if (taille % 2 == 0) throw new IllegalArgumentException("Taille du noyau doit être impaire");
        this.taille = taille;
        this.sigma = taille / 6.0;
    }

    public void setTaille(int taille) {
        if (taille % 2 == 0) throw new IllegalArgumentException("Taille du noyau doit être impaire");
        this.taille = taille;
        this.sigma = taille / 6.0;
    }

    private double gaussian(int x, int y) {
        return (1.0 / (2 * Math.PI * sigma * sigma)) *
                Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
    }

    @Override
    public int[] getRGB(BufferedImage image, int x, int y) {
        int[] result = new int[3];
        int demi = taille / 2;

        double[][] coeffs = new double[taille][taille];
        double kernelSum = 0.0;

        // Construire le noyau gaussien
        for (int i = -demi; i <= demi; i++) {
            for (int j = -demi; j <= demi; j++) {
                double value = gaussian(i, j);
                coeffs[i + demi][j + demi] = value;
                kernelSum += value;
            }
        }

        double sumR = 0, sumG = 0, sumB = 0;

        for (int dx = -demi; dx <= demi; dx++) {
            for (int dy = -demi; dy <= demi; dy++) {
                int xi = x + dx;
                int yj = y + dy;

                if (xi >= 0 && xi < image.getWidth() && yj >= 0 && yj < image.getHeight()) {
                    int rgb = image.getRGB(xi, yj);
                    double weight = coeffs[dx + demi][dy + demi] / kernelSum;

                    sumR += ((rgb >> 16) & 0xFF) * weight;
                    sumG += ((rgb >> 8) & 0xFF) * weight;
                    sumB += (rgb & 0xFF) * weight;
                }
            }
        }

        int finalR = (int) Math.round(sumR);
        int finalG = (int) Math.round(sumG);
        int finalB = (int) Math.round(sumB);

        result[0] = finalR;
        result[1] = finalG;
        result[2] = finalB;


        return result;
    }

    @Override
    public int getTaille() {
        return taille;
    }
}
