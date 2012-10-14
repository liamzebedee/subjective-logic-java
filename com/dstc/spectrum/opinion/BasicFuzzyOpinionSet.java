package com.dstc.spectrum.opinion;

public class BasicFuzzyOpinionSet extends FuzzyOpinionSet2D
{
  private static FuzzySet expectations = new FuzzySet();

  private static FuzzySet uncertainties = new FuzzySet();

  static
  {
    expectations.add("Absolutely Not", "Absolutely Not", 0.0D, 0.001D);
    expectations.add("Very Unlikely", "Very Unlikely", 0.1D);
    expectations.add("Unlikely", "Unlikely", 0.225D);
    expectations.add("Somewhat Unlikely", "Somewhat Unlikely", 0.35D);
    expectations.add("Chances about even", "Chances about even", 0.5D);
    expectations.add("Somewhat Likely", "Somewhat Likely", 0.65D);
    expectations.add("Likely", "Likely", 0.775D);
    expectations.add("Very Likely", "Very Likely", 0.9D);
    expectations.add("Absolutely", "Absolutely", 0.999D, 1.0D);

    uncertainties.add("Completely Uncertain", "Completely Uncertain", 0.99D, 1.0D);
    uncertainties.add("Highly Uncertain", "Very Uncertain", 0.75D);
    uncertainties.add("Uncertain", "Uncertain", 0.25D);
    uncertainties.add("Somewhat Uncertain", "Slightly Uncertain", 0.075D);
    uncertainties.add("Certain", null, 0.0D, 0.01D);
  }

  public BasicFuzzyOpinionSet()
  {
    super(expectations, uncertainties);
  }

  public BasicFuzzyOpinionSet(double atomicity)
  {
    super(expectations, uncertainties, atomicity);
  }
}