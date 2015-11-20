/*---------------------------------------------------------------------------

  PacMan clone.

  File: PacManLevel.java
  Date: 20.04.2002
  
  (C) Copyright 2002 Juha Kari.

---------------------------------------------------------------------------*/

import java.io.*;
import java.net.*;
import java.util.*;

public class PacManLevel
{
  private Vector level;
  private int levelWidth = 0;
  private int levelHeight = 0;

  private String levelFileName;
  private URL levelFileURL;
  private InputStream input;
  private BufferedReader data;

  public PacManLevel(URL docBase, String docName) throws NullPointerException, PacManLevelReadException, MalformedURLException, IOException
  {
    try
    {
    // Setting values for variables.
      levelFileName = docName;
      levelFileURL  = new URL(docBase, docName);
      input         = levelFileURL.openStream();
      data          = new BufferedReader(new InputStreamReader(input));

      String tmpLine;
      level = new Vector();
      boolean eof = false;

    // Reading the file line by line.
      while (data.ready() && eof == false)
      {
        tmpLine = data.readLine();
        if (tmpLine.length() < 1) eof = true;
        else if (tmpLine.length() <= 19  && levelHeight < 19)
        {
          levelHeight++;
          level.addElement(tmpLine);
          if (levelHeight == 1) levelWidth = tmpLine.length();
        }
        else throw new PacManLevelReadException(docName, "Too much rows or columns");
      }

      if (isValidLevel() == false) throw new PacManLevelReadException(docName, "Validity error");
    }
    catch (NullPointerException e) { throw e; }
    catch (PacManLevelReadException e) { throw e; }
    catch (MalformedURLException e) { throw e; }
    catch (IOException e) { throw e; }
  }

  public int getLevelWidth()
  {
    return levelWidth;
  }

  public int getLevelHeight()
  {
    return levelHeight;
  }

  public int getBlkWidth()
  {
    return (int)((levelWidth-1)/2);
  }

  public int getBlkHeight()
  {
    return (int)((levelHeight-1)/2);
  }

  public boolean getBlkAccess(double blkX, double blkY, int direction)
  {
    boolean access = false;
    switch (direction)
    {
      case 0: if (getLevelRow((int)blkY*2+1).charAt((int)blkX*2+1+1) != 'X') access = true; break;
      case 1: if (getLevelRow((int)blkY*2+1+1).charAt((int)blkX*2+1) != 'X') access = true; break;
      case 2: if (getLevelRow((int)blkY*2+1).charAt((int)blkX*2+1-1) != 'X') access = true; break;
      case 3: if (getLevelRow((int)blkY*2+1-1).charAt((int)blkX*2+1) != 'X') access = true; break;
    }
    return access;
  }

  public String getLevelRow(int r)
  {
    if (r >= 0 && r <= levelHeight-1) return (String)level.elementAt(r);
    else return null;
  }

  public String getLevelFileName()
  {
    return levelFileName;
  }

  public URL getLevelFileURL()
  {
    return levelFileURL;
  }

  public boolean isValidLevel()
  {
    boolean isValid = true;
    int len = 0;

    if (level != null && levelWidth >= 3 && levelHeight >= 3)
    {
    // Checking the length of the rows.  Should be equal.
      for (int y = 0; y < levelHeight; y++)
      {
        if (y == 0) len = getLevelRow(y).length();
        else if (getLevelRow(y).length() != len) isValid = false;
      }
    // Checking if the borders are marked properly.
      if (isValid) for (int x = 0; x < levelWidth; x++)
      {
        if (getLevelRow(0).charAt(x) != 'X' ||
            getLevelRow(levelHeight-1).charAt(x) != 'X') isValid = false;
      }
      if (isValid) for (int y = 0; y < levelHeight; y++)
      {
        if (getLevelRow(y).charAt(0) != 'X' ||
            getLevelRow(y).charAt(levelWidth-1) != 'X') isValid = false;
      }
    // Checking if there are lonesome X's.  Shouldn't be any.
      if (isValid) for (int y = 1; y < levelHeight-1; y++)
      {
        for (int x = 1; x < levelWidth-1; x++)
        {
          if (getLevelRow(y).charAt(x) == 'X')
          {
            if (getLevelRow(y).charAt(x-1) != 'X' &&
                getLevelRow(y).charAt(x+1) != 'X' &&
                getLevelRow(y-1).charAt(x) != 'X' &&
                getLevelRow(y+1).charAt(x) != 'X')
            {
              isValid = false;
            }
          }
        }
      }
    }
    else isValid = false;

    return isValid;
  }

  class PacManLevelReadException extends Exception
  {
    public PacManLevelReadException()
    {
      super("Error in reading PacMan level file!");
    }

    public PacManLevelReadException(String docName)
    {
      super("Error in reading PacMan level file " + docName + "!");
    }

    public PacManLevelReadException(String docName, String info)
    {
      super("Error in reading PacMan level file " + docName + "! " + info + "!" );
    }
  }
}
