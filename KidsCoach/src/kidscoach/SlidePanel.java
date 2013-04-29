/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kidscoach;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author avu
 */
public class SlidePanel extends JPanel {
    JTable source;
    JScrollPane scrollPane;
    SlideTableModel tableModel;
    Project prj;
    
    public SlidePanel(Project prj) {
        super(new BorderLayout());
        this.prj = prj;
        tableModel = new SlideTableModel(prj);
        source = new JTable(tableModel);
        TransferHandler handler = new TableRowTransferHandler();
        source.getSelectionModel().setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        source.setTransferHandler(handler);
        //source.setDropMode(DropMode.INSERT_ROWS);
        source.setDragEnabled(true);

        source.setShowGrid(false);
        
        
        source.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                int [] rows = SlidePanel.this.source.getSelectedRows();
                //System.out.print(SlidePanel.this.prj.getSlides().get(rows[0]).getId());
                SlidePanel.this.prj.readSlide(SlidePanel.this.prj.getSlides().get(rows[0]).getId());
                //SlidePanel.this.getParent().repaint();
                
            }
            
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 ) {
                    int [] rows = SlidePanel.this.source.getSelectedRows();
                    //SlidePanel.this.prj.readSlide(SlidePanel.this.prj.getSlides().get(rows[0]).getId());
                }
            }
        });
        
        source.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int [] rows = SlidePanel.this.source.getSelectedRows();
                //SlidePanel.this.getParent().repaint();
            }
        });
        
        source.getColumnModel().getColumn(0).setCellRenderer(new SlideCellRenderer());
        scrollPane = new JScrollPane(source);
        scrollPane.setPreferredSize(new Dimension(400,600));
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }
    
    JTable getSource() {
        return source;
    }
    
    public class SlideCellRenderer  extends JPanel implements TableCellRenderer {
        
        Image img;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {            
            img = isSelected?((SlideElem)value).getSelImage():((SlideElem)value).getImage();
            table.setRowHeight(img.getHeight(null)+2);
            
            return this;
        }
        
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0,0, null);
        }
    }
}
