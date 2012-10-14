package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.BasicOpinionTextualizer;
import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.OpinionTextualizer;
import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;

public class ProbabilityDensityGraph extends JComponent
{
  private Map<Object, Series> map = Collections.synchronizedMap(new HashMap());

  private double minX = 0.0D;

  private double minY = 0.0D;

  private double maxX = 1.0D;

  private double maxY = 10.0D;

  private String titleAxisX = "p";

  private String titleAxisY = "f(p)";

  private boolean showAxisTitleX = false;

  private boolean showAxisTitleY = false;

  private Color axisColor = Color.black;
  private static final int tickLength = 4;
  private boolean showGrid = true;

  private Color gridColor = new Color(224, 224, 224);

  private Stroke gridStroke = new BasicStroke(1.0F, 0, 0);

  private OpinionTextualizer opinionTextualizer = new BasicOpinionTextualizer();
  private static final long serialVersionUID = 3617572682795988790L;

  public ProbabilityDensityGraph()
  {
    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);
  }

  protected synchronized void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;

    if (isOpaque())
    {
      g2.setColor(getBackground());
      g2.fillRect(0, 0, getWidth(), getHeight());
    }

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int fontHeight = g2.getFontMetrics().getHeight();
    int fontAscent = g2.getFontMetrics().getAscent();
    int fontDescent = g2.getFontMetrics().getDescent();

    g2.setColor(this.axisColor);

    int topBuffer = 10;
    int titleOffsetY = 0;
    int titleOffsetX = 25;

    if ((this.showAxisTitleX) && (this.titleAxisY != null) && (this.titleAxisY.length() > 0))
    {
      g2.drawString(this.titleAxisY, 2, (getHeight() - titleOffsetY) / 2 - fontAscent / 2 + titleOffsetY);
      titleOffsetX += g2.getFontMetrics().stringWidth(this.titleAxisY);
    }

    if ((this.showAxisTitleY) && (this.titleAxisX != null) && (this.titleAxisX.length() > 0))
    {
      int fontWidth = g2.getFontMetrics().stringWidth(this.titleAxisX);
      g2.drawString(this.titleAxisX, titleOffsetX + (getWidth() - titleOffsetX - fontWidth) / 2, getHeight() - titleOffsetY - 
        fontDescent);
      titleOffsetY += fontHeight;
    }

    float newWidth = getWidth() - titleOffsetX - 4 - 10;
    float newHeight = getHeight() - titleOffsetY - 4 - fontHeight - topBuffer;

    Rectangle2D.Float plotRectangle = new Rectangle2D.Float(titleOffsetX + 4, topBuffer, newWidth, newHeight);

    g2.setColor(getBackground());
    g2.fillRect((int)plotRectangle.x, (int)plotRectangle.y, (int)plotRectangle.width, (int)plotRectangle.height);

    double xRange = this.maxX - this.minX;
    double yRange = this.maxY - this.minY;

    g2.setColor(this.axisColor);
    for (int i = 0; i <= 10; i++)
    {
      double x_value = (int)(100.0D * (i * 0.1D * (this.maxX - this.minX) + this.minX)) / 100.0D;
      double y_value = (int)(100.0D * (i * 0.1D * (this.maxY - this.minY) + this.minY)) / 100.0D;

      String x_label = String.valueOf(x_value);
      String y_label = String.valueOf(y_value);

      int xFontWidth = g2.getFontMetrics().stringWidth(x_label);
      int yFontWidth = g2.getFontMetrics().stringWidth(y_label);

      float x_pos = (float)(4 + titleOffsetX + newWidth * (x_value - this.minX) / xRange);
      float y_pos = (float)(topBuffer + newHeight * (this.maxY - y_value) / yRange);

      g2.drawString(x_label, x_pos - xFontWidth / 2, topBuffer + newHeight + 4.0F + fontHeight);
      g2.drawString(y_label, titleOffsetX - yFontWidth - 2, y_pos + fontAscent / 2);

      GeneralPath line1 = new GeneralPath();
      GeneralPath line2 = new GeneralPath();

      line1.moveTo(x_pos, topBuffer + newHeight + 4.0F);
      line1.lineTo(x_pos, topBuffer + newHeight);

      line2.moveTo(titleOffsetX, y_pos);
      line2.lineTo(titleOffsetX + 4, y_pos);

      if (isShowGrid())
      {
        Stroke oldStroke = g2.getStroke();

        GeneralPath grid1 = new GeneralPath();
        GeneralPath grid2 = new GeneralPath();

        grid1.moveTo(x_pos, topBuffer);
        grid1.lineTo(x_pos, topBuffer + newHeight);

        grid2.moveTo(titleOffsetX, y_pos);
        grid2.lineTo(titleOffsetX + newWidth + 4.0F, y_pos);

        g2.setColor(getGridColor());
        g2.setStroke(this.gridStroke);

        g2.draw(grid1);
        g2.draw(grid2);

        g2.setStroke(oldStroke);
      }

      g2.setColor(getForeground());
      g2.draw(line1);
      g2.draw(line2);
    }

    g2.setClip((int)plotRectangle.x, (int)plotRectangle.y, (int)plotRectangle.width, (int)plotRectangle.height);

    Stroke stroke = g2.getStroke();

    for (Series series : this.map.values())
    {
      boolean fillCurve = series.isFillCurve();
      GeneralPath curve = series.getCurve(fillCurve, plotRectangle);

      g2.setColor(series.getColor());

      if (fillCurve) {
        g2.fill(curve);
      }
      if (series.isDrawCurve())
      {
        g2.setStroke(series.getStroke());
        g2.draw(curve);
      }
    }

    g2.setClip(null);

    g2.setStroke(stroke);
    g2.setColor(this.axisColor);
    g2.drawRect((int)plotRectangle.x, (int)plotRectangle.y, (int)plotRectangle.width, (int)plotRectangle.height);
  }

  public synchronized Series getSeries(Object reference)
  {
    return (Series)this.map.get(reference);
  }

  public synchronized Opinion getOpinion(Object reference)
  {
    if (this.map.containsKey(reference)) {
      return ((Series)this.map.get(reference)).getOpinion();
    }
    return null;
  }

  public synchronized Series putOpinion(Object reference, Opinion opinion)
  {
    if ((reference == null) || (opinion == null)) {
      throw new NullPointerException("Reference and Opinion must not be null");
    }
    Series series = new Series(opinion);

    this.map.put(reference, series);

    repaint();

    return series;
  }

  public synchronized void removeSeries(Object reference)
  {
    if (reference == null) {
      throw new NullPointerException("Reference must not be null");
    }
    Series series = (Series)this.map.remove(reference);

    if (series != null)
    {
      series.getOpinion().removePropertyChangeListener(series);
      repaint();
    }
  }

  public synchronized void clearSeries()
  {
    if (!this.map.isEmpty())
    {
      for (Series series : this.map.values()) {
        series.getOpinion().removePropertyChangeListener(series);
      }
      this.map.clear();
      repaint();
    }
  }

  public synchronized Color getAxisColor()
  {
    return this.axisColor;
  }

  public synchronized void setAxisColor(Color axisColor)
  {
    this.axisColor = axisColor;
  }

  public synchronized double getMaxX()
  {
    return this.maxX;
  }

  public synchronized void setMaxX(double maxX)
  {
    this.maxX = Math.max(0.0D, Math.min(1.0D, maxX));
  }

  public synchronized double getMaxY()
  {
    return this.maxY;
  }

  public synchronized void setMaxY(double maxY)
  {
    this.maxY = Math.max(0.0D, Math.min(1.7976931348623157E+308D, maxY));
  }

  public synchronized double getMinX()
  {
    return this.minX;
  }

  public synchronized void setMinX(double minX)
  {
    this.minX = Math.min(1.0D, Math.max(0.0D, minX));
  }

  public synchronized double getMinY()
  {
    return this.minY;
  }

  public synchronized void setMinY(double minY)
  {
    this.minY = Math.min(1.7976931348623157E+308D, Math.max(0.0D, this.minX));
  }

  public synchronized String getTitleAxisX()
  {
    return this.titleAxisX;
  }

  public synchronized void setTitleAxisX(String titleAxisX)
  {
    this.titleAxisX = titleAxisX;
  }

  public synchronized String getTitleAxisY()
  {
    return this.titleAxisY;
  }

  public synchronized void setTitleAxisY(String titleAxisY)
  {
    this.titleAxisY = titleAxisY;
  }

  public synchronized boolean isShowAxisTitleX()
  {
    return this.showAxisTitleX;
  }

  public synchronized void setShowAxisTitleX(boolean showAxisTitleX)
  {
    this.showAxisTitleX = showAxisTitleX;
  }

  public synchronized boolean isShowAxisTitleY()
  {
    return this.showAxisTitleY;
  }

  public synchronized void setShowAxisTitleY(boolean showAxisTitleY)
  {
    this.showAxisTitleY = showAxisTitleY;
  }

  public synchronized Color getGridColor()
  {
    return this.gridColor;
  }

  public synchronized void setGridColor(Color gridColor)
  {
    this.gridColor = gridColor;
  }

  public synchronized boolean isShowGrid()
  {
    return this.showGrid;
  }

  public synchronized void setShowGrid(boolean showGrid)
  {
    this.showGrid = showGrid;
  }

  public synchronized OpinionTextualizer getOpinionTextualizer()
  {
    return this.opinionTextualizer;
  }

  public synchronized void setOpinionTextualizer(OpinionTextualizer opinionTextualizer)
  {
    if (opinionTextualizer == null) {
      throw new NullPointerException("OpinionTextualizer must not be null");
    }
    this.opinionTextualizer = opinionTextualizer;
  }

  public synchronized Stroke getGridStroke()
  {
    return this.gridStroke;
  }

  public synchronized void setGridStroke(Stroke gridStroke)
  {
    if (gridStroke == null) {
      throw new NullPointerException("GridStroke must not be nuill");
    }
    this.gridStroke = gridStroke;
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
      Point p = e.getPoint();

      Map.Entry selected = null;

      for (Map.Entry entry : ProbabilityDensityGraph.this.map.entrySet())
      {
        ProbabilityDensityGraph.Series s = (ProbabilityDensityGraph.Series)entry.getValue();

        if ((ProbabilityDensityGraph.Series.access$0(s) != null) && (ProbabilityDensityGraph.Series.access$0(s).contains(p))) {
          selected = entry;
        }
      }
      if (selected != null)
        ProbabilityDensityGraph.this.setToolTipText(selected.getKey().toString() + "=" + ProbabilityDensityGraph.this.opinionTextualizer.textualize(((ProbabilityDensityGraph.Series)selected.getValue()).getOpinion()));
      else
        ProbabilityDensityGraph.this.setToolTipText(null);
    }

    public void mouseReleased(MouseEvent e)
    {
    }
  }

  public class Series
    implements PropertyChangeListener
  {
    private Color color = new Color(0, 0, 204);

    private boolean fillCurve = false;

    private boolean drawCurve = true;

    private Stroke stroke = new BasicStroke(1.0F, 1, 1);

    private double alpha = 0.0D;

    private double beta = 0.0D;
    private double coefficient;
    private List<Point2D> points = new ArrayList();
    private Opinion opinion;
    private GeneralPath plotCurve = null;

    public Series(Opinion opinion)
    {
      setOpinion(opinion);
    }

    public synchronized void setOpinion(Opinion opinion)
    {
      if (opinion == null) {
        throw new NullPointerException("Opinion must not be null");
      }
      if (this.opinion != null) {
        opinion.removePropertyChangeListener(this);
      }
      this.opinion = opinion;

      updateBetaProbabilityValues();

      opinion.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
      updateBetaProbabilityValues();
      ProbabilityDensityGraph.this.repaint();
    }

    private void updateBetaProbabilityValues()
    {
      SubjectiveOpinion o = this.opinion.toSubjectiveOpinion();
      double alpha;
      double beta;
      synchronized (o)
      {
        double b = o.getBelief();
        double d = o.getDisbelief();
        double u = o.getUncertainty();
        double a = o.getAtomicity();

        if (u == 0.0D) {
          u = 1.E-005D;
        }
        alpha = 2.0D * b / u + 2.0D * a;
        beta = 2.0D * d / u + 2.0D * (1.0D - a);
      }

      if ((alpha == this.alpha) && (beta == this.beta)) {
        return;
      }
      this.points.clear();

      this.alpha = alpha;
      this.beta = beta;
      this.coefficient = coefficient(alpha, beta);
    }

    private synchronized double probability(double c, double alpha, double beta, double x)
    {
      return Math.exp(c + (alpha - 1.0D) * Math.log(x) + (beta - 1.0D) * Math.log(1.0D - x));
    }

    private double coefficient(double alpha, double beta)
    {
      return lnGamma(alpha + beta) - lnGamma(alpha) - lnGamma(beta);
    }

    private double lnGamma(double alpha)
    {
      double x = alpha; double f = 0.0D;

      if (x < 7.0D)
      {
        f = 1.0D;
        double z = x - 1.0D;
        while (++z < 7.0D)
        {
          f *= z;
        }
        x = z;
        f = -Math.log(f);
      }
      double z = 1.0D / (x * x);

      return f + (x - 0.5D) * Math.log(x) - x + 0.918938533204673D + 
        (((-0.000595238095238D * z + 0.000793650793651D) * z - 0.002777777777778D) * z + 0.083333333333333D) / x;
    }

    private synchronized double getAlpha()
    {
      return this.alpha;
    }

    private synchronized double getBeta()
    {
      return this.beta;
    }

    private synchronized double getCoefficient()
    {
      return this.coefficient;
    }

    private synchronized double getDensity(double x)
    {
      return Math.exp(this.coefficient + (this.alpha - 1.0D) * Math.log(x) + (this.beta - 1.0D) * Math.log(1.0D - x));
    }

    private synchronized List<Point2D> getProbabilityCurve()
    {
      double step = 0.005D;

      if (this.points.isEmpty())
      {
        for (double x = 0.0D; x <= 1.0D; x = Math.round((x + step) * 100000.0D) / 100000.0D)
        {
          double y;
          double y;
          if (x == 0.0D) {
            y = getDensity(0.001D);
          }
          else
          {
            double y;
            if (x == 1.0D)
              y = getDensity(0.999D);
            else
              y = getDensity(x);
          }
          if (Double.isNaN(y)) {
            y = 0.0D;
          }
          this.points.add(new Point2D.Double(x, y));
        }
      }

      return new ArrayList(this.points);
    }

    private GeneralPath getCurve(boolean fillCurve, Rectangle2D plotRectangle)
    {
      List points = getProbabilityCurve();

      double height = plotRectangle.getHeight();
      double width = plotRectangle.getWidth();
      double startX = plotRectangle.getX();
      double startY = plotRectangle.getY();

      double rangeX = ProbabilityDensityGraph.this.maxX - ProbabilityDensityGraph.this.minX;
      double rangeY = ProbabilityDensityGraph.this.maxY - ProbabilityDensityGraph.this.minY;

      int step = Math.max(1, (int)Math.floor(points.size() * 3.0D / width));

      List plottedPoints = new ArrayList();
      double y;
      for (int i = 0; i < points.size(); i += step)
      {
        Point2D point = (Point2D)points.get(i);

        double x = point.getX();
        y = point.getY();

        if (!Double.isInfinite(y))
        {
          float newX = (float)(width * (x - ProbabilityDensityGraph.this.minX) / rangeX + startX);
          float newY = (float)(height * (ProbabilityDensityGraph.this.maxY - y) / rangeY + startY);

          plottedPoints.add(new Point2D.Float(newX, newY));
        }
      }

      this.plotCurve = new GeneralPath();

      if (plottedPoints.isEmpty()) {
        return this.plotCurve;
      }
      Point2D.Float first = (Point2D.Float)plottedPoints.get(0);
      Point2D.Float last = (Point2D.Float)plottedPoints.get(plottedPoints.size() - 1);

      float graphHeight = (float)(startY + height);

      this.plotCurve.moveTo((float)startX - 5.0F, graphHeight + 5.0F);
      this.plotCurve.lineTo((float)startX - 5.0F, first.y);

      for (Point2D.Float p : plottedPoints) {
        this.plotCurve.lineTo(p.x, p.y);
      }
      this.plotCurve.lineTo((float)(startX + width + 5.0D), last.y);
      this.plotCurve.lineTo((float)(startX + width + 5.0D), graphHeight + 5.0F);
      this.plotCurve.closePath();

      return this.plotCurve;
    }

    public synchronized boolean isDrawCurve()
    {
      return this.drawCurve;
    }

    public synchronized void setDrawCurve(boolean drawCurve)
    {
      this.drawCurve = drawCurve;
    }

    public synchronized boolean isFillCurve()
    {
      return this.fillCurve;
    }

    public synchronized void setFillCurve(boolean fillCurve)
    {
      this.fillCurve = fillCurve;
    }

    public synchronized Color getColor()
    {
      return this.color;
    }

    public synchronized void setColor(Color color)
    {
      this.color = color;
    }

    public synchronized Opinion getOpinion()
    {
      return this.opinion;
    }

    public synchronized Stroke getStroke()
    {
      return this.stroke;
    }

    public synchronized void setStroke(Stroke stroke)
    {
      this.stroke = stroke;
    }
  }
}