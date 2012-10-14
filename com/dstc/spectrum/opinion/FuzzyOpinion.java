package com.dstc.spectrum.opinion;

public class FuzzyOpinion extends OpinionBase
{
  private SubjectiveOpinion opinion;
  private FuzzyOpinionSet parent = null;
  private FuzzySetElement expectation;
  private FuzzySetElement uncertainty = null;

  private boolean polarized = false;

  protected FuzzyOpinion(FuzzyOpinionSet parent, FuzzySetElement expectation, FuzzySetElement uncertainty, SubjectiveOpinion opinion, boolean polarized)
  {
    if (parent == null) {
      throw new NullPointerException("FuzzyOpinionSet must not be null");
    }
    if (expectation == null) {
      throw new NullPointerException("Expectation must not be null");
    }
    if (uncertainty == null) {
      throw new NullPointerException("Uncertainty must not be null");
    }
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    this.parent = parent;
    this.expectation = expectation;
    this.uncertainty = uncertainty;
    this.opinion = opinion;
    this.polarized = polarized;
  }

  public String toString()
  {
    return this.parent.toString(this);
  }

  public FuzzySetElement getUncertaintyElement()
  {
    return this.uncertainty;
  }

  public FuzzySetElement getExpectationElement()
  {
    return this.expectation;
  }

  public boolean isPolarized()
  {
    return this.polarized;
  }

  public boolean equals(Object obj)
  {
    if ((obj != null) && ((obj instanceof FuzzyOpinion)))
    {
      FuzzyOpinion fo = (FuzzyOpinion)obj;

      return (this.opinion.equals(fo.opinion)) && (this.polarized == fo.polarized) && (this.expectation.getTag().equals(fo.expectation.getTag())) && 
        (this.uncertainty.getTag().equals(fo.uncertainty.getTag())) && (this.parent == fo.parent);
    }

    return false;
  }

  public boolean equivalent(Opinion op)
  {
    if ((op != null) && ((op instanceof FuzzyOpinion)))
    {
      FuzzyOpinion fo = (FuzzyOpinion)op;

      return (this.polarized == fo.polarized) && (this.expectation.getTag().equals(fo.expectation.getTag())) && 
        (this.uncertainty.getTag().equals(fo.uncertainty.getTag())) && (this.parent == fo.parent);
    }

    return false;
  }

  public int compareTo(FuzzyOpinion fo)
  {
    int ret = this.expectation.compareTo(fo.expectation);

    if (ret == 0) {
      ret = this.uncertainty.compareTo(fo.uncertainty);
    }
    if (ret == 0) {
      ret = this.opinion.compareTo(fo.opinion);
    }
    return ret;
  }

  public double getAtomicity()
  {
    return this.opinion.getAtomicity();
  }

  protected FuzzyOpinion(FuzzyOpinionSet parent, FuzzySetElement expectation, FuzzySetElement uncertainty, SubjectiveOpinion opinion)
  {
    this(parent, expectation, uncertainty, opinion, false);
  }

  public PureBayesian toPureBayesian()
  {
    return this.opinion.toPureBayesian();
  }

  public SubjectiveOpinion toSubjectiveOpinion()
  {
    return new SubjectiveOpinion(this.opinion);
  }

  public double getExpectation()
  {
    return this.opinion.getExpectation();
  }
}