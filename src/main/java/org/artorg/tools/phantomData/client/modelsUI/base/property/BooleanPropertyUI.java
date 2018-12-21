package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.server.models.base.person.Person;
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
	protected String toString(Boolean value) {
		return value.toString();
	}

	@Override
	protected Boolean fromString(String s) {
		return Boolean.valueOf(s);
	}

	@Override
	protected BooleanProperty createProperty(PropertyField propertyField, Boolean value) {
		return new BooleanProperty(propertyField, value);
	}

	@Override
	protected Node createValueNode() {
		return new CheckBox();
	}

	@Override
	protected Boolean getValueFromNode(Node valueNode) {
		return ((CheckBox) valueNode).isSelected();
	}

	@Override
	protected void setValueToNode(Node valueNode, Boolean value) {
		((CheckBox) valueNode).setSelected(value);
	}

}
