package kidscoach;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.script.Interpreter;
import org.apache.batik.swing.JSVGCanvas;

public class SCanvas extends JSVGCanvas {
    private static final Logger log = Logger.getLogger(SCanvas.class.getName());
    
    public SCanvas() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SCanvas.this.repaint();
                SCanvas.this.validate();
                SCanvas.this.repaint();                
            }
        });
    }
    
    Image getSnapshot(int width, int height) {
        BufferedImage res = new BufferedImage(width, height, 
                                              BufferedImage.TYPE_INT_ARGB);
        
        BufferedImage orig = new BufferedImage(800, 600, 
                                               BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = orig.createGraphics();
        
        paintComponent(g2d);
        
        g2d = res.createGraphics();
        
        g2d.drawImage(orig, 0, 0, res.getWidth(), res.getHeight(), null);
        g2d.setStroke(new BasicStroke(4.0f));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, res.getWidth(), res.getHeight());
        return res;
    }
    
    void addSVGFile(final int id, final String name, 
                    final float x, final float y, final float w, final float h ) {
        executeScript("add_object(" + id + ",\"" + 
                      name + "\"," + x + "," + y + "," + w + "," + h + ")");
    }
    
    Object executeScript(final String src) {
        final UpdateManager um = getUpdateManager(); 
        um.getUpdateRunnableQueue().invokeLater(
            new Runnable() {
                
                @Override
                public void run() {
                    Interpreter inter = um.getBridgeContext().getInterpreter("text/ecmascript");
                    Object evaluate = inter.evaluate(src);
                }
        });
        
        return null;
    }
    
}
