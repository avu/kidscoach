package kidscoach;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.*;

public class Menu extends JMenu {
    static HashMap<String,JMenuItem> map = new HashMap<String,JMenuItem>();
    
    public Menu(ActionListener actionListener, String menuName, String[][] items) {
        super(menuName);
        setBackground(this.getBackground());
        
        for (int i = 0; i < items.length; i++) {
            
            JMenuItem item = null;
            if (items[i][1].length() != 0) {
                item = new JMenuItem(
                        new ImageIcon(this.getClass().getResource(
                            "/resources/image/" +
                            items[i][1])));
                item.setText(items[i][2]);
            } else {
                item = new JMenuItem(items[i][2]);
            }
            item.addActionListener(actionListener);
            item.setActionCommand(items[i][0]);
            item.setBackground(this.getBackground());
            
            map.put(items[i][0], item);
            add(item);
        }
    }
    
    public static synchronized void disableItem(String cmd) {
        map.get(cmd).setEnabled(false);
    }
    
    public static synchronized void enableItem(String cmd) {
        map.get(cmd).setEnabled(true);
    }
}

