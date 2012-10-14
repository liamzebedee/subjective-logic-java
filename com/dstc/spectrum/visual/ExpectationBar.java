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

public class ExpectationBar extends OpinionBar
{
  private static final long serialVersionUID = 3257562897638175801L;
  private Color beliefFillColor = new Color(0, 0, 204);

  private Color uncertaintyFillColor = new Color(152, 152, 245);

  protected void paintOpinion(Graphics g, float x, float y, float width, float height, RenderStyle renderStyle)
  {
    Graphics2D g2 = (Graphics2D)g;

    SubjectiveOpinion o = this.opinion.toSubjectiveOpinion();

    float filledBelief = width * (float)o.getExpectation();
    float filledUncertainty = width * (float)(o.getExpectation() - o.getBelief());
    float startUncertainty = x + filledBelief - filledUncertainty;

    if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
    {
      float bh2 = height / 2.0F;

      Rectangle2D barBelief = new Rectangle2D.Double(x, y, filledBelief, height);

      Rectangle2D barBelief1 = new Rectangle2D.Double(x, y, filledBelief - filledUncertainty, bh2);
      Rectangle2D barBelief2 = new Rectangle2D.Double(x, y + bh2, filledBelief - filledUncertainty, bh2);
      GradientPaint gradBelief2;
      GradientPaint gradBelief1;
      GradientPaint gradBelief2;
      if (renderStyle == RenderStyle.Raised)
      {
        GradientPaint gradBelief1 = new GradientPaint(x, y, SystemColor.controlLtHighlight, x, y + bh2, this.beliefFillColor);
        gradBelief2 = new GradientPaint(x, y + bh2, this.beliefFillColor, x, y + height, getBackground());
      }
      else
      {
        gradBelief1 = new GradientPaint(x, y, SystemColor.controlShadow, x, y + bh2, this.beliefFillColor);
        gradBelief2 = new GradientPaint(x, y + bh2, this.beliefFillColor, x, y + height, SystemColor.controlLtHighlight);
      }

      Rectangle2D barUncertainty = new Rectangle2D.Double(startUncertainty, y, filledUncertainty, height);

      Rectangle2D barUncertainty1 = new Rectangle2D.Double(startUncertainty, y, filledUncertainty, bh2);
      Rectangle2D barUncertainty2 = new Rectangle2D.Double(startUncertainty, y + bh2, filledUncertainty, bh2);
      GradientPaint gradUncertainty2;
      GradientPaint gradUncertainty1;
      GradientPaint gradUncertainty2;
      if (renderStyle == RenderStyle.Raised)
      {
        GradientPaint gradUncertainty1 = new GradientPaint(startUncertainty, y, SystemColor.controlLtHighlight, startUncertainty, 
          y + bh2, this.uncertaintyFillColor);
        gradUncertainty2 = new GradientPaint(startUncertainty, y + bh2, this.uncertaintyFillColor, startUncertainty, y + height, 
          getBackground());
      }
      else
      {
        gradUncertainty1 = new GradientPaint(startUncertainty, y, SystemColor.controlShadow, startUncertainty, 
          y + bh2, this.uncertaintyFillColor);
        gradUncertainty2 = new GradientPaint(startUncertainty, y + bh2, this.uncertaintyFillColor, startUncertainty, y + height, 
          SystemColor.controlLtHighlight);
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
      Rectangle2D barBelief = new Rectangle2D.Double(x, y, filledBelief, height);
      Rectangle2D barUncertainty = new Rectangle2D.Double(startUncertainty, y, filledUncertainty, height);

      g2.setColor(this.beliefFillColor);
      g2.fill(barBelief);

      g2.setColor(this.uncertaintyFillColor);
      g2.fill(barUncertainty);
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
}