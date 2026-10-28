package eu.fays.sandbox.format;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocaleEssay {

/*
-Duser.language=en
-Duser.country=AU
-Duser.variant=POSIX
-Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
*/

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(LocaleEssay.class.getName());

	public static void main(String[] args) throws Exception {
		logLocale();
	}

	public static void logLocale() {
		try {
			final Locale locale = Locale.getDefault();
			locale.toLanguageTag();

			Method[] methods = Locale.class.getMethods();
			sort(methods, (a, b) -> a.getName().compareTo(b.getName()));
			for (final Method m : methods) {
				final int mod = m.getModifiers();
				if ((m.getName().startsWith("get") || m.getName().startsWith("to")) && m.getParameterCount() == 0 && !isStatic(mod) && m.getReturnType() != Class.class) {
					final String attributeName = m.getName().replaceFirst("^[a-z]+", "");
					final Object value = m.invoke(locale);
					LOGGER.info(attributeName + "=" + Objects.toString(value));
				}
			}
		} catch (final IllegalAccessException | InvocationTargetException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
