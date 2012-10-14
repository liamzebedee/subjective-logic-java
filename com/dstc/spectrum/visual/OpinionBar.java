package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.Opinion;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public abstract class OpinionBar extends OpinionVisualization
{
  protected static int minBarHeight = 4;

  protected static int minBarWidth = 30;

  private boolean showAtomicity = false;

  private boolean showExpectation = false;

  private TickStyle tickStyle = TickStyle.None;

  private double scale = 1.0D;

  private int tickSpaces = 10;
  private static final int tickHeight = 3;
  private static final int tickBuffer = 2;
  private int barHeight = 10;

  private Color barBackgroundColor = null;

  private Border barBorder = BorderFactory.createBevelBorder(1);

  public OpinionBar()
  {
    initialize();
  }

  private void initialize()
  {
    setMinimumSize(new Dimension(minBarWidth + 1, this.barHeight + 1));
  }

  protected abstract void paintOpinion(Graphics paramGraphics, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, RenderStyle paramRenderStyle);

  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    Paint oldPaint = g2.getPaint();
    Color oldColor = g2.getColor();
    try
    {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      synchronized (this)
      {
        float x0 = 0.0F;
        float y0 = getHeight() - this.barHeight / 2.0F;

        float w = getWidth() - 1;
        float h = this.barHeight;

        if (this.tickStyle != TickStyle.None)
        {
          FontMetrics fm = g2.getFontMetrics();

          if (this.tickStyle != TickStyle.None)
          {
            x0 += 20.0F;
            w -= 40.0F;
          }

          float fh = this.tickStyle != TickStyle.None ? fm.getHeight() : 0;
          float tickY = 0.0F;
          float textY = 0.0F;

          h -= 5.0F + fh;

          if (this.tickStyle == TickStyle.Above)
          {
            y0 += 5.0F + fh;
            tickY += fh;
            textY += fm.getAscent();
          }
          else if (this.tickStyle == TickStyle.Below)
          {
            tickY += h + 2.0F;
            textY = tickY + 3.0F + fm.getLeading() + fm.getAscent();
          }

          int i = 0; for (int size = this.tickSpaces; i <= size; i++)
          {
            float tickX = x0 + i * (w / size);

            GeneralPath line = new GeneralPath();
            line.moveTo(tickX, tickY);
            line.lineTo(tickX, tickY + 3.0F);

            g2.setColor(SystemColor.controlShadow);
            g2.draw(line);

            if (this.tickStyle != TickStyle.None)
            {
              double value = i * this.scale / size;
              String text;
              String text;
              if (Math.floor(value) == value)
                text = String.valueOf((int)value);
              else {
                text = String.valueOf(value);
              }
              float fw = fm.stringWidth(text);
              float textX = tickX - fw / 2.0F;

              g2.setColor(getForeground());
              g2.drawString(text, textX, textY);
            }
          }
        }

        RenderStyle renderStyle = getRenderStyle();
        Rectangle2D bar = new Rectangle2D.Double(x0, y0, w, h);

        Color barBackgroundColor = this.barBackgroundColor == null ? getBackground() : this.barBackgroundColor;

        if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
        {
          float bh2 = h / 2.0F;

          Rectangle2D bar1 = new Rectangle2D.Double(bar.getX(), bar.getY(), w, bh2);
          Rectangle2D bar2 = new Rectangle2D.Double(bar.getX(), bar.getMaxY() - bh2, w, bh2);

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
          g2.fill(bar);
        }

        if (this.opinion != null)
        {
          paintOpinion(g, x0, y0, w, h, renderStyle);

          if (isShowAtomicity())
          {
            float atomicity = x0 + w * (float)this.opinion.getAtomicity();

            GeneralPath line = new GeneralPath();
            line.moveTo(atomicity, y0);
            line.lineTo(atomicity, y0 + h);

            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(1.5F, 0, 2));
            g2.draw(line);
          }

          if (isShowExpectation())
          {
            float expectation = x0 + w * (float)this.opinion.getExpectation();

            GeneralPath line = new GeneralPath();
            line.moveTo(expectation, y0 + 1.0F);
            line.lineTo(expectation, y0 + h - 2.0F);

            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(1.5F, 2, 2));
            g2.draw(line);
          }

        }

        if (this.barBorder != null)
          this.barBorder.paintBorder(this, g, (int)x0, (int)y0, (int)w, (int)h + 1);
      }
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

  public synchronized boolean isShowAtomicity()
  {
    return this.showAtomicity;
  }

  public synchronized void setShowAtomicity(boolean showAtomicity)
  {
    if (showAtomicity == this.showAtomicity)
    {
      this.showAtomicity = showAtomicity;
    }repaint();

    firePropertyChange("showAtomicity", !showAtomicity, showAtomicity);
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

  public synchronized int getTickSpaces()
  {
    return this.tickSpaces;
  }

  public synchronized void setTickSpaces(int tickSpaces)
  {
    if (tickSpaces == this.tickSpaces) {
      return;
    }
    int old = this.tickSpaces;
    this.tickSpaces = tickSpaces;
    repaint();

    firePropertyChange("tickSpaces", old, tickSpaces);
  }

  public synchronized double getScale()
  {
    return this.scale;
  }

  public synchronized void setScale(double tickScale)
  {
    if (tickScale == this.scale) {
      return;
    }
    double old = this.scale;
    this.scale = tickScale;

    repaint();

    firePropertyChange("scale", old, this.scale);
  }

  public synchronized TickStyle getTickStyle()
  {
    return this.tickStyle;
  }

  public synchronized void setTickStyle(TickStyle tickStyle)
  {
    if (tickStyle == this.tickStyle) {
      return;
    }
    TickStyle old = this.tickStyle;
    this.tickStyle = tickStyle;
    repaint();

    firePropertyChange("tickStyle", old, tickStyle);
  }

  public synchronized int getBarHeight()
  {
    return this.barHeight;
  }

  public synchronized void setBarHeight(int barHeight)
  {
    if (barHeight == this.barHeight) {
      return;
    }
    int old = this.barHeight;
    this.barHeight = barHeight;

    setMinimumSize(new Dimension(minBarWidth + 1, barHeight + 1));
    repaint();

    firePropertyChange("barHeight", old, barHeight);
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

  static enum TickStyle
  {
    None, Above, Below;
  }
}