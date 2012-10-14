package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.DiscreteBayesian;
import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.PureBayesian;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;

public class DiscreteBayesianGraph extends OpinionVisualization
{
  private static final long serialVersionUID = 3978425819091644723L;
  private static final String DEFAULT_FORMAT = "%1$1.1f";
  private Color barColor = new Color(60, 60, 244);

  private Color labelBackground = new Color(255, 228, 168);

  private int maxBarWidth = 60;

  private int minBarSpacing = 4;

  private double[] currentValues = null;

  private int numberColumns = 2;

  private Position labelPosition = Position.Above;

  private Font valueFont = null;

  private boolean paintValue = false;

  private String valueFormat = "%1$1.1f";

  private List<String> labels = Collections.synchronizedList(new ArrayList());

  private List<GeneralPath> bars = Collections.synchronizedList(new ArrayList());

  public DiscreteBayesianGraph()
  {
    setBorder(BorderFactory.createBevelBorder(1));

    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    Paint oldPaint = g2.getPaint();
    Color oldColor = g2.getColor();
    Stroke oldStroke = g2.getStroke();
    FontMetrics fm = g2.getFontMetrics();
    FontMetrics fm2 = g2.getFontMetrics(getValueFont());

    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    label1690: 
    try { RenderStyle renderStyle = getRenderStyle();

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke(1.5F, 0, 2));

      int size = getNumberColumns();
      double max;
      synchronized (this)
      {
        DiscreteBayesian dbo = null;

        if (this.opinion != null)
        {
          if ((this.opinion instanceof DiscreteBayesian))
            dbo = ((DiscreteBayesian)this.opinion).toDiscreteBayesian(size);
          else {
            dbo = this.opinion.toPureBayesian().toDiscreteBayesian(size);
          }
        }
        if (dbo != null)
        {
          double max = dbo.max();
          double min = dbo.min();
          this.currentValues = dbo.values();
        }
        else
        {
          max = 0.0D;
          double min = 0.0D;
          this.currentValues = new double[0];
        }
      }

      float barWidth = Math.min(this.maxBarWidth, (getWidth() - (size + 1) * this.minBarSpacing) / size);
      float barSpacing = (getWidth() - barWidth * size) / size + 1;

      float x = barSpacing;

      synchronized (this.bars)
      {
        this.bars.clear();
        float topSpacing = 2.0F;
        float bottomSpacing = 2.0F;
        float fontHeight = 0.0F;
        float vFontHeight = 0.0F;

        if ((this.labels.size() > 0) && (this.labelPosition != Position.None)) {
          fontHeight = fm.getAscent() + fm.getDescent();
        }
        if (isPaintValue()) {
          vFontHeight = this.labelPosition == Position.Above ? fm2.getHeight() : fm2.getAscent() + fm.getDescent();
        }
        float usableHeight = this.labelPosition == Position.Below ? getHeight() - fm.getHeight() - bottomSpacing : getHeight();
        float maxHeight = usableHeight - topSpacing - vFontHeight - (this.labelPosition == Position.Above ? fontHeight : 0.0F);

        if ((this.labels.size() > 0) && (this.labelPosition == Position.Below))
        {
          GeneralPath area = new GeneralPath();
          area.moveTo(0.0F, getHeight() - fm.getHeight());
          area.lineTo(getWidth(), getHeight() - fm.getHeight());
          area.lineTo(getWidth(), getHeight());
          area.lineTo(0.0F, getHeight());
          area.closePath();

          g2.setColor(this.labelBackground);
          g2.fill(area);
        }

        for (int i = 0; i < size; i++)
        {
          Color barColor = this.barColor;
          float height = 0.0F;

          if (i < this.currentValues.length) {
            height = max == 0.0D ? 0.0F : (float)(this.currentValues[i] / max) * maxHeight;
          }

          if ((i < this.labels.size()) && (this.labelPosition != Position.None))
          {
            String label = (String)this.labels.get(i);

            float xf = x + (barWidth - fm.stringWidth(label)) / 2.0F;
            float yf;
            float yf;
            if (this.labelPosition == Position.Above) {
              yf = usableHeight - height - fm.getDescent() - isPaintValue() ? fm2.getHeight() : 0;
            }
            else {
              yf = usableHeight + fm.getAscent() + 1.0F;
            }
            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(label, xf, yf);
          }

          if (isPaintValue())
          {
            String valueText = String.format(this.valueFormat, new Object[] { Double.valueOf(this.currentValues[i]) }).trim();

            float xf = x + (barWidth - fm.stringWidth(valueText)) / 2.0F;
            float yf = usableHeight - height - fm2.getDescent();

            g2.setColor(getForeground());
            g2.setFont(getValueFont());
            g2.drawString(valueText, xf, yf);
          }

          GeneralPath bar = new GeneralPath();
          bar.moveTo(x, usableHeight);
          bar.lineTo(x, usableHeight - height);
          bar.lineTo(x + barWidth, usableHeight - height);
          bar.lineTo(x + barWidth, usableHeight);
          bar.closePath();

          this.bars.add(bar);

          if (renderStyle == RenderStyle.Flat)
          {
            g2.setColor(barColor);
            g2.fill(bar);
          }
          else if ((renderStyle == RenderStyle.Raised) || (renderStyle == RenderStyle.Lowered))
          {
            GeneralPath bar1 = new GeneralPath();
            bar1.moveTo(x, usableHeight);
            bar1.lineTo(x, usableHeight - height);
            bar1.lineTo(x + barWidth / 2.0F + 1.0F, usableHeight - height);
            bar1.lineTo(x + barWidth / 2.0F + 1.0F, usableHeight);
            bar1.closePath();

            GeneralPath bar2 = new GeneralPath();
            bar2.moveTo(x + barWidth / 2.0F - 1.0F, usableHeight);
            bar2.lineTo(x + barWidth / 2.0F - 1.0F, usableHeight - height);
            bar2.lineTo(x + barWidth, usableHeight - height);
            bar2.lineTo(x + barWidth, usableHeight);
            bar2.closePath();

            float gradientWidth = barWidth * 7.0F / 16.0F;
            GradientPaint grad2;
            GradientPaint grad1;
            GradientPaint grad2;
            if (renderStyle == RenderStyle.Lowered)
            {
              GradientPaint grad1 = new GradientPaint(x, usableHeight, SystemColor.controlShadow, x + gradientWidth, usableHeight, 
                barColor);

              grad2 = new GradientPaint(x + barWidth - gradientWidth, usableHeight, barColor, x + barWidth, 
                usableHeight, SystemColor.controlLtHighlight);
            }
            else
            {
              grad1 = new GradientPaint(x, usableHeight, SystemColor.controlLtHighlight, x + gradientWidth, 
                usableHeight, barColor);

              grad2 = new GradientPaint(x + barWidth - gradientWidth, usableHeight, barColor, x + barWidth, 
                usableHeight, SystemColor.controlShadow);
            }

            g2.setPaint(grad1);
            g2.fill(bar1);

            g2.setPaint(grad2);
            g2.fill(bar2);

            GeneralPath line1 = new GeneralPath();
            line1.moveTo(x, usableHeight);
            line1.lineTo(x, usableHeight - height);
            line1.lineTo(x + barWidth, usableHeight - height);

            g2.setColor(SystemColor.controlLtHighlight);
            g2.draw(line1);
          }

          if ((renderStyle == RenderStyle.Lowered) || (renderStyle == RenderStyle.Raised))
          {
            GeneralPath line1 = new GeneralPath();
            line1.moveTo(x, usableHeight);
            line1.lineTo(x, usableHeight - height);
            line1.lineTo(x + barWidth, usableHeight - height);

            GeneralPath line2 = new GeneralPath();
            line2.moveTo(x + barWidth, usableHeight - height);
            line2.lineTo(x + barWidth, usableHeight);
            line2.lineTo(x, usableHeight);

            g2.setColor(SystemColor.controlLtHighlight);
            g2.draw(line1);

            g2.setColor(SystemColor.controlShadow);
            g2.draw(line2);
          }

          x += barWidth + barSpacing;
        }

      }

      if (this.labels.size() > 0) { if (this.labelPosition != Position.Below) {
          break label1690;
        }
        GeneralPath line1 = new GeneralPath();
        line1.moveTo(0.0F, getHeight() - fm.getHeight());
        line1.lineTo(getWidth(), getHeight() - fm.getHeight());

        GeneralPath line2 = new GeneralPath();
        line2.moveTo(0.0F, getHeight() - fm.getHeight() + 1);
        line2.lineTo(getWidth(), getHeight() - fm.getHeight() + 1);

        if ((renderStyle == RenderStyle.Lowered) || (renderStyle == RenderStyle.Raised))
        {
          g2.setColor(SystemColor.controlShadow);
          g2.draw(line1);

          g2.setColor(SystemColor.controlLtHighlight);
          g2.draw(line2);
        }
        else
        {
          g2.setColor(SystemColor.controlShadow);
          g2.draw(line1);
        }
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
      g2.setStroke(oldStroke);
    }
  }

