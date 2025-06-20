package normes;

import java.awt.*;

public class NormeCIELAB implements NormeCouleurs {


    @Override
    public double distanceCouleur(Color c1, Color c2) {

        int[] lab1 = RGBtoLAB.rgb2lab(c1.getRed(), c1.getGreen(), c1.getBlue());
        int[] lab2 = RGBtoLAB.rgb2lab(c2.getRed(), c2.getGreen(), c2.getBlue());

        double deltaL = lab1[0] - lab2[0];
        
        double C1 = Math.sqrt(Math.pow(lab1[1], 2) + Math.pow(lab1[2], 2));
        double C2 = Math.sqrt(Math.pow(lab2[1], 2) + Math.pow(lab2[2], 2));
        
        double deltaC = C1 - C2;
        
        double deltaH = Math.sqrt(Math.pow(lab1[1] - lab2[1], 2) + Math.pow(lab1[2] - lab2[2], 2) - Math.pow(deltaC, 2));

        double Sc = 1 + 0.045 * C1;
        double Sh = 1 + 0.015 * C1;

        return Math.sqrt(Math.pow(deltaL, 2) + Math.pow((deltaC / Sc), 2) + Math.pow((deltaH / Sh), 2));

    }


}
