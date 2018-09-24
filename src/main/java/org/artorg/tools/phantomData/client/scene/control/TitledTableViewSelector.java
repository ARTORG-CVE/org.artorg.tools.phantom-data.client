package org.artorg.tools.phantomData.client.scene.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpConnectorSpring;
import org.artorg.tools.phantomData.client.connectors.Connectors;
import org.artorg.tools.phantomData.client.scene.control.table.SplittedTableViewSelector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.client.util.Reflect;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TitledTableViewSelector extends TitledPane {
	private final TitledPane titledPane;
	private SplittedTableViewSelector<?> selector;
	
	{
		titledPane = this;
	}
	
	@SuppressWarnings("unchecked")
	public <ITEM extends DatabasePersistent, SUB_ITEM extends DatabasePersistent & Comparable<SUB_ITEM>> 
	TitledTableViewSelector(String title, ITEM item, Class<SUB_ITEM> cls) {
		HttpConnectorSpring<SUB_ITEM> connector = Connectors.getConnector(cls);
		Method[] methods = item.getClass().getMethods();
		List<Method>selectedMethods = Arrays.asList(methods).stream()
				.filter(m -> m.getParameterTypes().length == 0)
				.filter(m -> Collection.class.isAssignableFrom(m.getReturnType()))
				.filter(m -> Reflect.getGenericReturnTypeClass(m) == cls)
				.collect(Collectors.toList());
		
		if (selectedMethods.size() != 1) throw new IllegalArgumentException();
		Method selectedMethod = selectedMethods.get(0);
		
		Function<ITEM, Collection<SUB_ITEM>> subItemGetter2; 
		subItemGetter2 = i -> {
			try {
				return (Collection<SUB_ITEM>)(selectedMethod.invoke(item));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}; 
		
		AnchorPane pane = new AnchorPane();
		VBox vBox = new VBox();
		SplittedTableViewSelector<SUB_ITEM> selector = new SplittedTableViewSelector<SUB_ITEM>();
		selector.setSelectableItems(connector.readAllAsSet());
		selector.setSelectedItems(subItemGetter2.apply(item).stream().collect(Collectors.toSet()));
		selector.init();
		vBox.getChildren().add(selector);
		FxUtil.addToAnchorPane(pane, vBox);
		titledPane.setText(title);
		titledPane.setContent(pane);
		this.selector = selector;
	}
	
	public SplittedTableViewSelector<?> getSelector() {
		return this.selector;
	}

}
