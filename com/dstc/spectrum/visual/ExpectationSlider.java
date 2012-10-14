package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.List;

public class ExpectationSlider extends OpinionSlider
{
  private static final long serialVersionUID = 3257005462506779703L;

  public ExpectationSlider()
  {
    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  protected void paintBar(Graphics2D g2, float x0, float y0, float w, float h, float bh2, float scale)
  {
    double au = this.opinion == null ? 0.0D : this.opinion.getUncertainty() * this.opinion.getAtomicity();
    double b = this.opinion == null ? 0.0D : this.opinion.getBelief();

    float filledBelief = w * (float)b;
    float filledUncertainty = w * (float)au;
    float startBelief = x0;
    float startUncertainty = startBelief + filledBelief;

    float y1 = (float)(this.bar.getMaxY() - this.overlap);
    this.topHandle.setLocation(startBelief + filledBelief, y1 - scale);
    this.bottomHandle.setLocation(startUncertainty + filledUncertainty, y1);

    if (this.opinion != null)
    {
      Rectangle2D barBelief = new Rectangle2D.Double(x0, y0, filledBelief, h);
      Rectangle2D barUncertainty = new Rectangle2D.Double(startUncertainty, y0, filledUncertainty, h);

      if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
      {
        Rectangle2D barBelief1 = new Rectangle2D.Double(x0, y0, filledBelief, bh2);
        Rectangle2D barBelief2 = new Rectangle2D.Double(x0, y0 + bh2, filledBelief, bh2);
        GradientPaint gradBelief2;
        GradientPaint gradBelief1;
        GradientPaint gradBelief2;
        if (this.renderStyle == RenderStyle.Raised)
        {
          GradientPaint gradBelief1 = new GradientPaint(x0, (float)this.bar.getY(), SystemColor.controlLtHighlight, x0, 
            (float)(this.bar.getY() + bh2), this.beliefFillColor);
          gradBelief2 = new GradientPaint(x0, (float)this.bar.getY() + bh2, this.beliefFillColor, x0, (float)this.bar.getMaxY(), 
            SystemColor.controlShadow);
        }
        else
        {
          gradBelief1 = new GradientPaint(x0, (float)this.bar.getY(), SystemColor.controlShadow, x0, 
            (float)(this.bar.getY() + bh2), this.beliefFillColor);
          gradBelief2 = new GradientPaint(x0, (float)this.bar.getY() + bh2, this.beliefFillColor, x0, (float)this.bar.getMaxY(), 
            SystemColor.controlLtHighlight);
        }

        Rectangle2D barUncertainty1 = new Rectangle2D.Double(startUncertainty, y0, filledUncertainty, bh2);
        Rectangle2D barUncertainty2 = new Rectangle2D.Double(startUncertainty, y0 + bh2, filledUncertainty, bh2);
        GradientPaint gradUncertainty2;
        GradientPaint gradUncertainty1;
        GradientPaint gradUncertainty2;
        if (this.renderStyle == RenderStyle.Raised)
        {
          GradientPaint gradUncertainty1 = new GradientPaint(startUncertainty, (float)this.bar.getY(), 
            SystemColor.controlLtHighlight, startUncertainty, (float)(this.bar.getY() + bh2), this.uncertaintyFillColor);
          gradUncertainty2 = new GradientPaint(startUncertainty, (float)this.bar.getY() + bh2, 
            this.uncertaintyFillColor, startUncertainty, (float)this.bar.getMaxY(), SystemColor.controlShadow);
        }
        else
        {
          gradUncertainty1 = new GradientPaint(startUncertainty, (float)this.bar.getY(), 
            SystemColor.controlShadow, startUncertainty, (float)(this.bar.getY() + bh2), this.uncertaintyFillColor);
          gradUncertainty2 = new GradientPaint(startUncertainty, (float)this.bar.getY() + bh2, 
            this.uncertaintyFillColor, startUncertainty, (float)this.bar.getMaxY(), SystemColor.controlLtHighlight);
        }

        g2.setPaint(gradBelief1);
        g2.fill(barBelief1);

        g2.setPaint(gradBelief2);
        g2.fill(barBelief2);

        g2.setPaint(gradUncertainty1);
        g2.fill(barUncertainty1);

        g2.setPaint(gradUncertainty2);
        g2.fill(barUncertainty2);
      }
      else
      {
        g2.setColor(this.beliefFillColor);
        g2.fill(barBelief);

        g2.setColor(this.uncertaintyFillColor);
        g2.fill(barUncertainty);
      }
    }
  }

  private synchronized void setExpectationValue(double value)
  {
    value = Math.min(1.0D, Math.max(0.0D, value));

    double granularity = getGranularity();

    if (granularity > 0.0D) {
      value = Math.round(value / granularity) * granularity;
    }
    synchronized (this.opinion)
    {
      double e = this.opinion.getExpectation();
      double b = this.opinion.getBelief();
      double au = Math.max(0.0D, value - b);
      double a = this.opinion.getAtomicity();
      double u = a == 0.0D ? 0.0D : au / a;

      if (granularity > 0.0D) {
        u = Math.round(u / granularity) * granularity;
      }
      if (b + u > 1.0D)
      {
        if (isConstrainUncertaintySlider())
        {
          u = 1.0D - b;
        }
        else
        {
          b = (value - a) / (1.0D - a);
          u = 1.0D - b;
        }
      }

      if ((u <= 1.0E-007D) && (value < b)) {
        b = value;
      }
      SubjectiveOpinion old = new SubjectiveOpinion(this.opinion);

      this.opinion.setBelief(b, u);

      firePropertyChange("opinion", old, this.opinion);
    }

    repaint();
  }

  private synchronized void setBeliefValue(double value)
  {
    value = Math.min(1.0D, Math.max(0.0D, value));

    double granularity = getGranularity();

    if (granularity > 0.0D) {
      value = Math.round(value / granularity) * granularity;
    }
    double topHandle = this.opinion.getBelief();

    if (Math.abs(topHandle - value) <= 1.0E-007D) {
      return;
    }
    double bottomHandle = Math.max(0.0D, Math.min(1.0D, (this.opinion.getExpectation() - value) / this.opinion.getAtomicity()));

    if (value + bottomHandle > 1.0D) {
      bottomHandle = 1.0D - value;
    }
    SubjectiveOpinion old = new SubjectiveOpinion(this.opinion);

    this.opinion.setBelief(value, bottomHandle);

    firePropertyChange("opinion", old, this.opinion);

    repaint();
  }

  public synchronized void removeActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
  }

