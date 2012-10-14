package com.dstc.spectrum.opinion;

public class FuzzySetElement
  implements Comparable<FuzzySetElement>
{
  private static final double TOLERANCE = 1.0E-010D;
  private static final double TOLERANCE_ADJUST = 100000000000.0D;
  private Object tag;
  private String text = null;

  private double exemplar = 0.0D;

  private double maximum = 0.0D;

  private double minimum = 0.0D;

  private boolean autoRanging = true;

  protected static double adjust(double x)
  {
    return x == (0.0D / 0.0D) ? (0.0D / 0.0D) : Math.round(x * 100000000000.0D) / 100000000000.0D;
  }

  protected FuzzySetElement(Object tag, String text, double bound0, double bound1)
  {
    if ((this.exemplar > 1.0D) || (this.exemplar < 0.0D) || (bound0 > 1.0D) || (bound0 < 0.0D) || (bound1 > 1.0D) || (bound1 < 0.0D)) {
      throw new IllegalArgumentException("Bounds and exemplar muct be between 0 and 1");
    }
    double min = Math.min(bound0, bound1);
    double max = Math.max(bound0, bound1);

    setTag(tag);
    setText(text);
    setExemplar((min + max) / 2.0D);
    setMinimum(min);
    setMaximum(max);
    setAutoRanging(false);
  }

  protected FuzzySetElement(Object tag, String text, double exemplar)
  {
    if ((exemplar > 1.0D) || (exemplar < 0.0D)) {
      throw new IllegalArgumentException("Exemplar muct be between 0 and 1");
    }
    setTag(tag);
    setText(text);
    setExemplar(exemplar);
    setAutoRanging(true);
  }

  protected FuzzySetElement(FuzzySetElement e)
  {
    synchronized (e)
    {
      this.tag = e.tag;
      this.text = e.text;
      this.minimum = e.minimum;
      this.maximum = e.maximum;
      this.autoRanging = e.autoRanging;
      this.exemplar = e.exemplar;
    }
  }

  public int compareTo(FuzzySetElement o)
  {
    if (o == null) {
      throw new NullPointerException("Fuzzy Value must not be null");
    }
    return Double.compare(this.exemplar, o.exemplar);
  }

  public synchronized Object getTag()
  {
    return this.tag;
  }

  public synchronized double getExemplar()
  {
    return this.exemplar;
  }

  protected synchronized void setTag(Object tag)
  {
    if (tag == null) {
      throw new NullPointerException("Tag must not be null");
    }
    this.tag = tag;
  }

  public synchronized double getMaximum()
  {
    return this.maximum;
  }

  public synchronized double getMinimum()
  {
    return this.minimum;
  }

  public synchronized double getExtentAbove()
  {
    return adjust(this.maximum - this.exemplar);
  }

  protected synchronized void setMaximum(double maximum)
  {
    if ((this.exemplar < 0.0D) || (this.exemplar > 1.0D) || (Double.isNaN(this.exemplar)) || (maximum < this.exemplar)) {
      throw new IllegalArgumentException("Maximum, x, and Value, v, must be: 0 <= v <= x <= 1");
    }
    this.maximum = adjust(maximum);
  }

  protected synchronized void setMinimum(double minimum)
  {
    if ((this.exemplar < 0.0D) || (this.exemplar > 1.0D) || (Double.isNaN(this.exemplar)) || (minimum > this.exemplar)) {
      throw new IllegalArgumentException("Minimum, x, and Value, v, must be: 0 <= x <= v <= 1");
    }
    this.minimum = adjust(minimum);
  }

  protected synchronized void setExemplar(double value)
  {
    if ((value < 0.0D) || (value > 1.0D) || (Double.isNaN(value))) {
      throw new IllegalArgumentException("Value, x, must be: 0 <= x <= 1");
    }
    this.exemplar = value;
  }

  public synchronized boolean isAutoRanging()
  {
    return this.autoRanging;
  }

  protected synchronized void setAutoRanging(boolean autoRanging)
  {
    this.autoRanging = autoRanging;
  }

  public synchronized double getExtentBelow()
  {
    return adjust(this.exemplar - this.minimum);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("(");
    sb.append(this.text == null ? null : this.text);
    sb.append(", xmplr=");
    sb.append(String.valueOf(getExemplar()));
    sb.append(", min=");
    sb.append(String.valueOf(getMinimum()));
    sb.append(", max=");
    sb.append(String.valueOf(getMaximum()));
    sb.append(")");

    return sb.toString();
  }

  public synchronized String getText()
  {
    return this.text;
  }

  protected synchronized void setText(String text)
  {
    this.text = text;
  }
}