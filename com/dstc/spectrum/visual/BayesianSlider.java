package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.List;

public class BayesianSlider extends OpinionSlider
{
  private static final long serialVersionUID = 3257005462506779703L;
  private Color disbeliefFillColor = new Color(255, 71, 71);

  private boolean showUncertainty = true;

  private boolean showExpectation = true;

  public BayesianSlider()
  {
    this.beliefFillColor = new Color(98, 215, 86);
    this.uncertaintyFillColor = new Color(228, 241, 54);

    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  protected void paintBar(Graphics2D g2, float x0, float y0, float w, float h, float bh2, float scale)
  {
    SubjectiveOpinion o = getOpinion().toSubjectiveOpinion();

    float startBelief = x0;
    float filledBelief = w * (float)o.getBelief();
    float filledDisbelief = w * (float)o.getDisbelief();
    float startDisbelief = x0 + w - filledDisbelief;

    float y1 = (float)(this.bar.getMaxY() - this.overlap);
    this.topHandle.setLocation(startBelief + filledBelief, y1 - scale);
    this.bottomHandle.setLocation(startDisbelief, y1);

    Stroke oldStroke = g2.getStroke();
    Paint oldPaint = g2.getPaint();
    Color oldColor = g2.getColor();
    try
    {
      if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
      {
        if (this.showUncertainty)
        {
          Rectangle2D bar = new Rectangle2D.Double(x0, y0, w, h);
          Rectangle2D bar1 = new Rectangle2D.Double(bar.getX(), bar.getY(), w, bh2);
          Rectangle2D bar2 = new Rectangle2D.Double(bar.getX(), bar.getMaxY() - bh2, w, bh2);
          GradientPaint gradBar2;
          GradientPaint gradBar1;
          GradientPaint gradBar2;
          if (this.renderStyle == RenderStyle.Raised)
          {
            GradientPaint gradBar1 = new GradientPaint((float)bar1.getX(), (float)bar1.getY(), SystemColor.controlLtHighlight, 
              (float)bar1.getX(), (float)bar1.getMaxY(), this.uncertaintyFillColor);

            gradBar2 = new GradientPaint((float)bar2.getX(), (float)bar2.getY(), this.uncertaintyFillColor, 
              (float)bar2.getX(), (float)bar2.getMaxY(), SystemColor.controlShadow);
          }
          else
          {
            gradBar1 = new GradientPaint((float)bar1.getX(), (float)bar1.getY(), SystemColor.controlShadow, 
              (float)bar1.getX(), (float)bar1.getMaxY(), this.uncertaintyFillColor);

            gradBar2 = new GradientPaint((float)bar2.getX(), (float)bar2.getY(), this.uncertaintyFillColor, 
              (float)bar2.getX(), (float)bar2.getMaxY(), SystemColor.controlLtHighlight);
          }

          g2.setPaint(gradBar1);
          g2.fill(bar1);

          g2.setPaint(gradBar2);
          g2.fill(bar2);
        }

        Rectangle2D barBelief = new Rectangle2D.Double(x0, y0, filledBelief, h);

        Rectangle2D barBelief1 = new Rectangle2D.Double(x0, y0, filledBelief, bh2);
        Rectangle2D barBelief2 = new Rectangle2D.Double(x0, y0 + bh2, filledBelief, bh2);
        GradientPaint gradBelief2;
        GradientPaint gradBelief1;
        GradientPaint gradBelief2;
        if (this.renderStyle == RenderStyle.Raised)
        {
          GradientPaint gradBelief1 = new GradientPaint(x0, y0, SystemColor.controlLtHighlight, x0, y0 + bh2, this.beliefFillColor);
          gradBelief2 = new GradientPaint(x0, y0 + bh2, this.beliefFillColor, x0, y0 + h, getBackground());
        }
        else
        {
          gradBelief1 = new GradientPaint(x0, y0, SystemColor.controlShadow, x0, y0 + bh2, this.beliefFillColor);
          gradBelief2 = new GradientPaint(x0, y0 + bh2, this.beliefFillColor, x0, y0 + h, SystemColor.controlLtHighlight);
        }

        Rectangle2D barDisbelief = new Rectangle2D.Double(startDisbelief, y0, filledDisbelief, h);

        Rectangle2D barDisbelief1 = new Rectangle2D.Double(startDisbelief, y0, filledDisbelief, bh2);
        Rectangle2D barDisbelief2 = new Rectangle2D.Double(startDisbelief, y0 + bh2, filledDisbelief, bh2);
        GradientPaint gradUncertainty2;
        GradientPaint gradUncertainty1;
        GradientPaint gradUncertainty2;
        if (this.renderStyle == RenderStyle.Raised)
        {
          GradientPaint gradUncertainty1 = new GradientPaint(startDisbelief, y0, SystemColor.controlLtHighlight, startDisbelief, y0 + 
            bh2, this.disbeliefFillColor);
          gradUncertainty2 = new GradientPaint(startDisbelief, y0 + bh2, this.disbeliefFillColor, startDisbelief, y0 + h, 
            getBackground());
        }
        else
        {
          gradUncertainty1 = new GradientPaint(startDisbelief, y0, SystemColor.controlShadow, startDisbelief, y0 + bh2, 
            this.disbeliefFillColor);
          gradUncertainty2 = new GradientPaint(startDisbelief, y0 + bh2, this.disbeliefFillColor, startDisbelief, y0 + h, 
            SystemColor.controlLtHighlight);
        }

        g2.setPaint(gradBelief1);
        g2.fill(barBelief1);

        g2.setPaint(gradBelief2);
        g2.fill(barBelief2);

        g2.setPaint(gradUncertainty1);
        g2.fill(barDisbelief1);

        g2.setPaint(gradUncertainty2);
        g2.fill(barDisbelief2);
      }
      else
      {
        if (this.showUncertainty)
        {
          Rectangle2D bar = new Rectangle2D.Double(x0, y0, w, h);

          g2.setColor(this.uncertaintyFillColor);
          g2.fill(bar);
        }

        Rectangle2D barBelief = new Rectangle2D.Double(x0, y0, filledBelief, h);
        Rectangle2D barDisbelief = new Rectangle2D.Double(startDisbelief, y0, filledDisbelief, h);

        g2.setColor(this.beliefFillColor);
        g2.fill(barBelief);

        g2.setColor(this.disbeliefFillColor);
        g2.fill(barDisbelief);
      }

      if (isShowExpectation())
      {
        float expectation = x0 + w * (float)this.opinion.getExpectation();

        GeneralPath line = new GeneralPath();
        line.moveTo(expectation, y0);
        line.lineTo(expectation, y0 + h);

        g2.setColor(getForeground());

        g2.setStroke(new BasicStroke(1.5F, 0, 2));
        g2.draw(line);
      }
    }
    catch (RuntimeException rte)
    {
      throw rte;
    }
    finally
    {
      g2.setStroke(oldStroke);
      g2.setPaint(oldPaint);
      g2.setColor(oldColor);
    }
  }

  private synchronized void setDisbeliefValue(double value)
  {
    value = Math.min(1.0D, Math.max(0.0D, value));

    double granularity = getGranularity();

    if (granularity > 0.0D) {
      value = Math.round(value / granularity) * granularity;
    }
    double bottomHandle = this.opinion.getDisbelief();

    if (Math.abs(bottomHandle - value) <= 1.0E-007D) {
      return;
    }
    double uncertainty = Math.round(Math.max(0.0D, Math.min(1.0D, 1.0D - value - this.opinion.getBelief())) / granularity) * granularity;

    SubjectiveOpinion old = new SubjectiveOpinion(this.opinion);

    this.opinion.setDisbelief(value, uncertainty);

    firePropertyChange("opinion", old, this.opinion);

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
    double uncertainty = Math.round(Math.max(0.0D, Math.min(1.0D, 1.0D - value - this.opinion.getDisbelief())) / granularity) * 
      granularity;

    SubjectiveOpinion old = new SubjectiveOpinion(this.opinion);

    this.opinion.setBelief(value, uncertainty);

    firePropertyChange("opinion", old, this.opinion);

    repaint();
  }

  public synchronized Color getDisbeliefFillColor()
  {
    return this.disbeliefFillColor;
  }

  public synchronized void setDisbeliefFillColor(Color disbeliefFillColor)
  {
    if (disbeliefFillColor == null) {
      throw new NullPointerException("Color must not be null.");
    }
    if (this.disbeliefFillColor == disbeliefFillColor) {
      return;
    }
    Color old = this.disbeliefFillColor;
    this.disbeliefFillColor = disbeliefFillColor;
    repaint();

    firePropertyChange("disbeliefFillColor", old, disbeliefFillColor);
  }

  public synchronized boolean isShowExpectation()
  {
    return this.showExpectation;
  }

  public synchronized void setShowExpectation(boolean showExpectation)
  {
    if (this.showExpectation == showExpectation) {
      return;
    }
    this.showExpectation = showExpectation;
    repaint();

    firePropertyChange("showExpectation", !showExpectation, showExpectation);
  }

  public synchronized boolean isShowUncertainty()
  {
    return this.showUncertainty;
  }

  public synchronized void setShowUncertainty(boolean showUncertainty)
  {
    if (this.showUncertainty == showUncertainty) {
      return;
    }
    this.showUncertainty = showUncertainty;
    repaint();

    firePropertyChange("showUncertainty", !showUncertainty, showUncertainty);
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

      if ((!BayesianSlider.this.isEnabled()) || (BayesianSlider.this.topHandle == null) || (BayesianSlider.this.bottomHandle == null)) {
        return;
      }
      Point p = e.getPoint();

      if ((BayesianSlider.this.bottomHandle.contains(p)) && (BayesianSlider.this.isEnabled()))
      {
        this.dragging = true;
        this.handle = BayesianSlider.this.bottomHandle;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else if ((BayesianSlider.this.topHandle.contains(p)) && (BayesianSlider.this.isEnabled()))
      {
        this.dragging = true;
        this.handle = BayesianSlider.this.topHandle;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else
      {
        SliderHandle h = null;

        if (p.getY() <= BayesianSlider.this.bar.getMinY() + BayesianSlider.this.overlap)
          h = BayesianSlider.this.topHandle;
        else if (p.getY() >= BayesianSlider.this.bar.getMaxY() - BayesianSlider.this.overlap) {
          h = BayesianSlider.this.bottomHandle;
        }
        if ((h != null) && (BayesianSlider.this.isEnabled()))
        {
          double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - BayesianSlider.this.bar.getX()) / BayesianSlider.this.bar.getWidth()));

          if (BayesianSlider.this.isSnapToTicks()) {
            value = getClosest(value, BayesianSlider.this.ticks);
          }
          if (h == BayesianSlider.this.topHandle)
            BayesianSlider.this.setBeliefValue(value);
          else {
            BayesianSlider.this.setDisbeliefValue(1.0D - value);
          }
          BayesianSlider.this.setToolTipText(String.valueOf(h == BayesianSlider.this.topHandle ? BayesianSlider.this.opinion.getBelief() : BayesianSlider.this.opinion.getUncertainty()));

          if (BayesianSlider.this.actionListener != null)
            BayesianSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
              h == BayesianSlider.this.topHandle ? "topHandle" : "bottomHandle"));
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
      if ((BayesianSlider.this.isEnabled()) && (this.dragging) && (this.handle != null) && (BayesianSlider.this.isEnabled()))
      {
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - BayesianSlider.this.bar.getX()) / BayesianSlider.this.bar.getWidth()));

        if (BayesianSlider.this.isSnapToTicks()) {
          value = getClosest(value, BayesianSlider.this.ticks);
        }
        if (this.handle == BayesianSlider.this.topHandle)
          BayesianSlider.this.setBeliefValue(value);
        else {
          BayesianSlider.this.setDisbeliefValue(1.0D - value);
        }
        this.dragged = true;

        BayesianSlider.this.setToolTipText(String.valueOf(this.handle == BayesianSlider.this.topHandle ? BayesianSlider.this.opinion.getBelief() : BayesianSlider.this.opinion.getUncertainty()));

        if (BayesianSlider.this.actionListener != null)
          BayesianSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
            this.handle == BayesianSlider.this.topHandle ? "topHandle" : "bottomHandle"));
      }
    }

    public void mouseMoved(MouseEvent e)
    {
      SliderHandle h = null;
      Point p = e.getPoint();

      if (BayesianSlider.this.bottomHandle.contains(p))
        h = BayesianSlider.this.bottomHandle;
      else if (BayesianSlider.this.topHandle.contains(p)) {
        h = BayesianSlider.this.topHandle;
      }
      if (h != null)
      {
        BayesianSlider.this.setToolTipText(String.valueOf(h == BayesianSlider.this.topHandle ? BayesianSlider.this.opinion.getBelief() : BayesianSlider.this.opinion.getDisbelief()));
        BayesianSlider.this.setCursor(new Cursor(12));
      }
      else
      {
        BayesianSlider.this.setToolTipText(null);
        BayesianSlider.this.setCursor(new Cursor(0));
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      if ((this.dragging) && (this.dragged))
      {
        this.dragging = false;
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - BayesianSlider.this.bar.getX()) / BayesianSlider.this.bar.getWidth()));

        synchronized (this.parent)
        {
          if (BayesianSlider.this.isSnapToTicks()) {
            value = getClosest(value, BayesianSlider.this.ticks);
          }
        }
        if (this.handle == BayesianSlider.this.topHandle)
          BayesianSlider.this.setBeliefValue(value);
        else {
          BayesianSlider.this.setDisbeliefValue(1.0D - value);
        }
        BayesianSlider.this.setToolTipText(String.valueOf(this.handle == BayesianSlider.this.topHandle ? BayesianSlider.this.opinion.getBelief() : BayesianSlider.this.opinion.getUncertainty()));

        if (BayesianSlider.this.actionListener != null)
          BayesianSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, 
            this.handle == BayesianSlider.this.topHandle ? "topHandle" : "bottomHandle"));
      }
    }

    public boolean isDragging()
    {
      return this.dragging;
    }
  }
}