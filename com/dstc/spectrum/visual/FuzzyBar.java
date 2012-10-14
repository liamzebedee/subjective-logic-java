package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.BasicFuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzyOpinionSet2D;
import com.dstc.spectrum.opinion.Opinion;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class FuzzyBar extends OpinionBar
{
  private FuzzyOpinionSet fuzzyOpinionSet = new FuzzyOpinionSet2D();

  private String uncertainText = "Uncertain";

  private String polarizedText = "Polarized";

  private String text = "";

  private double polarizationFactor = (0.0D / 0.0D);

  private double certaintyThreshold = (0.0D / 0.0D);

  private Color barFillColor = new Color(0, 0, 204);

  private Color inverseForeground = Color.WHITE;

  private int horizontalAlignment = 0;
  private static final long serialVersionUID = 3691038764164134963L;

  public FuzzyBar()
  {
    this.fuzzyOpinionSet = new BasicFuzzyOpinionSet();
    initialize();
  }

  public FuzzyBar(FuzzyOpinionSet fuzzySet)
  {
    setFuzzyOpinionSet(fuzzySet);
    initialize();
  }

  private void initialize()
  {
    setPreferredSize(new Dimension(40, 16));
    setRenderStyle(RenderStyle.Raised);
  }

  public synchronized double getCertaintyThreshold()
  {
    return this.certaintyThreshold;
  }

  public synchronized void setCertaintyThreshold(double certaintyThreshold)
  {
    if (certaintyThreshold == this.certaintyThreshold) {
      return;
    }
    if (!Double.isNaN(certaintyThreshold)) {
      certaintyThreshold = Math.min(1.0D, Math.max(0.0D, certaintyThreshold));
    }
    this.certaintyThreshold = certaintyThreshold;

    repaint();
  }

  public synchronized FuzzyOpinionSet getFuzzyOpinionSet()
  {
    return this.fuzzyOpinionSet;
  }

  public synchronized void setFuzzyOpinionSet(FuzzyOpinionSet fuzzySet)
  {
    if (fuzzySet == null) {
      throw new NullPointerException("FuzzySet must not be null");
    }
    if (fuzzySet == this.fuzzyOpinionSet) {
      return;
    }
    this.fuzzyOpinionSet = fuzzySet;

    repaint();
  }

  public synchronized double getPolarizationFactor()
  {
    return this.polarizationFactor;
  }

  public synchronized void setPolarizationFactor(double polarizationFactor)
  {
    if (polarizationFactor == this.polarizationFactor) {
      return;
    }
    if (!Double.isNaN(polarizationFactor)) {
      polarizationFactor = Math.min(1.0D, Math.max(0.0D, polarizationFactor));
    }
    this.polarizationFactor = polarizationFactor;

    repaint();
  }

  public synchronized String getPolarizedText()
  {
    return this.polarizedText;
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

    repaint();
  }

  public synchronized String getUncertainText()
  {
    return this.uncertainText;
  }

  public synchronized void setUncertainText(String uncertainText)
  {
    if (uncertainText == null) {
      uncertainText = "";
    }
    if (this.uncertainText.equals(uncertainText)) {
      return;
    }
    this.uncertainText = uncertainText;

    repaint();
  }

  protected void paintOpinion(Graphics g, float x, float y, float width, float height, RenderStyle renderStyle)
  {
    Graphics2D g2 = (Graphics2D)g;

    Paint oldPaint = g2.getPaint();
    Color oldColor = g2.getColor();
    try
    {
      float filled = width * (float)this.opinion.getExpectation();

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
      {
        float bh2 = height / 2.0F;

        Rectangle2D bar = new Rectangle2D.Double(x, y, filled, height);

        Rectangle2D bar1 = new Rectangle2D.Double(x, y, filled, bh2);
        Rectangle2D bar2 = new Rectangle2D.Double(x, y + bh2, filled, bh2);
        GradientPaint grad2;
        GradientPaint grad1;
        GradientPaint grad2;
        if (renderStyle == RenderStyle.Raised)
        {
          GradientPaint grad1 = new GradientPaint(x, y, SystemColor.controlLtHighlight, x, y + bh2, this.barFillColor);
          grad2 = new GradientPaint(x, y + bh2, this.barFillColor, x, y + height, SystemColor.controlShadow);
        }
        else
        {
          grad1 = new GradientPaint(x, y, SystemColor.controlShadow, x, y + bh2, this.barFillColor);
          grad2 = new GradientPaint(x, y + bh2, this.barFillColor, x, y + height, SystemColor.controlLtHighlight);
        }

        g2.setPaint(grad1);
        g2.fill(bar1);

        g2.setPaint(grad2);
        g2.fill(bar2);
      }
      else
      {
        Rectangle2D bar = new Rectangle2D.Double(x, y, filled, height);

        g2.setColor(this.barFillColor);
        g2.fill(bar);
      }

      if (this.fuzzyOpinionSet != null) {
        this.text = this.fuzzyOpinionSet.getText(this.opinion);
      }
      FontMetrics fm = g2.getFontMetrics();
      float fh = fm.getAscent() + fm.getDescent();
      float tw = fm.stringWidth(this.text);
      float y0 = y + ((height - fh) / 2.0F + fm.getAscent());
      float x0 = x + (renderStyle == RenderStyle.Lowered) || (renderStyle == RenderStyle.Raised) ? 2 : 0;

      if (this.horizontalAlignment == 0)
        x0 = (width - tw) / 2.0F;
      else if (this.horizontalAlignment == 4) {
        x0 = width - tw;
      }
      if (filled > 0.0F)
      {
        g2.setClip((int)x, (int)y, (int)(x + filled), (int)height);
        g2.setColor(this.inverseForeground);
        g2.drawString(this.text, x0, y0);
      }

      g2.setClip((int)(x + filled), (int)y, (int)(width - filled), (int)height);
      g2.setColor(getForeground());
      g2.drawString(this.text, x0, y0);
    }
    catch (RuntimeException e)
    {
      throw e;
    }
    finally
    {
      g2.setClip(null);
      g2.setPaint(oldPaint);
      g2.setColor(oldColor);
    }
  }

  public synchronized String getText()
  {
    return this.text;
  }

  public synchronized int getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }

  public synchronized void setHorizontalAlignment(int horizontalAlignment)
  {
    if (this.horizontalAlignment == horizontalAlignment) {
      return;
    }
    this.horizontalAlignment = horizontalAlignment;

    repaint();
  }

  public synchronized Color getBarFillColor()
  {
    return this.barFillColor;
  }

  public synchronized void setBarFillColor(Color barFillColor)
  {
    if (barFillColor == null) {
      throw new NullPointerException("Color must not be null");
    }
    if (barFillColor.equals(this.barFillColor)) {
      return;
    }
    this.barFillColor = barFillColor;

    repaint();
  }

  public synchronized Color getInverseForeground()
  {
    return this.inverseForeground;
  }

  public synchronized void setInverseForeground(Color inverseForeground)
  {
    if (inverseForeground == null) {
      throw new NullPointerException("Color must not be null");
    }
    if (inverseForeground.equals(this.inverseForeground)) {
      return;
    }
    this.inverseForeground = inverseForeground;

    repaint();
  }
}