package innohack.gem.service.extract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractorFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtractorFactory.class);

  public static List<AbstractExtractor> createAllInstance() throws Exception {
    List<AbstractExtractor> extractors = new ArrayList<>();
    Iterator<Class<? extends AbstractExtractor>> iterator = getAllConcreteExtractorClass();
    while (iterator.hasNext()) {
      Class<? extends AbstractExtractor> extractorClass = iterator.next();
      AbstractExtractor extractor = extractorClass.newInstance();
      LOGGER.debug("Extractor added : {}", extractor);
      extractors.add(extractor);
    }
    return extractors;
  }

  private static Iterator<Class<? extends AbstractExtractor>> getAllConcreteExtractorClass() {
    Reflections reflections = new Reflections(AbstractExtractor.class.getPackage().getName());
    Set<Class<? extends AbstractExtractor>> allClasses =
        reflections.getSubTypesOf(AbstractExtractor.class);
    return allClasses.iterator();
  }
}
