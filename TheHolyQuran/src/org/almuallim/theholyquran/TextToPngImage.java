package org.almuallim.theholyquran;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Naveed
 */
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class TextToPngImage {
    private static final Logger LOG = Logger.getLogger(TextToPngImage.class.getName());

    
    private static Font font;//new Font("noorehuda", Font.PLAIN, 32);
    static {
        try {
            Font f = Font.createFont(Font.PLAIN, TextToPngImage.class.getClassLoader().getResourceAsStream("org/almuallim/theholyquran/data/noorehuda.ttf"));
            font = f.deriveFont(32f);
        } catch (IOException | FontFormatException e) {
            LOG.log(Level.SEVERE, "Unable to create Font! Text to Png conversion will be done using default font", e);
        }
    }

    private int paragraphStart;
    private int paragraphEnd;

    private Dimension preComputeImageDimensions(String text, Map<TextAttribute, Object> map, int maxWidth) {
        BufferedImage buffRenderImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffRenderImage.createGraphics();
        AttributedString attrString = new AttributedString(text, map);
        FontMetrics fm = g2d.getFontMetrics(font);
        AttributedCharacterIterator paragraph = attrString.getIterator();
        int textWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            textWidth += fm.charWidth(text.codePointAt(i));
        }
        paragraphStart = paragraph.getBeginIndex();
        paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);

        float breakWidth = Math.min(maxWidth, textWidth);
        float drawPosY = 0;

        // Set position to the index of the first character in the paragraph.
        lineMeasurer.setPosition(paragraphStart);

        // Get lines until the entire paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {

            // Retrieve next layout. A cleverer program would also cache
            // these layouts until the component is re-sized.
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);
            // Compute pen x position. If the paragraph is right-to-left we
            // will align the TextLayouts to the right edge of the panel.
            // Note: this won't occur for the English text in this sample.
            // Note: drawPosX is always where the LEFT of the text is placed.
//            float drawPosX = layout.isLeftToRight()
//                    ? 0 : breakWidth - layout.getAdvance();

            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();

            // Draw the TextLayout at (drawPosX, drawPosY).

            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        // don't use drawn graphic anymore.
        g2d.dispose();
        return new Dimension(Math.min(maxWidth, textWidth), (int) Math.ceil(drawPosY));
    }

    /**
     * Converts the given text to image.
     *
     * @param text text to convert
     * @param font font to use when drawing text
     * @param maxWidth the max width of the text
     * @param widthAdjustment adjustment value to leave equal size before and
     * after the text
     * @param heightAdjustment adjustment value to leave equal size before and
     * after the text
     * @param foreground foreground value
     * @param filename absolute path to file
     * @throws IOException
     */
    public void convertToImage(String text, int maxWidth, int widthAdjustment, int heightAdjustment, Color foreground, String filename) throws IOException {
        final Map<TextAttribute, Object> map =
                new HashMap<>();
        map.put(TextAttribute.FONT, font);
//        map.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
//        map.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
//        map.put(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
        AttributedString attrString = new AttributedString(text, map);
        Dimension size = preComputeImageDimensions(text, map, maxWidth);
        maxWidth = Math.min(maxWidth, (int) size.getWidth());
        int width = maxWidth + widthAdjustment;
        int height = (int) size.getHeight() + heightAdjustment;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = bi.createGraphics();
        // Get a DOMImplementation.
//        DOMImplementation domImpl =
//                GenericDOMImplementation.getDOMImplementation();
//
//        // Create an instance of org.w3c.dom.Document.
//        String svgNS = "http://www.w3.org/2000/svg";
//        Document document = domImpl.createDocument(svgNS, "svg", null);
//
//        // Create an instance of the SVG Generator.
//        SVGGraphics2D g2d = new SVGGraphics2D(document);
//        g2d.setSVGCanvasSize(new Dimension(width, height));
//        BufferedImage buffRenderImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//        Graphics2D g2d = buffRenderImage.createGraphics();
        AttributedCharacterIterator paragraph = attrString.getIterator();
        paragraphStart = paragraph.getBeginIndex();
        paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g2d.setColor(foreground);

        // Set break width to width of maxWidth.
        float breakWidth = maxWidth;
        float drawPosY = 0;

        // Set position to the index of the first character in the paragraph.
        lineMeasurer.setPosition(paragraphStart);
//        g2d.setFont(new Font("Segoe UI", 0, 16));
        // Get lines until the entire paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {

            // Retrieve next layout.
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);
            // Compute pen x position. If the paragraph is right-to-left we
            // will align the TextLayouts to the right edge of the panel.
            float drawPosX = layout.isLeftToRight()
                    ? 0 : breakWidth - layout.getAdvance();

            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();

            // Draw the TextLayout at (drawPosX, drawPosY).
            layout.draw(g2d, drawPosX + (widthAdjustment / 2), drawPosY + (heightAdjustment / 2));


//
            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        // don't use drawn graphic anymore.
        g2d.dispose();
        File file = new File(filename);
// Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
//        boolean useCSS = true; // we want to use CSS style attributes
//        Writer out = new FileWriter(file);
        ImageIO.write(bi, "PNG", file);
//        ImageIO.write(buffRenderImage, "png", file);
    }

//    public static void main(String[] args) throws IOException {
//        TextToSVGImage tti = new TextToSVGImage();
//        long start = System.currentTimeMillis();
//        try {
//
//            System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
//            Scanner scanner = new Scanner(TextToSVGImage.class.getResourceAsStream("quran-simple-enhanced.txt"));
//            int index = 1;
//            while (scanner.hasNextLine()) {
//                String text = scanner.nextLine();
//                if (text.isEmpty() || text.startsWith("#")) {
//                    continue;
//                }
////                System.out.println(text);
//                char left = '\ufd3e';
//                char right = '\ufd3f';
//                char ch = '\u06dd';
//                tti.convertToImage(text /*+ "      " + right + index + left*/, font, 890, 10, 10, Color.black, "D:\\Projects\\Al-mu'allim\\svg\\" + index + ".svg");
//                index++;
//            }
////            tti.convertToImage(text, font, 890, 10, 10, Color.BLACK, "G:\\svg\\" + 1 + ".svg");
//        } catch (IOException ex) {
//            Logger.getLogger(TextToSVGImage.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        long end = System.currentTimeMillis();
//        long timeTakenMs = end - start;
//        System.out.println((timeTakenMs / 1000) + " seconds");
//
//    }
}
