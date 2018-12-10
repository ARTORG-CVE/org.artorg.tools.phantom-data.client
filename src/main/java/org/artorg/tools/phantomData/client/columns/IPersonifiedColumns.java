package org.artorg.tools.phantomData.client.columns;

import java.text.SimpleDateFormat;
import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.server.model.specification.AbstractPersonifiedEntity;

public interface IPersonifiedColumns {

	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	default <T extends AbstractPersonifiedEntity<T>> void
		createPersonifiedColumns(List<AbstractColumn<T,?>> columns) {
		columns.add(new FilterColumn<T,String>("Last modified", item -> item,
			path -> format.format(path.getDateLastModified()), (path, value) -> {}));
		columns.add(new FilterColumn<T,String>("Changed By", item -> item.getChanger(),
			path -> path.getSimpleAcademicName(), (path, value) -> {}));
		columns.add(new FilterColumn<T,String>("Added", item -> item,
			path -> format.format(path.getDateAdded()), (path, value) -> {}));
		columns.add(new FilterColumn<T,String>("Created By", item -> item.getCreator(),
			path -> path.getSimpleAcademicName(), (path, value) -> {}));
	}

}
