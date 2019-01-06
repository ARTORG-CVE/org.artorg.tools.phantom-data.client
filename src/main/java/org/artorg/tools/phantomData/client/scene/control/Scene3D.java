package org.artorg.tools.phantomData.client.scene.control;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.artorg.tools.phantomData.client.scene.FileImporter;
import org.artorg.tools.phantomData.client.scene.Xform;
import org.artorg.tools.phantomData.client.scene.layout.AddableToPane;

import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Scene3D extends AnchorPane implements AddableToPane {
	private static final Color lightColor = Color.rgb(244, 255, 250);
	
	private final Group root;
    private final Xform world;
    private final PerspectiveCamera camera;
    private final Xform cameraXform;
    private final Xform cameraXform2;
    private final Xform cameraXform3;
    private final double cameraInitialDistance;
    private final double cameraInitialAnlgeX;
    private final double cameraInitialAngleY;
    private final double cameraNearClip;
    private final double cameraFarClip;
    private final double mouseSpeed;
    private final double rotationSpeed;
    private final double trackSpeed;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    
    private FileImporter fileImporter;
    
    private Group group;
    
    public Scene3D(
    		double cameraInitialDistance,
    		double cameraInitialAnlgeX,
    	    double cameraInitialAngleY,
    	    double cameraNearClip,
    	    double cameraFarClip,
    	    double mouseSpeed,
    	    double rotationSpeed,
    	    double trackSpeed,
    	    int viewportSize,
    	    Group root) {
    	this.root = root;
    	this.world = new Xform();
    	this.camera = new PerspectiveCamera(true);
    	this.cameraXform = new Xform();
    	this.cameraXform2 = new Xform();
    	this.cameraXform3 = new Xform();
    	this.cameraInitialDistance = cameraInitialDistance;
    	this.cameraInitialAnlgeX = cameraInitialAnlgeX;
	    this.cameraInitialAngleY = cameraInitialAngleY;
	    this.cameraNearClip = cameraNearClip;
	    this.cameraFarClip = cameraFarClip;
	    this.mouseSpeed = mouseSpeed;
	    this.rotationSpeed = rotationSpeed;
	    this.trackSpeed = trackSpeed;
	    
        System.setProperty("prism.dirtyopts", "false");
    	fileImporter = new FileImporter();
		SubScene subscene = new SubScene(root,400,400,true, SceneAntialiasing.DISABLED);
		root.getChildren().add(world);
		root.setDepthTest(DepthTest.ENABLE);
		subscene.setFill(Color.GREY);
		
        handleKeyboard(subscene, world);
        handleMouse(subscene, world);
        handleMouseZoom(subscene, world);
//        buildAxes();
        
        subscene.setCamera(camera);
		subscene.heightProperty().bind(super.heightProperty());
        subscene.widthProperty().bind(super.widthProperty());
		super.getChildren().add(subscene);
		super.setStyle("-fx-background-color: rgba(255, 255, 255, 0); -fx-background-radius: 10;");
        super.setMinWidth(300);
        
        AnchorPane.setBottomAnchor(subscene, 0.0);
    	AnchorPane.setLeftAnchor(subscene, 0.0);
    	AnchorPane.setRightAnchor(subscene, 0.0);
    	AnchorPane.setTopAnchor(subscene, 0.0);
        
	    
    }
    
    public Scene3D() {
    	this(
    		-450,		// cameraInitialDistance
    		70.0, 		// cameraInitialAnlgeX
    		320.0,		// cameraInitialAngleY
    		0.1,		// cameraNearClip
    		100000.0,	// cameraFarClip
    		0.1,		// mouseSpeed
    		2.0,		// rotationSpeed
    		0.3,		// trackSpeed
    		800,
    		new Group());		
    }
    
    public void loadFile(File file) {
    	if (group == null) {
    		group = fileImporter.importFile(file);
    		world.getChildren().addAll(group);
    		group.getChildren().addAll(createLightSources());
			buildCamera(group);
    	}
    	else
    		group = fileImporter.importFile(file);
    }
	
	 private void buildCamera(Group group) {
		 	root.getChildren().add(cameraXform);
	        cameraXform.getChildren().add(cameraXform2);
	        cameraXform2.getChildren().add(cameraXform3);
	        cameraXform3.getChildren().add(camera);
	        cameraXform3.setRotateZ(180.0);

	        camera.setNearClip(cameraNearClip);
	        camera.setFarClip(cameraFarClip);
	        camera.setTranslateZ(cameraInitialDistance);
	        cameraXform.ry.setAngle(cameraInitialAngleY);
	        cameraXform.rx.setAngle(cameraInitialAnlgeX);
    }
	
	private List<LightBase> createLightSources() {
		  PointLight pointLight = new PointLight(lightColor);
		    pointLight.setTranslateX(800*3/4);
		    pointLight.setTranslateY(800/2);
		    pointLight.setTranslateZ(800/2);
		    PointLight pointLight2 = new PointLight(lightColor);
		    pointLight2.setTranslateX(800*1/4);
		    pointLight2.setTranslateY(800*3/4);
		    pointLight2.setTranslateZ(800*3/4);
		    PointLight pointLight3 = new PointLight(lightColor);
		    pointLight3.setTranslateX(-800*5/8);
		    pointLight3.setTranslateY(0);
		    pointLight3.setTranslateZ(0); 
		    
		    Color ambientColor = Color.rgb(80, 80, 80, 0);
		    AmbientLight ambient = new AmbientLight(ambientColor);
		    
		    return Arrays.asList(
		    		pointLight
		    		, pointLight2, pointLight3
		    		, 
		    		ambient
		    		);
	  }
	
	private void handleMouseZoom(SubScene subscene, final Node root) {
		subscene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {

				final double factor = 1.2/40; 
				double value = event.getDeltaY();
				if (value > 0)
					value = 1/(value*factor);
				else if (value == 0)
					value = 1.0;
				else 
					value = Math.abs(value)*factor;
				camera.setTranslateZ(camera.getTranslateZ()*value);
			}
			
		});
	}
	
