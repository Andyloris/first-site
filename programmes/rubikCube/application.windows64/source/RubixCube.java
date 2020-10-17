import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import static javax.swing.JOptionPane.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class RubixCube extends PApplet {


//-------------------------------------------------------------------------------------------------
//Hey bro, here are some values you can change so go nuts...
//actually probably dont
//dont go too big (trust me)
//dont do even sized cubes (it wont work)
//Have fun
//while (snto == null) {
  //snto = showInputDialog("entrer le nombre de faces");
//}



int numberOfSides = PApplet.parseInt(showInputDialog("entrer le nombre de faces"));//<<< change this to change the size of the cube
float cubeSpeed = PI/2; //<<< this is the speed that the cube rotates 

/*
  some controls
 L lock/unlock cube (allows you to turn the cube with the mouse)
 SPACE pause/play (the program will automatically pause after the scramble, you will need to play the thing when you wanna start the scramble and when you wanna start the solve.
 */

//----------------------------------------------------------------------------------------------

int blockWidth = 400/numberOfSides; //you can change this value to increase the size of the cube in the window
//also if you've got a 4k monitor then check out line 97


//Global colors

int green = color(0, 255, 0);
int blue = color(0, 0, 255);
int red = color(255, 0, 0);
int white = color(255);
int yellow = color(255, 255, 0);
int orange =  color(255, 140, 0);



int n = numberOfSides;//better variable name
int middle = n/2;
int faceWidth = blockWidth;
int blacksShown = 0;
int speedUpCounter = 1;
boolean pause = true;
int rotateYCounter = 0;
int rotateXCounter = 0;
Cube cube;
int stageTestCounter =100;
int scrambleCounter = 100;
String turns = "";
ArrayList<TurnO> turnOs = new ArrayList();

int moveCount = 0;
int solveCounter =0;

boolean fixCubeRotation = false;
float  XRotationCompensation = 0;
float  YRotationCompensation = 0;
float  ZRotationCompensation = 0;

ArrayList<String> rotations = new ArrayList();
int[] turnCounter = new int[7];

float spinCounter =0;

boolean lockRotation = true;
float xRotationKey = 0;
float yRotationKey = 0;
float xRotationKeyTarget = 0;
float yRotationKeyTarget= 0;
//float zRotationKey = 0;
float rotationSpeed = PApplet.parseInt(showInputDialog("vitesse de rotaion du rubiks cube"));

int frameToStop = 0;

boolean justScrambled = true;
int pauseCounter = 0;

boolean showingOffFace = true;
float xTargetRotation = PI;
float yTargetRotation = PI;

int previousStageNo = 0;
int previousLargeCubeStageNo = 0;

boolean actualPause = false;
PVector lockedMousePos = new PVector();
PVector[] edgeCenters= {new PVector(middle, 0, 0), new PVector(n-1, 0, middle), new PVector(middle, 0, n-1), 
  new PVector(0, 0, middle), new PVector(middle, n-1, 0), new PVector(0, n-1, middle), new PVector(middle, n-1, n-1), new PVector(n-1, n-1, middle), 
  new PVector(0, middle, 0), new PVector(n-1, middle, 0), new PVector(0, middle, n-1), new PVector(n-1, middle, n-1)};

boolean firstPause = true;


boolean enableMouseX = true;
boolean enableMouseY = true;
boolean enableSpin = false;
boolean autoShow = false;
boolean repeatSolves = false;
int solveCounterThing =1;
int startTime =0;
public void setup() {
  
  //size(2000, 2000, P3D); //if you've got a 4K monitor then you can uncomment this out to have a bigger window, also make sure to comment out the line before 

  //turns+= "FFULL";
  cube = new Cube();
  for (int i= 0; i < 7; i++) {
    turnCounter[i] = 0;
  }
  cube.algos.scramble();
  //turns+= "XYZXYZXYZXYZXYZXYZXYZXYZYXYZXYZXYZXYZYXYZXYXZ";
  frameRate(60);
}

public void resetCube() {
  cube = new Cube();
  cube.algos.scramble();
}

public void draw() {
  if (!actualPause) {

    if (autoShow) {
      if (!pause && !cube.scrambling && cubeSpeed < PI) {
        if (cubeSpeed > PI/3) {
          cubeSpeed =PI;
          speedUpCounter =1;
        } else {
          cubeSpeed+=0.005f;
        }
      }
    }
    println(cubeSpeed, PI);
    //cubeSpeed = PI;
    cube.rotationSpeed = cubeSpeed;
    //if (showingOffFace) {
    //  //pause = true;
    //  float wholeCubeRotationSpeed = 2;
    //  yRotationKey += yTargetRotation-yRotationKey/ abs(yRotationKey-yTargetRotation)*wholeCubeRotationSpeed;
    //  xRotationKey += xTargetRotation-xRotationKey/ abs(xRotationKey-xTargetRotation)*wholeCubeRotationSpeed;
    //}


    //println(frameRate);
    pushMatrix();

    translate(width/2, height/2, 0);
    rotateX(-PI/6);
    if (autoShow) {
      if (!lockRotation && pause && spinCounter/rotationSpeed>2*PI) {
        //println(spinCounter);
        pause = false;
        lockRotation=true;
        spinCounter = 0;

        switch(cube.algos.largeCubeStageNo) {
        case 1:
          xRotationKeyTarget -=PI/2.0f;
          break;
        case 2:
          xRotationKeyTarget +=PI/2.0f;
          break;
        case 9:
          yRotationKeyTarget +=PI/2.0f;
          break;
        case 10:
          yRotationKeyTarget -=PI/2.0f;
          break;
        }
        if (cube.algos.stageNo ==7) {
          actualPause = true;
        }
      }
    }

    if (abs(yRotationKey - yRotationKeyTarget)>0.15f) {
      if (yRotationKey < yRotationKeyTarget) {
        yRotationKey+=0.1f;
      } else {
        yRotationKey-=0.1f;
      }
    } else {

      yRotationKey = yRotationKeyTarget;
    }
    if (abs(xRotationKey - xRotationKeyTarget) >0.15f) {
      if (xRotationKey < xRotationKeyTarget) {
        xRotationKey+=0.1f;
      } else {
        xRotationKey-=0.1f;
      }
    } else {

      xRotationKey = xRotationKeyTarget;
    }
    if (lockRotation) {
      rotateY(PI/5- yRotationKey);
      rotateX(-xRotationKey);
    } else {
      //println(lockedMousePos, mouseX, mouseY);

      if (enableMouseY) {
        rotateX(-xRotationKey +((lockedMousePos.y -mouseY))/rotationSpeed);
      } else {
        rotateX(-xRotationKey);
      }

      if (enableMouseX) {
        rotateY(PI/5- yRotationKey-((lockedMousePos.x -mouseX) + spinCounter)/rotationSpeed);
      } else {
        //rotateY(PI/5- yRotationKey);

        rotateY(PI/5- yRotationKey-(spinCounter)/rotationSpeed);
      }

      if (enableSpin) {
        spinCounter+=1.0f;
      }
    }  


    for (int i = 0; i< rotations.size(); i++) {
      int rotationDirection = 1;

      if (rotations.get(i).length() ==2) {
        rotationDirection = -1;
      } 

      switch(rotations.get(i).charAt(0)) {
      case 'X':
        rotateX(rotationDirection * PI/2);
        break;
      case 'Y':
        rotateY(rotationDirection * PI/2);
        break;
      case 'Z':
        rotateZ(rotationDirection * PI/2);
        break;
      }
    }
    //rotateX(XRotationCompensation) ;
    //rotateY(YRotationCompensation);

    //rotateZ(ZRotationCompensation);
    //if (cube.algos.stageNo ==7) {
    //  cube.rotationSpeed = PI/50.0;
    //}

    //  if (cube.algos.stageNo ==5) {
    //        cube.rotationSpeed = PI/1.0;

    //    //pause = true;
    //  }
    //}
    if (pause) {
      //print("paused");
    }
    if (!autoShow || cube.algos.largeCubeStageNo > 5) {
      background(255);
      cube.show();
      //saveFrame(n+"x" + n+"_rubiksCube_part2/"+ n+"x" + n+"_rubiksCube#########.jpg");
    } else {
      if (frameCount%10==0) {
        background(255);
        cube.show();
      }
    }
    int upTo =30;
    if (n<30) {
      upTo =1;
    }
    if (!autoShow && cube.scrambling && justScrambled) {
      //if first run through show the scramble slowly
      upTo = 1;
    }
    if (autoShow && speedUpCounter<upTo) {
      upTo = speedUpCounter;
      speedUpCounter++;
    }
    if (autoShow && cube.algos.largeCubeStageNo > 9) {
      upTo =1;
      cubeSpeed = PI/10.0f;
    }
    if (autoShow && cubeSpeed<PI) {
      upTo = 1;
    }

    for (int i = 0; i < upTo; i++) {
      cube.update();
      if (!pause && turns.length() == 0 && turnOs.size() ==0 && !cube.turning) {
        cube.scrambling=false;
        cube.algos.continueSolve();
        if (autoShow) {
          if (!pause && (previousLargeCubeStageNo != cube.algos.largeCubeStageNo || cube.algos.stageNo > 6 )) {

            if (previousLargeCubeStageNo!= 3 && previousLargeCubeStageNo!= 5 && previousLargeCubeStageNo!= 7) {
              spinCounter =0;
              pause = true;
              lockRotation = false;
              cubeSpeed = PI/60.0f;
            }
            previousStageNo = cube.algos.stageNo;
            previousLargeCubeStageNo = cube.algos.largeCubeStageNo;
          }
        }
      }



      if (!cube.scrambling && justScrambled) {

        //cubeSpeed= PI;
        pause = true;
        if (autoShow) {
          upTo=1;
          cubeSpeed = PI/60.0f;
        }
        //if(
        justScrambled = false;
      }


      if (!pause &&!cube.turning) {
        if (!cube.scrambling) {
          //turnCounter[cube.algos.stageNo]+=1;
        }
        if (turns.length() >0 ) {
          doTurn();
        } else if (turnOs.size()  >0) {
          doTurnFromObj();
        }
      }
    }


    //if (cube.algos.stageNo ==7) {

    //  if (scrambleCounter <0) {
    //    int sum = 0;
    //    solveCounter +=1;
    //    for (int i = 0; i< 7; i++) { 
    //      println("stage " + i + ": " + (turnCounter[i]/solveCounter) + " turns");
    //      sum += turnCounter[i];
    //      //turnCounter[i] =0;
    //    }
    //    println("Total : " + (sum/solveCounter));
    //    println("____________________________________________________");
    //    scrambleCounter = 1;
    //    cube.algos.scramble();
    //    cube.scrambling = true;
    //  } else {
    //    scrambleCounter--;
    //  }
    //}
    if (cube.algos.stageNo == 7) {
       frameToStop++;
       if(frameToStop == 7) {
         showMessageDialog(null, "Resolu " + solveCounterThing + " rubik's cube(s) en " +(cube.algos.globalTimer/1000.0f)+ " seconde(s)", "Info" , PLAIN_MESSAGE);
         exit();
       }
    }


    popMatrix();
  }
}


public void keyPressed() {

  switch(key) {
  case 'p':
    actualPause =!actualPause;
    break;
  case 'l':
    lockRotation = !lockRotation;
    lockedMousePos = new PVector(mouseX, mouseY);
    spinCounter =0;
    break;
  case ' ':
    pause=!pause;
    if (!pause && !justScrambled) {
      startTime = millis();
    }
    break;
  case 'm':
    enableMouseX = !enableMouseX;
    enableMouseY = !enableMouseY;
    break;
  case 's':
    enableSpin = !enableSpin;
    break;
  }
  float spinVal = PI/2;
  switch(keyCode) {
  case RIGHT:
    yRotationKey +=spinVal;
    break;
  case LEFT:
    yRotationKey -=spinVal;
    break;
  case UP:
    xRotationKey +=spinVal;
    break;
  case DOWN:
    xRotationKey -=spinVal;
    break;
  }
}

public void doTurn() {
  //input is a string e.g. RUL'DD
  //  void turnCube(int index, int xOrYOrZ, boolean turnClockwise) {
  char turn = turns.charAt(0);
  turns = turns.substring(1, turns.length());

  boolean clockwise = true;




  if (turns.length() > 0 && turns.charAt(0) == '\'') {
    clockwise = false;
    turns = turns.substring(1, turns.length());
    //println("Turning cube " + turn +"'");
    if (turns.length()>=4) {      
      if (turns.substring(0, 4).equals( "" + turn  + "'" + turn + "'")) {
        //println("replaced: " +  turn + "'" + turns.substring(0, 4) + " with " +turn); 

        clockwise = true;
        turns = turns.substring(4, turns.length());
      }
    }
  } else {
    //println("Turning cube " + turn);
    if (turns.length()>=2) {
      if (turns.charAt(0)== turn && turns.charAt(1) == turn) {
        //println("replaced: " +  turn+ turns.substring(0, 2) + " with " +turn + "'"); 
        clockwise = false;
        turns = turns.substring(2, turns.length());
      }
    }
  }



  if (turn == 'R' || turn =='D' || turn == 'F') {
    clockwise = !clockwise;
  }


  if (fixCubeRotation) {
    compensateForTurn(turn, clockwise);
  }


  switch(turn) {
  case 'R':
    cube.turnCube(numberOfSides-1, 0, clockwise);
    break;
  case 'L':
    cube.turnCube(0, 0, clockwise );
    break;
  case 'U':
    cube.turnCube(0, 1, clockwise);
    break;
  case 'D':
    cube.turnCube(numberOfSides-1, 1, clockwise);
    break;
  case 'F':
    cube.turnCube(numberOfSides-1, 2, clockwise);
    break;
  case 'B':
    cube.turnCube(0, 2, clockwise);
    break;
  case 'X':
    cube.turnWholeCube(0, clockwise);

    break;
  case 'Y':
    cube.turnWholeCube(1, clockwise);
    break;
  case 'Z':
    cube.turnWholeCube(2, clockwise);
    break;
  }
}

public void doTurnFromObj() {
  //println("turnos");
  //input is a string e.g. RUL'DD
  //  void turnCube(int index, int xOrYOrZ, boolean turnClockwise) {
  TurnO turn = turnOs.remove(0);

  if (turnOs.size() >= 2) {
    if (turn.matches(turnOs.get(0)) && turn.matches(turnOs.get(1))) {
      turnOs.remove(0);
      turnOs.remove(0);
      turn.clockwise = !turn.clockwise;
    }
  }
  cube.turnCubeFromObj(turn);
}

public void compensateForTurn(char direction, boolean clockwise) {
  if (clockwise) {
    rotations.add(""+direction);
  } else {
    rotations.add(""+direction +"'");
  }
  switch(direction) {
  case 'X':
    if (clockwise) {
      XRotationCompensation += PI/2;
    } else {
      XRotationCompensation -= PI/2;
    }
    break;
  case 'Y':
    if (clockwise) {
      YRotationCompensation += PI/2;
    } else {
      YRotationCompensation -= PI/2;
    }
    break;

  case 'Z':
    if (clockwise) {
      ZRotationCompensation += PI/2;
    } else {
      ZRotationCompensation -= PI/2;
    }
    break;//   float  YRotationCompensation = 0;
    //   float  ZRotationCompensation = 0;
  }
}

String mappings = "LRUDBF";


public void simulateRotation(int axis, boolean clockwise) {
  if (!clockwise) {
    simulateRotation(axis, true);
    simulateRotation(axis, true);
    simulateRotation(axis, true);
  }
  //LRUDBF
  switch(axis) {
  case 0:
    for (int i = 0; i< mappings.length(); i++) { 
      int index = XRotation.indexOf(mappings.charAt(i));
      if (index != -1) {
        mappings = mappings.substring(0, i) + XRotation.charAt((index +1)%4) + mappings.substring(i+1, mappings.length());
      }
    }
    break;
  case 1:
    for (int i = 0; i< mappings.length(); i++) { 
      int index = YRotation.indexOf(mappings.charAt(i));
      if (index != -1) {
        mappings = mappings.substring(0, i) + YRotation.charAt((index +1)%4) + mappings.substring(i+1, mappings.length());
      }
    }
    break;
  case 2:
    for (int i = 0; i< mappings.length(); i++) { 
      int index = ZRotation.indexOf(mappings.charAt(i));
      if (index != -1) {
        mappings = mappings.substring(0, i) + ZRotation.charAt((index +1)%4) + mappings.substring(i+1, mappings.length());
      }
    }
    break;
  }
}


public void printTurnos() {
  println("turnos mamte");
  for (int i = 0; i< turnOs.size(); i++) { 
    turnOs.get(i).printTurn();
  }
}

public boolean moreTurns() {
  return turnOs.size() != 0 || turns.length() !=0;
}
class Block {

  PVector pos;
  int[] colors = new int[6]; //left right up down back front
  boolean showBlack = false;
  int numberOfCols = 0;
  int id;
  Block(PVector pos) {
    this.pos = pos;
    id = (int)(pos.x*100*n*n + pos.y*10*n + pos.z*1) + floor(random(100000));
    setColors();
  }
  Block() {
  }

  public Block clone() {

    Block clone = new Block(); 
    clone.colors = colors.clone();
    clone.pos = new PVector(pos.x, pos.y, pos.z);
    clone.numberOfCols = numberOfCols;
    clone.id = id;
    return clone;
  }
  public void turn(int axisNo, boolean clockwise) {
    //assume always clockwise
    int[] newColors = colors.clone();
    if (clockwise) {
      turn(axisNo, false); 
      turn(axisNo, false); 
      turn(axisNo, false);
      return;
    }
    switch(axisNo) {
    case 0://x axis
      //front becomes up
      //up becomes back
      //back becomes down
      //down becomes font

      newColors[2] = colors[5];
      newColors[3] = colors[4];
      newColors[4] = colors[2];
      newColors[5] = colors[3];
      break;
    case 1://yaxis
      newColors[0] = colors[4];//left = back
      newColors[1] = colors[5];//right = fron
      newColors[4] = colors[1];//back = right
      newColors[5] = colors[0];//front = left
      break;
    case 2://zaxis
      newColors[0] = colors[3];
      newColors[1] = colors[2];
      newColors[2] = colors[0];
      newColors[3] = colors[1];
      break;
    }
    colors = newColors.clone();
  }
  public void setColors() {
    //position 0 0 0 is the top left back which is the blue, white orange corner (respectively)
    for (int i = 0; i< colors.length; i++) { 
      colors[i] = color(0);
    }
    if (pos.x==0) {
      colors[0] = color(255);
      numberOfCols++;
    }
    if (pos.x == numberOfSides-1) {
      colors[1] = color(255, 255, 0);
      numberOfCols++;
    }
    if (pos.y ==0) {
      colors[2] = color(0, 0, 255);
      numberOfCols++;
    }
    if (pos.y ==numberOfSides-1) {
      colors[3] = color(0, 255, 0);
      numberOfCols++;
    }
    if (pos.z ==0) {
      colors[4] = color(255, 140, 0);//orange
      numberOfCols++;
    }
    if (pos.z ==numberOfSides-1) {
      colors[5] = color(255, 0, 0);
      numberOfCols++;
    }
  }


  public void show() {

    for (int i = 0; i< colors.length; i++) { 
      drawFace(i, colors[i]);
    }
  }


  //left right up down back front
  public void drawFace(int faceNo, int col) {

    //if (!showBlack && col == color(0)) {
    //  return;
    //}
    if (col == color(0)) {
      return;
    }
    

    fill(col);
    stroke(0);
    float weightSize = max(1, blockWidth/20.0f);
    strokeWeight(weightSize);
    //strokeWeight(2);
    //noStroke();
    switch(faceNo) {
    case 0:
      //left
      beginShape();
      addVertex(0, 0, 1);
      addVertex(0, 0, 0);
      addVertex(0, 1, 0);
      addVertex(0, 1, 1);
      endShape(CLOSE);
      break;
    case 1:
      //right
      beginShape();
      addVertex(1, 0, 1);
      addVertex(1, 0, 0);
      addVertex(1, 1, 0);
      addVertex(1, 1, 1);
      endShape(CLOSE);
      break;
    case 2:
      //top
      beginShape();
      addVertex(0, 0, 1);
      addVertex(0, 0, 0);
      addVertex(1, 0, 0);
      addVertex(1, 0, 1);
      endShape(CLOSE);
      break;
    case 3:
      //bottom
      beginShape();
      addVertex(0, 1, 1);
      addVertex(0, 1, 0);
      addVertex(1, 1, 0);
      addVertex(1, 1, 1);
      endShape(CLOSE);
      break;
    case 4:
      //back
      beginShape();
      addVertex(0, 0, 0);
      addVertex(1, 0, 0);
      addVertex(1, 1, 0);
      addVertex(0, 1, 0);
      endShape(CLOSE);
      break;
    case 5:
      //front
      beginShape();
      addVertex(0, 0, 1);
      addVertex(1, 0, 1);
      addVertex(1, 1, 1);
      addVertex(0, 1, 1);
      endShape(CLOSE);
      break;
    }
  }


  //todo gonna need more info for larger problems
  public boolean matchesColors(int[] cols) {
    if (numberOfCols != cols.length) {
      return false;
    }

    for (int i = 0; i< cols.length; i++) {
      boolean foundMatch = false;
      for (int j = 0; j < colors.length; j++) {
        if (colors[j] == cols[i]) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return false;
      }
    }


    return true;
  }

  public boolean matchesColors(int col) {
    if (numberOfCols != 1) {
      return false;
    }
    for (int j = 0; j < colors.length; j++) {
      if (colors[j] == col) {
        return true;
      }
    }
    return false;
  }

  public int[] getColors() {
    int[] cols = new int[numberOfCols];
    int counter =0;
    for (int i = 0; i<6; i++) {
      if (colors[i]!= color(0)) {
        cols[counter] = colors[i];
        counter++;
      }
    } 
    return cols;
  }

  public int getColorFromFace(char face) {
    String faces = "LRUDBF";
    return colors[faces.indexOf(face)];
  }


  public char getFace(int col) { //left right up down back front
    for (int j = 0; j < colors.length; j++) {
      if (colors[j] == col) {
        String faces = "LRUDBF";
        return faces.charAt(j);
      }
    }
    return  ' ';
  }

  public String getFaces() {
    String faces = "";
    String faceOrder = "LRUDBF";
    for (int j = 0; j < colors.length; j++) {
      if (colors[j] != color(0)) {
        faces += faceOrder.charAt(j);
      }
    }
    return  faces;
  }


  public void addVertex(int x, int y, int z) {
    vertex((x-0.5f)*faceWidth, (y-0.5f)*faceWidth, (z-0.5f)*faceWidth);
  }
}
class Cube {
  Block[][][] blocks;
  int rotationAxis = 0;
  int rotatingIndex = 0;
  float rotationAngle = 0;
  ArrayList<Block> rotatingBlocks = new ArrayList();
  ArrayList<Block> showingBlack = new ArrayList();
  int counter = 0;
  ArrayList<ArrayList<Block>> cycleLists = new ArrayList();
  Block centerBlock = new Block(new PVector(0, 0, 0));
  boolean turning = false;
  boolean turningClockwise = true;
  boolean turningWholeCube = false;
  boolean scrambling = true;
  float rotationSpeed = PI/20.0f;

  CubeAlgorithms algos;
  Cube() {
    blocks = new Block[numberOfSides][numberOfSides][numberOfSides];
    for (int i = 0; i< numberOfSides; i++) { 
      for (int j = 0; j< numberOfSides; j++) { 
        for (int k = 0; k< numberOfSides; k++) { 
          blocks[i][j][k] = new Block(new PVector(i, j, k));
        }
      }
    }
    algos = new CubeAlgorithms(this);
  }

  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  public void show() {

    showBlacks();
    int angleMultiplier = 1; 
    if (turningClockwise) {
      angleMultiplier = -1;
    }
    //int otherCounter =0;
    //Block[][] face = getFace('D');
    //for (int i = 0; i< numberOfSides; i++) { 
    //  for (int j = 0; j< numberOfSides; j++) {
    //    pushMatrix();
    //    float x = face[i][j].pos.x;
    //    float y = face[i][j].pos.y;
    //    float z = face[i][j].pos.z;

    //    float m =(numberOfSides-1)/2.0;
    //    translate((x-m)*blockWidth, (y-m)*blockWidth, (z-m)*blockWidth);
    //    face[i][j].show();
    //    popMatrix();
    //    otherCounter++;
    //    if (otherCounter > counter) {
    //      counter ++;
    //      return;
    //    }
    //  }
    //}

    //counter =0;

    for (int i = 0; i< numberOfSides; i++) { 
      for (int j = 0; j< numberOfSides; j++) { 
        for (int k = 0; k< numberOfSides; k++) { 
          pushMatrix();
          if (turning && (turningWholeCube || rotatingBlocks.contains(blocks[i][j][k]) )) {
            switch(rotationAxis) {
            case 0:
              rotateX(angleMultiplier*rotationAngle);
              break;
            case 1:
              rotateY(angleMultiplier*rotationAngle);
              break;
            case 2:
              rotateZ(angleMultiplier*rotationAngle);
              break;
            }
          }
          float m =(numberOfSides-1)/2.0f;
          translate((i-m)*blockWidth, (j-m)*blockWidth, (k-m)*blockWidth);
          blocks[i][j][k].show();
          popMatrix();
        }
      }
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  public void update() {
    if (turning) {
      if (rotationAngle < PI/2) {
        float scramblingMultiplier = 1;
        if (scrambling) {
          scramblingMultiplier = 5;
        } 
        int turningEaseCoeff = 2;
        if (rotationAngle<PI/8) {
          rotationAngle += scramblingMultiplier*rotationSpeed/map(rotationAngle, 0, PI/8, turningEaseCoeff, 1);
        } else if (rotationAngle > PI/2-PI/8) {
          rotationAngle += scramblingMultiplier*rotationSpeed/map(rotationAngle, PI/2-PI/8, PI/2, 1, turningEaseCoeff);
        } else {
          rotationAngle += scramblingMultiplier* rotationSpeed;
        }
      }
      if (rotationAngle >=PI/2) {
        rotationAngle = 0;
        turning= false;
        if (turningWholeCube) {
          finishTurningWholeCube(rotationAxis, turningClockwise);
        } else {
          finaliseTurn(rotatingIndex, rotationAxis, turningClockwise);
        }
      }
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------

  public void finaliseTurn(int index, int xOrYOrZ, boolean turnClockwise) {
    turning = false;

    ////to turn once clockwise take 2 from the back of the list and chuck em on the front
    
    for (int i = 0; i< rotatingBlocks.size(); i++) { 
      rotatingBlocks.get(i).turn(xOrYOrZ, turnClockwise);
    }

    for (int j = 0; j < cycleLists.size(); j++) {
      ArrayList<Block> temp = cycleLists.get(j);
      for (int i = 0; i< numberOfSides-1-j*2; i++) { 
        if (!turnClockwise) {
          //remove from end and add to start
          temp.add(0, temp.remove(temp.size()-1));
        } else {
          //remove from front and add to the end
          temp.add(temp.remove(0));
        }
      }
      returnListToCube(temp, index, xOrYOrZ, j);
    }
  }

  public void finishTurningWholeCube(int axis, boolean clockwise) {
    turning = false;
    turningWholeCube = false;

    for (int i = 0; i< numberOfSides; i++) { 
      for (int j = 0; j< numberOfSides; j++) { 
        for (int k = 0; k< numberOfSides; k++) { 
          blocks[i][j][k].turn(axis, clockwise);
        }
      }
    }

    for (int k = 0; k< numberOfSides; k++) {
      cycleLists = getAllBlocksToRotate(k, axis);
      for (int j = 0; j < cycleLists.size(); j++) {
        ArrayList<Block> temp = cycleLists.get(j);
        for (int i = 0; i< numberOfSides-1-j*2; i++) { 
          if (!clockwise) {
            //remove from end and add to start
            temp.add(0, temp.remove(temp.size()-1));
          } else {
            //remove from front and add to the end
            temp.add(temp.remove(0));
          }
        }
        returnListToCube(temp, k, axis, j);
      }
    }
  }

  public void turnWholeCube(int axis, boolean clockwise) {
    if (turning) {
      return;
    }
    turning = true;
    turningClockwise = clockwise;
    turningWholeCube = true;
    rotationAxis = axis;
    if (fixCubeRotation) {
      rotationAngle = 0;
      turning= false;
      finishTurningWholeCube(axis, clockwise);
    }
  }


  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  public void turnCubeFromObj(TurnO t) {
    turnCube(t.index, t.axis, t.clockwise);
  }


  public void turnCube(int index, int xOrYOrZ, boolean turnClockwise) {
    if (turning) {//finish the turn
      rotationAngle = 0;
      turning= false;
      finaliseTurn(rotatingIndex, rotationAxis, turningClockwise);
    }
    turning = true;
    this.turningClockwise = turnClockwise;
    cycleLists = getAllBlocksToRotate(index, xOrYOrZ);
    rotatingBlocks = new ArrayList();
    for (int i = 0; i < cycleLists.size(); i++) {
      rotatingBlocks.addAll(cycleLists.get(i));
    }
    rotationAxis = xOrYOrZ;
    rotatingIndex = index;
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  public ArrayList<ArrayList<Block>> getAllBlocksToRotate(int index, int xOrYOrZ) {
    ArrayList<ArrayList<Block>>  temp = new ArrayList();   
    if (index ==0 || index ==n-1) {
      for (int i  = 0; i < floor((numberOfSides+1)/2); i++) {
        temp.add(getList(index, xOrYOrZ, i));
      }
    } else {
      temp.add(getList(index, xOrYOrZ, 0));
    }
    return temp;
  }

  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  //takes in an arraylist and adds it to the cube
  public void returnListToCube(ArrayList<Block> list, int index, int xOrYOrZ, int listNumber) {
    int i = 0;
    int j = 0;
    int k = 0;
    int size = numberOfSides-2*listNumber;

    switch(xOrYOrZ) {
    case 0:
      //return all blocks with the x index of "index"
      i = index;

      //add all on top row
      k = 0;
      for (j = 0; j< size; j++) { 
        blocks[i][j+listNumber][k+listNumber] = list.remove(0).clone();
      }

      //add right row
      j=size-1;
      for (k = 1; k< size; k++) { 
        blocks[i][j+listNumber][k+listNumber] = list.remove(0).clone();
      }

      //add bottom
      k=size-1;
      for (j = size-2; j>=0; j--) {
        blocks[i][j+listNumber][k+listNumber] = list.remove(0).clone();
      }

      //add left
      j=0;
      for (k = size-2; k>0; k--) {
        blocks[i][j+listNumber][k+listNumber] = list.remove(0).clone();
      }
      break;
    case 1:
      //return all blocks with the y index of "index"
      j = index;

      //add all on top row
      i = 0;
      for (k = 0; k< size; k++) { 
        blocks[i+listNumber][j][k+listNumber] = list.remove(0).clone();
      }

      //add right row
      k=size -1;
      for (i = 1; i< size; i++) { 
        blocks[i+listNumber][j][k+listNumber] = list.remove(0).clone();
      }

      //add bottom
      i=size-1;
      for (k = size-2; k>=0; k--) {
        blocks[i+listNumber][j][k+listNumber] = list.remove(0).clone();
      }

      //add left
      k=0;
      for (i = size-2; i>=1; i--) {
        blocks[i+listNumber][j][k+listNumber] = list.remove(0).clone();
      }
      break;

    case 2:
      //return all blocks with the y index of "index"
      k = index;

      //add all on top row
      j = 0;
      for (i = 0; i< size; i++) { 
        blocks[i+listNumber][j+listNumber][k] = list.remove(0).clone();
      }

      //add right row
      i=size-1;
      for (j = 1; j< size; j++) { 
        blocks[i+listNumber][j+listNumber][k] = list.remove(0).clone();
      }

      //add bottom
      j=size-1;
      for (i = size-2; i>=0; i--) {
        blocks[i+listNumber][j+listNumber][k] = list.remove(0).clone();
      }

      //add left
      i=0;
      for (j = size-2; j>=1; j--) {
        blocks[i+listNumber][j+listNumber][k] = list.remove(0).clone();
      }
      break;
    }
    for ( i = 0; i< numberOfSides; i++) { 
      for ( j = 0; j< numberOfSides; j++) { 
        for ( k = 0; k< numberOfSides; k++) { 
          blocks[i][j][k].pos = new PVector(i, j, k);
        }
      }
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------
  //returns a list of all the blocks in that row/column thing
  //returns them in an order going clockwise around the center
  //todo only works for 3 length rubix cube
  public ArrayList<Block> getList(int index, int xOrYOrZ, int listNumber) {
    ArrayList<Block> list = new ArrayList();
    int i = 0;
    int j = 0;
    int k = 0;
    int size = numberOfSides-listNumber*2;

    switch(xOrYOrZ) {
    case 0:
      //return all blocks with the x index of "index"
      i = index;

      //add all on top row
      k = 0;
      for (j = 0; j< size; j++) { 
        list.add(blocks[i][j+listNumber][k+listNumber]);
      }

      //add right row
      j=size-1;
      for (k = 1; k< size; k++) { 
        list.add(blocks[i][j+listNumber][k+listNumber]);
      }

      //add bottom
      k=size-1;
      for (j = size-2; j>=0; j--) {
        list.add(blocks[i][j+listNumber][k+listNumber]);
      }

      //add left
      j=0;
      for (k = size-2; k>=1; k--) {
        list.add(blocks[i][j+listNumber][k+listNumber]);
      }
      break;
    case 1:
      //return all blocks with the y index of "index"
      j = index;

      //add all on top row
      i = 0;
      for (k = 0; k< size; k++) { 
        list.add(blocks[i+listNumber][j][k+listNumber]);
      }

      //add right row
      k=size -1;
      for (i = 1; i< size; i++) { 
        list.add(blocks[i+listNumber][j][k+listNumber]);
      }

      //add bottom
      i=size-1;
      for (k = size-2; k>=0; k--) {
        list.add(blocks[i+listNumber][j][k+listNumber]);
      }

      //add left
      k=0;
      for (i = size-2; i>=1; i--) {
        list.add(blocks[i+listNumber][j][k+listNumber]);
      }
      break;

    case 2:
      //return all blocks with the y index of "index"
      k = index;

      //add all on top row
      j = 0;
      for (i = 0; i< size; i++) { 
        list.add(blocks[i+listNumber][j+listNumber][k]);
      }

      //add right row
      i=size -1;
      for (j = 1; j< size; j++) { 
        list.add(blocks[i+listNumber][j+listNumber][k]);
      }

      //add bottom
      j=size-1;
      for (i = size-2; i>=0; i--) {
        list.add(blocks[i+listNumber][j+listNumber][k]);
      }

      //add left
      i=0;
      for (j = size-2; j>=1; j--) {
        list.add(blocks[i+listNumber][j+listNumber][k]);
      }
      break;
    }
    return list;
  }

  public void showBlacks() {
    if (turning && !turningWholeCube) {
      pushMatrix();
      translate((-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f); 
      //now we are at 0,0,0
      fill(0);
      noStroke();

      switch(rotationAxis) {
      case 0:
        break;

      case 1: //yaxis
        translate((numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f);
        rotateZ(PI/2);
        translate((-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f); 
        break;
      case 2://zaxis
        translate((numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f);
        rotateY(-PI/2);
        translate((-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f); 
        break;
      }


      if (rotatingIndex !=0) {
        beginShape();
        vertex(blockWidth*rotatingIndex, 0, 0);
        vertex(blockWidth*rotatingIndex, numberOfSides*blockWidth, 0);
        vertex(blockWidth*rotatingIndex, numberOfSides*blockWidth, numberOfSides*blockWidth);
        vertex(blockWidth*rotatingIndex, 0, numberOfSides*blockWidth);
        endShape(CLOSE);
      }
      if (rotatingIndex !=numberOfSides-1) {
        beginShape();
        vertex(blockWidth*(rotatingIndex+1), 0, 0);
        vertex(blockWidth*(rotatingIndex+1), numberOfSides*blockWidth, 0);
        vertex(blockWidth*(rotatingIndex+1), numberOfSides*blockWidth, numberOfSides*blockWidth);
        vertex(blockWidth*(rotatingIndex+1), 0, numberOfSides*blockWidth);
        endShape(CLOSE);
      }
      pushMatrix();
      translate((numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f, (numberOfSides*blockWidth)/2.0f);
      if (turningClockwise) {
        rotateX(-rotationAngle);
      } else {
        rotateX(rotationAngle);
      }
      translate((-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f, (-numberOfSides*blockWidth)/2.0f); 
      if (rotatingIndex !=0) {
        beginShape();
        vertex(blockWidth*rotatingIndex, 0, 0);
        vertex(blockWidth*rotatingIndex, numberOfSides*blockWidth, 0);
        vertex(blockWidth*rotatingIndex, numberOfSides*blockWidth, numberOfSides*blockWidth);
        vertex(blockWidth*rotatingIndex, 0, numberOfSides*blockWidth);
        endShape(CLOSE);
      }
      if (rotatingIndex !=numberOfSides-1) {
        beginShape();
        vertex(blockWidth*(rotatingIndex+1), 0, 0);
        vertex(blockWidth*(rotatingIndex+1), numberOfSides*blockWidth, 0);
        vertex(blockWidth*(rotatingIndex+1), numberOfSides*blockWidth, numberOfSides*blockWidth);
        vertex(blockWidth*(rotatingIndex+1), 0, numberOfSides*blockWidth);
        endShape(CLOSE);
      }

      popMatrix();
      popMatrix();
    }
  }


  //Block[][] getFace(char face) {
  //  int minI =0;
  //  int maxI = n-1;
  //  int minJ = 0;
  //  int maxJ = n-1;
  //  int minK =0;
  //  int maxK = n-1;
  //  switch(face) {
  //  case 'F':
  //    minK = n-1;
  //    break;
  //  case 'B':
  //    minI = n-1;
  //    maxI = 0;
  //    maxK = 0;
  //    break;
  //  case 'L':
  //    maxI = 0;
  //    break;
  //  case 'R':
  //    minI = n-1;
  //    minK = n-1;
  //    maxK = 0;
  //    break;
  //  case 'U':
  //    maxJ = 0;
  //    break;
  //  case 'D':
  //    minJ = n-1;
  //    maxK = 0;
  //    minK = n-1;
  //    break;
  //  }

  //  Block[][] faceArr = new Block[n][n];
  //  int xCounter = 0;
  //  int yCounter =0;
  //  int negationI = 1;
  //  if (maxI!=minI) {
  //    negationI = (maxI-minI)/(abs(maxI-minI));
  //  }

  //  for (int i = minI; i!=maxI+negationI; i+= negationI) {
  //    int negationJ = 1;
  //    if (maxJ!=minJ) {
  //      negationJ = (maxJ-minJ)/(abs(maxJ-minJ));
  //    }

  //    for (int j = minJ; j!=maxJ+negationJ; j+= negationJ) {
  //      int negationK = 1;
  //      if (maxK!=minK) {
  //        negationK = (maxK-minK)/(abs(maxK-minK));
  //      }
  //      for (int k = minK; k!=maxK+negationK; k+= negationK) {
  //        faceArr[xCounter][yCounter] = blocks[i][j][k];
  //        xCounter++;
  //        if (xCounter ==n) {
  //          xCounter = 0;
  //          yCounter++;
  //          if (yCounter == n) {
  //            return faceArr;
  //          }
  //        }
  //      }
  //    }
  //  }
  //  println("Fuck");
  //  return null;
  //}



  public Block[][] getFace(char face) {

    Block[][] faceArr = new Block[n][n];

    switch(face) {
    case 'F':
      for (int i = 0; i< n; i++) { 
        for (int j = 0; j<n; j++) {
          faceArr[i][j] = blocks[i][j][n-1];
        }
      }
      return faceArr;
    case 'B':
      for (int i = 0; i< n; i++) { 
        for (int j = 0; j<n; j++) {
          faceArr[i][j] = blocks[n-1-i][j][0];
        }
      }
      return faceArr;

    case 'L':
      for (int k = 0; k< n; k++) { 
        for (int j = 0; j<n; j++) {
          faceArr[k][j] = blocks[0][j][k];
        }
      }
      return faceArr;      
    case 'R':
      for (int i = 0; i< n; i++) { 
        for (int j = 0; j<n; j++) {
          faceArr[i][j] = blocks[n-1][j][n-1-i];
        }
      }
      return faceArr;
    case 'U':
      for (int i = 0; i< n; i++) { 
        for (int j = 0; j<n; j++) {
          faceArr[i][j] = blocks[i][0][j];
        }
      }
      return faceArr;
    case 'D':
      for (int i = 0; i< n; i++) { 
        for (int j = 0; j<n; j++) {
          faceArr[i][j] = blocks[i][n-1][n-1-j];
        }
      }
      return faceArr;
    }

    println("Fuck");
    return null;
  }
}
class CubeAlgorithms { //<>// //<>// //<>// //<>// //<>// //<>// //<>// //<>// //<>// //<>//
  Cube cube;
  int stageNo = 0;
  int largeCubeStageNo =0;
  boolean part1 = true;
  int completedCorners = 0;
  int completedEdges = 0;
  int turnsDone = 0;
  int rowStage =0;
  int rowUpTo = 0;
  
  int globalTimer = millis()-startTime;

  int rowCounter =1;
  Block target;
  boolean newTarget = true;
  int targetId =0;
  boolean doneRedCenterRow = false;
  CubeAlgorithms(Cube cube) {
    this.cube = cube;
    if (numberOfSides ==3) {
      part1=false;
      largeCubeStageNo = 10;
    }
  }

  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //called whenever the turn string is empty
  public void continueSolve() {
    if (part1) {
      switch(largeCubeStageNo) {

      case -1:

        doEdges();
        break;
      case 0:      

        positionFace(green, 'D', 'X');
        largeCubeStageNo ++;
        return;
      case 1:
        FirstCenter();
        break;

      case 2:

        SecondCenter();
        break;
      case 3:
        positionFace(red, 'L', 'Y');
        largeCubeStageNo ++;
        break;
      case 4:

        ThirdCenter();
        break;
      case 5:
        positionFace(yellow, 'L', 'Y');
        largeCubeStageNo ++;
        break;
      case 6:
        FourthCenter();
        break;

      case 7:
        positionFace(yellow, 'D', 'Z');
        largeCubeStageNo ++;
        break;
      case 8:
        finalCenters();
        break;
      case 9:
        doEdges();
        break;
      default:
        //setup();
        //pause = true;
        part1 = false;
        return;
      }
    } else {

      switch(stageNo) {

      case 0://cross
        greenCross();    
        break;
      case 1:
        positionBottomCorners();    
        break;
      case 2:
        finishBottom2Rows();    
        break;
      case 3:
        positionTopCross();    
        break;
      case 4:
        finishTopCross();    
        break;
      case 5:
        getCornersInCorrectPositions();
        break;
      case 6:
        finalRotations();
        break;
      case 7:

        if (repeatSolves) {
          if (globalTimer <4109) {


            resetCube();
            //pause = true;
            println("new cube", solveCounterThing, globalTimer);
            solveCounterThing++;
          } else {
            pause = true; 
            println("Solved " + solveCounterThing + " rubik's cubes in " +(globalTimer/1000.0f)+ " seconds");
            println("averageTime = " + (PApplet.parseFloat(globalTimer)/PApplet.parseFloat(solveCounterThing)));
          }
        } else {
          pause = true; 

          println("Solved in " +(globalTimer/1000.0f)+ " seconds");
        }
        return;
      }
    }
  }
  //edge stuff
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void doEdges() {
    int[][] colorOrder = {{red, blue}, {red, white}, {red, green}, {red, yellow}, {orange, blue}, {orange, white}, {orange, green}, {orange, yellow}, {white, green}, {yellow, green}, {blue, yellow}, {blue, white}};
    for (int i = 0; i< colorOrder.length; i++) {
      if (!edgeFinished(colorOrder[i][0], colorOrder[i][1])) {
        solveEdge(colorOrder[i][0], colorOrder[i][1]);
      }
      if (moreTurns()) {
        return;
      }
    }



    //cube.rotationSpeed = PI/40.0;
    //setup();
    //return;

    for (int i = 0; i< colorOrder.length; i++) {
      if (!edgeFinishedWithRotation(colorOrder[i][0], colorOrder[i][1])) {
        parityFixer(colorOrder[i][0], colorOrder[i][1]);
        return;
      }
    }



    largeCubeStageNo++;
  }

  public void parityFixer(int c1, int c2) {
    Block middlePiece = getEdgePiece(c1, c2, middle);
    char f1 = middlePiece.getFace(c1);
    char f2 = middlePiece.getFace(c2);
    if (f1 != 'F') {
      String temp =  getDirection(f1, 'F');
      //println(f1, "F", temp);

      turns+=temp;
      //println("temp1 " + temp);
      if (moreTurns()) {
        //println("Turnes" + turns);
        return;
      }
    }
    if (f2!='U') {
      if (f2 == 'L') {
        turns+="Z'";
      }
      if (f2 == 'D') {
        turns+="ZZ";
      }
      if (f2 =='R') {

        turns+="Z";
      }
      if (moreTurns()) {
        //println("Turnes" + turns);

        return;
      }
    }

    //so now its up and front
    Block[][] frontFace = cube.getFace('F');
    ArrayList<Integer> parityPositions = new ArrayList();
    //get all the positions which are shit
    for (int i = 1; i< n-1; i++) {
      if (frontFace[i][0].getFace(c1) != f1) {
        parityPositions.add(i);
      }
    } 

    //println("pairirir");
    for (int i = 0; i<parityPositions.size(); i++) {
      //println(parityPositions.get(i));
    } 
    //ok now lets fuck em up
    moveAllParities(parityPositions, 0, false, false);
    turnOs.add( charToTurnO('U', true));
    turnOs.add( charToTurnO('U', true));
    moveAllParities(parityPositions, 0, true, true);
    turnOs.add( charToTurnO('F', true));
    turnOs.add( charToTurnO('F', true));
    moveAllParities(parityPositions, 0, false, true);
    turnOs.add( charToTurnO('F', true));
    turnOs.add( charToTurnO('F', true));
    moveAllParities(parityPositions, 0, false, false);
    moveAllParities(parityPositions, 0, false, false);
    turnOs.add( charToTurnO('U', true));
    turnOs.add( charToTurnO('U', true));
    moveAllParities(parityPositions, 0, true, false);
    turnOs.add( charToTurnO('U', true));
    turnOs.add( charToTurnO('U', true));
    moveAllParities(parityPositions, 0, false, false);
    turnOs.add( charToTurnO('U', true));
    turnOs.add( charToTurnO('U', true));
    turnOs.add( charToTurnO('F', true));
    turnOs.add( charToTurnO('F', true));
    moveAllParities(parityPositions, 0, false, false);
    moveAllParities(parityPositions, 0, false, false);
    turnOs.add( charToTurnO('F', true));
    turnOs.add( charToTurnO('F', true));
    //printTurnos();
  }

  public void moveAllParities(ArrayList<Integer> parities, int axis, boolean clockwise, boolean left) {
    for (int i = 0; i< parities.size(); i++) {
      if (left &&  parities.get(i)<middle) {
        //println("herherherherherher");
        turnOs.add(new TurnO(0, parities.get(i), clockwise));
      }
      if (!left &&  parities.get(i)>middle) {
        //println("herherherherherherRIGHT");

        turnOs.add(new TurnO(0, parities.get(i), !clockwise));
      }
    }
  }

  public void solveEdge(int c1, int c2) {
    int[] cols = {c1, c2};
    Block edgeCenter = getEdgePiece(c1, c2, middle);

    if (!cube.blocks[n-1][middle][n-1].matchesColors(cols)) {
      //position front
      String edgeFaces = edgeCenter.getFaces();
      turnOs.addAll(moveEdgeToFrontRight(edgeFaces.charAt(0), edgeFaces.charAt(1)));
      return;
    }

    //now center is in front right
    for (int i = 1; i<n-1; i++) {
      if (!cube.blocks[n-1][i][n-1].matchesColors(cols)) {
        if (newTarget) {
          target = getEdgePieceNotFrontRight(c1, c2, i);
          targetId = target.id;
        } else {
          target = getEdgePieceNotFrontRight(c1, c2, i);
          if (target.id != targetId) {
            target = getEdgePieceNotFrontRight(c1, c2, i, target);
          }
        }
        String edgeFaces = target.getFaces();
        turnOs.addAll(moveEdgeToFrontLeft(edgeFaces.charAt(0), edgeFaces.charAt(1)));
        if (moreTurns()) {
          //println("ahh");
          newTarget = false;
          return;
        }

        //now target is in middle row

        //now we need to check if they are facing the same way
        String targetFaces = target.getFaces();
        String centerFaces = edgeCenter.getFaces();
        char rightMostTargetFace = getRightMostMiddleChar(targetFaces);
        char rightMostCenterFace = getRightMostMiddleChar(centerFaces);


        if (target.getColorFromFace(rightMostTargetFace) == edgeCenter.getColorFromFace(rightMostCenterFace)) {
          //println("this");
          //todo check if its already in position
          if (rightMostTargetFace == 'F') { 
            turnOs.add( charToTurnO('L', true));
            turnOs.add( charToTurnO('L', true));
          } else {
            turnOs.add( charToTurnO(rightMostTargetFace, true));
            turnOs.add( charToTurnO(rightMostTargetFace, true));
          }
          newTarget = false;

          return;
        }

        //println(targetFaces, centerFaces, rightMostTargetFace, rightMostCenterFace, "also im here");
        //now the target is facing the opposite direction to the thing
        turnOs.addAll(getTurnObjects(rightMostCenterFace, rightMostTargetFace, (int)(n-1-target.pos.y), 1));
        if (targetFaces.indexOf(rightMostTargetFace) ==0) {
          turnOs.addAll(getEdgeFlippingTurns(targetFaces.charAt(1), rightMostTargetFace));
        } else {
          turnOs.addAll(getEdgeFlippingTurns(targetFaces.charAt(0), rightMostTargetFace));
        }
        turnOs.addAll(getTurnObjects(rightMostTargetFace, rightMostCenterFace, (int)(n-1-target.pos.y), 1));
        newTarget = true;
        return;
      } else if (cube.blocks[n-1][i][n-1].getFace(c1) !=edgeCenter.getFace(c1)) {
        //println("fuck oh");
      }
      //newTarget = true;
    }
  }

  public char getRightMostMiddleChar(String s) {
    return getRightMostMiddleChar(s.charAt(0), s.charAt(1));
  }
  public char getRightMostMiddleChar(char c1, char c2) {
    String faceOrder= "FRBL";
    int f1 = faceOrder.indexOf(c1);
    int f2 = faceOrder.indexOf(c2);
    if (abs(f1-f2) ==1) {
      if (f1>f2) {
        return c1;
      } else {
        return c2 ;
      }
    } else {
      if (f1<f2) {
        return c1;
      } else {
        return c2 ;
      }
    }
  }
  public Block getEdgePieceNotFrontRight(int c1, int c2, int edgePos) {
    String faces = "FBLRUD";
    int[] cols = {c1, c2};

    for (int i = 0; i<faces.length(); i++) {
      Block[][] face = cube.getFace(faces.charAt(i));
      if (face[edgePos][0].matchesColors(cols) && !face[edgePos][0].getFaces().equals("RF")) {
        return face[edgePos][0];
      }
      if (face[n-1-edgePos][0].matchesColors(cols) && !face[n-1-edgePos][0].getFaces().equals("RF")) {
        return face[n-1-edgePos][0];
      }
      if (face[n-1-edgePos][n-1].matchesColors(cols)&& !face[n-1-edgePos][n-1].getFaces().equals("RF")) {
        return face[n-1-edgePos][n-1];
      }
      if (face[edgePos][n-1].matchesColors(cols)&& !face[edgePos][n-1].getFaces().equals("RF")) {
        return face[edgePos][n-1];
      }
      if (face[0][edgePos].matchesColors(cols)&& !face[0][edgePos].getFaces().equals("RF")) {
        return face[0][edgePos];
      }
      if (face[n-1][edgePos].matchesColors(cols)&& !face[n-1][edgePos].getFaces().equals("RF")) {
        return face[n-1][edgePos];
      }
      if (face[n-1][n-1-edgePos].matchesColors(cols)&& !face[n-1][n-1-edgePos].getFaces().equals("RF")) {
        return face[n-1][n-1-edgePos];
      }
      if (face[0][n-1-edgePos].matchesColors(cols)&& !face[0][n-1-edgePos].getFaces().equals("RF")) {
        return face[0][n-1-edgePos];
      }
    }
    return null;
  }

  public Block getEdgePieceNotFrontRight(int c1, int c2, int edgePos, Block notThisOne) {
    String faces = "FBLRUD";
    int[] cols = {c1, c2};

    for (int i = 0; i<faces.length(); i++) {
      Block[][] face = cube.getFace(faces.charAt(i));
      if (face[edgePos][0].matchesColors(cols) && !face[edgePos][0].getFaces().equals("RF") && face[edgePos][0] != notThisOne) {
        return face[edgePos][0];
      }
      if (face[n-1-edgePos][0].matchesColors(cols) && !face[n-1-edgePos][0].getFaces().equals("RF")&& face[n-1-edgePos][0] != notThisOne) {
        return face[n-1-edgePos][0];
      }
      if (face[n-1-edgePos][n-1].matchesColors(cols)&& !face[n-1-edgePos][n-1].getFaces().equals("RF")&& face[n-1-edgePos][n-1] != notThisOne) {
        return face[n-1-edgePos][n-1];
      }
      if (face[edgePos][n-1].matchesColors(cols)&& !face[edgePos][n-1].getFaces().equals("RF")&& face[edgePos][n-1] != notThisOne) {
        return face[edgePos][n-1];
      }
      if (face[0][edgePos].matchesColors(cols)&& !face[0][edgePos].getFaces().equals("RF")&& face[0][edgePos] != notThisOne) {
        return face[0][edgePos];
      }
      if (face[n-1][edgePos].matchesColors(cols)&& !face[n-1][edgePos].getFaces().equals("RF")&& face[n-1][edgePos] != notThisOne) {
        return face[n-1][edgePos];
      }
      if (face[n-1][n-1-edgePos].matchesColors(cols)&& !face[n-1][n-1-edgePos].getFaces().equals("RF")&& face[n-1][n-1-edgePos] != notThisOne) {
        return face[n-1][n-1-edgePos];
      }
      if (face[0][n-1-edgePos].matchesColors(cols)&& !face[0][n-1-edgePos].getFaces().equals("RF")&& face[0][n-1-edgePos] != notThisOne) {
        return face[0][n-1-edgePos];
      }
    }
    return null;
  }
  public Block getEdgePiece(int c1, int c2, int edgePos) {
    String faces = "FBLRUD";
    int[] cols = {c1, c2};

    for (int i = 0; i<faces.length(); i++) {
      Block[][] face = cube.getFace(faces.charAt(i));
      if (face[edgePos][0].matchesColors(cols)) {
        return face[edgePos][0];
      }
      if (face[n-1-edgePos][0].matchesColors(cols)) {
        return face[n-1-edgePos][0];
      }
      if (face[n-1-edgePos][n-1].matchesColors(cols)) {
        return face[n-1-edgePos][n-1];
      }
      if (face[edgePos][n-1].matchesColors(cols)) {
        return face[edgePos][n-1];
      }
      if (face[0][edgePos].matchesColors(cols)) {
        return face[0][edgePos];
      }
      if (face[n-1][edgePos].matchesColors(cols)) {
        return face[n-1][edgePos];
      }
      if (face[n-1][n-1-edgePos].matchesColors(cols)) {
        return face[n-1][n-1-edgePos];
      }
      if (face[0][n-1-edgePos].matchesColors(cols)) {
        return face[0][n-1-edgePos];
      }
    }
    return null;
  }


  public boolean edgeFinished(char f1, char f2) {
    Block[][] face1 = cube.getFace(f1);
    Block[][] face2 = cube.getFace(f2);
    ArrayList<Block> common = new ArrayList();
    for (int i = 0; i< n; i++) {
      for (int j = 0; j< n; j++) {
        if (i==0 || i==n-1 || j==0 || j==n-1 ) {
          for (int k = 0; k<n; k++) {
            for (int l = 0; l<n; l++) {
              if (face1[i][j] == face2[k][l] && face1[i][j].numberOfCols ==2) {
                common.add(face1[i][j]);
              }
            }
          }
        }
      }
    }

    int[] cols = common.get(0).getColors();
    for (int i = 1; i<common.size(); i++) {
      if (!common.get(i).matchesColors(cols)) {
        return false;
      }
    }

    return true;
  }

  public boolean edgeFinishedWithRotation(char f1, char f2) {
    Block[][] face1 = cube.getFace(f1);
    Block[][] face2 = cube.getFace(f2);
    ArrayList<Block> common = new ArrayList();
    for (int i = 0; i< n; i++) {
      for (int j = 0; j< n; j++) {
        if (i==0 || i==n-1 || j==0 || j==n-1 ) {
          for (int k = 0; k<n; k++) {
            for (int l = 0; l<n; l++) {
              if (face1[i][j] == face2[k][l] && face1[i][j].numberOfCols ==2) {
                common.add(face1[i][j]);
              }
            }
          }
        }
      }
    }

    int[] cols = common.get(0).getColors();
    int f1Color = common.get(0).getColorFromFace(f1);
    for (int i = 1; i<common.size(); i++) {
      if (!common.get(i).matchesColors(cols) || common.get(i).getColorFromFace(f1) != f1Color) {
        return false;
      }
    }

    return true;
  }

  public boolean edgeFinishedWithRotation(int c1, int c2) {
    String faces = getEdgePiece(c1, c2, middle).getFaces();
    return edgeFinishedWithRotation(faces.charAt(0), faces.charAt(1));
  }

  public boolean edgeFinished(int c1, int c2) {
    String faces = getEdgePiece(c1, c2, middle).getFaces();
    return edgeFinished(faces.charAt(0), faces.charAt(1));
  }

  public ArrayList<TurnO> getEdgeFlippingTurns(char leftFace, char rightFace) {
    ArrayList<TurnO> t = new ArrayList();
    //println(leftFace, rightFace);
    t.add(charToTurnO(rightFace, true));
    t.add(charToTurnO(leftFace, false));
    t.add(charToTurnO('U', true));
    t.add(charToTurnO(rightFace, false));
    t.add(charToTurnO(leftFace, true));
    return t;
  }
  public ArrayList<TurnO> getEdgeFlippingTurns() {
    ArrayList<TurnO> t = new ArrayList();
    t.add(charToTurnO('R', true));
    t.add(charToTurnO('F', false));
    t.add(charToTurnO('U', true));
    t.add(charToTurnO('R', false));
    t.add(charToTurnO('F', true));
    return t;
  }


  //  STAGE 8L
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void finalCenters() {
    //cube.rotationSpeed = PI/20.0;

    for (int i = 1; i<n-1; i++) {
      for (int j = 1; j< n-1; j++) {
        if (!cube.blocks[i][j][n-1].matchesColors(orange)) {
          finalCenterThing(i, j);
        }
        if (moreTurns()) {
          return;
        }
      }
    }
    largeCubeStageNo++;
  }

  public void finalCenterThing(int x, int y) {
    char faceChar = 'U';
    boolean found = false;
    Block[][] face = cube.getFace(faceChar);

    int xRel = x-middle;
    int yRel = middle-y;
    int xMatrix = middle+xRel;
    int yMatrix = middle-yRel;

    for (int j = 0; j< 4; j++) {
      if (face[xMatrix][yMatrix].matchesColors(orange)) {
        for (int k = 0; k<j; k++) {
          turnOs.add(charToTurnO(faceChar, false));
        } 
        found = true;
        break;
      }
      int temp = xRel;
      xRel = yRel;
      yRel = -temp ;
      xMatrix = middle+xRel;
      yMatrix = middle-yRel;
    }
    if (found) {
      turnOs.add(new TurnO(0, x, false)); //bring it out
      turnOs.add(charToTurnO('U', true));
      int temp = x-middle;
      xRel = middle-y;
      yRel = -temp ;
      xMatrix = middle+xRel;
      yMatrix = middle-yRel;
      boolean tripleTurn = false;
      if (x == xMatrix) {
        tripleTurn = true;
        turnOs.add(charToTurnO('U', true));
        turnOs.add(charToTurnO('U', true));

        temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
        temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
      }
      turnOs.add(new TurnO(0, xMatrix, false)); 
      turnOs.add(charToTurnO('U', false));
      if (tripleTurn) {
        turnOs.add(charToTurnO('U', false));
        turnOs.add(charToTurnO('U', false));
      }
      turnOs.add(new TurnO(0, x, true)); //bring it out
      turnOs.add(charToTurnO('U', true));
      if (tripleTurn) {
        turnOs.add(charToTurnO('U', true));
        turnOs.add(charToTurnO('U', true));
      }
      turnOs.add(new TurnO(0, xMatrix, true)); 
      return;
    }
  }


  //  STAGE 5L
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void FourthCenter() {
    doYellowRow(rowCounter);
    if (moreTurns() || rowStage != 3) {
      return;
    }
    rowStage =0;
    rowCounter++;
    if (rowCounter>= n-1) {
      largeCubeStageNo++;
    }
  }

  public void doYellowRow(int y) {
    switch(rowStage) {
    case 0:
      //println(0);
      turnOs.add(new TurnO(1, y, false)); //bring it out
      turnOs.add(charToTurnO('F', true));
      rowStage++;
      return;

    case 1:
      //println(1);
      for (int i = middle-1; i> -middle; i--) {
        int xPos = n-1-y;
        if (!cube.blocks[xPos][middle-i][n-1].matchesColors(yellow)) {
          fillFaceGap('F', yellow, (xPos) - middle, i, 1, xPos, 4);
        }
        if (turnOs.size() >0 || turns.length()>0) {
          return;
        }
      }
      rowStage++;
      return;
    case 2:
      //println(2);
      turnOs.add(charToTurnO('F', false));
      turnOs.add(new TurnO(1, y, true));
      rowStage++;
      return;
    }
  }
  //this only works for y axis at the moment
  public void fillFaceGap(char targetFace, int pieceColor, int xIndex, int yIndex, int axis, int rowNo, int faceNo) {

    String faces = "LRB";
    if (faceNo ==3) {
      faces = "RB";//left is where we store shit
    }

    if (faceNo == 4) {
      faces = "R";//left is where we store shit
    }
    ArrayList<TurnO> adds = new ArrayList();
    char faceChar = 'N';
    boolean found = false;
    for (int i = 0; i< faces.length(); i++) { 

      Block[][] face = cube.getFace(faces.charAt(i));
      faceChar = faces.charAt(i); 

      int xRel = xIndex;
      int yRel = yIndex;
      int xMatrix = middle+xRel;
      int yMatrix = middle-yRel;

      for (int j = 0; j< 4; j++) {
        if (face[xMatrix][yMatrix].matchesColors(pieceColor)) {
          for (int k = 0; k<j; k++) {
            turnOs.add(charToTurnO(faceChar, false));
          } 
          found = true;
          break;
        }
        int temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
      }


      if (found) {
        //println("foundItcubt");
        adds.addAll(getTurnObjects(faceChar, targetFace, middle-yIndex, axis));
        turnOs.addAll(adds);

        if (faceNo ==3) {
          if (middle - yIndex < n-1-rowNo) { //if gonna fuck up previously done stuff
            //println(middle, yIndex, n, rowNo);
            turnOs.add(charToTurnO('F', false));//get the thing back into its row 
            while (adds.size() >0) {//reverse the movements
              turnOs.add(adds.remove(adds.size()-1).getReverse());
            }
            turnOs.add(charToTurnO('F', true));//get the thing back into its row
          }
        }

        if (faceNo ==4) {
          if (middle - yIndex == n-1-rowNo) { //need to turn other way
            turnOs.add(charToTurnO('F', true));//get the thing back into its row
          } else {
            turnOs.add(charToTurnO('F', false));//get the thing back into its row
          }
          while (adds.size() >0) {//reverse the movements
            turnOs.add(adds.remove(adds.size()-1).getReverse());
          }
          if (middle - yIndex == n-1-rowNo) { //need to turn other way
            turnOs.add(charToTurnO('F', false));//get the thing back into its row
          } else {
            turnOs.add(charToTurnO('F', true));//get the thing back into its row
          }
        }




        return;
      }
    }



    //checked faces and none found
    //println("oi mate its fuckede");
    //println(targetFace, pieceColor, xIndex, yIndex, axis, rowNo, faceNo);
    getToLRB(targetFace, pieceColor, xIndex, yIndex, axis, rowNo, faceNo);
  }


  //this only works for y axis at the moment
  public void getToLRB(char targetFace, int pieceColor, int xIndex, int yIndex, int axis, int rowNo, int faceNo) {

    String faces = "DFU";
    if (faceNo ==3) {
      faces = "FL";
    }
    if (faceNo ==4) {
      faces = "FB";
    }
    ArrayList<TurnO> adds = new ArrayList();
    char faceChar = 'N';
    int x = 0;
    int y =0;
    boolean found = false;
    for (int i = 0; i< faces.length(); i++) { 
      Block[][] face = cube.getFace(faces.charAt(i));
      faceChar = faces.charAt(i); 

      int xRel = xIndex;
      int yRel = yIndex;
      int xMatrix = middle+xRel;
      int yMatrix = middle-yRel;

      for (int j = 0; j< 4; j++) {
        if (face[xMatrix][yMatrix].matchesColors(pieceColor)) {
          //println(faceChar, xMatrix, yMatrix);
          if (faceChar != 'L' || n-1-rowNo<= yMatrix) {
            if (faceChar == 'D') {
              if (!rowFinished('D', xMatrix, pieceColor)) {
                found = true;
                break;
              } else {
                int temp = xRel;
                xRel = yRel;
                yRel = -temp ;
                xMatrix = middle+xRel;
                yMatrix = middle-yRel;
                continue;
              }
            }
            if (faceChar != 'F' || xMatrix!=rowNo) {
              found = true;
              break;
            }
          } else {
            //println("here 782");
          }
        }
        int temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
      }
      if (faceNo ==4) {
        if (found) {
          switch(faceChar) {
          case 'F':
            adds.add(charToTurnO('F', true));
            int temp = xRel;
            xRel = yRel;
            yRel = -temp ;
            xMatrix = middle+xRel;
            yMatrix = middle-yRel;
            adds.add(new TurnO(1, yMatrix, false));
            adds.add(charToTurnO('R', false));
            if (yMatrix != middle) {
              adds.add(charToTurnO('R', false));
            }
            adds.add(new TurnO(1, yMatrix, true));
            adds.add(charToTurnO('F', false));
            turnOs.addAll(adds);
            return;

          case 'B':
            adds.add(new TurnO(1, yMatrix, true));
            adds.add(charToTurnO('R', false));
            if (yMatrix != middle) {
              adds.add(charToTurnO('R', false));
            }
            adds.add(new TurnO(1, yMatrix, false));
            turnOs.addAll(adds);

            return;
          }
        }
      }

      if (faceNo ==3) {
        if (found) {
          switch(faceChar) {
          case 'F':
            adds.add(charToTurnO('F', true));
            int temp = xRel;
            xRel = yRel;
            yRel = -temp ;
            xMatrix = middle+xRel;
            yMatrix = middle-yRel;
            adds.add(new TurnO(1, yMatrix, false));
            adds.add(charToTurnO('R', false));
            if (yMatrix != middle) {
              adds.add(charToTurnO('R', false));
            }
            adds.add(new TurnO(1, yMatrix, true));
            adds.add(charToTurnO('F', false));
            turnOs.addAll(adds);
            return;
          case 'L':

            if (yRel == yIndex && xRel == xIndex) {
              //println("get it out", yRel, xRel);
              adds.add(new TurnO(1, yMatrix, false));
            } else {
              adds.add(new TurnO(1, yMatrix, true));
              adds.add(charToTurnO('B', false));
              if (yMatrix!=middle) {
                adds.add(charToTurnO('B', false));
              }
              adds.add(new TurnO(1, yMatrix, false));
            }
            turnOs.addAll(adds);

            return;
          }
        } else {
          //println("not found and thats nor great");
        }
      } else if (found) {
        switch(faceChar) {
        case 'F':
          adds.add(charToTurnO('F', true));
          int temp = xRel;
          xRel = yRel;
          yRel = -temp ;
          xMatrix = middle+xRel;
          yMatrix = middle-yRel;
          adds.add(new TurnO(1, yMatrix, true));
          adds.add(charToTurnO('F', false));
          turnOs.addAll(adds);
          return;
        case 'U':
          if (faceNo ==2) {
            adds.add(charToTurnO('U', true));
            temp = xRel;
            xRel = yRel;
            yRel = -temp ;
            xMatrix = middle+xRel;
            yMatrix = middle-yRel;
          }
          adds.add(new TurnO(2, yMatrix, true));
          adds.add(charToTurnO('L', true));
          adds.add(charToTurnO('L', true));

          //adds.add(charToTurnO('L', true));
          adds.add(new TurnO(2, yMatrix, false));
          if (faceNo ==2) {
            adds.add(charToTurnO('U', false));
          }
          turnOs.addAll(adds);
          return;

        case 'D':
          //println("<--------------------------------------------its down boi");
          //cube.rotationSpeed = PI/50.0;
          adds.add(charToTurnO('D', true));
          //adds.add(charToTurnO('L', true));
          temp = xRel;
          xRel = yRel;
          yRel = -temp ;
          xMatrix = middle+xRel;
          yMatrix = middle-yRel;
          //adds.add(charToTurnO('L', true));
          adds.add(new TurnO(2, n-1- yMatrix, false));
          adds.add(charToTurnO('D', false));
          turnOs.addAll(adds);
          return;
        }
      }
    }
    //println("FUCKUCKCUCKCUKCUCKCUC<MCUKCUCKCKCKKCKCKK");
  }
  //  STAGE 3L
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void ThirdCenter() {
    //println("-------------------------------------------------");
    //cube.rotationSpeed = PI/20.0;
    //if (!doneRedCenterRow) {
    //  doRedRow(middle);
    //  if (moreTurns() || rowStage != 3) {
    //    return;
    //  }
    //  doneRedCenterRow = true;
    //}
    doRedRow(rowCounter);
    if (moreTurns() || rowStage != 3) {
      return;
    }
    rowStage =0;
    rowCounter++;

    if (rowCounter>= n-1) {
      rowCounter =0;
      largeCubeStageNo++;
    }
  }

  public void doRedRow(int y) {
    switch(rowStage) {
    case 0:
      //println(0);
      turnOs.add(new TurnO(1, y, false)); //bring it out
      turnOs.add(charToTurnO('F', true));
      rowStage++;
      return;

    case 1:
      //println(1);
      for (int i = middle-1; i> -middle; i--) {
        int xPos = n-1-y;
        if (!cube.blocks[xPos][middle-i][n-1].matchesColors(red)) {
          fillFaceGap('F', red, (xPos) - middle, i, 1, xPos, 3);
        }
        if (turnOs.size() >0 || turns.length()>0) {
          return;
        }
      }
      rowStage++;
      return;
    case 2:
      //println(2);
      turnOs.add(charToTurnO('F', false));
      turnOs.add(new TurnO(1, y, true));
      rowStage++;
      return;
    }
  }




  //  STAGE 2L
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void SecondCenter() {
    //switch(rowUpTo) {
    //case 0:
    //if (!rowFinished('D', middle, green)) {
    //  doGreenRow(middle) ;
    //  if (turnOs.size()>0) {
    //    return;
    //  }
    //} else {
    //rowStage=0;



    if (!rowFinished('U', middle, blue)) {

      doMiddleBlueRow(); 
      return;
    }

    //cube.rotationSpeed = PI/30.0;

    for (int i = 1; i< n-1; i++) {
      if (i == middle) {
        continue;
      }
      if (!rowFinished('U', i, blue)) {
        doBlueRow(i); 
        return;
      } else if (rowStage ==2) {
        rowStage =0;
      }
    }
    //}

    largeCubeStageNo++;

    //DOES MIDDLE ROWQ HEWAPS OF TIMES

    //rowUpTo ++;
    //return;
    //case 1:

    //  doGreenRow(middle-1) ;
    //  if (turnOs.size()>0) {
    //    return;
    //  }
    //  //DOES MIDDLE ROWQ HEWAPS OF TIMES
    //  rowStage=0;
    //  rowUpTo ++;
    //  return;
  }
  //doGreenRow(middle-1) ;

  public void doBlueRow(int x) {
    switch(rowStage) {

    case 0:
      //println("stage 0");
      for (int i = middle-1; i> -middle; i--) {
        if (!cube.blocks[x][middle-i][n-1].matchesColors(blue)) {
          fillFaceGap('F', blue, x-middle, i, 1, x, 2);
        }
        if (turnOs.size() >0 || turns.length()>0) {
          return;
        }
      }
      rowStage++;
      return;
    case 1:
      //println("stage 1");
      boolean uppyUppy = false;
      if (rowFinished('U', n-1-x, blue)) {
        uppyUppy = true;
        turnOs.add(charToTurnO('U', true));
        turnOs.add(charToTurnO('U', true));
      }

      turnOs.add(new TurnO(0, x, false));
      turnOs.add(charToTurnO('U', true));
      turnOs.add(charToTurnO('U', true));
      turnOs.add(new TurnO(0, x, true));

      if (!uppyUppy) {
        turnOs.add(charToTurnO('U', true));
        turnOs.add(charToTurnO('U', true));
      }
      rowStage++;
      return;
    }
  }

  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //}  

  public void doMiddleBlueRow() {
    for (int i = middle-1; i> -middle; i--) {
      if (i == middle) {
        continue;
      }
      if (!cube.blocks[middle][0][middle - i].matchesColors(blue)) {

        String faces = "LRBF";
        ArrayList<TurnO> adds = new ArrayList();
        char faceChar = 'N';
        boolean found = false;
        for (int k = 0; k< faces.length(); k++) { 

          Block[][] face = cube.getFace(faces.charAt(k));
          faceChar = faces.charAt(k); 

          int xRel = 0;
          int yRel = i;
          int xMatrix = middle+xRel;
          int yMatrix = middle-yRel;

          for (int j = 0; j< 4; j++) {
            if (face[xMatrix][yMatrix].matchesColors(blue)) {
              //println("found piece requires " + i + "face turns");
              ////println(f aceChar,);
              for (int l = 0; l<j; l++) {
                adds.add(charToTurnO(faceChar, false));
              } 
              found = true;
              break;
            }
            int temp = xRel;
            xRel = yRel;
            yRel = -temp ;
            xMatrix = middle+xRel;
            yMatrix = middle-yRel;
          }


          if (found) {
            adds.addAll(getTurnObjects(faceChar, 'R', middle-i, 1));
            adds.add(charToTurnO('R', true));
            xRel = 0;
            yRel = i;

            int temp = xRel;
            xRel = yRel;
            yRel = -temp ;
            xMatrix = middle+xRel;
            yMatrix = middle-yRel;

            adds.add(new TurnO(2, n-1-xMatrix, true));
            adds.add(charToTurnO('U', true));
            adds.add(new TurnO(2, n-1-xMatrix, false));
            adds.add(charToTurnO('U', false));


            turnOs.addAll(adds);
            return;
          }
        }



        //checked faces and none found
        //println("oi mate its fuckede");
        //getToLRB(targetFace, pieceColor, xIndex, yIndex, axis, rowNo);
      }
    }
  }


  //  STAGE 0L
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void FirstCenter() {

    //switch(rowUpTo) {
    //case 0:
    //if (!rowFinished('D', middle, green)) {
    //  doGreenRow(middle) ;
    //  if (turnOs.size()>0) {
    //    return;
    //  }
    //} else {
    //rowStage=0;


    if (!rowFinished('D', middle, green)) {
      doGreenRow(middle); 
      return;
    } else if (rowStage ==3) {
      rowStage =0;
    }
    for (int i = 1; i< n-1; i++) {
      if (i == middle) {
        continue;
      }
      if (!rowFinished('D', i, green)) {
        doGreenRow(i); 
        return;
      } else if (rowStage ==3) {
        rowStage =0;
      }
    }
    //}
    rowStage =0;
    largeCubeStageNo++;

    //DOES MIDDLE ROWQ HEWAPS OF TIMES

    //rowUpTo ++;
    //return;
    //case 1:

    //  doGreenRow(middle-1) ;
    //  if (turnOs.size()>0) {
    //    return;
    //  }
    //  //DOES MIDDLE ROWQ HEWAPS OF TIMES
    //  rowStage=0;
    //  rowUpTo ++;
    //  return;
  }
  //doGreenRow(middle-1) ;



  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //if (!cube.blocks[middle][middle-2][n-1].matchesColors(green)) {
  //  fillFaceGap('F', green, 0, 2, 1);
  //}
  //}  

  public void doGreenRow(int x) {
    switch(rowStage) {
    case 0:
      //println("stage 0");
      turnOs.add(new TurnO(0, x, false));
      rowStage++;
      return;
    case 1:
      //println("stage 1");
      for (int i = middle-1; i> -middle; i--) {
        if (!cube.blocks[x][middle-i][n-1].matchesColors(green)) {
          fillFaceGap('F', green, x-middle, i, 1, x);
        }
        if (turnOs.size() >0 || turns.length()>0) {
          return;
        }
      }
      rowStage++;
      return;
    case 2:
      //println("stage 2");
      turnOs.add(new TurnO(0, x, true));
      rowStage++;
      return;
    }
  }
  //this only works for y axis at the moment
  public void fillFaceGap(char targetFace, int pieceColor, int xIndex, int yIndex, int axis, int rowNo) {

    String faces = "LRB";
    ArrayList<TurnO> adds = new ArrayList();
    char faceChar = 'N';
    boolean found = false;
    for (int i = 0; i< faces.length(); i++) { 

      Block[][] face = cube.getFace(faces.charAt(i));
      faceChar = faces.charAt(i); 

      int xRel = xIndex;
      int yRel = yIndex;
      int xMatrix = middle+xRel;
      int yMatrix = middle-yRel;

      for (int j = 0; j< 4; j++) {
        if (face[xMatrix][yMatrix].matchesColors(pieceColor)) {
          //println("found piece requires " + i + "face turns");
          ////println(f aceChar,);
          for (int k = 0; k<j; k++) {
            adds.add(charToTurnO(faceChar, false));
          } 
          found = true;
          break;
        }
        int temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
      }


      if (found) {
        adds.addAll(getTurnObjects(faceChar, targetFace, middle-yIndex, axis));
        turnOs.addAll(adds);
        return;
      }
    }



    //checked faces and none found
    //println("oi mate its fuckede");
    getToLRB(targetFace, pieceColor, xIndex, yIndex, axis, rowNo);
  }


  //this only works for y axis at the moment
  public void getToLRB(char targetFace, int pieceColor, int xIndex, int yIndex, int axis, int rowNo) {
    //println("lolololololololololo ");
    String faces = "DFU";
    ArrayList<TurnO> adds = new ArrayList();
    char faceChar = 'N';
    int x = 0;
    int y =0;
    boolean found = false;
    for (int i = 0; i< faces.length(); i++) { 

      Block[][] face = cube.getFace(faces.charAt(i));
      faceChar = faces.charAt(i); 


      int xRel = xIndex;
      int yRel = yIndex;
      int xMatrix = middle+xRel;
      int yMatrix = middle-yRel;

      for (int j = 0; j< 4; j++) {
        if (face[xMatrix][yMatrix].matchesColors(pieceColor)) {
          ////println(f aceChar,);

          if (faceChar == 'D') {

            if (!rowFinished('D', xMatrix, green) || xRel == xIndex ) {
              //println("its NICEEEEEEEEE");

              found = true;
              break;
            } else {
              //println("its hererererereerer");
              int temp = xRel;
              xRel = yRel;
              yRel = -temp ;
              xMatrix = middle+xRel;
              yMatrix = middle-yRel;
              continue;
            }
          }
          if (faceChar != 'F' || xMatrix!=rowNo) {
            found = true;
            break;
          }
        }
        int temp = xRel;
        xRel = yRel;
        yRel = -temp ;
        xMatrix = middle+xRel;
        yMatrix = middle-yRel;
      }


      if (found) {
        switch(faceChar) {
        case 'F':
          adds.add(charToTurnO('F', true));
          int temp = xRel;
          xRel = yRel;
          yRel = -temp ;
          xMatrix = middle+xRel;
          yMatrix = middle-yRel;
          adds.add(new TurnO(1, yMatrix, true));
          adds.add(charToTurnO('F', false));
          turnOs.addAll(adds);
          return;
        case 'U':
          adds.add(new TurnO(2, yMatrix, true));
          adds.add(charToTurnO('L', true));
          //adds.add(charToTurnO('L', true));
          adds.add(new TurnO(2, yMatrix, false));
          turnOs.addAll(adds);
          return;

        case 'D':
          //println("its here boiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
          //cube.rotationSpeed = PI/50.0;
          adds.add(charToTurnO('D', true));
          //adds.add(charToTurnO('L', true));
          temp = xRel;
          xRel = yRel;
          yRel = -temp ;
          xMatrix = middle+xRel;
          yMatrix = middle-yRel;
          //adds.add(charToTurnO('L', true));
          adds.add(new TurnO(2, n-1- yMatrix, false));
          adds.add(charToTurnO('D', false));
          turnOs.addAll(adds);
          return;
        }
      } else {
        //println("offfofofofofofofofofofofo"); 
        //pause = true;
      }
    }



    //      if (face[middle-xIndex][middle-yIndex].matchesColors(pieceColor)) {
    //        x = xIndex;
    //        y = yIndex;
    //        if (faceChar != 'F' || x!=rowNo-middle) {
    //          break;
    //        }
    //      }
    //      if (face[middle-yIndex][middle+xIndex].matchesColors(pieceColor)) {
    //        x = yIndex;
    //        y = -xIndex;
    //        if (faceChar != 'F' || x!=rowNo-middle) {
    //          break;
    //        }
    //      }
    //      if (face[middle+xIndex][middle+yIndex].matchesColors(pieceColor)) {
    //        x = -xIndex;
    //        y = -yIndex;
    //        if (faceChar != 'F' || x!=rowNo-middle) {
    //          break;
    //        }
    //      }
    //      if (face[middle+yIndex][middle-xIndex].matchesColors(pieceColor)) {
    //        x = -yIndex;
    //        y = xIndex;
    //        if (faceChar != 'F' || x!=rowNo-middle) {
    //          break;
    //        }
    //      }
    //    }





    //so now face is in the desired spot but its on a different face 
    //adds.addAll(getTurnObjects(faceChar, targetFace, middle-yIndex, axis));
    //turnOs.addAll(adds);
    ////printTurnos();
  }


  public boolean rowFinished(char face, int index, int col ) {
    Block[][] facePieces = cube.getFace(face);

    for (int i = 1; i< n-1; i++) {
      if (!facePieces[index][i].matchesColors(col)) {
        return false;
      }
    } 
    return true;
  }





  //  STAGE 6
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void finalRotations() {
    if (!(getFaceColor('D') == blue)) {
      positionFace(blue, 'D', 'X');
      return;
    }
    //now blue is down
    if (correctRotation(cube.blocks[n-1][n-1][n-1])) {
      if (turnsDone==4) {
        ////println("FUCK you I did it");
        stageNo++;
        return;
      } else {
        turns+="D";
        turnsDone++;
        return;
      }
    } else {
      turns += "RUR'U'RUR'U'"; 
      return;
    }
  }

  public boolean correctRotation(Block piece) {
    return(piece.colors[3] == blue);
  }

  //  STAGE 5
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void getCornersInCorrectPositions() {
    int correctCounter = 0;
    Block correctPiece = cube.blocks[0][0][0];

    Block testPiece = cube.blocks[0][0][0];
    ////println("enteringthe thing");

    if (cornerInCorrectPosition('L', 'U', 'B', testPiece)) {
      correctCounter ++;
    }
    testPiece = cube.blocks[n-1][0][0];
    if (cornerInCorrectPosition('R', 'U', 'B', testPiece)) {
      correctCounter ++;
      correctPiece = cube.blocks[n-1][0][0];
    }

    testPiece = cube.blocks[n-1][0][n-1];
    if (cornerInCorrectPosition('R', 'U', 'F', testPiece)) {
      correctCounter ++;
      correctPiece = cube.blocks[n-1][0][n-1];
    }

    testPiece = cube.blocks[0][0][n-1];
    if (cornerInCorrectPosition('L', 'U', 'F', testPiece)) {
      correctCounter ++;
      correctPiece = cube.blocks[0][0][n-1];
    }

    if (correctCounter ==4) {
      ////println("allgbro");
      stageNo++;
      return;
    }

    if (correctCounter ==0) {
      ////println("none of them are good mate");
      turns += "URU'L'UR'U'L";
      return;
    }


    //only one is correct
    ////println("only one is tops  aye");
    String temp =getDirectionsCorners(correctPiece.pos, new PVector(n-1, 0, n-1));
    ////println("temp:" + temp);
    turns+=temp;
    turns+="URU'L'UR'U'L";
    turns += reverseDirections(temp);
    ////println("reverse Temp:" +  reverseDirections(temp));
  }


  public boolean cornerInCorrectPosition(char face1, char face2, char face3, Block piece) {

    int c1 = getFaceColor(face1);
    int c2 = getFaceColor(face2);
    int c3 = getFaceColor(face3);


    if (piece.getFace(c1) == ' ') {
      return false;
    } else {

      ////println((piece.getFace(c1)));
    }
    if (piece.getFace(c2) == ' ') {
      return false;
    } else {

      ////println((piece.getFace(c2)));
    }
    if (piece.getFace(c3) == ' ') {
      return false;
    } else {

      ////println((piece.getFace(c3)));
    }
    return true;
  }

  //  STAGE 4
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void finishTopCross() {

    if (!(getFaceColor('F') == red)) {
      positionFace(red, 'F', 'Y');
      return;
    }

    int[] order = {orange, yellow, red, white, orange, yellow, red, white, orange, yellow};

    int[] currentOrder = {
      cube.blocks[middle][0][0].colors[4], //add back color
      cube.blocks[n-1][0][middle].colors[1], //right face
      cube.blocks[middle][0][n-1].colors[5], //front face
      cube.blocks[0][0][middle].colors[0], //left face
      cube.blocks[middle][0][0].colors[4] //add back color again

    };

    //also need to check if the order is already correct
    for (int i = 0; i< order.length - 4; i++) {
      boolean perfectMatch = true;
      for (int j = 0; j<4; j++) {
        if (order[i+j] != currentOrder[j]) {
          perfectMatch = false;
        }
      }
      if (perfectMatch) {
        for (int k = 0; k < i%4; k++) {
          turns+="U";
        }
        stageNo++;

        return;
      }
    }

    for (int i = 0; i< order.length - 4; i++) {
      boolean previousMatched = false;
      for (int j = 0; j<5; j++) {
        if (order[i+j] == currentOrder[j]) {
          if (previousMatched) {
            //foundPair
            //for each i rotate U
            for (int k = 0; k < i%4; k++) {
              turns+="U";
            }
            for (int k = 0; k< (i-1+j)%4; k++) {
              turns+="Y'";
            }

            turns+= "RUR'URUUR'U";
            stageNo++;
            return;
          } else {
            previousMatched = true;
          }
        } else {
          previousMatched = false;
        }
      }
    }
    turns+= "RUR'URUUR'";
    return;
  }
  //  STAGE 3
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void positionTopCross() {
    int numberOfBlueEdgesOnTop  = 0;
    //back,front,left, right
    boolean[] topBlueEdges={cube.blocks[middle][0][0].colors[2] == blue, cube.blocks[middle][0][n-1].colors[2] == blue, cube.blocks[0][0][middle].colors[2] == blue, cube.blocks[n-1][0][middle].colors[2] == blue};
    for (int i = 0; i< 4; i++) { 
      if (topBlueEdges[i]) {
        numberOfBlueEdgesOnTop ++;
      }
    }

    //case 1 cross is already formed
    if (numberOfBlueEdgesOnTop == 4) {
      stageNo ++;
      return;
    }

    //case 2 line on top

    if (topBlueEdges[0] && topBlueEdges[1]) {
      turns+= "UFRUR'U'F'"; 
      return;
    }

    if (topBlueEdges[2] && topBlueEdges[3] ) {
      turns+= "FRUR'U'F'";
      return;
    }

    //case 3 just a dot on top
    if (numberOfBlueEdgesOnTop ==0) {
      //this should convert it to case 4
      turns+= "FRUR'U'F'";
      return;
    }

    //case 4 a little L
    //first positionL to top left
    if (!topBlueEdges[0] && topBlueEdges[2]) {
      turns+="U";
    } else if (topBlueEdges[0] && !topBlueEdges[2]) {
      turns+="U'";
    } else if (!topBlueEdges[0] && !topBlueEdges[2]) {
      turns+="UU";
    }
    turns += "FRUR'U'RUR'U'F'";
  }



  //  STAGE 2
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void finishBottom2Rows() {
    int[][] colorOrder = {{red, yellow}, {yellow, orange}, {orange, white}, {white, red}};
    while (completedEdges<4) {
      positionMiddleEdge(colorOrder[completedEdges][0], colorOrder[completedEdges][1]);
      if (turns.length() > 0) {
        return;
      }
      completedEdges++;
      //////println("Next edge");
    }
    stageNo++;
  }
  //---------------------------------------------------------------------------------------------------------------------------------------------------------
  public void  positionMiddleEdge(int c1, int c2) {
    //c1 should be to the left of c2 when green is down



    int[] cols = {c1, c2};
    Block piece = findCenterEdge(cols);
    if (piece.pos.y ==0) {//top layer
      //////println("edge in top layer");
      int frontColor;
      if (piece.colors[2] == c1) {
        frontColor = c2;
      } else {
        frontColor = c1;
      }

      if (getFaceColor('F') != frontColor) {
        positionFace(frontColor, 'F', 'Y');
        //////println("turning to face");

        return;
      }

      //////println("facing correct direction");
      String temp = getDirectionsEdges(piece.pos, new PVector(middle, 0, n-1));
      if (!temp.equals("")) {
        //////println("positioning edge:" + temp);
        turns+=temp;
        return;
      }
      //now the piece is in the top center

      //////println("piece in top center");
      if (frontColor == c1) {//need to put it in the right 
        turns += "URU'R'U'F'UF";
      } else {
        turns += "U'L'ULUFU'F'";
      }
    } else if (piece.pos.y ==middle) {

      //////println("edge in second layer");
      boolean inCorrectSpotAndRotation = true;
      if (piece.colors[2] == c1) {
        int frontColor = c2;
        inCorrectSpotAndRotation = (pvectorsEqual(piece.pos, new PVector(0, middle, n-1)) && piece.colors[5] == getFaceColor('F'));
      } else {
        int frontColor = c1;
        inCorrectSpotAndRotation = (pvectorsEqual(piece.pos, new PVector(n-1, middle, n-1)) && piece.colors[5] == getFaceColor('F'));
      }


      if (!inCorrectSpotAndRotation) {
        //then its not in the right position
        //take it out 
        //////println("edge in wrong spot direction");

        turnCubeToFacePiece(piece, c1, 'Y') ;
        if (turns.length() !=0) {
          //////println("turning cube");
          return;
        }

        //////println("get that shit out of here");

        if (piece.pos.x == n-1) {
          turns += "URU'R'U'F'UF";
        } else {
          turns += "U'L'ULUFU'F'";
        }
      }
    }
  }

  //  STAGE 1
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void positionBottomCorners() {
    int[][] colorOrder = {{red, yellow}, {yellow, orange}, {orange, white}, {white, red}};
    while (completedCorners<4) {
      positionCornerAtBottom(colorOrder[completedCorners][0], colorOrder[completedCorners][1]);
      if (turns.length() > 0) {
        return;
      }
      completedCorners++;
    }
    stageNo++;
  }


  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------


  public void  positionCornerAtBottom(int c1, int c2) {
    //c1 should be to the left of c2 when green is down



    int[] cols = {c1, c2, green};
    Block piece = findCornerPiece(cols);
    if (!(getFaceColor('F') == c1)) {
      positionFace(c1, 'F', 'Y');
      return;
    }

    if (piece.pos.y ==0) {//top layer
      String temp = getDirectionsCorners(piece.pos, new PVector(n-1, 0, n-1));
      if (!temp.equals("")) {
        turns+=temp;
        return;
      }
      //now the piece is in the top right
      if (piece.colors[5] == green) {
        turns+= "URU'R'";
      } else if (piece.colors[5] == c1) {
        turns += "RUR'U'";
      } else if (piece.colors[5] == c2) {
        turns += "RUUR'U'RUR'";
      }
      //done
    } else if (piece.pos.y == n-1) {
      if (!pvectorsEqual(piece.pos, new PVector(n-1, n-1, n-1)) || piece.colors[3] != green) {
        //then its not in the right position
        //take it out 
        String temp = getDirectionsCorners(piece.pos, new PVector(n-1, n-1, n-1));
        turns+= temp;
        ////////println("temp:" + temp);
        turns+= "RUR'";
        turns += reverseDirections(temp);
      }
    }
  }

  //  STAGE 0
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  public void greenCross() {
    if (!(getFaceColor('D') == green)) {
      ////////println("Shit man");
      positionFace(green, 'D', 'X');
      return;
    }

    if (turns.length() != 0) {
      return;
    }
    positionGreenCrossColor(red);
    if (turns.length() != 0) {
      return;
    }
    positionGreenCrossColor(orange);
    if (turns.length() != 0) {
      return;
    }
    positionGreenCrossColor(yellow);
    if (turns.length() != 0) {
      return;
    }
    positionGreenCrossColor(white);
    if (turns.length() == 0) {
      stageNo++;
    }
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void positionGreenCrossColor(int col) {
    int[] colors = {col, green};
    Block piece = findCenterEdge(colors);
    if (piece.pos.y==n-1) {
      if (piece.getFace(green) == 'D') {
        if (getFaceColor(piece.getFace(col)) != col) {//if not inright place
          char faceToTurn = piece.getFace(col);
          turns += "" + faceToTurn+faceToTurn;
        }
        return;//ignore piece for now
      }
      //piece is on the bottom layer with green facing up
      char pieceFace =piece.getFace(green);
      String temp = getDirection(pieceFace, 'F');
      if (temp.length() ==2 && temp.charAt(0) == temp.charAt(1)) {
        temp = "YY";
      }
      turns+=temp;
      //now the piece we want is at the bottom front edge
      turns+= "FF";//chuck it at the top
      return;
    }
    if (piece.pos.y==middle) {//if in the middle row

      turnCubeToFacePiece(piece, green, 'Y');
      if (turns.length() > 0) {
        return;
      }

      if (piece.pos.x ==0) {
        turns+= "L'U'L";
      } else {
        turns += "RUR'";
      }
    }
    if (piece.pos.y==0) {//if in the top row
      ////////println("y=0");

      if (getFaceColor('F') != col) {
        positionFace(col, 'F', 'Y');//face the desired color to the front
        return;
      }
      if (piece.getFace(green) == 'F') {
        turns+="ULF'L'";
      } else if (piece.getFace(col) == 'F') {
        turns+= "FF";
      } else {
        char pieceFace =piece.getFace(col);
        if (pieceFace =='U') {
          pieceFace = piece.getFace(green);
        }

        String temp = getTurns(pieceFace, 'F', 0);
        temp = replaceDoubles(temp, 'U');
        ////////println(pieceFace, turns);

        turns +=temp;
      }
    }
  }

  //  HELPER FUNCTIONS
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------


  public int getFaceColor(char face) {
    int middle = numberOfSides/2;
    int last = numberOfSides-1;
    switch( face) {
    case 'D':
      return(cube.blocks[middle][last][middle].colors[3]);
    case 'U':
      return(cube.blocks[middle][0][middle].colors[2]);
    case 'L':
      return(cube.blocks[0][middle][middle].colors[0]);
    case 'R':
      return(cube.blocks[last][middle][middle].colors[1]);
    case 'F':
      return(cube.blocks[middle][middle][last].colors[5]);
    case 'B':
      return(cube.blocks[middle][middle][0].colors[4]);
    }
    return color(0);
  }


  public char getFaceGivenColor(int col) {
    String faces = "UDLRFB";
    for (int i = 0; i< 6; i++) { 
      if (getFaceColor(faces.charAt(i)) == col) {
        return faces.charAt(i);
      }
    }
    return ' ';
  }
  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------





  public Block findCenterEdge(int[] pieceColors) {
    for (int i = 0; i< edgeCenters.length; i++) { 
      PVector vec = edgeCenters[i];
      int x = (int)vec.x;
      int y = (int)vec.y;
      int z = (int)vec.z;

      if (cube.blocks[x][y][z].matchesColors(pieceColors)) {
        return cube.blocks[x][y][z];//new PVector(i, j, k);
      }
    }
    return null;
  }

  public Block findCornerPiece(int[] pieceColors) {

    for (int i = 0; i< numberOfSides; i+=n-1) { 
      for (int j = 0; j< numberOfSides; j+=n-1) { 
        for (int k = 0; k< numberOfSides; k+=n-1) { 
          if (cube.blocks[i][j][k].matchesColors(pieceColors)) {
            return cube.blocks[i][j][k];//new PVector(i, j, k);
          }
        }
      }
    }

    return null;
  }



  public Block findPiece(int[] pieceColors) {

    for (int i = 0; i< numberOfSides; i++) { 
      for (int j = 0; j< numberOfSides; j++) { 
        for (int k = 0; k< numberOfSides; k++) { 
          if (cube.blocks[i][j][k].matchesColors(pieceColors)) {
            return cube.blocks[i][j][k];//new PVector(i, j, k);
          }
        }
      }
    }

    return null;
  }

  //ok so you only need to define a face piece by the position relative to the center in the 1st (top right) quadrant 
  //  for example the very top right face piece on a 7x7 cube would be x =2 y = 2
  public Block findFacePiece(int pieceColor, int xIndex, int yIndex ) {
    xIndex += middle;
    yIndex += middle;
    String faces = "LRUDFB";
    for (int i = 0; i< faces.length(); i++) { 

      Block[][] face = cube.getFace(faces.charAt(i));


      if (face[xIndex][yIndex].matchesColors(pieceColor)) {
        return face[xIndex][yIndex];
      }
      if (face[yIndex][-xIndex].matchesColors(pieceColor)) {
        return face[yIndex][-xIndex];
      }
      if (face[-xIndex][-yIndex].matchesColors(pieceColor)) {
        return face[-xIndex][-yIndex];
      }
      if (face[-yIndex][xIndex].matchesColors(pieceColor)) {
        return face[-yIndex][xIndex];
      }
    }


    return null;
  }


  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void scramble() { 
    stageNo = 0;
    completedCorners = 0;
    completedEdges = 0;
    turnsDone = 0;


    if (n>3) {
      for (int i = 0; i< 15 * n; i++) { 
        turnOs.add(new TurnO());
      }
    } else {
      String options = "LRUDFBLRUDFBLRUDFBXYZ"; 
      for (int i = 0; i< 50; i++) { 
        turns += options.charAt(floor(random(options.length())));
      }
    }
  }

  //positions 

  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void positionFace(int col, char face, char turnDirectionThatWontFuckShitUp) {

    String faces = "UDLRFB";
    char fromFace = 'F';
    for (int i = 0; i< faces.length(); i++) { 
      if (getFaceColor(faces.charAt(i)) == col) {
        fromFace = faces.charAt(i);
        break;
      }
    }

    String temp = getDirection(fromFace, face);
    if (temp.length() ==2 && temp.charAt(0) == temp.charAt(1)) {
      temp = "" + turnDirectionThatWontFuckShitUp + turnDirectionThatWontFuckShitUp;
    }

    turns+=temp;
  }


  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------


  public String replaceDoubles(String temp, char turnDirectionThatWontFuckShitUp) {
    if (temp.length() ==2 && temp.charAt(0) == temp.charAt(1)) {
      temp = "" + turnDirectionThatWontFuckShitUp + turnDirectionThatWontFuckShitUp;
    }
    return temp;
  }


  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public void turnCubeToFacePiece(Block piece, int col, char turnDirectionThatWontFuckShitUp) {
    char pieceFace =piece.getFace(col);
    String temp = getDirection(pieceFace, 'F');
    if (temp.length() ==2 && temp.charAt(0) == temp.charAt(1)) {
      temp = "" + turnDirectionThatWontFuckShitUp + turnDirectionThatWontFuckShitUp;
    }
    turns +=temp;
  }

  //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

  public Block getCorner(char face1, char face2, char face3) {
    int[] cols = {getFaceColor(face1), getFaceColor(face2), getFaceColor(face3)};
    return findCornerPiece(cols);
  }



  //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


  public TurnO charToTurnO(char c, boolean clockwise) {
    //println(c);
    int axis =0;
    int index=0;
    switch(c) {
    case 'D':
      axis = 1;
      index = n-1;
      clockwise = !clockwise;
      break;
    case 'U':
      axis = 1;
      break;
    case 'L':
      break;
    case 'R':
      index = n-1;
      clockwise = !clockwise;
      break;
    case 'F':
      axis = 2;
      index = n-1;
      clockwise = !clockwise;
      break;    
    case 'B':
      axis = 2;
      break;
    }

    return new TurnO(axis, index, clockwise);
  }
}
String XRotation = "FDBU";
String YRotation = "FLBR";//'F', 'R', 'B', 'R'};
String ZRotation = "LDRB";//{'L', 'D', 'R', 'B'};

public ArrayList<TurnO> getTurnObjects(char fromFace, char toFace, int index, int idealAxis) {
  int fromIndex = XRotation.indexOf(fromFace);
  int toIndex = XRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotationGimmeObj(fromIndex, toIndex, index, 0, idealAxis);
  }
  fromIndex = YRotation.indexOf(fromFace);
  toIndex = YRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotationGimmeObj(fromIndex, toIndex, index, 1, idealAxis);
  }
  fromIndex = ZRotation.indexOf(fromFace);
  toIndex = ZRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotationGimmeObj(fromIndex, toIndex, index, 2, idealAxis);
  }
  return null;
}

//move edge to front left if not alreay in the middle row
public ArrayList<TurnO> moveEdgeToFrontLeft(char face1, char face2) {
  ArrayList<TurnO> returnList = new ArrayList();
  String middleRowFaces = "FRBL";
  boolean face1Middle = middleRowFaces.indexOf(face1) != -1;
  boolean face2Middle = middleRowFaces.indexOf(face2) != -1;

  if (face1Middle && face2Middle) {
    return new ArrayList<TurnO>();
  }
  char nonMidFace = face1;
  char midFace = face2;
  if (face1Middle) {
    nonMidFace = face2; 
    midFace = face1;
  }
  int yIndex = 0;
  if (nonMidFace == 'D') {
    yIndex = n-1;
  }

  returnList.addAll(getTurnObjects(midFace, 'L', yIndex, 1));


  if (nonMidFace == 'D') {
    returnList.add(new TurnO(0, 0, false));
  } else {
    returnList.add(new TurnO(0, 0, true));
  }
  return returnList;
}



//move edge to front right 
public ArrayList<TurnO> moveEdgeToFrontRight(char face1, char face2) {

  ArrayList<TurnO> returnList = new ArrayList();
  String middleRowFaces = "FRBL";
  boolean face1Middle = middleRowFaces.indexOf(face1) != -1;
  boolean face2Middle = middleRowFaces.indexOf(face2) != -1;

  if (face1Middle && face2Middle) {
    String test = "FL";
    if (test.indexOf(face1)!=-1 &&test.indexOf(face2)!=-1) {
      returnList.add(new TurnO(2, n-1, true));
      returnList.add(new TurnO(2, n-1, true));
      return returnList;
    }
    test = "BL";
    if (test.indexOf(face1)!=-1 &&test.indexOf(face2)!=-1) {
      returnList.add(new TurnO(0, 0, true));
      returnList.add(new TurnO(1, 0, false));
      returnList.add(new TurnO(2, n-1, false));
      return returnList;
    }

    test = "BR";
    if (test.indexOf(face1)!=-1 &&test.indexOf(face2)!=-1) {
      returnList.add(new TurnO(0, n-1, true));
      returnList.add(new TurnO(0, n-1, true));

      return returnList;
    }
    return new ArrayList<TurnO>();
  }


  char nonMidFace = face1;
  char midFace = face2;
  if (face1Middle) {
    nonMidFace = face2; 
    midFace = face1;
  }
  int yIndex = 0;
  if (nonMidFace == 'D') {
    yIndex = n-1;
  }

  returnList.addAll(getTurnObjects(midFace, 'R', yIndex, 1));


  if (nonMidFace == 'D') {
    returnList.add(new TurnO(0, n-1, false));
  } else {
    returnList.add(new TurnO(0, n-1, true));
  }
  return returnList;
}


public String getTurns(char fromFace, char toFace, int index) {
  int fromIndex = XRotation.indexOf(fromFace);
  int toIndex = XRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    if (index ==0) {
      return foundRotation(fromIndex, toIndex, 'L');
    } else {
      return foundRotation(fromIndex, toIndex, 'R');
    }
  }
  fromIndex = YRotation.indexOf(fromFace);
  toIndex = YRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    if (index ==0) {
      return foundRotation(fromIndex, toIndex, 'U');
    } else {
      return foundRotation(fromIndex, toIndex, 'D');
    }
  }
  fromIndex = ZRotation.indexOf(fromFace);
  toIndex = ZRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    if (index ==0) {
      return foundRotation(fromIndex, toIndex, 'B');
    } else {
      return foundRotation(fromIndex, toIndex, 'F');
    }
  }
  return "";
}


public String getDirection(char fromFace, char toFace) {
  int fromIndex = XRotation.indexOf(fromFace);
  int toIndex = XRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'X');
  }
  fromIndex = YRotation.indexOf(fromFace);
  toIndex = YRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'Y');
  }
  fromIndex = ZRotation.indexOf(fromFace);
  toIndex = ZRotation.indexOf(toFace);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'Z');
  }
  return "";
}



public ArrayList<TurnO> foundRotationGimmeObj(int fromIndex, int toIndex, int index, int axis, int idealAxis) {
  ArrayList<TurnO> turns = new ArrayList();
  TurnO t = new TurnO(idealAxis, index, true);
  if (abs(fromIndex - toIndex) == 2) {
    turns.add(t);
    turns.add(t);
    return turns;
  }

  t.axis = axis;

  if (fromIndex<=toIndex) {
    for (int i = fromIndex; i< toIndex; i++) { 
      turns.add(t);
    }
  } else {
    t.clockwise = false;
    for (int i = toIndex; i< fromIndex; i++) { 
      turns.add(t);
    }
  }
  return turns;
}

public String foundRotation(int fromIndex, int toIndex, char turnCharacter) {
  String returnString ="";
  if (abs(fromIndex - toIndex) ==2) {
    return "" + turnCharacter + turnCharacter;
  }
  if (fromIndex<=toIndex) {
    for (int i = fromIndex; i< toIndex; i++) { 
      returnString += turnCharacter;
    }
  } else {
    for (int i = toIndex; i< fromIndex; i++) { 
      returnString += turnCharacter + "\'";
    }
  }
  return returnString;
}




PVector[] cornerRotationU = {new PVector(0, 0, 0), new PVector(n-1, 0, 0), new PVector(n-1, 0, n-1), new PVector(0, 0, n-1)};
PVector[] cornerRotationD = {new PVector(0, n-1, 0), new PVector(0, n-1, n-1), new PVector(n-1, n-1, n-1), new PVector(n-1, n-1, 0)};


public String getDirectionsCorners(PVector from, PVector to) {

  int fromIndex = getIndex(cornerRotationU, from);
  int toIndex = getIndex(cornerRotationU, to);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'U');
  }

  fromIndex = getIndex(cornerRotationD, from);
  toIndex = getIndex(cornerRotationD, to);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'D');
  }

  return "";
  //will add others if needed
}


