package Sarah_Florian_Mathieu.Converter_json_csv;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class csvManagerTest {

	@Before
	public void setUp() throws IOException {
		File f = new File("TestFileNotCsv.txt");
		if(f.exists() == false) {
			f.createNewFile();
			f.deleteOnExit();
		}
		
		f = new File("TestCsvType.csv");
		if(f.exists() == false) {
			f.createNewFile();
			f.deleteOnExit();
		}
		
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("CsvFile.csv"));
		fw.write("marque,nom,\tquantité,produit, prix\n"
				+ "\n"
				+ "Andros,\"yaourt au citron,fraise\",2,yaourt,1.50\n"
				+ "            \t\n"
				+ "\"La laitière\",\"yaourt à la \"vanille\"\",5,yaourt,2.50\n");
		fw.close();
	}
	
	@After
	public void tearDown() throws IOException {
		File f = new File("CsvFile.csv");
		if(f.exists()) f.deleteOnExit();
		f = new File("CsvFile2.csv");
		if(f.exists()) f.deleteOnExit();
		f = new File("CsvFile3.csv");
		if(f.exists()) f.deleteOnExit();
		f = new File("CsvFile4.csv");
		if(f.exists()) f.deleteOnExit();
	}
	
	@Test
	public void testInitEnglish() {
		csvManager c1 = new csvManager(',','.');
		assertTrue(c1.getDecimalSeparator() == '.' && c1.getSeparator() == ',');
	}
	
	@Test
	public void testInitFrenchWithSetter() {
		csvManager c1 = new csvManager();
		c1.setSeparator(';');
		c1.setDecimalSeparator(',');
		assertTrue(c1.getDecimalSeparator() == ',' && c1.getSeparator() == ';');
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitEqualSeparator1_2() {
		csvManager c1 = new csvManager(',',',');
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitIllegalSeparator2() {
		csvManager c1 = new csvManager(',',';');
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInitIllegalSeparator1() {
		csvManager c1 = new csvManager('.',',');
	}
	
	@Test
	public void testAllPossibleSeparators() {
		csvManager c1 = new csvManager();
		c1.setSeparator(';');
		c1.setSeparator(',');
		c1.setSeparator('|');
		c1.setSeparator('#');
		c1.setSeparator('/');
		c1.setSeparator('&');
		c1.setSeparator(':');
		c1.setSeparator(' ');
		c1.setSeparator('\t');
		c1.setDecimalSeparator(',');
		c1.setDecimalSeparator('.');
		assert true;
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testPathNull() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile(null);
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testFileNotFound() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("FileNotFound.csv");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testIsNotCsvFile() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("TestFileNotCsv.txt");
	}
	
	@Test (expected = NonReadableCsvFileException.class)
	public void testEmptyFile() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("TestCsvType.csv");
	}
	
	/*
	 * Tests unitaires sur la détection d'erreur dans un fichier csv 
	 */
	
	@Test
	public void testLectureFileEffective() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile.csv");
		assertTrue(csv.getStringAt(2, 2).equalsIgnoreCase("5"));
	}
	
	@Test (expected = NonReadableCsvFileException.class)
	public void testLectureFileTooMuchValues() throws IOException, IllegalArgumentException, NonReadableCsvFileException {
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("CsvFile2.csv"));
		fw.write("marque,nom,quantité,produit,prix\n"
				+ "Andros,\"yaourt au citron\",2,yaourt,1.50,\n"
				+ "\"La laitière\",\"yaourt à la vanille\",5,yaourt,2.50\n");
		fw.close();
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile2.csv");
	}
	
	@Test
	public void testLectureFileMissingValues() throws IOException, IllegalArgumentException, NonReadableCsvFileException {
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("CsvFile3.csv"));
		fw.write("marque,nom,quantité,produit,prix\n"
				+ "Andros,\"yaourt au citron\",2,yaourt,1.50\n"
				+ "\"La laitière,yaourt à la vanille\",5,yaourt,2.50\n");
		fw.close();
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile3.csv");
		assertTrue(csv.getStringAt(4, 2) == "");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void saveWithNameNull() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile.csv");
		csv.saveAs(null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void saveWithNameEmpty() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile.csv");
		csv.saveAs("");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void saveWithNameThatAlreadyExists() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile.csv");
		csv.saveAs("CsvFile");
	}
	
	@Test
	public void saveEffective() throws IllegalArgumentException, IOException, NonReadableCsvFileException {
		csvManager csv = new csvManager ();
		csv.loadFile("CsvFile.csv");
		csv.saveAs("CsvFile4");
		csvManager csv4 = new csvManager();
		csv4.loadFile("CsvFile4.csv");
		assertTrue(csv.toString().equalsIgnoreCase(csv4.toString()));
	}
	
	

}
