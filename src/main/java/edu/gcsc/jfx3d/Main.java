package edu.gcsc.jfx3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

    



public class Main extends Application{
    /**
     * @param args the command line arguments
     */
    
    
    Group testGroup;
    
  

    private final double cameraModifier = 20.0;
    private final double cameraQuantity = 0.50;
    private final double sceneWidth = 600;
    private final double sceneHeight = 600;
    private double mouseXold = 0;
    private double mouseYold = 0;
    private final double cameraYlimit = 15;
    private final double rotateModifier = 25;
    double oldX = 0.0;
    double oldY = 0.0;
    
    double textTranslateX = (-sceneWidth/8);
    double textTranslateY = (-sceneWidth/10);
    
    String[] subsetNameArray;
    Scene scene;
    Group pickedGroup;
    
    private double scenex, sceney = 0;
    private double fixedXAngle, fixedYAngle = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
      
    private double anchorAngleX;
    private double anchorAngleY;
    
    Text t;
    
    public static boolean ctrlPressedDown = false;
    
    Group ugxGeometry;
    int ugxSubsetCount;
    int ugxSwitchCounter = -1;
    
    public static void main(String[] args) {
        launch(args);
    }
 

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Group root = createContent();

            // Create camera
            PerspectiveCamera camera = new PerspectiveCamera(true);
            camera.setFarClip(100000);

            // and position it
            camera.getTransforms().addAll(
                    new Rotate(0, Rotate.Y_AXIS),
                    new Rotate(0, Rotate.X_AXIS),
                    new Translate(0, 0, -50));

            // add camera as node to scene graph
            root.getChildren().add(camera);

            // Setup a scene
            scene = new Scene(root, 1024, 768, true);
            scene.setFill(Color.DARKGRAY.darker().darker().darker().darker());
            scene.setCamera(camera);

            //Add the scene to the stage and show the stage
            primaryStage.setScene(scene);
            primaryStage.show();
            
