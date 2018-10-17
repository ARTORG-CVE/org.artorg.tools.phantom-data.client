package org.artorg.tools.phantomData.client.scene.control;

import java.util.List;
import java.util.function.Supplier;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SmartTabPane {
	private TabPane tabPane;
	private Node contentNode;
	private Supplier<ObservableList<Node>> parentItemsSupplier;
	private boolean initialized = false;

	public SmartTabPane(Supplier<ObservableList<Node>> parentItemsSupplier) {
		this.parentItemsSupplier = parentItemsSupplier;
		this.tabPane = new TabPane();
	}

	public void init() {
		if (!initialized) {
			Tab tab = getSelectedTab(getTabPane());
			contentNode = tab.getContent();
			getTabPane().getTabs()
				.addListener(createListChangeListener(getTabPane(), tab));
			initialized = true;
		}
	}

	public void showHeader() {
		if (!initialized)
			init();
		Tab tab = getSelectedTab(getTabPane());
		showHeader(getTabPane(), tab);
	}

	public void removeHeader() {
		if (!initialized)
			init();
		Tab tab = getSelectedTab(getTabPane());
		removeHeader(getTabPane(), tab);
	}

	private Tab getSelectedTab(TabPane tabPane) {
		try {
			return tabPane.getSelectionModel().getSelectedItem();
		} catch (Exception e) {
		}
		if (tabPane.getTabs().size() != 0)
			return tabPane.getTabs().get(0);
		throw new NullPointerException();
	}

	private void removeHeader(TabPane tabPane, Tab tab) {
		if (tabPane.getTabs().size() == 1) {
			ObservableList<Node> nodes = parentItemsSupplier.get();
			for (int i = 0; i < nodes.size(); i++)
				if (nodes.get(i) == tabPane) {
					int index = nodes.indexOf(tabPane);
					nodes.remove(index);
					contentNode = tabPane.getTabs().get(0).getContent();
					nodes.add(index, contentNode);
				}
		}
	}

	private void showHeader(TabPane tabPane, Tab tab) {
		ObservableList<Node> nodes2 = parentItemsSupplier.get();
		int selectedTabIndex = getTabPane().getSelectionModel()
			.getSelectedIndex();
		for (int i = 0; i < nodes2.size(); i++)
			if (nodes2.get(i) == contentNode) {
				int index = nodes2.indexOf(contentNode);
				nodes2.remove(index);

				TabPane tabPane2 = new TabPane();
				setTabPane(tabPane2);
				nodes2.add(index, tabPane2);
				List<Tab> tabs = tabPane.getTabs();
				for (int j = 0; j < tabs.size(); j++) {
					tabPane2.getTabs().add(tabs.get(j));
					tabPane2.getSelectionModel().select(j);
				}
				tabPane2.getSelectionModel().select(selectedTabIndex);
				tabPane2.getTabs()
					.addListener(createListChangeListener(tabPane2, tab));
			}
	}

	private ListChangeListener<Tab> createListChangeListener(TabPane tabPane,
		Tab tab) {
		ListChangeListener<Tab> changeListener = new ListChangeListener<Tab>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onChanged(Change<? extends Tab> c) {
				if (c.next()) {
					if (c.wasAdded()) {
						showHeader(tabPane, tab);
						List<Tab> tabs = (List<Tab>) c.getAddedSubList();
						getTabPane().getSelectionModel()
							.select(tabs.get(tabs.size() - 1));
					} else if (c.wasRemoved()) {
						removeHeader(tabPane, tab);
					}
				}
				getTabPane().getSelectionModel()
					.select(getTabPane().getTabs().size() - 1);

			}
		};
		return changeListener;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	private void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

}
