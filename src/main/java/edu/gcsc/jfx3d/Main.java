package edu.gcsc.jfx3d;

import com.leapmotion.leap.Controller;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.leapmotion.leap.*;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;



public class Main extends Application{
    /**
     * @param args the command line arguments
     */
    

    private final double cameraModifier = 20.0;
    private final double cameraQuantity = 0.50;
    private final double sceneWidth = 1028;
    private final double sceneHeight = 720;
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
    Stage currentStage;
    Group rootUGX;

    public static boolean ctrlPressedDown = false;
    
    Group ugxGeometry;
    int ugxSubsetCount;
    int ugxSwitchCounter = -1;
    Group ultraRoot; // top node of everything
    
    Parent previousPickedNode;
    
    private boolean highResolution = false;
    private boolean doubleFacesOnEdges = true;
    private boolean renderFaces = true;
    private boolean debugMode = true;
    private boolean renderVertices = true;
    private boolean renderEdges = true;
    private boolean renderVolumes = true;
    
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    PerspectiveCamera camera;
    
    private double mouseDeltaX;
    private double mouseDeltaY;
    
    private boolean leftHandJustEntered = true;
    private boolean rightHandJustEntered = true;
    
    private long leftHandJustEnteredAtFrameID;
    private long rightHandJustEnteredAtFrameID;
    
    private long handEnteredViedDelayFrame;
    
    LeapMotionListener listener ;
    Controller controller;
    Group handGroup = new Group();
    Group leftHandGroup = new Group();
    Group rightHandGroup = new Group();
    PhongMaterial handSphereMat = new PhongMaterial(Color.RED);
    Random rand = new Random(System.currentTimeMillis());
    Group rGroup = new Group();
    double palmZ ;
    Circle r = new Circle();

    com.sun.glass.ui.Robot wall_e = com.sun.glass.ui.Application.GetApplication().createRobot();
    long lastTap;
    RotateTransition rotateAnimation = new RotateTransition();
    
    Vector yNegVector = new Vector(0, -1, 0);
    Cylinder[] cArray = new Cylinder[60]; //max number of hands simultaniously on the screen = amountOfHands * 15. (eg. 30 equals 2 hands)(each hand has 15 cylinder)
    
    EventHandler mouseB;
    private ArrayList<EventHandler> mouseBehaviorList = new ArrayList<>();
    
    File file;
    
    private ArrayList<Node> renderedUGXGeometries = new ArrayList<>();
    
    public static void main(String[] args) {
        launch(args);
    }
    private ArrayList<Rectangle> mousePlaneList = new ArrayList<>();
    final Rectangle mousePlane = new Rectangle(800, 800, Color.RED);
    
 
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            currentStage = primaryStage;
            ultraRoot = new Group();


            // Create camera
            camera = new PerspectiveCamera(true);
            camera.setFarClip(100000);

            // and position it
            camera.getTransforms().addAll(
                    new Rotate(0, Rotate.Y_AXIS),
                    new Rotate(0, Rotate.X_AXIS),
                    new Translate(0, 0, -50));

            // add camera as node to scene graph

            VBox guiGroup = addGuiElements();
            // Setup a scene
            SubScene subscene = createScene3D(ultraRoot, camera);
            VBox layout = new VBox( guiGroup, subscene);
            subscene.heightProperty().bind(layout.heightProperty());
            subscene.widthProperty().bind(layout.widthProperty());
            layout.setSpacing(0.0);
            scene = new Scene(layout, 1024, 768, true);
            
            scene.setFill(Color.DARKGRAY.darker().darker().darker().darker());
            //Add the scene to the stage and show the stage
            PointLight light2 = new PointLight(Color.LIGHTGRAY);
            ultraRoot.getChildren().add(light2);
            light2.getTransforms().add(new Translate(-50, 10, -520));

            AmbientLight light3 = new AmbientLight(new Color(0.35,0.35,0.35,1.0));
            ultraRoot.getChildren().add(light3);
            primaryStage.setScene(scene);
            primaryStage.show();

            handleKeyboard(scene, camera);
            //handleMouse(scene, camera);   
            

            listener = new LeapMotionListener();
            controller = new Controller();

            controller.addListener(listener);
            addGlobalLeapMotionPropertyListener();
            
