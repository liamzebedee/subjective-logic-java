package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.BasicFuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzyOpinionSet;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class FuzzyLabel extends OpinionVisualization
{
  private FuzzyOpinionSet fuzzyOpinionSet = new BasicFuzzyOpinionSet();

  private String uncertainText = "Uncertain";

  private String polarizedText = "Polarized";

  private String text = "";

  private double polarizationFactor = (0.0D / 0.0D);

  private double certaintyThreshold = (0.0D / 0.0D);

  private HorizontalAlignment horizontalAlignment = HorizontalAlignment.Left;
  private static final long serialVersionUID = 3691038764164134963L;

  public FuzzyLabel()
  {
    this.fuzzyOpinionSet = new BasicFuzzyOpinionSet();
    initialize();
  }

  public FuzzyLabel(FuzzyOpinionSet fuzzyOpinionSet)
  {
    setFuzzyOpinionSet(fuzzyOpinionSet);
    initialize();
  }

  private void initialize()
  {
    setPreferredSize(new Dimension(100, 16));
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

  public synchronized void setFuzzyOpinionSet(FuzzyOpinionSet fuzzyOpinionSet)
  {
    if (fuzzyOpinionSet == null) {
      throw new NullPointerException("FuzzyOpinionSet must not be null");
    }
    if (fuzzyOpinionSet == this.fuzzyOpinionSet) {
      return;
    }
    this.fuzzyOpinionSet = fuzzyOpinionSet;

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

  protected synchronized void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    if (isOpaque())
    {
      g2.setColor(getBackground());
      g2.fillRect(0, 0, getWidth(), getHeight());
    }

    if ((this.opinion != null) && (this.fuzzyOpinionSet != null))
    {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      this.text = this.fuzzyOpinionSet.getText(this.opinion);

      if (this.text == null) {
        this.text = "";
      }
      FontMetrics fm = g2.getFontMetrics();
      float fh = fm.getAscent() + fm.getDescent();
      float tw = fm.stringWidth(this.text);
      float y = (getHeight() - fh) / 2.0F + fm.getAscent();
      float x = 0.0F;

      if (this.horizontalAlignment == HorizontalAlignment.Center)
        x = (getWidth() - tw) / 2.0F;
      else if (this.horizontalAlignment == HorizontalAlignment.Right) {
        x = getWidth() - tw;
      }
      g2.setColor(getForeground());
      g2.drawString(this.text, x, y);
    }
  }

  public synchronized String getText()
  {
    return this.text;
  }

  public synchronized HorizontalAlignment getHorizontalAlignment()
  {
    return this.horizontalAlignment;
  }

  public synchronized void setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
  {
    if (this.horizontalAlignment == horizontalAlignment) {
      return;
    }
    this.horizontalAlignment = horizontalAlignment;

    repaint();
  }

  public void setPreferredSize(Dimension preferredSize)
  {
    super.setMinimumSize(preferredSize);
    super.setPreferredSize(preferredSize);
  }
}