package org.artorg.tools.phantomData.client.scene.control.tableView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.swing.filechooser.FileSystemView;

import org.artorg.tools.phantomData.client.scene.control.FilterMenuButton;
import org.artorg.tools.phantomData.client.table.AbstractFilterColumn;
import org.artorg.tools.phantomData.client.table.IDbTable;
import org.artorg.tools.phantomData.client.table.IFilterTable;
import org.artorg.tools.phantomData.server.model.base.DbFile;
import org.artorg.tools.phantomData.server.specification.DbPersistent;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class DbFilterTableView<ITEM extends DbPersistent<ITEM, ?>>
	extends DbTableView<ITEM> {
	protected List<FilterMenuButton<ITEM, ?>> filterMenuButtons;

	{
		super.setEditable(true);
		filterMenuButtons = new ArrayList<FilterMenuButton<ITEM, ?>>();
	}

	public DbFilterTableView() {
		super();
	}

	public DbFilterTableView(Class<ITEM> itemClass) {
		super(itemClass);
	}

	public void showFilterButtons() {
		for (Node n : super.lookupAll(".column-header > .label")) {
			if (n instanceof Label) {
				Label label = (Label) n;

				String columnName = label.getText();
				Optional<FilterMenuButton<ITEM, ?>> filterMenuButton = filterMenuButtons
					.stream().filter(f -> f.getText().equals(columnName)).findFirst();
				if (filterMenuButton.isPresent()) {
					filterMenuButton.get().prefWidthProperty()
						.bind(label.widthProperty());
					filterMenuButton.get().getStyleClass().add("filter-menu-button");
					label.setGraphic(filterMenuButton.get());
					label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initTable() {
		if (getTable() instanceof IFilterTable)
			initFilterTable((IFilterTable<ITEM>) getTable());
		else super.initTable();
	}

	protected void initFilterTable(IFilterTable<ITEM> table) {
		super.getColumns().removeAll(super.getColumns());

		// creating columns
		List<TableColumn<ITEM, ?>> columns = new ArrayList<TableColumn<ITEM, ?>>();
		List<String> columnNames = table.getFilteredColumnNames();

		int nCols = table.getFilteredNcols();

		for (int col = 0; col < nCols; col++) {
			TableColumn<ITEM, Object> column =
				new TableColumn<ITEM, Object>(columnNames.get(col));
			column.setSortable(false);

			final int localCol = col;
			FilterMenuButton<ITEM, ?> filterMenuButton =
				new FilterMenuButton<ITEM, Object>();
			filterMenuButton.setText(columnNames.get(col));

			AbstractFilterColumn<ITEM, ?> filterColumn =
				table.getFilteredColumns().get(localCol);
			filterColumn.setSortComparatorQueue(table.getSortComparatorQueue());
			filterMenuButton.setColumn(filterColumn, () -> table.applyFilter());
			filterMenuButtons.add(filterMenuButton);

//		    column.setCellValueFactory(cellData -> {
//		    	ObjectProperty<Object> objectProperty = new ObjectPropertyBase<Object>() {
//
//					@Override
//					public Object getBean() {
//						return cellData.getValue();
//					}
//
//					@Override
//					public String getName() {
//						return table.getFilteredValue(cellData.getValue(), localCol).getClass().getSimpleName();
//					}
//		    		
//		    	};
//		 
////    		table.getFilteredValue(cellData.getValue(), localCol).getClass().getSimpleName());
//		    return objectProperty;
//		    });

			column.setCellFactory(new Callback<TableColumn<ITEM,Object>, TableCell<ITEM,Object>>() {

				@Override
				public TableCell<ITEM, Object> call(TableColumn<ITEM, Object> param) {
					try {
					return new AttachmentListCell();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				
//	            @Override
//	            public ListCell<String> call(ListView<String> list) {
//	                return new AttachmentListCell();
//	            }
	        });
			
			column.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<Object>(
				table.getFilteredValue(cellData.getValue(), localCol).getClass()
					.getSimpleName()));

//		    new SimpleStringProperty(
//    		String.valueOf(table.getFilteredValue(cellData.getValue(), localCol))));

//		    column.setCellValueFactory(cellData -> new SimpleStringProperty(
//		    		String.valueOf(table.getFilteredValue(cellData.getValue(), localCol))));
			columns.add(column);
		}

		super.getColumns().addAll(columns);
		super.setItems(table.getFilteredItems());
		autoResizeColumns();

		Platform.runLater(() -> showFilterButtons());
	}
	
	private static class AttachmentListCell<ITEM> extends TableCell<ITEM,Object> {

		
		
		@Override
		protected void updateItem(Object item, boolean empty) {			
          super.updateItem(item, empty);
          
          if (item instanceof DbFile)  {
          
          if (empty) {
              setGraphic(null);
              setText(null);
          } else {
              Image fxImage = getFileIcon(((DbFile)item).getFile().getPath());
              ImageView imageView = new ImageView(fxImage);
              setGraphic(imageView);
              setText("test");
          }
          } else {
        	  try {
        	  System.out.println("DbFilterTableView - AttachementListCell - updataItem - class " +item.getClass().getSimpleName() +", value = " +item);
        	  } catch (NullPointerException e) {
        	  }
        	  
          }
          
			
		}
		
		
		
		
//        @Override
//        public void updateItem(String item, boolean empty) {
//            super.updateItem(item, empty);
//            if (empty) {
//                setGraphic(null);
//                setText(null);
//            } else {
//                Image fxImage = getFileIcon(item);
//                ImageView imageView = new ImageView(fxImage);
//                setGraphic(imageView);
//                setText(item);
//            }
//        }
    }
	
static HashMap<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();
	
	private static Image getFileIcon(String fname) {
        final String ext = getFileExt(fname);

        Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
        if (fileIcon == null) {

            javax.swing.Icon jswingIcon = null; 

            File file = new File(fname);
            if (file.exists()) {
                jswingIcon = getJSwingIconFromFileSystem(file);
            }
            else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("icon", ext);
                    jswingIcon = getJSwingIconFromFileSystem(tempFile);
                }
                catch (IOException e) {
                	e.printStackTrace();
                }
                finally {
                    if (tempFile != null) tempFile.delete();
                }
            }

            if (jswingIcon != null) {
                fileIcon = jswingIconToImage(jswingIcon);
                mapOfFileExtToSmallIcon.put(ext, fileIcon);
            }
        }

        return fileIcon;
    }
	
	private static javax.swing.Icon getJSwingIconFromFileSystem(File file) {

        // Windows {
        FileSystemView view = FileSystemView.getFileSystemView();
        javax.swing.Icon icon = view.getSystemIcon(file);
        // }

        // OS X {
        //final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        //javax.swing.Icon icon = fc.getUI().getFileView(fc).getIcon(file);
        // }

        return icon;
    }
	
	private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
	
	private static String getFileExt(String fname) {
        String ext = ".";
        int p = fname.lastIndexOf('.');
        if (p >= 0) {
            ext = fname.substring(p);
        }
        return ext.toLowerCase();
    }

	@Override
	@SuppressWarnings("unchecked")
	public void reload() {
		if (getTable() instanceof IDbTable && getTable() instanceof IFilterTable)
			reloadFilterTable((IDbTable<ITEM> & IFilterTable<ITEM>) getTable());
	}

	private <TABLE extends IDbTable<ITEM> & IFilterTable<ITEM>> void
		reloadFilterTable(TABLE table) {
//		table.getItems().removeListener(getListenerChangedListenerRefresh());
		table.readAllData();
		super.setItems(table.getFilteredItems());
//		getTable().getItems().addListener(getListenerChangedListenerRefresh());
		refresh();
	}

}