public int getIndex(PVector[] arr, PVector c) {
  for (int i = 0; i< arr.length; i++) { 
    if (pvectorsEqual(arr[i], c)) {
      return i;
    }
  }
  return -1;
}
public boolean pvectorsEqual(PVector p1, PVector p2) {
  return(p1.x ==p2.x && p1.y == p2.y &&  p1.z == p2.z);
}
//converts into reverse e.g. RULR' becomes RL'U'R'
public String reverseDirections(String original) {
  String reverse = "";
  for (int i = 0; i< original.length(); i++) {
    if (i+1< original.length() && original.charAt(i+1) == '\'') {
      reverse = original.charAt(i) + reverse;
      i+=1;
    } else {
      reverse = original.charAt(i) + "'" + reverse;
    }
  }

  return reverse;
}




PVector[] edgeRotationU = {new PVector(middle, 0, 0), new PVector(n-1, 0, middle), new PVector(middle, 0, n-1), new PVector(0, 0, middle)};
PVector[] edgeRotationD = {new PVector(middle, n-1, 0), new PVector(0, n-1, middle), new PVector(middle, n-1, n-1), new PVector(n-1, n-1, middle)};


public String getDirectionsEdges(PVector from, PVector to) {

  int fromIndex = getIndex(edgeRotationU, from);
  int toIndex = getIndex(edgeRotationU, to);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'U');
  }

  fromIndex = getIndex(edgeRotationD, from);
  toIndex = getIndex(cornerRotationD, to);
  if (fromIndex != -1 && toIndex !=-1) {
    return foundRotation(fromIndex, toIndex, 'D');
  }

  return "";
  //will add others if needed
}
class TurnO {
  int axis = 0;
  int index = 0;
  boolean clockwise = true;

  TurnO(int a, int i, boolean c) {
    axis =a;
    index = i;
    clockwise = c;
  }
  TurnO() {
    axis =floor(random(3));
    index = floor(random(n));
    if (random(1) <0.5f) {
      clockwise =false;
    }
  }

  public boolean matches(TurnO t) {
    return t.axis ==axis && t.index == index && clockwise == t.clockwise;
  }
  
  
  public void printTurn(){
    println("axis: " + axis +", index " + index + ", isclockwise " +clockwise);  
    
  }
  
  public TurnO getReverse(){
    TurnO reverse = new TurnO(axis,index,!clockwise);
    return reverse;
  }
}
  public void settings() {  size(1000, 1000, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RubixCube" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
