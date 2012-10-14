package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.BasicOpinionTextualizer;
import com.dstc.spectrum.opinion.FuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzySet;
import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.OpinionTextualizer;
import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.AWTEventMulticaster;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;

public class OpinionTriangle extends JComponent
{
  private static final long serialVersionUID = 3257849887420133432L;
  private static final double FLOOR = 0.005D;
  private static final double INVROOT3 = 0.57735D;
  public static final double TOLERANCE_ADJUST = 10000000000.0D;
  private OpinionPoint current = null;

  private ActionListener actionListener = null;

  private double[] uncertaintyGridlines = OpinionVisualization.FIVE_DIVISIONS;

  private double[] expectationGridlines = OpinionVisualization.FIVE_DIVISIONS;

  private Point2D offset = new Point2D.Double(0.0D, 0.0D);

  private double atomicity = 0.5D;
  private GeneralPath triangle;
  private GeneralPath atomHandle;
  private int pointSize = 8;

  private boolean atomicityEnabled = true;

  private double scale = 0.0D;

  private String beliefText = "Belief";

  private String disbeliefText = "Disbelief";

  private String uncertaintyText = "Uncertainty";

  private String atomicityText = "Atomicity";

  private int labelWhiteSpace = 4;

  private Color fillColor = Color.white;

  private boolean paintFilled = true;

  private boolean paintPointLabels = true;

  private boolean paintGrid = false;

  private Color gridColor = new Color(224, 224, 224);

  private Stroke gridStroke = new BasicStroke(1.0F, 0, 0);

  private boolean paintControlLabels = true;

  private double granularity = 0.005D;

  private boolean autoSize = true;

  private double preferredScale = 0.0D;

  private Map<String, OpinionPoint> opinionPoints = Collections.synchronizedMap(new TreeMap());

  private OpinionTextualizer opinionTextualizer = new BasicOpinionTextualizer();

  public OpinionTriangle()
  {
    initialize();
  }

  public OpinionTriangle(int height, int width)
  {
    this();
    setPreferredSize(new Dimension(height, width));
  }

  public void initialize()
  {
    MyMouseListener myListener = new MyMouseListener(this);
    addMouseListener(myListener);
    addMouseMotionListener(myListener);

    setMinimumSize(new Dimension(118, 78));
    setPreferredSize(getMinimumSize());
  }

  public OpinionPoint createOpinionPoint(String reference, Opinion opinion)
  {
    return createOpinionPoint(reference, opinion, null);
  }

  public synchronized OpinionPoint createOpinionPoint(String reference, Opinion opinion, String text)
  {
    if ((opinion == null) || (reference == null)) {
      throw new NullPointerException("The Opinion and reference must not be null");
    }
    OpinionPoint point = new OpinionPoint(this, opinion, reference, text, null);

    point.setAtomicity(this.atomicity);

    this.opinionPoints.put(reference, point);

    repaint();

    firePropertyChange("point", null, point);

    return point;
  }

  public synchronized double getAtomicity()
  {
    return this.atomicity;
  }

  public synchronized void setAtomicity(double atomicity)
  {
    double old = this.atomicity;
    this.atomicity = adjust(Math.round(atomicity / this.granularity) * this.granularity);

    for (OpinionPoint p : this.opinionPoints.values()) {
      p.setAtomicity(this.atomicity);
    }
    repaint();

    firePropertyChange("atomicity", new Double(old), new Double(atomicity));
  }

  private Point2D getExpectationPoint(double expectation)
  {
    return new Point2D.Double(this.offset.getX() + expectation * this.scale * 2.0D * 0.57735D, this.offset.getY() + this.scale);
  }

