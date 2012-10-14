package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.Opinion;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

public abstract class OpinionVisualization extends JComponent
{
  public static final double[] TEN_DIVISIONS = { 0.0D, 0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D };

  public static final double[] FIVE_DIVISIONS = { 0.0D, 0.2D, 0.4D, 0.6D, 0.8D, 1.0D };

  public static final double[] FOUR_DIVISIONS = { 0.0D, 0.25D, 0.5D, 0.75D, 1.0D };
  protected static final double TOLERANCE = 10000000.0D;
  protected Opinion opinion = null;

  protected PropertyChangeListener listener = new PropertyChangeListener()
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      OpinionVisualization.this.handleOpinionChange();
    }
  };

  private RenderStyle renderStyle = RenderStyle.Raised;

  protected void handleOpinionChange()
  {
    repaint();
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

  public synchronized void setOpinion(Opinion opinion)
  {
    if (opinion != this.opinion)
    {
      if (this.opinion != null) {
        this.opinion.removePropertyChangeListener(this.listener);
      }
      Opinion old = this.opinion;
      this.opinion = opinion;

      repaint();

      opinion.addPropertyChangeListener(this.listener);
      firePropertyChange("opinion", old, opinion);
    }
  }

  public synchronized Opinion getOpinion()
  {
    return this.opinion;
  }

  protected PropertyChangeListener getListener()
  {
    return this.listener;
  }

  protected void setListener(PropertyChangeListener listener)
  {
    this.listener = listener;
  }
}