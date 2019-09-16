package eu.fays.sandbox.parametrizedtype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.IntStream;

public interface Table<D, C extends Column<D>> {

	@SuppressWarnings("unchecked")
	default Type parametrizedType(final int ordinal) {
		final Class<Table<D, C>> type = (Class<Table<D, C>>) getClass();
		final Class<?>[] interfaces = type.getInterfaces();
		final int index = IntStream.range(0, interfaces.length).filter(i -> interfaces[i] == Table.class).findFirst().orElseThrow(AssertionError::new);
		final ParameterizedType parametrizedType = (ParameterizedType) type.getGenericInterfaces()[index];
		final Type[] types = parametrizedType.getActualTypeArguments();
		return types[ordinal];
	}

	@SuppressWarnings("unchecked")
	default Class<D> dataType() {
		return (Class<D>) parametrizedType(0);
	}

	@SuppressWarnings("unchecked")
	default Class<C> columnType() {
		return (Class<C>) parametrizedType(1);
	}

}
