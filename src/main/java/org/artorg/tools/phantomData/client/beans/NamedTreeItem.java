package org.artorg.tools.phantomData.client.beans;

public class NamedTreeItem {
	private final Object value;
	private final String name;
	private final String type;

	public NamedTreeItem(Object value, String name, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}