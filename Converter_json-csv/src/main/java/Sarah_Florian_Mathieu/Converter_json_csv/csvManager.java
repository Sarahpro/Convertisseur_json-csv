package Sarah_Florian_Mathieu.Converter_json_csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.IllegalFormatException;

public class csvManager {
	private static final String dimSeparator = "__|__";
	private char separator;
	private char decimalSeparator;
	private String[][] csv = null;
	private int size1D;
	private int size2D;

	//getters and setters
	
	public static final String getDimSeparator() {
		return dimSeparator;
	}
	
	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
		try{
			checkValidSeparator();
		}
		catch(IllegalArgumentException e) {
			System.out.println("Separator for decimal already use this separator character");
			e.getLocalizedMessage();
		}
	}

	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(char decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
		try{
			checkValidSeparator();
		}
		catch(IllegalArgumentException e) {
			System.out.println("Separator for compartment already use this separator character");
			e.getLocalizedMessage();
		}
	}

	
	//constructors
	
	/**
	 * Construction of csvManager with customs separators
	 * @param separator
	 * @param decimalSeparator
	 * @throws IllegalArgumentException
	 */
	csvManager(char separator, char decimalSeparator) throws IllegalArgumentException {
		this.separator = separator;
		this.decimalSeparator = decimalSeparator;
		
		checkValidSeparator();
		
		csv = null;
	}
	
	/**
	 * Construction of csvManager with English separators
	 * @throws IllegalArgumentException
	 */
	csvManager() throws IllegalArgumentException {
		this(',', '.');
	}
	
	/**
	 * Check if actually separators are the same or not an allowed separator 
	 * @throws IllegalArgumentException
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
		case ' ' : /* separator allowed */ break;
		default : 
			System.err.println("Separator for compartment must be one of them {<,>,<;>,<|>,<#>,</>,<&>,<:>,< >}");
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * read csv files if possible and store data in array of String
	 * @param path which file to process
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void loadFile(String path) throws IllegalArgumentException, IOException {
		checkPath(path);
		calculSizeTab(path);
		readFile(path);
	}

	/**
	 * check if path refer to a valid file
	 * @param path which file to process
	 * @throws IllegalArgumentException
	 * @throws IOException
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
			System.err.println("File " + path + "not found");
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
	 * @throws IOException
	 */
	private void calculSizeTab(String path) throws IOException {
		FileReader fr = null;
		fr = new FileReader(path);
		char c = 0;
		
		//calcul de size1D
		boolean ignore = false;
		int cptSeparator = 0;
		while(c != '\n' && c != -1) {
			c = (char)fr.read();
			if(c == separator && !ignore) cptSeparator++;
			if(c == '"')ignore = !ignore;
			if(c == '\\') c = (char)fr.read();
		}
		//fin de calcul de size1D
		
		if(c == -1) {
			fr.close();
			throw new IOException();
		}
		
		//calcul de size2D
		boolean emptyLine = true;
		int nbLines = 1;
		while (c != -1) {
			c = 0;
			while(c != '\n' && c != -1) {
				c = (char)fr.read();
				if(c != '\n' && c != ' ' && c != '\t') emptyLine = false;
			}
			if(!emptyLine && c != -1)nbLines++;
			emptyLine = true;
		}
		//fin de calcul de size2D
		
		fr.close();
		
		size1D = cptSeparator + 1;
		size2D = nbLines;
		csv = new String[size1D][size2D];
	}
	
	/**
	 * A part of readFile() that treats strings (when <"> found)
	 * @param fr variable to read the current file
	 * @param val store the value at the compartment (i,j)
	 * @param c variable that store the character read
	 * @param i variable that increment on the column
	 * @param j variable that increment on the row
	 * @return string containing value AND the last character read that must be removed after
	 * @throws IOException
	 */
	private String readFilePartString(FileReader fr, String val, char c, int i, int j) throws IOException {
		if(c == '"' && val.length() == 0) { //traitement d'une chaine de caractère
			while(c != '\n' && c != -1 && c != '"' && (c == ' ' || c == '\t')) {
				c = (char)fr.read();
				if(c == '\\') { //traitement d'un caractère précédé d'un <\>
					c = (char)fr.read();
					if(c == '"' || c == '\\') val.concat("" + c);
					else System.err.println("Warning : ignored character at line " + j + ", column " + i);
					if(c != '\n' && c != -1) c = (char)fr.read();
				}
				else if(c == ' ' || c == '\t') { //traitement d'un caractère blanc
					System.err.println("Warning : ignored tabulation or space character found at line " + j + ", column " + i);
				}
				else val.concat("" + c);
			}
			c = (char)fr.read();
			if(c != '\n' && c != -1 && c != separator){ // s'il existe encore des caractères pour la valeur de la case
				System.err.println("Non-readable File, problem found at line " + j + ", column " + i);
				fr.close();
				throw new IOException();
			}
		}
		return val + c; //afin de retourner le caractère lu avec la chaine
	}
	
	/**
	 * A part of readFile() that treats characters
	 * @param fr variable to read the current file
	 * @param val store the value at the compartment (i,j)
	 * @param c variable that store the character read
	 * @param i variable that increment on the column
	 * @param j variable that increment on the row
	 * @return string containing value AND the last character read that must be removed after
	 * @throws IOException
	 */
	private String readFilePartCharacter(FileReader fr, String val, char c, int i, int j) throws IOException {
		if(c == '\\') { //traitement d'un caractère précédé d'un <\>
			c = (char)fr.read();
			if(c == '"' || c == '\\') val.concat("" + c);
			else System.err.println("Warning : only <\\> or <\"> are accepted after <\\>. See at line " + j + ", column " + i);
			if(c != '\n' && c != -1) c = (char)fr.read();
		}
		
		if(c == separator) { //traitement du caractère de séparation de colonne
			i++;
			if(i == size1D) {
				System.err.println("too many values to read at line" + j);
				fr.close();
				throw new IOException();
			}
		}
		if(c == ' ' || c == '\t') { //traitement d'un caractère blanc
			System.err.println("Warning : ignored tabulation or space character found at line " + j + ", column " + i);
		}
		if(c == '"' && val.length() > 0) { //si l'on lit une chaine de caractère alors que l'on a déjà lu un bout de valeur
			System.err.println("Non-readable File, problem found at line " + j + ", column " + i);
			fr.close();
			throw new IOException();
		}
		return val + c;
	}
	
	/**
	 * read the csv file and store data in array
	 * @param path
	 * @throws IOException
	 */
	private void readFile(String path) throws IOException {
		FileReader fr = null;
		fr = new FileReader(path);
		char c = 0;
		String val = "";
		int i = 0, j = 0;
		
		while(c != -1) {
			while(c != '\n' && c != -1) {
				c = (char)fr.read();
				
				val = readFilePartString(fr, val, c, i, j); //partie traitement d'une chaine de caractère
				c = val.charAt(val.length() - 1); //récupérer le caractère stocké dans la chaine
				val = val.substring(0, val.length() - 1); //on retire le caractère ajouté à la fin de la chaine
				
				val = readFilePartCharacter(fr, val, c, i, j); //partie traitement d'un caractère
				c = val.charAt(val.length() - 1); //récupérer le caractère stocké dans la chaine
				val = val.substring(0, val.length() - 1); //on retire le caractère ajouté à la fin de la chaine
				
			}
			csv[i][j] = val;
			if(val.length() == 0)j++;
			i = 0;
			val = "";
		}
		fr.close();
	}
}
