package com.dstc.spectrum.visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;

public class SliderHandle
  implements Serializable
{
  public static final long serialVersionUID = 3905804158425577265L;
  private Point2D point = new Point2D.Float();

  private GeneralPath shape = new GeneralPath();

  private HandleType type = HandleType.Lower;

  public SliderHandle(HandleType type)
  {
    this.type = type;
  }

  public synchronized HandleType getType()
  {
    return this.type;
  }

  public synchronized GeneralPath getShape()
  {
    return this.shape;
  }

  public synchronized Point2D getPoint()
  {
    return this.point;
  }

  protected synchronized void paintHandle(Graphics2D g2, RenderStyle renderStyle, int sliderSize, Color outlineColor)
  {
    if ((g2 == null) || (outlineColor == null)) {
      throw new NullPointerException("Graphics2D and Color must not be null");
    }
    if (sliderSize < 0) {
      sliderSize = 0;
    }
    float scale = 24 * sliderSize / 35;
    float factor = 11.0F * scale / 24.0F;
    float gradScale = scale / 2.0F;
    float x = (float)this.point.getX() - factor;
    float y = (float)this.point.getY();

    this.shape = new GeneralPath();

    if (this.type == HandleType.Lower)
    {
      this.shape.moveTo(x + factor, y);
      this.shape.lineTo(x, y + factor);
      this.shape.lineTo(x, y + scale);
      this.shape.lineTo(x + factor * 2.0F, y + scale);
      this.shape.lineTo(x + factor * 2.0F, y + factor);
      this.shape.closePath();
    }
    else
    {
      this.shape.moveTo(x + factor, y + scale);
      this.shape.lineTo(x, y + scale - factor);
      this.shape.lineTo(x, y);
      this.shape.lineTo(x + factor * 2.0F, y);
      this.shape.lineTo(x + factor * 2.0F, y + scale - factor);
      this.shape.closePath();
    }

    if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
    {
      GeneralPath handle1 = new GeneralPath();
      GradientPaint gradHandle2;
      Rectangle2D handle2;
      GradientPaint gradHandle1;
      GradientPaint gradHandle2;
      if (this.type == HandleType.Lower)
      {
        handle1.moveTo(x + factor, y);
        handle1.lineTo(x, y + factor);
        handle1.lineTo(x, y + gradScale);
        handle1.lineTo(x + factor * 2.0F, y + gradScale);
        handle1.lineTo(x + factor * 2.0F, y + factor);
        handle1.closePath();

        Rectangle2D handle2 = new Rectangle2D.Double(x, y + gradScale, factor * 2.0F, scale - gradScale);

        GradientPaint gradHandle1 = new GradientPaint(x + factor, y, Color.LIGHT_GRAY, x + factor, y + gradScale, 
          SystemColor.controlLtHighlight);

        gradHandle2 = new GradientPaint(x + factor, y + gradScale, SystemColor.controlLtHighlight, x + factor, y + scale, 
          Color.LIGHT_GRAY);
      }
      else
      {
        handle1.moveTo(x + factor, y + scale);
        handle1.lineTo(x, y + factor);
        handle1.lineTo(x, y + gradScale);
        handle1.lineTo(x + factor * 2.0F, y + gradScale);
        handle1.lineTo(x + factor * 2.0F, y + factor);
        handle1.closePath();

        handle2 = new Rectangle2D.Double(x, y, factor * 2.0F, scale - gradScale);

        gradHandle1 = new GradientPaint(x + factor, y + scale, Color.LIGHT_GRAY, x + factor, y + gradScale, 
          SystemColor.controlLtHighlight);

        gradHandle2 = new GradientPaint(x + factor, y, Color.LIGHT_GRAY, x + factor, y + gradScale, 
          SystemColor.controlLtHighlight);
      }

      g2.setPaint(gradHandle1);
      g2.fill(handle1);

      g2.setPaint(gradHandle2);
      g2.fill(handle2);
    }
    else
    {
      g2.setColor(SystemColor.control);
      g2.fill(this.shape);
    }

    g2.setStroke(new BasicStroke(0.75F, 1, 1));

    g2.setColor(outlineColor);
    g2.draw(this.shape);
  }

  public synchronized boolean contains(Point2D p)
  {
    return this.shape.contains(p);
  }

  public synchronized void setLocation(double x, double y)
  {
    this.point.setLocation(x, y);
  }

  public synchronized void setLocation(Point2D p)
  {
    this.point.setLocation(p);
  }

  public synchronized void setType(HandleType type)
  {
    this.type = type;
  }

  public static enum HandleType
  {
    Upper, Lower;
  }
}