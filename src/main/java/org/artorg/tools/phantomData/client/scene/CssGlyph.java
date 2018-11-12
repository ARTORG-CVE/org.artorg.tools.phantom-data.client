package org.artorg.tools.phantomData.client.scene;

import org.artorg.tools.phantomData.client.util.FxUtil;
import org.controlsfx.glyphfont.Glyph;

public class CssGlyph extends Glyph {
	
	public CssGlyph(String fontFamily, char unicode) {
		super(fontFamily, unicode);
		init();
	}
	
	public CssGlyph(String fontFamily, Object icon) {
		super(fontFamily, icon);
		init();
	}
	
	private void init() {
		this.getStylesheets().add(FxUtil.readCSSstylesheet("css/application.css"));
		this.getStyleClass().add("glyph-icon");
	}

}
