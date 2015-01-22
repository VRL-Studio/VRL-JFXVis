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

Open the `JFX3DSample` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.0) and build it
by calling the `run` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/JFX3DSample`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    sh gradlew run
    
#### Windows (CMD)

    gradlew run

#### Controls

- Rotate the object with your mouse button(left button to rotate x,y axis, right button to rotate x,z axis)

- Cycle through subsets : +

- Show all subsets : -

- Move camera : w,a,s,d (hold down shift to move faster)
