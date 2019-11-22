package Sarah_Florian_Mathieu.Converter_json_csv;

import static org.junit.Assert.*;
import org.junit.Test;

public class csvManagerTest {

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
		c1.setDecimalSeparator(',');
		c1.setDecimalSeparator('.');
		assert true;
	}
}
