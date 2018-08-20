package org.artorg.tools.phantomData.client.table.multiSelectCombo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;

public class FilterBox extends MultiSelectCombo {

	private final List<Callable<String>> getters;
	
	public FilterBox(String name, List<Callable<String>> getters) {
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
		
		getters.stream().map(c -> {
			try {
				return c.call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}).filter(s -> !s.equals("")).distinct().forEach(s -> nodes.add(createCheckBoxItem(() -> s)));
		
		return nodes;
		
	}

}
