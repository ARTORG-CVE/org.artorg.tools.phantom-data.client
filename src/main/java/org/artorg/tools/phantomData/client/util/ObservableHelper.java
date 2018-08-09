package org.artorg.tools.phantomData.client.util;

import org.artorg.tools.phantomData.server.specification.DatabasePersistent;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class ObservableHelper<ITEM extends DatabasePersistent<ITEM, ID_TYPE>, ID_TYPE> {
	
	private final ITEM item;
	
	private final SpreadsheetCell cell;
	
	
	public ObservableHelper(ITEM item, SpreadsheetCell cell) {
		this.item = item;
		this.cell = cell;
	}

	public ITEM getItem() {
		return item;
	}

	public SpreadsheetCell getCell() {
		return cell;
	}

}
