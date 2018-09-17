package org.artorg.tools.phantomData.client.controller;

import org.artorg.tools.phantomData.client.controllers.editTable.AddAnnulusDiameterController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddBooleanPropertyController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFabricationTypeController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFileController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddFileTypeController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddLiteratureBaseController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddPhantomController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddPropertyFieldController;
import org.artorg.tools.phantomData.client.controllers.editTable.AddSpecialController;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.FileType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.PhantomFile;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.model.property.BooleanProperty;
import org.artorg.tools.phantomData.server.model.property.PropertyField;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class ItemFormFactory {
	
	public static AnchorPane createForm(Class<?> itemClass) {
		if (itemClass == Phantom.class) {
			return new AddPhantomController().create(null);
		} else if (itemClass == AnnulusDiameter.class) {
			return new AddAnnulusDiameterController().create(null);
		} else if (itemClass == FabricationType.class) {
			return new AddFabricationTypeController().create(null);
		} else if (itemClass == LiteratureBase.class) {
			return new AddLiteratureBaseController().create(null);
		} else if (itemClass == PropertyField.class) {
			return new AddPropertyFieldController().create(null);
		} else if (itemClass == Special.class) {
			return new AddSpecialController().create(null);
		} else if (itemClass == BooleanProperty.class) {
			return new AddBooleanPropertyController().create(null);
		} else if (itemClass == PhantomFile.class) {
			return new AddFileController().create(null);
		} else if (itemClass == FileType.class) {
			return new AddFileTypeController().create(null);
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
