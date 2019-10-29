package com.neptunedreams;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import static java.awt.AlphaComposite.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/26/19
 * <p>Time: 11:23 AM
 *
 * @author Miguel Mu\u00f1oz
 */
public final class TransparencyTest extends JPanel {

  private static final int diameter = 60;

  public static void main(String[] args) {
    //noinspection HardCodedStringLiteral
    JFrame frame = new JFrame("Red Source with Blue Destination");
    frame.setLocationByPlatform(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.add(new TransparencyTest());
    frame.pack();
    frame.setVisible(true);
  }
  
  private TransparencyTest() {
    super(new GridLayout(1, 0));
    setOpaque(false);
    for (Alpha alpha : Alpha.values()) {
      add(makeIcon(alpha));
    }
  }
  
  private JComponent makeIcon(Alpha alpha) {

    BufferedImage picture = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
    int d2 = diameter / 2;
    final int d4 = diameter / 4;
    Graphics2D gP = picture.createGraphics();
    gP.setColor(Color.red);
    int delta = 4;
    int y = diameter/2;
    int size = y - delta;
    gP.fillRect(delta, y, size, size);
    

//    gP.setColor(Color.RED);
    gP.dispose();
    
    BufferedImage image = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setComposite(Src);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
//    int maskSize = diameter-4;

    gP.fillRect(d4, d4, d2, d2);
    Ellipse2D circle = new Ellipse2D.Double(d4, d4, d2, d2);
    g.setColor(Color.blue);
    g.fill(circle); // mask out circle

    g.setComposite(alpha.composite);
    g.drawImage(picture, 0, 0, null);
    g.setColor(Color.black);
    g.setComposite(AlphaComposite.SrcOver);
    g.drawRect(1, 1, diameter - 2, diameter - 2);
    g.dispose();
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JLabel(new ImageIcon(image), JLabel.CENTER), BorderLayout.CENTER);
    panel.add(new JLabel(alpha.name, JLabel.CENTER), BorderLayout.PAGE_END);

    return panel;
  }
  
  @SuppressWarnings({"HardCodedStringLiteral", "PublicField"})
  private enum Alpha {
    ASrc(Src, "Src"),
    ASrcOver(SrcOver, "SrcOver"),
    ASrcIn(SrcIn, "SrcIn"),
    ASrcOut(SrcOut, "SrcOut"),
    ASrcAtop(SrcAtop, "SrcAtop"),
    ADst(Dst, "Dst"),
    ADstOver(DstOver, "DstOver"),
    ADstIn(DstIn, "DstIn"),
    ADstOut(DstOut, "DstOut"),
    ADstAtop(DstAtop, "DstAtop"),
    AXor(Xor, "Xor"),
    AClear(Clear, "Clear")
    ;
    
    public final AlphaComposite composite;
    public final String name;
    private Alpha(AlphaComposite composite, String name) {
      this.composite = composite;
      this.name = name;
    }
  }
}