  private class MyMouseListener extends MouseAdapter
    implements MouseMotionListener
  {
    private boolean dragging = false;

    private boolean dragged = false;

    private SliderHandle handle = null;

    private double offsetX = 0.0D;
    private Component parent;

    MyMouseListener(Component parent)
    {
      this.parent = parent;
    }

    public void mousePressed(MouseEvent e)
    {
      this.dragging = false;
      this.dragged = false;

      if ((!ExpectationSlider.this.isEnabled()) || (ExpectationSlider.this.topHandle == null) || (ExpectationSlider.this.bottomHandle == null)) {
        return;
      }
      Point p = e.getPoint();

      if ((ExpectationSlider.this.bottomHandle.contains(p)) && (ExpectationSlider.this.isEnabled()))
      {
        this.dragging = true;
        this.handle = ExpectationSlider.this.bottomHandle;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else if ((ExpectationSlider.this.topHandle.contains(p)) && (ExpectationSlider.this.isEnabled()))
      {
        this.dragging = true;
        this.handle = ExpectationSlider.this.topHandle;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else
      {
        SliderHandle h = null;

        if (p.getY() <= ExpectationSlider.this.bar.getMinY() + ExpectationSlider.this.overlap)
          h = ExpectationSlider.this.topHandle;
        else if (p.getY() >= ExpectationSlider.this.bar.getMaxY() - ExpectationSlider.this.overlap) {
          h = ExpectationSlider.this.bottomHandle;
        }
        if ((h != null) && (ExpectationSlider.this.isEnabled()))
        {
          double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - ExpectationSlider.this.bar.getX()) / ExpectationSlider.this.bar.getWidth()));

          if (h == ExpectationSlider.this.topHandle)
            ExpectationSlider.this.setBeliefValue(getClosest(value, ExpectationSlider.this.ticks));
          else {
            ExpectationSlider.this.setExpectationValue(getClosest(value, ExpectationSlider.this.ticks));
          }
          ExpectationSlider.this.setToolTipText(String.valueOf(h == ExpectationSlider.this.topHandle ? ExpectationSlider.this.opinion.getBelief() : ExpectationSlider.this.opinion.getUncertainty()));

          if (ExpectationSlider.this.actionListener != null)
            ExpectationSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
              h == ExpectationSlider.this.topHandle ? "topHandle" : "bottomHandle"));
        }
      }
    }

    private double getClosest(double value, List<Double> list)
    {
      if (list.isEmpty()) {
        return value;
      }
      Double p = null;

      for (Double n : list)
      {
        if (value == n.doubleValue()) {
          return value;
        }
        if (value > n.doubleValue())
        {
          p = n;
        }
        else
        {
          if (p == null) {
            return n.doubleValue();
          }
          if (n.doubleValue() - value <= value - p.doubleValue()) {
            return n.doubleValue();
          }
          return p.doubleValue();
        }
      }

      return p.doubleValue();
    }

    public void mouseDragged(MouseEvent e)
    {
      if ((ExpectationSlider.this.isEnabled()) && (this.dragging) && (this.handle != null) && (ExpectationSlider.this.isEnabled()))
      {
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - ExpectationSlider.this.bar.getX()) / ExpectationSlider.this.bar.getWidth()));

        if (ExpectationSlider.this.isSnapToTicks()) {
          value = getClosest(value, ExpectationSlider.this.ticks);
        }
        if (this.handle == ExpectationSlider.this.topHandle)
          ExpectationSlider.this.setBeliefValue(value);
        else {
          ExpectationSlider.this.setExpectationValue(value);
        }
        this.dragged = true;

        ExpectationSlider.this.setToolTipText(String.valueOf(this.handle == ExpectationSlider.this.topHandle ? ExpectationSlider.this.opinion.getBelief() : ExpectationSlider.this.opinion.getUncertainty()));

        if (ExpectationSlider.this.actionListener != null)
          ExpectationSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
            this.handle == ExpectationSlider.this.topHandle ? "topHandle" : "bottomHandle"));
      }
    }

    public void mouseMoved(MouseEvent e)
    {
      SliderHandle h = null;
      Point p = e.getPoint();

      if (ExpectationSlider.this.bottomHandle.contains(p))
        h = ExpectationSlider.this.bottomHandle;
      else if (ExpectationSlider.this.topHandle.contains(p)) {
        h = ExpectationSlider.this.topHandle;
      }
      if (h != null)
      {
        ExpectationSlider.this.setToolTipText(String.valueOf(h == ExpectationSlider.this.topHandle ? ExpectationSlider.this.opinion.getBelief() : ExpectationSlider.this.opinion.getExpectation()));
        ExpectationSlider.this.setCursor(new Cursor(12));
      }
      else
      {
        ExpectationSlider.this.setToolTipText(null);
        ExpectationSlider.this.setCursor(new Cursor(0));
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      if ((this.dragging) && (this.dragged))
      {
        this.dragging = false;
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - ExpectationSlider.this.bar.getX()) / ExpectationSlider.this.bar.getWidth()));

        synchronized (this.parent)
        {
          if (ExpectationSlider.this.isSnapToTicks()) {
            value = getClosest(value, ExpectationSlider.this.ticks);
          }
        }
        if (this.handle == ExpectationSlider.this.topHandle)
          ExpectationSlider.this.setBeliefValue(value);
        else {
          ExpectationSlider.this.setExpectationValue(value);
        }
        ExpectationSlider.this.setToolTipText(String.valueOf(this.handle == ExpectationSlider.this.topHandle ? ExpectationSlider.this.opinion.getBelief() : ExpectationSlider.this.opinion.getUncertainty()));

        if (ExpectationSlider.this.actionListener != null)
          ExpectationSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
            this.handle == ExpectationSlider.this.topHandle ? "topHandle" : "bottomHandle"));
      }
    }

    public boolean isDragging()
    {
      return this.dragging;
    }
  }
}