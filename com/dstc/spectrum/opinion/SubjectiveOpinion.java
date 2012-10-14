package com.dstc.spectrum.opinion;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

public class SubjectiveOpinion extends OpinionBase
{
  private static final String TO_STRING_FORMAT = "(b=%1$1.3f, d=%2$1.3f, u=%3$1.3f, a=%4$1.3f, e=%5$1.3f)";
  public static final SubjectiveOpinion UNCERTAIN = new SubjectiveOpinion(0.0D, 0.0D, 1.0D, 0.5D);

  private double a = 0.5D;

  private double b = 0.0D;

  private double d = 0.0D;

  private double e = 0.5D;

  private OpinionOperator lastOp = null;

  private boolean recalculate = false;

  private double relativeWeight = 1.0D;

  private double u = 1.0D;

  private static final SubjectiveOpinion abduction(SubjectiveOpinion y, SubjectiveOpinion yTx, SubjectiveOpinion yFx, double baseRateX)
    throws OpinionArithmeticException
  {
    if ((baseRateX < 0.0D) || (baseRateX > 1.0D)) {
      throw new IllegalArgumentException("Base Rate x, must be: 0 <= x <= 1");
    }
    if ((y == null) || (yTx == null) || (yFx == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = null;

    if (y.getAtomicity() == 0.0D)
    {
      o = createVacuousOpinion(baseRateX);
    }
    else
    {
      Conditionals conditionals = reverseConditionals(yTx, yFx, baseRateX);
      o = deduction(y, conditionals.getPositive().toSubjectiveOpinion(), conditionals.getNegative().toSubjectiveOpinion());
    }

    o.lastOp = OpinionOperator.Abduce;

    return o;
  }

  public static final SubjectiveOpinion add(Collection<? extends Opinion> opinions) throws OpinionArithmeticException
  {
    return sum(opinions);
  }

  private static final void adjustExpectation(SubjectiveOpinion x, double expectation)
  {
    synchronized (x)
    {
      x.setDependants();

      double new_e = OpinionBase.constrain(OpinionBase.adjust(expectation));

      if (Math.abs(new_e - x.e) <= 1.0E-010D) {
        return;
      }
      if ((new_e == 0.0D) || (new_e == 1.0D))
      {
        x.setBelief(new_e, true);
      }
      else if (new_e < x.e)
      {
        double new_d = OpinionBase.adjust(((1.0D - new_e) * (x.b + x.u) - (1.0D - x.a) * x.u) / x.e);
        double new_u = OpinionBase.adjust(new_e * x.u / x.e);

        if (new_d + new_u > 1.0D) {
          new_u = 1.0D - new_d;
        }
        x.setDisbelief(new_d, new_u);
      }
      else
      {
        double divisor = x.d + x.u - x.a * x.u;
        double new_b = OpinionBase.adjust((new_e * (x.d + x.u) - x.a * x.u) / divisor);
        double new_u = OpinionBase.adjust((1.0D - new_e) * x.u / divisor);

        if (x.b + new_u > 1.0D) {
          new_u = 1.0D - new_b;
        }
        x.setBelief(new_b, new_u);
      }
    }
  }

  public static final SubjectiveOpinion and(Collection<? extends Opinion> opinions) throws OpinionArithmeticException
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    SubjectiveOpinion x = null;

    for (Opinion opinion : opinions) {
      if (opinion != null)
        x = x == null ? new SubjectiveOpinion(opinion) : x.and(opinion);
    }
    return x;
  }

  public static final SubjectiveOpinion average(Collection<? extends Opinion> opinions)
  {
    return smoothAverage(opinions);
  }

  private static final SubjectiveOpinion coDivision(SubjectiveOpinion x, SubjectiveOpinion y, double r)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    if ((r < 0.0D) || (r > 1.0D)) {
      throw new IllegalArgumentException("Limiting value, r, must be: 0<= r <=1");
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();
    try
    {
      o.b = ((x.b - y.b) / (1.0D - y.b));
      o.a = ((x.a - y.a) / (1.0D - y.a));

      if (x.a > y.a)
      {
        o.u = (((1.0D - x.b) / (1.0D - y.b) - (x.d + (1.0D - x.a) * x.u) / (y.d + (1.0D - y.a) * y.u)) * (1.0D - y.a) / (x.a - y.a));
        o.d = 
          (((1.0D - y.a) * (x.d + (1.0D - x.a) * x.u) / (y.d + (1.0D - y.a) * y.u) - (1.0D - x.a) * (1.0D - x.b) / (1.0D - y.b)) / (
          x.a - y.a));
      }
      else
      {
        o.d = (r * (1.0D - x.b) / (1.0D - y.b));
        o.u = ((1.0D - r) * (1.0D - x.b) / (1.0D - y.b));
      }

      o.checkConsistency();
      o.recalculate = true;

      o.lastOp = OpinionOperator.UnOr;

      return o;
    }
    catch (OpinionArithmeticException e)
    {
      return null;
    }
    catch (ArithmeticException ae) {
    }
    return null;
  }

  private static final SubjectiveOpinion complement(SubjectiveOpinion x)
  {
    if (x == null) {
      throw new NullPointerException();
    }
    synchronized (x)
    {
      SubjectiveOpinion o = new SubjectiveOpinion();

      o.b = x.d;
      o.d = x.b;
      o.u = x.u;
      o.a = (1.0D - x.a);

      o.checkConsistency(true);

      o.lastOp = OpinionOperator.Not;

      return o;
    }
  }

  private static final SubjectiveOpinion coMultiplication(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    double r = x.getRelativeWeight(y, OpinionOperator.Or);

    x.setDependants();
    y.setDependants();

    o.b = (x.b + y.b - x.b * y.b);
    o.a = (x.a + y.a - x.a * y.a);

    if (o.a != 0.0D)
    {
      o.u = (x.u * y.u + (y.a * x.d * y.u + x.a * x.u * y.d) / o.a);
    }
    else
    {
      o.u = (x.u * y.u + (x.d * y.u + r * x.u * y.d) / (r + 1.0D));
    }

    o.d = (1.0D - o.b - o.u);

    o.checkConsistency();
    o.recalculate = true;

    o.lastOp = OpinionOperator.Or;
    x.relativeWeight += y.relativeWeight;

    return o;
  }

  private static final SubjectiveOpinion consensus(SubjectiveOpinion x, SubjectiveOpinion y) throws OpinionArithmeticException
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    double rd = x.getRelativeWeight(y, OpinionOperator.Fuse);
    double k = x.u + y.u - x.u * y.u;
    double l = x.u + y.u - 2.0D * x.u * y.u;

    if (k != 0.0D)
    {
      if (l != 0.0D)
      {
        o.b = ((x.b * y.u + y.b * x.u) / k);
        o.d = ((x.d * y.u + y.d * x.u) / k);
        o.u = (x.u * y.u / k);
        o.a = ((y.a * x.u + x.a * y.u - (x.a + y.a) * x.u * y.u) / l);
      }
      else if (Math.abs(x.a - y.a) <= 1.0E-010D)
      {
        o.b = 0.0D;
        o.d = 0.0D;
        o.u = 1.0D;
        o.a = x.a;
      }
      else
      {
        throw new OpinionArithmeticException("Relative atomicities are not equal");
      }

    }
    else
    {
      o.b = ((rd * x.b + y.b) / (rd + 1.0D));
      o.d = ((rd * x.d + y.d) / (rd + 1.0D));
      o.u = 0.0D;
      o.a = ((rd * x.a + y.a) / (rd + 1.0D));
    }

    o.checkConsistency(true);

    o.lastOp = OpinionOperator.Fuse;
    x.relativeWeight += y.relativeWeight;

    return o;
  }

