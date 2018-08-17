package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FilterBox extends MultiSelectCombo {

	private final List<Runnable> getters;
	
	public FilterBox(String name, List<Runnable> getters) {
		this.getters = getters;
		
	    Image imgNormal = null;
		try {
			InputStream normalStream = new FileInputStream(new File("src/main/resources/arrow.png"));
			imgNormal = new Image(normalStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Image imgFilter = null;
		try {
			InputStream filterStream = new FileInputStream(new File("src/main/resources/filter.png"));
			imgFilter = new Image(filterStream); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		super.setNodes(makeData());
		super.setImgNormal(imgNormal);
		super.setImgFilter(imgFilter);
		super.setPromptText(name);
		
		this.setStyle("-fx-background-color: transparent;");
	}

	private List<Node> makeData() {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(createCheckBoxAll());
		
		Separator separator = new Separator(Orientation.HORIZONTAL); 
		separator.setPrefHeight(1);
		nodes.add(separator);
		
		for (int i=1; i<10; i++) {
			final int j = i;
			nodes.add(createCheckBoxItem(() -> "Item " +j));
		}
			
		return nodes;
		
	}

}