            for (int i = 0; i < cArray.length; i++) { //initialize the cylinder array for the hand bones
                cArray[i] = new Cylinder(1, 1, 20); //using a fixed size array for the hand bones avoids memory issues
                cArray[i].setMouseTransparent(true); //but limits the max. amount of hands that can be registered at the same time
            //set it mouse transparent, so that it wont block the mouse click events by the robot
                cArray[i].setMaterial(handSphereMat);
            }                                       
            
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
     * Creates a group node visualization of a ugx file that was chosen by the user.
     *
     * @return group containing the ugx geometry with listeners that enable the node to be dragged and set new anchor points.
     */
    public Group createContent() {

        Group root = new Group();

        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Geometry File");
        
        //remember last directory
        if(file != null){
                    File existDirectory = file.getParentFile();
                    fileChooser.setInitialDirectory(existDirectory);
                }else{
            //or start a few folders above if its opened for the first time
            fileChooser.setInitialDirectory(new File(getClass().getResource("../../..").getFile()));
        }

        FileChooser.ExtensionFilter extFilterUGX = new FileChooser.ExtensionFilter("UGX files (*.ugx)", "*.ugx");
        FileChooser.ExtensionFilter extFilterSTL = new FileChooser.ExtensionFilter("STL files (*.stl)", "*.stl");
        fileChooser.getExtensionFilters().addAll(extFilterUGX,extFilterSTL);
        file = fileChooser.showOpenDialog(currentStage);
        


        String filePath = file.getAbsolutePath();
        if (filePath.endsWith(".stl")) {
            Group stl = buildSTL(filePath, Color.AQUA, false, true);
            VFX3DUtil.addMouseBehavior(stl, stl, MouseButton.PRIMARY,
                Rotate.X_AXIS, Rotate.Y_AXIS);
            return stl;
        }
        UGXReader ugxr = new UGXReader(filePath);
        ugxr.setFlagHighResolution(highResolution);
        ugxr.setFlagDoubleFacesOnEdges(true);
        //ugxr.setFlagRenderFaces(true);
        ugxr.setFlagRenderedElements(renderVertices, renderEdges, renderFaces, renderVolumes);
        ugxr.setFlagDebugMode(debugMode);
        ugxGeometry = ugxr.xbuildUGX();
        subsetNameArray = ugxr.getSubssetNameArray();
        root.getChildren().add(ugxGeometry);
        
        ugxSubsetCount = ugxr.getNumberOfSubsets();
        
        return enableNewFocusPoint(dragDrop(root,renderedUGXGeometries.size()),renderedUGXGeometries.size());
    }
    
    /**Handles keyboard input on a specified scene.
     * 
     * @param scene the scene, that should be affected by the input
     * @param camera the camera, that should be affected by the input
     */
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

