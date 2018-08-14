package org.artorg.tools.phantomData.client.control;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.artorg.tools.phantomData.client.connectors.AnnulusDiameterConnector;
import org.artorg.tools.phantomData.client.connectors.FabricationTypeConnector;
import org.artorg.tools.phantomData.client.connectors.LiteratureBaseConnector;
import org.artorg.tools.phantomData.client.connectors.SpecialConnector;
import org.artorg.tools.phantomData.client.graphics.Scene3D;
import org.artorg.tools.phantomData.server.model.AnnulusDiameter;
import org.artorg.tools.phantomData.server.model.Phantom;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddPhantomController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label labelId;

    @FXML
    private ComboBox<Float> comboBoxAnnulus;
    
    @FXML
    private ComboBox<String> comboBoxType, comboBoxLiterature, comboBoxSpecials;

    @FXML
    private CheckBox checkBoxLeaflets, checkBoxCoronairies;

    @FXML
    private TextField textFieldModelNumber;

    @FXML
    private TextField textFieldFilePath1, textFieldFilePath2, textFieldFilePath3;

    @FXML
    private Button buttonOpenFileChooser1, buttonOpenFileChooser2, buttonOpenFileChooser3;

    @FXML
    private ComboBox<String> comboBoxFileType1, comboBoxFileType2, comboBoxFileType3;

    @FXML
    private Button buttonAddFile1, buttonAddFile2, buttonAddFile3, buttonAddPhantom;

    @FXML
    private AnchorPane pane3d;

    @FXML
    private TableView<Phantom> tableView;

    @FXML
    void initialize() {
        assert labelId != null : "fx:id=\"labelId\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxAnnulus != null : "fx:id=\"comboBoxAnnulus\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxType != null : "fx:id=\"comboBoxType\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxSpecials != null : "fx:id=\"comboBoxDesign\" was not injected: check your FXML file 'New.fxml'.";
        assert checkBoxLeaflets != null : "fx:id=\"checkBoxLeaflets\" was not injected: check your FXML file 'New.fxml'.";
        assert checkBoxCoronairies != null : "fx:id=\"checkBoxCoronairies\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxLiterature != null : "fx:id=\"comboBoxLiterature\" was not injected: check your FXML file 'New.fxml'.";
        assert textFieldModelNumber != null : "fx:id=\"textFieldModelNumber\" was not injected: check your FXML file 'New.fxml'.";
        assert textFieldFilePath1 != null : "fx:id=\"textFieldFilePath1\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonOpenFileChooser1 != null : "fx:id=\"buttonOpenFileChooser1\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxFileType1 != null : "fx:id=\"comboBoxFileType1\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonAddFile1 != null : "fx:id=\"buttonAddFile1\" was not injected: check your FXML file 'New.fxml'.";
        assert textFieldFilePath2 != null : "fx:id=\"textFieldFilePath2\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonOpenFileChooser2 != null : "fx:id=\"buttonOpenFileChooser2\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxFileType2 != null : "fx:id=\"comboBoxFileType2\" was not injected: check your FXML file 'New.fxml'.";
        assert textFieldFilePath3 != null : "fx:id=\"textFieldFilePath3\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonOpenFileChooser3 != null : "fx:id=\"buttonOpenFileChooser3\" was not injected: check your FXML file 'New.fxml'.";
        assert comboBoxFileType3 != null : "fx:id=\"comboBoxFileType3\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonAddFile2 != null : "fx:id=\"buttonAddFile2\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonAddFile3 != null : "fx:id=\"buttonAddFile3\" was not injected: check your FXML file 'New.fxml'.";
        assert buttonAddPhantom != null : "fx:id=\"buttonAddPhantom\" was not injected: check your FXML file 'New.fxml'.";
        assert pane3d != null : "fx:id=\"pane3d\" was not injected: check your FXML file 'New.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'New.fxml'.";
        
        updateComboBoxes();
        
        textFieldModelNumber.setText("1");
        
        Scene3D scene3d = new Scene3D(pane3d);

    }
    
    private void updateComboBoxes() {
    	List<Float> annulusDiameters = AnnulusDiameterConnector.get().readAllAsStream()
        		.distinct().map(ad -> ad.getValue().floatValue()).collect(Collectors.toList());
        comboBoxAnnulus.setItems(FXCollections.observableList(annulusDiameters));
        
        comboBoxAnnulus.getSelectionModel().selectFirst();
        comboBoxAnnulus.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
              updateId();
            }
          });
        
        List<String> fabricationType = FabricationTypeConnector.get().readAllAsStream()
        		.distinct().map(ad -> ad.getValue()).collect(Collectors.toList());
        comboBoxType.setItems(FXCollections.observableList(fabricationType));
        comboBoxType.getSelectionModel().selectFirst();

        List<String> literatureBases = LiteratureBaseConnector.get().readAllAsStream()
        		.distinct().map(lb -> lb.getValue()).collect(Collectors.toList());
        comboBoxLiterature.setItems(FXCollections.observableList(literatureBases));
        comboBoxLiterature.getSelectionModel().selectFirst();
        
        List<String> specials = SpecialConnector.get().readAllAsStream()
        		.distinct().map(s -> s.getShortcut()).collect(Collectors.toList());
        comboBoxSpecials.setItems(FXCollections.observableList(specials));
        comboBoxSpecials.getSelectionModel().selectFirst();
    }
    
    private void updateId() {
    	
    }
    
    @FXML
    void openFileChooser1(ActionEvent event) {
    	openFileChooser(textFieldFilePath1);
    }

    @FXML
    void openFileChooser2(ActionEvent event) {
    	openFileChooser(textFieldFilePath2);
    }

    @FXML
    void openFileChooser3(ActionEvent event) {
    	openFileChooser(textFieldFilePath3);
    }
    
    private void openFileChooser(TextField textfield) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select phantom specific file");
    	File desktopDir = new File(System.getProperty("user.home") +"\\Desktop\\");
    	fileChooser.setInitialDirectory(desktopDir);
    	File file = fileChooser.showOpenDialog(new Stage());
    	textfield.setText(file.getAbsolutePath());
    }
    
}