package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public abstract class OpinionSlider extends JComponent
{
  protected ActionListener actionListener = null;

  protected PropertyChangeListener listener = new PropertyChangeListener()
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      OpinionSlider.this.repaint();
    }
  };
  protected static final double TOLERANCE = 10000000.0D;
  protected static final double TOLERANCE_ADJUST = 1.0E-007D;
  protected static int minBarHeight = 3;

  protected static int minSliderSize = 4;

  protected static int minBarWidth = 30;

  protected List<Double> ticks = new ArrayList();

  protected int tickHeight = 8;

  protected int tickBuffer = 3;

  protected int sliderSize = 22;

  protected int barHeight = 7;

  private boolean paintTicks = true;

  private boolean snapToTicks = false;

  protected SliderHandle bottomHandle = new SliderHandle(SliderHandle.HandleType.Lower);

  protected SliderHandle topHandle = new SliderHandle(SliderHandle.HandleType.Upper);
  protected Rectangle2D bar;
  protected SubjectiveOpinion opinion = null;

  protected double overlap = 0.0D;

  private double granularity = 0.01D;

  protected Color beliefFillColor = new Color(0, 0, 204);

  protected Color uncertaintyFillColor = new Color(152, 152, 245);

  protected RenderStyle renderStyle = RenderStyle.Raised;

  private boolean constrainUncertaintySlider = false;

  private boolean showHandlesWhenDisabled = false;

  private Color barBackgroundColor = null;

  private Border barBorder = BorderFactory.createBevelBorder(1);

  public OpinionSlider()
  {
    initialize();
  }

  protected void initialize()
  {
    this.opinion = new SubjectiveOpinion();

    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(2 * this.sliderSize + this.barHeight - 2.0D * this.overlap)));
  }

  public synchronized int getSliderSize()
  {
    return this.sliderSize;
  }

  public synchronized void setSliderSize(int sliderSize)
  {
    if ((sliderSize < minSliderSize) || (sliderSize == this.sliderSize)) {
      return;
    }
    int old = this.sliderSize;
    this.sliderSize = sliderSize;

    setMinimumSize(new Dimension(minBarWidth + sliderSize, (int)Math.floor(2 * sliderSize + this.barHeight - 2.0D * this.overlap)));
    repaint();

    firePropertyChange("sliderSize", old, sliderSize);
  }

  public synchronized int getBarHeight()
  {
    return this.barHeight;
  }

  public synchronized void setBarHeight(int barHeight)
  {
    if ((barHeight < minBarHeight) || (barHeight == this.barHeight)) {
      return;
    }
    int old = this.barHeight;
    this.barHeight = barHeight;

    setSize();
    repaint();

    firePropertyChange("barHeight", old, barHeight);
  }

  public synchronized double getGranularity()
  {
    return this.granularity;
  }

  public synchronized void setGranularity(double granularity)
  {
    if (granularity == this.granularity) {
      return;
    }
    double oldValue = this.granularity;
    this.granularity = granularity;

    firePropertyChange("granularity", oldValue, granularity);
  }

  public synchronized void setTicks(double[] ticks)
  {
    this.ticks.clear();

    int i = 0; for (int size = ticks.length; i < size; i++) {
      this.ticks.add(new Double(ticks[i]));
    }
    Collections.sort(this.ticks);
    repaint();
  }

  public synchronized double[] getTicks()
  {
    double[] ticks = new double[this.ticks.size()];
    int i = 0;

    for (Double d : this.ticks) {
      ticks[(i++)] = d.doubleValue();
    }
    return ticks;
  }

  public synchronized void addActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
  }

  public synchronized void removeActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
  }

  public synchronized Color getBeliefFillColor()
  {
    return this.beliefFillColor;
  }

  public synchronized void setBeliefFillColor(Color beliefFillColor)
  {
    if (beliefFillColor == null) {
      throw new NullPointerException("Color must not be null.");
    }
    if (this.beliefFillColor == beliefFillColor) {
      return;
    }
    Color old = this.beliefFillColor;
    this.beliefFillColor = beliefFillColor;
    repaint();

    firePropertyChange("beliefFillColor", old, beliefFillColor);
  }

  public synchronized Color getUncertaintyFillColor()
  {
    return this.uncertaintyFillColor;
  }

  public synchronized void setUncertaintyFillColor(Color uncertaintyFillColor)
  {
    if (uncertaintyFillColor == null) {
      throw new NullPointerException("Color must not be null.");
    }
    if (this.uncertaintyFillColor == uncertaintyFillColor) {
      return;
    }
    Color old = this.uncertaintyFillColor;
    this.uncertaintyFillColor = uncertaintyFillColor;
    repaint();

    firePropertyChange("uncertaintyFillColor", old, uncertaintyFillColor);
  }

  public synchronized boolean isPaintTicks()
  {
    return this.paintTicks;
  }

  public synchronized void setPaintTicks(boolean paintTicks)
  {
    if (this.paintTicks == paintTicks) {
      return;
    }
    this.paintTicks = paintTicks;

    repaint();

    firePropertyChange("paintTicks", !paintTicks, paintTicks);
  }

  public synchronized boolean isSnapToTicks()
  {
    return this.snapToTicks;
  }

  public synchronized void setSnapToTicks(boolean snapToTicks)
  {
    if (this.snapToTicks == snapToTicks) {
      return;
    }
    this.snapToTicks = snapToTicks;
    firePropertyChange("snapToTicks", !snapToTicks, snapToTicks);
  }

  public synchronized SubjectiveOpinion getOpinion()
  {
    return this.opinion;
  }

  public synchronized void setOpinion(SubjectiveOpinion opinion)
  {
    if (opinion == null) {
      throw new NullPointerException("Opinion must not be null");
    }
    SubjectiveOpinion old = this.opinion;

    if (old != null) {
      old.removePropertyChangeListener(this.listener);
    }
    this.opinion = opinion;

    opinion.addPropertyChangeListener(this.listener);

    firePropertyChange("opinion", old, opinion);

    repaint();
  }

  public synchronized double getAtomicity()
  {
    return this.opinion.getAtomicity();
  }

  public synchronized void setAtomicity(double atomicity)
  {
    if (atomicity == this.opinion.getAtomicity()) {
      return;
    }
    double old = this.opinion.getAtomicity();
    this.opinion.setAtomicity(atomicity);
    repaint();

    firePropertyChange("atomicity", old, atomicity);
  }

  public synchronized RenderStyle getRenderStyle()
  {
    return this.renderStyle;
  }

  public synchronized void setRenderStyle(RenderStyle renderStyle)
  {
    if (renderStyle == this.renderStyle) {
      return;
    }
    RenderStyle old = this.renderStyle;
    this.renderStyle = renderStyle;

    repaint();

    firePropertyChange("renderStyle", old, renderStyle);
  }

  public synchronized boolean isConstrainUncertaintySlider()
  {
    return this.constrainUncertaintySlider;
  }

  public synchronized void setConstrainUncertaintySlider(boolean constrainUncertaintySlider)
  {
    if (this.constrainUncertaintySlider == constrainUncertaintySlider) {
      return;
    }
    this.constrainUncertaintySlider = constrainUncertaintySlider;

    repaint();

    firePropertyChange("constrainUncertaintySlider", !constrainUncertaintySlider, constrainUncertaintySlider);
  }

  public synchronized boolean isShowHandlesWhenDisabled()
  {
    return this.showHandlesWhenDisabled;
  }

  public synchronized void setShowHandlesWhenDisabled(boolean showSliderWhenDisabled)
  {
    if (this.showHandlesWhenDisabled == showSliderWhenDisabled) {
      return;
    }
    this.showHandlesWhenDisabled = showSliderWhenDisabled;
    repaint();

    firePropertyChange("showSliderWhenDisabled", !showSliderWhenDisabled, showSliderWhenDisabled);
  }

  protected void setSize()
  {
    Dimension dim;
    Dimension dim;
    if ((isEnabled()) || (isShowHandlesWhenDisabled())) {
      dim = new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(2 * this.sliderSize + this.barHeight - 2.0D * this.overlap));
    }
    else
    {
      Dimension dim;
      if ((isPaintTicks()) && (getTicks().length > 0))
        dim = new Dimension(minBarWidth, this.barHeight + (this.tickHeight + this.tickBuffer) * 2);
      else
        dim = new Dimension(minBarWidth, this.barHeight);
    }
    setMinimumSize(dim);
  }

  protected abstract void paintBar(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    Paint oldPaint = g2.getPaint();
    Color oldColor = g2.getColor();

    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    this.overlap = (Math.min(this.sliderSize, this.barHeight) / 2.0D);

    float scale = 24 * this.sliderSize / 35;
    float factor = 11.0F * scale / 24.0F;

    float w = getWidth();

    if ((isEnabled()) || (isShowHandlesWhenDisabled())) {
      w -= Math.round(2.0F * factor + 0.5F);
    }
    float h = this.barHeight;
    float bh2 = h / 2.0F;

    synchronized (this)
    {
      try
      {
        float x0 = 0.0F;

        if ((isEnabled()) || (isShowHandlesWhenDisabled())) {
          x0 += factor;
        }
        float y0 = getHeight() - this.barHeight / 2.0F;

        this.bar = new Rectangle2D.Double(x0, y0, w, h);

        Color barBackgroundColor = this.barBackgroundColor == null ? getBackground() : this.barBackgroundColor;

        if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
        {
          Rectangle2D bar1 = new Rectangle2D.Double(this.bar.getX(), this.bar.getY(), w, bh2);
          Rectangle2D bar2 = new Rectangle2D.Double(this.bar.getX(), this.bar.getMaxY() - bh2, w, bh2);

          GradientPaint gradBar1 = new GradientPaint((float)bar1.getX(), (float)bar1.getY(), SystemColor.controlShadow, 
            (float)bar1.getX(), (float)bar1.getMaxY(), barBackgroundColor);
          GradientPaint gradBar2 = new GradientPaint((float)bar2.getX(), (float)bar2.getY(), barBackgroundColor, 
            (float)bar2.getX(), (float)bar2.getMaxY(), SystemColor.controlLtHighlight);

          g2.setPaint(gradBar1);
          g2.fill(bar1);

          g2.setPaint(gradBar2);
          g2.fill(bar2);
        }
        else
        {
          g2.setColor(barBackgroundColor);
          g2.fill(this.bar);
        }

        double lineY1 = this.bar.getMinY() - this.tickBuffer;
        double lineY2 = this.bar.getMaxY() + this.tickBuffer;

        g2.setColor(SystemColor.controlShadow);

        if (isPaintTicks()) {
          for (Double d : this.ticks)
          {
            double lineX = d.doubleValue() * this.bar.getWidth() + this.bar.getX();

            Line2D lineA = new Line2D.Double(lineX, lineY1, lineX, lineY1 - this.tickHeight);
            g2.draw(lineA);

            Line2D lineB = new Line2D.Double(lineX, lineY2, lineX, lineY2 + this.tickHeight);
            g2.draw(lineB);
          }

        }

        paintBar(g2, x0, y0, w, h, bh2, scale);

        if (this.barBorder != null) {
          this.barBorder.paintBorder(this, g, (int)x0, (int)y0, (int)w + 1, (int)h + 1);
        }

        if ((isEnabled()) || (isShowHandlesWhenDisabled())) {
          this.bottomHandle
            .paintHandle(g2, this.renderStyle, this.sliderSize, isEnabled() ? getForeground() : SystemColor.controlShadow);
        }

        if ((isEnabled()) || (isShowHandlesWhenDisabled()))
          this.topHandle.paintHandle(g2, this.renderStyle, this.sliderSize, isEnabled() ? getForeground() : SystemColor.controlShadow);
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      finally
      {
        g2.setPaint(oldPaint);
        g2.setColor(oldColor);
      }
    }
  }

  public synchronized Border getBarBorder()
  {
    return this.barBorder;
  }

  public synchronized void setBarBorder(Border barBorder)
  {
    if (this.barBorder == barBorder) {
      return;
    }
    Border old = this.barBorder;
    this.barBorder = barBorder;

    repaint();

    firePropertyChange("barBorder", old, barBorder);
  }

  public synchronized Color getBarBackgroundColor()
  {
    return this.barBackgroundColor;
  }

  public synchronized void setBarBackgroundColor(Color barBackgroundColor)
  {
    if (this.barBackgroundColor == barBackgroundColor) {
      return;
    }
    Color old = barBackgroundColor;
    this.barBackgroundColor = barBackgroundColor;

    repaint();

    firePropertyChange("barBackgroundColor", old, barBackgroundColor);
  }
}