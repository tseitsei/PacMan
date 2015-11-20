/*---------------------------------------------------------------------------

  PacMan clone.

  File: PacManFood.java
  Date: 20.04.2002

  (C) Copyright 2002 Juha Kari.

---------------------------------------------------------------------------*/

import java.util.*;

public class PacManFoodStorage
{
  private Vector foodStorage;

  public PacManFoodStorage()
  {
    foodStorage = new Vector();
  }

  public void addFoodAt(int x, int y, int size)
  {
    PacManFood food = new PacManFood(x, y, size);
    foodStorage.addElement(food);
  }

  public void removeFoodAt(int x, int y)
  {
    for (int i = 0; i < foodStorage.size(); i++)
    {
      PacManFood tmp = (PacManFood)foodStorage.elementAt(i);
      if (tmp.getX() == x && tmp.getY() == y) foodStorage.removeElementAt(i);
    }
  }

  public boolean thereIsFoodAt(int x, int y)
  {
    boolean isFood = false;
    for (int i = 0; i < foodStorage.size(); i++)
    {
      PacManFood tmp = (PacManFood)foodStorage.elementAt(i);
      if (tmp.getX() == x && tmp.getY() == y) isFood = true;
    }
    return isFood;
  }

  public int getFoodSizeAt(int x, int y)
  {
    int foodSize = 0;
    for (int i = 0; i < foodStorage.size(); i++)
    {
      PacManFood tmp = (PacManFood)foodStorage.elementAt(i);
      if (tmp.getX() == x && tmp.getY() == y) foodSize = tmp.getSize();
    }
    return foodSize;
  }

  public int getFoodNumber()
  {
    return foodStorage.size();
  }

  public int getX(int i)
  {
    PacManFood tmp = (PacManFood)foodStorage.elementAt(i);
    return tmp.getX();
  }

  public int getY(int i)
  {
    PacManFood tmp = (PacManFood)foodStorage.elementAt(i);
    return tmp.getY();
  }

  public class PacManFood
  {
    private int x;
    private int y;
    private int size;

    public PacManFood(int x, int y, int size)
    {
      this.x = x;
      this.y = y;
      this.size = size;
    }

    public int getX()    { return x; }
    public int getY()    { return y; }
    public int getSize() { return size; }
  }
}
