package com.dstc.spectrum.opinion;

public abstract interface Bayesian<T>
{
  public abstract double getNegative();

  public abstract double getPositive();

  public abstract double max();

  public abstract double min();

  public abstract int size();

  public abstract double[] values();
}