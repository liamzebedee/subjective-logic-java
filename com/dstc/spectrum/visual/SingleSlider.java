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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class SingleSlider extends JComponent
{
  private ActionListener actionListener = null;
  private static final long serialVersionUID = 3257005462506779703L;
  private static final double TOLERANCE = 10000000.0D;
  private static int minSliderHeight = 3;

  private static int minBarHeight = 4;

  private static int minBarWidth = 30;

  private int tickHeight = 8;

  private double value = 0.0D;

  private int sliderSize = 22;

  private float scale = 24 * this.sliderSize / 35;

  private float factor = 11.0F * this.scale / 24.0F;

  private float gradScale = this.scale / 2.0F;

  private int barHeight = 10;

  private SliderHandle handle = new SliderHandle(SliderHandle.HandleType.Lower);
  private Rectangle2D bar;
  private Color barFillColor = new Color(0, 0, 204);

  private Color barBackgroundColor = null;

  private double overlap = 0.0D;

  private double minValue = 0.0D;

  private double range = 1.0D;

  private double granularity = 0.01D;

  private boolean snapToTicks = false;

  private boolean paintTicks = true;

  private RenderStyle renderStyle = RenderStyle.Raised;

  private Border barBorder = BorderFactory.createBevelBorder(1);

  protected List<Double> ticks = new ArrayList();

  public SingleSlider()
  {
    initialize();

    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  private void initialize()
  {
    setSize(200, 40);
    setPreferredSize(new Dimension(minBarWidth * 6 + this.sliderSize, (int)Math.floor(this.sliderSize + this.barHeight - this.overlap)));
    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(this.sliderSize + this.barHeight - this.overlap)));
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

    this.overlap = (Math.min(this.sliderSize, this.barHeight) / 2.0D);
    Rectangle2D barFilled;
    float valueX;
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

          GradientPaint gradBar1 = new GradientPaint((float)bar1.getX(), (float)bar1.getY(), barBackgroundColor, (float)bar1.getX(), 
            (float)bar1.getMaxY(), getBackground());
          GradientPaint gradBar2 = new GradientPaint((float)bar2.getX(), (float)bar2.getY(), getBackground(), 
            (float)bar2.getX(), (float)bar2.getMaxY(), barBackgroundColor);

          g2.setPaint(gradBar1);
          g2.fill(bar1);
        }
        else
        {
          g2.setColor(barBackgroundColor);
          g2.fill(this.bar);
        }

        if (isPaintTicks())
        {
          double lineY = this.bar.getMaxY() + this.handle.getType() == SliderHandle.HandleType.Lower ? 3 : -3;

          g2.setColor(SystemColor.controlShadow);

          double[] arrayOfDouble = getTicks(); int i = 0; for (int j = arrayOfDouble.length; i < j; i++) { Double d = Double.valueOf(arrayOfDouble[i]);

            double lineX = (d.doubleValue() - this.minValue) / this.range * this.bar.getWidth() + this.bar.getX();
            Line2D line;
            Line2D line;
            if (this.handle.getType() == SliderHandle.HandleType.Lower)
              line = new Line2D.Double(lineX, lineY, lineX, lineY + this.tickHeight);
            else {
              line = new Line2D.Double(lineX, lineY, lineX, lineY - this.tickHeight);
            }
            g2.draw(line);
          }
        }

        float barWidth = (float)this.bar.getWidth();

        float valueX = barWidth * (float)((getValue() - this.minValue) / this.range);

        float handleY = (float)(this.bar.getMaxY() - this.overlap);
        this.handle.setLocation((float)this.bar.getX() + valueX, handleY);

        float filledx = this.factor;

        Rectangle2D barFilled = new Rectangle2D.Float(filledx, (float)y0 + this.scale, valueX, (float)this.bar.getHeight());

        if (this.renderStyle == RenderStyle.Raised)
        {
          Rectangle2D barFilled1 = new Rectangle2D.Double(filledx, (float)y0 + this.scale, valueX, (float)(this.bar.getHeight() / 2.0D));
          Rectangle2D barFilled2 = new Rectangle2D.Double(filledx, (float)y0 + this.scale + (float)(this.bar.getHeight() / 2.0D), valueX, 
            (float)(this.bar.getHeight() / 2.0D));

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

        if (this.barBorder != null) {
          this.barBorder.paintBorder(this, g, (int)this.factor, (int)(y0 + this.scale), (int)w + 1, this.barHeight);
        }

        this.handle.paintHandle(g2, this.renderStyle, this.sliderSize, isEnabled() ? getForeground() : SystemColor.controlShadow);
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
      (int)Math.floor(this.sliderSize + barHeight - this.overlap)));
    setMinimumSize(new Dimension(minBarWidth + this.sliderSize, (int)Math.floor(this.sliderSize + barHeight - this.overlap)));

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

  public synchronized void setGranularity(double granularity)
  {
    if (granularity == this.granularity) {
      return;
    }
    double oldValue = this.granularity;
    this.granularity = granularity;
    firePropertyChange("granularity", oldValue, granularity);

    setValue(this.value);
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

  public synchronized double getValue()
  {
    return this.value;
  }

  public synchronized void setValue(double value)
  {
    if (value < this.minValue) {
      value = this.minValue;
    }
    if (value > this.minValue + this.range) {
      value = this.minValue + this.range;
    }
    if (this.granularity > 0.0D) {
      value = Math.floor(value / this.granularity) * this.granularity;
    }
    if (value == this.value) {
      return;
    }
    double oldvalue = this.value;

    this.value = (Math.floor(value * 10000000.0D) / 10000000.0D);

    repaint();

    firePropertyChange("value", oldvalue, value);
  }

  public synchronized void addActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
  }

  public synchronized void removeActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
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

  public synchronized boolean isPaintTicks()
  {
    return this.paintTicks;
  }

  public synchronized boolean isSnapToTicks()
  {
    return this.snapToTicks;
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

  public synchronized void setSnapToTicks(boolean snapToTicks)
  {
    if (this.snapToTicks == snapToTicks) {
      return;
    }
    this.snapToTicks = snapToTicks;
    firePropertyChange("snapToTicks", !snapToTicks, snapToTicks);
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

  public synchronized void setTicks(double[] ticks)
  {
    this.ticks.clear();

    int i = 0; for (int size = ticks.length; i < size; i++) {
      this.ticks.add(new Double(ticks[i]));
    }
    Collections.sort(this.ticks);
    repaint();
  }

  public SliderHandle.HandleType getHandleType()
  {
    return this.handle.getType();
  }

  public void setHandleType(SliderHandle.HandleType type)
  {
    SliderHandle.HandleType old = this.handle.getType();

    if (old == type) {
      return;
    }
    this.handle.setType(type);

    repaint();

    firePropertyChange("handleType", old, type);
  }

  private class MyMouseListener extends MouseAdapter
    implements MouseMotionListener
  {
    private boolean dragging = false;

    private boolean dragged = false;

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

      if ((!SingleSlider.this.isEnabled()) || (SingleSlider.this.handle == null)) {
        return;
      }
      Point p = e.getPoint();

      if ((SingleSlider.this.handle.contains(p)) && (SingleSlider.this.isEnabled()))
      {
        this.dragging = true;
        this.offsetX = (SingleSlider.this.handle.getPoint().getX() - p.getX());
      }
      else
      {
        SliderHandle h = null;
        this.offsetX = 0.0D;

        if (p.getY() >= SingleSlider.this.bar.getMaxY() - SingleSlider.this.overlap) {
          h = SingleSlider.this.handle;
        }
        if ((h != null) && (SingleSlider.this.isEnabled()))
        {
          double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - SingleSlider.this.bar.getX()) / SingleSlider.this.bar.getWidth())) * SingleSlider.this.range + SingleSlider.this.minValue;

          if (h == SingleSlider.this.handle) {
            SingleSlider.this.setValue(getClosest(value, SingleSlider.this.ticks));
          }
          SingleSlider.this.setToolTipText(String.valueOf(SingleSlider.this.getValue()));

          if (SingleSlider.this.actionListener != null)
            SingleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, "handle"));
        }
      }
    }

    public void mouseDragged(MouseEvent e)
    {
      if ((SingleSlider.this.isEnabled()) && (this.dragging) && (SingleSlider.this.isEnabled()))
      {
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - SingleSlider.this.bar.getX()) / SingleSlider.this.bar.getWidth()));

        if (SingleSlider.this.isSnapToTicks())
          value = getClosest(value * SingleSlider.this.range + SingleSlider.this.minValue, SingleSlider.this.ticks);
        else {
          value = value * SingleSlider.this.range + SingleSlider.this.minValue;
        }
        SingleSlider.this.setValue(value);

        this.dragged = true;

        SingleSlider.this.setToolTipText(String.valueOf(SingleSlider.this.getValue()));

        if (SingleSlider.this.actionListener != null)
          SingleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, "handle"));
      }
    }

    public void mouseMoved(MouseEvent e)
    {
      SliderHandle h = null;
      Point p = e.getPoint();

      if (SingleSlider.this.handle.contains(p)) {
        h = SingleSlider.this.handle;
      }
      if (h != null)
      {
        SingleSlider.this.setToolTipText(String.valueOf(SingleSlider.this.getValue()));

        if (SingleSlider.this.isEnabled())
          SingleSlider.this.setCursor(new Cursor(12));
      }
      else
      {
        SingleSlider.this.setToolTipText(null);
        SingleSlider.this.setCursor(new Cursor(0));
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      if ((this.dragging) && (this.dragged))
      {
        this.dragging = false;
        double value = Math.min(1.0D, Math.max(0.0D, (e.getX() - this.offsetX - SingleSlider.this.bar.getX()) / SingleSlider.this.bar.getWidth()));

        synchronized (this.parent)
        {
          if (SingleSlider.this.isSnapToTicks())
            value = getClosest(value * SingleSlider.this.range + SingleSlider.this.minValue, SingleSlider.this.ticks);
          else {
            value = value * SingleSlider.this.range + SingleSlider.this.minValue;
          }
        }
        SingleSlider.this.setValue(value);

        SingleSlider.this.setToolTipText(String.valueOf(SingleSlider.this.getValue()));

        if (SingleSlider.this.actionListener != null)
          SingleSlider.this.actionListener.actionPerformed(new ActionEvent(this, 1001, "handle"));
      }
    }

    public boolean isDragging()
    {
      return this.dragging;
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
  }
}