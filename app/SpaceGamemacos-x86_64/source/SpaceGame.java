/* autogenerated by Processing revision 1282 on 2022-05-27 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import processing.sound.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SpaceGame extends PApplet {

Spaceship s1;

SoundFile laser;
Star[] stars = new Star[100];
ArrayList<Rock> rocks = new ArrayList<Rock>();
ArrayList<Laser> lasers = new ArrayList<Laser>();
ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
int score, level, rockTime, rockCount;
boolean play;
Timer rockTimer, puTimer;
PImage startScreen, gameOver;



 public void setup() {
  /* size commented out by preprocessor */;
  laser = new SoundFile(this, "Flash-laser-03.wav");
  s1 = new Spaceship();
  rockCount = 0;
  play = false;
  score = 0;
  level = 1;
  rockTime = 1000;
  play = false;
  startScreen = loadImage("start.png");
  gameOver = loadImage("gameOver.png");
  for (int i = 0; i < stars.length; i++) {
    stars[i] = new Star();
  }
  rockTimer = new Timer(rockTime);
  rockTimer.start();
  puTimer = new Timer(10000);
  puTimer.start();
}

 public void draw() {
  background(0);
  noCursor();

  // Check to see if play = true
  if (!play) {
    startScreen();
  } else {
    if (frameCount % 1000 == 10) {
      level ++;
    }

    infoPanel();
    //render stars
    for (int i = 0; i < stars.length; i++) {
      stars[i].display();
      stars[i].move();
      if (stars[i].reachedBottom()) {
        stars[i].y=-10;
      }
    }

    // Adding a rock based on the rock timer
    if (rockTimer.isFinished()) {
      rocks.add(new Rock(PApplet.parseInt(random(width)), -50));
      rockTimer.start();
    }

    // Add timer finished for powerup
    if (puTimer.isFinished()) {
      powerUps.add(new PowerUp(PApplet.parseInt(random(width)), -50));
      puTimer.start();
    }

    // Render powerup and detect ship collision
    for (int i = 0; i<powerUps.size(); i++) {
      PowerUp pu = powerUps.get(i);
      pu.display();
      pu.move();
      if (pu.reachedBottom()) {
        powerUps.remove(pu);
      }
      if (pu.intersect(s1)) {
        if (pu.type == 'h') {
          s1.health+=100;
        } else if (pu.type == 'l') {
          s1.laserCount+=100;
        } else if (pu.type == 't') {
          s1.turret++;
        }
        powerUps.remove(pu);
      }
    }

    // Render Rocks and detect ship collision
    for (int i = 0; i<rocks.size(); i++) {
      Rock rock = rocks.get(i);
      rock.display();
      rock.move();
      if (rock.reachedBottom()) {
        rockCount++;
        rocks.remove(rock);
      }
      if (rock.intersect(s1)) {
        rocks.remove(rock);
        s1.health-=rock.health;
        score+=rock.health/2;
      }
    }

    //Render Lasers and detect rock collision
    for (int i = 0; i<lasers.size(); i++) {
      Laser laser = lasers.get(i);
      for (int j = 0; j<rocks.size(); j++) {
        Rock rock = rocks.get(j);
        if (laser.intersect(rock)) {
          lasers.remove(laser);
          rock.diam-=30;
          if (rock.diam<20) {
            rocks.remove(rock);
            score+=rock.health;
          }
        }
      }
      laser.display();
      laser.move();
      if (laser.reachedTop()) {
        lasers.remove(laser);
      }
    }

    //Render the spaceship
    s1.display(mouseX, mouseY);

    // check the status of play and run Game Over
    if (s1.health<1 || rockCount > 9) {
      gameOver();
      noLoop();
    }
  }
}

 public void infoPanel() {
  fill(127, 127);
  rectMode(CORNER);
  rect(0, 0, width, 50);
  fill(255);
  textAlign(CENTER);
  textSize(30);
  text("Rock Count:" + rockCount + " Score:" + score + " Level:" + level + " Health:" + s1.health + " Ammo:" + s1.laserCount, width/2, 40);
}

 public void startScreen() {
  background(0); // consider using a graphic
  //image(startScreen,0,0)
  textAlign(CENTER);
  textSize(40);
  fill(255);
  text("Welcome to SpaceGame!", width/2, height/2-200);
  text("By: Ethan Reynolds", width/2, height/2-160);
  text("Press spacebar to start...", width/2, height/2-120);
  if (keyPressed || mousePressed) {
    play = true;
  }
}

 public void gameOver() {
  background(0); // consider using a graphic
  //image(gameOver,0,0)
  textAlign(CENTER);
  textSize(40);
  fill(255);
  text("Game Over!!", width/2, height/2-200);
  text("Final Score:" + score, width/2, height/2-160);
  text("Level Achieved:" + level, width/2, height/2-120);
}

 public void mousePressed() {
  if (play == true) {
    laser.play();
  }
  if (s1.fire()) {
    if (s1.turret == 1) {
      lasers.add(new Laser(s1.x, s1.y));
      s1.laserCount--;
    } else if (s1.turret == 2) {
      lasers.add(new Laser(s1.x-10, s1.y));
      lasers.add(new Laser(s1.x+10, s1.y));
      s1.laserCount--;
    }
  }
}


 public void keyPressed() {
  if (keyPressed) {
    if (key == ' ') {
      if (s1.fire()) {
        if (s1.turret == 1) {
          lasers.add(new Laser(s1.x, s1.y));
          s1.laserCount--;
        } else if (s1.turret == 2) {
          lasers.add(new Laser(s1.x-10, s1.y));
          lasers.add(new Laser(s1.x+10, s1.y));
          s1.laserCount--;
        }
      }
    }
  }
}

 public void ticker() {
}
class Laser {
  int x, y, diam, speed;

