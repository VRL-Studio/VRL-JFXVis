VRL-JFXVis
===========

Visualization Plugin for VRL

Visualises .ugx files in Java using JavaFX3D! (Work in progress)

![](/resources/img/sample.jpg)

## How to Run It

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `VRL-JFXVis` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.0) and build it
by calling the `run` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/VRL-JFXVis`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    sh gradlew run
    
#### Windows (CMD)

    gradlew run

#### Controls

- Rotate the object with your left mouse button(rotation around x,y axis)

- Select the underlying geometry part with the right mouse button

- Hold down the alt button while right clicking to select multiple parts

- Cycle through subsets : +

- Show all subsets : -

- Move camera : w,a,s,d (hold down shift to move faster)

#### Leap Motion controls

## Left Hand 

- Rotates the geometry, based on the position of the left hand.

- Move the hand towards you to bring the geometry closer, move the hand away to move the geometry further away.

- Close the left hand to stop the rotation of the geometry. Open the hand again to resume it.

## Right Hand

- Enables selection and rotation of the geometry by using gestures

- The tip of the right index finger will be highlithed in white color, whenever it crosses the bounding box of the geometry

- To select a part of the geometry, hold the tip of the right index finger over the respective part and perform a [keytap gesture] (https://developer.leapmotion.com/documentation/java/api/Leap.KeyTapGesture.html?proglang=java) with the right middle finger
  TIP: The mouse cursor will follow the center position of the tip of the index finger while it is hovering over the geometry.
 
- To do a rotation animation, perform a [circle gesture] (https://developer.leapmotion.com/documentation/java/api/Leap.CircleGesture.html#id23) with the right index finger.

- The geometry will rotate in the same direction as the circle was drawn

- Perform another circle gesture while the geometry is still rotating to stop the rotation