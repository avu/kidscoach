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
import java.util.logging.Logger;
import javax.swing.DropMode;
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
public class ResourcePanel extends JPanel {
    private static final Logger log = 
        Logger.getLogger(ResourcePanel.class.getName());
    
    JTable source;
    JScrollPane scrollPane;
    ResourceTableModel tableModel;
    
    public ResourcePanel() {
        super(new BorderLayout());
        tableModel = new ResourceTableModel();
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
                int [] rows = ResourcePanel.this.source.getSelectedRows();
                ResourcePanel.this.getParent().repaint();
            }
            
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 ) {
                    int [] rows = ResourcePanel.this.source.getSelectedRows();
                }
            }
        });
        
        source.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int [] rows = ResourcePanel.this.source.getSelectedRows();
                ResourcePanel.this.getParent().repaint();
            }
        });
        
        source.getColumnModel().getColumn(0).setCellRenderer(new ResCellRenderer());
        scrollPane = new JScrollPane(source);
        scrollPane.setPreferredSize(new Dimension(400,600));
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }
    
    JTable getSource() {
        return source;
    }
    
    public class ResCellRenderer  extends JPanel implements TableCellRenderer {
        
        Image img;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {            
            img = isSelected?((ResElem)value).getSelImage():((ResElem)value).getImage();
            table.setRowHeight(img.getHeight(null)+2);
            
            return this;
        }
        
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0,0, null);
        }
    }
    ResourceTableModel getTableModel() {
        return tableModel;
    }
}