  // Member Methods
  Laser(int x, int y) {
    this.x = x;
    this.y = y;
    speed = 8;
    diam = 3;
  }

   public void display() {
    rectMode(CENTER);
    fill(255, 0, 0);
    noStroke();
    rect(x, y, diam, speed);
    //rect(x-20, y, diam, speed);
  }

   public void move() {
    y-=speed;
  }
   public boolean reachedTop() {
    if(y<-10) {
      return true;
    } else{
      return false;
    }
  }

   public boolean intersect(Rock r) {
    float distance = dist(x,y,r.x,r.y);
    if(distance < 30) {
      return true;
    } else {
      return false;
    }
  }
}
class PowerUp {
  int x, y, diam, speed, rand;
  char type;
  PImage pu;

  // Member Methods
  PowerUp(int x, int y) {
    this.x = x;
    this.y = y;
    speed = PApplet.parseInt(random(1, 7));
    diam = PApplet.parseInt(random(40, 60));
    rand = PApplet.parseInt(random(3));
    if (rand == 0) {
      type = 'l';
      pu = loadImage("laserCount.png"); // increase laser count
    } else if (rand == 1) {
      type = 'h';
      pu = loadImage("health.png"); // increase ship health
    } else {
      type = 't';
      pu = loadImage("turret1.png"); // increases turret count
    }
  }
   public void display() {
    // Rock
    fill(255);
    stroke(59);
    pu.resize(diam, diam);
    //rock.resize(diam,diam);   
    imageMode(CENTER);
    image(pu,x,y);
  }

   public void move() {
    y+=speed;
  }

   public boolean reachedBottom() {
    if (y>height+50) {
      return true;
    } else {
      return false;
    }
  }
   public boolean intersect(Spaceship s) {
    float distance = dist(x, y, s.x, s.y);
    if (distance < diam/2 + s.w) {
      return true;
    } else {
      return false;
    }
  }
}
class Rock {
  int x, y, diam, speed, health, rand;
  PImage rock;