  public static final SubjectiveOpinion createDogmaticOpinion(double expectation, double atomcity)
  {
    if ((expectation < 0.0D) || (expectation > 1.0D)) {
      throw new IllegalArgumentException("Expectation e, must be 0 <= e <= 1");
    }
    return new SubjectiveOpinion(expectation, 1.0D - expectation, 0.0D, atomcity);
  }

  public static final SubjectiveOpinion createVacuousOpinion(double expectation)
  {
    if ((expectation < 0.0D) || (expectation > 1.0D)) {
      throw new IllegalArgumentException("Expectation e, must be 0 <= e <= 1");
    }
    return new SubjectiveOpinion(0.0D, 0.0D, 1.0D, expectation);
  }

  private static final SubjectiveOpinion deduction(SubjectiveOpinion x, SubjectiveOpinion yTx, SubjectiveOpinion yFx)
    throws OpinionArithmeticException
  {
    if ((x == null) || (yTx == null) || (yFx == null)) {
      throw new NullPointerException();
    }
    if (Math.abs(yTx.a - yFx.a) > 1.0E-010D) {
      throw new OpinionArithmeticException("The atomicities of both sub-conditionals must be equal");
    }
    x.setDependants();

    SubjectiveOpinion I = new SubjectiveOpinion();

    I.a = yTx.a;
    I.b = (x.b * yTx.b + x.d * yFx.b + x.u * (yTx.b * x.a + yFx.b * (1.0D - x.a)));
    I.d = (x.b * yTx.d + x.d * yFx.d + x.u * (yTx.d * x.a + yFx.d * (1.0D - x.a)));
    I.u = (x.b * yTx.u + x.d * yFx.u + x.u * (yTx.u * x.a + yFx.u * (1.0D - x.a)));

    I.setDependants(true);
    SubjectiveOpinion y;
    SubjectiveOpinion y;
    if (((yTx.b >= yFx.b) && (yTx.d >= yFx.d)) || ((yTx.b <= yFx.b) && (yTx.d <= yFx.d)))
    {
      y = I;
    }
    else
    {
      double expec = yTx.b * x.a + yFx.b * (1.0D - x.a) + yTx.a * (yTx.u * x.a + yFx.u * (1.0D - x.a));

      boolean case_II = (yTx.b > yFx.b) && (yTx.d < yFx.d);

      boolean case_1 = x.e <= x.a;
      double k;
      double k;
      if (case_II)
      {
        boolean case_A = expec <= yFx.b + yTx.a * (1.0D - yFx.b - yTx.d);
        double k;
        if (case_A)
        {
          double k;
          if (case_1)
          {
            double divisor;
            double k;
            if ((divisor = x.getExpectation() * yTx.a) > 0.0D)
              k = x.a * x.u * (I.b - yFx.b) / divisor;
            else
              k = I.b - yFx.b;
          }
          else
          {
            double divisor;
            double k;
            if ((divisor = (x.d + (1.0D - x.a) * x.u) * yTx.a * (yFx.d - yTx.d)) > 0.0D)
              k = x.a * x.u * (I.d - yTx.d) * (yTx.b - yFx.b) / divisor;
            else
              k = (I.d - yTx.d) * (yTx.b - yFx.b);
          }
        }
        else
        {
          double k;
          if (case_1)
          {
            double divisor;
            double k;
            if ((divisor = x.getExpectation() * (1.0D - yTx.a) * (yTx.b - yFx.b)) > 0.0D)
              k = (1.0D - x.a) * x.u * (I.b - yFx.b) * (yFx.d - yTx.d) / divisor;
            else
              k = (I.b - yFx.b) * (yFx.d - yTx.d);
          }
          else
          {
            double divisor;
            double k;
            if ((divisor = (x.d + (1.0D - x.a) * x.u) * (1.0D - yTx.a)) > 0.0D)
              k = (1.0D - x.a) * x.u * (I.d - yTx.d) / divisor;
            else {
              k = I.d - yTx.d;
            }
          }
        }
      }
      else
      {
        boolean case_A = expec <= yTx.b + yTx.a * (1.0D - yTx.b - yFx.d);
        double k;
        if (case_A)
        {
          double k;
          if (case_1)
          {
            double divisor;
            double k;
            if ((divisor = x.getExpectation() * yTx.a * (yTx.d - yFx.d)) > 0.0D)
              k = (1.0D - x.a) * x.u * (I.d - yFx.d) * (yFx.b - yTx.b) / divisor;
            else
              k = (I.d - yFx.d) * (yFx.b - yTx.b);
          }
          else
          {
            double divisor;
            double k;
            if ((divisor = (x.d + (1.0D - x.a) * x.u) * yTx.a) > 0.0D)
              k = (1.0D - x.a) * x.u * (I.b - yTx.b) / divisor;
            else
              k = I.b - yTx.b;
          }
        }
        else
        {
          double k;
          if (case_1)
          {
            double divisor;
            double k;
            if ((divisor = x.getExpectation() * (1.0D - yTx.a)) > 0.0D)
              k = x.a * x.u * (I.d - yFx.d) / divisor;
            else
              k = I.d - yFx.d;
          }
          else
          {
            double divisor;
            double k;
            if ((divisor = (x.d + (1.0D - x.a) * x.u) * (1.0D - yTx.a) * (yFx.b - yTx.b)) > 0.0D)
              k = x.a * x.u * (I.b - yTx.b) * (yTx.d - yFx.d) / divisor;
            else {
              k = (I.b - yTx.b) * (yTx.d - yFx.d);
            }
          }
        }
      }
      y = new SubjectiveOpinion();
      y.a = yTx.a;

      y.b = OpinionBase.adjust(I.b - k * y.a);
      y.d = OpinionBase.adjust(I.d - k * (1.0D - y.a));
      y.u = OpinionBase.adjust(I.u + k);

      y.checkConsistency(true);
    }

    y.lastOp = OpinionOperator.Deduce;

    return y;
  }

  private static final SubjectiveOpinion clippedOpinion(double b, double u, double a) throws OpinionArithmeticException
  {
    if ((a < 0.0D) || (a > 1.0D)) {
      throw new OpinionArithmeticException("Atomicity out of range, a: 0 <= a <= 1");
    }
    SubjectiveOpinion o = new SubjectiveOpinion(a);
    double e = OpinionBase.constrain(b + a * u);
    double sum = u + b;

    if (u < 0.0D)
    {
      o.u = 0.0D;
      o.b = e;
      o.d = (1.0D - o.b);
    }
    else if (b < 0.0D)
    {
      o.b = 0.0D;
      o.u = (e / o.a);
      o.d = (1.0D - o.u);
    }
    else if (sum > 1.0D)
    {
      if (a == 1.0D)
      {
        o.d = 0.0D;
        o.b = (b / sum);
        o.u = (u / sum);
      }
      else
      {
        o.d = 0.0D;
        o.b = (a < 1.0D ? (e - a) / (1.0D - a) : e);
        o.u = (1.0D - o.b);
      }
    }
    else
    {
      o.b = b;
      o.u = u;
      o.d = (1.0D - b - u);
    }

    o.adjust();

    o.checkConsistency();
    o.recalculate = true;

    return o;
  }

  private static final SubjectiveOpinion division(SubjectiveOpinion x, SubjectiveOpinion y) throws OpinionArithmeticException
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    if (y.a == 0.0D) {
      throw new OpinionArithmeticException("Atomicity of divisor is zero");
    }

    x.setDependants();
    y.setDependants();

