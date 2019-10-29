package com.neptunedreams.zoetrope;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/14/19
 * <p>Time: 11:02 PM
 * 
 * Images Source: https://publicdomainreview.org/collections/phenakistoscopes-1833/
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "StringConcatenation", "SameParameterValue"})
public enum Zoetrope {
  ;

  /**
   * Animates a zoopraxiscope by constructing an animating .gif file.
   * Usage: 
   *   java com.neptunedreams.zoetrope.Zoetrope {@literal <file-name> <frame-count>} <br> 
   *   java com.neptunedreams.zoetrope.Zoetrope {@literal <file-name> <frame-count> <fps>} <br> 
   *   java com.neptunedreams.zoetrope.Zoetrope {@literal <file-name> <frame-count> <fps> <final-size>} <br>
   * <ul>
   *   <li>file-name is the name of the file.</li>
   *   <li>frame-count is the number of frames. Find this value by counting the slits in the image.</li>
   *   <li>fps is the number of frames per second. Defaults to 10.</li>
   *   <li>final-size (optional) is the final size, in pixels of the resulting gif file.</li>
   * </ul>
   * Always start with a square image.
   * @param args The arguments
   * @throws IOException If there's a problem reading the image file or writing the new image
   */
  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public static void main(String[] args) throws IOException {

    if ((args.length == 0) || args[0].startsWith("-")) {
      System.out.println("Parameters: <filename> <frame-count> [<fps> <diameter>]");
      System.out.println("frame-count may be negative to reverse the direction of the animation.");
      System.out.println("fps (frames per second) defaults to 10");
      System.out.println("diameter defaults to the original");
      System.out.println("---");
      System.out.println("Output file name will be of the form N-F-R-Sfps.gif or N-F-R-Sfps-Dp.gif");
      System.out.println("where N is the original base name");
      System.out.println("      F is the number of frames");
      System.out.println("      R is f or r, for forward or reverse");
      System.out.println("      S is the speed, in approximate frames per second");
      System.out.println("      D is the diameter, which will be used only if it differs from the original");
      System.exit(-9);
    }
    String fileName = args[0];

    int frames = Integer.decode(args[1]);
    int fps = optionalArg(2, args, 10);
    int diameter = optionalArg(3, args);
    if (fps < 0) {
      throw new IllegalArgumentException("Frames per second must not be negative. Frame count may be negative.");
    }
    build(fileName, frames, fps, diameter);
  }
  
  private static int optionalArg(int index, String[] args) { return optionalArg(index, args, 0); }
  private static int optionalArg(int index, String[] args, int defaultValue) {
    return (args.length > index) ? Integer.decode(args[index]) : defaultValue;
  }
  
  @SuppressWarnings({"OverlyBroadThrowsClause", "UseOfSystemOutOrSystemErr"})
  private static void build(String fileName, int frames, int fps, int diameter) throws IOException {
    // I use an alpha mask to mask out anything outside the circle. My circular mask is anti-aliased, but the code
    // to turn it into an animated gif somehow ignore the anti-aliasing and produces an un-aliased rim. I haven't
    // been able to fix this. I have verified that the image 
    
    System.out.printf("Frames: %d%nFps:    %d%n", frames, fps);
    BufferedImage rawImage = ImageIO.read(new File(fileName));
    Dimension size = new Dimension(rawImage.getWidth(), rawImage.getHeight());
    BufferedImage image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D rawG2 = makeMask(image);
    rawG2.drawImage(rawImage, 0, 0, null);
    final int origMin = Math.min(size.width, size.height);
    int finalSize = (diameter == 0) ? origMin : diameter;
    System.out.printf("Size: %d x %d%n", size.width, size.height);
    System.out.printf("Diameter: %d%n", diameter);
    double rotation = -(2 * Math.PI) / frames;

    File outputFile = new File(makeGifName(fileName, diameter, fps, frames));
    ImageOutputStream os = new FileImageOutputStream(outputFile);
    int fX = size.width;
    int fY = size.height;
    GifSequenceWriter sequenceWriter = new GifSequenceWriter(os, image.getType(), 1000/fps, true);
    int cx = fX/2;
    int cy = fY/2;
    int absFrames = Math.abs(frames);

//    BufferedImage dbgDummy = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
//    makeMask(dbgDummy, true);

    for (int ii=0; ii<absFrames; ++ii) {
      BufferedImage nextFrame = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = makeMask(nextFrame);
      double angle = rotation * ii;
      AffineTransform savedTransform = g2.getTransform();
      g2.translate(cx, cy);
      g2.rotate(angle);
      g2.drawImage(image, -cx, -cy, fX, fY, null);
      g2.setTransform(savedTransform);
      g2.dispose();

      // If we specified a different size, draw the old, rotated image into the new one of the new size
      if (diameter > 0) {
        BufferedImage smallImage = new BufferedImage(finalSize, finalSize, image.getType());
        g2 = makeMask(smallImage);
        float ratio = finalSize / (float) origMin;
        int smallX = Math.round(size.width * ratio);
        int smallY = Math.round(size.height * ratio);
        g2.drawImage(nextFrame, 0, 0, smallX, smallY, null);
        g2.dispose();
        nextFrame = smallImage;
      }
      System.out.println("Frame " + ii);
      
//      dbgShowFrame(nextFrame);

      sequenceWriter.writeToSequence(nextFrame);
    }
    sequenceWriter.close();
    os.close();
  }
  
  private static void dbgShowFrame(Image frame) {
    JFrame jFrame = new JFrame("frame");
    jFrame.setLocationByPlatform(true);
    final JLabel comp = new JLabel(new ImageIcon(frame));
    jFrame.add(new JScrollPane(comp));
    jFrame.pack();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setVisible(true);
  }

  private static Graphics2D makeMask(final BufferedImage image) {
    return makeMask(image, false);
  }
  
  private static Graphics2D makeMask(BufferedImage mask, boolean show) {
//    final BufferedImage mask = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    int width  = mask.getWidth();
    int height = mask.getHeight();

    Graphics2D maskGr = mask.createGraphics();
    maskGr.setComposite(AlphaComposite.Src);
    maskGr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, width, height);
    maskGr.setColor(Color.black);
    maskGr.fill(circle);

    maskGr.setComposite(AlphaComposite.SrcAtop);
    if (show) {
      dbgShowFrame(mask);
    }
    return maskGr;
  }

  private static String makeGifName(String fileName, int size, int fps, int frames) {
    @SuppressWarnings("MagicCharacter")
    int lastDot = fileName.lastIndexOf('.');
    final String baseName = fileName.substring(0, lastDot);
    @SuppressWarnings("MagicCharacter")
    char direction = (frames < 0) ? 'r' : 'f';
    if (size == 0) {
      return String.format("%s-%d-%c-%dfps.gif", baseName, Math.abs(frames), direction, fps); // NON-NLS
    } else {
      return String.format("%s-%d-%c-%dfps-%dp.gif", baseName, Math.abs(frames), direction, fps, size);
    }
  }
}
