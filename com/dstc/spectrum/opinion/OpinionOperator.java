package com.dstc.spectrum.opinion;

import java.util.HashSet;
import java.util.Set;

public enum OpinionOperator
{
  Discount(2, "%1$s:%2$s"), 

  Fuse(2, 2147483647, "%1$s¤%2$s"), 

  Add(2, 2147483647, "%1$s+%2$s"), 

  Subtract(2, "%1$s-%2$s"), 

  Or(2, 2147483647, "%1$s|%2$s"), 

  And(2, 2147483647, "%1$s&%2$s"), 

  SimpleOr(2, "%1$s||%2$s"), 

  SimpleAnd(2, "%1$s&&%2$s"), 

  UnOr(2, "%1$s~|%2$s"), 

  UnAnd(2, "%1$s~&%2$s"), 

  Not(1, "¬%1$s"), 

  Deduce(3, "Deduce(%1$s,%2$s,%3$s)"), 

  Abduce(4, "Abduce(%1$s,%2$s,%3$s,%4$s)");

  private String format;
  private String tag;
  private boolean commutative = false;

  private boolean associative = false;

  private int minArgs = 0;

  private int maxArgs = 0;

  private Set<OpinionOperator> distributesOver = null;

  static
  {
    Or.commutative = true;
    Or.associative = true;

    And.commutative = true;
    And.associative = true;

    Add.associative = true;
    Add.commutative = true;

    Fuse.commutative = true;
    Fuse.associative = true;

    Discount.associative = true;

    Not.associative = true;
    Not.commutative = true;
  }

  public static OpinionOperator get(String tag)
  {
    return (OpinionOperator)Enum.valueOf(OpinionOperator.class, tag);
  }

  public void distributesOver(OpinionOperator operator)
  {
    if (operator == null) {
      throw new NullPointerException();
    }
    synchronized (this)
    {
      if (this.distributesOver == null) {
        this.distributesOver = new HashSet();
      }
      this.distributesOver.add(operator);
    }
  }

  public boolean isDistributiveOver(OpinionOperator operator)
  {
    synchronized (this)
    {
      if (this.distributesOver == null) {
        return false;
      }
      return this.distributesOver.contains(operator);
    }
  }

  private OpinionOperator(int minArgs, int maxArgs, String format)
  {
    if ((minArgs < 0) || (maxArgs < 0)) {
      throw new IllegalArgumentException("Arguments must be greater than or equal to zero.");
    }
    if (format == null) {
      throw new NullPointerException("String format must not be null.");
    }
    int min = Math.min(minArgs, maxArgs);
    int max = Math.max(minArgs, maxArgs);

    this.minArgs = min;
    this.maxArgs = max;
    this.format = format;
  }

  private OpinionOperator(int args, String format)
  {
    this(args, args, format);
  }

  public String format(Object[] args)
  {
    return String.format(this.format, args);
  }

  public String toString()
  {
    return this.tag;
  }

  public boolean isAssociative()
  {
    return this.associative;
  }

  public boolean isCommutative()
  {
    return this.commutative;
  }

  public int getMinArgs()
  {
    return this.minArgs;
  }

  public int getMaxArgs()
  {
    return this.maxArgs;
  }

  public boolean checkArgCount(int args)
  {
    return (args >= this.minArgs) && (args <= this.maxArgs);
  }
}