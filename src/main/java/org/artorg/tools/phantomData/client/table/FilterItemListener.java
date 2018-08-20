package org.artorg.tools.phantomData.client.table;

import java.util.List;

@FunctionalInterface
public interface FilterItemListener {
	
	void changed(List<String> newValues);

}
