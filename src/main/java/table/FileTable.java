package table;

import java.util.HashSet;
import java.util.Set;

import org.artorg.tools.phantomData.server.connector.FileConnector;
import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import specification.Table;

public class FileTable implements Table<PhantomFile> {

	private Set<PhantomFile> files;
	
	{
		files = new HashSet<PhantomFile>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TableView<PhantomFile> createTableView(TableView<PhantomFile> table) {
		TableColumn<PhantomFile, String> idCol = new TableColumn<PhantomFile, String>("id");
		TableColumn<PhantomFile, String> pathCol = new TableColumn<PhantomFile, String>("path");
	    TableColumn<PhantomFile, String> nameCol = new TableColumn<PhantomFile, String>("name");
	    TableColumn<PhantomFile, String> extensionCol = new TableColumn<PhantomFile, String>("extension");
	    TableColumn<PhantomFile, String> fileTypeCol = new TableColumn<PhantomFile, String>("file type");
	    
	    idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
	    pathCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPath()));
	    nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
	    extensionCol.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getExtension()));
	    fileTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType().getName()));
	    
	    table.getColumns().removeAll(table.getColumns());
		table.getColumns().addAll(idCol, fileTypeCol, nameCol, extensionCol, pathCol);
		
		files.addAll(FileConnector.get().readAllAsSet());
	    ObservableList<PhantomFile> data = FXCollections.observableArrayList(files);
	    table.setItems(data);
		return table;
		
		
	}

}
