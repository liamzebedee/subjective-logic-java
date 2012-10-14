package com.dstc.spectrum.opinion;

import java.util.Iterator;
import java.util.SortedSet;

public abstract interface FuzzyOpinionSet extends Iterable<FuzzyOpinion>
{
  public abstract void setAtomicity(double paramDouble);

  public abstract double getAtomicity();

  public abstract FuzzyOpinion get(Opinion paramOpinion);

  public abstract String getText(Opinion paramOpinion);

  public abstract double getPolarization();

  public abstract void setPolarization(double paramDouble);

  public abstract String toString(FuzzyOpinion paramFuzzyOpinion);

  public abstract String toString();

  public abstract SortedSet<FuzzyOpinion> values();

  public abstract Iterator<FuzzyOpinion> iterator();

  public abstract FuzzySet getExpectationSet();

  public abstract FuzzySet getUncertaintySet();
}