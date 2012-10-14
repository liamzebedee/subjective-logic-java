package com.dstc.spectrum.visual;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class DoubleSlider extends JComponent
{
  private ActionListener actionListener = null;
  private static final long serialVersionUID = 3257005462506779703L;
  private static final double TOLERANCE = 10000000.0D;
  private static int minSliderHeight = 3;

  private static int minBarHeight = 4;

  private static int minBarWidth = 30;

  private int tickHeight = 8;

  private int sliderSize = 22;

  private float scale = 24 * this.sliderSize / 35;

  private float factor = 11.0F * this.scale / 24.0F;

  private float gradScale = this.scale / 2.0F;

  private int barHeight = 10;

  private Handle lower = new Handle(SliderHandle.HandleType.Lower);

  private Handle upper = new Handle(SliderHandle.HandleType.Upper);
  private Rectangle2D bar;
  private Color barFillColor = new Color(0, 0, 204);

  private Color barCommonFillColor = Color.GREEN;

  private Color barBackgroundColor = null;

  private boolean paintCommonRange = false;

  private double overlap = 0.0D;

  private double minValue = 0.0D;

  private double range = 1.0D;

  private double granularity = 0.01D;

  private double minCommonRange = 0.0D;

  private double maxCommonRange = 0.0D;

  private RenderStyle renderStyle = RenderStyle.Raised;

  private Border barBorder = BorderFactory.createBevelBorder(1);

  public DoubleSlider()
  {
    initialize();

    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  private void initialize()
  {
    setSize(200, 40);
    setPreferredSize(new Dimension(minBarWidth * 6 + this.sliderSize, 
      (int)Math.floor(2 * this.sliderSize + this.barHeight - 2.0D * this.overlap)));
    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(2 * this.sliderSize + this.barHeight - 2.0D * this.overlap)));
  }

  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    Rectangle2D bar31 = null;
    Rectangle2D bar32 = null;
    this.overlap = (Math.min(this.sliderSize, this.barHeight) / 2.0D);

    GradientPaint gradBar31 = null;
    GradientPaint gradBar32 = null;

    List upperTickLines = new ArrayList();
    List lowerTickLines = new ArrayList();
    Rectangle2D barFilled;
    float maxCommonX;
    float minCommonX;
    float upperX;
    float lowerX;
    synchronized (this)
    {
      double y0 = (getHeight() - 2 * this.sliderSize + this.barHeight - 2.0D * this.overlap) / 2.0D;
      float w = getWidth() - Math.round(2.0F * this.factor + 0.5F);

      Paint oldPaint = g2.getPaint();
      Color oldColor = g2.getColor();
      try
      {
        this.bar = new Rectangle2D.Float(this.factor, (float)y0 + this.scale, w, this.barHeight);

        Color barBackgroundColor = this.barBackgroundColor == null ? getBackground() : this.barBackgroundColor;

        if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
        {
          Rectangle2D bar1 = new Rectangle2D.Double(this.bar.getX(), this.bar.getY(), this.bar.getWidth(), this.bar.getHeight() / 2.0D);
          Rectangle2D bar2 = new Rectangle2D.Double(this.bar.getX(), this.bar.getMaxY() - this.bar.getHeight() / 2.0D, this.bar.getWidth(), 
            this.bar.getHeight() / 2.0D);

          GradientPaint gradBar1 = new GradientPaint((float)bar1.getX(), (float)bar1.getY(), SystemColor.controlShadow, 
            (float)bar1.getX(), (float)bar1.getMaxY(), barBackgroundColor);
          GradientPaint gradBar2 = new GradientPaint((float)bar2.getX(), (float)bar2.getY(), barBackgroundColor, 
            (float)bar2.getX(), (float)bar2.getMaxY(), SystemColor.controlLtHighlight);

          g2.setPaint(gradBar1);
          g2.fill(bar1);
        }
        else
        {
          g2.setColor(barBackgroundColor);
          g2.fill(this.bar);
        }

        double lineY1 = this.bar.getMinY() - 3.0D;
        double lineY2 = this.bar.getMaxY() + 3.0D;

        g2.setColor(SystemColor.controlShadow);

        if (this.upper.isPaintTicks()) {
          for (Double d : this.upper.getTicks())
          {
            double lineX = (d.doubleValue() - this.minValue) / this.range * this.bar.getWidth() + this.bar.getX();
            Line2D line = new Line2D.Double(lineX, lineY1, lineX, lineY1 - this.tickHeight);
            g2.draw(line);
          }
        }
        if (this.lower.isPaintTicks()) {
          for (Double d : this.lower.getTicks())
          {
            double lineX = (d.doubleValue() - this.minValue) / this.range * this.bar.getWidth() + this.bar.getX();
            Line2D line = new Line2D.Double(lineX, lineY2, lineX, lineY2 + this.tickHeight);
            g2.draw(line);
          }
        }

        float barWidth = (float)this.bar.getWidth();

        float lowerX = barWidth * (float)((this.lower.getValue() - this.minValue) / this.range);
        float upperX = barWidth * (float)((this.upper.getValue() - this.minValue) / this.range);

        float minCommonX = barWidth * (float)((this.minCommonRange - this.minValue) / this.range);
        float maxCommonX = barWidth * (float)((this.maxCommonRange - this.minValue) / this.range);

        float handleY = (float)(this.bar.getMaxY() - this.overlap);
        this.upper.setLocation((float)this.bar.getX() + upperX, handleY - this.scale);
        this.lower.setLocation((float)this.bar.getX() + lowerX, handleY);

        float filledx = this.factor + Math.min(upperX, lowerX);

        Rectangle2D barFilled = new Rectangle2D.Float(filledx, (float)y0 + this.scale, Math.abs(upperX - lowerX), 
          (float)this.bar.getHeight());

        if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
        {
          Rectangle2D barFilled1 = new Rectangle2D.Double(filledx, (float)y0 + this.scale, Math.abs(upperX - lowerX), (float)(
            this.bar.getHeight() / 2.0D));
          Rectangle2D barFilled2 = new Rectangle2D.Double(filledx, (float)y0 + this.scale + (float)(this.bar.getHeight() / 2.0D), 
            Math.abs(upperX - lowerX), (float)(this.bar.getHeight() / 2.0D));

          GradientPaint gradBarFilled1 = new GradientPaint(filledx, (float)this.bar.getY(), SystemColor.controlLtHighlight, filledx, 
            (float)(this.bar.getY() + this.bar.getHeight() / 2.0D), this.barFillColor);
          GradientPaint gradBarFilled2 = new GradientPaint(filledx, (float)(this.bar.getY() + this.bar.getHeight() / 2.0D), this.barFillColor, 
            filledx, (float)this.bar.getMaxY(), SystemColor.controlShadow);

          g2.setPaint(gradBarFilled1);
          g2.fill(barFilled1);

          g2.setPaint(gradBarFilled2);
          g2.fill(barFilled2);
        }
        else
        {
          g2.setColor(this.barFillColor);
          g2.fill(barFilled);
        }

        if ((isPaintCommonRange()) && (this.minCommonRange != this.maxCommonRange) && (Math.min(lowerX, upperX) <= minCommonX) && 
          (Math.max(lowerX, upperX) >= maxCommonX))
        {
          float pMin = this.factor + minCommonX;
          float pMax = this.factor + maxCommonX;

          Rectangle2D barCommon = new Rectangle2D.Double(pMin, (float)y0 + this.scale, pMax - pMin, (float)this.bar.getHeight());

          if ((this.renderStyle == RenderStyle.Raised) || (this.renderStyle == RenderStyle.Lowered))
          {
            bar31 = new Rectangle2D.Double(pMin, (float)y0 + this.scale, pMax - pMin, (float)(this.bar.getHeight() / 2.0D));
            bar32 = new Rectangle2D.Double(pMin, (float)y0 + this.scale + (float)(this.bar.getHeight() / 2.0D), pMax - pMin, 
              (float)(this.bar.getHeight() / 2.0D));

            gradBar31 = new GradientPaint(pMin, (float)this.bar.getY(), SystemColor.controlLtHighlight, pMin, 
              (float)(this.bar.getY() + this.bar.getHeight() / 2.0D), this.barCommonFillColor);
            gradBar32 = new GradientPaint(pMin, (float)(this.bar.getY() + this.bar.getHeight() / 2.0D), this.barCommonFillColor, 
              pMin, (float)this.bar.getMaxY(), SystemColor.controlShadow);

            g2.setPaint(gradBar31);
            g2.fill(bar31);

            g2.setPaint(gradBar32);
            g2.fill(bar32);
          }
          else
          {
            g2.setColor(this.barCommonFillColor);
            g2.fill(barCommon);
          }

        }

        if (this.barBorder != null) {
          this.barBorder.paintBorder(this, g, (int)this.factor, (int)(y0 + this.scale), (int)w + 1, this.barHeight);
        }

        this.lower.paintHandle(g2, this.renderStyle, this.sliderSize, (this.lower.isEnabled()) && (isEnabled()) ? getForeground() : 
          SystemColor.controlShadow);

        this.upper.paintHandle(g2, this.renderStyle, this.sliderSize, (this.upper.isEnabled()) && (isEnabled()) ? getForeground() : 
          SystemColor.controlShadow);
      }
      catch (RuntimeException e)
      {
        throw e;
      }
      finally
      {
        g2.setPaint(oldPaint);
        g2.setColor(oldColor);
      }
    }
  }

  public synchronized int getSliderSize()
  {
    return this.sliderSize;
  }

  public synchronized void setSliderSize(int sliderHeight)
  {
    if ((sliderHeight < minSliderHeight) || (sliderHeight == this.sliderSize)) {
      return;
    }
    int old = this.sliderSize;
    this.sliderSize = sliderHeight;

    this.scale = 24 * this.sliderSize / 35;
    this.factor = (11.0F * this.scale / 24.0F);
    this.gradScale = (this.scale / 2.0F);

    setPreferredSize(new Dimension(minBarWidth * 6 + this.sliderSize, 
      (int)Math.floor((2 * this.sliderSize + this.barHeight - 2.0D * this.overlap) / 2.0D)));
    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, 
      (int)Math.floor((2 * this.sliderSize + this.barHeight - 2.0D * this.overlap) / 2.0D)));

    repaint();

    firePropertyChange("sliderSize", old, this.sliderSize);
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

  public synchronized Color getBarFillColor()
  {
    return this.barFillColor;
  }

  public synchronized void setBarFillColor(Color barFillColor)
  {
    if (this.barFillColor == barFillColor) {
      return;
    }
    Color old = barFillColor;
    this.barFillColor = barFillColor;

    repaint();

    firePropertyChange("barFillColor", old, barFillColor);
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

    setPreferredSize(new Dimension(minBarWidth * 6 + this.sliderSize, 
      (int)Math.floor(2 * this.sliderSize + barHeight - 2.0D * this.overlap)));
    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(2 * this.sliderSize + barHeight - 2.0D * this.overlap)));

    repaint();

    firePropertyChange("barHeight", old, barHeight);
  }

  public synchronized double getGranularity()
  {
    return this.granularity;
  }

  public synchronized double getRange()
  {
    return this.range;
  }

  public synchronized double getMinValue()
  {
    return this.minValue;
  }

  public synchronized double[] getLowerTicks()
  {
    double[] ticks = new double[this.lower.getTicks().size()];
    int i = 0;

    for (Double d : this.lower.getTicks()) {
      ticks[(i++)] = d.doubleValue();
    }
    return ticks;
  }

  public synchronized double getLowerValue()
  {
    return this.lower.getValue();
  }

  public synchronized boolean isLowerPaintTicks()
  {
    return this.lower.isPaintTicks();
  }

  public synchronized boolean isLowerSnapToTicks()
  {
    return this.lower.isSnapToTicks();
  }

  public synchronized void setGranularity(double granularity)
  {
    if (granularity == this.granularity) {
      return;
    }
    double oldValue = this.granularity;
    this.granularity = granularity;
    firePropertyChange("granularity", oldValue, granularity);

    setLowerValue(this.lower.getValue());
    setUpperValue(this.upper.getValue());
  }

  public synchronized void setRange(double range)
  {
    if (range < 0.0D) {
      range = 0.0D;
    }
    if (range == this.range) {
      return;
    }
    double oldValue = range;
    this.range = range;
    repaint();

    firePropertyChange("range", oldValue, range);
  }

  public synchronized void setMinValue(double minValue)
  {
    if (minValue == this.minValue) {
      return;
    }
    double oldValue = minValue;
    this.minValue = minValue;
    repaint();

    firePropertyChange("minValue", oldValue, minValue);
  }

  public synchronized void setLowerPaintTicks(boolean paintTicks)
  {
    if (paintTicks == this.lower.isPaintTicks()) {
      return;
    }
    this.lower.setPaintTicks(paintTicks);
    firePropertyChange("lowerPaintTicks", !paintTicks, paintTicks);
  }

  public synchronized void setLowerSnapToTicks(boolean snapToTicks)
  {
    if (snapToTicks == this.lower.snapToTicks) {
      return;
    }
    this.lower.setSnapToTicks(snapToTicks);
    firePropertyChange("lowerSnapToTicks", !snapToTicks, snapToTicks);
  }

  public synchronized void setLowerTicks(double[] ticks)
  {
    this.lower.getTicks().clear();

    int i = 0; for (int size = ticks.length; i < size; i++) {
      this.lower.getTicks().add(new Double(ticks[i]));
    }
    Collections.sort(this.lower.getTicks());
    repaint();

    firePropertyChange("lowerTicks", null, ticks);
  }

  public synchronized void setLowerValue(double value)
  {
    if (value < this.minValue) {
      value = this.minValue;
    }
    if (value > this.minValue + this.range) {
      value = this.minValue + this.range;
    }
    if (this.granularity > 0.0D) {
      value = Math.round(value / this.granularity) * this.granularity;
    }
    if (value == this.lower.getValue()) {
      return;
    }
    double oldvalue = this.lower.getValue();

    this.lower.setValue(Math.round(value * 10000000.0D) / 10000000.0D);

    repaint();

    firePropertyChange("lowerValue", oldvalue, value);
  }

  public synchronized boolean isLowerEnabled()
  {
    return this.lower.isEnabled();
  }

  public synchronized void setLowerEnabled(boolean enabled)
  {
    if (this.lower.isEnabled() == enabled) {
      return;
    }
    this.lower.setEnabled(enabled);

    repaint();

    firePropertyChange("lowerEnabled", !enabled, enabled);
  }

  public synchronized double[] getUpperTicks()
  {
    double[] ticks = new double[this.upper.getTicks().size()];
    int i = 0;

    for (Double d : this.upper.getTicks()) {
      ticks[(i++)] = d.doubleValue();
    }
    return ticks;
  }

  public synchronized double getUpperValue()
  {
    return this.upper.getValue();
  }

  public synchronized boolean isUpperPaintTicks()
  {
    return this.upper.isPaintTicks();
  }

  public synchronized boolean isUpperSnapToTicks()
  {
    return this.upper.isSnapToTicks();
  }

  public synchronized void setUpperPaintTicks(boolean paintTicks)
  {
    if (paintTicks == this.upper.isPaintTicks()) {
      return;
    }
    this.upper.setPaintTicks(paintTicks);
    firePropertyChange("upperPaintTicks", !paintTicks, paintTicks);
  }

  public synchronized void setUpperSnapToTicks(boolean snapToTicks)
  {
    if (snapToTicks == this.upper.snapToTicks) {
      return;
    }
    this.upper.setSnapToTicks(snapToTicks);
    firePropertyChange("upperSnapToTicks", !snapToTicks, snapToTicks);
  }

  public synchronized void setUpperTicks(double[] ticks)
  {
    this.upper.ticks.clear();

    int i = 0; for (int size = ticks.length; i < size; i++) {
      this.upper.ticks.add(new Double(ticks[i]));
    }
    Collections.sort(this.upper.ticks);
    repaint();

    firePropertyChange("upperTicks", null, ticks);
  }

  public synchronized void setUpperValue(double value)
  {
    if (value < this.minValue) {
      value = this.minValue;
    }
    if (value > this.minValue + this.range) {
      value = this.minValue + this.range;
    }
    if (this.granularity > 0.0D) {
      value = Math.round(value / this.granularity) * this.granularity;
    }
    if (value == this.upper.getValue()) {
      return;
    }
    double oldvalue = this.upper.getValue();

    this.upper.setValue(Math.floor(value * 10000000.0D) / 10000000.0D);

    repaint();

    firePropertyChange("upperValue", oldvalue, value);
  }

  public synchronized boolean isUpperEnabled()
  {
    return this.upper.isEnabled();
  }

  public synchronized void setUpperEnabled(boolean enabled)
  {
    if (this.upper.isEnabled() == enabled) {
      return;
    }
    this.upper.setEnabled(enabled);

    repaint();

    firePropertyChange("upperEnabled", !enabled, enabled);
  }

  public synchronized void addActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
  }

  public synchronized void removeActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
  }

  public synchronized double getMaxCommonRange()
  {
    return this.maxCommonRange;
  }

  public synchronized double getMinCommonRange()
  {
    return this.minCommonRange;
  }

  public synchronized void setCommonRange(double arg0, double arg1)
  {
    double min = Math.floor(Math.min(arg0, arg1) * 10000000.0D) / 10000000.0D;
    double max = Math.floor(Math.max(arg0, arg1) * 10000000.0D) / 10000000.0D;

    if ((min == this.minCommonRange) && (max == this.maxCommonRange)) {
      return;
    }
    this.minCommonRange = min;
    this.maxCommonRange = max;

    repaint();

    firePropertyChange("commonRange", min, max);
  }

  public synchronized Color getBarCommonFillColor()
  {
    return this.barCommonFillColor;
  }

  public synchronized void setBarCommonFillColor(Color barCommonFillColor)
  {
    if (this.barCommonFillColor == barCommonFillColor) {
      return;
    }
    Color old = barCommonFillColor;
    this.barCommonFillColor = barCommonFillColor;

    repaint();

    firePropertyChange("barCommonFillColor", old, barCommonFillColor);
  }

  public synchronized boolean isPaintCommonRange()
  {
    return this.paintCommonRange;
  }

  public synchronized void setPaintCommonRange(boolean paintCommonRange)
  {
    if (paintCommonRange == this.paintCommonRange) {
      return;
    }
    this.paintCommonRange = paintCommonRange;
    repaint();

    firePropertyChange("paintCommonRange", !paintCommonRange, paintCommonRange);
  }

  public synchronized boolean isInCommonRange()
  {
    return (Math.min(this.lower.getValue(), this.upper.getValue()) <= this.minCommonRange) && 
      (Math.max(this.lower.value, this.upper.value) >= this.maxCommonRange);
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

  private class Handle extends SliderHandle
    implements Serializable
  {
    private static final long serialVersionUID = 3905804158425577265L;
    private List<Double> ticks = new ArrayList();

    private double value = 0.0D;

    private boolean snapToTicks = false;

    private boolean paintTicks = true;

    private boolean enabled = true;

    public Handle(SliderHandle.HandleType type)
    {
      super();
    }

    public double getValue()
    {
      return this.value;
    }

    public void setValue(double value)
    {
      this.value = value;
    }

    public boolean isSnapToTicks()
    {
      return this.snapToTicks;
    }

    public void setSnapToTicks(boolean snapToTicks)
    {
      this.snapToTicks = snapToTicks;
    }

    public boolean isPaintTicks()
    {
      return this.paintTicks;
    }

    public void setPaintTicks(boolean paintTicks)
    {
      this.paintTicks = paintTicks;
    }

    public List<Double> getTicks()
    {
      return this.ticks;
    }

    public void setTicks(List<Double> ticks)
    {
      this.ticks = ticks;
    }

    public boolean isEnabled()
    {
      return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
      this.enabled = enabled;
    }
  }

  private class MyMouseListener extends MouseAdapter implements MouseMotionListener
  {
    private boolean dragging = false;

    private boolean dragged = false;

    private DoubleSlider.Handle handle = null;

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

      if ((!DoubleSlider.this.isEnabled()) || (DoubleSlider.this.upper == null) || (DoubleSlider.this.lower == null)) {
        return;
      }
      Point p = e.getPoint();

      if ((DoubleSlider.this.lower.contains(p)) && (DoubleSlider.this.lower.isEnabled()))
      {
        this.dragging = true;
        this.handle = DoubleSlider.this.lower;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else if ((DoubleSlider.this.upper.contains(p)) && (DoubleSlider.access$0(DoubleSlider.this).enabled))
      {
        this.dragging = true;
        this.handle = DoubleSlider.this.upper;
        this.offsetX = (this.handle.getPoint().getX() - p.getX());
      }
      else
      {
        DoubleSlider.Handle h = null;
        this.offsetX = 0.0D;

        if (p.getY() <= DoubleSlider.this.bar.getMinY() + DoubleSlider.this.overlap)
          h = DoubleSlider.this.upper;
        else if (p.getY() >= DoubleSlider.this.bar.getMaxY() - DoubleSlider.this.overlap) {
          h = DoubleSlider.this.lower;
        }
        if ((h != null) && (h.enabled))
        {
          double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - DoubleSlider.this.bar.getX()) / DoubleSlider.this.bar.getWidth())) * DoubleSlider.this.range + DoubleSlider.this.minValue;

          if (h == DoubleSlider.this.upper)
            DoubleSlider.this.setUpperValue(getClosest(value, h.getTicks()));
          else {
            DoubleSlider.this.setLowerValue(getClosest(value, h.getTicks()));
          }
          DoubleSlider.this.setToolTipText(String.valueOf(h.getValue()));

          if (DoubleSlider.this.actionListener != null)
            DoubleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, h == DoubleSlider.this.upper ? "upper" : 
              "lower"));
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
      if ((DoubleSlider.this.isEnabled()) && (this.dragging) && (this.handle != null) && (this.handle.isEnabled()))
      {
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - DoubleSlider.this.bar.getX()) / DoubleSlider.this.bar.getWidth()));

        if (this.handle.isSnapToTicks())
          value = getClosest(value * DoubleSlider.this.range + DoubleSlider.this.minValue, this.handle.getTicks());
        else {
          value = value * DoubleSlider.this.range + DoubleSlider.this.minValue;
        }
        if (this.handle == DoubleSlider.this.upper)
          DoubleSlider.this.setUpperValue(value);
        else {
          DoubleSlider.this.setLowerValue(value);
        }
        this.dragged = true;

        DoubleSlider.this.setToolTipText(String.valueOf(this.handle.getValue()));

        if (DoubleSlider.this.actionListener != null)
          DoubleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, this.handle == DoubleSlider.this.upper ? "upper" : 
            "lower"));
      }
    }

    public void mouseMoved(MouseEvent e)
    {
      DoubleSlider.Handle h = null;
      Point p = e.getPoint();

      if (DoubleSlider.this.lower.contains(p))
        h = DoubleSlider.this.lower;
      else if (DoubleSlider.this.upper.contains(p)) {
        h = DoubleSlider.this.upper;
      }
      if (h != null)
      {
        DoubleSlider.this.setToolTipText(String.valueOf(h.getValue()));

        if (h.isEnabled())
          DoubleSlider.this.setCursor(new Cursor(12));
      }
      else
      {
        DoubleSlider.this.setToolTipText(null);
        DoubleSlider.this.setCursor(new Cursor(0));
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      if ((this.dragging) && (this.dragged))
      {
        this.dragging = false;
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - DoubleSlider.this.bar.getX()) / DoubleSlider.this.bar.getWidth()));

        synchronized (this.parent)
        {
          if (this.handle.isSnapToTicks())
            value = getClosest(value * DoubleSlider.this.range + DoubleSlider.this.minValue, this.handle.getTicks());
          else {
            value = value * DoubleSlider.this.range + DoubleSlider.this.minValue;
          }
        }
        if (this.handle == DoubleSlider.this.upper)
          DoubleSlider.this.setUpperValue(value);
        else {
          DoubleSlider.this.setLowerValue(value);
        }
        DoubleSlider.this.setToolTipText(String.valueOf(this.handle.getValue()));

        if (DoubleSlider.this.actionListener != null)
          DoubleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, this.handle == DoubleSlider.this.upper ? "upper" : 
            "lower"));
      }
    }

    public boolean isDragging()
    {
      return this.dragging;
    }
  }
}