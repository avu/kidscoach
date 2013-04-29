/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kidscoach;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author avu
 */
public class SlideElem {
    private String name;
    BufferedImage img;
    BufferedImage selImg;
    int id;
    
    public SlideElem(int id, String name) {
        this.id = id;
        this.name = name;
        try {
            System.out.println(Project.getProject().getProjectFile(name));
            img = ImageIO.read(Project.getProject().getProjectFile(name));
        } catch (IOException ex) {
            Logger.getLogger(SlideElem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        buildSelImage();
    }
        
    private void buildSelImage() {
        selImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = selImg.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, selImg.getWidth(), selImg.getHeight());
        g2d.drawImage(img, 0, 0, null);
        
        g2d.setColor(Color.WHITE);
        Stroke str = new BasicStroke(2.0f);
        g2d.setStroke(str);
        g2d.drawRect(0, 0, selImg.getWidth(), selImg.getHeight());
                
        str = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_BEVEL, 0, new float[] {12, 12}, 0.0f);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(str);
        g2d.drawRect(0, 0, selImg.getWidth(), selImg.getHeight());
    }
    
    void setImage(Image img) {
        try {
            ImageIO.write((BufferedImage)img, "png", 
                           Project.getProject().getProjectFile(name));
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.img = (BufferedImage) img;
        
        buildSelImage();
    }
    
    Image getImage() {
        return img;
    }
    
    Image getSelImage() {
        return selImg;
    }
    
    int getId() {
        return id;
    }
}