  // Member Methods
  Rock(int x, int y) {
    this.x = x;
    this.y = y;
    speed = PApplet.parseInt(random(1, 5));
    diam = PApplet.parseInt(random(45, 65));
    health = diam/4;
    rand = PApplet.parseInt(random(3));
    if (rand == 0) {
      rock = loadImage("rock1.png");
    } else if(rand == 1) {
      rock = loadImage("rock2.png");
    } else {
      rock = loadImage("rock3.png");
    }
    
  }

   public void display() {
    // Rock
    fill(255);
    stroke(59);
    rock.resize(diam,diam);
    //ellipse(x, y, diam, diam);
    //quad();
    textAlign(CENTER, CENTER);
    textSize(8);
    fill(255);
    text(diam, x, y);
    imageMode(CENTER);
    image(rock,x,y);
  }

   public void move() {
    y+=speed;
  }

   public boolean reachedBottom() {
    if (y>height+50) {
      return true;
    } else {
      return false;
    }
  }
   public boolean intersect(Spaceship s) {
    float distance = dist(x, y, s.x, s.y);
    if (distance < diam/2 + s.w) {
      return true;
    } else {
      return false;
    }
  }
}
class Spaceship {
  // Member variables
  int x, y, w, health, laserCount, diam, turret;
  //PImage ship;

  // Constructor
  Spaceship() {
    x = 0;
    y = 0;
    w = 50;
    diam = 25;
    laserCount = 501;
    turret=1;
    health = 100;
    //ship = loadImage("Ship.png");
  }

  // Member Methods
   public void display(int tempX, int tempY) {
    x = tempX;
    y = tempY;
    //imageMode(CENTER);
    //ship.resize(80,80);
    //image(ship,x,y);
    stroke(255);
    rectMode(CENTER);
    // turrets
    fill(255, 0, 0);
    rect(x-20.5f, y, 5, 80);
    rect(x+20, y, 5, 80);
    // wings
    fill(0, 255, 0);
    triangle(x, y, x+50, y+40, x-50, y+40);
    // thrusters
    fill(0, 0, 255);
    ellipse(x-20, y+25, 15, 35);
    ellipse(x+20, y+25, 15, 35);
    // fuselage
    fill(255, 100, 0);
    ellipse(x, y, 25, 80);
    // windows
    fill(0, 150, 200);
    ellipse(x, y-20, 20, 10);
  }


   public boolean fire() {
    if (laserCount>0) {
      return true;
    } else {
      return false;
    }
  }

   public boolean intersect(Rock r) {
    float distance = dist(x,y,r.x,r.y);
    if(distance < w/2 + r.diam) {
      return true;
    } else {
      return false;
    }
  }
}
class Star{
  // Member variables
  int x,y,diam,speed;
  
  // Constructor
  Star() {
    x = PApplet.parseInt(random(width));
    y = PApplet.parseInt(random(height));
    diam = PApplet.parseInt(random(1,5));
    speed = PApplet.parseInt(random(1,5));
  }
 
  // Member Methods
   public void display() {
    fill(PApplet.parseInt(random(200,255)),PApplet.parseInt(random(200,255)),10);
    ellipse(x,y,diam,diam);
  }
  
   public void move() {
    y+=speed;
  }
  
   public boolean reachedBottom() {
    if(y>height+10) {
      return true;
    } else{
      return false;
    }
  }
}
// Example 10-5: Object-oriented timer

class Timer {

  int savedTime; // When Timer started
  int totalTime; // How long Timer should last

  Timer(int tempTotalTime) {
    totalTime = tempTotalTime;
  }

  // Starting the timer
   public void start() {
    // When the timer starts it stores the current time in milliseconds.
    savedTime = millis();
  }

  // The function isFinished() returns true if 5,000 ms have passed. 
  // The work of the timer is farmed out to this method.
   public boolean isFinished() { 
    // Check how much time has passed
    int passedTime = millis()- savedTime;
    if (passedTime > totalTime) {
      return true;
    } else {
      return false;
    }
  }
}


  public void settings() { size(1000, 1000); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceGame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}