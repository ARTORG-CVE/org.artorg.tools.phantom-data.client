package org.artorg.tools.phantomData.client.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.DesktopSwingBootApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class IOutil extends huma.io.IOutil {
	
	public static BufferedImage readAsBufferedImage(String path) {
		try {
			return ImageIO.read(DesktopSwingBootApplication.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {}
		throw new IllegalArgumentException();
	}
	
	public static File readAsFile(String path) {
		try {
			File file = File.createTempFile("model", "stl");
			InputStream inputStream = DesktopSwingBootApplication.class.getClassLoader().getResourceAsStream(path);
			FileUtils.copyInputStreamToFile(inputStream, file);
			return file;
		} catch (IOException e1) {};
		throw new IllegalArgumentException();
	}
	
	public static <T> T loadFXML(String path, Object controller) {
		FXMLLoader loader = new FXMLLoader(DesktopSwingBootApplication.class.getClassLoader().getResource(path));
		loader.setController(controller);
		try {
			return loader.<T>load();
		} catch (IOException e) {}
		throw new IllegalArgumentException();
	}
	
	public static String readCSSstylesheet(String path) {
		return DesktopSwingBootApplication.class.getClassLoader().getResource(path).toExternalForm();
	}
	
	public static Image readResourceAsImage(String path) {
		InputStream normalStream = readResourceAsStream("img/arrow.png");
		return new Image(normalStream);
	}
	
}