  public synchronized Color getBarColor()
  {
    return this.barColor;
  }

  public synchronized void setBarColor(Color barColor)
  {
    if (barColor == null) {
      throw new NullPointerException("Color must not be null.");
    }
    if (this.barColor == barColor) {
      return;
    }
    Color old = this.barColor;
    this.barColor = barColor;

    repaint();

    firePropertyChange("barColor", old, barColor);
  }

  public synchronized int getMaxBarWidth()
  {
    return this.maxBarWidth;
  }

  public synchronized void setMaxBarWidth(int maxBarWidth)
  {
    if (maxBarWidth == this.maxBarWidth) {
      return;
    }
    int old = this.maxBarWidth;
    this.maxBarWidth = maxBarWidth;

    repaint();

    firePropertyChange("maxBarWidth", old, maxBarWidth);
  }

  public synchronized void setNumberColumns(int numberColumns)
  {
    if (numberColumns < 2) {
      numberColumns = 2;
    }
    if (numberColumns == this.numberColumns) {
      return;
    }
    int oldnumberColumns = this.numberColumns;
    this.numberColumns = numberColumns;

    firePropertyChange("numberColumns", oldnumberColumns, numberColumns);
  }

  public synchronized int getNumberColumns()
  {
    return this.numberColumns;
  }

