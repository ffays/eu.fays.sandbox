package eu.fays.sandbox.parametrizedtype;

import java.util.Set;

public abstract class Column<D> {
	private final int _ordinal;
	private final String _title;
	
	
	public abstract Set<Column<D>> columns();
	
	public int ordinal() {
		return _ordinal;
	}
	
	public String title() {
		return _title;
	}
	
	protected Column(final int ordinal, final String title) {
		_ordinal = ordinal;
		_title = title;
	}
}
