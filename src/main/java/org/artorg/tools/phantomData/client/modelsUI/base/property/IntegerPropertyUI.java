package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
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
	public String toString(Integer value) {
		return value.toString();
	}

	@Override
	public Integer fromString(String s) {
		return Integer.valueOf(s);
	}

	@Override
	public IntegerProperty createProperty(PropertyField propertyField, Integer value) {
		return new IntegerProperty(propertyField, value);
	}

	@Override
	public Node createValueNode() {
		return new TextField();
	}

	@Override
	public Integer getValueFromNode(Node valueNode) {
		return Integer.valueOf(((TextField)valueNode).getText()); 
	}

	@Override
	public void setValueToNode(Node valueNode, Integer value) {
		((TextField)valueNode).setText(value.toString());
	}

	@Override
	public Integer getDefaultValue() {
		return 0;
	}

}
