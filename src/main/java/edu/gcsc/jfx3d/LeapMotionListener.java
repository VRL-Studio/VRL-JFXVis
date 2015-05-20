/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.jfx3d;

/**
 *
 * @author Eugen
 */
import java.io.IOException;
import java.lang.Math;
import com.leapmotion.leap.*;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

class LeapMotionListener extends Listener {
    
    private final ObjectProperty<Point3D> posHandLeft=new SimpleObjectProperty<>();
    private final DoubleProperty pitchLeft=new SimpleDoubleProperty(0);
    private final DoubleProperty rollLeft=new SimpleDoubleProperty(0);
    private final DoubleProperty yawLeft=new SimpleDoubleProperty(0);
    private final LimitQueue<Vector> posLeftAverage = new LimitQueue<>(10);
    private final LimitQueue<Double> pitchLeftAverage = new LimitQueue<>(10);
    private final LimitQueue<Double> rollLeftAverage = new LimitQueue<>(10);
    private final LimitQueue<Double> yawLeftAverage = new LimitQueue<>(10);
    
    private final DoubleProperty palmZcoordinates = new SimpleDoubleProperty(0);
    private final DoubleProperty rightPalmZcoordinates = new SimpleDoubleProperty(0);
    private final DoubleProperty leftHandConfidence = new SimpleDoubleProperty();
    private final DoubleProperty rightHandConfidence = new SimpleDoubleProperty();
    
    private final BooleanProperty fingerChanged= new SimpleBooleanProperty(false);
    private final BooleanProperty rightFingerChanged= new SimpleBooleanProperty(false);
    private final ArrayList<Bone> leftHandBones=new ArrayList<>();
    private final ArrayList<Bone> rightHandBones = new ArrayList<>();

    private final ObjectProperty<Point2D> point=new SimpleObjectProperty<>();
    
    private final LimitQueue<Vector> positionAverage = new LimitQueue<>(10);

    private final BooleanProperty leftHandVisible = new SimpleBooleanProperty(false);
    private final BooleanProperty rightHandVisible = new SimpleBooleanProperty(false);
    private final Vector pos = new Vector(0, 0, 0);
    
    private final BooleanProperty keyTapMiddleFinger = new SimpleBooleanProperty(false);
    private final ObjectProperty<Point3D> circleGestureVector=new SimpleObjectProperty<>();
    
    @Override
    public void onInit(Controller controller) {
        //System.out.println("Leap Motion Device initialized");
    }

