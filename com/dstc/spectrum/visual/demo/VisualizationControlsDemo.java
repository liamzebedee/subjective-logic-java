package com.dstc.spectrum.visual.demo;

import com.dstc.spectrum.opinion.BasicFuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzyOpinionSet;
import com.dstc.spectrum.opinion.FuzzyOpinionTextualizer;
import com.dstc.spectrum.opinion.Opinion;
import com.dstc.spectrum.opinion.OpinionTextualizer;
import com.dstc.spectrum.opinion.SubjectiveOpinion;
import com.dstc.spectrum.visual.BayesianSlider;
import com.dstc.spectrum.visual.ExpectationSlider;
import com.dstc.spectrum.visual.FuzzyBar;
import com.dstc.spectrum.visual.FuzzyLabel;
import com.dstc.spectrum.visual.OpinionTextPanel;
import com.dstc.spectrum.visual.OpinionTriangle;
import com.dstc.spectrum.visual.OpinionTriangle.OpinionPoint;
import com.dstc.spectrum.visual.ProbabilityDensityGraph;
import com.dstc.spectrum.visual.ProbabilityDensityGraph.Series;
import com.dstc.spectrum.visual.RenderStyle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VisualizationControlsDemo extends JApplet
{
  private FuzzyOpinionSet opinionSet = new BasicFuzzyOpinionSet();

  private OpinionTextualizer TEXTUALIZER = new FuzzyOpinionTextualizer(this.opinionSet)
  {
    public String textualize(Opinion opinion)
    {
      if (opinion == null) {
        throw new NullPointerException("Opinion must not be null.");
      }
      StringBuilder sb = new StringBuilder(super.textualize(opinion));
      sb.append(" ");
      sb.append(opinion.toString());

      return sb.toString();
    }
  };

  private static final Color GREEN = new Color(40, 190, 40);
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

  private OpinionTextPanel jOpinionTextPanel = null;

  private OpinionTriangle jOpinionTriangle = null;

  private ExpectationSlider opinionBar = null;

  private ProbabilityDensityGraph betaPDFGraph = null;

  private BayesianSlider triColorOpinionBar = null;

  private JLabel jLabel = null;

  private JLabel jLabel1 = null;

  private JLabel jLabel2 = null;

  private JLabel jLabel3 = null;

  private FuzzyBar fuzzyBar = null;

  private FuzzyLabel fuzzyLabel3 = null;

  private FuzzyLabel fuzzyLabel = null;

  private FuzzyLabel fuzzyLabel1 = null;

  private JCheckBox jCheckBox = null;

  private JCheckBox jCheckBox1 = null;

  public VisualizationControlsDemo()
  {
    init();
  }

  public void init()
  {
    setSize(593, 500);
    setContentPane(getJContentPane());

    OpinionTriangle.OpinionPoint x = this.jOpinionTriangle.createOpinionPoint("X", new SubjectiveOpinion(0.33D, 0.33D, 0.34D), "x");
    x.setPaintLabels(true);
    OpinionTriangle.OpinionPoint y = this.jOpinionTriangle.createOpinionPoint("Y", new SubjectiveOpinion(0.8D, 0.1D, 0.1D), "y");
    y.setPaintLabels(true);
    y.setColor(Color.MAGENTA);
    OpinionTriangle.OpinionPoint z = this.jOpinionTriangle.createOpinionPoint("Z", new SubjectiveOpinion(0.1D, 0.8D, 0.1D), "z");
    z.setPaintLabels(true);
    z.setColor(GREEN);

    for (OpinionTriangle.OpinionPoint point : this.jOpinionTriangle.getOpinionPoints().values()) {
      point.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent evt)
        {
          VisualizationControlsDemo.this.updateOpinion();
        }
      });
    }
    updateOpinion();
  }

  private void updateOpinion()
  {
    for (Map.Entry entry : this.jOpinionTriangle.getOpinionPoints().entrySet())
    {
      String reference = (String)entry.getKey();
      SubjectiveOpinion opinion = ((OpinionTriangle.OpinionPoint)entry.getValue()).getOpinion();

      if (reference.equals("X"))
      {
        getOpinionBar().setOpinion(opinion);
        getFuzzyLabel().setOpinion(opinion);
      }
      else if (reference.equals("Y"))
      {
        getTriColorOpinionBar().setOpinion(opinion);
        getFuzzyLabel1().setOpinion(opinion);
      }
      else if (reference.equals("Z"))
      {
        getFuzzyBar().setOpinion(opinion);
        getFuzzyLabel3().setOpinion(opinion);
      }

      ProbabilityDensityGraph.Series series = this.betaPDFGraph.putOpinion(reference, opinion);

      series.setDrawCurve(true);
      series.setFillCurve(false);
      series.setStroke(new BasicStroke(1.5F, 1, 1));

      if (reference.equals("Y"))
        series.setColor(Color.MAGENTA);
      else if (reference.equals("Z"))
        series.setColor(GREEN);
    }
  }

  private JPanel getJContentPane()
  {
    if (this.jContentPane == null)
    {
      GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
      this.jLabel3 = new JLabel();
      GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
      this.jLabel2 = new JLabel();
      this.jLabel1 = new JLabel();
      this.jLabel = new JLabel();
      GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
      this.jContentPane = new JPanel();
      this.jContentPane.setLayout(new GridBagLayout());
      gridBagConstraints2.gridx = 0;
      gridBagConstraints2.gridy = 0;
      gridBagConstraints2.fill = 1;
      gridBagConstraints2.weighty = 5.0D;
      gridBagConstraints2.weightx = 1.0D;
      gridBagConstraints2.insets = new Insets(0, 5, 0, 5);
      gridBagConstraints2.ipadx = 5;
      gridBagConstraints2.gridwidth = 3;
      gridBagConstraints3.gridx = 0;
      gridBagConstraints3.gridy = 7;
      gridBagConstraints3.fill = 2;
      gridBagConstraints3.weighty = 0.5D;
      gridBagConstraints3.anchor = 11;
      gridBagConstraints3.insets = new Insets(20, 0, 0, 0);
      gridBagConstraints3.weightx = 1.0D;
      gridBagConstraints3.gridwidth = 3;
      gridBagConstraints3.gridheight = 2;
      gridBagConstraints10.gridx = 2;
      gridBagConstraints10.gridy = 2;
      gridBagConstraints10.insets = new Insets(3, 5, 0, 25);
      gridBagConstraints10.fill = 2;
      gridBagConstraints10.weighty = 0.0D;
      gridBagConstraints1.gridx = 3;
      gridBagConstraints1.gridy = 0;
      gridBagConstraints1.gridheight = 8;
      gridBagConstraints1.fill = 1;
      gridBagConstraints1.weightx = 1.0D;
      gridBagConstraints11.gridx = 2;
      gridBagConstraints11.gridy = 4;
      gridBagConstraints11.fill = 2;
      gridBagConstraints11.insets = new Insets(3, 5, 0, 25);
      gridBagConstraints11.weighty = 0.0D;
      gridBagConstraints21.gridx = 0;
      gridBagConstraints21.gridy = 1;
      gridBagConstraints21.insets = new Insets(3, 10, 0, 0);
      gridBagConstraints21.gridheight = 2;
      this.jLabel.setText("X");
      this.jLabel.setFont(new Font("Microsoft Sans Serif", 1, 24));
      gridBagConstraints31.gridx = 0;
      gridBagConstraints31.gridy = 3;
      gridBagConstraints31.insets = new Insets(10, 10, 0, 0);
      gridBagConstraints31.gridheight = 2;
      this.jLabel1.setText("Y");
      this.jLabel1.setFont(new Font("Microsoft Sans Serif", 1, 24));
      gridBagConstraints4.gridx = 0;
      gridBagConstraints4.gridy = 5;
      gridBagConstraints4.insets = new Insets(10, 10, 0, 0);
      gridBagConstraints4.gridheight = 2;
      this.jLabel2.setText("Z");
      this.jLabel2.setFont(new Font("Microsoft Sans Serif", 1, 24));
      gridBagConstraints5.gridx = 3;
      gridBagConstraints5.gridy = 8;
      gridBagConstraints5.insets = new Insets(10, 15, 0, 0);
      this.jLabel3.setText("Beta Probability Density");
      this.jLabel3.setFont(new Font("Arial", 3, 14));
      gridBagConstraints22.gridx = 2;
      gridBagConstraints22.gridy = 6;
      gridBagConstraints22.insets = new Insets(3, 5, 0, 25);
      gridBagConstraints22.fill = 2;
      gridBagConstraints51.gridx = 2;
      gridBagConstraints51.gridy = 5;
      gridBagConstraints51.insets = new Insets(10, 5, 0, 25);
      gridBagConstraints51.fill = 2;
      gridBagConstraints51.weighty = 0.0D;
      gridBagConstraints12.gridx = 2;
      gridBagConstraints12.gridy = 1;
      gridBagConstraints12.insets = new Insets(10, 10, 0, 25);
      gridBagConstraints12.fill = 2;
      gridBagConstraints23.gridx = 2;
      gridBagConstraints23.gridy = 3;
      gridBagConstraints23.fill = 2;
      gridBagConstraints23.insets = new Insets(10, 10, 0, 25);
      gridBagConstraints41.gridx = 1;
      gridBagConstraints41.gridy = 1;
      gridBagConstraints41.gridheight = 2;
      gridBagConstraints52.gridx = 1;
      gridBagConstraints52.gridy = 3;
      gridBagConstraints52.gridheight = 2;
      this.jContentPane.add(getJOpinionTriangle(), gridBagConstraints2);
      this.jContentPane.add(getJOpinionTextPanel(), gridBagConstraints3);
      this.jContentPane.add(getOpinionBar(), gridBagConstraints10);
      this.jContentPane.add(getFuzzyLabel3(), gridBagConstraints51);
      this.jContentPane.add(getBetaPDFGraph(), gridBagConstraints1);
      this.jContentPane.add(getTriColorOpinionBar(), gridBagConstraints11);
      this.jContentPane.add(this.jLabel, gridBagConstraints21);
      this.jContentPane.add(this.jLabel1, gridBagConstraints31);
      this.jContentPane.add(this.jLabel2, gridBagConstraints4);
      this.jContentPane.add(this.jLabel3, gridBagConstraints5);
      this.jContentPane.add(getFuzzyBar(), gridBagConstraints22);
      this.jContentPane.add(getFuzzyLabel(), gridBagConstraints12);
      this.jContentPane.add(getFuzzyLabel1(), gridBagConstraints23);
      this.jContentPane.add(getJCheckBox(), gridBagConstraints41);
      this.jContentPane.add(getJCheckBox1(), gridBagConstraints52);
    }
    return this.jContentPane;
  }

  private OpinionTextPanel getJOpinionTextPanel()
  {
    if (this.jOpinionTextPanel == null)
    {
      this.jOpinionTextPanel = new OpinionTextPanel();
      this.jOpinionTextPanel.setOpinionTriangle(getJOpinionTriangle());
    }
    return this.jOpinionTextPanel;
  }

  private OpinionTriangle getJOpinionTriangle()
  {
    if (this.jOpinionTriangle == null)
    {
      this.jOpinionTriangle = new OpinionTriangle();
      this.jOpinionTriangle.setGridlines(this.opinionSet);
      this.jOpinionTriangle.setOpinionTextualizer(this.TEXTUALIZER);
      this.jOpinionTriangle.setPaintPointLabels(true);
      this.jOpinionTriangle.setPaintGrid(true);
      this.jOpinionTriangle.setPreferredSize(new Dimension(200, 200));
    }

    return this.jOpinionTriangle;
  }

  private ExpectationSlider getOpinionBar()
  {
    if (this.opinionBar == null)
    {
      this.opinionBar = new ExpectationSlider();
      this.opinionBar.setPreferredSize(new Dimension(180, 32));
      this.opinionBar.setBarHeight(12);
      this.opinionBar.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          VisualizationControlsDemo.this.getJOpinionTriangle().getOpinionPoint("X").setOpinion(VisualizationControlsDemo.this.opinionBar.getOpinion());
        }
      });
    }
    return this.opinionBar;
  }

  private ProbabilityDensityGraph getBetaPDFGraph()
  {
    if (this.betaPDFGraph == null)
    {
      this.betaPDFGraph = new ProbabilityDensityGraph();
      this.betaPDFGraph.setBackground(Color.white);
      this.betaPDFGraph.setOpinionTextualizer(this.TEXTUALIZER);
    }
    return this.betaPDFGraph;
  }

  private BayesianSlider getTriColorOpinionBar()
  {
    if (this.triColorOpinionBar == null)
    {
      this.triColorOpinionBar = new BayesianSlider();
      this.triColorOpinionBar.setPreferredSize(new Dimension(180, 31));
      this.triColorOpinionBar.setBarHeight(12);
      this.triColorOpinionBar.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          VisualizationControlsDemo.this.getJOpinionTriangle().getOpinionPoint("Y").setOpinion(VisualizationControlsDemo.this.triColorOpinionBar.getOpinion());
        }
      });
    }
    return this.triColorOpinionBar;
  }

  private FuzzyBar getFuzzyBar()
  {
    if (this.fuzzyBar == null)
    {
      this.fuzzyBar = new FuzzyBar();
      this.fuzzyBar.setBarHeight(16);
      this.fuzzyBar.setRenderStyle(RenderStyle.Raised);
      this.fuzzyBar.setFont(new Font("Microsoft Sans Serif", 1, 11));
    }
    return this.fuzzyBar;
  }

  private FuzzyLabel getFuzzyLabel3()
  {
    if (this.fuzzyLabel3 == null)
    {
      this.fuzzyLabel3 = new FuzzyLabel();
      this.fuzzyLabel3.setFont(new Font("Microsoft Sans Serif", 1, 11));
      this.fuzzyLabel3.setForeground(GREEN);
      this.fuzzyLabel3.setPreferredSize(new Dimension(100, 12));
    }
    return this.fuzzyLabel3;
  }

  private FuzzyLabel getFuzzyLabel()
  {
    if (this.fuzzyLabel == null)
    {
      this.fuzzyLabel = new FuzzyLabel();
      this.fuzzyLabel.setPreferredSize(new Dimension(100, 12));
      this.fuzzyLabel.setFont(new Font("Microsoft Sans Serif", 1, 11));
      this.fuzzyLabel.setForeground(Color.blue);
    }
    return this.fuzzyLabel;
  }

  private FuzzyLabel getFuzzyLabel1()
  {
    if (this.fuzzyLabel1 == null)
    {
      this.fuzzyLabel1 = new FuzzyLabel();
      this.fuzzyLabel1.setPreferredSize(new Dimension(100, 12));
      this.fuzzyLabel1.setFont(new Font("Microsoft Sans Serif", 1, 11));
      this.fuzzyLabel1.setForeground(Color.magenta);
    }
    return this.fuzzyLabel1;
  }

  private JCheckBox getJCheckBox()
  {
    if (this.jCheckBox == null)
    {
      this.jCheckBox = new JCheckBox();
      this.jCheckBox.setSelected(true);
      this.jCheckBox.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          VisualizationControlsDemo.this.getOpinionBar().setEnabled(VisualizationControlsDemo.this.jCheckBox.isSelected());
        }
      });
    }
    return this.jCheckBox;
  }

  private JCheckBox getJCheckBox1()
  {
    if (this.jCheckBox1 == null)
    {
      this.jCheckBox1 = new JCheckBox();
      this.jCheckBox1.setSelected(true);
      this.jCheckBox1.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          VisualizationControlsDemo.this.getTriColorOpinionBar().setEnabled(VisualizationControlsDemo.this.jCheckBox1.isSelected());
        }
      });
    }
    return this.jCheckBox1;
  }
}