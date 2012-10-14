package com.dstc.spectrum.opinion;

import java.util.Comparator;

public abstract class OpinionComparator
  implements Comparator<Opinion>
{
  public static final OpinionComparator CERTAINTY = new OpinionComparator()
  {
    public int compare(Opinion arg0, Opinion arg1)
    {
      if ((arg0 == null) || (arg1 == null)) {
        throw new NullPointerException("Opinions cannot be null");
      }
      SubjectiveOpinion o1 = arg0.toSubjectiveOpinion();
      SubjectiveOpinion o2 = arg1.toSubjectiveOpinion();

      double Ux = o1.getUncertainty();
      double Uy = o2.getUncertainty();

      if (Ux < Uy)
        return 1;
      if (Ux > Uy) {
        return -1;
      }
      return 0;
    }
  };

  public static final OpinionComparator EXPECTATION = new OpinionComparator()
  {
    public int compare(Opinion arg0, Opinion arg1)
    {
      if ((arg0 == null) || (arg1 == null)) {
        throw new NullPointerException("Opinions cannot be null");
      }
      SubjectiveOpinion o1 = arg0.toSubjectiveOpinion();
      SubjectiveOpinion o2 = arg1.toSubjectiveOpinion();

      double Ex = o1.getExpectation();
      double Ey = o2.getExpectation();

      if (Ex > Ey)
        return 1;
      if (Ex < Ey) {
        return -1;
      }

      double Ux = o1.getUncertainty();
      double Uy = o2.getUncertainty();

      if (Ux < Uy)
        return 1;
      if (Ux > Uy) {
        return -1;
      }

      double Ax = o1.getAtomicity();
      double Ay = o2.getAtomicity();

      if (Ax < Ay)
        return 1;
      if (Ax > Ay) {
        return -1;
      }

      double Bx = o1.getBelief();
      double By = o2.getBelief();

      if (Bx > By)
        return 1;
      if (Bx < By) {
        return -1;
      }

      return 0;
    }
  };

  public static final OpinionComparator DEFAULT = EXPECTATION;

  public Opinion max(Opinion o1, Opinion o2)
  {
    if (compare(o1, o2) < 0) {
      return o2;
    }
    return o1;
  }

  public Opinion min(Opinion o1, Opinion o2)
  {
    if (compare(o1, o2) > 0) {
      return o2;
    }
    return o1;
  }

  public Opinion max(Opinion[] opinions)
  {
    if (opinions == null) {
      throw new NullPointerException("Opinions must not be null");
    }
    Opinion max = null;

    int i = 0; for (int size = opinions.length; i < size; i++)
    {
      Opinion o = opinions[i];

      if ((max == null) || ((o != null) && (compare(max, o) < 0))) {
        max = o;
      }
    }
    return max;
  }

  public Opinion min(Opinion[] opinions)
  {
    if (opinions == null) {
      throw new NullPointerException("Opinions must not be null");
    }
    Opinion min = null;

    int i = 0; for (int size = opinions.length; i < size; i++)
    {
      Opinion o = opinions[i];

      if ((min == null) || ((o != null) && (compare(min, o) > 0))) {
        min = o;
      }
    }
    return min;
  }
}