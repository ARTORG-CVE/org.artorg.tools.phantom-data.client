package util;

import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class GuiUtils {
	
	public static void autoResizeColumns( TableView<?> table ) {
	    //Set the right policy
	    table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
	    table.getColumns().stream().forEach( (column) -> {
	        Text t = new Text( column.getText() );
	        double max = t.getLayoutBounds().getWidth();
	        for ( int i = 0; i < table.getItems().size(); i++ ) {
	            //cell must not be empty
	            if ( column.getCellData( i ) != null ) {
	                t = new Text( column.getCellData( i ).toString() );
	                double calcwidth = t.getLayoutBounds().getWidth();
	                if ( calcwidth > max )
	                    max = calcwidth;
	            }
	        }
	        //set the new max-widht with some extra space
	        column.setPrefWidth( max + 30.0d );
	    } );
	}

}
