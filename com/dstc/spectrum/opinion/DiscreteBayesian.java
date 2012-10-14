package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeSupport;
import java.util.Collection;

public class DiscreteBayesian extends OpinionBase
  implements Bayesian<DiscreteBayesian>
{
  public static double DEFAULT_POLARIZATION = 0.5D;

  private static String EXTRAS_FORMAT = "(%1$s, a=%2$1.3f, e=%3$1.3f)";

  private static String FORMAT = "%1$1.3f";

  private static double ROOT_2 = Math.sqrt(2.0D);

  private double atomicity = 0.5D;
  private double[] buckets;
  private double expectation = 0.0D;

  private double negative = 0.0D;

  private double positive = 0.0D;

  private boolean recalculate = false;

  private double rs2 = 2.0D;

  private static final DiscreteBayesian consensus(DiscreteBayesian x, DiscreteBayesian y)
    throws OpinionArithmeticException
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    if (x.size() != y.size()) {
      throw new OpinionArithmeticException("Discrete Bayesian beliefs must have the same size");
    }
    DiscreteBayesian o = new DiscreteBayesian(x.size());

    double sumx = 0.0D; double sumy = 0.0D;

    int i = 0; for (int size = x.size(); i < size; i++)
    {
      x.buckets[i] += y.buckets[i];
      sumx += x.buckets[i];
      sumy += y.buckets[i];
    }

    if (Math.abs(sumx - sumy) < 1.0E-010D)
      o.atomicity = x.atomicity;
    else {
      o.atomicity = ((sumx * x.atomicity + sumy * y.atomicity) / (sumx + sumy));
    }
    o.recalculate = true;

    return o;
  }

  private static final DiscreteBayesian discount(Opinion discounter, DiscreteBayesian opinion)
  {
    if ((discounter == null) || (opinion == null)) {
      throw new NullPointerException();
    }
    DiscreteBayesian y = new DiscreteBayesian(opinion);
    DiscreteBayesian o = new DiscreteBayesian(y.size());
    double scale;
    double scale;
    if ((discounter instanceof PureBayesian)) {
      scale = discountScale(new PureBayesian(discounter), opinion);
    }
    else
    {
      double scale;
      if ((discounter instanceof DiscreteBayesian))
        scale = discountScale(discounter.toPureBayesian(), opinion);
      else
        scale = discountScale(new SubjectiveOpinion(discounter), opinion);
    }
    int i = 0; for (int size = o.size(); i < size; i++) {
      y.buckets[i] *= scale;
    }
    o.recalculate = true;

    return o;
  }

  private static final double discountScale(PureBayesian discounter, DiscreteBayesian opinion)
  {
    double r = discounter.getPositive();
    double s = discounter.getNegative();
    double w = discounter.getWeight();

    double divisor = (opinion.getWeight() + 2.0D) * (s + 2.0D) + 2.0D * r;
    return divisor == 0.0D ? 0.0D : 2.0D * r / divisor;
  }

  private static final double discountScale(SubjectiveOpinion discounter, DiscreteBayesian opinion)
  {
    double b = discounter.getBelief();
    double d = discounter.getDisbelief();
    double u = discounter.getUncertainty();

    double divisor = (d + u) * (opinion.getWeight() + 2.0D) + 2.0D * b;
    return divisor == 0.0D ? 0.0D : 2.0D * b / divisor;
  }

  private static final DiscreteBayesian erosion(DiscreteBayesian x, double factor)
  {
    if (x == null) {
      throw new NullPointerException();
    }
    if ((factor < 0.0D) || (factor > 1.0D)) {
      throw new IllegalArgumentException("Erosion Factor, f must be: 0 <= f <= 1");
    }
    synchronized (x)
    {
      DiscreteBayesian o = new DiscreteBayesian();

      double f = 1.0D - factor;

      int i = 0; for (int size = o.size(); i < size; i++) {
        x.buckets[i] *= f;
      }
      o.atomicity = x.atomicity;

      o.recalculate = true;

      return o;
    }
  }

  public static final DiscreteBayesian fuse(Collection<? extends DiscreteBayesian> opinions) throws OpinionArithmeticException
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    DiscreteBayesian x = null;

    if (opinions.isEmpty()) {
      return x;
    }
    for (DiscreteBayesian opinion : opinions) {
      if (opinion != null)
        x = x == null ? opinion : x.fuse(opinion);
    }
    return x;
  }

  public DiscreteBayesian()
  {
    this(2);
  }

  public DiscreteBayesian(DiscreteBayesian o)
  {
    if (o == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    synchronized (o)
    {
      this.buckets = new double[o.buckets.length];

      int i = 0; for (int size = this.buckets.length; i < size; i++) {
        this.buckets[i] = o.buckets[i];
      }
      this.atomicity = o.atomicity;
      this.expectation = o.expectation;
      this.recalculate = o.recalculate;
    }
  }

  public DiscreteBayesian(double[] bucketValues)
  {
    if (bucketValues == null) {
      throw new NullPointerException("Bucket Values must not be null");
    }
    if (bucketValues.length < 2) {
      throw new IllegalArgumentException("Bucket size, x, must be: x > 1");
    }
    this.buckets = new double[bucketValues.length];

    setValue(bucketValues);
  }

  public DiscreteBayesian(double[] bucketValues, double atomicity)
  {
    this(bucketValues);
    setAtomicity(atomicity);
  }

  public DiscreteBayesian(int size)
  {
    if (size < 2) {
      throw new IllegalArgumentException("Bucket size, x, must be: x > 1");
    }
    this.buckets = new double[size];
  }

  public DiscreteBayesian(Opinion o, int size)
  {
    if (o == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    if (size < 2)
      throw new IllegalArgumentException("Conversion not possible");
    DiscreteBayesian x;
    DiscreteBayesian x;
    if ((o instanceof SubjectiveOpinion)) {
      x = ((SubjectiveOpinion)o).toDiscreteBayesian(size);
    }
    else
    {
      DiscreteBayesian x;
      if ((o instanceof PureBayesian))
        x = ((PureBayesian)o).toDiscreteBayesian(size);
      else if ((o instanceof DiscreteBayesian))
        x = ((DiscreteBayesian)o).toDiscreteBayesian(size);
      else
        throw new ClassCastException("Opinion is not an instance of Bayesian, Subjective Opinion");
    }
    this.buckets = x.buckets;
    this.atomicity = x.atomicity;
    this.rs2 = x.rs2;
    this.expectation = x.expectation;
    this.recalculate = x.recalculate;
  }

  public void addScalar(double value)
  {
    if (Double.isNaN(value)) {
      throw new IllegalArgumentException("Value must not be a NaN");
    }
    if ((value < 0.0D) || (value > 1.0D)) {
      throw new IllegalArgumentException("Value, x, must be: 0 <= x <= 1");
    }
    setValue((int)Math.floor(value * this.buckets.length), 1.0D);
  }

  private double[] convert(double[] array, int size)
  {
    if (array == null) {
      throw new NullPointerException("Array must not be null");
    }
    if (size < 2)
      throw new IllegalArgumentException("Conversion not possible");
    double[] results;
    if (size > array.length)
    {
      double[] results = new double[array.length + 1];

      int targetLength = results.length - 2;
      int k = array.length;
      double slip = Math.min(array[0], array[(k - 1)]) / k + 1;

      results[1] = slip;
      array[0] -= slip;

      results[(results.length - 2)] += slip;
      array[(array.length - 1)] -= slip;

      int i = 1; for (int count = array.length - 1; i < count; i++)
      {
        results[i] += targetLength - i * array[i] / targetLength;
        results[(i + 1)] += i * array[i] / targetLength;
      }

    }
    else
    {
      results = new double[array.length - 1];

      int targetLength = results.length;

      int i = 0; for (int count = results.length; i < count; i++)
      {
        results[i] += targetLength - i * array[i] / targetLength;
        results[i] += i + 1 * array[(i + 1)] / targetLength;
      }
    }

    if (results.length == size)
    {
      int i = 0; for (int count = results.length; i < count; i++) {
        results[i] = OpinionBase.adjust(results[i]);
      }
      return results;
    }

    return convert(results, size);
  }

  public final DiscreteBayesian decay(double halfLife, double time)
  {
    return erosion(this, OpinionBase.erosionFactorFromHalfLife(halfLife, time));
  }

  public final DiscreteBayesian discountBy(Opinion opinion)
  {
    return discount(opinion, this);
  }

  public boolean equals(Object obj)
  {
    if ((obj != null) && ((obj instanceof DiscreteBayesian)))
    {
      DiscreteBayesian o = (DiscreteBayesian)obj;

      if ((o.atomicity == this.atomicity) && (o.buckets.length == this.buckets.length))
      {
        int i = 0; for (int size = this.buckets.length; i < size; i++) {
          if (this.buckets[i] != o.buckets[i])
            return false;
        }
        return true;
      }
    }

    return false;
  }

  public final DiscreteBayesian erode(double factor)
  {
    return erosion(this, factor);
  }

  public final DiscreteBayesian fuse(DiscreteBayesian opinion) throws OpinionArithmeticException
  {
    return consensus(new DiscreteBayesian(this), new DiscreteBayesian(opinion));
  }

  public double getAtomicity()
  {
    return this.atomicity;
  }

  public double getExpectation()
  {
    synchronized (this)
    {
      setDependants();
      return this.expectation;
    }
  }

  public synchronized double getNegative()
  {
    setDependants();

    return this.negative;
  }

  public synchronized double getPositive()
  {
    setDependants();

    return this.positive;
  }

  public double getValue(int index)
    throws IndexOutOfBoundsException
  {
    if ((index < 0) || (index > this.buckets.length)) {
      throw new IndexOutOfBoundsException("Index is out of range");
    }
    synchronized (this)
    {
      return this.buckets[index];
    }
  }

  public double getWeight()
  {
    double x = 0.0D;

    synchronized (this)
    {
      for (int i = 0; i < this.buckets.length; i++) {
        x += this.buckets[i];
      }
    }
    return x;
  }

  public boolean isPolarized()
  {
    return isPolarized(DEFAULT_POLARIZATION);
  }

  public boolean isPolarized(double tolerance)
  {
    if ((tolerance < 0.0D) || (tolerance > 1.0D)) {
      throw new IllegalArgumentException("Threshold, x: 0 <= x <= 1");
    }
    double similarity = Math.log10(2.0D - tolerance);
    double scale = ROOT_2 * similarity;

    double lValue = 0.0D;
    double rValue = 0.0D;
    double mlValue = 0.0D;
    double mrValue = 0.0D;

    synchronized (this)
    {
      double n2 = this.buckets.length / 2.0D;
      int eSize = (int)Math.floor(n2);
      int mSize = eSize + (eSize + 1) % 2;

      int lEnd = eSize - 1;
      int rStart = this.buckets.length - eSize;

      int mStart = (this.buckets.length - mSize) / 2;
      int mEnd = mStart + mSize - 1;

      int i = 0; for (int size = this.buckets.length; i < size; i++)
      {
        if (i <= lEnd)
        {
          lValue += eSize - i * this.buckets[i] / eSize + 1.0D;
        }
        else if (i >= rStart)
        {
          rValue += i - (rStart - 1) * this.buckets[i] / eSize + 1.0D;
        }

        if ((size > 2) && (i >= mStart) && (i <= mEnd))
        {
          if (i - mStart <= mEnd - i) {
            mlValue += this.buckets[i] * i - mStart + 1 / eSize + 1.0D;
          }
          if (i - mStart >= mEnd - i) {
            mrValue += this.buckets[i] * mEnd - i + 1 / eSize + 1.0D;
          }
        }

      }

    }

    double r_l = Math.abs(Math.log10(lValue) - Math.log10(rValue));
    double l_m = Math.log10(lValue) - Math.log10(mlValue);
    double r_m = Math.log10(rValue) - Math.log10(mrValue);

    return (r_l <= similarity) && (l_m > scale) && (r_m > scale);
  }

  public synchronized double max()
  {
    double max = 0.0D;

    int i = 0; for (int size = this.buckets.length; i < size; i++) {
      if (this.buckets[i] > max)
        max = this.buckets[i];
    }
    return max;
  }

  public synchronized double min()
  {
    double min = 1.7976931348623157E+308D;

    int i = 0; for (int size = this.buckets.length; i < size; i++) {
      if (this.buckets[i] < min)
        min = this.buckets[i];
    }
    return min;
  }

  public DiscreteBayesian not()
  {
    DiscreteBayesian o = new DiscreteBayesian(size());

    synchronized (this)
    {
      int size = (int)Math.floor(this.buckets.length / 2);

      for (int i = 0; i < size; i++)
      {
        double tmp = this.buckets[i];
        int j = this.buckets.length - i;
        this.buckets[i] = this.buckets[j];
        this.buckets[j] = tmp;
      }

      o.atomicity = (1.0D - this.atomicity);
    }

    return o;
  }

  public void setAtomicity(double atomicity)
  {
    if ((atomicity < 0.0D) || (atomicity > 1.0D)) {
      throw new IllegalArgumentException("Atomicity, x, must be: 0 <= x <= 1");
    }
    if (atomicity == this.atomicity) {
      return;
    }
    Double old = new Double(this.atomicity);

    synchronized (this)
    {
      this.atomicity = atomicity;
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("atomicity", old, new Double(atomicity));
  }

  private void setDependants()
  {
    synchronized (this)
    {
      if (this.recalculate)
      {
        this.rs2 = 2.0D;
        this.positive = 0.0D;
        this.negative = 0.0D;

        int i = 0; for (int size = this.buckets.length; i < size; i++)
        {
          this.rs2 += this.buckets[i];
          this.positive += i * this.buckets[i] / size - 1;
          this.negative += size - 1 - i * this.buckets[i] / size - 1;
        }

        this.expectation = ((this.positive + this.atomicity * 2.0D) / this.rs2);
        this.recalculate = false;
      }
    }
  }

  public void setValue(double[] values) throws IndexOutOfBoundsException
  {
    if (values.length != this.buckets.length) {
      throw new IndexOutOfBoundsException("Index is out of range");
    }
    for (int i = 0; i < values.length; i++) {
      if (values[i] < 0.0D)
        throw new IllegalArgumentException("Value, x, must be: 0 <= x");
      if (Double.isNaN(values[i]))
        throw new IllegalArgumentException("Value must not be a NaN");
    }
    boolean changed = false;

    synchronized (this)
    {
      for (int i = 0; i < values.length; i++)
      {
        if (this.buckets[i] != values[i])
        {
          this.buckets[i] = Math.min(200000000000.0D, values[i]);
          changed = true;
        }
      }

      if (changed) {
        this.recalculate = true;
      }
    }
    if (changed)
      this.changeSupport.firePropertyChange("value", this, this);
  }

  public void setValue(int index, double value) throws IndexOutOfBoundsException
  {
    if ((index < 0) || (index > this.buckets.length)) {
      throw new IndexOutOfBoundsException("Index is out of range");
    }
    if (Double.isNaN(value)) {
      throw new IllegalArgumentException("Value must not be a NaN");
    }
    if (value < 0.0D) {
      throw new IllegalArgumentException("Value, x, must be: 0 <= x");
    }
    if (this.buckets[index] == value) {
      return;
    }
    synchronized (this)
    {
      this.buckets[index] = value;
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("value", this, this);
  }

  public synchronized int size()
  {
    return this.buckets.length;
  }

  public DiscreteBayesian toDiscreteBayesian(int size)
  {
    if (size < 2) {
      throw new IllegalArgumentException("Conversion not possible");
    }
    synchronized (this)
    {
      if (this.buckets.length == size) {
        return new DiscreteBayesian(this);
      }
      double[] results = convert(this.buckets, size);
      return new DiscreteBayesian(results, this.atomicity);
    }
  }

  public synchronized PureBayesian toPureBayesian()
  {
    PureBayesian bayesian = new PureBayesian();

    setDependants();

    bayesian.setPositive(this.positive);
    bayesian.setNegative(this.negative);
    bayesian.setAtomicity(this.atomicity);

    return bayesian;
  }

  public String toString()
  {
    synchronized (this)
    {
      setDependants();

      StringBuilder sb = new StringBuilder();

      int i = 0; for (int size = this.buckets.length; i < size; i++)
      {
        Object[] args = { Double.valueOf(this.buckets[i]) };

        if (sb.length() > 0) {
          sb.append(", ");
        }
        sb.append(String.format(FORMAT, args));
      }

      Object[] args = { sb.toString(), Double.valueOf(this.atomicity), Double.valueOf(this.expectation) };
      return String.format(EXTRAS_FORMAT, args);
    }
  }

  public SubjectiveOpinion toSubjectiveOpinion()
  {
    SubjectiveOpinion opinion = new SubjectiveOpinion();

    synchronized (this)
    {
      setDependants();
      double uncertainty = 2.0D / this.rs2;
      opinion.setBelief(this.positive / this.rs2, uncertainty);
      opinion.setAtomicity(this.atomicity);
    }

    return opinion;
  }

  public double[] values()
  {
    double[] values = new double[this.buckets.length];

    synchronized (this)
    {
      int i = 0; for (int size = this.buckets.length; i < size; i++) {
        values[i] = this.buckets[i];
      }
    }
    return values;
  }
}