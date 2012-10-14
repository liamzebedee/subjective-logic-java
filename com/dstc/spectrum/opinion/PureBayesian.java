package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PureBayesian extends OpinionBase
  implements Bayesian<PureBayesian>
{
  private static String TO_STRING_FORMAT = "(r=%1$1.3f, s=%2$1.3f, a=%3$1.3f, e=%4$1.3f)";

  private double atomicity = 0.5D;

  private double expectation = 0.0D;

  private double negative = 0.0D;

  private double positive = 0.0D;

  private boolean recalculate = false;

  private double rs2 = 2.0D;

  public static final PureBayesian add(Collection<? extends Opinion> opinions)
  {
    return SubjectiveOpinion.add(opinions).toPureBayesian();
  }

  public static final PureBayesian and(Collection<? extends Opinion> opinions)
  {
    return SubjectiveOpinion.and(opinions).toPureBayesian();
  }

  private static final PureBayesian consensus(PureBayesian x, PureBayesian y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    PureBayesian o = new PureBayesian();

    x.positive += y.positive;
    x.negative += y.negative;

    o.recalculate = true;

    return o;
  }

  private static final PureBayesian discount(Opinion discounter, PureBayesian opinion)
  {
    if ((discounter == null) || (opinion == null)) {
      throw new NullPointerException();
    }
    PureBayesian y = new PureBayesian(opinion);
    PureBayesian o = new PureBayesian();
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
    y.positive *= scale;
    y.negative *= scale;
    o.recalculate = true;

    return o;
  }

  private static final double discountScale(PureBayesian discounter, PureBayesian opinion)
  {
    double r = discounter.getPositive();
    double s = discounter.getNegative();
    double w = discounter.getWeight();

    double divisor = (opinion.getWeight() + 2.0D) * (s + 2.0D) + 2.0D * r;
    return divisor == 0.0D ? 0.0D : 2.0D * r / divisor;
  }

  private static final double discountScale(SubjectiveOpinion discounter, PureBayesian opinion)
  {
    double b = discounter.getBelief();
    double d = discounter.getDisbelief();
    double u = discounter.getUncertainty();

    double divisor = (d + u) * (opinion.getWeight() + 2.0D) + 2.0D * b;
    return divisor == 0.0D ? 0.0D : 2.0D * b / divisor;
  }

  private static final PureBayesian erosion(PureBayesian x, double factor)
  {
    if (x == null) {
      throw new NullPointerException();
    }
    if ((factor < 0.0D) || (factor > 1.0D)) {
      throw new IllegalArgumentException("Erosion Factor, f must be: 0 <= f <= 1");
    }
    synchronized (x)
    {
      PureBayesian o = new PureBayesian();

      double f = 1.0D - factor;

      x.positive *= f;
      x.negative *= f;
      o.atomicity = x.atomicity;

      o.recalculate = true;

      return o;
    }
  }

  public static final PureBayesian fuse(Collection<? extends Opinion> opinions)
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    PureBayesian x = null;

    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    for (Opinion opinion : opinions) {
      if (opinion != null)
        x = x == null ? new PureBayesian(opinion) : x.fuse(opinion.toPureBayesian());
    }
    return x;
  }

  public static final List<PureBayesian> normalize(Collection<? extends PureBayesian> opinions)
  {
    if (opinions == null) {
      throw new NullPointerException("Opinions must not be null");
    }
    List newOpinions = new ArrayList();

    double sum = 0.0D;

    for (PureBayesian o : opinions) {
      sum += o.getExpectation();
    }
    for (PureBayesian o : opinions)
    {
      if (sum == 0.0D)
        newOpinions.add(new PureBayesian(0.0D, 0.0D));
      else {
        newOpinions.add(new PureBayesian(o.adjustExpectation(o.getExpectation() / sum)));
      }
    }
    return newOpinions;
  }

  public static final PureBayesian or(Collection<? extends Opinion> opinions)
  {
    return SubjectiveOpinion.or(opinions).toPureBayesian();
  }

  public PureBayesian()
  {
  }

  public PureBayesian(double r, double s)
  {
    setPositive(r);
    setNegative(s);
  }

  public PureBayesian(double r, double s, double atomicity)
  {
    this(r, s);
    setAtomicity(atomicity);
  }

  public PureBayesian(Opinion o)
  {
    if (o == null)
      throw new NullPointerException("Opinion must not be null");
    PureBayesian x;
    PureBayesian x;
    if ((o instanceof SubjectiveOpinion))
      x = ((SubjectiveOpinion)o).toPureBayesian();
    else if ((o instanceof DiscreteBayesian))
      x = ((DiscreteBayesian)o).toPureBayesian();
    else {
      throw new ClassCastException("Opinion is not an instance of Bayesian, Subjective Opinion");
    }
    this.positive = x.positive;
    this.negative = x.negative;
    this.atomicity = x.atomicity;
    this.rs2 = x.rs2;
    this.expectation = x.expectation;
    this.recalculate = x.recalculate;
  }

  public PureBayesian(PureBayesian o)
  {
    if (o == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    synchronized (o)
    {
      this.positive = o.positive;
      this.negative = o.negative;
      this.atomicity = o.atomicity;
      this.rs2 = o.rs2;
      this.expectation = o.expectation;
      this.recalculate = o.recalculate;
    }
  }

  public PureBayesian abduce(Conditionals conditionals, double baseRateX)
  {
    return toSubjectiveOpinion().abduce(conditionals, baseRateX).toPureBayesian();
  }

  public PureBayesian abduce(Opinion xTy, Opinion xFy, double baseRateX)
  {
    return toSubjectiveOpinion().abduce(xTy, xFy, baseRateX).toPureBayesian();
  }

  public PureBayesian add(Opinion y)
  {
    return toSubjectiveOpinion().add(y).toPureBayesian();
  }

  public PureBayesian adjustExpectation(double value)
  {
    return toSubjectiveOpinion().adjustExpectation(value).toPureBayesian();
  }

  public PureBayesian and(Opinion y)
  {
    return toSubjectiveOpinion().and(y).toPureBayesian();
  }

  public final PureBayesian decay(double halfLife, double time)
  {
    return erosion(this, OpinionBase.erosionFactorFromHalfLife(halfLife, time));
  }

  public PureBayesian deduce(Conditionals conditionals)
  {
    return toSubjectiveOpinion().deduce(conditionals).toPureBayesian();
  }

  public PureBayesian deduce(Opinion yTx, Opinion yFx)
  {
    return toSubjectiveOpinion().deduce(yTx, yFx).toPureBayesian();
  }

  public PureBayesian discount(Opinion y)
  {
    return toSubjectiveOpinion().discount(y).toPureBayesian();
  }

  public PureBayesian discountBy(Opinion y)
  {
    return discount(y, this);
  }

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if ((obj instanceof PureBayesian))
    {
      PureBayesian o = (PureBayesian)obj;
      synchronized (this)
      {
        return (o.positive == this.positive) && (o.negative == this.negative) && (o.atomicity == this.atomicity);
      }
    }

    return false;
  }

  public final PureBayesian erode(double factor)
  {
    return erosion(this, factor);
  }

  public final PureBayesian fuse(Opinion opinion)
  {
    return consensus(new PureBayesian(this), new PureBayesian(opinion));
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

  public double getNegative()
  {
    return this.negative;
  }

  public double getPositive()
  {
    return this.positive;
  }

  public synchronized double getWeight()
  {
    return this.positive + this.negative;
  }

  public PureBayesian increasedUncertainty()
  {
    return toSubjectiveOpinion().increasedUncertainty().toPureBayesian();
  }

  public double max()
  {
    return Math.max(this.negative, this.positive);
  }

  public PureBayesian maximizedUncertainty()
  {
    return toSubjectiveOpinion().uncertainOpinion().toPureBayesian();
  }

  public double min()
  {
    return Math.min(this.negative, this.positive);
  }

  public final PureBayesian not()
  {
    PureBayesian o = new PureBayesian();

    o.positive = this.negative;
    o.negative = this.positive;
    o.atomicity = (1.0D - this.atomicity);

    return o;
  }

  public PureBayesian or(Opinion y)
  {
    return toSubjectiveOpinion().or(y).toPureBayesian();
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
        this.positive = OpinionBase.adjust(this.positive);
        this.negative = OpinionBase.adjust(this.negative);
        this.rs2 = (this.positive + this.negative + 2.0D);
        this.expectation = ((this.positive + this.atomicity * 2.0D) / this.rs2);
        this.recalculate = false;
      }
    }
  }

  public void setNegative(double s)
  {
    if (s == this.negative) {
      return;
    }
    if (s < 0.0D) {
      throw new IllegalArgumentException("Value x, must be: 0 <= x");
    }
    Double old = new Double(s);

    synchronized (this)
    {
      this.negative = Math.min(200000000000.0D, s);
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("negative", old, new Double(s));
  }

  public void setPositive(double r)
  {
    if (r == this.positive) {
      return;
    }
    if (r < 0.0D) {
      throw new IllegalArgumentException("Value x, must be: 0 <= x");
    }
    Double old = new Double(r);

    synchronized (this)
    {
      this.positive = Math.min(200000000000.0D, r);
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("positive", old, new Double(r));
  }

  public int size()
  {
    return 2;
  }

  public PureBayesian subtract(Opinion y)
  {
    return toSubjectiveOpinion().subtract(y).toPureBayesian();
  }

  public DiscreteBayesian toDiscreteBayesian()
  {
    return toDiscreteBayesian(2);
  }

  public DiscreteBayesian toDiscreteBayesian(int size)
  {
    synchronized (this)
    {
      if (size < 2) {
        throw new IllegalArgumentException("Conversion not possible");
      }
      DiscreteBayesian bo = new DiscreteBayesian(new double[] { this.negative, this.positive });

      if (size == 2) {
        return bo;
      }
      return bo.toDiscreteBayesian(size);
    }
  }

  public PureBayesian toPureBayesian()
  {
    return this;
  }

  public String toString()
  {
    synchronized (this)
    {
      setDependants();
      Object[] args = { Double.valueOf(this.positive), Double.valueOf(this.negative), Double.valueOf(this.atomicity), 
        Double.valueOf(getExpectation()) };
      return String.format(TO_STRING_FORMAT, args);
    }
  }

  public SubjectiveOpinion toSubjectiveOpinion()
  {
    SubjectiveOpinion opinion = new SubjectiveOpinion();

    synchronized (this)
    {
      setDependants();
      opinion.setBelief(this.positive / this.rs2, 2.0D / this.rs2);
      opinion.setAtomicity(this.atomicity);
    }

    return opinion;
  }

  public PureBayesian unAnd(Opinion y)
  {
    return toSubjectiveOpinion().unAnd(y).toPureBayesian();
  }

  public PureBayesian unOr(Opinion y)
  {
    return toSubjectiveOpinion().unOr(y).toPureBayesian();
  }

  public double[] values()
  {
    return new double[] { this.negative, this.positive };
  }

  public static final PureBayesian average(Collection<? extends Opinion> opinions)
  {
    return SubjectiveOpinion.smoothAverage(opinions).toPureBayesian();
  }

  public final PureBayesian average(Opinion opinion)
  {
    Collection opinions = new ArrayList();

    opinions.add(new SubjectiveOpinion(this));
    opinions.add(new SubjectiveOpinion(opinion));

    return SubjectiveOpinion.smoothAverage(opinions).toPureBayesian();
  }
}