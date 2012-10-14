package com.dstc.spectrum.opinion;

public class FuzzyOpinionTextualizer
  implements OpinionTextualizer
{
  private FuzzyOpinionSet fuzzyOpinionSet;

  public FuzzyOpinionTextualizer(FuzzyOpinionSet fuzzyOpinionSet)
  {
    if (fuzzyOpinionSet == null) {
      throw new NullPointerException("FuzzyOpinionSet must not be null.");
    }
    this.fuzzyOpinionSet = fuzzyOpinionSet;
  }

  public String textualize(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null.");
    }
    return this.fuzzyOpinionSet.getText(opinion);
  }
}