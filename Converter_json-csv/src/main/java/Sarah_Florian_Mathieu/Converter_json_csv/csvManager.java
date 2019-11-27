package Sarah_Florian_Mathieu.Converter_json_csv;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.IllegalFormatException;

public class csvManager {
	private static final String dimSeparator = "__|__";
	private char separator;
	private char decimalSeparator;
	
	//  --> csv[size1D][size2D]
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
			e.getLocalizedMessage();
		}
	}
	
	public int getSize1D() {
		return size1D;
	}

	public int getSize2D() {
		return size2D;
	}
	
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
	
	public String toString() {
		String s = "";
		int x,y;
		
		if(csv == null) return "csv File is not loaded";
		
		for(y = 0; y < size2D; y++) {
			for(x = 0; x < size1D; x++) {
				s += csv[x][y] + ',';
			}
			s += '\n';
		}
		return s;
	}
	
	//constructors
	
	/**
	 * Construction of csvManager with customs separators
	 * @param separator
	 * @param decimalSeparator
	 * @throws IllegalArgumentException
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
	 * @throws IllegalArgumentException
	 */
	public csvManager() throws IllegalArgumentException {
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
	 * @throws NonReadableCsvFileException 
	 */
	public void loadFile(String path) throws IllegalArgumentException, IOException, NonReadableCsvFileException {
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
	 * @throws NonReadableCsvFileException 
	 * @throws IOException 
	 */
	private void calculSizeTab(String path) throws NonReadableCsvFileException, IOException {
		FileReader fr = null;
		fr = new FileReader(path);
		int c = 0;
		
		//calcul de size1D
		boolean ignore = false;
		int cptSeparator = 0;
		while(c != '\n' && c != -1) {
			c = fr.read();
			if(c == separator && !ignore) cptSeparator++;
			if(c == '"')ignore = !ignore;
			if(c == '\\') c = fr.read();
		}
		//fin de calcul de size1D
		
		if(cptSeparator == 0) {
			fr.close();
			System.err.println("Empty File");
			throw new NonReadableCsvFileException();
		}
		
		//calcul de size2D
		boolean emptyLine = true;
		int nbLines = 1;
		while (c != -1) {
			c = 0;
			while(c != '\n' && c != -1) {
				c = fr.read();
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
	 * @throws NonReadableCsvFileException 
	 * @throws IOException 
	 */
	private String readFilePartString(FileReader fr, String val, int c, int i, int j) throws NonReadableCsvFileException, IOException {
		if(c == '"' && val.length() == 0) { //traitement d'une chaine de caractère
			do {
				c = fr.read();
				if(c == '\\') { //traitement d'un caractère précédé d'un <\>
					c = fr.read();
					if(c == '"' || c == '\\') val += c;
					else System.err.println("Warning : ignored character at line " + j);
					if(c != '\n' && c != -1) {
						c = fr.read();
					}
				}
				if(c == '\n' || c == -1) {
					System.err.println("Non-readable File, problem found at line " + j);
					fr.close();
					throw new NonReadableCsvFileException();
				}
				else if(c != '"') val += (char)c;
			}while(c != '"');
			c = fr.read();
			if(c != '\n' && c != -1 && c != separator){ // s'il existe encore des caractères pour la valeur de la case
				System.err.println("Non-readable File, problem found at line " + j);
				fr.close();
				throw new NonReadableCsvFileException();
			}
		}
		return val + (char)c; //afin de retourner le caractère lu avec la chaine
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
	 * @throws NonReadableCsvFileException 
	 */
	private String readFilePartCharacter(FileReader fr, String val, int c, int [] ite) throws IOException, NonReadableCsvFileException {
		boolean save = true;
		if(c == '\\') { //traitement d'un caractère précédé d'un <\>
			c = fr.read();
			if(c == '"' || c == '\\') val += (char)c;
			else System.err.println("Warning : only <\\> or <\"> are accepted after <\\>. See at line " + ite[1]);
			if(c != '\n' && c != -1) c = fr.read();
		}
		
		if(c == separator) { //traitement du caractère de séparation de colonne 
			csv[ite[0]][ite[1]] = val;
			val = "";
			ite[0]++;
			save = false;
			if(ite[0] == size1D) {
				System.err.println("too many values to read at line" + ite[1]);
				fr.close();
				throw new NonReadableCsvFileException();
			}
		}
		if(c == ' ' || c == '\t') { //traitement d'un caractère blanc
			System.err.println("Warning : ignored tabulation or space character found at line " + ite[1]);
			save = false;
		}
		if(c == '"' && val.length() > 0) { //si l'on lit une chaine de caractère alors que l'on a déjà lu un bout de valeur
			System.err.println("Non-readable File, problem found at line " + ite[1]);
			fr.close();
			throw new NonReadableCsvFileException();
		}
		if(c == '\n') save = false;
		
		if(save) val += (char)c;
		
		return val + (char)c;
	}
	
	/**
	 * read the csv file and store data in array
	 * @param path
	 * @throws IOException
	 * @throws NonReadableCsvFileException 
	 */
	private void readFile(String path) throws IOException, NonReadableCsvFileException {
		FileReader fr = null;
		fr = new FileReader(path);
		int c = 0;
		String val = "";
		int[] ite = new int[2];
		ite[0] = ite[1] = 0;
		
		while(c != -1 && ite[1] != size2D) {
			while(c != '\n' && c != -1) {
				c = fr.read();
				val = readFilePartString(fr, val, c, ite[0], ite[1]); //partie traitement d'une chaine de caractère
				c = val.charAt(val.length() - 1); //récupérer le caractère stocké dans la chaine
				val = val.substring(0, val.length() - 1); //on retire le caractère ajouté à la fin de la chaine (voir javadoc de la fonction)
				
				val = readFilePartCharacter(fr, val, c, ite); //partie traitement d'un caractère
				c = val.charAt(val.length() - 1); //récupérer le caractère stocké dans la chaine
				val = val.substring(0, val.length() - 1); //on retire le caractère ajouté à la fin de la chaine (voir javadoc de la fonction)
				
			}
			csv[ite[0]][ite[1]] = val;
			ite[1]++;
			if(ite[0] == 0 && val.length() == 0)ite[1]--; //si la ligne est vide
			else if(ite[0]+1 != size1D) { //si des valeurs sont manquantes
				System.err.println("Found " + ite[0] + "/" + size1D + " values at line " + ite[1]);
				csv[ite[0]][ite[1]] = "";
			}
			ite[0] = 0;
			val = "";
			c = 0;
		}
		fr.close();
	}
}
