package org.artorg.tools.phantomData.client.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import static org.artorg.tools.phantomData.server.io.IOutil.*;

public class FxUtil {
	private static Class<?> mainClass;
	
	static {
		mainClass = null;
	}
	
	public static void setMainFxClass(Class<?> mainClass) {
		if (FxUtil.mainClass!=null) throw new UnsupportedOperationException();
		FxUtil.mainClass = mainClass;
	}
	
	public static <T> T loadFXML(String path, Object controller) {
		FXMLLoader loader = new FXMLLoader(getMainClass().getClassLoader().getResource(path));
		loader.setController(controller);
		try {
			return loader.<T>load();
		} catch (IOException e) {}
		throw new IllegalArgumentException();
	}
	
	public static String readCSSstylesheet(String path) {
		return readResource(path).toExternalForm();
	}
	
	public static Class<?> getMainClass() {
		if (FxUtil.mainClass == null) 
			throw new IllegalArgumentException();
		return FxUtil.mainClass;
	}
	
	public static void addToAnchorPane(AnchorPane parentPane, Node child) {
		parentPane.getChildren().add(child);
    	AnchorPane.setBottomAnchor(child, 0.0);
    	AnchorPane.setLeftAnchor(child, 0.0);
    	AnchorPane.setRightAnchor(child, 0.0);
    	AnchorPane.setTopAnchor(child, 0.0);
	}

}