            handleKeyboard(scene, camera);
            handleMouse(scene, camera);                                                                                                                                                                                                                                     
            
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
        
        
        
    }
    
    @Override
    public void stop() {
        System.exit(0);
    }
    
    /**
     * Creates a sphere with the specified resolution.
     *
     * @param radius sphere radius
     * @param resolution resolution (high resolution results in high number of
     * triangles)
     * @return groupcontaining the sphere.
     */
    public Group createContent() {

        Group root = new Group();

        
        PointLight light2 = new PointLight(Color.LIGHTGRAY);
        root.getChildren().add(light2);
        light2.getTransforms().add(new Translate(-50, 10, -520));

        AmbientLight light3 = new AmbientLight(new Color(0.35,0.35,0.35,1.0));
        root.getChildren().add(light3);

/*
        Group d10 = buildSTL("../JFX3DSample-master/src/main/java/edu/gcsc/jfx3d/STL/hexamail.stl", Color.AQUA, false, true);
        Group d10bin = buildSTL("../JFX3DSample-master/src/main/java/edu/gcsc/jfx3d/STL/d10bin.stl", Color.AQUA, false, true);
        Group porsche = buildSTL("../JFX3DSample-master/src/main/java/edu/gcsc/jfx3d/STL/porsche.stl", Color.AQUA, false, true);
        Group tire = buildSTL("../JFX3DSample-master/src/main/java/edu/gcsc/jfx3d/STL/tire_v.stl", Color.AQUA, false, true);
        d10.setTranslateX(-50);
        VFX3DUtil.addMouseBehavior(d10, d10, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);
        
        VFX3DUtil.addMouseBehavior(d10bin, d10bin, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);
        
        VFX3DUtil.addMouseBehavior(porsche, porsche, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);
        
        
        VFX3DUtil.addMouseBehavior(tire, tire, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);
        
        root.getChildren().add(porsche);
        root.getChildren().add(d10);
        root.getChildren().add(d10bin);
        root.getChildren().add(tire);
        */
        
        String filePath = "../VRL-JFXVis/src/main/java/edu/gcsc/jfx3d/ugx/lowResSubsetTest.ugx";
        UGXReader ugxr = new UGXReader(filePath);
        ugxr.setFlagHighResolution(true);
        ugxr.setFlagDoubleFacesOnEdges(true);
        ugxr.setFlagRenderFaces(true);
        ugxGeometry = ugxr.xbuildUGX();
        subsetNameArray = ugxr.getSubssetNameArray();
        
        root.getChildren().add(ugxGeometry);

        VFX3DUtil.addMouseBehavior(ugxGeometry, ugxGeometry, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);

        
        ugxSubsetCount = ugxr.getNumberOfSubsets();
        
        return root;
    }
    
    private void handleKeyboard(Scene scene,PerspectiveCamera camera){
        scene.setOnKeyPressed(event -> {
            double change = cameraQuantity;
            
        Rotate xRotate = new Rotate(5, 0,0,0,Rotate.X_AXIS);
        Rotate yRotate = new Rotate(5, 0,0,0,Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(5, 0,0,0,Rotate.Z_AXIS);
            
            //Add shift modifier to simulate "Running Speed"
            if(event.isShiftDown()) { change = cameraModifier; }
            //What key did the user press?
            KeyCode keycode = event.getCode();
            // Add Zoom controls
            if(keycode == KeyCode.W) {
                camera.setTranslateZ(camera.getTranslateZ() + change); 
            }
            if(keycode == KeyCode.S) { 
                camera.setTranslateZ(camera.getTranslateZ() - change);
            }
            // Add Strafe controls
            if(keycode == KeyCode.A) { 
                camera.setTranslateX(camera.getTranslateX() - change); 
            }
            if(keycode == KeyCode.D) { 
                camera.setTranslateX(camera.getTranslateX() + change); 
            }
            if (keycode == KeyCode.Q) {
                camera.setRotationAxis(Rotate.Y_AXIS);
                camera.setRotate( camera.getRotate() + 10.0);
            }
            if (keycode == KeyCode.E) {
                camera.setRotationAxis(Rotate.Y_AXIS);
                camera.setRotate( camera.getRotate() - 10.0);
            }
            if (keycode == KeyCode.R) {
                camera.setRotationAxis(Rotate.Y_AXIS);
                camera.setRotate(0);
                camera.setRotationAxis(Rotate.X_AXIS);
                camera.setRotate(0);
                camera.setRotationAxis(Rotate.Z_AXIS);
                camera.setRotate(0);
                camera.setTranslateX(0);
                camera.setTranslateY(0);
                camera.setTranslateZ(0);
                
            }
            if(keycode == KeyCode.ADD || keycode == KeyCode.PLUS){
                
                ugxSwitchCounter = (ugxSwitchCounter +1) % ugxSubsetCount;
                for (int i = 0; i < ugxSubsetCount; i++) {
                    if (i == ugxSwitchCounter) {
                        ugxGeometry.getChildren().get(i).setVisible(true);
                        System.out.println("Currently shown subset: " + ugxSwitchCounter +" of "+ (ugxSubsetCount-1) + ", "  + subsetNameArray[ugxSwitchCounter]);
                    }
                    else{
                        ugxGeometry.getChildren().get(i).setVisible(false);
                    }
                }
               
            }
            
            if (keycode == KeyCode.SUBTRACT || keycode == KeyCode.MINUS) {
                ugxSwitchCounter = -1;
                for (int i = 0; i < ugxSubsetCount; i++) {
                    ugxGeometry.getChildren().get(i).setVisible(true);
                }
                System.out.println("Currently shown subset: All");
            }
            
            if (keycode == KeyCode.CONTROL) {
                ctrlPressedDown = true;
            }
            
            if(keycode == KeyCode.T){
                camera.setRotationAxis(Rotate.X_AXIS);
                camera.setRotate(camera.getRotate() +10);   
            }
            if(keycode == KeyCode.Z){
                camera.setRotationAxis(Rotate.Z_AXIS);
                camera.setRotate(camera.getRotate() +10);   
            }
            if (keycode == KeyCode.NUMPAD2) {
                camera.getTransforms().add(yRotate);
                }
            if (keycode == KeyCode.NUMPAD1) {
                camera.getTransforms().add((xRotate));   
                }
            if (keycode == KeyCode.NUMPAD3) {
                camera.getTransforms().add(zRotate);   
                
            }
        });
 
        scene.setOnKeyReleased(releaseEvent -> {

            KeyCode keycode = releaseEvent.getCode();

            if (keycode == KeyCode.CONTROL) {
                ctrlPressedDown = false;
            }

        });
    }

    private void handleMouse(Scene scene,PerspectiveCamera camera){
        
        Rotate xRotate = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        camera.getTransforms().addAll(xRotate, yRotate);
        scene.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED
                    || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                //acquire the new Mouse coordinates from the recent event
                double mouseXnew = event.getSceneX();
                double mouseYnew = event.getSceneY();
                if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    //calculate the rotational change of the camera pitch
                    double pitchRotate = xRotate.getAngle() + (mouseYnew - mouseYold) / rotateModifier;
                    //set min/max camera pitch to prevent camera flipping
                    pitchRotate = pitchRotate > cameraYlimit ? cameraYlimit : pitchRotate;
                    pitchRotate = pitchRotate < -cameraYlimit ? -cameraYlimit : pitchRotate;
                    //replace the old camera pitch rotation with the new one.
                    xRotate.setAngle(pitchRotate);
                    //calculate the rotational change of the camera yaw
                    double yawRotate = yRotate.getAngle() - (mouseXnew - mouseXold) / rotateModifier;
                    yRotate.setAngle(yawRotate);
                }
                mouseXold = mouseXnew;
                mouseYold = mouseYnew;
            }
        }     
        );
        
       scene.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {
           //Node pickRes = mouseClick.getPickResult().getIntersectedNode();
           
           //System.out.println("pickRes "+pickRes );

            
           
        }     
        );

    }
    

    private Group buildPyramid(float height, float hypotenuse, Color color, boolean ambient, boolean fill) {
        final TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
                0, 0, 0, //Point 0: Top of Pyramid
                0, height, -hypotenuse / 2, //Point 1: closest base point to camera
                -hypotenuse / 2, height, 0, //Point 2: leftmost base point to camera
                hypotenuse / 2, height, 0, //Point 3: farthest base point to camera
                0, height, hypotenuse / 2 //Point 4: rightmost base point to camera
        );

        mesh.getTexCoords().addAll(0, 0);

        mesh.getFaces().addAll( //use dummy texCoords
                0, 0, 2, 0, 1, 0, // Vertical Faces "wind" counter clockwise
                0, 0, 1, 0, 3, 0, // Vertical Faces "wind" counter clockwise
                0, 0, 3, 0, 4, 0, // Vertical Faces "wind" counter clockwise
                0, 0, 4, 0, 2, 0, // Vertical Faces "wind" counter clockwise
                4, 0, 1, 0, 2, 0, // Base Triangle 1 "wind" clockwise because camera has rotated
                4, 0, 3, 0, 1, 0 // Base Triangle 2 "wind" clockwise because camera has rotated
        ); 

        
        MeshView meshView = new MeshView(mesh);

        meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        meshView.setCullFace(CullFace.NONE); //Removing culling to show back lines

        Group pyramidGroup = new Group();
        pyramidGroup.getChildren().add(meshView);

        if (null != color) {
            PhongMaterial material = new PhongMaterial(color);
            meshView.setMaterial(material);
        }
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            pyramidGroup.getChildren().add(light);
        }
        if (fill) {
            meshView.setDrawMode(DrawMode.FILL);
        }

        return pyramidGroup;
    }
    
    private Group buildTest( Color color, boolean ambient, boolean filled){
        
        TriangleMesh mesh = new TriangleMesh();
        
        float[] points = {
            0,0,0,
            10,0,0,
            20,0,0,
            0,10,0,
            10,10,0,
            20,10,0,
            0,20,0,
            10,20,0,
            20,20,0,
        };
        
        float[] texCoords = {0,0};
        
        int[] faces = {
            3,0,0,0,1,0,
            3,0,1,0,4,0,
            4,0,1,0,2,0,
            4,0,2,0,5,0,
            6,0,3,0,4,0,
            6,0,4,0,7,0,
            7,0,4,0,5,0,
            7,0,5,0,8,0,
        };
        
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);
       
        MeshView meshView = new MeshView(mesh);
        
        meshView.setDrawMode(DrawMode.LINE);
        meshView.setCullFace(CullFace.NONE);
        
        Group customGroup = new Group();
        customGroup.getChildren().add(meshView);

        if (null != color) {
            PhongMaterial material = new PhongMaterial(color);
            meshView.setMaterial(material);
        }
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            customGroup.getChildren().add(light);
        }
        if (filled) {
            meshView.setDrawMode(DrawMode.FILL);
        }
        return customGroup;
    }
    
    private Group customBuild2 (int arrayXsize,int arrayYsize,int spacing,Color color, boolean ambient, boolean fill){
        
        float[][] array = new float[arrayXsize][arrayYsize];
        
        for (int i = 0; i < arrayXsize; i++) {
            for (int j = 0; j < arrayYsize; j++) {
                array[i][j] = (float) (Math.sin(i)*2);
                }
        }
     
        
        TriangleMesh mesh = new TriangleMesh();
        
        for (int x = 0; x < arrayXsize; x++) {
            for (int z = 0; z < arrayYsize; z++) {
                mesh.getPoints().addAll(x * spacing, array[x][z], z * spacing);
            }
        }
        
        mesh.getTexCoords().addAll(0,0);
        
        int total = arrayXsize * arrayYsize;
        int nextRow = arrayYsize;
        
        for (int i = 0; i < total - nextRow -1; i++) {
            //Top upper left triangle
            mesh.getFaces().addAll(i,0,i+nextRow,0,i+1,0);
            //Top lower right triangle
            mesh.getFaces().addAll(i+nextRow,0,i+nextRow + 1,0,i+1,0);
        }
        
        MeshView meshView = new MeshView(mesh);

        
        Group customGroup2 = new Group();
        customGroup2.getChildren().add(meshView);
        
        if (null != color) {
            PhongMaterial material = new PhongMaterial(color);
            meshView.setMaterial(material);
        }
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            customGroup2.getChildren().add(light);
        }
        if(fill) { 
            meshView.setDrawMode(DrawMode.FILL);
        } else {
            meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        }
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines

        return customGroup2;
    }
    
    private Group buildSTL (String filePath,Color color, boolean ambient, boolean fill){
        
        STLReader reader = new STLReader(filePath);
        reader.start();
        reader.getFacetPoints(0);
        reader.getNormal(0);
        
//        reader.readFromAscii();
        float[] vertices = reader.getVerticesFloatArray();
        float[] normals = reader.getNormalsFloatArray();
        
        TriangleMesh mesh = new TriangleMesh();
        
        mesh.getPoints().addAll(vertices);
        mesh.getTexCoords().addAll(0,0);
        
        
        for (int i = 0; i < vertices.length/3 ; i+=3) {
            mesh.getFaces().addAll(i,0, i+1,0,i+2,0);
        }
        
        MeshView meshView = new MeshView(mesh);

        
        Group customGroup2 = new Group();
        customGroup2.getChildren().add(meshView);
        
        if (null != color) {
            PhongMaterial material = new PhongMaterial(color);
            meshView.setMaterial(material);
        }
        if (ambient) {
            AmbientLight light = new AmbientLight(Color.WHITE);
            light.getScope().add(meshView);
            customGroup2.getChildren().add(light);
        }
        if(fill) { 
            meshView.setDrawMode(DrawMode.FILL);
        } else {
            meshView.setDrawMode(DrawMode.LINE); //show lines only by default
        }
        meshView.setCullFace(CullFace.BACK); //Removing culling to show back lines

        return customGroup2;
    }

    
}
