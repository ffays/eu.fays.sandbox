package eu.fays.sandbox.reflection;

import static java.util.Arrays.stream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Clone an XML object
 */
public interface ClonableTrait {
	/**
	 * Using Java reflection, provides all attributes of the given class having the {@link XmlElement} annotation
	 * @param type the given class
	 * @return the attributes.
	 */
	public static Set<Field> getXmlElementFieldS(final Class<?> type) {
		//
		assert type != null;
		assert stream(type.getAnnotations()).filter(a -> a.annotationType().equals(XmlType.class)).count() > 0L;
		//
		return getXmlElementFieldS(new LinkedHashSet<>(), type);
	}

	/**
	 * Using Java reflection, provides all attributes of the given class having the {@link XmlElement} annotation
	 * @param accumulator the field accumulator, will be modified and returned.
	 * @param type the given class
	 * @return the attributes.
	 */
	public static Set<Field> getXmlElementFieldS(final Set<Field> accumulator, final Class<?> type) {
		//
		assert accumulator != null;
		assert type != null;
		//
		if (type.getSuperclass() != null) {
			accumulator.addAll(getXmlElementFieldS(accumulator, type.getSuperclass()));
		}
		for (final Field field : type.getDeclaredFields()) {
			for (final Annotation annotation : field.getAnnotations()) {
				if (annotation.annotationType().equals(XmlElement.class) || annotation.annotationType().equals(XmlJavaTypeAdapter.class)) {
					accumulator.add(field);
					break;
				}
			}
		}
		return accumulator;
	}

	/**
	 * Creates a shallow copy of the given instance, with the attributes having the {@link XmlElement} annotation being copied.<br>
	 * Note: works only if all fields are declared as public
	 * @param <T> the type of the instance to be cloned
	 * @param instance the given instance to be cloned
	 * @return the shallow copy
	 */
	public static <T> T cloneWithXmlElementFields(final T instance) {
		try {
			@SuppressWarnings("unchecked")
			final Class<T> type = (Class<T>) instance.getClass();
			final T result = type.newInstance();
			for (final Field field : getXmlElementFieldS(type)) {
				final Object value = field.get(instance);
				if (value != null) {
					field.set(result, value);
				}
			}
			return result;
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new AssertionError(e.getMessage(), e);
		}
	}

	/**
	 * Creates a shallow copy of the given instance, with the attributes having the {@link XmlElement} annotation being copied.<br>
	 * Note: works only if all fields are declared as public, otherwise this method has to be copied inside the class having private/protected or package level fields
	 * @param <T> the type of the instance to be cloned
	 * @return the shallow copy
	 */
	default <T> T cloneWithXmlElementFields() {
		try {
			@SuppressWarnings("unchecked")
			final Class<T> type = (Class<T>) getClass();
			final T result = type.newInstance();
			for (final Field field : getXmlElementFieldS(type)) {
				final Object value = field.get(this);
				if (value != null) {
					field.set(result, value);
				}
			}
			return result;
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new AssertionError(e.getMessage(), e);
		}
	}
}
