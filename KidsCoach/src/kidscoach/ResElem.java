/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kidscoach;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.ext.awt.image.codec.jpeg.JPEGImageWriter;
import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;


/**
 *
 * @author avu
 */
public class ResElem {
    private String uri;
    private JSVGCanvas svgCanvas;
    BufferedImage img;
    BufferedImage selImg;
    
    public ResElem(String name) throws URISyntaxException, TranscoderException {
        try {
            InputStream istream = null;
            this.uri = Project.getProject().tempDir.resolve("resource").resolve(name).toUri().toString();
            int p = uri.lastIndexOf('.');
            String ext =  uri.substring(p + 1);
            if ("svg".equals(ext)) {
                String parser = XMLResourceDescriptor.getXMLParserClassName();
                SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
                SVGDocument doc = (SVGDocument)f.createDocument(this.uri);
                GVTBuilder builder = new GVTBuilder();
                BridgeContext ctx;
                ctx = new BridgeContext(new UserAgentAdapter());
                GraphicsNode gvtRoot = builder.build(ctx, doc);
                Rectangle2D bnds = gvtRoot.getSensitiveBounds();
            
                doc.getRootElement().setAttribute("width", "50");
                doc.getRootElement().setAttribute("height", "50");

            
                JPEGTranscoder t = new JPEGTranscoder();

                // Set the transcoding hints.
                t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                       new Float(.8));

                // Create the transcoder input.
                TranscoderInput input = new TranscoderInput(doc);

                // Create the transcoder output.
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                TranscoderOutput output = new TranscoderOutput(ostream);
                t.transcode(input, output);

                istream = new ByteArrayInputStream(ostream.toByteArray());
            } else {
                istream = new FileInputStream(new File(new URI(uri)));
            }
            // Flush and close the stream.
            //img = ImageIO.read(new OutputStream() {})
            BufferedImage origImg = ImageIO.read(istream);
            
            if (origImg.getWidth() != 50 || origImg.getHeight() != 50) {
                img = new BufferedImage(50, 50, origImg.getType());
                Graphics2D g2d = img.createGraphics();
                g2d.drawImage(origImg, 0, 0, 50, 50, null);
            } else {
                img = origImg;
            }
            
            selImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
            
            float[] matrix = {
                0.111f, 0.111f, 0.111f, 
                0.111f, 0.111f, 0.111f, 
                0.111f, 0.111f, 0.111f, 
            };

            BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix));
            op.filter(img, selImg);
            Graphics2D g2d = selImg.createGraphics();
            
            g2d.setColor(Color.WHITE);
            Stroke str = new BasicStroke(2.0f);
            g2d.setStroke(str);
            g2d.drawRect(0, 0, selImg.getWidth(), selImg.getHeight());
                    
            str = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_BEVEL, 0, new float[] {12, 12}, 0.0f);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(str);
            g2d.drawRect(0, 0, selImg.getWidth(), selImg.getHeight());
        } catch (IOException ex) {
            Logger.getLogger(ResElem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String getUri() {
        return uri;
    }
    
    JSVGCanvas getSVGCanvas() {
        return svgCanvas;
    }
    
    Image getImage() {
        return img;
    }
    
    Image getSelImage() {
        return selImg;
    }
}