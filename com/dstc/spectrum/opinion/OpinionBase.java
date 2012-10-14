package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;

public abstract class OpinionBase
  implements Opinion, Cloneable
{
  public static final double TOLERANCE = 1.0E-010D;
  protected static final double TOLERANCE_ADJUST = 100000000000.0D;
  private static final double LOG_HALF = Math.log10(0.5D);
  protected static final double MAX_VALUE = 1.797693134862316E+297D;
  protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  public static final double erosionFactorFromHalfLife(double halfLife)
  {
    if (halfLife < 0.0D) {
      throw new IllegalArgumentException("Half-life h, must be 0 <= h");
    }
    if (halfLife == 0.0D) {
      return 0.0D;
    }
    return constrain(1.0D - 1.0D / Math.pow(2.0D, 1.0D / halfLife));
  }

  public static final double halfLifeFromErosionFactor(double erosionFactor)
  {
    if ((erosionFactor < 0.0D) || (erosionFactor > 1.0D)) {
      throw new IllegalArgumentException("Erosion Factor e, must be 0 <= e <=1");
    }
    if (erosionFactor == 0.0D) {
      return 0.0D;
    }
    return adjust(LOG_HALF / Math.log10(1.0D - erosionFactor));
  }

  public static final double halfLifeFromResidual(double residual, double time)
  {
    return halfLifeFromErosionFactor(erosionFactorFromResidual(residual, time));
  }

  public static final double erosionFactorFromResidual(double residual, double time)
  {
    if ((residual < 0.0D) || (residual > 1.0D)) {
      throw new IllegalArgumentException("Residual r, must be 0 <= r <= 1");
    }
    if (time < 0.0D) {
      throw new IllegalArgumentException("Time t, must be 0 <= t");
    }
    if ((time == 0.0D) || (residual == 1.0D)) {
      return 0.0D;
    }

    if (residual == 0.0D) {
      residual = 1.0E-010D;
    }
    return 1.0D - Math.pow(residual, 1.0D / time);
  }

  public static final double erosionFactorFromHalfLife(double halfLife, double time)
  {
    if (halfLife < 0.0D) {
      throw new IllegalArgumentException("Half-life h, must be 0 <= h");
    }
    if (time < 0.0D) {
      throw new IllegalArgumentException("Time t, must be 0 <= t");
    }
    if ((time == 0.0D) || (halfLife == 0.0D)) {
      return 0.0D;
    }
    return Math.pow(erosionFactorFromHalfLife(halfLife), time);
  }

  protected static double adjust(double x)
  {
    return x >= 1.797693134862316E+297D ? 1.797693134862316E+297D : x == (0.0D / 0.0D) ? (0.0D / 0.0D) : Math.round(x * 100000000000.0D) / 100000000000.0D;
  }

  protected static double constrain(double x)
  {
    return Math.min(1.0D, Math.max(0.0D, x));
  }

  public int compareTo(Opinion arg0)
  {
    return OpinionComparator.DEFAULT.compare(this, arg0);
  }

  public Object clone() throws CloneNotSupportedException
  {
    OpinionBase o = (OpinionBase)super.clone();
    return o;
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

  public boolean hasListeners(String propertyName)
  {
    return this.changeSupport.hasListeners(propertyName);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    this.changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    this.changeSupport.removePropertyChangeListener(propertyName, listener);
  }
}