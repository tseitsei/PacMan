/*---------------------------------------------------------------------------

  PacMan clone.

  File: PacMan.java
  Date: 20.04.2002

  (C) Copyright 2002 Juha Kari.

---------------------------------------------------------------------------*/

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.io.*;

public class PacMan extends Applet implements Runnable
{
// Threads.
  Thread repaintThread = null;
  int repaintThreadSleepTime = 20;

// Background buffer.
  Image bgBufScreen;
  Graphics bgBuf;

// Doublebuffer.
  Image dBufScreen;
  Graphics dBuf;

// Width, height and codebase.
  int appWidth = 0;
  int appHeight = 0;
  URL codeBase;

// Audio.
  AudioClip pacTune;

// PacMan himself.
  PacManCreature pacman;

// Level.
  PacManLevel level;

// Food storage.
  PacManFoodStorage foodStorage;

// Points.
  int points;

// KeyListener.
  KeyListener keyHandler;

// Waiting enter key after a game.
  boolean waitingEnter = true;

  private Exception ex;

  int lastKeyCode;
  boolean audioLoop = true;

  public void init()
  {
    lastKeyCode = 0;

    points = 0;
    pacman = null;

  // Width, height and codebase.
    appWidth = this.getSize().width;
    appHeight = this.getSize().height-20;

    int[] emptySpaceX = new int[appWidth*appHeight];
    int[] emptySpaceY = new int[appWidth*appHeight];
    int emptyCounter = 0;

    codeBase = getCodeBase();

    try
    {
      level = new PacManLevel(codeBase, "Level01.dat");
    }
    catch (NullPointerException ea) { ex = new Exception(ea.getMessage()); ea.printStackTrace(); }
    catch (PacManLevel.PacManLevelReadException eb) { ex = new Exception(eb.getMessage()); eb.printStackTrace(); }
    catch (MalformedURLException ec) { ex = new Exception(ec.getMessage()); ec.printStackTrace(); }
    catch (IOException ed) { ex = new Exception(ed.getMessage()); ed.printStackTrace(); }

    setBackground(Color.white);

  // Background buffer.
    bgBufScreen = createImage(appWidth, appHeight);
    bgBuf = bgBufScreen.getGraphics();

  // Clearing the background.
    bgBuf.setColor(Color.white);
    bgBuf.fillRect(0, 0, appWidth, appHeight+20);

    foodStorage = new PacManFoodStorage();

    if (ex == null)
    {
      for (int y = 0; y < level.getLevelHeight(); y++)
      {
	for (int x = 0; x < level.getLevelWidth(); x++)
	{
	// Drawing walls.
	  if (level.getLevelRow(y).charAt(x) == 'X')
	  {
	    bgBuf.setColor(Color.black);
	  // Drawing horizontal lines.
	    if (x>=1 && level.getLevelRow(y).charAt(x-1) == 'X')
	    {
	      bgBuf.drawLine(x*16, 8+y*16, 8+x*16, 8+y*16);
	    }
	    if (x<=level.getLevelWidth()-2 && level.getLevelRow(y).charAt(x+1) == 'X')
	    {
	      bgBuf.drawLine(8+x*16, 8+y*16, 16+x*16, 8+y*16);
	    }

	  // Drawing vertical lines.
	    if (y>=1 && level.getLevelRow(y-1).charAt(x) == 'X')
	    {
	      bgBuf.drawLine(8+x*16, y*16, 8+x*16, 8+y*16);
	    }
	    if (y<=level.getLevelHeight()-2 && level.getLevelRow(y+1).charAt(x) == 'X')
	    {
	      bgBuf.drawLine(8+x*16, 8+y*16, 8+x*16, 16+y*16);
	    }
	  }
	  else if (level.getLevelRow(y).charAt(x) == ' ')
	  {
	    if (x%2 == 1 && y%2 == 1)
	    {
	      emptySpaceX[emptyCounter] = x;
	      emptySpaceY[emptyCounter] = y;
	      emptyCounter++;
	    }
	  }
	}
      }
    // Making food.
      for (int i = 0; i < 10; i++)
      {
	int z = ((int)Math.round(Math.random()*1000000))%emptyCounter;
	  foodStorage.addFoodAt(emptySpaceX[z], emptySpaceY[z], 4);
      }
    }
    else bgBuf.drawString("Exception: " + ex.getMessage(), 10, 310);

  // Doublebuffer.
    dBufScreen = createImage(appWidth, appHeight+20);
    dBuf = dBufScreen.getGraphics();
    dBuf.setColor(getBackground());

  // Audio.
    pacTune = getAudioClip(getCodeBase(), "PacTune.au");

  // PacMan himself.
    try
    {
      pacman = new PacManCreature(this);
    }
    catch (InterruptedException e) { e.printStackTrace(); }

    if (keyHandler != null)
    {
      removeKeyListener(keyHandler);
      keyHandler = null;
    }
    keyHandler = new KeyHandler();
    addKeyListener(keyHandler);

    waitingEnter = false;

    requestFocus();
  }

