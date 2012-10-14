package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.PropertyChangeEvent;

public class BayesianBar extends OpinionBar
{
  private static final long serialVersionUID = 4121694379070076208L;
  private Color beliefFillColor = new Color(98, 215, 86);

  private Color disbeliefFillColor = new Color(255, 71, 71);

  private Color uncertaintyFillColor = new Color(228, 241, 54);

  private boolean showUncertainty = true;

  private boolean showTrueUncertainty = true;

  public BayesianBar()
  {
    setShowExpectation(true);
  }

  protected void paintOpinion(Graphics g, float x, float y, float width, float height, RenderStyle renderStyle)
  {
    Graphics2D g2 = (Graphics2D)g;

    SubjectiveOpinion o = isShowTrueUncertainty() ? this.opinion.toSubjectiveOpinion() : 
      this.opinion.toSubjectiveOpinion().increasedUncertainty();

    float filledBelief = width * (float)o.getBelief();
    float filledDisbelief = width * (float)o.getDisbelief();
    float startDisbelief = x + width - filledDisbelief;

    if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
    {
      float bh2 = height / 2.0F;

      if (this.showUncertainty)
      {
        Rectangle2D bar = new Rectangle2D.Double(x, y, width, height);
        Rectangle2D bar1 = new Rectangle2D.Double(bar.getX(), bar.getY(), width, bh2);
        Rectangle2D bar2 = new Rectangle2D.Double(bar.getX(), bar.getMaxY() - bh2, width, bh2);
        GradientPaint gradBar2;
        GradientPaint gradBar1;
        GradientPaint gradBar2;
        if (renderStyle == RenderStyle.Raised)
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

      Rectangle2D barBelief = new Rectangle2D.Double(x, y, filledBelief, height);

      Rectangle2D barBelief1 = new Rectangle2D.Double(x, y, filledBelief, bh2);
      Rectangle2D barBelief2 = new Rectangle2D.Double(x, y + bh2, filledBelief, bh2);
      GradientPaint gradBelief2;
      GradientPaint gradBelief1;
      GradientPaint gradBelief2;
      if (renderStyle == RenderStyle.Raised)
      {
        GradientPaint gradBelief1 = new GradientPaint(x, y, SystemColor.controlLtHighlight, x, y + bh2, this.beliefFillColor);
        gradBelief2 = new GradientPaint(x, y + bh2, this.beliefFillColor, x, y + height, SystemColor.controlShadow);
      }
      else
      {
        gradBelief1 = new GradientPaint(x, y, SystemColor.controlShadow, x, y + bh2, this.beliefFillColor);
        gradBelief2 = new GradientPaint(x, y + bh2, this.beliefFillColor, x, y + height, SystemColor.controlLtHighlight);
      }

      Rectangle2D barDisbelief = new Rectangle2D.Double(startDisbelief, y, filledDisbelief, height);

      Rectangle2D barDisbelief1 = new Rectangle2D.Double(startDisbelief, y, filledDisbelief, bh2);
      Rectangle2D barDisbelief2 = new Rectangle2D.Double(startDisbelief, y + bh2, filledDisbelief, bh2);
      GradientPaint gradUncertainty2;
      GradientPaint gradUncertainty1;
      GradientPaint gradUncertainty2;
      if (renderStyle == RenderStyle.Raised)
      {
        GradientPaint gradUncertainty1 = new GradientPaint(startDisbelief, y, SystemColor.controlLtHighlight, startDisbelief, y + bh2, 
          this.disbeliefFillColor);
        gradUncertainty2 = new GradientPaint(startDisbelief, y + bh2, this.disbeliefFillColor, startDisbelief, y + height, 
          SystemColor.controlShadow);
      }
      else
      {
        gradUncertainty1 = new GradientPaint(startDisbelief, y, SystemColor.controlShadow, startDisbelief, y + bh2, 
          this.disbeliefFillColor);
        gradUncertainty2 = new GradientPaint(startDisbelief, y + bh2, this.disbeliefFillColor, startDisbelief, y + height, 
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
        Rectangle2D bar = new Rectangle2D.Double(x, y, width, height);

        g2.setColor(this.uncertaintyFillColor);
        g2.fill(bar);
      }

      Rectangle2D barBelief = new Rectangle2D.Double(x, y, filledBelief, height);
      Rectangle2D barDisbelief = new Rectangle2D.Double(startDisbelief, y, filledDisbelief, height);

      g2.setColor(this.beliefFillColor);
      g2.fill(barBelief);

      g2.setColor(this.disbeliefFillColor);
      g2.fill(barDisbelief);
    }
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

  public void propertyChange(PropertyChangeEvent evt)
  {
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

  public synchronized boolean isShowTrueUncertainty()
  {
    return this.showTrueUncertainty;
  }

  public synchronized void setShowTrueUncertainty(boolean showTrueUncertainty)
  {
    if (this.showTrueUncertainty == showTrueUncertainty) {
      return;
    }
    this.showTrueUncertainty = showTrueUncertainty;
    repaint();

    firePropertyChange("showTrueUncertainty", !showTrueUncertainty, showTrueUncertainty);
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
}