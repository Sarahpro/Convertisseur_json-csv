package Sarah_Florian_Mathieu.Converter_json_csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class csvManager {
	private static final String dimSeparator = "__|__";
	private char separator;
	private char decimalSeparator;
	
	//  --> csv[size1D][size2D]
	private String[][] csv = null;
	private int size1D;
	private int size2D;

	
	//getters and setters
	
	/**
	 * identify multiple dimensions of json file
	 * @return which separator used to identify dimensions above 2
	 */
	public static final String getDimSeparator() {
		return dimSeparator;
	}
	
	/**
	 * get the separator for values
	 * @return character used to separate values
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * change value of separator for values if possible
	 * @param separator which character to update
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
		try{
			checkValidSeparator();
		}
		catch(IllegalArgumentException e) {
			e.getLocalizedMessage();
		}
	}

	/**
	 * get the separator for decimals
	 * @return character used to separate in decimals
	 */
	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	/**
	 * change value of separator for decimals if possible
	 * @param decimalSeparator which character to update
	 */
	public void setDecimalSeparator(char decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
		try{
			checkValidSeparator();
		}
		catch(IllegalArgumentException e) {
			e.getLocalizedMessage();
		}
	}
	
	/**
	 * get the size of the first dimension of array
	 * @return size of the first dimension of array
	 */
	public int getSize1D() {
		return size1D;
	}

	/**
	 * get the size of the second dimension of array
	 * @return size of the second dimension of array
	 */
	public int getSize2D() {
		return size2D;
	}
	
	/**
	 * get the string value at i,j position in array if possible
	 * @param i which position in first dimension of array
	 * @param j which position in second dimension of array
	 * @return string value at i,j position in array if possible
	 */
	public String getStringAt(int i, int j) {
		if(csv != null) {
			try {
				return csv[i][j];
			}
			catch(ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.err.println("Size is [" + size1D + "][" + size2D + "]");
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * the returned string contains each box in the table , the columns are separated by a comma and the lines are separated from a new line
	 */
	public String toString() {
		String s = "";
		int x,y;
		
		if(csv == null) return "null";
		
		for(y = 0; y < size2D; y++) {
			for(x = 0; x < size1D - 1; x++) {
				s += csv[x][y] + ',';
			}
			s += csv[x][y] + '\n';
		}
		return s;
	}
	
	//constructors
	
	/**
	 * Construction of csvManager with customs separators
	 * @param separator to separate the values
	 * @param decimalSeparator for decimals
	 * @throws IllegalArgumentException invalid separator
	 */
	public csvManager(char separator, char decimalSeparator) throws IllegalArgumentException {
		this.separator = separator;
		this.decimalSeparator = decimalSeparator;
		
		checkValidSeparator();
		
		csv = null;
		size1D = 0;
		size2D = 0;
	}
	
	/**
	 * Construction of csvManager with English separators
	 * @throws IllegalArgumentException invalid separator
	 */
	public csvManager() throws IllegalArgumentException {
		this(',', '.');
	}
	
	/**
	 * Check if actually separators are the same or not an allowed separator 
	 * @throws IllegalArgumentException invalid separator
	 */
	private void checkValidSeparator() throws IllegalArgumentException {
		if(separator == decimalSeparator) {
			System.err.println("Separator and decimalSeparator are equals");
			throw new IllegalArgumentException();
		}
		if(decimalSeparator != ',' && decimalSeparator != '.') {
			System.err.println("decimalSeparator must be one of them {<,>,<.>}");
			throw new IllegalArgumentException();
		}
		switch(separator) {
		case ',' :
		case ';' :
		case '|' :
		case '#' :
		case '/' :
		case '&' :
		case ':' :
		case ' ' :
		case '\t' : /* separator allowed */ break;
		default : 
			System.err.println("Separator for compartment must be one of them {<,>,<;>,<|>,<#>,</>,<&>,<:>,< >, <tabulation>}");
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * read csv files if possible and store data in array of String
	 * @param path which file to process
	 * @throws IllegalArgumentException problem with path
	 * @throws IOException read the file
	 * @throws NonReadableCsvFileException syntax error or problem with values
	 */
	public void loadFile(String path) throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		checkPath(path);
		calculSizeTab(path);
		readFile(path);
	}

	/**
	 * check if path refer to a valid file
	 * @param path which file to process
	 * @throws IllegalArgumentException problem with path
	 * @throws IOException problem reading the file
	 */
	private void checkPath(String path) throws IllegalArgumentException, IOException {
		if(path == null) {
			System.err.println("path is not selected");
			throw new IllegalArgumentException();
		}
		if(path.endsWith(".csv") == false ) {
			System.err.println("This path doesn't correspond with a csv file");
			throw new IllegalArgumentException();
		}
		
		File f = new File(path);
		if(f.exists() == false) {
			System.err.println("File " + path + " not found");
			throw new FileNotFoundException();
		}
		if(f.canRead() == false) {
			System.err.println("File " + path + "can't be read");
			throw new IOException();
		}
	}
	
	/**
	 * computes table size from file with separators and rows
	 * @param path which file to process
	 * @throws NonReadableCsvFileException if file is empty
	 * @throws IOException read the file
	 */
	private void calculSizeTab(String path) throws NonReadableCsvFileException, IOException {
		FileReader fr = null;
		fr = new FileReader(path);
		int c = 0;
		boolean emptyLine = true;
		//calcul de size1D
		int cptSeparator = 0;
		while(c != '\n' && c != -1) {
			c = fr.read();
			//si un caractère lu n'est pas un caractère blanc la ligne n'est pas vide
			if(c != '\n' && c != ' ' && c != '\t' && c != -1) emptyLine = false;
			if(c == separator) cptSeparator++;
		}
		//fin de calcul de size1D
		
		if(cptSeparator == 0 && emptyLine) {
			fr.close();
			System.err.println(path + " is empty");
			throw new NonReadableCsvFileException();
		}
		
		//calcul de size2D
		int nbLines = 1;
		emptyLine = true;
		while (c != -1) {
			c = 0;
			while(c != '\n' && c != -1) {
				c = fr.read();
				//si un caractère lu n'est pas un caractère blanc la ligne n'est pas vide
				if(c != '\n' && c != ' ' && c != '\t' && emptyLine == true) emptyLine = false;
				//si un espace ou une tabulation est lu et que c'est le séparateur alors la ligne n'est pas vide
				if(((c == ' ' && separator == ' ') || (c == '\t' && separator == '\t')) && emptyLine == true) emptyLine = false; 
			}
			if(!emptyLine && c != -1)nbLines++;
			emptyLine = true;
		}
		//fin de calcul de size2D
		
		fr.close();
		
		size1D = cptSeparator + 1;
		size2D = nbLines;
		csv = new String[size1D][size2D];
		
		int x,y;
		for(y = 0; y < size2D; y++) 
			for(x = 0; x < size1D; x++) 
				csv[x][y] = "";
	}
	
	/**
	 * read the csv file and store data in array
	 * @param path which file to read
	 * @throws IOException read the file
	 * @throws NonReadableCsvFileException syntax error or problem with values
	 */
	private void readFile(String path) throws IOException, NonReadableCsvFileException {
		FileReader fr = null;
		fr = new FileReader(path);
		int c = 0;
		String val = "";
		int i = 0, j = 0;
		
		while(c != -1 && j != size2D) {
			while(c != '\n' && c != -1) {
				c = fr.read();
				
				if(c == separator) { //traitement du caractère de séparation de colonne
					if(val.charAt(0) == '"' && (val.charAt(val.length() - 1) != '"' || val.length() == 1)) { //si le separateur est dans une chaine de caractere, on le sauvegarde
						val += (char)c;
					}
					else {
						if(val.charAt(0) == '"' && val.endsWith("\"") && val.length() > 2) val = val.substring(1, val.length() - 1);
						if(val.length() < 3 && val.charAt(0) == '"' && val.endsWith("\"")) val = "";
						csv[i][j] = val;
						val = "";
						i++;
						if(i == size1D) {
							System.err.println("too many values to read at line " + j);
							fr.close();
							throw new NonReadableCsvFileException();
						}
					}
				}
				
				//si la ligne ne contient que des tabulations ou des espaces, la ligne sera ignoré sauf si le separateur est defini sur espace ou tabulation
				else if(!(((c == ' ') || c == '\t') && val.length() == 0) && c != '\n') {
					val += (char)c;
				}
			}
			csv[i][j] = val;
			j++;
			if(i == 0 && val.length() == 0)j--; //si la ligne est vide
			else if(i+1 != size1D) { //si des valeurs sont manquantes
				System.err.println("Warning : Found " + (i + 1) + "/" + size1D + " values at line " + j);
			}
			i = 0;
			val = "";
			if(c != -1) c = 0;
		}
		fr.close();
	}
	
	/**
	 * check if name of new csv file is available
	 * @param name name of the new file
	 * @throws IllegalArgumentException if incorrect name of unavailable name is given
	 */
	private void checkNameForSave(String name) throws IllegalArgumentException {
		if(name == null) {
			System.err.println("name is not selected");
			throw new IllegalArgumentException();
		}
		if(name.equals("")) {
			System.err.println("Name given is empty");
			throw new IllegalArgumentException();
		}
		
		File f = new File(name + ".csv");
		if(f.exists() == true) {
			System.err.println("File " + name + ".csv already exists");
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * replace a char at the position i by the newChar
	 * @param s string in which a character must be replaced
	 * @param i position of character to replace
	 * @param newChar character that will replace the character at position i
	 * @return string with change
	 */
	private String changeChar(String s, int i, char newChar) {
		String chaine = "";
		String chaine2 = "";
		int a;
		if(i < s.length()) {
			for(a = 0; a < i; a++)
				chaine += s.charAt(a);
			a++;
			for(; a < s.length(); a++)
				chaine2 += s.charAt(a);
		}
		return chaine + newChar + chaine2;
	}
	
	/**
	 * write in the number format if param s can be cast in Double or Integer type
	 * @param fw to write in the file
	 * @param s string to test if is number
	 * @return false if param s can't be cast as a number, true else
	 * @throws IOException if problem to write
	 */
	private boolean tryWriteNumber(OutputStreamWriter fw, String s) throws IOException {
		String work = "" + s;
		if(decimalSeparator == ',') {
			for(int i = 0; i < work.length(); i++) {
				if((work.charAt(i) < '0' || work.charAt(i) > '9') && work.charAt(i) != '-' && work.charAt(i) != decimalSeparator) return false;
				else if(work.charAt(i) == decimalSeparator) work = changeChar(work,i,'.');
			}
		}
		try {
			Double.parseDouble(work);
			fw.write(s);
		}
		catch(java.lang.NumberFormatException e) {
			try{
				Integer.parseInt(work);
				fw.write(s);
			}
			catch(java.lang.NumberFormatException e2) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * write the table csv in the file
	 * @param name name of the new csv file
	 * @throws IOException if problem to write
	 */
	private void writeToFile(String name) throws IOException {
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(name));
		int i,j;
		for(i = 0; i < size1D - 1; i++) {
			fw.write(csv[i][0] + separator);
		}
		fw.write(csv[i][0] + "\n");
		
		for(j = 1; j < size2D; j++) {
			for(i = 0; i < size1D - 1; i++) {
				if(!tryWriteNumber(fw,csv[i][j])) {
					fw.write("\"");
					fw.write(csv[i][j]);
					fw.write("\"");
				}
				fw.write(separator);
			}
			if(!tryWriteNumber(fw,csv[i][j])) {
				fw.write("\"");
				fw.write(csv[i][j]);
				fw.write("\"");
			}
			fw.write("\n");
		}
		fw.close();
	}
	
	/**
	 * save the array csv in the file specified in the parameter name
	 * @param name name of the new csv file (the .csv suffix is automatically added)
	 * @throws IOException if problem to write
	 * @throws IllegalArgumentException if name is incorrect or unavailable
	 */
	public void saveAs(String name) throws IOException, IllegalArgumentException {
		checkNameForSave(name);
		name += ".csv";
		writeToFile(name);
	}
}
