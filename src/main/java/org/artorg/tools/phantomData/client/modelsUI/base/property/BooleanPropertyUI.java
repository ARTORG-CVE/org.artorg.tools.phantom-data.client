package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.server.models.base.property.BooleanProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class BooleanPropertyUI extends PropertyUI<BooleanProperty, Boolean> {

	public Class<BooleanProperty> getItemClass() {
		return BooleanProperty.class;
	}

	@Override
	public String getTableName() {
		return "Boolean Properties";
	}

	@Override
	public String toString(Boolean value) {
		return value.toString();
	}

	@Override
	public Boolean fromString(String s) {
		return Boolean.valueOf(s);
	}

	@Override
	public BooleanProperty createProperty(PropertyField propertyField, Boolean value) {
		return new BooleanProperty(propertyField, value);
	}

	@Override
	public Node createValueNode() {
		return new CheckBox();
	}

	@Override
	public Boolean getValueFromNode(Node valueNode) {
		return ((CheckBox) valueNode).isSelected();
	}

	@Override
	public void setValueToNode(Node valueNode, Boolean value) {
		((CheckBox) valueNode).setSelected(value);
	}

	@Override
	public Boolean getDefaultValue() {
		return false;
	}

}
