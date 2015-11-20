/*---------------------------------------------------------------------------

  PacMan clone.

  File: PacManCreature.java
  Date: 20.04.2002

  (C) Copyright 2002 Juha Kari.

---------------------------------------------------------------------------*/

import java.awt.*;
import java.awt.image.*;
import java.applet.*;
import java.net.*;

public class PacManCreature
{
// Images and MediaTracker.
  Image imageTable[][];
  MediaTracker tracker;

// PacMan's coordinates and bitmap#.
  int pacX = 11;
  int pacY = 11;
  int pacXBox = 0;
  int pacYBox = 0;
  int pacImg = 0;
  int pacImgCount = 0;
  int pacImgCountMax = 10;
  int pacDir = 0;
  int pacIsAboutToGoDir = 0;
  boolean pacMov = false;
  boolean deadMan = false;

// Applet window size.
  int appWidth = 0;
  int appHeight = 0;
  URL codeBase;

  public PacManCreature(Applet appl) throws InterruptedException
  {
  // Applet window size.
    appWidth = appl.getSize().width;
    appHeight = appl.getSize().height;
    codeBase = appl.getCodeBase();

    imageTable = new Image [4][2];
    tracker = new MediaTracker(appl);
  // Images.
    imageTable[0][0] = appl.getImage(codeBase, "PacMan00.gif");
    imageTable[0][1] = appl.getImage(codeBase, "PacMan01.gif");
    imageTable[1][0] = appl.getImage(codeBase, "PacMan10.gif");
    imageTable[1][1] = appl.getImage(codeBase, "PacMan11.gif");
    imageTable[2][0] = appl.getImage(codeBase, "PacMan20.gif");
    imageTable[2][1] = appl.getImage(codeBase, "PacMan21.gif");
    imageTable[3][0] = appl.getImage(codeBase, "PacMan30.gif");
    imageTable[3][1] = appl.getImage(codeBase, "PacMan31.gif");

  // MediaTracker.
    try
    {
      tracker.addImage(imageTable[0][0], 0);
      tracker.addImage(imageTable[0][1], 1);
      tracker.addImage(imageTable[1][0], 2);
      tracker.addImage(imageTable[1][1], 3);
      tracker.addImage(imageTable[2][0], 4);
      tracker.addImage(imageTable[2][1], 5);
      tracker.addImage(imageTable[3][0], 6);
      tracker.addImage(imageTable[3][1], 7);
      tracker.waitForAll();
    }
    catch (InterruptedException e)
    {
      throw e;
    }
  }

  public int move(PacManLevel level, PacManFoodStorage foodStorage)
  {
  // Opening and closing PacMan's mouth.
    if (pacImgCount < pacImgCountMax) pacImgCount++;
    else
    {
      pacImgCount = 0;
      pacImg = (pacImg != 0) ? 0 : 1;
    }

  // Turning PacMan and checking walls.
    if (getPacBlkX()%1 == 0 && getPacBlkY()%1 == 0)
    {
      pacDir = pacIsAboutToGoDir;
      pacMov = false;
      if (level.getBlkAccess(getPacBlkX(), getPacBlkY(), pacIsAboutToGoDir) == true) pacMov = true;
    }

  // Moving PacMan.
    if (pacMov == true) switch (pacDir)
    {
      case 0: pacX+=2;  break;  // Right
      case 1: pacY+=2;  break;  // Down
      case 2: pacX-=2;  break;  // Left
      case 3: pacY-=2;  break;  // Up
    }

  // Checking if PacMan is going too far away.
    if (getPacBlkX() < 0)
    {
      pacMov = false;
      pacX = 11;
    }
    else if (getPacBlkX() > level.getBlkWidth()-1)
    {
      pacMov = false;
      pacX = (11+(level.getBlkWidth()-1)*32);
    }
    else if (getPacBlkY() < 0)
    {
      pacMov = false;
      pacY = 11;
    }
    else if (getPacBlkY() > level.getBlkHeight()-1)
    {
      pacMov = false;
      pacY = (11+(level.getBlkHeight()-1)*32);
    }

  // Eating food if possible.
    int points = 0;
    if (foodStorage.thereIsFoodAt(getPacBlkXInt()*2+1, getPacBlkYInt()*2+1))
    {
      points += foodStorage.getFoodSizeAt(getPacBlkXInt()*2+1, getPacBlkYInt()*2+1);
      foodStorage.removeFoodAt(getPacBlkXInt()*2+1, getPacBlkYInt()*2+1);
    }

    return points;
  }

  public boolean isDeadMan()
  {
    return deadMan;
  }

  public Image getCurrentImage()
  {
    return imageTable[pacDir][pacImg];
  }

  public int getPacX()
  {
    return pacX;
  }

  public int getPacY()
  {
    return pacY;
  }

  public int getPacBlkXInt()
  {
    return (int)java.lang.Math.round((double)(pacX-11)/32);
  }

  public double getPacBlkX()
  {
    return (double)(pacX-11)/32;
  }

  public int getPacBlkYInt()
  {
    return (int)java.lang.Math.round((double)(pacY-11)/32);
  }

  public double getPacBlkY()
  {
    return (double)(pacY-11)/32;
  }

  public void keyAction(int status, int k, String s)
  {
    if (status == 1) switch (k)
    {
      case 39: pacMov = true; pacIsAboutToGoDir = 0;  break;  // Right arrow
      case 40: pacMov = true; pacIsAboutToGoDir = 1;  break;  // Down arrow
      case 37: pacMov = true; pacIsAboutToGoDir = 2;  break;  // Left arrow
      case 38: pacMov = true; pacIsAboutToGoDir = 3;  break;  // Up arrow
    }
  }
}