    if (y.e - x.e < -1.0E-010D) {
      throw new OpinionArithmeticException("Expectation of divisor cannot be less than of numerator");
    }
    try
    {
      double a = x.a / y.a;
      SubjectiveOpinion o;
      SubjectiveOpinion o;
      if (x.e == 0.0D)
      {
        o = new SubjectiveOpinion(0.0D, 1.0D, 0.0D, a);
      }
      else
      {
        SubjectiveOpinion o;
        if (a == 1.0D)
        {
          o = new SubjectiveOpinion(1.0D, 0.0D, 0.0D, a);
        }
        else
        {
          double e = x.e / y.e;

          double d = OpinionBase.constrain((x.d - y.d) / (1.0D - y.d));
          double u = (1.0D - d - e) / (1.0D - a);
          double b = 1.0D - d - u;

          o = clippedOpinion(b, u, a);
        }
      }
      o.checkConsistency();
      o.recalculate = true;

      o.lastOp = OpinionOperator.UnAnd;

      return o;
    }
    catch (ArithmeticException ae)
    {
      throw new OpinionArithmeticException(ae.getMessage());
    }
  }

  private static final SubjectiveOpinion erosion(SubjectiveOpinion x, double factor)
  {
    if (x == null) {
      throw new NullPointerException();
    }
    if ((factor < 0.0D) || (factor > 1.0D)) {
      throw new IllegalArgumentException("Erosion Factor, f must be: 0 <= f <= 1");
    }
    synchronized (x)
    {
      SubjectiveOpinion o = new SubjectiveOpinion();

      double f = 1.0D - factor;

      o.b = OpinionBase.constrain(OpinionBase.adjust(x.b * f));
      o.d = OpinionBase.constrain(OpinionBase.adjust(x.d * f));
      o.u = OpinionBase.constrain(OpinionBase.adjust(1.0D - o.b - o.d));
      o.a = x.a;

      o.checkConsistency(true);

      return o;
    }
  }

  public static final SubjectiveOpinion fuse(Collection<? extends Opinion> opinions) throws OpinionArithmeticException
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    SubjectiveOpinion x = null;

    for (Opinion opinion : opinions) {
      if (opinion != null)
        x = x == null ? new SubjectiveOpinion(opinion) : x.fuse(opinion);
    }
    return x;
  }

  public static final Conditionals reverseConditionals(Conditionals conditionals, double baseRateX)
    throws OpinionArithmeticException
  {
    if (conditionals == null) {
      throw new NullPointerException();
    }
    return reverseConditionals(conditionals.getPositive().toSubjectiveOpinion(), 
      conditionals.getNegative().toSubjectiveOpinion(), baseRateX);
  }

  public static final Conditionals reverseConditionals(SubjectiveOpinion yTx, SubjectiveOpinion yFx, double baseRateX)
    throws OpinionArithmeticException
  {
    if ((baseRateX < 0.0D) || (baseRateX > 1.0D)) {
      throw new IllegalArgumentException("Base Rate x, must be: 0 <= x <= 1");
    }
    if ((yTx == null) || (yFx == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion x_br = createVacuousOpinion(baseRateX);

    double atom_y = yTx.getAtomicity();
    SubjectiveOpinion xFy;
    SubjectiveOpinion xTy;
    SubjectiveOpinion xFy;
    if (baseRateX == 0.0D)
    {
      SubjectiveOpinion xTy = createDogmaticOpinion(0.0D, 0.0D);
      xFy = createDogmaticOpinion(0.0D, 0.0D);
    }
    else
    {
      SubjectiveOpinion xFy;
      if (baseRateX == 1.0D)
      {
        SubjectiveOpinion xTy = createDogmaticOpinion(1.0D, 1.0D);
        xFy = createDogmaticOpinion(1.0D, 1.0D);
      }
      else
      {
        SubjectiveOpinion xFy;
        if ((atom_y == 0.0D) || (atom_y == 1.0D))
        {
          SubjectiveOpinion xTy = new SubjectiveOpinion(0.0D, 0.0D, 1.0D, baseRateX);
          xFy = new SubjectiveOpinion(0.0D, 0.0D, 1.0D, baseRateX);
        }
        else
        {
          SubjectiveOpinion not_yTx = complement(yTx);
          SubjectiveOpinion y_br = deduction(x_br, yTx, yFx);
          SubjectiveOpinion not_y_br = complement(y_br);
          SubjectiveOpinion y_and_x = multiply(x_br, yTx);
          SubjectiveOpinion not_y_and_x = multiply(x_br, not_yTx);

          xTy = division(y_and_x, y_br);
          xFy = division(not_y_and_x, not_y_br);
        }
      }
    }
    return new Conditionals(xTy, xFy);
  }

  private static final void maximizeUncertainty(SubjectiveOpinion x)
  {
    if (x == null) {
      throw new NullPointerException();
    }
    synchronized (x)
    {
      x.setDependants();
      double u;
      double d;
      double a;
      double b;
      double u;
      if (x.e <= x.a)
      {
        double b = 0.0D;
        double a = x.a;
        double u;
        if (x.a > 0.0D)
        {
          double d = OpinionBase.adjust(1.0D - x.u - x.b / x.a);
          u = OpinionBase.adjust(x.u + x.b / x.a);
        }
        else
        {
          double d = 0.0D;
          u = OpinionBase.adjust(1.0D - b);
        }
      }
      else
      {
        d = 0.0D;
        a = x.a;
        double u;
        if (x.a < 1.0D)
        {
          double b = OpinionBase.adjust(1.0D - x.u - x.d / (1.0D - x.a));
          u = OpinionBase.adjust(x.u + x.d / (1.0D - x.a));
        }
        else
        {
          b = 0.0D;
          u = b;
        }
      }

      x.b = b;
      x.d = d;
      x.u = u;
      x.a = a;

      x.checkConsistency(true);
    }
  }

  private static final SubjectiveOpinion multiply(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();

    double divisor = 1.0D;
    double r = x.getRelativeWeight(y, OpinionOperator.Or);
    double expec = 0.0D;

    o.d = (x.d + y.d - x.d * y.d);
    x.a *= y.a;
    expec = x.e * (y.b + y.a * y.u);
    divisor = 1.0D - o.a;

    if (divisor != 0.0D)
    {
      o.b = (((o.d - 1.0D) * o.a + expec) / divisor);
      o.u = (-(o.d - 1.0D + expec) / divisor);
    }
    else
    {
      o.b = (x.b * y.b + (r * x.b * y.u + x.u * y.b) / (r + 1.0D));
      o.u = ((x.b * y.u + r * y.b * x.u) / (r + 1.0D) + x.u * y.u);
    }

    o.adjust();
    o.checkConsistency(true);

    o.lastOp = OpinionOperator.And;
    x.relativeWeight += y.relativeWeight;

    return o;
  }

  public static void normalize(Collection<? extends SubjectiveOpinion> opinions)
  {
    if (opinions == null) {
      throw new NullPointerException("Opinions must not be null");
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    double sum = 0.0D;

    for (SubjectiveOpinion o : opinions) {
      sum += o.getExpectation();
    }
    for (SubjectiveOpinion o : opinions)
    {
      if (sum == 0.0D)
        o.setDisbelief(1.0D);
      else
        o.set(new SubjectiveOpinion(o.adjustExpectation(o.getExpectation() / sum)));
    }
  }

  public static final SubjectiveOpinion or(Collection<? extends Opinion> opinions) throws OpinionArithmeticException
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    SubjectiveOpinion x = null;

    for (Opinion opinion : opinions) {
      if (opinion != null)
        x = x == null ? new SubjectiveOpinion(opinion) : x.or(opinion);
    }
    return x;
  }

  private static final SubjectiveOpinion simpleAnd(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();

    double divisor = 1.0D;
    x.b *= y.b;
    o.d = (x.d + y.d - x.d * y.d);
    o.u = (x.b * y.u + y.b * x.u + x.u * y.u);
    divisor = x.b * y.u + y.b * x.u + x.u * y.u;
    if (divisor != 0.0D)
    {
      o.a = ((y.a * x.b * y.u + x.a * y.b * x.u + x.a * y.a * x.u * y.u) / (x.b * y.u + y.b * x.u + x.u * y.u));
    }
    else if ((y.u == 0.0D) && (x.u == 0.0D) && (x.d != 1.0D) && (y.d != 1.0D))
    {
      o.a = ((y.a * x.b + x.relativeWeight * x.a * y.b) / (x.b + x.relativeWeight * y.b));
    }
    else if ((x.d == 1.0D) && (y.u != 0.0D))
    {
      o.a = 
        ((y.a * y.u + x.relativeWeight * x.a * y.b + x.relativeWeight * x.a * y.a * y.u) / (
        y.u + x.relativeWeight - x.relativeWeight * y.d));
    }
    else if ((y.d == 1.0D) && (x.u != 0.0D))
    {
      o.a = 
        ((x.relativeWeight * y.a * x.b + x.a * x.u + x.relativeWeight * x.a * y.a * x.u) / (
        x.relativeWeight + x.u - x.relativeWeight * x.d));
    }
    else if ((x.d == 1.0D) && (y.u == 0.0D))
    {
      o.a = ((x.relativeWeight * y.a + x.a * y.b) / (x.relativeWeight + y.b));
    }
    else if ((y.d == 1.0D) && (x.u == 0.0D))
    {
      o.a = ((y.a * x.b + x.relativeWeight * x.a) / (x.b + x.relativeWeight));
    }
    else if ((x.d == 1.0D) && (y.d == 1.0D))
    {
      o.a = 
        ((y.relativeWeight * y.a + x.relativeWeight * x.a + x.relativeWeight * y.relativeWeight * x.a * y.a) / (
        y.relativeWeight + x.relativeWeight + x.relativeWeight * y.relativeWeight));
    }
    else
    {
      o.a = 0.5D;
    }

    o.checkConsistency(true);
    o.lastOp = OpinionOperator.SimpleAnd;
    x.relativeWeight += y.relativeWeight;

    return o;
  }

  private static final SubjectiveOpinion simpleCoMultiplication(SubjectiveOpinion x, SubjectiveOpinion y, double r, double s)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();

    double divisor = 1.0D;
    o.b = (x.b + y.b - x.b * y.b);
    x.d *= y.d;
    o.u = (x.d * y.u + y.d * x.u + x.u * y.u);
    divisor = x.u + y.u - x.b * y.u - y.b * x.u - x.u * y.u;

    if (divisor != 0.0D)
    {
      o.a = 
        ((x.u * x.a + y.u * y.a - y.a * x.b * y.u - x.a * y.b * x.u - x.a * y.a * x.u * y.u) / (
        x.u + y.u - x.b * y.u - y.b * x.u - x.u * y.u));
    }
    else if ((y.u == 0.0D) && (x.u == 0.0D) && (x.d != 0.0D) && (y.d != 0.0D))
    {
      o.a = ((r * x.a * y.d + y.a * x.d) / (r * y.d + x.d));
    }
    else if ((x.b == 1.0D) && (y.u != 0.0D))
    {
      o.a = ((r * x.a * y.d + r * x.a * y.u + r * y.u * y.a + y.u * y.a - r * x.a * y.a * y.u) / (r + y.u - r * y.b));
    }
    else if ((y.b == 1.0D) && (x.u != 0.0D))
    {
      o.a = ((r * x.u * x.a + x.u * x.a + r * y.a * x.d + r * y.a * x.u - r * x.a * y.a * x.u) / (x.u + r - r * x.b));
    }
    else if ((x.b == 1.0D) && (y.u == 0.0D))
    {
      o.a = ((x.a * y.d + r * y.a) / (y.d + r));
    }
    else if ((y.b == 1.0D) && (x.u == 0.0D))
    {
      o.a = ((r * x.a + y.a * x.d) / (r + x.d));
    }
    else if ((x.b == 1.0D) && (y.b == 1.0D))
    {
      o.a = ((r * s * x.a + r * x.a + r * s * y.a + s * y.a - r * s * x.a * y.a) / (r + s + r * s));
    }
    else
    {
      o.a = 0.5D;
    }

    o.checkConsistency(true);

    return o;
  }

  private static final SubjectiveOpinion simpleMultiplication(SubjectiveOpinion x, SubjectiveOpinion y, double r, double s)
  {
    if (y == null) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();

    x.b *= y.b;
    o.d = (x.d + y.d - x.d * y.d);
    o.u = (x.b * y.u + y.b * x.u + x.u * y.u);

    double divisor = x.b * y.u + y.b * x.u + x.u * y.u;

    if (divisor != 0.0D)
    {
      o.a = ((y.a * x.b * y.u + x.a * y.b * x.u + x.a * y.a * x.u * y.u) / (x.b * y.u + y.b * x.u + x.u * y.u));
    }
    else if ((y.u == 0.0D) && (x.u == 0.0D) && (x.d != 1.0D) && (y.d != 1.0D))
    {
      o.a = ((y.a * x.b + r * x.a * y.b) / (x.b + r * y.b));
    }
    else if ((x.d == 1.0D) && (y.u != 0.0D))
    {
      o.a = ((y.a * y.u + r * x.a * y.b + r * x.a * y.a * y.u) / (y.u + r - r * y.d));
    }
    else if ((y.d == 1.0D) && (x.u != 0.0D))
    {
      o.a = ((r * y.a * x.b + x.a * x.u + r * x.a * y.a * x.u) / (r + x.u - r * x.d));
    }
    else if ((x.d == 1.0D) && (y.u == 0.0D))
    {
      o.a = ((r * y.a + x.a * y.b) / (r + y.b));
    }
    else if ((y.d == 1.0D) && (x.u == 0.0D))
    {
      o.a = ((y.a * x.b + r * x.a) / (x.b + r));
    }
    else if ((x.d == 1.0D) && (y.d == 1.0D))
    {
      o.a = ((s * y.a + r * x.a + r * s * x.a * y.a) / (s + r + r * s));
    }
    else
    {
      o.a = 0.5D;
    }

    o.checkConsistency(true);

    return o;
  }

  private static final SubjectiveOpinion simpleOr(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.setDependants();
    y.setDependants();

    double divisor = 1.0D;
    o.b = (x.b + y.b - x.b * y.b);
    x.d *= y.d;
    o.u = (x.d * y.u + y.d * x.u + x.u * y.u);
    divisor = x.u + y.u - x.b * y.u - y.b * x.u - x.u * y.u;
    if (divisor != 0.0D)
    {
      o.a = 
        ((x.u * x.a + y.u * y.a - y.a * x.b * y.u - x.a * y.b * x.u - x.a * y.a * x.u * y.u) / (
        x.u + y.u - x.b * y.u - y.b * x.u - x.u * y.u));
    }
    else if ((y.u == 0.0D) && (x.u == 0.0D) && (x.d != 0.0D) && (y.d != 0.0D))
    {
      o.a = ((x.relativeWeight * x.a * y.d + y.a * x.d) / (x.relativeWeight * y.d + x.d));
    }
    else if ((x.b == 1.0D) && (y.u != 0.0D))
    {
      o.a = 
        ((x.relativeWeight * x.a * y.d + x.relativeWeight * x.a * y.u + x.relativeWeight * y.u * y.a + y.u * y.a - x.relativeWeight * 
        x.a * y.a * y.u) / (
        x.relativeWeight + y.u - x.relativeWeight * y.b));
    }
    else if ((y.b == 1.0D) && (x.u != 0.0D))
    {
      o.a = 
        ((x.relativeWeight * x.u * x.a + x.u * x.a + x.relativeWeight * y.a * x.d + x.relativeWeight * y.a * x.u - x.relativeWeight * 
        x.a * y.a * x.u) / (
        x.u + x.relativeWeight - x.relativeWeight * x.b));
    }
    else if ((x.b == 1.0D) && (y.u == 0.0D))
    {
      o.a = ((x.a * y.d + x.relativeWeight * y.a) / (y.d + x.relativeWeight));
    }
    else if ((y.b == 1.0D) && (x.u == 0.0D))
    {
      o.a = ((x.relativeWeight * x.a + y.a * x.d) / (x.relativeWeight + x.d));
    }
    else if ((x.b == 1.0D) && (y.b == 1.0D))
    {
      o.a = 
        ((x.relativeWeight * y.relativeWeight * x.a + x.relativeWeight * x.a + x.relativeWeight * y.relativeWeight * y.a + 
        y.relativeWeight * y.a - x.relativeWeight * y.relativeWeight * x.a * y.a) / (
        x.relativeWeight + y.relativeWeight + x.relativeWeight * y.relativeWeight));
    }
    else
    {
      o.a = 0.5D;
    }

    o.checkConsistency(true);
    o.lastOp = OpinionOperator.SimpleOr;
    x.relativeWeight += y.relativeWeight;

    return o;
  }

  protected static final SubjectiveOpinion smoothAverage(Collection<? extends Opinion> opinions)
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new IllegalArgumentException("Opinions must not be empty");
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    int count = 0;

    double b = 0.0D; double a = 0.0D; double e = 0.0D;

    for (Opinion opinion : opinions) {
      if (opinion != null)
      {
        SubjectiveOpinion x = new SubjectiveOpinion(opinion);

        count++;
        b += x.b;
        a += x.a;
        e += x.b + x.a * x.u;
      }
    }
    if (count == 0) {
      throw new IllegalArgumentException("Opinions must not be empty");
    }
    o.b = (b / count);
    o.a = (a / count);
    o.u = ((e / count - o.b) / o.a);
    o.d = (1.0D - o.b - o.u);

    o.adjust();
    o.checkConsistency(true);

    return o;
  }

  private static final SubjectiveOpinion subtraction(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    if (x.a - y.a < 0.0D) {
      throw new OpinionArithmeticException("Illegal operation, Difference of atomicities is less than 0.0");
    }
    double b = x.b - y.b;
    double a = OpinionBase.constrain(x.a - y.a);
    double u = x.a * x.u - y.a * y.u;

    if (a != 0.0D) {
      u /= a;
    }
    SubjectiveOpinion o = clippedOpinion(b, u, a);

    o.lastOp = OpinionOperator.Subtract;

    return o;
  }

  private static final SubjectiveOpinion sum(Collection<? extends Opinion> opinions) throws OpinionArithmeticException
  {
    if (opinions == null) {
      throw new NullPointerException();
    }
    if (opinions.isEmpty()) {
      throw new OpinionArithmeticException("Opinions must not be empty");
    }
    double b = 0.0D;
    double u = 0.0D;
    double a = 0.0D;

    for (Opinion o : opinions)
    {
      SubjectiveOpinion so = o.toSubjectiveOpinion();

      b += so.b;
      u += so.u * so.a;
      a += so.a;
    }

    if (a > 1.0D) {
      throw new OpinionArithmeticException("Illegal operation, Sum of atomicities is greater than 1.0");
    }
    if (a > 0.0D) {
      u /= a;
    }
    SubjectiveOpinion o = clippedOpinion(b, u, a);

    o.lastOp = OpinionOperator.Add;

    return o;
  }

  private static final SubjectiveOpinion sum(SubjectiveOpinion x, SubjectiveOpinion y) throws OpinionArithmeticException
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    if (y.a + x.a > 1.0D) {
      throw new OpinionArithmeticException("Illegal operation, Sum of atomicities is greater than 1.0");
    }
    double b = x.b + y.b;
    double a = x.a + y.a;
    double u = x.a * x.u + y.a * y.u;

    if (a > 0.0D) {
      u /= a;
    }
    SubjectiveOpinion o = clippedOpinion(b, u, a);

    o.lastOp = OpinionOperator.Add;

    return o;
  }

  /** @deprecated */
  private static final SubjectiveOpinion symmetricDeduction(SubjectiveOpinion x, SubjectiveOpinion yTx, SubjectiveOpinion yFx)
    throws OpinionArithmeticException
  {
    if ((x == null) || (yTx == null) || (yFx == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o1 = new SubjectiveOpinion();
    SubjectiveOpinion Eo = new SubjectiveOpinion();

    double Expec = 0.0D;

    if (Math.abs(yTx.a - yFx.a) <= 1.0E-010D)
      o1.a = yTx.a;
    else {
      throw new OpinionArithmeticException("The atomicities of both sub-conditionals must be equal");
    }

    Eo.b = (x.e * yTx.b + (x.d + (1.0D - x.a) * x.u) * yFx.b);
    Eo.d = (x.e * yTx.d + (x.d + (1.0D - x.a) * x.u) * yFx.d);
    Eo.u = (x.e * yTx.u + (x.d + (1.0D - x.a) * x.u) * yFx.u);
    Eo.a = o1.a;
    Expec = Eo.b + Eo.a * Eo.u;

    if ((((yTx.b >= yFx.b) && (yTx.d >= yFx.d) ? 1 : 0) | ((yTx.b <= yFx.b) && (yTx.d <= yFx.d) ? 1 : 0)) != 0)
    {
      o1.b = Eo.b;
      o1.d = Eo.d;
      o1.u = Eo.u;
    }
    else if ((yTx.b >= yFx.b) && (yTx.d <= yFx.d) && (Expec <= yFx.b + o1.a * (1.0D - yFx.b - yTx.d)) && (x.e <= x.a))
    {
      if (x.b > 0.0D)
      {
        Eo.b -= x.u * x.a * (Eo.b - yFx.b) / x.e;
        Eo.d -= x.u * x.a * (1.0D - o1.a) * (Eo.b - yFx.b) / (x.e * o1.a);
        Eo.u += x.u * x.a * (Eo.b - yFx.b) / (x.e * o1.a);
      }
      else
      {
        Eo.b -= Eo.b - yFx.b;
        Eo.d -= (1.0D - o1.a) * (Eo.b - yFx.b) / o1.a;
        Eo.u += (Eo.b - yFx.b) / o1.a;
      }
    }
    else if ((yTx.b >= yFx.b) && (yTx.d <= yFx.d) && (Expec <= yFx.b + o1.a * (1.0D - yFx.b - yTx.d)) && (x.e > x.a))
    {
      if (x.d > 0.0D)
      {
        Eo.b -= x.u * (1.0D - x.a) * (Eo.b - yFx.b) / (x.d + (1.0D - x.a) * x.u);
        Eo.d -= x.u * (1.0D - x.a) * (1.0D - o1.a) * (Eo.b - yFx.b) / ((x.d + (1.0D - x.a) * x.u) * o1.a);
        Eo.u += x.u * (1.0D - x.a) * (Eo.b - yFx.b) / ((x.d + (1.0D - x.a) * x.u) * o1.a);
      }
      else
      {
        Eo.b -= Eo.b - yFx.b;
        Eo.d -= (1.0D - o1.a) * (Eo.b - yFx.b) / o1.a;
        Eo.u += (Eo.b - yFx.b) / o1.a;
      }
    }
    else if ((yTx.b >= yFx.b) && (yTx.d <= yFx.d) && (Expec > yFx.b + o1.a * (1.0D - yFx.b - yTx.d)) && (x.e <= x.a))
    {
      if (x.b > 0.0D)
      {
        Eo.b -= x.u * x.a * o1.a * (Eo.d - yTx.d) / (x.e * (1.0D - o1.a));
        Eo.d -= x.u * x.a * (1.0D - o1.a) * (Eo.d - yTx.d) / (x.e * (1.0D - o1.a));
        Eo.u += x.u * x.a * (Eo.d - yTx.d) / (x.e * (1.0D - o1.a));
      }
      else
      {
        Eo.b -= o1.a * (Eo.d - yTx.d) / (1.0D - o1.a);
        Eo.d -= (1.0D - o1.a) * (Eo.d - yTx.d) / (1.0D - o1.a);
        Eo.u += (Eo.d - yTx.d) / (1.0D - o1.a);
      }
    }
    else if ((yTx.b >= yFx.b) && (yTx.d <= yFx.d) && (Expec > yFx.b + o1.a * (1.0D - yFx.b - yTx.d)) && (x.e > x.a))
    {
      if (x.d > 0.0D)
      {
        Eo.b -= x.u * (1.0D - x.a) * o1.a * (Eo.d - yTx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
        Eo.d -= x.u * (1.0D - x.a) * (1.0D - o1.a) * (Eo.d - yTx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
        Eo.u += x.u * (1.0D - x.a) * (Eo.d - yTx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
      }
      else
      {
        Eo.b -= o1.a * (Eo.d - yTx.d) / (1.0D - o1.a);
        Eo.d -= (1.0D - o1.a) * (Eo.d - yTx.d) / (1.0D - o1.a);
        Eo.u += (Eo.d - yTx.d) / (1.0D - o1.a);
      }
    }
    else if ((yTx.b <= yFx.b) && (yTx.d >= yFx.d) && (Expec <= yTx.b + o1.a * (1.0D - yTx.b - yFx.d)) && (x.e <= x.a))
    {
      if (x.b > 0.0D)
      {
        Eo.b -= x.u * x.a * (Eo.b - yTx.b) / x.e;
        Eo.d -= x.u * x.a * (1.0D - o1.a) * (Eo.b - yTx.b) / (x.e * o1.a);
        Eo.u += x.u * x.a * (Eo.b - yTx.b) / (x.e * o1.a);
      }
      else
      {
        Eo.b -= Eo.b - yTx.b;
        Eo.d -= (1.0D - o1.a) * (Eo.b - yTx.b) / o1.a;
        Eo.u += (Eo.b - yTx.b) / o1.a;
      }
    }
    else if ((yTx.b <= yFx.b) && (yTx.d >= yFx.d) && (Expec <= yTx.b + o1.a * (1.0D - yTx.b - yFx.d)) && (x.e > x.a))
    {
      if (x.d > 0.0D)
      {
        Eo.b -= x.u * (1.0D - x.a) * (Eo.b - yTx.b) / (x.d + (1.0D - x.a) * x.u);
        Eo.d -= x.u * (1.0D - x.a) * (1.0D - o1.a) * (Eo.b - yTx.b) / ((x.d + (1.0D - x.a) * x.u) * o1.a);
        Eo.u += x.u * (1.0D - x.a) * (Eo.b - yTx.b) / ((x.d + (1.0D - x.a) * x.u) * o1.a);
      }
      else
      {
        Eo.b -= Eo.b - yTx.b;
        Eo.d -= (1.0D - o1.a) * (Eo.b - yTx.b) / o1.a;
        Eo.u += (Eo.b - yTx.b) / o1.a;
      }
    }
    else if ((yTx.b <= yFx.b) && (yTx.d >= yFx.d) && (Expec > yTx.b + o1.a * (1.0D - yTx.b - yFx.d)) && (x.e <= x.a))
    {
      if (x.b > 0.0D)
      {
        Eo.b -= x.u * x.a * o1.a * (Eo.d - yFx.d) / (x.e * (1.0D - o1.a));
        Eo.d -= x.u * x.a * (1.0D - o1.a) * (Eo.d - yFx.d) / (x.e * (1.0D - o1.a));
        Eo.u += x.u * x.a * (Eo.d - yFx.d) / (x.e * (1.0D - o1.a));
      }
      else
      {
        Eo.b -= o1.a * (Eo.d - yFx.d) / (1.0D - o1.a);
        Eo.d -= (1.0D - o1.a) * (Eo.d - yFx.d) / (1.0D - o1.a);
        Eo.u += (Eo.d - yFx.d) / (1.0D - o1.a);
      }
    }
    else if ((yTx.b <= yFx.b) && (yTx.d >= yFx.d) && (Expec > yTx.b + o1.a * (1.0D - yTx.b - yFx.d)) && (x.e > x.a))
    {
      if (x.d > 0.0D)
      {
        Eo.b -= x.u * (1.0D - x.a) * o1.a * (Eo.d - yFx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
        Eo.d -= x.u * (1.0D - x.a) * (1.0D - o1.a) * (Eo.d - yFx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
        Eo.u += x.u * (1.0D - x.a) * (Eo.d - yFx.d) / ((x.d + (1.0D - x.a) * x.u) * (1.0D - o1.a));
      }
      else
      {
        Eo.b -= o1.a * (Eo.d - yFx.d) / (1.0D - o1.a);
        Eo.d -= (1.0D - o1.a) * (Eo.d - yFx.d) / (1.0D - o1.a);
        Eo.u += (Eo.d - yFx.d) / (1.0D - o1.a);
      }
    }
    else
    {
      throw new OpinionArithmeticException("Undefined Conditional Inference");
    }

    o1.checkConsistency(true);

    o1.lastOp = OpinionOperator.Deduce;

    SubjectiveOpinion o = o1;
    o.lastOp = OpinionOperator.Deduce;

    return o;
  }

  private static final SubjectiveOpinion transitivity(SubjectiveOpinion x, SubjectiveOpinion y)
  {
    if ((x == null) || (y == null)) {
      throw new NullPointerException();
    }
    SubjectiveOpinion o = new SubjectiveOpinion();

    x.b *= y.b;
    o.d = (x.b * y.d);
    o.u = (x.d + x.u + x.b * y.u);
    o.a = y.a;

    o.checkConsistency(true);

    o.lastOp = OpinionOperator.Discount;

    return o;
  }

  public SubjectiveOpinion()
  {
  }

  public SubjectiveOpinion(double atomicity)
  {
    setAtomicity(atomicity);
  }

  public SubjectiveOpinion(double belief, boolean dogmatic)
  {
    setBelief(belief, dogmatic);
  }

  public SubjectiveOpinion(double belief, boolean dogmatic, double atomicity)
  {
    setBelief(belief, dogmatic);
    setAtomicity(atomicity);
  }

  public SubjectiveOpinion(double belief, double uncertainty)
  {
    setBelief(belief, uncertainty);
  }

  public SubjectiveOpinion(double belief, double disbelief, double uncertainty)
  {
    set(belief, disbelief, uncertainty);
  }

  public SubjectiveOpinion(double belief, double disbelief, double uncertainty, double atomicity)
  {
    this(belief, disbelief, uncertainty);
    setAtomicity(atomicity);
  }

  public SubjectiveOpinion(Opinion o)
  {
    if (o == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    SubjectiveOpinion x = o.toSubjectiveOpinion();

    this.b = x.b;
    this.d = x.d;
    this.u = x.u;
    this.a = x.a;
    this.e = x.e;
    this.relativeWeight = x.relativeWeight;
    this.lastOp = x.lastOp;
    this.recalculate = x.recalculate;
  }

  public SubjectiveOpinion(SubjectiveOpinion o)
  {
    if (o == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    synchronized (o)
    {
      o.setDependants();

      this.b = o.b;
      this.d = o.d;
      this.u = o.u;
      this.a = o.a;
      this.e = o.e;
      this.relativeWeight = o.relativeWeight;
      this.lastOp = o.lastOp;
      this.recalculate = o.recalculate;
    }
  }

  public final SubjectiveOpinion abduce(Conditionals conditionals, double baseRateX) throws OpinionArithmeticException
  {
    if (conditionals == null) {
      throw new NullPointerException();
    }
    return abduction(new SubjectiveOpinion(this), new SubjectiveOpinion(conditionals.getPositive()), new SubjectiveOpinion(
      conditionals.getNegative()), baseRateX);
  }

  public final SubjectiveOpinion abduce(Opinion xTy, Opinion xFy, double baseRateX) throws OpinionArithmeticException
  {
    if ((xTy == null) || (xFy == null)) {
      throw new NullPointerException();
    }
    return abduction(new SubjectiveOpinion(this), new SubjectiveOpinion(xTy), new SubjectiveOpinion(xFy), baseRateX);
  }

  public final SubjectiveOpinion add(Opinion opinion)
    throws OpinionArithmeticException
  {
    if (opinion == null) {
      throw new NullPointerException("The Opinion must not be null");
    }
    return sum(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  private final void adjust()
  {
    this.b = OpinionBase.constrain(OpinionBase.adjust(this.b));
    this.d = OpinionBase.constrain(OpinionBase.adjust(this.d));
    this.u = OpinionBase.constrain(OpinionBase.adjust(this.u));
  }

  public SubjectiveOpinion adjustExpectation(double expectation)
  {
    SubjectiveOpinion o = new SubjectiveOpinion(this);
    adjustExpectation(o, expectation);
    return o;
  }

  public SubjectiveOpinion adjustExpectation(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return adjustExpectation(opinion.getExpectation());
  }

  public final SubjectiveOpinion and(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return multiply(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public final SubjectiveOpinion average(Opinion opinion)
  {
    Collection opinions = new ArrayList();

    opinions.add(new SubjectiveOpinion(this));
    opinions.add(new SubjectiveOpinion(opinion));

    return smoothAverage(opinions).toSubjectiveOpinion();
  }

  private void checkConsistency() throws OpinionArithmeticException
  {
    checkConsistency(false);
  }

  private void checkConsistency(boolean recalculate) throws OpinionArithmeticException
  {
    synchronized (this)
    {
      if ((this.a < 0.0D) || (this.a > 1.0D)) {
        throw new OpinionArithmeticException("Atomicity out of range, a: 0 <= a <= 1");
      }
      if (recalculate)
      {
        this.b = OpinionBase.constrain(OpinionBase.adjust(this.b));
        this.d = OpinionBase.constrain(OpinionBase.adjust(this.d));
        this.u = OpinionBase.constrain(OpinionBase.adjust(this.u));

        if (Math.abs(this.b + this.d + this.u - 1.0D) > 1.0E-010D)
        {
          double bdu = this.b + this.d + this.u;
          this.b = OpinionBase.constrain(OpinionBase.adjust(this.b / bdu));
          this.u = OpinionBase.constrain(OpinionBase.adjust(this.u / bdu));
          this.d = (1.0D - (this.b + this.u));
        }

        this.recalculate = true;
      }
      else
      {
        if ((this.b < 0.0D) || (this.b > 1.0D)) {
          throw new OpinionArithmeticException("Belief out of range, b: 0 <= b <= 1");
        }
        if ((this.d < 0.0D) || (this.d > 1.0D)) {
          throw new OpinionArithmeticException("Disbelief out of range, d: 0 <= d <= 1");
        }
        if ((this.u < 0.0D) || (this.u > 1.0D)) {
          throw new OpinionArithmeticException("Uncertainty out of range, u: 0 <= u <= 1");
        }
        if (Math.abs(this.b + this.d + this.u - 1.0D) > 1.0E-010D)
          throw new OpinionArithmeticException("Belief, disbelief and uncertainty do not add up to 1: b + d + u != 1");
      }
    }
  }

  public final SubjectiveOpinion decay(double halfLife, double time)
  {
    return erosion(this, OpinionBase.erosionFactorFromHalfLife(halfLife, time));
  }

  public final SubjectiveOpinion deduce(Conditionals conditionals) throws OpinionArithmeticException
  {
    if (conditionals == null) {
      throw new NullPointerException("The conditionals must not be null");
    }
    return deduction(new SubjectiveOpinion(this), new SubjectiveOpinion(conditionals.getPositive()), new SubjectiveOpinion(
      conditionals.getNegative()));
  }

  public final SubjectiveOpinion deduce(Opinion yTx, Opinion yFx)
    throws OpinionArithmeticException
  {
    if ((yTx == null) || (yFx == null)) {
      throw new NullPointerException("The conditionals must not be null");
    }
    return deduction(new SubjectiveOpinion(this), new SubjectiveOpinion(yTx), new SubjectiveOpinion(yFx));
  }

  /** @deprecated */
  public final SubjectiveOpinion deduceSymmetric(Conditionals conditionals)
    throws OpinionArithmeticException
  {
    if (conditionals == null) {
      throw new NullPointerException("The conditionals must not be null");
    }
    return symmetricDeduction(new SubjectiveOpinion(this), new SubjectiveOpinion(conditionals.getPositive()), 
      new SubjectiveOpinion(conditionals.getNegative()));
  }

  /** @deprecated */
  public final SubjectiveOpinion deduceSymmetric(Opinion yTx, Opinion yFx)
    throws OpinionArithmeticException
  {
    if ((yTx == null) || (yFx == null)) {
      throw new NullPointerException("The conditionals must not be null");
    }
    return symmetricDeduction(new SubjectiveOpinion(this), new SubjectiveOpinion(yTx), new SubjectiveOpinion(yFx));
  }

  public final SubjectiveOpinion discount(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return transitivity(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public final SubjectiveOpinion discountBy(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return transitivity(new SubjectiveOpinion(opinion), new SubjectiveOpinion(this));
  }

  public final SubjectiveOpinion dogmaticOpinion()
  {
    SubjectiveOpinion o = new SubjectiveOpinion(this);

    o.setBelief(o.getExpectation(), true);

    return o;
  }

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if ((obj instanceof SubjectiveOpinion))
    {
      SubjectiveOpinion o = (SubjectiveOpinion)obj;

      return (Math.abs(o.b - this.b) < 1.0E-010D) && (Math.abs(o.d - this.d) < 1.0E-010D) && (Math.abs(o.u - this.u) < 1.0E-010D) && 
        (Math.abs(o.a - 
        this.a) < 1.0E-010D);
    }

    return false;
  }

  public final SubjectiveOpinion erode(double factor)
  {
    return erosion(this, factor);
  }

  public final SubjectiveOpinion fuse(Opinion opinion)
    throws OpinionArithmeticException
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return consensus(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public final double getAtomicity()
  {
    return this.a;
  }

  public final double getBelief()
  {
    return this.b;
  }

  public final double getCertainty()
  {
    if (this.u == (0.0D / 0.0D)) {
      return (0.0D / 0.0D);
    }
    return OpinionBase.adjust(1.0D - this.u);
  }

  public final double getDisbelief()
  {
    return this.d;
  }

  public final double getExpectation()
  {
    synchronized (this)
    {
      setDependants();
      return this.e;
    }
  }

  public double getRelativeWeight()
  {
    return this.relativeWeight;
  }

  private double getRelativeWeight(SubjectiveOpinion opinion, OpinionOperator operator)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    if ((operator != null) && (this.lastOp == operator) && (operator.isAssociative())) {
      return this.relativeWeight / opinion.relativeWeight;
    }
    return 1.0D;
  }

  public final double getUncertainty()
  {
    return this.u;
  }

  public SubjectiveOpinion increasedUncertainty()
  {
    synchronized (this)
    {
      SubjectiveOpinion br = new SubjectiveOpinion();

      double sqrt_u = OpinionBase.adjust(Math.sqrt(this.u));
      double k = 1.0D - (sqrt_u - this.u) / (this.b + this.d);

      br.b = OpinionBase.adjust(this.b * k);
      br.u = sqrt_u;
      br.d = OpinionBase.adjust(this.d * k);

      return br;
    }
  }

  public boolean isAbsolute()
  {
    return (this.b == 1.0D) || (this.d == 1.0D);
  }

  public boolean isVacuous()
  {
    return this.u == 1.0D;
  }

  public boolean isCertain(double threshold)
  {
    return !isUncertain(threshold);
  }

  public boolean isConsistent()
  {
    try
    {
      checkConsistency();
      return true;
    }
    catch (OpinionArithmeticException e) {
    }
    return false;
  }

  public boolean isDogmatic()
  {
    return this.u == 0.0D;
  }

  public boolean isMaximizedUncertainty()
  {
    return (this.d == 0.0D) || (this.b == 0.0D);
  }

  public boolean isUncertain(double threshold)
  {
    return 1.0D - this.u < threshold;
  }

  public SubjectiveOpinion uncertainOpinion()
  {
    SubjectiveOpinion o = new SubjectiveOpinion(this);
    maximizeUncertainty(o);
    return o;
  }

  public final SubjectiveOpinion not()
  {
    return complement(this);
  }

  public final SubjectiveOpinion or(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException();
    }
    return coMultiplication(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public void set(double belief, double disbelief, double uncertainty)
  {
    if ((this.b < 0.0D) || (this.d < 0.0D) || (this.u < 0.0D)) {
      throw new IllegalArgumentException("Belief, Disbelief and Uncertainty, x,  must be: 0 <= x");
    }
    double bdu = belief + disbelief + uncertainty;
    setBelief(belief / bdu, uncertainty / bdu);
  }

  public synchronized void set(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    if (opinion.equals(this)) {
      return;
    }
    Opinion old = new SubjectiveOpinion(this);

    SubjectiveOpinion o = opinion.toSubjectiveOpinion();

    synchronized (o)
    {
      this.b = o.b;
      this.d = o.d;
      this.u = o.u;
      this.e = o.e;
      this.a = o.a;
      this.recalculate = o.recalculate;
      this.lastOp = o.lastOp;
      this.relativeWeight = o.relativeWeight;
    }

    this.changeSupport.firePropertyChange("opinion", old, this);
  }

  public final void setAtomicity(double atomicity)
  {
    if ((atomicity < 0.0D) || (atomicity > 1.0D)) {
      throw new IllegalArgumentException("Atomicity, x, must be: 0 <= x <= 1");
    }
    if (atomicity == this.a) {
      return;
    }
    double old = this.a;

    synchronized (this)
    {
      this.a = atomicity;
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("atomicity", new Double(old), new Double(this.a));
  }

  public final void setBelief(double belief)
  {
    setBelief(belief, false);
  }

  public final void setBelief(double belief, boolean dogmatic)
  {
    if (dogmatic)
      setBelief(belief, 0.0D);
    else
      setBelief(belief, 1.0D - belief);
  }

  public final void setBelief(double belief, double uncertainty)
  {
    if ((belief < 0.0D) || (belief > 1.0D) || (uncertainty < 0.0D) || (uncertainty > 1.0D)) {
      throw new IllegalArgumentException("Belief, x, must be: 0 <= x <= 1");
    }
    if (belief + uncertainty - 1.0D > 1.0E-010D) {
      throw new IllegalArgumentException("Belief b, Uncertainty u, must be: (b + u) <= 1");
    }
    if ((belief == this.b) && (uncertainty == this.u)) {
      return;
    }
    Opinion old = new SubjectiveOpinion(this);

    synchronized (this)
    {
      this.b = belief;
      this.u = uncertainty;
      this.d = (1.0D - (belief + uncertainty));
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("opinion", old, this);
  }

  private synchronized void setDependants()
  {
    if (this.recalculate)
    {
      this.b = OpinionBase.constrain(OpinionBase.adjust(this.b));
      this.d = OpinionBase.constrain(OpinionBase.adjust(this.d));
      this.u = OpinionBase.constrain(OpinionBase.adjust(this.u));
      this.a = OpinionBase.constrain(OpinionBase.adjust(this.a));
      this.e = OpinionBase.constrain(OpinionBase.adjust(this.b + this.a * this.u));
      this.recalculate = false;
    }
  }

  private synchronized void setDependants(boolean force)
  {
    if (force) {
      this.recalculate = true;
    }
    setDependants();
  }

  public final void setDisbelief(double disbelief)
  {
    setDisbelief(disbelief, false);
  }

  public final void setDisbelief(double disbelief, boolean dogmatic)
  {
    if (dogmatic)
      setDisbelief(disbelief, 0.0D);
    else
      setDisbelief(disbelief, 1.0D - disbelief);
  }

  public final void setDisbelief(double disbelief, double uncertainty)
  {
    if ((disbelief < 0.0D) || (disbelief > 1.0D) || (uncertainty < 0.0D) || (uncertainty > 1.0D)) {
      throw new IllegalArgumentException("Disbelief, x, must be: 0 <= 1");
    }
    if (disbelief + uncertainty - 1.0D > 1.0E-010D) {
      throw new IllegalArgumentException("Disbelief d, Uncertainty u, must be: (d + u) <= 1");
    }
    if ((disbelief == this.d) && (uncertainty == this.u)) {
      return;
    }
    Opinion old = new SubjectiveOpinion(this);

    synchronized (this)
    {
      this.d = disbelief;
      this.u = uncertainty;
      this.b = (1.0D - (disbelief + uncertainty));
      this.recalculate = true;
    }

    this.changeSupport.firePropertyChange("opinion", old, this);
  }

  public void setRelativeWeight(double weight)
  {
    if (weight <= 0.0D) {
      throw new IllegalArgumentException("Weight must be > 0");
    }
    if (weight == this.relativeWeight) {
      return;
    }
    Double old = new Double(this.relativeWeight);

    this.relativeWeight = weight;

    this.changeSupport.firePropertyChange("relativeWeight", old, new Double(this.relativeWeight));
  }

  /** @deprecated */
  public final SubjectiveOpinion simpleAnd(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return simpleAnd(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  /** @deprecated */
  public final SubjectiveOpinion simpleOr(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    return simpleOr(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public final SubjectiveOpinion subtract(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException();
    }
    return subtraction(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public DiscreteBayesian toDiscreteBayesian(int size)
  {
    if (size < 2) {
      throw new IllegalArgumentException("Conversion not possible");
    }
    return toPureBayesian().toDiscreteBayesian(size);
  }

  public PureBayesian toPureBayesian()
  {
    PureBayesian bayesian = new PureBayesian();

    synchronized (this)
    {
      if (this.u == 0.0D)
      {
        bayesian.setPositive(1.797693134862316E+297D);
        bayesian.setNegative(1.797693134862316E+297D);
      }
      else
      {
        double r = 2.0D * this.b / this.u;
        double s = 2.0D * this.d / this.u;

        bayesian.setPositive(Double.isInfinite(r) ? 1.797693134862316E+297D : r);
        bayesian.setNegative(Double.isInfinite(s) ? 1.797693134862316E+297D : s);
      }

      bayesian.setAtomicity(this.a);
    }

    return bayesian;
  }

  public String toString()
  {
    Object[] args = { Double.valueOf(this.b), Double.valueOf(this.d), Double.valueOf(this.u), Double.valueOf(this.a), 
      Double.valueOf(getExpectation()) };
    return String.format("(b=%1$1.3f, d=%2$1.3f, u=%3$1.3f, a=%4$1.3f, e=%5$1.3f)", args);
  }

  public SubjectiveOpinion toSubjectiveOpinion()
  {
    return this;
  }

  public final SubjectiveOpinion unAnd(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException();
    }
    return division(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion));
  }

  public final SubjectiveOpinion unOr(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException();
    }
    return coDivision(new SubjectiveOpinion(this), new SubjectiveOpinion(opinion), 0.0D);
  }
}