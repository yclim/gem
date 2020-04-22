package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import innohack.gem.entity.GEMFile;

public class FileExtensionTest {

	@Test
	public void testCheck() throws Exception {
	  FileExtension csvRule = new FileExtension("csv");
	  FileExtension datRule = new FileExtension("dat");
	  
	  GEMFile csvFile = new GEMFile("chats.csv", "src/test/resources");
	  GEMFile csvDblFile = new GEMFile("chats.csv.csv", "src/test/resources");
    GEMFile datFile = new GEMFile("data.dat", "src/test/resources");
    
    assertTrue(csvRule.check(csvFile));
	  assertTrue(csvRule.check(csvDblFile));
	  assertTrue(datRule.check(datFile));
	  
	  assertFalse(datRule.check(csvFile));
	  assertFalse(csvRule.check(datFile));
	}

}