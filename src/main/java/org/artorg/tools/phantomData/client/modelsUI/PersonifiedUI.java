package org.artorg.tools.phantomData.client.modelsUI;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractPersonifiedEntity;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PersonifiedUI extends UIEntity<AbstractPersonifiedEntity> {

	@Override
	public Class<AbstractPersonifiedEntity> getItemClass() {
		return AbstractPersonifiedEntity.class;
	}

	@Override
	public String getTableName() {
		return "Personified items";
	}

	@Override
	public List<AbstractColumn<AbstractPersonifiedEntity, ? extends Object>> createColumns(
			Table<AbstractPersonifiedEntity> table, List<AbstractPersonifiedEntity> items) {
		List<AbstractColumn<AbstractPersonifiedEntity, ?>> columns = new ArrayList<>();
		ColumnCreator<AbstractPersonifiedEntity, AbstractPersonifiedEntity> creator =
				new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Type", path -> path.getClass().getSimpleName()));
		columns.add(creator.createFilterColumn("Name", path -> path.toName()));
		createPersonifiedColumns(table, columns);
		columns.add(creator.createFilterColumn("Id", path -> path.getId().toString()));
		return columns;
	}

	@Override
	public ItemEditor<AbstractPersonifiedEntity> createEditFactory() {
		return new ItemEditor<AbstractPersonifiedEntity>(AbstractPersonifiedEntity.class) {

			@Override
			public void onShowingCreateMode(AbstractPersonifiedEntity item) {
				ItemEditor<AbstractPersonifiedEntity> editor =
						(ItemEditor<AbstractPersonifiedEntity>) Main.getUIEntity(item.getClass())
								.createEditFactory();
				this.getChildren().clear();
				FxUtil.addToPane(this, editor);
				editor.showCreateMode(item);
			}

			@Override
			public void onShowingEditMode(AbstractPersonifiedEntity item) {
				ItemEditor<AbstractPersonifiedEntity> editor =
						(ItemEditor<AbstractPersonifiedEntity>) Main.getUIEntity(item.getClass())
								.createEditFactory();
				this.getChildren().clear();
				FxUtil.addToPane(this, editor);
				editor.showEditMode(item);
			}

		};
	}

}
