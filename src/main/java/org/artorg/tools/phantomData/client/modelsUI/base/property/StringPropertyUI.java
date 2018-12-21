package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;
import org.artorg.tools.phantomData.server.models.base.property.StringProperty;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public class StringPropertyUI extends PropertyUI<StringProperty,String> {

	public Class<StringProperty> getItemClass() {
		return StringProperty.class;
	}

	@Override
	public String getTableName() {
		return "String Properties";
	}

	@Override
	protected String toString(String value) {
		return value;
	}

	@Override
	protected String fromString(String s) {
		return s;
	}

	@Override
	protected StringProperty createProperty(PropertyField propertyField, String value) {
		return new StringProperty(propertyField, value);
	}

	@Override
	protected Node createValueNode() {
		return new TextField();
	}

	@Override
	protected String getValueFromNode(Node valueNode) {
		return ((TextField)valueNode).getText();
	}

	@Override
	protected void setValueToNode(Node valueNode, String value) {
		((TextField)valueNode).setText(value);
	}

}
