package org.artorg.tools.phantomData.client.controller;

import java.util.function.BiFunction;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.control.TextField;

public abstract class AddEditStringStringController<ITEM extends DatabasePersistent<ID_TYPE>, ID_TYPE> extends AddEditController<ITEM, ID_TYPE> {
	private TextField textFieldRow1;
	private TextField textFieldRow2;
	
	{
		textFieldRow1 = new TextField();
		textFieldRow2 = new TextField();
	}
	
	public AddEditStringStringController(String name1, String name2) {
		super.addProperty(name1, textFieldRow1);
		super.addProperty(name2, textFieldRow2);
		
		super.create();
	}
	
	public abstract BiFunction<String,String,ITEM> getItemConstructor();
	
	@Override
	public ITEM createItem() {
		String shortcut = textFieldRow1.getText();
		String name = textFieldRow2.getText();
		
		return getItemConstructor().apply(shortcut, name);
	}

}
