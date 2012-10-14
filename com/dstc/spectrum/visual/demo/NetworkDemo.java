package com.dstc.spectrum.visual.demo;

import com.dstc.spectrum.opinion.SubjectiveOpinion;
import com.dstc.spectrum.visual.OpinionTriangle;
import com.dstc.spectrum.visual.OpinionTriangle.OpinionPoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NetworkDemo extends JApplet
{
  private static final long serialVersionUID = 3256445802514887989L;
  private NetworkPanel panel = null;

  public void init()
  {
    add(getPanel());
    super.init();
  }

  public synchronized NetworkPanel getPanel()
  {
    if (this.panel == null) {
      this.panel = new NetworkPanel();
    }
    return this.panel;
  }

  private class NetworkPanel extends JPanel
    implements PropertyChangeListener
  {
    private static final long serialVersionUID = 1L;
    private OpinionTriangle opinionAB;
    private OpinionTriangle opinionBD;
    private OpinionTriangle opinionAC;
    private OpinionTriangle opinionCD;
    private OpinionTriangle opinionAD;
    private OpinionTriangle.OpinionPoint AB;
    private OpinionTriangle.OpinionPoint BD;
    private OpinionTriangle.OpinionPoint AC;
    private OpinionTriangle.OpinionPoint CD;
    private OpinionTriangle.OpinionPoint AD;
    private JLabel[] textABCD;
    private List<OpinionTriangle> triangles = new ArrayList();
    private int w;
    private int h;
    private int xunit;
    private int yunit;

    public NetworkPanel()
    {
      this.textABCD = new JLabel[4];
      this.xunit = (getSize().width / 6);
      this.yunit = (getSize().height / 5);

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      setLayout(gridbag);

      Font font = new Font("Dialog", 0, 12);
      try
      {
        this.opinionAB = new OpinionTriangle(250, 250);
        this.AB = this.opinionAB.createOpinionPoint("AB", new SubjectiveOpinion(0.7D, 0.15D, 0.15D, 0.5D));
        this.AB.addPropertyChangeListener(this);

        this.opinionBD = new OpinionTriangle(250, 250);
        this.BD = this.opinionBD.createOpinionPoint("BD", new SubjectiveOpinion(0.7D, 0.15D, 0.15D, 0.5D));
        this.BD.addPropertyChangeListener(this);
        this.opinionBD.addPropertyChangeListener("atomicity", this);

        this.opinionAC = new OpinionTriangle(250, 250);
        this.AC = this.opinionAC.createOpinionPoint("AC", new SubjectiveOpinion(0.7D, 0.15D, 0.15D, 0.5D));
        this.AC.addPropertyChangeListener(this);

        this.opinionCD = new OpinionTriangle(250, 250);
        this.CD = this.opinionCD.createOpinionPoint("CD", new SubjectiveOpinion(0.7D, 0.15D, 0.15D, 0.5D));
        this.CD.addPropertyChangeListener(this);
        this.opinionCD.addPropertyChangeListener("atomicity", this);

        this.opinionAD = new OpinionTriangle(250, 250);
        this.AD = this.opinionAD.createOpinionPoint("AD", new SubjectiveOpinion(0.616D, 0.131D, 0.253D, 0.5D));

        this.triangles.add(this.opinionAB);
        this.triangles.add(this.opinionBD);
        this.triangles.add(this.opinionAC);
        this.triangles.add(this.opinionCD);
        this.triangles.add(this.opinionAD);

        for (OpinionTriangle t : this.triangles)
        {
          t.setUncertaintyText("u");
          t.setBeliefText("b");
          t.setDisbeliefText("d");
          t.setAtomicityText("a");
          t.setFont(font);
        }

        Font boldFont = new Font("SansSerif", 1, 26);
        this.textABCD[0] = new JLabel("A");
        this.textABCD[1] = new JLabel("B");
        this.textABCD[2] = new JLabel("C");
        this.textABCD[3] = new JLabel("D");

        for (int i = 0; i < 4; i++)
        {
          this.textABCD[i].setFont(boldFont);
          this.textABCD[i].setOpaque(true);
        }

        c.weightx = (c.weighty = 0.0D);
        c.fill = 0;
        c.anchor = 17;
        c.insets = new Insets(0, 10, 0, 10);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        setFont(new Font("Dialog", 0, 10));
        gridbag.setConstraints(this.textABCD[0], c);
        c.gridx = 4;
        c.gridwidth = 0;
        gridbag.setConstraints(this.textABCD[3], c);
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = -1;
        c.gridy = 0;
        c.anchor = 10;
        gridbag.setConstraints(this.textABCD[1], c);
        c.gridy = 2;
        gridbag.setConstraints(this.textABCD[2], c);

        c.weightx = (c.weighty = 1.0D);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = 1;
        gridbag.setConstraints(this.opinionAB, c);
        c.gridy = 2;
        gridbag.setConstraints(this.opinionAC, c);
        c.gridwidth = 1;
        c.gridx = -1;
        gridbag.setConstraints(this.opinionCD, c);
        c.gridy = 0;
        gridbag.setConstraints(this.opinionBD, c);
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = 1;
        gridbag.setConstraints(this.opinionAD, c);

        add(this.textABCD[0]);
        add(this.opinionAB);
        add(this.textABCD[1]);
        add(this.opinionBD);
        add(this.textABCD[3]);
        add(this.opinionAC);
        add(this.textABCD[2]);
        add(this.opinionCD);
        add(this.opinionAD);

        validate();

        this.opinionAD.setEnabled(false);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
      try
      {
        if (evt.getSource() == this.opinionBD)
          this.opinionCD.setAtomicity(this.opinionBD.getAtomicity());
        else if (evt.getSource() == this.opinionCD) {
          this.opinionBD.setAtomicity(this.opinionCD.getAtomicity());
        }
        SubjectiveOpinion tmp1 = this.AB.getOpinion().discount(this.BD.getOpinion());
        SubjectiveOpinion tmp2 = this.AC.getOpinion().discount(this.CD.getOpinion());
        this.AD.setOpinion(tmp1.fuse(tmp2));
      }
      catch (Exception err)
      {
        err.printStackTrace();
      }
    }

    protected void paintComponent(Graphics g)
    {
      if (isOpaque())
      {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
      }

      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setColor(Color.black);
      Rectangle r1 = this.textABCD[0].getBounds();
      Rectangle r2 = this.opinionAB.getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.opinionAB.getBounds();
      r2 = this.textABCD[1].getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.textABCD[1].getBounds();
      r2 = this.opinionBD.getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.opinionBD.getBounds();
      r2 = this.textABCD[3].getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);

      r1 = this.textABCD[0].getBounds();
      r2 = this.opinionAC.getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.opinionAC.getBounds();
      r2 = this.textABCD[2].getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.textABCD[2].getBounds();
      r2 = this.opinionCD.getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.opinionCD.getBounds();
      r2 = this.textABCD[3].getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);

      g2.setColor(Color.blue);
      r1 = this.textABCD[0].getBounds();
      r2 = this.opinionAD.getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height, r1.x + r1.width / 2, r2.y + r2.height / 2);
      g2.drawLine(r1.x + r1.width / 2, r2.y + r2.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
      r1 = this.textABCD[3].getBounds();
      g2.drawLine(r1.x + r1.width / 2, r1.y + r1.height, r1.x + r1.width / 2, r2.y + r2.height / 2);
      g2.drawLine(r1.x + r1.width / 2, r2.y + r2.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
    }
  }
}