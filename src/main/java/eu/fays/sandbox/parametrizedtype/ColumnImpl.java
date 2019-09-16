package eu.fays.sandbox.parametrizedtype;

import java.awt.geom.Point2D;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ColumnImpl extends Column<Point2D> {

	private ColumnImpl(int ordinal, String title) {
		super(ordinal, title);
	}

	@Override
	public Set<Column<Point2D>> columns() {
		return unmodifiableSet(new LinkedHashSet<>(asList(new ColumnImpl[] { new ColumnImpl(0, "X"), new ColumnImpl(1, "Y") })));
	}

	@Override
	public int hashCode() {
		return ordinal();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ColumnImpl) {
			return ordinal() == ((ColumnImpl) o).ordinal();
		}
		return false;
	}
}