    @Override
    public void onConnect(Controller controller) {
        System.out.println("Leap Motion Device connected");
//        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
//        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        circleGestureVector.set(Point3D.ZERO);
        controller.config().setFloat("Gesture.Circle.MinRadius", 40.0f);
        controller.config().setFloat("Gesture.Circle.MinArc", 4.0f);
        controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 70.0f);
        controller.config().setFloat("Gesture.KeyTap.HistorySeconds", .6f);
        controller.config().setFloat("Gesture.KeyTap.MinDistance", 11.0f);
        controller.config().save();
        
    }

    @Override
    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Leap Motion Device disconnected");
    }

    @Override
    public void onExit(Controller controller) {
        controller.delete();
        this.delete();
        System.out.println("Exited");
    }

    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            
            Screen screen = controller.locatedScreens().get(0);
            if (screen != null && screen.isValid()) {
                keyTapMiddleFinger.set(false);

                for (Hand hand : frame.hands()) {
                    if (hand.isValid()) {
                        if (hand.isLeft()) {

                            leftHandConfidence.set(hand.confidence());
                            leftHandVisible.set(true);
                            //credits to José Pereda (http://jperedadnr.blogspot.de/) 
                            pitchLeftAverage.add(new Double(hand.direction().pitch()));
                            rollLeftAverage.add(new Double(hand.palmNormal().roll()));
                            yawLeftAverage.add(new Double(hand.direction().yaw()));
                            pitchLeft.set(dAverage(pitchLeftAverage));
                            rollLeft.set(dAverage(rollLeftAverage));
                            yawLeft.set(dAverage(yawLeftAverage));

                            //enable rotation of the object by using the left hand
                            if (hand.grabStrength() < 0.85f && hand.isLeft()) {
                                Vector intersect = screen.intersect(hand.palmPosition(), hand.direction(), true);
                                posLeftAverage.add(intersect);
                                Vector avIntersect = Average(posLeftAverage);
                                posHandLeft.setValue(new Point3D(screen.widthPixels() * Math.min(1.0, Math.max(0.0, avIntersect.getX())),
                                        screen.heightPixels() * Math.min(1.0, Math.max(0d, (1.0 - avIntersect.getY()))),
                                        hand.palmPosition().getZ()));
                            }
                            
                            if (frame.hands().count() == 1) { //if the left hand is the only one on the view
                                rightHandVisible.set(false);
                            }
                        }

                        if (hand.isRight()) {
                            rightHandConfidence.set(hand.confidence());
                            rightHandVisible.set(true);
                            for (Gesture g : frame.gestures()) {
                                //detect keytap gesture with the right hand
                                if (g.type() == Gesture.Type.TYPE_KEY_TAP && hand.isRight()) {
                                    KeyTapGesture keyTap = new KeyTapGesture(g);
                                    Finger finger = new Finger(keyTap.pointable());
                                    if (finger.type() == Finger.Type.TYPE_MIDDLE) {
                                        keyTapMiddleFinger.set(true);
                                    }
                                }

                                //detect circle gesture with the right hand
                                if (g.type() == Gesture.Type.TYPE_CIRCLE && hand.isRight()) {
                                    CircleGesture circleG = new CircleGesture(g);
                                    Finger finger = new Finger(circleG.pointable());
                                    if (finger.type() == Finger.Type.TYPE_INDEX) {
                                        Point3D p = new Point3D(circleG.normal().getX(),
                                                circleG.normal().getY(),
                                                -circleG.normal().getZ());
                                        //negative z to transform it into the javafx coordinate system
                                        circleGestureVector.set(p);
                                    }
                                }
                            }
                            if (frame.hands().count() == 1) {
                                leftHandVisible.set(false);
                            }
                            
                        }

                    //get the bones of the fingers to show them in the javafx thread
                    fingerChanged.set(false);
                    rightFingerChanged.set(false);
                    leftHandBones.clear();
                    rightHandBones.clear();
                        
                    
                    for (Finger finger : hand.fingers()) {
                        if (finger.isValid()) {
                            for (Bone.Type b : Bone.Type.values()) {
                                    if (!b.equals(Bone.Type.TYPE_METACARPAL) && hand.isLeft()) { // only save the fingers
                                    leftHandBones.add(finger.bone(b));
                                   
                                }
                                
                                    if (!b.equals(Bone.Type.TYPE_METACARPAL) && hand.isRight()) {
                                    rightHandBones.add(finger.bone(b));
                                   
                                }
                            }
                        }
                    }
                    
                    fingerChanged.set(!leftHandBones.isEmpty());
                    rightFingerChanged.set(!rightHandBones.isEmpty());

                        if (hand.isLeft()) {
                            Vector palmPosition = hand.palmPosition();
                            pos.setX(palmPosition.getX());
                            pos.setY(palmPosition.getY());
                            pos.setZ(palmPosition.getZ());
                            
                            palmZcoordinates.set(pos.getZ());
                        }
                        if (hand.isRight()) {
                            Vector palmPosition = hand.palmPosition();
                            pos.setX(palmPosition.getX());
                            pos.setY(palmPosition.getY());
                            pos.setZ(palmPosition.getZ());
                            
                            rightPalmZcoordinates.set(pos.getZ());
                            
                        }
                        
                }
            }
        } 
                }else {
            leftHandVisible.set(false);
            rightHandVisible.set(false);
        }

    }

    @Override
    public void onFocusLost(Controller controller){
        System.out.println("FOCUS LOST");
    }
    
    public void clearInfo(){
        this.leftHandBones.clear();
        this.pitchLeftAverage.clear();
        this.posLeftAverage.clear();
        this.positionAverage.clear();
        this.rollLeftAverage.clear();
        this.yawLeftAverage.clear();
        
    }
    
    public ArrayList<Bone> getLeftBones(){ 
        // Returns a fresh copy of the bones collection 
        // to avoid concurrent exceptions iterating this list
        return (ArrayList<Bone>) leftHandBones.clone();
    }
    
    public ArrayList<Bone> getRightBones(){
        return (ArrayList<Bone>) rightHandBones.clone();
    }
    
    public BooleanProperty leftFingerChangedProperty() { 
        return fingerChanged; 
    }
    
    public BooleanProperty rightFingerChangedProperty() { 
        return rightFingerChanged; 
    }
    
    public BooleanProperty leftHandVisibleProperty(){
        return leftHandVisible;
    }
    
    public BooleanProperty rightHandVisibleProperty(){
        return rightHandVisible;
    }
    
    private Vector Average(LimitQueue<Vector> vectors)
    {
        float vx=0f, vy=0f, vz=0f;
        for(Vector v:vectors){
            vx=vx+v.getX(); vy=vy+v.getY(); vz=vz+v.getZ();
        }
        return new Vector(vx/vectors.size(), vy/vectors.size(), vz/vectors.size());
    }
    
    private Double dAverage(LimitQueue<Double> vectors){
        double vx=0;
        for(Double d:vectors){
            vx=vx+d;
        }
        return vx/vectors.size();
    }
    
    private class LimitQueue<E> extends LinkedList<E> {
        private int limit;
        public LimitQueue(int limit) {
            this.limit = limit;
        }
 
        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) { super.remove(); }
            return true;
        }
    }
    
    public ObservableValue<Point3D> posHandLeftProperty(){
        return posHandLeft;
    }
    public DoubleProperty yawLeftProperty(){ 
        return yawLeft; 
    }
    public DoubleProperty pitchLeftProperty(){ 
        return pitchLeft; 
    }
    public DoubleProperty rollLeftProperty(){ 
        return rollLeft; 
    }
    public ObservableValue<Point2D> pointProperty(){ 
        return point; 
    }
    
    public DoubleProperty palmZcoordinatePropery(){
        return palmZcoordinates;
    }
    
    public DoubleProperty rightPalmZcoordinateProperty(){
        return rightPalmZcoordinates;
    }
    
    public BooleanProperty keyTapMiddleFingerProperty(){
        return keyTapMiddleFinger;
    }
    
    public ObservableValue<Point3D> circleGestureVectorProperty(){
        return circleGestureVector;
    }
    
    public DoubleProperty leftHandConfidenceProperty(){
        return leftHandConfidence;
    }
    public DoubleProperty rightHandConfidenceProperty(){
        return rightHandConfidence;
    }
    
}