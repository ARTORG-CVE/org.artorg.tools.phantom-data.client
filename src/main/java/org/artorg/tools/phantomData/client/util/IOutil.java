package org.artorg.tools.phantomData.client.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.artorg.tools.phantomData.client.DesktopFxBootApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class IOutil extends huma.io.IOutil {
	
	
	public static String getFileExt(String filename) {
        String ext = ".";
        int p = filename.lastIndexOf('.');
        if (p >= 0) {
            ext = filename.substring(p);
        }
        return ext.toLowerCase();
    }
	
	
	public static BufferedImage readAsBufferedImage(String path) {
		try {
			return ImageIO.read(DesktopFxBootApplication.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {}
		throw new IllegalArgumentException();
	}
	
	public static File readAsFile(String path) {
		try {
			File file = File.createTempFile("model", "stl");
			InputStream inputStream = DesktopFxBootApplication.class.getClassLoader().getResourceAsStream(path);
			FileUtils.copyInputStreamToFile(inputStream, file);
			return file;
		} catch (IOException e1) {};
		throw new IllegalArgumentException();
	}
	
	public static <T> T loadFXML(String path, Object controller) {
		FXMLLoader loader = new FXMLLoader(DesktopFxBootApplication.class.getClassLoader().getResource(path));
		loader.setController(controller);
		try {
			return loader.<T>load();
		} catch (IOException e) {}
		throw new IllegalArgumentException();
	}
	
	public static String readCSSstylesheet(String path) {
		return DesktopFxBootApplication.class.getClassLoader().getResource(path).toExternalForm();
	}
	
	public static Image readResourceAsImage(String path) {
		InputStream normalStream = readResourceAsStream(path);
		return new Image(normalStream);
	}
	
}