//	private void buildAxes() {
//		final int AXIS_LENGTH = 30;
//        final PhongMaterial redMaterial = new PhongMaterial();
//        redMaterial.setDiffuseColor(Color.DARKRED);
//        redMaterial.setSpecularColor(Color.RED);
//
//        final PhongMaterial greenMaterial = new PhongMaterial();
//        greenMaterial.setDiffuseColor(Color.DARKGREEN);
//        greenMaterial.setSpecularColor(Color.GREEN);
//
//        final PhongMaterial blueMaterial = new PhongMaterial();
//        blueMaterial.setDiffuseColor(Color.DARKBLUE);
//        blueMaterial.setSpecularColor(Color.BLUE);
//
//        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
//        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
//        final Box zAxis = new Box(1, 1, AXIS_LENGTH);
//
//        xAxis.setMaterial(redMaterial);
//        yAxis.setMaterial(greenMaterial);
//        zAxis.setMaterial(blueMaterial);
//        
//        final Xform axisGroup = new Xform();
//        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
//        axisGroup.setVisible(true);
//        world.getChildren().addAll(axisGroup);
//    }
	
	  private void handleMouse(SubScene subscene, final Node root) {
	        subscene.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent me) {
	                mousePosX = me.getSceneX();
	                mousePosY = me.getSceneY();
	                mouseOldX = me.getSceneX();
	                mouseOldY = me.getSceneY();
	            }
	        });
	        subscene.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override public void handle(MouseEvent me) {
	                mouseOldX = mousePosX;
	                mouseOldY = mousePosY;
	                mousePosX = me.getSceneX();
	                mousePosY = me.getSceneY();
	                mouseDeltaX = (mousePosX - mouseOldX); 
	                mouseDeltaY = (mousePosY - mouseOldY); 

	                
	                if (me.isPrimaryButtonDown()) {
	                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*mouseSpeed*rotationSpeed);  
	                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*mouseSpeed*rotationSpeed);  
	                }
	                else if (me.isSecondaryButtonDown()) {
	                    double z = camera.getTranslateZ();
	                    double newZ = z + mouseDeltaX*mouseSpeed;
	                    camera.setTranslateZ(newZ);
	                }
	                else if (me.isMiddleButtonDown()) {
	                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*mouseSpeed*trackSpeed);  
	                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*mouseSpeed*trackSpeed);  
	                }
	            }
	        });
	    }
	    
	    private void handleKeyboard(SubScene subscene, final Node root) {
	        subscene.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            @Override
	            public void handle(KeyEvent event) {
	                switch (event.getCode()) {
	                    case Z:
	                    	cameraXform2.t.setX(0.0);
	                        cameraXform2.t.setY(0.0);
	                        camera.setTranslateZ(cameraInitialDistance);
	                        cameraXform.ry.setAngle(cameraInitialAngleY);
	                        cameraXform.rx.setAngle(cameraInitialAnlgeX);
	                        break;
					default:
						break;
	                }
	            }
	        });
	    }

}