  public synchronized Collection<String> getLabels()
  {
    return this.labels;
  }

  public synchronized void setLabels(Collection<String> labels)
  {
    this.labels.clear();

    if (labels != null)
    {
      this.labels.addAll(labels);
      setNumberColumns(labels.size());
    }

    repaint();

    firePropertyChange("labels", null, labels);
  }

  public synchronized Position getLabelPosition()
  {
    return this.labelPosition;
  }

  public synchronized void setLabelPosition(Position labelPosition)
  {
    if (labelPosition == this.labelPosition) {
      return;
    }
    Position old = this.labelPosition;
    this.labelPosition = labelPosition;

    repaint();

    firePropertyChange("labelPosition", old, labelPosition);
  }

  public synchronized Color getLabelBackground()
  {
    return this.labelBackground;
  }

  public synchronized void setLabelBackground(Color labelBackground)
  {
    if (labelBackground == null) {
      throw new NullPointerException("Color must not be null.");
    }
    if (this.labelBackground == labelBackground) {
      return;
    }
    Color old = this.labelBackground;
    this.labelBackground = labelBackground;

    repaint();

    firePropertyChange("labelBackground", old, labelBackground);
  }

  public synchronized boolean isPaintValue()
  {
    return this.paintValue;
  }

  public synchronized void setPaintValue(boolean paintValue)
  {
    if (this.paintValue == paintValue) {
      return;
    }
    this.paintValue = paintValue;

    repaint();

    firePropertyChange("paintValue", !paintValue, paintValue);
  }

  public synchronized Font getValueFont()
  {
    if (this.valueFont == null) {
      return getFont();
    }
    return this.valueFont;
  }

  public synchronized void setValueFont(Font valueFont)
  {
    if (this.valueFont == valueFont) {
      return;
    }
    Font old = this.valueFont;
    this.valueFont = valueFont;

    repaint();

    firePropertyChange("valueFont", old, valueFont);
  }

  public synchronized String getValueFormat()
  {
    return this.valueFormat;
  }

  public synchronized void setValueFormat(String valueFormat)
  {
    if (valueFormat == null) {
      valueFormat = "%1$1.1f";
    }
    if (this.valueFormat == valueFormat) {
      return;
    }
    String old = this.valueFormat;
    this.valueFormat = valueFormat;

    repaint();

    firePropertyChange("valueFormat", old, valueFormat);
  }

  public static enum Position
  {
    None, Above, Below;
  }

  private class MyMouseListener extends MouseAdapter
    implements MouseMotionListener
  {
    private Component parent;

    MyMouseListener(Component parent)
    {
      this.parent = parent;
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
      if (DiscreteBayesianGraph.this.currentValues != null)
      {
        Point p = e.getPoint();
        int count = 0;

        for (GeneralPath path : DiscreteBayesianGraph.this.bars)
        {
          if (path.contains(p))
          {
            StringBuilder sb = new StringBuilder();

            if (count < DiscreteBayesianGraph.this.labels.size())
            {
              sb.append((String)DiscreteBayesianGraph.this.labels.get(count));
              sb.append("=");
            }

            sb.append(String.format(DiscreteBayesianGraph.this.valueFormat, new Object[] { Double.valueOf(DiscreteBayesianGraph.this.currentValues[count]) }));

            DiscreteBayesianGraph.this.setToolTipText(sb.toString());
            return;
          }

          count++;
        }
      }

      DiscreteBayesianGraph.this.setToolTipText(null);
    }

    public void mouseReleased(MouseEvent e)
    {
    }
  }
}