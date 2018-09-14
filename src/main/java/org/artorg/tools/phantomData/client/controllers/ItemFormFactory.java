package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.Node;

public class ItemFormFactory {
	
	public static Node createForm(Class<?> itemClass) {
		if (itemClass == Phantom.class) {
			return new AddPhantomController().loadFXML();
		} else if (itemClass == AnnulusDiameter.class) {
			return new AddAnnulusDiameterController().loadFXML();
		} else if (itemClass == FabricationType.class) {
			return new AddFabricationTypeController().loadFXML();
		} else if (itemClass == LiteratureBase.class) {
			return new AddLiteratureBaseController().loadFXML();
		} else if (itemClass == PropertyField.class) {
			return new AddPropertyFieldController().loadFXML();
		}
		throw new IllegalArgumentException();
	}
	

}
