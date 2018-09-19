package org.artorg.tools.phantomData.client.controllers.editTable;

import java.util.List;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.controller.AddEditController;
import org.artorg.tools.phantomData.client.controller.PropertyEntry;
import org.artorg.tools.phantomData.server.model.LiteratureBase;

import javafx.scene.control.TextField;

public class AddLiteratureBaseController extends AddEditController<LiteratureBase, Integer> {
	private TextField textFieldShortcut;
	private TextField textFieldValue;

	{
		textFieldShortcut = new TextField();
		textFieldValue = new TextField();
	}
	
	@Override
	protected HttpConnectorSpring<LiteratureBase, Integer> getConnector() {
		return LiteratureBaseConnector.get();
	}
	
	@Override
	protected void addPropertyEntries(List<PropertyEntry> entries) {
		entries.add(new PropertyEntry("Shortcut", textFieldShortcut));
		entries.add(new PropertyEntry("Name", textFieldValue));
	}

	@Override
	protected void setTemplate(LiteratureBase item) {
		textFieldShortcut.setText(item.getShortcut());
		textFieldValue.setText(item.getValue());
	}

	@Override
	public LiteratureBase createItem() {
		String shortcut = textFieldShortcut.getText();
		String value = textFieldValue.getText();
		return new LiteratureBase(shortcut, value);
	}

}