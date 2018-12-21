package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.server.models.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.models.base.property.IntegerProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public class IntegerPropertyUI extends PropertyUI<IntegerProperty,Integer> {


	public Class<IntegerProperty> getItemClass() {
		return IntegerProperty.class;
	}

	@Override
	public String getTableName() {
		return "Integer Properties";
	}

	@Override
	protected String toString(Integer value) {
		return value.toString();
	}

	@Override
	protected Integer fromString(String s) {
		return Integer.valueOf(s);
	}

	@Override
	protected IntegerProperty createProperty(PropertyField propertyField, Integer value) {
		return new IntegerProperty(propertyField, value);
	}

	@Override
	protected Node createValueNode() {
		return new TextField();
	}

	@Override
	protected Integer getValueFromNode(Node valueNode) {
		return Integer.valueOf(((TextField)valueNode).getText()); 
	}

	@Override
	protected void setValueToNode(Node valueNode, Integer value) {
		((TextField)valueNode).setText(value.toString());
	}

}
