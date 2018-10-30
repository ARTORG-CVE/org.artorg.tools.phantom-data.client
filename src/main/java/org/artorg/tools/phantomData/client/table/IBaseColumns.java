package org.artorg.tools.phantomData.client.table;

import java.text.SimpleDateFormat;
import java.util.List;

import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;

public interface IBaseColumns {

	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	default <T extends AbstractBaseEntity<T>> void
		createBaseColumns(List<AbstractColumn<T>> columns) {
		columns.add(new FilterColumn<T>("Last modified", item -> item,
			path -> format.format(path.getDateLastModified()), (path, value) -> {}));
		columns.add(new FilterColumn<T>("Changed By", item -> item.getChanger(),
			path -> path.getSimpleAcademicName(), (path, value) -> {}));
		columns.add(new FilterColumn<T>("Added", item -> item,
			path -> format.format(path.getDateAdded()), (path, value) -> {}));
		columns.add(new FilterColumn<T>("Creator", item -> item.getCreator(),
			path -> path.getSimpleAcademicName(), (path, value) -> {}));
	}

}
