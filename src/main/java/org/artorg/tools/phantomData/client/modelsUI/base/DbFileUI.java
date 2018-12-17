package org.artorg.tools.phantomData.client.modelsUI.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.Column;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.editor.ItemEditFactoryController;
import org.artorg.tools.phantomData.client.editors.DbFileEditFactoryController;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.util.ColumnUtils;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.models.base.DbFile;

public class DbFileUI implements UIEntity<DbFile> {

	@Override
	public Class<DbFile> getItemClass() {
		return DbFile.class;
	}

	@Override
	public String getTableName() {
		return "Files";
	}

	@Override
	public List<AbstractColumn<DbFile, ?>> createColumns() {
		List<AbstractColumn<DbFile, ?>> columns = new ArrayList<>();
		columns.add(new Column<>("", path -> FxUtil.getFxFileIcon(path.getFile())));
		columns.add(new FilterColumn<>("Name", path -> path.getName(),
				(path, value) -> path.setName(value)));
		columns.add(new FilterColumn<>("Extension", path -> path.getExtension(),
				(path, value) -> path.setExtension(value)));
		columns.add(new FilterColumn<>("File Tags", path -> path.getFileTags().stream()
				.map(fileTag -> fileTag.getName()).collect(Collectors.joining(", "))));
		columns.add(
				new FilterColumn<>("Phantoms", path -> String.valueOf(path.getPhantoms().size())));
		ColumnUtils.createCountingColumn("Notes", columns, item -> item.getNotes());
		ColumnUtils.createPersonifiedColumns(columns);
		return columns;
	}

	@Override
	public ItemEditFactoryController<DbFile> createEditFactory() {
		return new DbFileEditFactoryController();
	}

}
