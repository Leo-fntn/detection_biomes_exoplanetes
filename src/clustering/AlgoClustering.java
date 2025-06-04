package clustering;

import java.util.ArrayList;

public interface AlgoClustering {

    /**
     * Méthode qui permet de calculer des clusters à partir d'une liste de caractéristiques pour chaque objet
     * @param list_carac Liste d'objet représenté par une liste de caractéristiques (R, G, B)
     * @return Une liste indiquant le numéro de cluster pour chaque objet de la liste passée en paramètre
     */
    public ArrayList<Integer> calculate_clusters(ArrayList<ArrayList<Integer>> list_carac);

}
