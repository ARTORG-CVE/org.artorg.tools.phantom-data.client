package init;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.controlsfx.samples.HelloGlyphFont;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class GlyphTest extends Application {

	public static void main(String[] args) {
		launch();
	}

	static {
		// Register a custom default font
		GlyphFontRegistry.register("icomoon", HelloGlyphFont.class.getResourceAsStream("icomoon.ttf"), 16);
	}

	private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
	private GlyphFont icoMoon = GlyphFontRegistry.font("icomoon");

	// private static char FAW_TRASH = '\uf014';
	private static char FAW_GEAR = '\uf013';
//	private static char FAW_STAR  = '\uf005';

	private static char IM_BOLD = '\ue027';
	
	private static char IM_UNDERSCORED = '\ue02b';
	private static char IM_ITALIC = '\ue13e';

	@Override
	public void start(Stage stage) throws Exception {
		AnchorPane root = new AnchorPane();
		
		
		 ToolBar toolbar = new ToolBar(

	                // There are many ways how you can define a Glyph:

	                new Button("Hello", new Glyph("FontAwesome", "TRASH_ALT")),              // Use the Glyph-class with a icon name
	                new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.STAR)),   // Use the Glyph-class with a known enum value
	                new Button("", Glyph.create("FontAwesome|BUG")),                    // Use the static Glyph-class create protocol
	                new Button("", fontAwesome.create("REBEL")),                        // Use the font-instance with a name
	                new Button("", fontAwesome.create(FontAwesome.Glyph.SMILE_ALT)),    // Use the font-instance with a enum
	                new Button("", fontAwesome.create(FAW_GEAR).color(Color.RED))       // Use the font-instance with a unicode char
	        );
		
		 root.getChildren().add(toolbar);
		
    	Scene scene = new Scene(root,500,500);
		

		stage.setScene(scene);
		stage.setTitle("Phantom Database");
		stage.setWidth(800);
		stage.setHeight(500);
		stage.show();
		stage.requestFocus();
		stage.toFront();
    }

}
