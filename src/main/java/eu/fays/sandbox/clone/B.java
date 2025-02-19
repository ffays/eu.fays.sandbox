package eu.fays.sandbox.clone;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;

public class B extends A  {

	private Double decimal;
	
	public B(final Integer number, final String text, final Double decimal, final List<String> list) {
		super(number, text, list);
		this.decimal = decimal;
	}

	public Double getDecimal() {
		return decimal;
	}

	public void setDecimal(Double decimal) {
		this.decimal = decimal;
	}

	@Override
	public String toString() {
		return format("number: {0,number,0}, text:{1}, decimal:{2}, list:{3}", getNumber(), getText(), getDecimal(), getList());
	}

	/**
	 * Deep clone
	 * @return deep clone
	 */
	@SuppressWarnings("unchecked")
	public B copy() {
		try {
			final B result = (B) clone();
			result.setList((ArrayList<String>) getList().clone());
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