  public synchronized void addActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.add(this.actionListener, l);
  }

  public synchronized void removeActionListener(ActionListener l)
  {
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, l);
  }

  private OpinionPoint locatePoint(int x, int y)
  {
    synchronized (this)
    {
      for (OpinionPoint point : this.opinionPoints.values())
      {
        if ((point.isVisible()) && (point.insidePoint(x, y))) {
          return point;
        }
      }
    }
    return null;
  }

  private Point2D getOffset()
  {
    return this.offset;
  }

  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;
    FontMetrics fm = g2.getFontMetrics();

    boolean paintControlLabels = isPaintControlLabels();
    boolean paintPointLabels = isPaintPointLabels();

    int pointSize = getPointSize();
    int atomHandleHeight = (int)(3 * pointSize / 4.0D / 0.57735D);
    int atomHandleWidth = (int)(3 * pointSize / 4.0D);

    String disbeliefText = getDisbeliefText();
    String beliefText = getBeliefText();
    String uncertaintyText = getUncertaintyText();
    String atomicityText = getAtomicityText();

    double[] uncertaintyGridlines = getUncertaintyGridlines();
    double[] expectationGridlines = getExpectationGridlines();

    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    Stroke strokeThick = new BasicStroke(1.5F, 0, 0);
    Stroke strokeThin = new BasicStroke(1.0F, 0, 0);

    int buff = paintControlLabels ? this.labelWhiteSpace : 0;
    int fh = paintControlLabels ? fm.getHeight() : 0;
    int vh = Math.max(0, pointSize / 2 - fh);

    double w = getWidth() - pointSize - 1;
    double h = getHeight() - atomHandleHeight - vh;

    if (paintControlLabels)
    {
      w -= Math.max(4 * buff + fm.stringWidth(disbeliefText) + fm.stringWidth(beliefText), fm.stringWidth(uncertaintyText));
      h -= 2 * (fh + buff);
    }
    else if (paintPointLabels)
    {
      h -= 2 * this.labelWhiteSpace;
    }

    double s = Math.min(w / 1.1547D, h);

    if (!this.autoSize) {
      s = Math.min(this.preferredScale, s);
    }
    setScaleInternal(s);

    double ow = (w - 2.0D * this.scale * 0.57735D) / 2.0D;
    double oh = (h - this.scale) / 2.0D;

    if (paintControlLabels)
      this.offset.setLocation(fm.stringWidth(disbeliefText) + 2 * buff + ow, fh + vh + buff + oh);
    else if (paintPointLabels)
      this.offset.setLocation(ow + pointSize / 2.0F, this.labelWhiteSpace + oh + vh);
    else {
      this.offset.setLocation(ow + pointSize / 2.0F, oh + vh);
    }
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    double triangleWidth = this.scale * 2.0D * 0.57735D;

    Point2D topCorner = new Point2D.Double(this.offset.getX() + this.scale * 0.57735D, this.offset.getY());
    Point2D leftCorner = new Point2D.Double(this.offset.getX(), this.offset.getY() + this.scale);
    Point2D rightCorner = new Point2D.Double(this.offset.getX() + triangleWidth, this.offset.getY() + this.scale);

    this.triangle = new GeneralPath();
    this.triangle.moveTo((float)topCorner.getX(), (float)topCorner.getY());
    this.triangle.lineTo((float)leftCorner.getX(), (float)leftCorner.getY());
    this.triangle.lineTo((float)rightCorner.getX(), (float)rightCorner.getY());
    this.triangle.closePath();

    if (isPaintFilled())
    {
      g2.setColor(this.fillColor);
      g2.fill(this.triangle);
    }

    if (isPaintGrid())
    {
      g2.setClip(this.triangle);
      g2.setColor(this.gridColor);
      g2.setStroke(this.gridStroke);

      if (expectationGridlines != null)
      {
        float skew = (float)((0.5D - this.atomicity) * triangleWidth);

        for (double value : expectationGridlines)
        {
          if ((value > 0.0D) && (value < 1.0D))
          {
            float bottomX = (float)(leftCorner.getX() + value * triangleWidth);
            float topX = bottomX + skew;
            GeneralPath line = new GeneralPath();
            line.moveTo(topX, (float)topCorner.getY());
            line.lineTo(bottomX, (float)leftCorner.getY());
            g2.draw(line);
          }
        }
      }
      if (uncertaintyGridlines != null)
      {
        for (double value : uncertaintyGridlines)
        {
          if ((value > 0.0D) && (value < 1.0D))
          {
            float lineY = (float)(leftCorner.getY() - value * this.scale);
            GeneralPath line = new GeneralPath();
            line.moveTo(0.0F, lineY);
            line.lineTo(getWidth(), lineY);
            g2.draw(line);
          }
        }
      }
      g2.setClip(null);
    }

    this.atomHandle = new GeneralPath();
    this.atomHandle.moveTo(0.0F, 0.0F);
    this.atomHandle.lineTo(-atomHandleWidth, atomHandleHeight);
    this.atomHandle.lineTo(atomHandleWidth, atomHandleHeight);
    this.atomHandle.closePath();
    this.atomHandle.transform(
      AffineTransform.getTranslateInstance(this.offset.getX() + this.atomicity * this.scale * 2.0D * 0.57735D, this.offset.getY() + 
      this.scale));

    Line2D atomicityLine = new Line2D.Double((float)topCorner.getX(), (float)topCorner.getY(), (float)this.offset.getX() + 
      (float)(this.atomicity * this.scale * 2.0D * 0.57735D), this.offset.getY() + (float)this.scale);

    g2.setColor(Color.red);
    g2.setStroke(strokeThin);
    g2.fill(this.atomHandle);
    g2.draw(atomicityLine);

    g2.setColor(getForeground());

    if (paintControlLabels)
    {
      g2.drawString(atomicityText, 
        (float)(this.offset.getX() + this.atomicity * this.scale * 2.0D * 0.57735D - fm.stringWidth(atomicityText) / 2), (float)(
        this.offset.getY() + 
        this.scale + atomHandleHeight + fm.getHeight() / 2) + 
        buff);

      g2.setColor(getForeground());
      g2.drawString(uncertaintyText, (float)(this.offset.getX() - fm.stringWidth(uncertaintyText) / 2 + this.scale * 0.57735D), 
        (float)oh + fh - vh);
      g2.drawString(disbeliefText, (float)ow, (float)(this.offset.getY() + buff + this.scale));
      g2.drawString(beliefText, (float)(this.offset.getX() + 2.0D * this.scale * 0.57735D + 2 * buff), 
        (float)(this.offset.getY() + buff + this.scale));
    }

    g2.setStroke(strokeThick);
    g2.setColor(getForeground());
    g2.draw(this.triangle);

    synchronized (this)
    {
      for (OpinionPoint point : this.opinionPoints.values())
        point.drawPoint(g);
    }
  }

  public String getAtomicityText()
  {
    return this.atomicityText;
  }

  public void setAtomicityText(String atomicityText)
  {
    if (this.atomicityText.equals(atomicityText)) {
      return;
    }
    this.atomicityText = atomicityText;

    repaint();
  }

  public String getBeliefText()
  {
    return this.beliefText;
  }

  public void setBeliefText(String belilefText)
  {
    if (this.beliefText.equals(belilefText)) {
      return;
    }
    this.beliefText = belilefText;

    repaint();
  }

  public String getDisbeliefText()
  {
    return this.disbeliefText;
  }

  public void setDisbeliefText(String disbeliefText)
  {
    if (this.disbeliefText.equals(disbeliefText)) {
      return;
    }
    this.disbeliefText = disbeliefText;

    repaint();
  }

  public String getUncertaintyText()
  {
    return this.uncertaintyText;
  }

  public void setUncertaintyText(String uncertaintyText)
  {
    if (this.uncertaintyText.equals(uncertaintyText)) {
      return;
    }
    this.uncertaintyText = uncertaintyText;

    repaint();
  }

  public int getPointSize()
  {
    return this.pointSize;
  }

  public int pointCount()
  {
    return this.opinionPoints.size();
  }

  private boolean insideAtomicity(int x, int y)
  {
    return (this.atomHandle != null) && (this.atomHandle.contains(x, y));
  }

  public void setPointSize(int pointSize)
  {
    if (pointSize < 3) {
      pointSize = 3;
    }
    this.pointSize = pointSize;
  }

  public synchronized void clearOpinionPoints()
  {
    for (Iterator iter = this.opinionPoints.values().iterator(); iter.hasNext(); )
    {
      OpinionPoint point = (OpinionPoint)iter.next();

      if (point != null)
      {
        iter.remove();
        firePropertyChange("point", point, null);
      }
    }

    repaint();
  }

  public OpinionPoint getOpinionPoint(String reference)
  {
    if (reference == null) {
      throw new NullPointerException("Reference must not be null");
    }
    return (OpinionPoint)this.opinionPoints.get(reference);
  }

  public SubjectiveOpinion getOpinion(String reference)
  {
    if (reference == null) {
      throw new NullPointerException("Reference must not be null");
    }
    OpinionPoint point = (OpinionPoint)this.opinionPoints.get(reference);

    if (point == null) {
      return null;
    }
    return point.getOpinion();
  }

  public boolean hasOpinionPoint(String reference)
  {
    if (reference == null) {
      throw new NullPointerException("Reference must not be null");
    }
    return this.opinionPoints.containsKey(reference);
  }

  public synchronized boolean removeOpinionPoint(String reference)
  {
    if (reference == null) {
      throw new NullPointerException("Reference must not be null");
    }
    OpinionPoint point = (OpinionPoint)this.opinionPoints.remove(reference);

    if (point != null)
    {
      repaint();
      firePropertyChange("point", point, null);
    }

    return point != null;
  }

  public boolean isPaintControlLabels()
  {
    return this.paintControlLabels;
  }

  public void setPaintControlLabels(boolean paintControlLabels)
  {
    this.paintControlLabels = paintControlLabels;
  }

  public boolean isPaintPointLabels()
  {
    return this.paintPointLabels;
  }

  public void setPaintPointLabels(boolean paintPointLabels)
  {
    this.paintPointLabels = paintPointLabels;
  }

  public int getLabelWhiteSpace()
  {
    return this.labelWhiteSpace;
  }

  public void setLabelWhiteSpace(int labelWhiteSpace)
  {
    this.labelWhiteSpace = Math.max(0, labelWhiteSpace);
  }

  public Color getFillColor()
  {
    return this.fillColor;
  }

  public void setFillColor(Color fillColor)
  {
    if (fillColor == null) {
      throw new NullPointerException("FillColor must not be null");
    }
    this.fillColor = fillColor;
  }

  public double getGranularity()
  {
    return this.granularity;
  }

  public void setGranularity(double granularity)
  {
    this.granularity = Math.max(0.0D, granularity);
  }

  protected double adjust(double x)
  {
    return x == (0.0D / 0.0D) ? (0.0D / 0.0D) : Math.round(x * 10000000000.0D) / 10000000000.0D;
  }

  public Map<String, OpinionPoint> getOpinionPoints()
  {
    return Collections.unmodifiableMap(this.opinionPoints);
  }

  public synchronized Map<String, SubjectiveOpinion> getOpinions()
  {
    Map map = new TreeMap();

    for (OpinionPoint p : this.opinionPoints.values()) {
      map.put(p.getReference(), p.getOpinion());
    }
    return map;
  }

  public boolean isAutoSize()
  {
    return this.autoSize;
  }

  public void setAutoSize(boolean autoSize)
  {
    if (this.autoSize == autoSize) {
      return;
    }
    this.autoSize = autoSize;

    firePropertyChange("autoSize", !autoSize, autoSize);
  }

  private void setScaleInternal(double scale)
  {
    if (scale == this.scale) {
      return;
    }
    double old = this.scale;

    this.scale = scale;
  }

  public synchronized boolean isAtomicityEnabled()
  {
    return this.atomicityEnabled;
  }

  public synchronized void setAtomicityEnabled(boolean atomicityEnabled)
  {
    this.atomicityEnabled = atomicityEnabled;
  }

  public synchronized double[] getExpectationGridlines()
  {
    return this.expectationGridlines;
  }

  public synchronized void ExpectationGridlines(double[] boundaries)
  {
    this.expectationGridlines = boundaries;
  }

  public synchronized double[] getUncertaintyGridlines()
  {
    return this.uncertaintyGridlines;
  }

  public synchronized void setUncertaintyGridlines(double[] boundaries)
  {
    this.uncertaintyGridlines = boundaries;
  }

  public synchronized void setGridlines(FuzzyOpinionSet fuzzyOpinionSet)
  {
    if (fuzzyOpinionSet == null) {
      throw new NullPointerException("FuzzyOpinionSet must not be null");
    }
    setExpectationGridlines(fuzzyOpinionSet.getExpectationSet());
    setUncertaintyGridlines(fuzzyOpinionSet.getUncertaintySet());
  }

  public synchronized void setExpectationGridlines(FuzzySet boundaries)
  {
    if (boundaries == null) {
      throw new NullPointerException("FuzzySet must not be null");
    }
    this.expectationGridlines = boundaries.boundaries();
  }

  public synchronized void setUncertaintyGridlines(FuzzySet boundaries)
  {
    if (boundaries == null) {
      throw new NullPointerException("FuzzySet must not be null");
    }
    this.uncertaintyGridlines = boundaries.boundaries();
  }

  public synchronized boolean isPaintGrid()
  {
    return this.paintGrid;
  }

  public synchronized void setPaintGrid(boolean paintGrid)
  {
    this.paintGrid = paintGrid;
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

  public synchronized Color getGridColor()
  {
    return this.gridColor;
  }

  public synchronized void setGridColor(Color gridColor)
  {
    if (gridColor == null) {
      throw new NullPointerException("GridColor must not be null");
    }
    this.gridColor = gridColor;
  }

  public synchronized boolean isPaintFilled() {
    return this.paintFilled;
  }

  public synchronized void setPaintFilled(boolean paintFilled) {
    this.paintFilled = paintFilled;
  }

  private class MyMouseListener extends MouseAdapter
    implements MouseMotionListener
  {
    private OpinionTriangle triangle;
    private boolean dragAtomicity = false;

    private boolean dragged = false;

    private OpinionTriangle.OpinionPoint dragPoint = null;

    public MyMouseListener(OpinionTriangle triangle)
    {
      this.triangle = triangle;
    }

    public void mousePressed(MouseEvent e)
    {
      OpinionTriangle.OpinionPoint point = OpinionTriangle.this.locatePoint(e.getX(), e.getY());

      this.dragPoint = null;
      this.dragged = false;
      this.dragAtomicity = false;

      if ((point != null) && (point.isEnabled()))
      {
        this.dragPoint = point;
      }
      else if ((OpinionTriangle.this.isAtomicityEnabled()) && (OpinionTriangle.this.insideAtomicity(e.getX(), e.getY())) && (OpinionTriangle.this.isEnabled()))
      {
        this.dragAtomicity = true;
      }
    }

    public void mouseDragged(MouseEvent e)
    {
      if (!OpinionTriangle.this.isEnabled())
      {
        this.dragPoint = null;
        this.dragAtomicity = false;
        return;
      }

      if (this.dragPoint != null)
      {
        OpinionTriangle.OpinionPoint.access$1(this.dragPoint, e.getX(), e.getY());

        if (OpinionTriangle.this.actionListener != null)
          OpinionTriangle.this.actionListener.actionPerformed(new ActionEvent(this.triangle, 1001, "Opinion Changed"));
      }
      else if (this.dragAtomicity)
      {
        int x = e.getX();
        int y = e.getY();

        double olda = OpinionTriangle.this.atomicity;
        double tmp_a = (x - OpinionTriangle.this.offset.getX()) / OpinionTriangle.this.scale * 0.5D / 0.57735D;
        if (tmp_a < 0.0D)
          tmp_a = 0.0D;
        else if (tmp_a > 1.0D)
          tmp_a = 1.0D;
        else if ((0.5D - tmp_a) * (0.5D - tmp_a) < 0.0001D) {
          tmp_a = 0.5D;
        }
        OpinionTriangle.this.setAtomicity(tmp_a);

        OpinionTriangle.this.repaint();

        if (OpinionTriangle.this.actionListener != null)
          OpinionTriangle.this.actionListener.actionPerformed(new ActionEvent(this.triangle, 1001, "Opinion Changed"));
      }
    }

    public void mouseMoved(MouseEvent e)
    {
      int X = e.getX();
      int Y = e.getY();

      OpinionTriangle.OpinionPoint point = OpinionTriangle.this.locatePoint(X, Y);

      String ttt = null;

      if (point != null)
      {
        StringBuilder sb = new StringBuilder();

        sb.append(point.getReference());
        sb.append("=");
        sb.append(OpinionTriangle.this.opinionTextualizer.textualize(point.getOpinion()));

        ttt = sb.toString();

        if ((OpinionTriangle.this.isEnabled()) && (point.isEnabled()))
          OpinionTriangle.this.setCursor(new Cursor(12));
      }
      else if ((OpinionTriangle.this.insideAtomicity(X, Y)) && (OpinionTriangle.this.isAtomicityEnabled()))
      {
        StringBuilder sb = new StringBuilder();

        sb.append(OpinionTriangle.this.atomicityText);
        sb.append("=");
        sb.append(String.valueOf(OpinionTriangle.this.atomicity));

        ttt = sb.toString();
        OpinionTriangle.this.setCursor(new Cursor(12));
      }
      else
      {
        OpinionTriangle.this.setCursor(new Cursor(0));
      }

      OpinionTriangle.this.setToolTipText(ttt);
    }

    public void mouseReleased(MouseEvent e)
    {
      if ((this.dragged) && ((this.dragPoint != null) || (this.dragAtomicity)))
      {
        this.dragPoint = null;
        this.dragAtomicity = false;

        OpinionTriangle.this.repaint();

        if (OpinionTriangle.this.actionListener != null)
          OpinionTriangle.this.actionListener.actionPerformed(new ActionEvent(this.triangle, 1001, "Opinion Changed"));
      }
    }
  }

  public class OpinionPoint extends Point2D.Double
    implements Comparable<OpinionPoint>
  {
    private static final long serialVersionUID = 3257568416620361785L;
    private ActionListener actionListener = null;

    private OpinionTriangle parent = null;
    private static final double FLOOR = 0.005D;
    private SubjectiveOpinion opinion;
    private String text = "";

    private boolean paintLabels = false;

    private String reference = null;

    private boolean enabled = true;

    private Color color = Color.blue;

    private Ellipse2D circle = null;

    private boolean visible = true;

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private OpinionPoint(OpinionTriangle parent, Opinion opinion, String reference, String text)
    {
      if ((opinion == null) || (reference == null)) {
        throw new NullPointerException("Opinion and Reference must not be null");
      }
      if (parent == null) {
        throw new NullPointerException("Parent must not be null");
      }
      this.parent = parent;
      this.reference = reference;
      setOpinion(opinion);
      setText(text);
    }

    private OpinionPoint(OpinionTriangle parent, Opinion opinion, String reference)
    {
      this(parent, opinion, reference, null);
    }

    public synchronized void setOpinion(Opinion opinion)
    {
      Opinion old = this.opinion;
      this.opinion = new SubjectiveOpinion(opinion);

      if (OpinionTriangle.this.pointCount() == 1) {
        this.parent.setAtomicity(opinion.getAtomicity());
      }
      OpinionTriangle.this.repaint();

      this.support.firePropertyChange("opinion", old, this.opinion);
    }

    public synchronized SubjectiveOpinion getOpinion()
    {
      return new SubjectiveOpinion(this.opinion);
    }

    private boolean insidePoint(double xx, double yy)
    {
      return (this.circle != null) && (this.circle.contains(xx, yy));
    }

    private void setOpinion(double xx, double yy)
    {
      xx -= OpinionTriangle.this.offset.getX();
      yy -= OpinionTriangle.this.offset.getY();

      double d = -(((xx / 0.57735D - yy) / OpinionTriangle.this.scale - 1.0D) * 0.5D);
      double u = 1.0D - yy / OpinionTriangle.this.scale;

      d = Math.round(OpinionTriangle.this.adjust(d) / OpinionTriangle.this.granularity) * OpinionTriangle.this.granularity;
      u = Math.round(OpinionTriangle.this.adjust(u) / OpinionTriangle.this.granularity) * OpinionTriangle.this.granularity;

      if (d < 0.005D) {
        d = 0.0D;
      }
      if (u > 0.995D)
      {
        u = 1.0D;
        d = 0.0D;
      }

      if (d > 0.995D)
      {
        d = 1.0D;
        u = 0.0D;
      }

      double b = 1.0D - d - u;

      if (b < 0.005D)
      {
        b = 0.0D;
        d = 1.0D - u;
      }

      if (b > 0.995D)
      {
        b = 1.0D;
        u = 0.0D;
        d = 0.0D;
      }

      Opinion old = new SubjectiveOpinion(this.opinion);

      this.opinion.set(Math.max(0.0D, b), Math.max(0.0D, d), Math.max(0.0D, u));

      this.support.firePropertyChange("opinion", old, this.opinion);
    }

    private synchronized void drawPoint(Graphics g)
    {
      if ((OpinionTriangle.this.scale <= 0.0D) || (!isVisible())) {
        return;
      }

      setLocation(OpinionTriangle.this.offset.getX() + (
        0.5D + (1.0D - 2.0D * this.opinion.getDisbelief() + 1.0D - this.opinion.getUncertainty()) * OpinionTriangle.this.scale * 0.57735D), OpinionTriangle.this.offset.getY() + (
        0.5D + (1.0D - this.opinion.getUncertainty()) * OpinionTriangle.this.scale));

      Graphics2D g2 = (Graphics2D)g;
      FontMetrics fm = g2.getFontMetrics();

      Color oldColor = g2.getColor();
      try
      {
        this.circle = new Ellipse2D.Double(this.x - OpinionTriangle.this.getPointSize() / 2, this.y - OpinionTriangle.this.getPointSize() / 2, OpinionTriangle.this.getPointSize(), OpinionTriangle.this.getPointSize());
        Point2D expectationPoint = OpinionTriangle.this.getExpectationPoint(this.opinion.getExpectation());
        Line2D line = new Line2D.Double(this, expectationPoint);

        g2.setColor(this.color);
        g2.fill(this.circle);

        g2.draw(line);

        g2.setColor(oldColor);

        if ((this.text != null) && (OpinionTriangle.this.paintPointLabels))
          g2.drawString(this.text, (float)(this.x - fm.stringWidth(this.text) / 2.0D), (float)(this.y - OpinionTriangle.this.getPointSize() / 2.0D - 1.0D));
      }
      catch (RuntimeException e)
      {
        e.printStackTrace();
      }
      finally
      {
        g2.setColor(oldColor);
      }
    }

    private synchronized void dragPoint(double xm, double ym)
    {
      if (OpinionTriangle.this.triangle.contains(xm, ym))
      {
        this.x = xm;
        this.y = ym;
      }
      else
      {
        double d = -(((xm - OpinionTriangle.this.offset.getX()) / 0.57735D - (ym - OpinionTriangle.this.offset.getY())) / OpinionTriangle.this.scale - 1.0D) * 0.5D;
        double b = -((-(xm - OpinionTriangle.this.offset.getX()) / 0.57735D - (ym - OpinionTriangle.this.offset.getY())) / OpinionTriangle.this.scale + 1.0D) * 0.5D;
        double u = 1.0D - d - b;

        if (u * b > 0.0D)
        {
          if (u <= 0.0D)
          {
            d = 1.0D;
            u = 0.0D;
          }
          else if (d <= 0.0D)
          {
            u /= (b + u);
            d = 0.0D;
          }
        }
        else if (u * d > 0.0D)
        {
          if (u <= 0.0D)
          {
            d = 0.0D;
            u = 0.0D;
          }
          else
          {
            u /= (u + d);
            d = 1.0D - u;
          }

        }
        else if (d <= 0.0D)
        {
          u = 1.0D;
          d = 0.0D;
        }
        else
        {
          u = 0.0D;
          d /= (b + d);
        }

        setLocation(OpinionTriangle.this.offset.getX() + (0.5D + (1.0D - 2.0D * d + 1.0D - u) * OpinionTriangle.this.scale * 0.57735D), OpinionTriangle.this.offset.getY() + (
          0.5D + (1.0D - u) * OpinionTriangle.this.scale));
      }

      setOpinion(this.x, this.y);

      OpinionTriangle.this.repaint();
    }

    public synchronized double getAtomicity()
    {
      return this.opinion.getAtomicity();
    }

    public synchronized void setAtomicity(double atomicity)
    {
      double old = this.opinion.getAtomicity();
      this.opinion.setAtomicity(atomicity);
      this.support.firePropertyChange("atomicity", new Double(old), new Double(atomicity));
    }

    public String getText()
    {
      return this.text;
    }

    public void setText(String text)
    {
      if (text == null) {
        text = "";
      }
      if (this.text.equals(text)) {
        return;
      }
      this.text = text;

      OpinionTriangle.this.repaint();
    }

    private void setLocation(double b, double d, double u)
    {
      setLocation(OpinionTriangle.this.offset.getX() + (int)(0.5D + (1.0D - 2.0D * d + 1.0D - u) * OpinionTriangle.this.scale * 0.57735D), OpinionTriangle.this.offset.getY() + 
        (int)(0.5D + (1.0D - u) * OpinionTriangle.this.scale));
    }

    public boolean isEnabled()
    {
      return this.enabled;
    }

    public synchronized void setEnabled(boolean enabled)
    {
      if (this.enabled == enabled) {
        return;
      }
      this.enabled = enabled;

      this.support.firePropertyChange("enabled", !enabled, enabled);
    }

    public String getReference()
    {
      return this.reference;
    }

    public boolean isPaintLabels()
    {
      return this.paintLabels;
    }

    public void setPaintLabels(boolean paintLabels)
    {
      this.paintLabels = paintLabels;
    }

    public Color getColor()
    {
      return this.color;
    }

    public void setColor(Color color)
    {
      this.color = color;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
      this.support.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
      this.support.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
      this.support.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
      this.support.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners()
    {
      return this.support.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName)
    {
      return this.support.getPropertyChangeListeners(propertyName);
    }

    public boolean hasListeners(String propertyName)
    {
      return this.support.hasListeners(propertyName);
    }

    public int compareTo(OpinionPoint o)
    {
      return this.reference.compareTo(o.reference);
    }

    public synchronized boolean isVisible()
    {
      return this.visible;
    }

    public synchronized void setVisible(boolean visible)
    {
      if (this.visible == visible) {
        return;
      }
      this.visible = visible;

      this.support.firePropertyChange("visible", !visible, visible);
    }
  }
}