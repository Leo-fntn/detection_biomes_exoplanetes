import java.awt.image.BufferedImage;

public class FlouGauss implements NormeFlou{
    private int taille;

    public FlouGauss(int taille) {
        this.taille = taille;
    }

    @Override
    public int[][] getRGB(BufferedImage image, int x, int y) {
        return new int[3][taille * taille];
    }


    @Override
    public int getTaille() {
        return taille;
    }
}
