package table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.artorg.tools.phantomData.server.model.PhantomFile;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import specification.Table;

public class FileTable implements Table<FileTable, PhantomFile> {

	private Set<PhantomFile> files;
	
	{
		files = new HashSet<PhantomFile>();
	}

	@Override
	public List<TableColumn<PhantomFile, ?>> createColumns() {
		List<TableColumn<PhantomFile, ?>> columns = new ArrayList<TableColumn<PhantomFile, ?>>();
		
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
	    
	    columns.add(idCol);
	    columns.add(fileTypeCol);
	    columns.add(nameCol);
	    columns.add(extensionCol);
	    columns.add(pathCol);
	    
		return columns;
	}

	@Override
	public Set<PhantomFile> getItems() {
		return files;
	}

}
