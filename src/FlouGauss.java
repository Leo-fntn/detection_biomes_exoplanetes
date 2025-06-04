public class FlouGauss implements NormeFlou{
    private int taille;

    public FlouGauss(int taille) {
        this.taille = taille;
    }

    @Override
    public int[] getRGB(BufferedImage image, int x, int y) {
        double r = 0, g = 0, b = 0;
        double sum = 0.0;

        for (int i = -taille; i <= taille; i++) {
            for (int j = -taille; j <= taille; j++) {
                int newX = x + i;
                int newY = y + j;

                if (newX >= 0 && newY >= 0 && newX < image.getWidth() && newY < image.getHeight()) {
                    int rgb = image.getRGB(newX, newY);
                    double weight = Math.exp(-(i * i + j * j) / (2.0 * taille * taille));
                    r += ((rgb >> 16) & 0xFF) * weight;
                    g += ((rgb >> 8) & 0xFF) * weight;
                    b += (rgb & 0xFF) * weight;
                    sum += weight;
                }
            }
        }

        return new int[]{(int)(r / sum), (int)(g / sum), (int)(b / sum)};
    }

    @Override
    public int getTaille() {
        return taille;
    }
}