    /**
     * Early implementation of a mouse handler. Needs to be tweaked to work correctly.
     * Might be worked on later.
     * @param scene the scene that recieves the changes
     * @param camera the camera that will be affected by the rotations
     */
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
        


    }
    

    /**Creates a visualization of a .stl file.
     * 
     * @param filePath the absolute file path of the .stl file
     * @param color the color the visualized file should have
     * @param ambient enables ambient light on the geometry
     * @param fill renders the faces or just shows the lines
     * @return the visualized STL file as a group node
     */
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
    
    
    /**Creates a MenuBar filled with several sub menus and returns them as a VBox
     * @return the VBox that contains all gui elements
    **/
    private VBox addGuiElements(){
        
        MenuBar menuBar = new MenuBar();
        
        Menu menuFile = new Menu("File");
        
        MenuItem fileOpenNewFile = new MenuItem("Open new file...");
        
        MenuItem fileOpenAdditionalFile = new MenuItem("Add file to scene...");
        
        MenuItem fileExit = new MenuItem("Exit");
        
        fileOpenNewFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                addGeometryToScene(true);
            }
        }); 
        
        fileOpenAdditionalFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                addGeometryToScene(false);
            }
        });
        
        fileExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        }); 
        
        menuFile.getItems().addAll(fileOpenNewFile,fileOpenAdditionalFile ,fileExit);
        
        Menu menuSettings = new Menu("Settings");
        
        CheckMenuItem settingsHighRes = new CheckMenuItem("High Resolution/Interaction");
        
        settingsHighRes.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            highResolution = new_val;
        }
    });
        
        CheckMenuItem settingsDebugMode = new CheckMenuItem("Debug Mode");
        settingsDebugMode.setSelected(true);
        settingsDebugMode.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            debugMode = new_val;
        }
    });
        
        CheckMenuItem settingsShowVertices = new CheckMenuItem("Show Vertices");
        settingsShowVertices.setSelected(true);
        settingsShowVertices.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            renderVertices = new_val;
        }
    });
        
        CheckMenuItem settingsShowEdges = new CheckMenuItem("Show Edges");
        settingsShowEdges.setSelected(true);
        settingsShowEdges.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            renderEdges = new_val;
        }
    });
        
        CheckMenuItem settingsShowFaces = new CheckMenuItem("Show Faces");
        settingsShowFaces.setSelected(true);
        settingsShowFaces.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            renderFaces = new_val;
        }
    });
        
        CheckMenuItem settingsShowVolumes = new CheckMenuItem("Show Volumes");
        settingsShowVolumes.setSelected(true);
        settingsShowVolumes.selectedProperty().addListener(new ChangeListener<Boolean>() {
        public void changed(ObservableValue ov,
        Boolean old_val, Boolean new_val) {
            renderVolumes = new_val;
        }
    });
        menuSettings.getItems().addAll(settingsHighRes,settingsDebugMode,settingsShowVertices,settingsShowEdges,settingsShowFaces,settingsShowVolumes);
        
        menuBar.getMenus().addAll(menuFile,menuSettings);
        
        VBox box = new VBox(menuBar);
        
        return box;
    }

    /**Creates a 3D subscene with a perspectiveCamera, where the specified group node acts as the parent node of
     * the subscene. Dont forget to make the subscene scale with the size of the whole scene, by binding its size
     * (mySubscene.heightProperty().bind(myVBox.heightProperty()))
     **/
    private SubScene createScene3D(Group group,PerspectiveCamera camera) {
    SubScene scene3d = new SubScene(group, sceneWidth, sceneHeight, true, SceneAntialiasing.DISABLED);
    scene3d.setFill(Color.DARKGRAY.darker().darker().darker().darker());
    scene3d.setCamera(camera);

    return scene3d;
    
  }
    /**Enables the node to be dragged around the screen with the mouse by using an invisible 2D rectangle that registers the dragging and converts the
     * movement to the node.
     * Use this method together with setupMousePlane, using the same index to make a node draggable.
     * @param node the node that should be draggable
     * @param index the index of the object on the scene
     */
    private Group dragDrop(Node node,int index){
        
        node.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    node.setMouseTransparent(true); // node will not be picked
                    mousePlaneList.get(index).setMouseTransparent(false); // mousePlane will be pickable
                    node.startFullDrag(); // this redirects drag events from the origin (node) to the picked target (which will be mousePlane)
                }
            }
        });

        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!event.isSecondaryButtonDown()) {
                    node.setMouseTransparent(false);
                    mousePlaneList.get(index).setMouseTransparent(true);
                }
            }
        });

        mousePlaneList.get(index).setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                if (event.isSecondaryButtonDown()) {
                    Point3D coords = event.getPickResult().getIntersectedPoint();
                    double x = coords.getX();
                    double y = coords.getY();
                    double z = coords.getZ();

                    coords = mousePlaneList.get(index).localToParent(new Point3D(x, y, z)); //mouseplane has the same parent as the real plane and objects like cube

                    node.setTranslateX(coords.getX());
                    node.setTranslateY(coords.getY());
                    node.setTranslateZ(coords.getZ());
                }
            }
        });

        return (Group) node;

    }
    
    /** Sets up the invisible mouse plane that is needed for the dragging of nodes on the scene using the mouse.
     * The index of the mouse plane should be the same as the index for enableNewFocusPoint.
     * @param index the index of the rectangle in a rectangle list that will be used for dragging that specific object
     * 
     */
    private Rectangle setupMousePlane(int index) {

        mousePlaneList.get(index).setLayoutX(-800 / 2);
        mousePlaneList.get(index).setLayoutY(-800 / 2);
        mousePlaneList.get(index).setOpacity(0.7);
        mousePlaneList.get(index).setMouseTransparent(true);
        mousePlaneList.get(index).setDepthTest(DepthTest.DISABLE); // this makes the plane to be picked even if there are objects closer to the camera

        mousePlaneList.get(index).setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent me) {
                if (me.isSecondaryButtonDown() && me.isAltDown()) {
                    //do nothing, we are rotating on the gizmo
                } else if (me.isSecondaryButtonDown()) {
                    Point3D coords = me.getPickResult().getIntersectedPoint();
                    mouseOldX = mousePosX;
                    mouseOldY = mousePosY;
                    mousePosX = coords.getX();
                    mousePosY = coords.getY();

                    double z = coords.getZ();

                    coords = mousePlaneList.get(index).localToParent(new Point3D(mousePosX, mousePosY, z)); //mouseplane has the same parent as the real plane and objects like cube
                    mousePosX = coords.getX();
                    mousePosY = coords.getY();
                    mouseDeltaX = (mousePosX - mouseOldX);
                    mouseDeltaY = (mousePosY - mouseOldY);

                }
            }
        });
        return mousePlaneList.get(index);
    }

    /**Allows to set a new rotation focus point on a group by clicking on it, while the alt key is down.
     * The index should be the same as the index for setupMousePlane.
     * @param node the group node that should be able to have a new rotation point
     * @param index the index of the specific mouse behaviour for that specific node (each geometry has exactly 1 mouse behaviour)
     */
    private Group enableNewFocusPoint(Group node, int index){

            //incase a new object was added to the scene, the mouse behavior will be applied to it
            mouseBehaviorList.add(new MouseBehaviorImpl1(node, MouseButton.PRIMARY, Rotate.X_AXIS, Rotate.Y_AXIS)) ;
            node.addEventHandler(MouseEvent.ANY, mouseBehaviorList.get(mouseBehaviorList.size()-1));

        node.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.isAltDown() && event.getButton().equals(MouseButton.PRIMARY)   ) {
                //remove old rotation anchor, so it wont rotate around multiple points

                    node.removeEventHandler(MouseEvent.ANY, mouseBehaviorList.get(index));
                    //set new rotation anchor point
                    Point3D pt = event.getPickResult().getIntersectedPoint();
                    
                    mouseB = new MouseBehaviorImpl1(node, MouseButton.PRIMARY, Rotate.X_AXIS, Rotate.Y_AXIS, pt); 
                    
                    node.addEventHandler(MouseEvent.ANY, mouseB);
                    
                    mouseBehaviorList.set(index, mouseB);
               
                    if (debugMode) {
                        System.out.println("New rotation anchor for " + node.toString() + " was set at \n"+ pt.toString());
                }
                }
           
        }     
        );
        

        return node;
    }
    
    /**Adds a new visualization of a file to the screen by either replacing all other geometries or just adding it to the scene.
     * @param replaceOldScene true if the next rendered geometry should be the only one on the scene. false if it should just be added to the scene
     */
    private void addGeometryToScene(boolean replaceOldScene){
        
        if (replaceOldScene) {
            for (int i = (ultraRoot.getChildren().size()-1); i > 1 ; i--) { //remove every node except the light 
                    ultraRoot.getChildren().remove(i);
                }
                mouseBehaviorList.clear();
                renderedUGXGeometries.clear();
                mousePlaneList.clear();
                mousePlaneList.add(new Rectangle(800, 800, Color.TRANSPARENT));
                Rectangle rect = setupMousePlane(0);
                renderedUGXGeometries.add(createContent());
               
                ultraRoot.getChildren().add(rect);
                ultraRoot.getChildren().add(new Group(renderedUGXGeometries.get(0)));
                
                addNodeLeapMotionPropertyListener(renderedUGXGeometries.get(0));
                
        }else{
            
            int size = renderedUGXGeometries.size();
                mousePlaneList.add(new Rectangle(800, 800, Color.TRANSPARENT));
                Rectangle rect = setupMousePlane(size);
                Group newG = new Group(createContent());
                
                renderedUGXGeometries.add(newG);
                addNodeLeapMotionPropertyListener(renderedUGXGeometries.get(size));

                ultraRoot.getChildren().addAll(newG,rect);
        }
        
    }
    

    /**
     * Adds the hand model to the scene and updates it with the most recent
     * information on the hand location.
     */
    private void addGlobalLeapMotionPropertyListener() {

        //listen to changed positions of the hand and add the model to the view
        listener.leftFingerChangedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            
            
            if (newValue) {

                ArrayList<Bone> leftHandBones = listener.getLeftBones();

                Platform.runLater(() -> { //javaFX thread

                    handGroup.getChildren().remove(leftHandGroup);
                    leftHandGroup.getChildren().clear();
                    int i = 0;

                    for (Bone b : leftHandBones) {

                        {
                            final Vector p = b.center();
                                // create bone as a vertical cylinder and locate it at its center position

                            cArray[i].setRadius(b.width() / 4); // use the cylinder in the cArray, DO NOT create new cylinders every frame as it would
                            cArray[i].setHeight(b.length()); // lead to memory issues
                            cArray[i].getTransforms().clear(); // remove all previous transforms

                            cArray[i].setMaterial(handSphereMat);

                            // translate and rotate the cylinder towards its direction
                            {
                                final Vector v2 = b.direction();
                                Vector vDirection = new Vector(v2.getX(), -v2.getY(), -v2.getZ());
                                Vector cross2 = vDirection.cross(yNegVector);
                                double ang2 = vDirection.angleTo(yNegVector);
                                Translate translateToMiddle = new Translate(p.getX(), -p.getY() + 200, -p.getZ());
                                Point3D crossProd = new Point3D(cross2.getX(), -cross2.getY(), cross2.getZ());
                                Rotate rotateToConnectFingers = new Rotate(-Math.toDegrees(ang2), 0, 0, 0, crossProd);
                                cArray[i].getTransforms().addAll(translateToMiddle, rotateToConnectFingers);
                            }
                            cArray[i].setScaleX(0.1);
                            cArray[i].setScaleY(0.1);
                            cArray[i].setScaleZ(0.1);

                            leftHandGroup.getChildren().addAll(cArray[i]);
                            i++;
                        }
                    }

                    handGroup.getChildren().addAll(leftHandGroup);
                    if (!ultraRoot.getChildren().contains(handGroup)) {
                        ultraRoot.getChildren().add(handGroup);
                        if (debugMode) {
                            System.out.println("Left hand  " + leftHandGroup.toString() + " entered the view.");
                        }
                    }
                });
            }
        });

        listener.rightFingerChangedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {

                ArrayList<Bone> rightHandBones = listener.getRightBones();

                Platform.runLater(() -> { //javaFX thread

                    handGroup.getChildren().remove(rightHandGroup);
                    rightHandGroup.getChildren().clear();

                    int i = leftHandGroup.getChildren().size();
                    for (Bone b : rightHandBones) {

                        {
                            final Vector p = b.center();
                            // create bone as a vertical cylinder and locate it at its center position
                            cArray[i].setRadius(b.width() / 4); // use the cylinder in the cArray, DO NOT create new cylinders every frame as it would
                            cArray[i].setHeight(b.length()); // lead to memory issues
                            cArray[i].getTransforms().clear(); // remove all previous transforms

                            if (i % 15 == 5) {
                                cArray[i].setMaterial(handSphereMat);
                            }

                            // translate and rotate the cylinder towards its direction
                            {
                                final Vector v2 = b.direction();
                                Vector vDirection = new Vector(v2.getX(), -v2.getY(), -v2.getZ());
                                Vector cross2 = vDirection.cross(yNegVector);
                                double ang2 = vDirection.angleTo(yNegVector);
                                Translate translateToMiddle = new Translate(p.getX(), -p.getY() + 200, -p.getZ());
                                Point3D crossProd = new Point3D(cross2.getX(), -cross2.getY(), cross2.getZ());
                                Rotate rotateToConnectFingers = new Rotate(-Math.toDegrees(ang2), 0, 0, 0, crossProd);
                                cArray[i].getTransforms().addAll(translateToMiddle, rotateToConnectFingers);
                            }
                            cArray[i].setScaleX(0.1);
                            cArray[i].setScaleY(0.1);
                            cArray[i].setScaleZ(0.1);

                            rightHandGroup.getChildren().addAll(cArray[i]);
                            //handGroup.getChildren().addAll(cArray[i]);
                            i++;
                        }
                    }

                    handGroup.getChildren().addAll(rightHandGroup);
                    if (!ultraRoot.getChildren().contains(handGroup)) {
                        ultraRoot.getChildren().add(handGroup);
                        if (debugMode) {
                            System.out.println("Right hand  " + rightHandGroup.toString() + " entered the view.");
                        }
                    }
                });
            }
        });

        listener.leftHandVisibleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //remove the model of the left hand from the scene, when there is no left hand visible for the device
            if (!newValue) {
                Platform.runLater(() -> {
                    if (debugMode) {
                        System.out.println("Left hand out of view");
                    }
                    handGroup.getChildren().remove(leftHandGroup);
                    if (!handGroup.getChildren().contains(rightHandGroup)) { //if both hands are out, remove the node from the root node
                        ultraRoot.getChildren().remove(handGroup);
                        
                        //set a new color to the hand material, so the next time a hand enters the view it will have a different color
                        handSphereMat.setDiffuseColor(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1));
                    }
                    listener.clearInfo();
                    leftHandJustEntered = true;
                    r.setOpacity(0);
                });
            }
        });

        listener.rightHandVisibleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //remove the model of the right hand from the scene, when there is no right hand visible for the device
            if (!newValue) {
                Platform.runLater(() -> {
                    if (debugMode) {
                        System.out.println("Right hand out of view");
                    }
                    handGroup.getChildren().remove(rightHandGroup);
                    if (!handGroup.getChildren().contains(leftHandGroup)) { //if both hands are out, remove the node from the root node
                        ultraRoot.getChildren().remove(handGroup);
                        
                        //set a new color to the hand material, so the next time a hand enters the view it will have a different color
                        handSphereMat.setDiffuseColor(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1));
                        leftHandJustEntered = true;
                    }
                    listener.clearInfo();
                    r.setOpacity(0);
                });
            }
        });
    }

    /**
     * Adds predefined property listeners to the leap motion listener, attached
     * to node n.
     *
     * @param n the node that should be affected by the leap motion gestures
     */
    private void addNodeLeapMotionPropertyListener(Node m) {

            listener.posHandLeftProperty().addListener((ObservableValue<? extends Point3D> ov, Point3D t, final Point3D t1) -> {
                Platform.runLater(() -> {
                    
                    //add a small delay to make sure the device will have the right information about the hand
                    if (handGroup.getChildren().contains(leftHandGroup) && leftHandJustEntered && listener.leftHandConfidenceProperty().get()> 0.70) {
                        leftHandJustEntered = false;
                        leftHandJustEnteredAtFrameID = controller.frame().id();
                    }

                    if (controller.frame().id() - leftHandJustEnteredAtFrameID > 20 && !leftHandJustEntered) {

                        palmZ = listener.palmZcoordinatePropery().get();
                        if (t1 != null) {
                            double roll = listener.rollLeftProperty().get();
                            double pitch = -listener.pitchLeftProperty().get();
                            double yaw = -listener.yawLeftProperty().get();
                            matrixRotateNode(m, roll / 2, pitch / 2, yaw / 2);
                        }
                    }
                });
            });
            
        listener.rightPalmZcoordinateProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

            Platform.runLater(() -> {

                int index = handGroup.getChildren().indexOf(rightHandGroup);
                if (index != -1) {
                    Group tempHandGroup = (Group) handGroup.getChildren().get(index);
                    rGroup.getChildren().clear();
                    for (Node n : renderedUGXGeometries) {
                        
                        for (Node cylinderNode : tempHandGroup.getChildren()) {
                            
                            if (tempHandGroup.getChildren().indexOf(cylinderNode) % 15 == 5) { //only detect the cylinder that represents the tip of the index finger
                                Bounds b = cylinderNode.localToScreen(cylinderNode.getBoundsInLocal());
                                if (b.intersects(n.localToScreen(n.getBoundsInLocal()))) {
                                    //display a red circle around the tip of the index finger when it crossed a bounding box
                                    r = new Circle((cylinderNode.getBoundsInParent().getMinX() + cylinderNode.getBoundsInParent().getMaxX() + 1) / 2,
                                            (cylinderNode.getBoundsInParent().getMinY() + cylinderNode.getBoundsInParent().getMaxY() - 1) / 2,
                                            (cylinderNode.getBoundsInParent().getHeight() + cylinderNode.getBoundsInParent().getWidth()) / 4, Color.RED);
                                    
                                    ((Cylinder) cylinderNode).setMaterial(new PhongMaterial(Color.WHITESMOKE));
                                    
                                    r.setOpacity(0.75);
                                    rGroup.getChildren().add(r);
                                    
                                    Point2D screenCoordinates = cylinderNode.localToScreen(new Point2D(cylinderNode.getTranslateX(), cylinderNode.getTranslateY()));
                                    
                                    //simulate a mouse click 
                                    //move mouse to the position of the tip of the right index finger
                                    wall_e.mouseMove((int) screenCoordinates.getX(), (int) screenCoordinates.getY());
                                    if (listener.keyTapMiddleFingerProperty().get() && controller.frame().id() - lastTap > 20) {
                                        
                                        if (debugMode) {
                                            System.out.println("Clicked screen coordinates " + screenCoordinates.toString());
                                        }
                                        wall_e.mousePress(2); //press right mouse button

                                        wall_e.mouseRelease(2); //release right mouse button
                                        lastTap = controller.frame().id();
                                    }
                                }
                            }
                        }
                        if (!ultraRoot.getChildren().contains(rGroup)) {
                            ultraRoot.getChildren().add(rGroup);
                        }
                    }
                }
            });
        });

        listener.circleGestureVectorProperty().addListener((ObservableValue<? extends Point3D> observable, Point3D oldValue, Point3D newValue) -> {
            Platform.runLater(() -> {

                //circle gesture logic of the right hand
                if (newValue.getX() != oldValue.getX() && rotateAnimation.getStatus() == Animation.Status.STOPPED
                        && controller.frame().id() - lastTap > 70) { //minimum of 70 frames before it will accept new circle gestures

                    for (Node geometry : renderedUGXGeometries) {
                        rotateAnimation.setNode(geometry);
                        rotateAnimation.setAxis(newValue);
                        rotateAnimation.setDuration(Duration.seconds(6));
                        rotateAnimation.setByAngle(360);

                        rotateAnimation.play();
                        lastTap = controller.frame().id();
                    }

                } else if (newValue.getX() != oldValue.getX() && rotateAnimation.getStatus() == Animation.Status.RUNNING
                        && controller.frame().id() - lastTap > 70) { //stop animation if another circle gesture is detected during the runtime
                    rotateAnimation.stop();
                    lastTap = controller.frame().id();
                    if (debugMode) {
                        System.out.println("Animation stopped");
                    }
                }
            });
        });
    }

    /**Rotates a node by using a matrix.
     * Credits to José Pereda (http://jperedadnr.blogspot.de/)
     * 
     * @param n the node that needs to be rotated
     * @param alf rotation around x axis
     * @param bet rotation around y axis
     * @param gam rotation around z axis
    */
    private void matrixRotateNode(Node n, double alf, double bet, double gam){
        double A11=Math.cos(alf)*Math.cos(gam);
        double A12=Math.cos(bet)*Math.sin(alf)+Math.cos(alf)*Math.sin(bet)*Math.sin(gam);
        double A13=Math.sin(alf)*Math.sin(bet)-Math.cos(alf)*Math.cos(bet)*Math.sin(gam);
        double A21=-Math.cos(gam)*Math.sin(alf);
        double A22=Math.cos(alf)*Math.cos(bet)-Math.sin(alf)*Math.sin(bet)*Math.sin(gam);
        double A23=Math.cos(alf)*Math.sin(bet)+Math.cos(bet)*Math.sin(alf)*Math.sin(gam);
        double A31=Math.sin(gam);
        double A32=-Math.cos(gam)*Math.sin(bet);
        double A33=Math.cos(bet)*Math.cos(gam);
         
        double d = Math.acos((A11+A22+A33-1.0)/2.0);
        if(d!=0){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d)); 
            n.setTranslateZ(-palmZ/8);
            
        }
    }
    
}
