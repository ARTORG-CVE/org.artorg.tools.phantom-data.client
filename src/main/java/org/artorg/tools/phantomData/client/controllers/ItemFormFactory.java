package org.artorg.tools.phantomData.client.controllers;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;

public class ItemFormFactory {
	
	public static AnchorPane createForm(Class<?> itemClass) {
		if (itemClass == Phantom.class) {
			return new AddPhantomController2().create();
		} else if (itemClass == AnnulusDiameter.class) {
			return wrapInScrollPane(new AddAnnulusDiameterController().loadFXML());
		} else if (itemClass == FabricationType.class) {
			return wrapInScrollPane(new AddFabricationTypeController().loadFXML());
		} else if (itemClass == LiteratureBase.class) {
			return wrapInScrollPane(new AddLiteratureBaseController().loadFXML());
		} else if (itemClass == PropertyField.class) {
			return wrapInScrollPane(new AddPropertyFieldController().loadFXML());
		}
		throw new IllegalArgumentException();
	}
	
	
	
	private static AnchorPane wrapInScrollPane(Node n) {
//		ScrollPane scrollPane = new ScrollPane();
		return (AnchorPane) n;
//		scrollPane.setContent(pane);
//		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
//		
//		scrollPane.setFitToHeight(true);
//		scrollPane.setFitToWidth(true);
//		return scrollPane;
//		return pane;
	}
	

}
