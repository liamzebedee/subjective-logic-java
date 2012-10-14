package com.dstc.spectrum.opinion;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class FuzzyOpinionSet2D
  implements FuzzyOpinionSet
{
  private FuzzySet expectationSet = new FuzzySet();

  private FuzzySet uncertaintySet = new FuzzySet();

  private Map<FuzzySetElement, Map<FuzzySetElement, FuzzyOpinion>> map = new HashMap();

  private double atomicity = 0.5D;

  private double polarization = 0.75D;

  private String polarizedText = "Polarized";

  private String uncertainTextFormat = "%2$s, and %1$s";

  private boolean valid = false;

  public FuzzyOpinionSet2D()
  {
  }

  public FuzzyOpinionSet2D(double atomicity)
  {
    this();
    setAtomicity(atomicity);
  }

  public FuzzyOpinionSet2D(FuzzySet expectations, FuzzySet uncertainties)
  {
    setExpectations(expectations);
    setUncertainties(uncertainties);
  }

  public FuzzyOpinionSet2D(FuzzySet expectations, FuzzySet uncertainties, double atomicity)
  {
    this(expectations, uncertainties);
    setAtomicity(atomicity);
  }

  public synchronized Map<FuzzySetElement, Map<FuzzySetElement, FuzzyOpinion>> valueMap()
  {
    Map retMap = new HashMap();
    Map map = getMap();

    for (FuzzySetElement v : map.keySet()) {
      retMap.put(v, Collections.unmodifiableMap((Map)map.get(v)));
    }
    return Collections.unmodifiableMap(retMap);
  }

  public synchronized void setAtomicity(double atomicity)
  {
    if ((atomicity < 0.0D) || (atomicity > 1.0D)) {
      throw new IllegalArgumentException("Atomicity, x, must be 0 <= x <= 1");
    }
    this.atomicity = atomicity;
    this.valid = false;
  }

  private Map<FuzzySetElement, Map<FuzzySetElement, FuzzyOpinion>> getMap()
  {
    if (this.valid) {
      return this.map;
    }
    this.map.clear();
    this.map = createMap(this.atomicity);

    this.valid = true;

    return this.map;
  }

  private Map<FuzzySetElement, Map<FuzzySetElement, FuzzyOpinion>> getMap(double atomicity)
  {
    if (atomicity == this.atomicity) {
      return getMap();
    }
    return createMap(atomicity);
  }

  private synchronized Map<FuzzySetElement, Map<FuzzySetElement, FuzzyOpinion>> createMap(double atomicity)
  {
    Map map = new HashMap();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.expectationSet.iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      FuzzySetElement ed = (FuzzySetElement)localIterator1.next();

      double eMin = ed.getMinimum();
      double eMax = ed.getMaximum();

      Map map2 = (Map)map.get(ed);

      localIterator2 = this.uncertaintySet.iterator(); continue; FuzzySetElement ud = (FuzzySetElement)localIterator2.next();

      double minLower = ud.getMinimum() * atomicity;
      double maxUpper = minLower + (1.0D - ud.getMinimum());

      if (overlaps(eMin, eMax, minLower, maxUpper))
      {
        SubjectiveOpinion o = createSubjectiveOpinion(ed, ud, atomicity);

        if (o != null)
        {
          if (map2 == null)
          {
            map2 = new HashMap();
            map.put(ed, map2);
          }

          map2.put(ud, new FuzzyOpinion(this, ed, ud, o));
        }
      }

    }

    return map;
  }

  private SubjectiveOpinion createSubjectiveOpinion(FuzzySetElement exp, FuzzySetElement unc, double atomicity)
  {
    double uMin = unc.getMinimum();
    double uMax = unc.getMaximum();

    double e1 = Math.max(exp.getMinimum(), uMin * atomicity);
    double e2 = Math.min(exp.getMaximum(), uMin * atomicity + (1.0D - uMin));
    double u1;
    if (e1 < atomicity) {
      u1 = e1 / atomicity;
    }
    else
    {
      double u1;
      if (e1 > atomicity)
        u1 = (e1 - 1.0D) / (1.0D - atomicity);
      else
        u1 = uMax;
    }
    double u2;
    if (e1 < atomicity) {
      u2 = e2 / atomicity;
    }
    else
    {
      double u2;
      if (e1 > atomicity)
        u2 = (e2 - 1.0D) / (1.0D - atomicity);
      else
        u2 = uMax;
    }
    double u1 = Math.min(uMax, Math.max(uMin, u1));
    double u2 = Math.min(uMax, Math.max(uMin, u2));

    if (Math.abs(e2 - e1) < 1.0E-010D) {
      return null;
    }
    double e3 = (e1 + e2) / 2.0D;
    double u3;
    double u3;
    if (u1 < u2)
    {
      if (Math.abs(u2 - uMin) < 2.E-010D) {
        return null;
      }
      u3 = adjust((uMin + u2) / 2.0D);
    }
    else
    {
      double u3;
      if (u1 > u2)
      {
        if (Math.abs(u1 - uMin) < 2.E-010D) {
          return null;
        }
        u3 = adjust((uMin + u1) / 2.0D);
      }
      else
      {
        double u3Max;
        if (e3 < atomicity) {
          u3Max = e3 / atomicity;
        }
        else
        {
          double u3Max;
          if (e1 > atomicity)
            u3Max = (e3 - 1.0D) / (atomicity - 1.0D);
          else
            u3Max = uMax;
        }
        double u3Max = Math.min(uMax, Math.max(uMin, u3Max));

        if (Math.abs(u3Max - uMin) < 2.E-010D) {
          return null;
        }
        u3 = adjust((uMin + u3Max) / 2.0D);
      }
    }
    SubjectiveOpinion o = new SubjectiveOpinion(atomicity);
    o.setBelief(adjust(e3 - u3 * atomicity), u3);

    return o;
  }

  protected static double adjust(double x)
  {
    return x == (0.0D / 0.0D) ? (0.0D / 0.0D) : Math.round(x * 100000000000.0D) / 100000000000.0D;
  }

  private boolean overlaps(double x1, double x2, double y1, double y2)
  {
    return ((x1 <= y1) && (x2 >= y1)) || ((y1 <= x1) && (y2 >= x1));
  }

  public synchronized double getAtomicity()
  {
    return this.atomicity;
  }

  public synchronized FuzzyOpinion get(Object e)
  {
    return get(e, null);
  }

  public synchronized FuzzyOpinion get(Object e, Object u)
  {
    if (e == null) {
      throw new NullPointerException("Expectation Tag must not be null");
    }
    FuzzySetElement ed = this.expectationSet.get(e);
    FuzzySetElement ud = this.uncertaintySet.get(u);

    FuzzyOpinion op = null;
    Map uncertaintyMap = null;

    if (ed != null) {
      uncertaintyMap = (Map)getMap().get(ed);
    }
    if (uncertaintyMap != null) {
      op = (FuzzyOpinion)uncertaintyMap.get(ud);
    }
    return op;
  }

  public synchronized FuzzyOpinion get(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    Map map = getMap(opinion.getAtomicity());

    double e = opinion.getExpectation();
    double u = opinion.toSubjectiveOpinion().getUncertainty();

    FuzzySetElement ed = this.expectationSet.get(e);
    FuzzySetElement ud = this.uncertaintySet.get(u);

    FuzzyOpinion op = null;
    Map uncertaintyMap = null;

    if (ed != null) {
      uncertaintyMap = (Map)map.get(ed);
    }
    if (uncertaintyMap != null) {
      op = (FuzzyOpinion)uncertaintyMap.get(ud);
    }
    if ((opinion instanceof DiscreteBayesian))
    {
      DiscreteBayesian db = (DiscreteBayesian)opinion;
      if (db.isPolarized(this.polarization)) {
        return new FuzzyOpinion(this, ed, ud, db.toSubjectiveOpinion(), true);
      }
    }
    return op;
  }

  public synchronized SortedSet<FuzzySetElement> getExpectationChoices(FuzzySetElement uncertainty)
  {
    Map map = getMap();

    SortedSet set = new TreeSet();

    for (FuzzySetElement expectation : map.keySet()) {
      if (map.containsKey(uncertainty))
        set.add(expectation);
    }
    return set;
  }

  public synchronized SortedSet<FuzzySetElement> getUncertaintyChoices(FuzzySetElement expectation)
  {
    Map map = getMap();

    SortedSet set = new TreeSet();

    Map choices = (Map)map.get(expectation);

    if (choices != null) {
      set.addAll((Collection)choices.keySet());
    }
    return set;
  }

  public synchronized String getText(Opinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    FuzzyOpinion fo = get(opinion);

    return fo == null ? null : fo.toString();
  }

  public synchronized double getPolarization()
  {
    return this.polarization;
  }

  public synchronized String getPolarizedText()
  {
    return this.polarizedText;
  }

  public synchronized void setPolarization(double polarizationFactor)
  {
    if (polarizationFactor == this.polarization) {
      return;
    }
    if (Double.isNaN(polarizationFactor)) {
      throw new IllegalArgumentException("Polarization Factor, x, must be 0 <= x <= 1");
    }
    polarizationFactor = Math.min(1.0D, Math.max(0.0D, polarizationFactor));

    this.polarization = polarizationFactor;
  }

  public synchronized void setPolarizedText(String polarizedText)
  {
    if (polarizedText == null) {
      polarizedText = "";
    }
    if (this.polarizedText.equals(polarizedText)) {
      return;
    }
    this.polarizedText = polarizedText;
  }

  public synchronized void setExpectations(FuzzySet expectations)
  {
    if (expectations == null) {
      throw new NullPointerException("Expectation Set must not be null");
    }
    this.expectationSet.clear();
    this.expectationSet.setAutoRange(expectations.getAutoRange());
    this.expectationSet.addAll(expectations);

    this.valid = false;
  }

  public synchronized void setUncertainties(FuzzySet uncertainties)
  {
    if (uncertainties == null) {
      throw new NullPointerException("Uncertainty Set must not be null");
    }
    this.uncertaintySet.clear();
    this.uncertaintySet.setAutoRange(uncertainties.getAutoRange());
    this.uncertaintySet.addAll(uncertainties);

    this.valid = false;
  }

  public String toString(FuzzyOpinion opinion)
  {
    synchronized (opinion)
    {
      String expectationText;
      String expectationText;
      if (opinion.isPolarized())
        expectationText = getPolarizedText();
      else {
        expectationText = opinion.getExpectationElement().getText();
      }
      String uncertaintyText = opinion.getUncertaintyElement().getText();

      if (uncertaintyText == null) {
        return expectationText;
      }
      return String.format(this.uncertainTextFormat, new Object[] { uncertaintyText, expectationText });
    }
  }

  public String getUncertainTextFormat()
  {
    return this.uncertainTextFormat;
  }

  public void setUncertainTextFormat(String uncertainTextFormat)
  {
    if (uncertainTextFormat == null) {
      throw new NullPointerException("Text format must not be null");
    }
    this.uncertainTextFormat = uncertainTextFormat;
  }

  public String toString()
  {
    return this.map.toString();
  }

  public synchronized SortedSet<FuzzyOpinion> values()
  {
    SortedSet retSet = new TreeSet();

    for (FuzzySetElement v : getMap().keySet()) {
      retSet.addAll(((Map)this.map.get(v)).values());
    }
    return Collections.unmodifiableSortedSet(retSet);
  }

  public Iterator<FuzzyOpinion> iterator()
  {
    return values().iterator();
  }

  public synchronized FuzzySet getExpectationSet()
  {
    return this.expectationSet;
  }

  public synchronized FuzzySet getUncertaintySet()
  {
    return this.uncertaintySet;
  }
}