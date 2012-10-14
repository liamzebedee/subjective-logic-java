package com.dstc.spectrum.opinion;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class FuzzySet
  implements Iterable<FuzzySetElement>
{
  private SortedSet<FuzzySetElement> set = new TreeSet();

  private Map<Object, FuzzySetElement> map = new HashMap();

  private SortedSet<FuzzySetElement> copy = Collections.synchronizedSortedSet(new TreeSet());

  private boolean valid = true;
  private boolean autoRange;

  public FuzzySet()
  {
    this(true);
  }

  public FuzzySet(Collection<FuzzySetElement> collection)
  {
    this(true);

    if (collection == null) {
      throw new NullPointerException("Collection must not be null");
    }
    addAll(collection);
  }

  public FuzzySet(FuzzySet set)
  {
    this(true);

    if (set == null) {
      throw new NullPointerException("FuzzySet must not be null");
    }
    addAll(set);
  }

  protected FuzzySet(boolean autoRange)
  {
    this.autoRange = autoRange;
  }

  public synchronized FuzzySetElement first()
  {
    fixExtents();
    return (FuzzySetElement)this.set.first();
  }

  public synchronized SortedSet<FuzzySetElement> headSet(FuzzySetElement toElement)
  {
    fixExtents();
    return this.set.headSet(toElement);
  }

  public synchronized FuzzySetElement last()
  {
    fixExtents();
    return (FuzzySetElement)this.set.last();
  }

  public synchronized SortedSet<FuzzySetElement> subSet(FuzzySetElement fromElement, FuzzySetElement toElement)
  {
    fixExtents();
    return this.set.subSet(fromElement, toElement);
  }

  public synchronized SortedSet<FuzzySetElement> tailSet(FuzzySetElement fromElement)
  {
    fixExtents();
    return this.set.tailSet(fromElement);
  }

  private FuzzySetElement add(FuzzySetElement element)
  {
    Object tag = element.getTag();

    if (this.map.containsKey(tag)) {
      this.set.remove(this.map.get(tag));
    }
    this.set.remove(element);

    this.set.add(element);
    this.map.put(tag, element);

    this.valid = false;

    return element;
  }

  private void cloneSet()
  {
    this.copy.clear();

    for (FuzzySetElement e : this.set)
      this.copy.add(new FuzzySetElement(e));
  }

  private void fixExtents()
  {
    if (this.valid) {
      return;
    }
    cloneSet();

    FuzzySetElement last = null;

    for (Iterator iter = this.copy.iterator(); iter.hasNext(); )
    {
      FuzzySetElement element = (FuzzySetElement)iter.next();

      if (last == null)
      {
        if (this.autoRange)
          element.setMinimum(0.0D);
      }
      else if ((last.isAutoRanging()) && (element.isAutoRanging()))
      {
        double midPoint = FuzzySetElement.adjust((last.getExemplar() + element.getExemplar()) / 2.0D);

        last.setMaximum(midPoint);
        element.setMinimum(midPoint);
      }
      else if (!last.isAutoRanging())
      {
        double midPoint = Math.min(last.getMaximum(), element.getExemplar());

        element.setMinimum(midPoint);
        last.setMaximum(midPoint);
      }
      else
      {
        double midPoint = Math.max(element.getMinimum(), last.getExemplar());

        element.setMinimum(midPoint);
        last.setMaximum(midPoint);
      }

      if ((!iter.hasNext()) && (this.autoRange)) {
        element.setMaximum(1.0D);
      }
      last = element;
    }

    this.valid = true;
  }

  public synchronized void addAll(FuzzySet set)
  {
    if (set == null) {
      throw new NullPointerException("Set must not be null");
    }
    for (FuzzySetElement e : set)
      add(new FuzzySetElement(e));
  }

  public synchronized void addAll(Collection<FuzzySetElement> collection)
  {
    if (collection == null) {
      throw new NullPointerException("Collection must not be null");
    }
    for (FuzzySetElement e : collection)
      add(new FuzzySetElement(e));
  }

  public synchronized FuzzySetElement add(String tag, double minimum, double maximum)
  {
    if (tag == null) {
      throw new NullPointerException("Tag must not be null");
    }
    FuzzySetElement element = new FuzzySetElement(tag, tag, minimum, maximum);

    return add(element);
  }

  public synchronized FuzzySetElement add(String tag, double value)
  {
    if (tag == null) {
      throw new NullPointerException("Tag must not be null");
    }
    FuzzySetElement element = new FuzzySetElement(tag, tag, value);

    return add(element);
  }

  public synchronized FuzzySetElement add(Object tag, String text, double minimum, double maximum)
  {
    if (tag == null) {
      throw new NullPointerException("Tag must not be null");
    }
    FuzzySetElement element = new FuzzySetElement(tag, text, minimum, maximum);

    return add(element);
  }

  public synchronized FuzzySetElement add(Object tag, String text, double value)
  {
    if (tag == null) {
      throw new NullPointerException("Tag must not be null");
    }
    FuzzySetElement element = new FuzzySetElement(tag, text, value);

    return add(element);
  }

  public synchronized FuzzySetElement get(Object tag)
  {
    fixExtents();

    for (FuzzySetElement e : this.copy) {
      if (e.getTag().equals(tag))
        return e;
    }
    return null;
  }

  public synchronized FuzzySetElement get(double value)
  {
    fixExtents();

    for (FuzzySetElement e : this.copy) {
      if ((value >= e.getMinimum()) && (value <= e.getMaximum()))
        return e;
    }
    return null;
  }

  public synchronized void clear()
  {
    this.set.clear();
    this.valid = true;
  }

  public synchronized boolean contains(Object o)
  {
    return this.set.contains(o);
  }

  public synchronized boolean containsAll(Collection<?> c)
  {
    return this.set.containsAll(c);
  }

  public synchronized boolean isEmpty()
  {
    return this.set.isEmpty();
  }

  public synchronized Iterator<FuzzySetElement> iterator()
  {
    return values().iterator();
  }

  public synchronized SortedSet<FuzzySetElement> values()
  {
    fixExtents();
    return Collections.unmodifiableSortedSet(this.copy);
  }

  public synchronized Set keySet()
  {
    return Collections.unmodifiableSet(this.map.keySet());
  }

  public synchronized boolean remove(Object o)
  {
    this.valid = false;
    this.copy.remove(o);
    return this.set.remove(o);
  }

  public synchronized boolean removeAll(Collection<?> c)
  {
    this.valid = false;
    this.copy.removeAll(c);
    return this.set.removeAll(c);
  }

  public synchronized boolean retainAll(Collection<?> c)
  {
    this.valid = false;
    this.copy.retainAll(c);
    return this.set.retainAll(c);
  }

  public synchronized int size()
  {
    return this.set.size();
  }

  public synchronized Object[] toArray()
  {
    fixExtents();
    return this.set.toArray();
  }

  public synchronized double[] exemplars()
  {
    fixExtents();

    double[] a = new double[size()];

    int i = 0;
    for (FuzzySetElement e : this.set) {
      a[(i++)] = e.getExemplar();
    }
    return a;
  }

  public synchronized double[] minimums()
  {
    fixExtents();

    double[] a = new double[size()];

    int i = 0;
    for (FuzzySetElement e : this.set) {
      a[(i++)] = e.getMinimum();
    }
    return a;
  }

  public synchronized double[] maximums()
  {
    fixExtents();

    double[] a = new double[size()];

    int i = 0;
    for (FuzzySetElement e : this.set) {
      a[(i++)] = e.getMaximum();
    }
    return a;
  }

  public synchronized double[] boundaries()
  {
    fixExtents();

    double[] a = new double[size() + 1];

    int i = 0;
    for (FuzzySetElement e : this.set) {
      a[(i++)] = e.getMinimum();
    }
    a[i] = ((FuzzySetElement)this.set.last()).getMaximum();

    return a;
  }

  public synchronized FuzzySetElement[] toArray(FuzzySetElement[] a)
  {
    fixExtents();

    int size = size();

    if (a.length < size) {
      a = (FuzzySetElement[])Array.newInstance(a.getClass().getComponentType(), size);
    }
    int i = 0;
    for (FuzzySetElement e : this.set) {
      a[(i++)] = e;
    }
    return a;
  }

  protected synchronized boolean getAutoRange()
  {
    return this.autoRange;
  }

  protected synchronized void setAutoRange(boolean autoRange)
  {
    if (autoRange == this.autoRange) {
      return;
    }
    this.valid = false;
    this.autoRange = autoRange;
  }
}