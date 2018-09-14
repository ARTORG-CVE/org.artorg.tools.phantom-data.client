package org.artorg.tools.phantomData.client.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connector.HttpDatabaseCrud;
import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.PhantomConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.util.FxUtil;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.FabricationType;
import org.artorg.tools.phantomData.server.model.LiteratureBase;
import org.artorg.tools.phantomData.server.model.Phantom;
import org.artorg.tools.phantomData.server.model.Special;
import org.artorg.tools.phantomData.server.specification.DatabasePersistent;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class AddPhantomController implements FXMLloadable<AnchorPane> {
	private static final PhantomConnector connector = PhantomConnector.get();

	@Override
	public AnchorPane loadFXML() {
		return FxUtil.loadFXML("fxml/AddPhantom.fxml", this);
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label labelId;

    @FXML
    private TextField textFieldModelNumber;

    @FXML
    private ComboBox<AnnulusDiameter> comboBoxAnnulus;
    
    @FXML
    private ComboBox<FabricationType> comboBoxFabricationType;
    
    @FXML
    private ComboBox<LiteratureBase> comboBoxLiterature;
    
    @FXML
    private ComboBox<Special> comboBoxSpecials;
    
    @FXML
    private Button buttonAdd;

    @FXML
    void add(ActionEvent event) {
    	PhantomConnector.get().create(createPhantom());
    }

    @FXML
    void initialize() {
        assert labelId != null : "fx:id=\"labelId\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert textFieldModelNumber != null : "fx:id=\"textFieldModelNumber\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert comboBoxAnnulus != null : "fx:id=\"comboBoxAnnulus\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert comboBoxFabricationType != null : "fx:id=\"comboBoxFabricationType\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert comboBoxLiterature != null : "fx:id=\"comboBoxLiterature\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert comboBoxSpecials != null : "fx:id=\"comboBoxSpecials\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        assert buttonAdd != null : "fx:id=\"buttonAdd\" was not injected: check your FXML file 'AddPhantom.fxml'.";
        
        updateComboBoxes();
        
        textFieldModelNumber.setText("1");
        textFieldModelNumber.textProperty().addListener(event -> {
        	try {
        		updateId();
        	} catch( Exception e) {}
    	});
        
        updateId();


    }
    
    private Phantom createPhantom() {
    	AnnulusDiameter annulusDiameter = comboBoxAnnulus.getSelectionModel().getSelectedItem();
    	FabricationType fabricationType = comboBoxFabricationType.getSelectionModel().getSelectedItem();
    	LiteratureBase literatureBase = comboBoxLiterature.getSelectionModel().getSelectedItem();
    	Special special = comboBoxSpecials.getSelectionModel().getSelectedItem();
    	String sNumber = textFieldModelNumber.getText();
    	int number = Integer.valueOf(sNumber);
    	
    	return new Phantom(annulusDiameter, fabricationType, literatureBase, special, number);
    }
    
    private void updateComboBoxes() {
        createComboBox(comboBoxAnnulus, AnnulusDiameterConnector.get(), d -> String.valueOf(d.getValue()));
        createComboBox(comboBoxFabricationType, FabricationTypeConnector.get(), f -> f.getValue());
        createComboBox(comboBoxLiterature, LiteratureBaseConnector.get(), l -> l.getValue());
        createComboBox(comboBoxSpecials, SpecialConnector.get(), s -> s.getShortcut());
    }
    
    private <T extends DatabasePersistent<ID_TYPE>, ID_TYPE> void createComboBox(ComboBox<T> comboBox, HttpDatabaseCrud<T, ID_TYPE> connector, Function<T,String> mapper) {
    	List<T> fabricationType = connector.readAllAsStream()
        		.distinct().collect(Collectors.toList());
    	comboBox.setItems(FXCollections.observableList(fabricationType));
    	comboBox.getSelectionModel().selectFirst();
        Callback<ListView<T>, ListCell<T>> cellFactory = createComboBoxCellFactory(mapper);
        comboBox.setButtonCell(cellFactory.call(null));
        comboBox.setCellFactory(cellFactory);
        comboBox.getSelectionModel().selectedIndexProperty().addListener(e -> updateId());
    }
    
    private <T> Callback<ListView<T>, ListCell<T>> createComboBoxCellFactory(Function<T,String> mapper) {
    	return param -> {
        	return new ListCell<T>() {
				@Override
	            protected void updateItem(T item, boolean empty) {
	                super.updateItem(item, empty);
	                if (item == null || empty) {
	                    setGraphic(null);
	                } else {
	                    setText(mapper.apply(item));
	                }
	            }
			};
        };
    }
    
    private void updateId() {
    	labelId.setText(createPhantom().getProductId());
    	System.out.println("update id");
    }
}