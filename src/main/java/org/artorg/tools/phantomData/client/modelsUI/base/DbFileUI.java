package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editors.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DbFileUI implements UIEntity<DbFile> {
	private static final Map<String, Image> iconMap = new HashMap<>();

	public Class<DbFile> getItemClass() {
		return DbFile.class;
	}

	@Override
	public String getTableName() {
		return "Files";
	}

	@Override
	public List<AbstractColumn<DbFile, ?>> createColumns(List<DbFile> items) {
		List<AbstractColumn<DbFile, ?>> columns = new ArrayList<>();
		ColumnCreator<DbFile, DbFile> creator = new ColumnCreator<>(getItemClass());
		AbstractFilterColumn<DbFile, ?> column;
		columns.add(creator.createColumn("", path -> {
			if (path.getExtension().isEmpty()) return null;
			if (iconMap.containsKey(path.getExtension()))
				return new ImageView(iconMap.get(path.getExtension()));
			else {
				Image icon = FxUtil.getFileIcon(path.getFile());
				iconMap.put(path.getExtension(), icon);
				return new ImageView(icon);
			}
		}));
		column = creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Extension", path -> path.getExtension(),
				(path, value) -> path.setExtension(value)));
		columns.add(creator.createFilterColumn("File Tags", path -> path.getFileTags().stream()
				.map(fileTag -> fileTag.getName()).collect(Collectors.joining(", "))));
//		columns.add(
//				new FilterColumn<>("Phantoms", path -> String.valueOf(path.getPhantoms().size())));
		ColumnUtils.createCountingColumn(getItemClass(), "Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(getItemClass(), columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<DbFile> createEditFactory() {
		return new DbFileEditFactoryController();
	}

}
