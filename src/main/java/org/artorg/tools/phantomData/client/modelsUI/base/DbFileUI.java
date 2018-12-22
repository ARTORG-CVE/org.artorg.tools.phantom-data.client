package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editors.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;

public class DbFileUI implements UIEntity<DbFile> {

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
		columns.add(creator.createFilterColumn("", path -> FxUtil.getFxFileIcon(path.getFile())));
		columns.add(creator.createFilterColumn("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
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
