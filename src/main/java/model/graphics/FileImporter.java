package model.graphics;
	
import java.io.File;

import com.interactivemesh.jfx.importer.Importer;
import com.interactivemesh.jfx.importer.col.ColModelImporter;
import com.interactivemesh.jfx.importer.fxml.FxmlModelImporter;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import com.interactivemesh.jfx.importer.x3d.X3dModelImporter;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;

public class FileImporter {
	  private final Color lightColor;
	  private final Color modelColor;

	  public FileImporter(
			  Color lightColor,
			  Color phantomColor
			  ) {
		  this.lightColor = lightColor;
		  this.modelColor = phantomColor;
	  }
	  
	  public FileImporter() {
		  this(
			  Color.rgb(244, 255, 250),		// lightColor
			  Color.rgb(0, 190, 222));	// phantomColor
	  }
	  
	  public Group importFile(String filePath) {
		  return importStl(filePath);
	  }
	  
	  public Group importStl(String filePath) {
		  return createGroup(loadMeshViews(new StlMeshImporter(), filePath));
	  }
	  
	  public Group import3ds(String filePath) {
		  return createGroup(loadMeshViews(new TdsModelImporter(), filePath));
	  }
	  
	  public Group importCol(String filePath) {
		  return createGroup(loadMeshViews(new ColModelImporter(), filePath));
	  }
	  
	  public Group importFxml(String filePath) {
		  return createGroup(loadMeshViews(new FxmlModelImporter(), filePath));
	  } 
	  
	  public Group importObj(String filePath) {
		  return createGroup(loadMeshViews(new ObjModelImporter(), filePath));
	  } 
	  
	  public Group importX3d(String filePath) {
		  return createGroup(loadMeshViews(new X3dModelImporter(), filePath));
	  } 
	  
	  private MeshView[] loadMeshViews(Importer importer, String filePath) {
	    File file = new File(filePath);
	    importer.read(file);
	    Mesh mesh = (Mesh) importer.getImport();

	    return new MeshView[] { new MeshView(mesh) };
	  }

	  private Group createGroup(MeshView[] meshViews) {	
		  PhongMaterial material = new PhongMaterial(modelColor);
		  material.setSpecularColor(lightColor);
	      material.setSpecularPower(16);
	      
	    for (int i = 0; i < meshViews.length; i++)
	      meshViews[i].setMaterial(material);
	    Group group = new Group(meshViews); 

	    double centerX = -(group.getBoundsInLocal().getMaxX() 
	    		+ group.getBoundsInLocal().getMinX())/2;
	    double centerY = -(group.getBoundsInLocal().getMaxY() 
	    		+ group.getBoundsInLocal().getMinY())/2;
	    double centerZ = -(group.getBoundsInLocal().getMaxZ() 
	    		+ group.getBoundsInLocal().getMinZ())/2;
	    group.setTranslateX(centerX);
	    group.setTranslateY(centerY);
	    group.setTranslateZ(centerZ);

	    return group;
	  }
    
	}