  public void start()
  {
    pacTune.loop();
    if (repaintThread == null)
    {
      repaintThread = new Thread(this);
      repaintThread.start();
    }
  }

  public void run()
  {
    while (waitingEnter == true || ( pacman.isDeadMan() == false && ex == null ))
    {
      if (!waitingEnter && (foodStorage.getFoodNumber() > 0 ||
	  (pacman.getPacBlkX() != pacman.getPacBlkXInt()) ||
	  (pacman.getPacBlkY() != pacman.getPacBlkYInt())) )
      {
      // Moving PacMan.
	points += pacman.move(level, foodStorage);
      }
      else if (keyHandler instanceof KeyListener)
      {
	removeKeyListener(keyHandler);
	keyHandler = null;
	keyHandler = new KeyHandler2();
	addKeyListener(keyHandler);
	waitingEnter = true;
      }
    // Repainting the screen.
      repaint();

    // Trying to sleep.
      try
      {
	repaintThread.sleep(repaintThreadSleepTime);
      }
      catch (InterruptedException e)
      {
	e.printStackTrace();
      }
    }
  }

  public void paint(Graphics g)
  {
    update(g);
  }

  public void update(Graphics g)
  {
    dBuf.drawImage(bgBufScreen, 0, 0, this);

  // Drawing PacManFood.
    dBuf.setColor(Color.blue);
    for (int y = 0; y < level.getLevelHeight(); y++)
    {
      for (int x = 0; x < level.getLevelWidth(); x++)
      {
	if (foodStorage.thereIsFoodAt(x, y))
	{
	  dBuf.fillRect(5+x*16, 5+y*16, 6, 6);
	}
      }
    }

  // Drawing PacMan.
    dBuf.drawImage(pacman.getCurrentImage(),
		   pacman.getPacX(),
		   pacman.getPacY(),
		   this);

    dBuf.setColor(Color.white);
    dBuf.fillRect(0, 304, 304, 20);

    dBuf.setColor(Color.black);

    //dBuf.drawString("pacY (" + pacman.getPacBlkXInt() + "), blkY (" + pacman.getPacBlkYInt() + "), points (" + points + ")", 0, 50);
    //dBuf.drawString("pacY (" + (1+pacman.getPacBlkXInt()*2) + "), blkY (" + (1+pacman.getPacBlkYInt()*2) + "), points (" + points + ")", 0, 70);
    //for (int i = 0; i < foodStorage.getFoodNumber(); i++)
    //{
    //	dBuf.drawString("fooX (" + foodStorage.getX(i) + "), fooY (" + foodStorage.getY(i) + "), foodNumber (" + foodStorage.getFoodNumber() + ")", 0, 90+i*20);
    //}

    dBuf.drawString("PacManFood: " + foodStorage.getFoodNumber(), 200, 310);

    //dBuf.drawString(":" + lastKeyCode, 160, 310);

    if (waitingEnter)
    {
      dBuf.setColor(Color.red);
      dBuf.drawString("Press enter for a new game!", 10, 310);
    }

    g.drawImage(dBufScreen, 0, 0, this);
  }

  public void stop()
  {
  // If the repaintThread is still running it will be stopped.
    if (repaintThread != null) repaintThread.stop();
    pacTune.stop();
  }

  class KeyHandler implements KeyListener
  {
    public void keyReleased(KeyEvent k)
    {
      int n = 0;
      String s = "";
      pacman.keyAction(0, n, s);
      repaint();
    }

    public void keyPressed(KeyEvent k)
    {
      lastKeyCode = k.getKeyCode();
      if (k.getKeyCode() == 83)  // S key
      {
      // Toggle "music" on/off.
	if (audioLoop) { pacTune.stop(); audioLoop = false; }
	else { pacTune.loop(); audioLoop = true; }
      }
      else
      {
	int n = k.getKeyCode();
	String s = k.getKeyText(k.getKeyCode());
	pacman.keyAction(1, n, s);
	repaint();
      }
    }

    public void keyTyped(KeyEvent k)
    {
    }
  }

  class KeyHandler2 implements KeyListener
  {
    public void keyReleased(KeyEvent k)
    {
    }

    public void keyPressed(KeyEvent k)
    {
      lastKeyCode = k.getKeyCode();
      if (k.getKeyCode() == KeyEvent.VK_ENTER)
      {
      // Start a new game.
	init();
      }
      else if (k.getKeyCode() == 83)  // S key
      {
      // Toggle "music" on/off.
	if (audioLoop) { pacTune.stop(); audioLoop = false; }
	else { pacTune.loop(); audioLoop = true; }
      }
    }

    public void keyTyped(KeyEvent k)
    {
    }
  }

  public String getAppletInfo()
  {
    return "Name: PacMan\r\n" +
	"Author: Juha J. Kari\r\n" +
	"Version: 0.1";
  }
}
