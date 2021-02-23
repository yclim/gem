package innohack.gem.core.entity.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.google.common.collect.Sets;

import innohack.gem.core.rules.Rule;

public class RuleFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleFactory.class);

	public static void main(String[] arg) {
		HashMap<RuleType, List<Rule>> mapping = new HashMap<>();
		List<Rule> rules = RuleFactory.createAllInstance();
		for (Rule r : rules) {
			LOGGER.info("Rule: {}", r.getRuleType());
			List<Rule> ruleList = mapping.get(r.getRuleType());
			if (ruleList == null) {
				ruleList = new ArrayList<Rule>();
			}
			ruleList.add(r);
			mapping.put(r.getRuleType(), ruleList);
		}
	}

	public static Rule createInstance(String ruleId) {
		try {
			Iterator<Class<? extends Rule>> iterator = getAllConcreteRuleClass();
			while (iterator.hasNext()) {
				Class<? extends Rule> rule = iterator.next();
				if (rule.getCanonicalName().equals(ruleId)) {
					return rule.newInstance();
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Rule> createAllInstance() {
		List<Rule> rules = new ArrayList<Rule>();
		try {
			Iterator<Class<? extends Rule>> iterator = getAllConcreteRuleClass();
			while (iterator.hasNext()) {
				Class<? extends Rule> ruleClass = iterator.next();
				Rule rule = ruleClass.newInstance();
				LOGGER.debug("(createAllInstance) rule added : {}", rule);
				rules.add(rule);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return rules;
	}

	private static Iterator<Class<? extends Rule>> getAllConcreteRuleClass() {
		Set<Class<? extends Rule>> results = Sets.newHashSet();
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(Rule.class));

		// scan in org.example.package
		Set<BeanDefinition> components = provider.findCandidateComponents(Rule.class.getPackage().getName());
		for (BeanDefinition component : components) {
			try {
				Class cls = Class.forName(component.getBeanClassName());
				results.add(cls);
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException("Unable to find Rule class", ex);
			}
		}
		return results.iterator();

//    Reflections reflections = new Reflections(Rule.class.getPackage().getName());
//    Set<Class<? extends Rule>> allClasses = reflections.<Rule>getSubTypesOf(Rule.class);
//    return allClasses.iterator();
	}
}
