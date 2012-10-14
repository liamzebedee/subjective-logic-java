package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Conditionals
  implements PropertyChangeListener
{
  private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  private SubjectiveOpinion positive = null;

  private SubjectiveOpinion negative = null;

  public Conditionals(SubjectiveOpinion positive, SubjectiveOpinion negative)
  {
    this();

    setPositive(positive);
    setNegative(negative);
  }

  public Conditionals(Conditionals conditionals)
  {
    this();

    set(conditionals);
  }

  private Conditionals()
  {
  }

  public void set(Conditionals conditionals)
  {
    if (conditionals == null)
      throw new NullPointerException();
    SubjectiveOpinion p;
    SubjectiveOpinion n;
    synchronized (conditionals)
    {
      p = new SubjectiveOpinion(conditionals.positive);
      n = new SubjectiveOpinion(conditionals.negative);
    }

    synchronized (this)
    {
      setPositive(p);
      setNegative(n);
    }
  }

  public synchronized Opinion getNegative()
  {
    return this.negative;
  }

  public synchronized void setNegative(SubjectiveOpinion negative)
  {
    if (negative == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    Opinion old = this.negative;
    this.negative = negative;

    this.changeSupport.firePropertyChange("negative", old, this);

    negative.addPropertyChangeListener(this);
  }

  public synchronized Opinion getPositive()
  {
    return this.positive;
  }

  public synchronized void setPositive(SubjectiveOpinion positive)
  {
    if (positive == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    Opinion old = this.positive;
    this.positive = positive;

    this.changeSupport.firePropertyChange("positive", old, this);

    positive.addPropertyChangeListener(this);
  }

  public synchronized void adjustPositiveExpectation(double expectation)
  {
    Opinion old = new SubjectiveOpinion(this.positive);
    this.positive.set(this.positive.adjustExpectation(expectation));

    this.changeSupport.firePropertyChange("positive", old, this);
  }

  public synchronized void adjustNegativeExpectation(double expectation)
  {
    Opinion old = new SubjectiveOpinion(this.negative);
    this.negative.set(this.negative.adjustExpectation(expectation));

    this.changeSupport.firePropertyChange("negative", old, this);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    this.changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    this.changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    return this.changeSupport.getPropertyChangeListeners();
  }

  public PropertyChangeListener[] getPropertyChangeListeners(String propertyName)
  {
    return this.changeSupport.getPropertyChangeListeners(propertyName);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    this.changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    this.changeSupport.removePropertyChangeListener(propertyName, listener);
  }

  public void propertyChange(PropertyChangeEvent evt)
  {
    synchronized (this)
    {
      if (evt.getSource() == this.positive)
        this.changeSupport.firePropertyChange("positive", null, this);
      else if (evt.getSource() == this.negative)
        this.changeSupport.firePropertyChange("negative", null, this);
    }
  }
}