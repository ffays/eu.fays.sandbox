package eu.fays.sandbox.parametrizedtype;

import java.awt.geom.Point2D;

public class TableImpl implements Table<Point2D, ColumnImpl> {

	public TableImpl() {
		
	}
	public static void main(String[] args) {
		TableImpl table = new TableImpl();
		
		System.out.println(table.columnType());
		System.out.println(table.dataType());
	}
}
