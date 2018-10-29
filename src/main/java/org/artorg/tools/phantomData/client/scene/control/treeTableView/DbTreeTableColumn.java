package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.function.Function;

import org.artorg.tools.phantomData.server.beans.DbProperty;
import org.artorg.tools.phantomData.server.model.specification.AbstractBaseEntity;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.text.Text;

public class DbTreeTableColumn extends TreeTableColumn<Object, String> {
	private double minAutosizeWidth = 0.0;
	private double prefAutosizeWidth = 0.0;
	private double maxAutosizeWidth = Double.MAX_VALUE;

	public DbTreeTableColumn(String columnName,
		Function<AbstractBaseEntity<?>, String> mapper) {
		this(columnName, mapper, item -> "");
	}

	public DbTreeTableColumn(String columnName,
		Function<AbstractBaseEntity<?>, String> mapper,
		Function<Object, String> orMapper) {
		this(columnName);

		setCellValueFactory(
			(TreeTableColumn.CellDataFeatures<Object, String> param) -> {
				Object item = param.getValue().getValue();
				if (item == null)
					return new ReadOnlyStringWrapper("null");
				
//				if (item instanceof PropertyDescriptor) {
//					((PropertyDescriptor)item).
//				}
				
//				if (item instanceof NamedValue) {
//					try {
//					String name = ((NamedValue)item).getValue().toString();
//					return new ReadOnlyStringWrapper(name);
//					} catch (Exception e) {}
//					return new ReadOnlyStringWrapper("ERROR");
//				}
				
				
				if (item instanceof AbstractBaseEntity) {
					try {
						return new ReadOnlyStringWrapper(mapper
							.apply(((AbstractBaseEntity<?>) item)));
					} catch (NullPointerException e) {
					}
					return new ReadOnlyStringWrapper("null");
				} else if (item instanceof List) 
					return new ReadOnlyStringWrapper("----LIST-----");
				else
					return new ReadOnlyStringWrapper(orMapper.apply(item));
			});
	}

	public DbTreeTableColumn(String columnName) {
		super(columnName);
		setPrefWidth(200);
	}

	public void autoResizeWidth(ObservableList<TreeItem<Object>> treeItems) {
		Text t = new Text(getText());
		double max = t.getLayoutBounds().getWidth() + 45.0;
		for (int i = 0; i < treeItems.size(); i++) {
			if (getCellData(i) != null) {
				t = new Text(getCellData(i).toString());
				double calcwidth = t.getLayoutBounds().getWidth() + 10;
				if (calcwidth > max)
					max = calcwidth;
			}
		}

		if (max < prefAutosizeWidth) {
			if (max < minAutosizeWidth)
				setPrefWidth(minAutosizeWidth);
			else
				setPrefWidth(prefAutosizeWidth);
		} else {
			setPrefWidth(Math.min(max, maxAutosizeWidth));
		}
	}

	public double getMinAutosizeWidth() {
		return minAutosizeWidth;
	}

	public void setMinAutosizeWidth(double autosizeMinWidth) {
		this.minAutosizeWidth = autosizeMinWidth;
		this.prefAutosizeWidth = Math.max(autosizeMinWidth, prefAutosizeWidth);
		this.maxAutosizeWidth = Math.max(autosizeMinWidth, maxAutosizeWidth);
	}

	public double getPrefAutosizeWidth() {
		return prefAutosizeWidth;
	}

	public void setPrefAutosizeWidth(double autosizePrefWidth) {
		this.prefAutosizeWidth = autosizePrefWidth;
		this.minAutosizeWidth = Math.min(minAutosizeWidth, autosizePrefWidth);
		this.maxAutosizeWidth = Math.max(autosizePrefWidth, maxAutosizeWidth);
	}

	public double getMaxAutosizeWidth() {
		return maxAutosizeWidth;
	}

	public void setMaxAutosizeWidth(double autosizeMaxWidth) {
		this.maxAutosizeWidth = autosizeMaxWidth;
		this.minAutosizeWidth = Math.min(minAutosizeWidth, autosizeMaxWidth);
		this.prefAutosizeWidth = Math.min(prefAutosizeWidth, autosizeMaxWidth);
	}

}
