package com.dstc.spectrum.visual.demo;

import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.OpinionArithmeticException;
import com.dstc.spectrum.opinion.SubjectiveOpinion;
import com.dstc.spectrum.visual.OpinionOperator;
import com.dstc.spectrum.visual.OpinionOperator.Operator;
import com.dstc.spectrum.visual.OpinionTextPanel;
import com.dstc.spectrum.visual.OpinionTriangle;
import com.dstc.spectrum.visual.OpinionTriangle.OpinionPoint;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JApplet;
import javax.swing.JPanel;

public class MetricDemo extends JApplet
  implements PropertyChangeListener
{
  private static final long serialVersionUID = 3258126959954573109L;
  public static final SubjectiveOpinion BLAND_OPINION = new SubjectiveOpinion(0.333333D, 0.333333D, 0.333334D);

  private JPanel jContentPane = null;

  private OpinionOperator jOpinionOperator = null;

  private OpinionOperator jOpinionOperator1 = null;

  private OpinionTriangle jOpinionTriangle1 = null;

  private OpinionTriangle jOpinionTriangle2 = null;

  private OpinionTextPanel jOpinionTextPanel = null;

  private OpinionTextPanel jOpinionTextPanel1 = null;

  private OpinionTextPanel jOpinionTextPanel2 = null;

  private OpinionTriangle jOpinionTriangle = null;

  private String resultTitle = "";

  private Map<OpinionTriangle, Map<String, SubjectiveOpinion>> lastGoodValues = new HashMap();

  public MetricDemo()
  {
    init();
  }

  public void init()
  {
    setSize(640, 480);
    setContentPane(getJContentPane());
    setupApplet(getJOpinionOperator().getOperator());
  }

  private JPanel getJContentPane()
  {
    if (this.jContentPane == null)
    {
      GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
      this.jContentPane = new JPanel();
      this.jContentPane.setLayout(new GridBagLayout());
      gridBagConstraints7.gridx = 1;
      gridBagConstraints7.gridy = 0;
      gridBagConstraints7.gridwidth = 1;
      gridBagConstraints7.gridheight = 1;
      gridBagConstraints7.weightx = 0.0D;
      gridBagConstraints7.fill = 3;
      gridBagConstraints7.weighty = 1.0D;
      gridBagConstraints7.anchor = 10;
      gridBagConstraints14.gridx = 3;
      gridBagConstraints14.gridy = 0;
      gridBagConstraints14.fill = 3;
      gridBagConstraints14.weightx = 0.0D;
      gridBagConstraints14.weighty = 1.0D;
      gridBagConstraints15.gridx = 0;
      gridBagConstraints15.gridy = 0;
      gridBagConstraints15.fill = 1;
      gridBagConstraints15.insets = new Insets(5, 0, 0, 0);
      gridBagConstraints15.weighty = 1.0D;
      gridBagConstraints15.weightx = 1.0D;
      gridBagConstraints16.gridx = 2;
      gridBagConstraints16.gridy = 0;
      gridBagConstraints16.fill = 1;
      gridBagConstraints16.insets = new Insets(5, 5, 0, 5);
      gridBagConstraints16.weightx = 1.0D;
      gridBagConstraints16.weighty = 2.0D;
      gridBagConstraints16.ipady = 4;
      gridBagConstraints17.gridx = 2;
      gridBagConstraints17.gridy = 1;
      gridBagConstraints17.fill = 0;
      gridBagConstraints17.anchor = 11;
      gridBagConstraints17.weighty = 1.0D;
      gridBagConstraints17.gridwidth = 1;
      gridBagConstraints18.gridx = 4;
      gridBagConstraints18.gridy = 0;
      gridBagConstraints18.fill = 1;
      gridBagConstraints18.insets = new Insets(5, 5, 0, 5);
      gridBagConstraints18.weightx = 1.0D;
      gridBagConstraints18.weighty = 2.0D;
      gridBagConstraints18.ipady = 4;
      gridBagConstraints19.gridx = 4;
      gridBagConstraints19.gridy = 1;
      gridBagConstraints19.fill = 0;
      gridBagConstraints19.anchor = 11;
      gridBagConstraints19.weighty = 1.0D;
      gridBagConstraints20.gridx = 0;
      gridBagConstraints20.gridy = 0;
      gridBagConstraints20.weightx = 1.0D;
      gridBagConstraints20.weighty = 2.0D;
      gridBagConstraints20.fill = 1;
      gridBagConstraints20.ipady = 4;
      gridBagConstraints20.insets = new Insets(5, 5, 0, 5);
      gridBagConstraints21.gridx = 0;
      gridBagConstraints21.gridy = 1;
      gridBagConstraints21.fill = 0;
      gridBagConstraints21.anchor = 11;
      gridBagConstraints21.ipadx = 1;
      gridBagConstraints21.insets = new Insets(0, 10, 0, 0);
      this.jContentPane.add(getJOpinionTriangle(), gridBagConstraints20);
      this.jContentPane.add(getJOpinionTextPanel(), gridBagConstraints21);
      this.jContentPane.add(getJOpinionOperator(), gridBagConstraints7);
      this.jContentPane.add(getJOpinionTriangle1(), gridBagConstraints16);
      this.jContentPane.add(getJOpinionTextPanel1(), gridBagConstraints17);
      this.jContentPane.add(getJOpinionOperator1(), gridBagConstraints14);
      this.jContentPane.add(getJOpinionTriangle2(), gridBagConstraints18);
      this.jContentPane.add(getJOpinionTextPanel2(), gridBagConstraints19);
      this.jContentPane.add(getJOpinionTriangle(), gridBagConstraints15);
    }
    return this.jContentPane;
  }

  private OpinionOperator getJOpinionOperator()
  {
    if (this.jOpinionOperator == null)
    {
      this.jOpinionOperator = new OpinionOperator();
      this.jOpinionOperator.setFont(new Font("Dialog", 0, 10));
      this.jOpinionOperator.setOperatorSize(25);
      this.jOpinionOperator.setMinimumSize(new Dimension(60, 60));
      this.jOpinionOperator.setPreferredSize(new Dimension(75, 75));
      this.jOpinionOperator.addPropertyChangeListener("operator", new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          if ((evt.getNewValue() instanceof OpinionOperator.Operator))
            MetricDemo.this.setupApplet((OpinionOperator.Operator)evt.getNewValue());
        }
      });
    }
    return this.jOpinionOperator;
  }

  private OpinionOperator getJOpinionOperator1()
  {
    if (this.jOpinionOperator1 == null)
    {
      this.jOpinionOperator1 = new OpinionOperator();
      this.jOpinionOperator1.setOperator(OpinionOperator.Operator.Equals);
      this.jOpinionOperator1.setFont(new Font("Dialog", 0, 10));
      this.jOpinionOperator1.setMinimumSize(new Dimension(30, 30));
      this.jOpinionOperator1.setPreferredSize(new Dimension(75, 75));
    }
    return this.jOpinionOperator1;
  }

  private OpinionTriangle getJOpinionTriangle1()
  {
    if (this.jOpinionTriangle1 == null)
    {
      this.jOpinionTriangle1 = new OpinionTriangle();
      this.jOpinionTriangle1.setUncertaintyText("u");
      this.jOpinionTriangle1.setDisbeliefText("d");
      this.jOpinionTriangle1.setBeliefText("b");
      this.jOpinionTriangle1.setAtomicityText("a");
      this.jOpinionTriangle1.setPreferredSize(new Dimension(80, 80));
      this.jOpinionTriangle1.setGranularity(0.01D);
      this.jOpinionTriangle1.addPropertyChangeListener("atomicity", this);
      this.jOpinionTriangle1.addPropertyChangeListener("point", this);
    }
    return this.jOpinionTriangle1;
  }

  private OpinionTriangle getJOpinionTriangle2()
  {
    if (this.jOpinionTriangle2 == null)
    {
      this.jOpinionTriangle2 = new OpinionTriangle();
      this.jOpinionTriangle2.setEnabled(false);
      this.jOpinionTriangle2.setUncertaintyText("u");
      this.jOpinionTriangle2.setDisbeliefText("d");
      this.jOpinionTriangle2.setBeliefText("b");
      this.jOpinionTriangle2.setAtomicityText("a");
      this.jOpinionTriangle2.setPreferredSize(new Dimension(80, 80));
      this.jOpinionTriangle2.setPaintPointLabels(false);
    }
    return this.jOpinionTriangle2;
  }

  private OpinionTextPanel getJOpinionTextPanel()
  {
    if (this.jOpinionTextPanel == null)
    {
      this.jOpinionTextPanel = new OpinionTextPanel();
      this.jOpinionTextPanel.setOpinionTriangle(getJOpinionTriangle());
      this.jOpinionTextPanel.setPreferredSize(new Dimension(200, 120));
    }
    return this.jOpinionTextPanel;
  }

  private OpinionTextPanel getJOpinionTextPanel1()
  {
    if (this.jOpinionTextPanel1 == null)
    {
      this.jOpinionTextPanel1 = new OpinionTextPanel();
      this.jOpinionTextPanel1.setOpinionTriangle(getJOpinionTriangle1());
      this.jOpinionTextPanel1.setPreferredSize(new Dimension(200, 120));
    }
    return this.jOpinionTextPanel1;
  }

  private OpinionTextPanel getJOpinionTextPanel2()
  {
    if (this.jOpinionTextPanel2 == null)
    {
      this.jOpinionTextPanel2 = new OpinionTextPanel();
      this.jOpinionTextPanel2.setOpinionTriangle(getJOpinionTriangle2());
      this.jOpinionTextPanel2.setPreferredSize(new Dimension(200, 120));
    }
    return this.jOpinionTextPanel2;
  }

  private OpinionTriangle getJOpinionTriangle()
  {
    if (this.jOpinionTriangle == null)
    {
      this.jOpinionTriangle = new OpinionTriangle();
      this.jOpinionTriangle.setDisbeliefText("d");
      this.jOpinionTriangle.setBeliefText("b");
      this.jOpinionTriangle.setAtomicityText("a");
      this.jOpinionTriangle.setUncertaintyText("u");
      this.jOpinionTriangle.setPreferredSize(new Dimension(80, 80));
      this.jOpinionTriangle.setGranularity(0.01D);
      this.jOpinionTriangle.addPropertyChangeListener("atomicity", this);
      this.jOpinionTriangle.addPropertyChangeListener("point", this);
    }

    return this.jOpinionTriangle;
  }

  private synchronized void setupApplet(OpinionOperator.Operator operator) throws OpinionArithmeticException
  {
    OpinionTriangle A = getJOpinionTriangle();
    OpinionTextPanel Apanel = getJOpinionTextPanel();

    OpinionTriangle B = getJOpinionTriangle1();
    OpinionTextPanel Bpanel = getJOpinionTextPanel1();

    OpinionTriangle C = getJOpinionTriangle2();
    OpinionTextPanel Cpanel = getJOpinionTextPanel2();

    boolean visible = true;

    SubjectiveOpinion op1 = BLAND_OPINION;
    SubjectiveOpinion op2 = BLAND_OPINION;

    for (OpinionTriangle.OpinionPoint p : A.getOpinionPoints().values())
    {
      op1 = p.getOpinion();
      break;
    }

    for (OpinionTriangle.OpinionPoint p : B.getOpinionPoints().values())
    {
      op2 = p.getOpinion();
      break;
    }

    A.clearOpinionPoints();
    B.clearOpinionPoints();

    if (operator == OpinionOperator.Operator.Complement)
    {
      visible = false;

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("x", op1);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "NOT x";
    }
    else if (operator == OpinionOperator.Operator.MaximizedUncertainty)
    {
      visible = false;

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("x", op1);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x MaxU y";
    }
    else if (operator == OpinionOperator.Operator.AdjustedExpectation)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Desired Expectation");
      OpinionTriangle.OpinionPoint p = B.createOpinionPoint("y", SubjectiveOpinion.createVacuousOpinion(op2.getExpectation()));
      p.setVisible(false);

      Cpanel.setTitle("Adjusted Opinion");
      this.resultTitle = "x ADJ y";
    }
    else if (operator == OpinionOperator.Operator.Average)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "avg(x,y)";
    }
    else if (operator == OpinionOperator.Operator.Erosion)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Erosion Factor");
      OpinionTriangle.OpinionPoint p = B.createOpinionPoint("y", SubjectiveOpinion.createVacuousOpinion(op2.getExpectation()));
      p.setVisible(false);

      Cpanel.setTitle("Eroded Opinion");
      this.resultTitle = "x'";
    }
    else if (operator == OpinionOperator.Operator.Multiplication)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x AND y";
    }
    else if (operator == OpinionOperator.Operator.CoMultiplication)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x OR y";
    }
    else if (operator == OpinionOperator.Operator.Division)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x and y", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x";
    }
    else if (operator == OpinionOperator.Operator.CoDivision)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x or y", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x";
    }
    else if (operator == OpinionOperator.Operator.Discount)
    {
      Apanel.setTitle("A's opinion");
      A.createOpinionPoint("about B", op1);

      Bpanel.setTitle("B's opinion");
      B.createOpinionPoint("about x", op2);

      Cpanel.setTitle("A's opinion");
      this.resultTitle = "about x";
    }
    else if (operator == OpinionOperator.Operator.Consensus)
    {
      Apanel.setTitle("A's opinion");
      A.createOpinionPoint("about x", op1);

      Bpanel.setTitle("B's opinion");
      B.createOpinionPoint("about x", op2);

      Cpanel.setTitle("A and B's opinion");
      this.resultTitle = "about x";
    }
    else if (operator == OpinionOperator.Operator.Addition)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x + y";
    }
    else if (operator == OpinionOperator.Operator.Subtraction)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      B.createOpinionPoint("y", op2);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "x - y";
    }
    else if (operator == OpinionOperator.Operator.ConditionalInference)
    {
      Apanel.setTitle("Opinion about");
      A.createOpinionPoint("x", op1);

      Bpanel.setTitle("Opinion about");
      SubjectiveOpinion op2a = op2.not();
      B.createOpinionPoint("y|x", op2, "y|x").setPaintLabels(true);
      B.createOpinionPoint("y|¬x", op2a, "y|¬x").setPaintLabels(true);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "y";
    }
    else if (operator == OpinionOperator.Operator.ReverseConditionalInference)
    {
      Apanel.setTitle("Opinion about");
      OpinionTriangle.OpinionPoint p = A.createOpinionPoint("br(y)", SubjectiveOpinion.createVacuousOpinion(op1.getAtomicity()));
      p.setVisible(false);

      Bpanel.setTitle("Opinion about");

      op2 = BLAND_OPINION;
      op2a = new SubjectiveOpinion(0.1D, 0.8D, 0.1D);
      SubjectiveOpinion op2b = op2a.not();

      B.createOpinionPoint("x", op2, "x").setPaintLabels(true);
      B.createOpinionPoint("x|¬y", op2a, "x|¬y").setPaintLabels(true);
      B.createOpinionPoint("x|y", op2b, "x|y").setPaintLabels(true);

      Cpanel.setTitle("Opinion about");
      this.resultTitle = "y";
    }

    A.setVisible(visible);
    Apanel.setVisible(visible);
    try
    {
      if (operator != null)
        operate(A, B, C, this.resultTitle);
    }
    catch (OpinionArithmeticException ex)
    {
      resetToBlandValues(A);
      resetToBlandValues(B);
      operate(A, B, C, this.resultTitle);
    }
    finally
    {
      repaint();
    }
  }

  private void resetToBlandValues(OpinionTriangle control)
  {
    for (OpinionTriangle.OpinionPoint point : control.getOpinionPoints().values()) {
      point.setOpinion(BLAND_OPINION);
    }
    control.repaint();
  }

  private void recalculate(int iteration)
  {
    label94: synchronized (this)
    {
      try
      {
        operate(getJOpinionTriangle(), getJOpinionTriangle1(), getJOpinionTriangle2(), this.resultTitle);
        storeLastKnownGoodValues();
      }
      catch (OpinionArithmeticException ex)
      {
        switch (iteration)
        {
        case 0:
          retrieveLastKnownGoodValues();
          recalculate(1);
        case 1:
        }
      }
      resetToBlandValues(getJOpinionTriangle());
      resetToBlandValues(getJOpinionTriangle1());
      recalculate(2);
      break label94;

      throw ex;
    }
  }

  private void recalculate()
  {
    recalculate(0);
  }

  private void storeLastKnownGoodValues()
  {
    this.lastGoodValues.clear();
    this.lastGoodValues.put(getJOpinionTriangle(), getJOpinionTriangle().getOpinions());
    this.lastGoodValues.put(getJOpinionTriangle1(), getJOpinionTriangle1().getOpinions());
  }

  private void retrieveLastKnownGoodValues()
  {
    retrieveLastKnownGoodValues(getJOpinionTriangle());
    retrieveLastKnownGoodValues(getJOpinionTriangle1());
  }

  private void retrieveLastKnownGoodValues(OpinionTriangle control)
  {
    Map map = (Map)this.lastGoodValues.get(control);

    if (map == null) {
      return;
    }
    control.clearOpinionPoints();

    for (Map.Entry entry : map.entrySet())
      control.createOpinionPoint((String)entry.getKey(), (Opinion)entry.getValue());
  }

  public void propertyChange(PropertyChangeEvent evt)
  {
    if ((evt.getSource() instanceof OpinionTriangle.OpinionPoint))
    {
      recalculate();
    }
    else if ((evt.getSource() instanceof OpinionTriangle))
    {
      OpinionTriangle control = (OpinionTriangle)evt.getSource();

      if (evt.getPropertyName().equals("atomicity"))
      {
        recalculate();
      }
      else if (evt.getPropertyName().equals("point"))
      {
        if ((evt.getOldValue() == null) && (evt.getNewValue() != null))
        {
          OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)evt.getNewValue();
          point.addPropertyChangeListener("opinion", this);
        }
        else if ((evt.getNewValue() == null) && (evt.getOldValue() != null))
        {
          OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)evt.getOldValue();
          point.removePropertyChangeListener("opinion", this);
        }
      }
    }
  }

  private synchronized void operate(OpinionTriangle A, OpinionTriangle B, OpinionTriangle C, String reference)
  {
    OpinionOperator.Operator operator = getJOpinionOperator().getOperator();

    if (operator == OpinionOperator.Operator.Equals) {
      return;
    }

    OpinionTriangle.OpinionPoint point = null;

    C.clearOpinionPoints();

    SubjectiveOpinion[] opA = (SubjectiveOpinion[])null;
    SubjectiveOpinion[] opB = (SubjectiveOpinion[])null;
    SubjectiveOpinion result = null;

    opA = new SubjectiveOpinion[A.pointCount()];
    opB = new SubjectiveOpinion[B.pointCount()];
    int i;
    synchronized (A)
    {
      i = 0;
      for (OpinionTriangle.OpinionPoint p : A.getOpinionPoints().values()) {
        opA[(i++)] = p.getOpinion();
      }
    }
    synchronized (B)
    {
      i = 0;
      for (OpinionTriangle.OpinionPoint p : B.getOpinionPoints().values()) {
        opB[(i++)] = p.getOpinion();
      }
    }
    try
    {
      if (operator == OpinionOperator.Operator.Complement)
      {
        result = opB[0].not();
      }
      else if (operator == OpinionOperator.Operator.AdjustedExpectation)
      {
        result = opA[0].adjustExpectation(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Average)
      {
        result = opA[0].average(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Erosion)
      {
        result = opA[0].erode(opB[0].getExpectation());
      }
      else if (operator == OpinionOperator.Operator.MaximizedUncertainty)
      {
        result = opB[0].uncertainOpinion();
      }
      else if (operator == OpinionOperator.Operator.Multiplication)
      {
        result = opA[0].and(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.CoMultiplication)
      {
        result = opA[0].or(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Division)
      {
        result = opA[0].unAnd(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.CoDivision)
      {
        result = opA[0].unOr(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Discount)
      {
        result = opA[0].discount(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Consensus)
      {
        result = opA[0].fuse(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Addition)
      {
        result = opA[0].add(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.Subtraction)
      {
        result = opA[0].subtract(opB[0]);
      }
      else if (operator == OpinionOperator.Operator.ConditionalInference)
      {
        if (B.pointCount() > 1)
          result = opA[0].deduce(B.getOpinionPoint("y|x").getOpinion(), B.getOpinionPoint("y|¬x").getOpinion());
      }
      else if (operator == OpinionOperator.Operator.ReverseConditionalInference)
      {
        if (B.pointCount() > 2) {
          result = B.getOpinionPoint("x").getOpinion().abduce(B.getOpinionPoint("x|y").getOpinion(), B.getOpinionPoint("x|¬y").getOpinion(), opA[0].getExpectation());
        }
      }
    }
    catch (OpinionArithmeticException oae)
    {
      result = null;
    }

    synchronized (C)
    {
      C.clearOpinionPoints();

      if (result != null)
        C.setAtomicity(result.getAtomicity());
      else {
        C.setAtomicity(0.0D);
      }
      if (result != null) {
        C.createOpinionPoint(reference, result, reference);
      }
    }
    repaint();
  }
}