package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeListener;

public abstract interface Opinion extends Comparable<Opinion>
{
  public abstract double getAtomicity();

  public abstract double getExpectation();

  public abstract SubjectiveOpinion toSubjectiveOpinion();

  public abstract PureBayesian toPureBayesian();

  public abstract int compareTo(Opinion paramOpinion);

  public abstract void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);

  public abstract void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);

  public abstract PropertyChangeListener[] getPropertyChangeListeners();

  public abstract PropertyChangeListener[] getPropertyChangeListeners(String paramString);

  public abstract boolean hasListeners(String paramString);

  public abstract void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);

  public abstract void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener);
}