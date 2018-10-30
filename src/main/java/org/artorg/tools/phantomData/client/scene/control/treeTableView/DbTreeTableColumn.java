package org.artorg.tools.phantomData.client.scene.control.treeTableView;

import org.artorg.tools.phantomData.server.beans.DbNode;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.text.Text;

public class DbTreeTableColumn extends TreeTableColumn<DbNode, String> {
	private double minAutosizeWidth = 0.0;
	private double prefAutosizeWidth = 0.0;
	private double maxAutosizeWidth = Double.MAX_VALUE;

	public DbTreeTableColumn(String columnName) {
		super(columnName);
		setPrefWidth(200);
	}

	public <T> void autoResizeWidth(ObservableList<TreeItem<T>> treeItems) {
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
