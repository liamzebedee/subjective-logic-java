package com.dstc.spectrum.visual;

import com.dstc.spectrum.opinion.SubjectiveOpinion;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OpinionTextPanel extends JPanel
{
  private PropertyChangeListener listener = new PropertyChangeListener()
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      OpinionTextPanel.this.handlePropertyChange(evt);
    }
  };

  private ActionListener actionListener = null;
  private static final long serialVersionUID = 3256718481314297396L;
  private Font labelFont = null;

  private JLabel titleBelief = null;

  private JLabel titleDisbelief = null;

  private JLabel titleUncertainty = null;

  private JLabel titleAtomicity = null;

  private JLabel titleExpectation = null;

  private OpinionTriangle opinionTriangle = null;

  private JLabel titleLabel = null;

  private Map<OpinionTriangle.OpinionPoint, JLabel[]> opinionLabels = new TreeMap();

  public OpinionTextPanel()
  {
    initialize();
  }

  private GridBagConstraints createTitleConstraints(int gridx, int gridy)
  {
    GridBagConstraints gridBagConstraints = new GridBagConstraints();

    gridBagConstraints.gridx = gridx;
    gridBagConstraints.gridy = gridy;
    gridBagConstraints.ipady = 2;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.fill = 2;
    gridBagConstraints.anchor = 17;

    return gridBagConstraints;
  }

  private void initialize()
  {
    setLayout(new GridBagLayout());
    setSize(182, 133);

    setOpaque(false);

    this.labelFont = getFont();

    this.titleExpectation = new JLabel();
    this.titleAtomicity = new JLabel();
    this.titleUncertainty = new JLabel();
    this.titleDisbelief = new JLabel();
    this.titleBelief = new JLabel();
    this.titleLabel = new JLabel();

    GridBagConstraints gridBagConstraints1 = createTitleConstraints(0, 1);
    GridBagConstraints gridBagConstraints2 = createTitleConstraints(0, 2);
    GridBagConstraints gridBagConstraints3 = createTitleConstraints(0, 3);
    GridBagConstraints gridBagConstraints4 = createTitleConstraints(0, 4);
    GridBagConstraints gridBagConstraints5 = createTitleConstraints(0, 5);
    GridBagConstraints gridBagConstraints6 = createTitleConstraints(0, 0);

    this.titleBelief.setText("Belief");
    this.titleDisbelief.setText("Disbelief");
    this.titleUncertainty.setText("Uncertainty");
    this.titleAtomicity.setText("Atomicity");
    this.titleExpectation.setText("Expectation");
    this.titleLabel.setText("Opinion about");

    add(this.titleBelief, gridBagConstraints1);
    add(this.titleDisbelief, gridBagConstraints2);
    add(this.titleUncertainty, gridBagConstraints3);
    add(this.titleAtomicity, gridBagConstraints4);
    add(this.titleExpectation, gridBagConstraints5);
    add(this.titleLabel, gridBagConstraints6);
  }

  private String doubleToString(double x)
  {
    return String.format("%1$1.2f", new Object[] { new Double(x) });
  }

  private synchronized void handlePropertyChange(PropertyChangeEvent evt)
  {
    JLabel[] labels;
    if ((evt.getSource() instanceof OpinionTriangle.OpinionPoint))
    {
      OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)evt.getSource();

      synchronized (point)
      {
        SubjectiveOpinion opinion = point.getOpinion();

        labels = (JLabel[])this.opinionLabels.get(point);

        labels[0].setText(point.getReference());
        labels[1].setText(doubleToString(opinion.getBelief()));
        labels[2].setText(doubleToString(opinion.getDisbelief()));
        labels[3].setText(doubleToString(opinion.getUncertainty()));
        labels[4].setText(doubleToString(opinion.getAtomicity()));
        labels[5].setText(doubleToString(opinion.getExpectation()));
      }
    }
    else if ((evt.getSource() instanceof OpinionTriangle))
    {
      OpinionTriangle control = (OpinionTriangle)evt.getSource();

      if ((evt.getOldValue() == null) && (evt.getNewValue() != null))
      {
        OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)evt.getNewValue();
        point.addPropertyChangeListener(this.listener);
      }
      else if ((evt.getNewValue() == null) && (evt.getOldValue() != null))
      {
        OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)evt.getOldValue();
        point.removePropertyChangeListener(this.listener);
      }

      synchronized (control)
      {
        int j;
        for (labels = this.opinionLabels.values().iterator(); labels.hasNext(); 
          j < size)
        {
          JLabel[] labels = (JLabel[])labels.next();
          j = 0; size = labels.length; continue;
          remove(labels[j]);

          j++;
        }

        this.opinionLabels.clear();

        Font font = getFont();

        int i = 0;
        int j;
        int size;
        for (int size = control.getOpinionPoints().values().iterator(); size.hasNext(); 
          j < size)
        {
          OpinionTriangle.OpinionPoint point = (OpinionTriangle.OpinionPoint)size.next();

          SubjectiveOpinion opinion = point.getOpinion();

          JLabel[] labels = new JLabel[6];

          for (int j = 0; j < 6; j++)
          {
            labels[j] = new JLabel();
            labels[j].setFont(font);
          }

          this.opinionLabels.put(point, labels);

          labels[0].setText(point.getReference());
          labels[1].setText(doubleToString(opinion.getBelief()));
          labels[2].setText(doubleToString(opinion.getDisbelief()));
          labels[3].setText(doubleToString(opinion.getUncertainty()));
          labels[4].setText(doubleToString(opinion.getAtomicity()));
          labels[5].setText(doubleToString(opinion.getExpectation()));

          i++;
          j = 0; size = labels.length; continue;
          add(labels[j], createTitleConstraints(i, j));

          j++;
        }
      }

    }

    repaint();
  }

  public OpinionTriangle getOpinionTriangle()
  {
    return this.opinionTriangle;
  }

  public void setOpinionTriangle(OpinionTriangle opinionTriangle)
  {
    if (opinionTriangle == null) {
      throw new NullPointerException("OpinionTriangle must not be mull");
    }
    if (opinionTriangle == this.opinionTriangle) {
      return;
    }
    this.opinionTriangle = opinionTriangle;

    opinionTriangle.addPropertyChangeListener("point", this.listener);
  }

  public String getTitle()
  {
    return this.titleLabel.getText();
  }

  public void setTitle(String text)
  {
    this.titleLabel.setText(text);
  }

  public void setFont(Font font)
  {
    super.setFont(font);

    if (this.opinionLabels != null)
    {
      int i;
      int size;
      for (Iterator localIterator = this.opinionLabels.values().iterator(); localIterator.hasNext(); 
        i < size)
      {
        JLabel[] labels = (JLabel[])localIterator.next();
        i = 0; size = labels.length; continue;
        labels[i].setFont(font);

        i++;
      }
    }
  }

  public Font getLabelFont() {
    return this.labelFont;
  }

  public void setLabelFont(Font font)
  {
    if (font == null) {
      throw new NullPointerException("Font must not be null");
    }
    this.labelFont = font;

    if (this.titleAtomicity != null) {
      this.titleAtomicity.setFont(font);
    }
    if (this.titleBelief != null) {
      this.titleBelief.setFont(font);
    }
    if (this.titleDisbelief != null) {
      this.titleDisbelief.setFont(font);
    }
    if (this.titleExpectation != null) {
      this.titleExpectation.setFont(font);
    }
    if (this.titleLabel != null) {
      this.titleLabel.setFont(font);
    }
    if (this.titleUncertainty != null)
      this.titleUncertainty.setFont(font);
  }
}