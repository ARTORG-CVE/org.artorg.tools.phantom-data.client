package org.artorg.tools.phantomData.client.table;

import java.util.List;

import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;

public interface IBaseColumns extends IPersonifiedColumns {

	default <T extends AbstractBaseEntity<T>> void
		createFilesColumns(List<AbstractColumn<T, ?>> columns) {
		columns.add(new FilterColumn<T, String>("Files", item -> item,
			path -> {
				if (path.getFiles().size()==0) return "";
				return Integer.toString(path.getFiles().size());
				}, (path, value) -> {}));
		
	}

}
