package org.artorg.tools.phantomData.client.modelsUI.base.property;

import org.artorg.tools.phantomData.client.modelUI.PropertyUI;
import org.artorg.tools.phantomData.server.models.base.property.DoubleProperty;
import org.artorg.tools.phantomData.server.models.base.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public class DoublePropertyUI extends PropertyUI<DoubleProperty,Double> {

	public Class<DoubleProperty> getItemClass() {
		return DoubleProperty.class;
	}

	@Override
	public String getTableName() {
		return "Double Properties";
	}

	@Override
	public String toString(Double value) {
		return value.toString();
	}

	@Override
	public Double fromString(String s) {
		return Double.valueOf(s);
	}

	@Override
	public DoubleProperty createProperty(PropertyField propertyField, Double value) {
		return new DoubleProperty(propertyField, value);
	}

	@Override
	public Node createValueNode() {
		return new TextField();
	}

	@Override
	public Double getValueFromNode(Node valueNode) {
		return Double.valueOf(((TextField)valueNode).getText());
	}

	@Override
	public void setValueToNode(Node valueNode, Double value) {
		((TextField)valueNode).setText(Double.toString(value));
	}

	@Override
	public Double getDefaultValue() {
		return 0.0;
	}

}
