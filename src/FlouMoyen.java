import java.awt.image.BufferedImage;

public class FlouMoyen implements NormeFlou{
    private int taille;

    public FlouMoyen(int taille) {
        this.taille = taille;
    }

    @Override
    public int[][] getRGB(BufferedImage image, int x, int y) {
        int[][] rgb = new int

        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int i = -taille%2-1; i <= taille%2+1; i++) {
            for (int j = -taille%2-1; j <= taille%2+1; j++) {
                int newX = x + i;
                int newY = y + j;

                if (newX >= 0 && newY >= 0 && newX < image.getWidth() && newY < image.getHeight()) {
                    int rgb = image.getRGB(newX, newY);
                    r += (rgb >> 16) & 0xFF;
                    g += (rgb >> 8) & 0xFF;
                    b += rgb & 0xFF;
                    count++;
                }
            }
        }

        return new int[]{r / count, g / count, b / count};
    }

    @Override
    public int getTaille() {
        return taille;
    }
}
