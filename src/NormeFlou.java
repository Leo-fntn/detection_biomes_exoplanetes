import java.awt.image.BufferedImage;

public interface NormeFlou {
    /**
     * Returns the RGB values of the pixel at (x, y) in the image after applying the blur effect.
     *
     * @param image The image to process.
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return An array containing the RGB values of the pixel after applying the blur effect.
     */
    int[][] getRGB(BufferedImage image, int x, int y);

    /**
     * Returns the size of the blur effect.
     *
     * @return The size of the blur effect.
     */
    int getTaille();
}
