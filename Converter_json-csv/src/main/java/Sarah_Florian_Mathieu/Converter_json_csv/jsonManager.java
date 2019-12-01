package Sarah_Florian_Mathieu.Converter_json_csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class jsonManager {
    private int Acc_number; //Nombre de crochets lus (utile pour vérification structure fichier)
    private int Hk_number; //Nombre d'accolades lues
    private final char openObjectSeparator = '{';
    private final char closeObjectSeparator = '}';
    private final char openObjectElementsSeparator = '[';
    private final char closeObjectElementsSeparator = ']';

    /** class constructor
     *
     */
    public jsonManager(){
        this.Acc_number = 0;
        this.Hk_number = 0;
    }

    /**
     * 
     */
    public void ajoutSeparator(){

    }

    /**
     * Verify the correct number of separators
     * @return true if numbers of open separators - close separators = 0 or false
     */
    public boolean verifseparator(){
    	return true;
    }


}
