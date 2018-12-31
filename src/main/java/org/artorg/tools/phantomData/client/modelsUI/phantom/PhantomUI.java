package org.artorg.tools.phantomData.client.modelsUI.phantom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.Main;
import org.artorg.tools.phantomData.client.column.AbstractColumn;
import org.artorg.tools.phantomData.client.column.ColumnCreator;
import org.artorg.tools.phantomData.client.column.FilterColumn;
import org.artorg.tools.phantomData.client.connector.Connectors;
import org.artorg.tools.phantomData.client.connector.ICrudConnector;
import org.artorg.tools.phantomData.client.editor.AbstractEditor;
import org.artorg.tools.phantomData.client.editor.Creator;
import org.artorg.tools.phantomData.client.editor.IPropertyNode;
import org.artorg.tools.phantomData.client.editor.ItemEditor;
import org.artorg.tools.phantomData.client.editor.PropertyGridPane;
import org.artorg.tools.phantomData.client.exceptions.InvalidUIInputException;
import org.artorg.tools.phantomData.client.exceptions.NoUserLoggedInException;
import org.artorg.tools.phantomData.client.exceptions.PostException;
import org.artorg.tools.phantomData.client.modelUI.UIEntity;
import org.artorg.tools.phantomData.client.modelsUI.phantom.PhantominaUI.PhantominaEditor;
import org.artorg.tools.phantomData.client.table.Table;
import org.artorg.tools.phantomData.client.util.StreamUtils;
import org.artorg.tools.phantomData.server.models.base.DbFile;
import org.artorg.tools.phantomData.server.models.measurement.Measurement;
import org.artorg.tools.phantomData.server.models.phantom.Manufacturing;
import org.artorg.tools.phantomData.server.models.phantom.Phantom;
import org.artorg.tools.phantomData.server.models.phantom.Phantomina;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PhantomUI extends UIEntity<Phantom> {

	public Class<Phantom> getItemClass() {
		return Phantom.class;
	}

	@Override
	public String getTableName() {
		return "Phantoms";
	}

	@Override
	public List<AbstractColumn<Phantom, ?>> createColumns(Table<Phantom> table,
			List<Phantom> items) {
		List<AbstractColumn<Phantom, ?>> columns = new ArrayList<>();
		FilterColumn<Phantom, ?, ?> column;
		ColumnCreator<Phantom, Phantom> creator = new ColumnCreator<>(table);
		ColumnCreator<Phantom, Phantomina> creatorP =
				new ColumnCreator<>(table, item -> item.getPhantomina());
		columns.add(creator.createFilterColumn("", path -> {
			Rectangle coloredStatusBox = new Rectangle();
			coloredStatusBox.setWidth(8.0);
			coloredStatusBox.setHeight(8.0);
			HBox hBox = new HBox();
			coloredStatusBox = new Rectangle();
			coloredStatusBox.setWidth(8.0);
			coloredStatusBox.setHeight(8.0);
			HBox.setMargin(coloredStatusBox, new Insets(0, 5, 0, 5));
			hBox.getChildren().addAll(coloredStatusBox);
			hBox.setAlignment(Pos.CENTER_LEFT);
			if (path.isViable()) coloredStatusBox.setFill(Color.GREEN);
			else
				coloredStatusBox.setFill(Color.RED);
			return hBox;
		}));
		column = creator.createFilterColumn("PID", path -> path.getProductId(),
				(path, value) -> path.setProductId(value));
		column.setAscendingSortComparator(
				(p1, p2) -> Phantomina.comparePid(p1.getProductId(), p2.getProductId()));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creatorP.createFilterColumn("Annulus [mm]",
				path -> String.valueOf(path.getAnnulusDiameter().getValue()),
				(path, value) -> path.getAnnulusDiameter().setValue(Double.valueOf(value))));
		columns.add(
				creatorP.createFilterColumn("Type", path -> path.getFabricationType().getValue(),
						(path, value) -> path.getFabricationType().setValue(value)));
		columns.add(creatorP.createFilterColumn("Literature",
				path -> path.getLiteratureBase().getValue(),
				(path, value) -> path.getLiteratureBase().setValue(value)));
		columns.add(creatorP.createFilterColumn("Special", path -> path.getSpecial().getShortcut(),
				(path, value) -> path.getSpecial().setShortcut(value)));
		column = creator.createFilterColumn("Number", path -> String.valueOf(path.getNumber()),
				(path, value) -> path.setNumber(Integer.valueOf(value)));
		column.setItemsFilter(false);
		columns.add(column);
		columns.add(creator.createFilterColumn("Manufacturing",
				path -> path.getManufacturing().getName(),
				(path, value) -> path.getManufacturing().setName(value)));
		columns.add(creator.createFilterColumn("Material", path -> path.getMaterial().getName(),
				(path, value) -> path.getMaterial().setName(value)));
		columns.add(
				creator.createFilterColumn("Thickness", path -> Float.toString(path.getThickness()),
						(path, value) -> path.setThickness(Float.valueOf(value))));
		columns.add(creator.createFilterColumn("Viable", path -> Boolean.toString(path.isViable()),
				(path, value) -> path.setViable(Boolean.valueOf(value))));
		createCountingColumn(table, "Files", columns, item -> item.getFiles());
		createCountingColumn(table, "Measurements", columns, item -> item.getMeasurements());
		createCountingColumn(table, "Notes", columns, item -> item.getNotes());
		createPropertyColumns(table, columns, items);
		createPersonifiedColumns(table, columns);

		column.setAscendingSortComparator(
				(p1, p2) -> ((Integer) p1.getNumber()).compareTo((Integer) p2.getNumber()));
		return columns;
	}

	@Override
	public ItemEditor<Phantom> createEditFactory() {
		TextField textFieldNumber = new TextField();
		PhantominaEditor phantominaEditor =
				(PhantominaEditor) Main.getUIEntity(Phantomina.class).createEditFactory();

		ItemEditor<Phantom> editor = new ItemEditor<Phantom>(Phantom.class) {

			@Override
			public void onCreateInit(Phantom item) {
				phantominaEditor.showCreateMode();
			}

			@Override
			public void onEditInit(Phantom item) {
				phantominaEditor.showEditMode(item.getPhantomina());
			}

			@Override
			public void createPropertyGridPanes(Creator<Phantom> creator) {
				PropertyGridPane<Phantom> propertyPane =
						new PropertyGridPane<Phantom>(Phantom.class);

				
//				phantominaEditor.getAllAbstractEditors()
				
				PhantominaEditor temp = phantominaEditor;
				List<PropertyGridPane<Phantomina>> propertyGridPanes =
						phantominaEditor.getAllPropertyGridPanes();
				PropertyGridPane<Phantomina> phantominaPropertyPane = propertyGridPanes.get(0);
				phantominaPropertyPane.setUntitled();

				List<Node> temp2 = phantominaPropertyPane.getGridPane().getChildren();

				List<AbstractEditor<Phantomina, ?>> temp3 = temp2.stream()
						.collect(StreamUtils
								.castFilter(node -> (AbstractEditor<Phantomina, ?>) node))
						.collect(Collectors.toList());
//				
				

//				List<AbstractEditor<Phantomina, ?>> phantominaPropertyNodes =
//						phantominaPropertyPane.getPropertyChildren().stream()
//								.map(propertyNode -> (AbstractEditor<Phantomina, ?>) propertyNode)
//								.collect(Collectors.toList());
//				List<AbstractEditor<Phantom, ?>> phantomPropertyNodes =
//						phantominaPropertyNodes.stream()
//								.map(phantominaPropertyNode -> phantominaPropertyNode.map(
//										Phantom.class, item -> item.getPhantomina(), item -> null,
//										(item, value) -> item.setPhantomina(value)))
//								.collect(Collectors.toList());
//
//				ItemEditor<Phantom> editor = this;
//
//				PropertyGridPane<Phantom> test = new PropertyGridPane<>(Phantom.class);

//				phantomPropertyNodes.forEach(propertyNode -> propertyNode.addOn(test, "test"));
//				test.addOn(this);
//				propertyPane.addOn(this);

//				Collection<PropertyGridPane> propertyPanes =
//						phantominaEditor.getPropertyGridPanes();
//				propertyPanes.forEach(
//						propertyPane -> creator.addPropertyEntries(propertyPane.getEntries()));

				creator.create(textFieldNumber, item -> Integer.toString(item.getNumber()),
						(item, value) -> item.setNumber(Integer.valueOf(value)))
						.addOn(propertyPane, "Specific Number");
				creator.createComboBox(Manufacturing.class, item -> item.getManufacturing(),
						(item, value) -> item.setManufacturing(value)).setMapper(m -> m.getName())
						.addOn(propertyPane, "Manufacturing");
				creator.createTextField(item -> Float.toString(item.getThickness()),
						(item, value) -> item.setThickness(Float.valueOf(value)))
						.addOn(propertyPane, "Nominal thickness");
				creator.createCheckBox(item -> item.isViable(),
						(item, value) -> item.setViable(value), true).addOn(propertyPane, "Viable");
				propertyPane.setTitled("General");
				propertyPane.addOn(this);
			}

			@Override
			public void createSelectors(Creator<Phantom> creator) {
				creator.createSelector(DbFile.class, item -> item.getFiles(),
						(item, subItems) -> item.setFiles((List<DbFile>) subItems))
						.setTitled("Files").addOn(this);
				creator.createSelector(Measurement.class, item -> item.getMeasurements(),
						(item, subItems) -> item.setMeasurements((List<Measurement>) subItems))
						.setTitled("Measurement").addOn(this);
			}

			@Override
			public void onCreateBeforePost(Phantom item)
					throws NoUserLoggedInException, PostException, InvalidUIInputException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				if (item.getPhantomina() == null) throw new RuntimeException();
				if (!connector.exist(item.getPhantomina())) connector.create(item.getPhantomina());
			}

			@Override
			public void onCreateBeforeApplyChanges(Phantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}

			@Override
			public void onEditBeforeApplyChanges(Phantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				setPhantomina(item);
			}

			private void setPhantomina(Phantom item)
					throws PostException, InvalidUIInputException, NoUserLoggedInException {
				ICrudConnector<Phantomina> connector = Connectors.get(Phantomina.class);
				List<Phantomina> phantominas = connector.readAllAsList();
				String pid = phantominaEditor.getPid();
				Optional<Phantomina> optional =
						phantominas.stream().filter(p -> p.getProductId().equals(pid)).findFirst();
				if (optional.isPresent()) {
					item.setPhantomina(optional.get());
				} else {
					Phantomina phantomina = phantominaEditor.createItem();
					connector.create(phantomina);
					item.setPhantomina(phantomina);
				}
			}

		};
		editor.addApplyButton();
		return editor;

	}

}
