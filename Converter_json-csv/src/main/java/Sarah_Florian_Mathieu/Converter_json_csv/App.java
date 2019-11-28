package Sarah_Florian_Mathieu.Converter_json_csv;

import java.io.IOException;

/**
 * ne sert à rien
 * @author user
 *
 */
public enum App 
{
	APPPLICATION;
	
    public static void main( String[] args ) throws IllegalArgumentException, IOException, NonReadableCsvFileException
    {
    	csvManager csv = new csvManager ();
		//csv.loadFile("CsvFile.csv");
		System.out.println(csv);
    }
    
    csvManager m = new csvManager();
}
