package innohack.gem.core;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extract.ExtractedRecords;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GEMMainTest {

  @Test
  public void testProcess() throws Exception {
    String file = "src/test/resources/reviews.csv";
    String spec = "src/test/resources/core/spec.json";
    ObjectMapper mapper = new ObjectMapper();
    Project project = mapper.readValue(new File(spec), Project.class);
    File f = new File(file);

    List<ExtractedRecords> recordsList = GEMMain.process(f, project);
    assertEquals(1, recordsList.size());
    assertEquals(2, recordsList.get(0).getHeaders().size());
    assertEquals("Sender_Name", recordsList.get(0).getHeaders().get(1));
    assertEquals(5, recordsList.get(0).getRecords().size());
    assertEquals("Sam", recordsList.get(0).getRecords().get(0).get(1));
  }
}
