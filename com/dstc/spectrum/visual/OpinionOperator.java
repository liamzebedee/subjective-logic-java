package com.dstc.spectrum.visual;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class OpinionOperator extends JComponent
  implements ActionListener
{
  private static final long serialVersionUID = 3257284742771849271L;
  private Operator operator = Operator.Discount;

  private boolean unary = false;

  private JPopupMenu popup = new JPopupMenu();

  private int operatorSize = 25;
  private MenuItem[] op;

  public OpinionOperator()
  {
    for (Operator o : Operator.values())
    {
      if (o != Operator.Equals)
      {
        JMenuItem menuItem = new JMenuItem(o.getMenuName());
        menuItem.addActionListener(this);

        this.popup.add(menuItem);
      }
    }
    PopupListener listener = new PopupListener();
    addMouseMotionListener(listener);
    addMouseListener(listener);

    setToolTipText(this.operator.getMenuName());
  }

  protected void paintComponent(Graphics g)
  {
    if (isOpaque())
    {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    this.operator.paint(g, this.operatorSize, getSize());
  }

  public JPopupMenu getPopup()
  {
    return this.popup;
  }

  public void actionPerformed(ActionEvent e)
  {
    for (Operator o : Operator.values())
    {
      if (o.getMenuName().equals(e.getActionCommand()))
      {
        setOperator(o);
        break;
      }
    }

    repaint();
  }

  public Operator getOperator()
  {
    return this.operator;
  }

  public void setOperator(Operator operator)
  {
    Operator old = this.operator;

    if (operator == this.operator) {
      return;
    }
    setToolTipText(operator.getMenuName());
    this.operator = operator;

    firePropertyChange("operator", old, operator);
  }

  public void setOperatorSize(int operatorSize)
  {
    this.operatorSize = operatorSize;
  }

  public int getOperatorSize()
  {
    return this.operatorSize;
  }

  private class PopupListener extends MouseAdapter
    implements MouseMotionListener
  {
    PopupListener()
    {
    }

    public void mousePressed(MouseEvent e)
    {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e)
    {
      if (OpinionOperator.this.operator != OpinionOperator.Operator.Equals)
        OpinionOperator.this.popup.show(e.getComponent(), e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
      if ((OpinionOperator.this.operator != OpinionOperator.Operator.Equals) && (OpinionOperator.this.contains(e.getPoint())))
        OpinionOperator.this.setCursor(new Cursor(12));
      else
        OpinionOperator.this.setCursor(new Cursor(0));
    }
  }

  public static enum Operator
  {
    Equals("Equals"), 
    Complement("Complment/NOT", "NOT"), 
    Multiplication("Multiplication/Conjunction/AND", "AND"), 
    CoMultiplication("Co-multiplication/Disjunction/OR", "OR"), 
    Division("Division/Unconjunction/UN-AND", "UN-AND"), 
    CoDivision("Co-division/Undisjunction/UN-OR", "UN-OR"), 
    Discount("Discount", "DISCOUNT"), 
    Consensus("Consensus/FUSE", "Fuse"), 
    ConditionalInference("Conditional Inference/Deduce", "Deduce"), 
    ReverseConditionalInference("Reverse Conditional Inference/Abduce", "Abduce"), 

    Addition("Addition/Sum", "ADD"), 
    Subtraction("Subtraction/Difference", "SUBTRACT"), 
    AdjustedExpectation("Adjusted Expectation", "Adjust", "Expectation"), 
    MaximizedUncertainty("Maximized Uncertainty", "Maximized", "Uncertainty"), 
    Erosion("Erosion/Decay", "Erode"), 
    Average("Smooth Average", "Average");

    private String menuName;
    private String[] displayText;

    public String getMenuName()
    {
      return this.menuName;
    }

    public String[] getDisplayText()
    {
      return this.displayText;
    }

    Operator(String menuName)
    {
      this.menuName = menuName;
    }

    Operator(String menuName, String[] displayText)
    {
      this(menuName);
      this.displayText = displayText;
    }

    Operator(String menuName, String arg0)
    {
      this(menuName);
      this.displayText = new String[] { arg0 };
    }

    Operator(String menuName, String arg0, String arg1)
    {
      this(menuName);
      this.displayText = new String[] { arg0, arg1 };
    }

    Operator(String menuName, String arg0, String arg1, String arg2)
    {
      this(menuName);
      this.displayText = new String[] { arg0, arg1, arg2 };
    }

    public void paint(Graphics g, int operatorSize, Dimension dim)
    {
      Graphics2D g2 = (Graphics2D)g;
      FontMetrics fm = g2.getFontMetrics();

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int textHeight = 0;
      int buff = 4;
      int sw = 0;

      if (this.displayText != null)
      {
        textHeight = fm.getHeight() * this.displayText.length;

        int i = 0; for (int size = this.displayText.length; i < size; i++) {
          sw = Math.max(fm.stringWidth(this.displayText[i]), sw);
        }
      }
      int h = (int)Math.max(0.0D, dim.getHeight() - buff - textHeight);
      int w = (int)Math.min(h, dim.getWidth() - buff);

      if (operatorSize < w) {
        w = operatorSize;
      }
      if ((this.displayText != null) && (this.displayText.length > 0))
      {
        double y0 = w + (dim.getHeight() - w - buff - textHeight) / 2.0D;

        int offset = 1;
        int i = 0; for (int size = this.displayText.length; i < size; i++)
        {
          if (this.displayText[i] != null) {
            g2.drawString(this.displayText[i], 
              (int)(dim.getWidth() / 2.0D - fm.stringWidth(this.displayText[i]) / 2), 
              (int)y0 + (buff + offset++ * fm.getHeight()));
          }
        }
      }
      g2.translate((dim.getWidth() - w) / 2.0D, (dim.getHeight() - w - buff - textHeight) / 2.0D);

      int thick = (int)(w * 0.2D);

      if (this == Equals)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, thick + i, w, thick + i);
          g2.drawLine(0, w - thick - i, w, w - thick - i);
        }
      }
      else if (this == Equals)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, i, w, i);
          g2.drawLine(w - i, 0, w - i, w / 2);
        }
      }
      else if (this == Complement)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, i, w, i);
          g2.drawLine(w - i, 0, w - i, w / 2);
        }

      }
      else if (this == Multiplication)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(i, w, w / 2 - thick / 2 + i, 0);
          g2.drawLine(w - i, w, w / 2 + thick / 2 - i, 0);
        }

      }
      else if (this == CoMultiplication)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(i, 0, w / 2 - thick / 2 + i, w);
          g2.drawLine(w - i, 0, w / 2 + thick / 2 - i, w);
        }
      }
      else if (this == Division)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, i, w, i);
          g2.drawLine(i + thick / 2, w, w / 2 - thick / 2 + i, w / 3 + thick / 2);
          g2.drawLine(w - i - thick / 2, w, w / 2 + thick / 2 - i, w / 3 + thick / 2);
        }
      }
      else if (this == CoDivision)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, i, w, i);
          g2.drawLine(i + thick / 2, w / 3 + thick / 2, w / 2 - thick / 2 + i, w);
          g2.drawLine(w - i - thick / 2, w / 3 + thick / 2, w / 2 + thick / 2 - i, w);
        }
      }
      else if (this == Discount)
      {
        int corner = (int)(0.17157288D * w);
        for (int i = thick / 2 - 1; i >= 0; i--)
        {
          g2.drawOval(i, i, w - 2 * i, w - 2 * i);
          g2.drawLine(corner + i, corner, w - corner, w - corner - i);
          g2.drawLine(corner, corner + i, w - corner - i, w - corner);
          g2.drawLine(w - corner - i, corner, corner, w - corner - i);
          g2.drawLine(w - corner, corner + i, corner + i, w - corner);
        }
      }
      else if (this == Consensus)
      {
        for (int i = thick / 2 - 1; i >= 0; i--)
        {
          g2.drawOval(i, i, w - 2 * i, w - 2 * i);
          g2.drawLine(w / 2 - thick / 4 + i, 0, w / 2 - thick / 4 + i, w);
          g2.drawLine(0, w / 2 - thick / 4 + i, w, w / 2 - thick / 4 + i);
        }
      }
      else if (this == ConditionalInference)
      {
        for (int i = thick / 2 - 1; i >= 0; i--)
        {
          g2.drawOval(i, i, w - 2 * i, w - 2 * i);
          g2.drawOval(i + thick, i + thick, w - 2 * i - thick * 2, w - 2 * i - thick * 2);
        }

      }
      else if (this == ReverseConditionalInference)
      {
        for (int i = thick / 2 - 1; i >= 0; i--)
        {
          g2.drawLine(0, i - thick, w, i - thick);
          g2.drawOval(i, i, w - 2 * i, w - 2 * i);
          g2.drawOval(i + thick, i + thick, w - 2 * i - thick * 2, w - 2 * i - thick * 2);
        }
      }
      else if (this == Addition)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(w / 2 - thick / 2 + i, 0, w / 2 - thick / 2 + i, w);
          g2.drawLine(0, w / 2 - thick / 2 + i, w, w / 2 - thick / 2 + i);
        }
      }
      else if (this == Subtraction)
      {
        for (int i = 0; i <= thick; i++)
        {
          g2.drawLine(0, w / 2 - thick / 2 + i, w, w / 2 - thick / 2 + i);
        }
      }
      else if (this == AdjustedExpectation);
    }
  }
}