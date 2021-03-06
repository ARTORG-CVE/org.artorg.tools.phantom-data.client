package org.artorg.tools.phantomData.client.modelsUI.base.property;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AbstractProperty;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PropertiesUI extends UIEntity<AbstractProperty> {

	@Override
	public Class<AbstractProperty> getItemClass() {
		return AbstractProperty.class;
	}

	@Override
	public String getTableName() {
		return "Properties";
	}

	@Override
	public List<AbstractColumn<AbstractProperty, ? extends Object>>
			createColumns(Table<AbstractProperty> table, List<AbstractProperty> items) {
		List<AbstractColumn<AbstractProperty, ?>> columns = new ArrayList<>();
		ColumnCreator<AbstractProperty, AbstractProperty> creator = new ColumnCreator<>(table);
		columns.add(creator.createFilterColumn("Property Type",
				path -> path.getClass().getSimpleName(), (path, value) -> {}));
		columns.add(creator.createFilterColumn("Entity Type",
				path -> path.getPropertyField().getEntityType(), (path, value) -> {}));
		columns.add(
				creator.createFilterColumn("Field Name", path -> path.getPropertyField().getName(),
						(path, value) -> path.getPropertyField().setName(value)));
		columns.add(creator.createFilterColumn("Value", path -> String.valueOf(path.getValue()),
				(path, value) -> path
						.setValue(Main.getPropertyUIEntity(path.getClass()).fromString(value))));
		createPersonifiedColumns(table, columns);
		return columns;
	}

	@Override
	public ItemEditor<AbstractProperty> createEditFactory() {
		return new ItemEditor<AbstractProperty>(AbstractProperty.class) {

			@Override
			public void onShowingCreateMode(Class<? extends AbstractProperty> beanClass) {
				ItemEditor<AbstractProperty> editor = (ItemEditor<AbstractProperty>) Main
						.getUIEntity(beanClass).createEditFactory();
				this.getChildren().clear();
				FxUtil.addToPane(this, editor);
				editor.showCreateMode();
			}

			@Override
			public void onShowingEditMode(AbstractProperty item) {
				ItemEditor<AbstractProperty> editor = (ItemEditor<AbstractProperty>) Main
						.getUIEntity(item.getClass()).createEditFactory();
				this.getChildren().clear();
				FxUtil.addToPane(this, editor);
				editor.showEditMode(item);
			}

		};

	}

}
