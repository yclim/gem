package innohack.gem.core.extract;

import com.beust.jcommander.internal.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

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

  private static Iterator<Class<? extends AbstractExtractor>> getAllConcreteExtractorClass()
      throws ClassNotFoundException {
    Set<Class<? extends AbstractExtractor>> results = Sets.newHashSet();
    ClassPathScanningCandidateComponentProvider provider =
        new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(AbstractExtractor.class));

    // scan in org.example.package
    Set<BeanDefinition> components =
        provider.findCandidateComponents(AbstractExtractor.class.getPackage().getName());
    for (BeanDefinition component : components) {
      Class cls = Class.forName(component.getBeanClassName());
      results.add(cls);
    }
    return results.iterator();
    //
    //    Reflections reflections = new Reflections(AbstractExtractor.class.getPackage().getName());
    //    Set<Class<? extends AbstractExtractor>> allClasses =
    //        reflections.getSubTypesOf(AbstractExtractor.class);
    //    return allClasses.iterator();
  }
}
