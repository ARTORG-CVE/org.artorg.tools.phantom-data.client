package org.artorg.tools.phantomData.client.modelsUI.base.person;

import java.util.List;

import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.server.models.base.person.Gender;

public class GenderUI extends UIEntity<Gender> {

	@Override
	public Class<Gender> getItemClass() {
		return Gender.class;
	}

	@Override
	public String getTableName() {
		return "Genders";
	}

	@Override
	public List<AbstractColumn<Gender, ? extends Object>> createColumns(Table<Gender> table,
			List<Gender> items) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ItemEditor<Gender> createEditFactory() {
		throw new UnsupportedOperationException();
	}

}
