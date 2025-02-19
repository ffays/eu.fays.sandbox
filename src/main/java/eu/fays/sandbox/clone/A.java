package eu.fays.sandbox.clone;

import java.util.ArrayList;
import java.util.List;

public abstract class A implements Cloneable {

	private Integer number;
	
	private String text;

	private ArrayList<String> list = new ArrayList<>();

	public A(final int number, final String string, final List<String> list) {
		this.number = number;
		this.text = string;
		this.list.addAll(list);
	}
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}

	final void add(final String value) {
		list.add(value);
	}}
