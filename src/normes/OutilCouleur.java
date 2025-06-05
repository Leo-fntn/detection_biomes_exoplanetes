package normes;

public class OutilCouleur {
    public static int[] getTabColor(int rgb){
        int[] tabColor = new int[3];
        tabColor[0] = (rgb >> 16) & 0xFF; // Red
        tabColor[1] = (rgb >> 8) & 0xFF;  // Green
        tabColor[2] = rgb & 0xFF;         // Blue
        return tabColor;
    }
}
