package Sarah_Florian_Mathieu.Converter_json_csv;

public class csvManager {
	private static final String dimSeparator = "__|__";
	private char separator;
	private char decimalSeparator;
	private char[][] csv = null;

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
		case '_' :
		case '&' :
		case '~' :
		case ':' : /* separator allowed */ break;
		default : 
			System.err.println("Separator must be one of them {<,>,<;>,<|>,<#>,</>,<_>,<&>,<~>,<:>}");
			throw new IllegalArgumentException();
		}
	}
	
	
}
