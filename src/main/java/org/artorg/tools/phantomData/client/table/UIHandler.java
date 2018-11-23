package org.artorg.tools.phantomData.client.table;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.beans.EntityBeanInfo;
import org.artorg.tools.phantomData.client.scene.control.Scene3D;
import org.artorg.tools.phantomData.client.scene.control.SmartTabPane;
import org.artorg.tools.phantomData.server.model.base.DbFile;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;
import org.artorg.tools.phantomData.server.model.specification.NameGeneratable;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

public class UIHandler {
	private static final ObservableList<ObservableList<SmartTabPane>> uiMatrix;
	
	static {
		uiMatrix = FXCollections.observableArrayList();
		
	}
	
	private static <ITEM extends DbPersistent<ITEM, ?>> void show3dInViewer(ITEM item) {
		if (viewerTabPane.getTabPane().getTabs().size() > 0) {
			Tab tab = viewerTabPane.getTabPane().getSelectionModel().getSelectedItem();

			if (show3dInViewer(item, tab) && item instanceof NameGeneratable)
				tab.setText(((NameGeneratable) item).toName());
		}
	}
	
	private static <ITEM extends DbPersistent<ITEM, ?>> boolean show3dInViewer(ITEM item,
		Tab tab) {
		if (tab != null) {
			if (item instanceof DbFile)
				return show3dInViewer(((DbFile) item).getFile(), tab);
			else {
				File file = get3dFile(item);
				if (file != null) return show3dInViewer(file, tab);
			}
		}
		return false;
	}
	
	private static <ITEM extends DbPersistent<ITEM, ?>> boolean show3dInViewer(File file,
		Tab tab) {
		Scene3D newScene3d = new Scene3D();
		newScene3d.loadFile(file);
		tab.setContent(newScene3d);
		return true;
	}
	
	private static <ITEM extends DbPersistent<ITEM, ?>> File get3dFile(ITEM item) {
		List<DbFile> files = getFiles(item);
		if (files != null && !files.isEmpty()) {
			Optional<DbFile> optionalFile = files.stream()
				.filter(dbFile -> dbFile.getExtension().equals("stl")).findFirst();
			if (optionalFile.isPresent()) return optionalFile.get().getFile();
		}
		return null;
	}
	
	private static <ITEM extends DbPersistent<ITEM, ?>> List<DbFile> getFiles(ITEM item) {
		if (item == null) return new ArrayList<DbFile>();

		if (item instanceof AbstractBaseEntity)
			return ((AbstractBaseEntity<?>) item).getFiles();

		List<DbFile> files = null;
		try {
			files = getValue(item, "files");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (files == null) return new ArrayList<DbFile>();

		return files;
	}
	
	@SuppressWarnings("unchecked")
	private static <ITEM extends DbPersistent<ITEM, ?>, T> T getValue(ITEM item, String name) {
		EntityBeanInfo beanInfo = Main.getBeaninfos().getEntityBeanInfo(item.getClass());
		List<PropertyDescriptor> descriptors =
			beanInfo.getAllPropertyDescriptors().stream()
				.filter(d -> d.getName().equals("files")).collect(Collectors.toList());

		if (descriptors.size() == 0) return null;
		if (descriptors.size() != 1) throw new IllegalArgumentException();

		T value = null;
		try {
			value = (T) descriptors.get(0).getReadMethod().invoke(item);
		} catch (IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e) {
			e.printStackTrace();
		}

		return value;
	}
	

}
