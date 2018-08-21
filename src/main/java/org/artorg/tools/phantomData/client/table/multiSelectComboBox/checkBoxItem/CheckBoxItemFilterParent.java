package org.artorg.tools.phantomData.client.table.multiSelectComboBox.checkBoxItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.artorg.tools.phantomData.client.table.multiSelectComboBox.IMultiSelectComboBox;

import javafx.scene.image.Image;

public class CheckBoxItemFilterParent extends CheckBoxItem {
	private static final Image imgNormal, imgFilter;
	
	static {
		InputStream normalStream = null;
		try {
			normalStream = new FileInputStream(new File("src/main/resources/arrow.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imgNormal = new Image(normalStream);

		InputStream filterStream = null;
		try {
			filterStream = new FileInputStream(new File("src/main/resources/filter.png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imgFilter = new Image(filterStream);
	}
	
	public static Stream<CheckBoxItemFilterParent> streamParent(IMultiSelectComboBox multiSelectComboBox) {
		return multiSelectComboBox.getNodeStream().filter(n -> n instanceof CheckBoxItemFilterParent)
				.map(n -> ((CheckBoxItemFilterParent) n));
	}

	public static Image getImgnormal() {
		return imgNormal;
	}

	public static Image getImgfilter() {
		return imgFilter;
	}
	

}