package innohack.gem.util;

import java.util.Collection;


public class Util {

	public static <T> T first(Collection<T> values) {
		if(values==null || values.isEmpty())
			return null;
		return values.iterator().next();
	}
}
