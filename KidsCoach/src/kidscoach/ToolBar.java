
 package kidscoach;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import javax.swing.*;

public class ToolBar extends JToolBar {
    static HashMap<String,JComponent> map = new HashMap<String,JComponent>();
    static final int DT_ACTION_CMD = 0;
    static final int DT_IMAGE_NAME = 1;
    static final int DT_INPUT_TEXT = 1;
    static final int DT_COMBO_ITEM = 1;    
    static final int DT_TOOLTIP_TEXT = 2;
    static final int DT_LABEL_TEXT = 2;
    static final int DT_CTRL_TYPE = 3;
    
    public static final String BUTTON = "button";
    public static final String TEXT_FIELD_INT = "text_field_int";
    public static final String LABEL = "label";
    public static final String COLOR = "color";
    public static final String LIST = "list";
    
    private class ActionFocusListener implements FocusListener {
        ActionListener actionListener;
        Object source;
        String command;
        
        public ActionFocusListener(ActionListener actionListener, 
                Object source, String command) 
        {
            this.actionListener = actionListener;
            this.source = source;
            this.command = command;
        }
        
        @Override
        public void focusGained(FocusEvent e) {
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            actionListener.actionPerformed(new ActionEvent(source, 0, command));
        }
    }

    public ToolBar(ActionListener actionListener, String[][] btns) {
        Insets margins = new Insets(0, 0, 0, 0);
        for (int i = 0; i < btns.length; i++) {
            if (btns[i].length <= DT_CTRL_TYPE || BUTTON.equals(btns[i][DT_CTRL_TYPE])) { 
                ToolBarButton button = new ToolBarButton(
                    new ImageIcon(this.getClass().getResource(
                        "/resources/image/" + btns[i][DT_IMAGE_NAME])));
                button.setToolTipText(btns[i][DT_TOOLTIP_TEXT]);
                button.setActionCommand(btns[i][DT_ACTION_CMD]);
                button.setMargin(margins);
                button.addActionListener(actionListener);
                map.put(btns[i][DT_ACTION_CMD], button);
                add(button);
            } else if (TEXT_FIELD_INT.equals(btns[i][DT_CTRL_TYPE])) {
                final ToolBarTextField txtFld = new ToolBarTextField();
                txtFld.setActionCommand(btns[i][DT_ACTION_CMD]);
                txtFld.addActionListener(actionListener);
                txtFld.setText(btns[i][DT_INPUT_TEXT]);
                txtFld.setMinimumSize(new Dimension(40, 40));
                map.put(btns[i][DT_ACTION_CMD], txtFld);
                txtFld.addFocusListener(
                    new ActionFocusListener(actionListener, txtFld,
                        btns[i][DT_ACTION_CMD]));
                add(txtFld);
            } else if (LABEL.equals(btns[i][DT_CTRL_TYPE])) {
                JLabel l = new JLabel(btns[i][DT_LABEL_TEXT]);
                add(l);
            } else if (COLOR.equals((btns[i][DT_CTRL_TYPE]))) {
                BufferedImage img = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 32, 32);
                g.setColor(Color.BLACK);
                g.drawRect(1, 1, 30, 30);
            
                ToolBarButton button = new ToolBarButton(new ImageIcon(img));
                button.setToolTipText(btns[i][DT_TOOLTIP_TEXT]);
                button.setActionCommand(btns[i][DT_ACTION_CMD]);
                button.setMargin(margins);
                button.addActionListener(actionListener);
                map.put(btns[i][DT_ACTION_CMD], button);
                add(button);
            } else if (LIST.equals(btns[i][DT_CTRL_TYPE])) {
                JComboBox<String> cb = new JComboBox<String>();
                if ("font-family".equals(btns[i][DT_COMBO_ITEM])) {
                    GraphicsEnvironment ge = 
                        GraphicsEnvironment.getLocalGraphicsEnvironment();
                    
                    String []fontFamilies = ge.getAvailableFontFamilyNames();
                    int curFontFamily = 0;
                    for (int j = 0; j < fontFamilies.length; j++) {
                        cb.addItem(fontFamilies[j]);
                        if ("arial".equalsIgnoreCase(fontFamilies[j])) {
                            curFontFamily = j;
                        }
                    }
                    cb.setSelectedIndex(curFontFamily);
                }
                cb.setToolTipText(btns[i][DT_TOOLTIP_TEXT]);
                cb.setActionCommand(btns[i][DT_ACTION_CMD]);
                cb.addActionListener(actionListener);
                add(cb);
            }
        }
    }
    
    public static synchronized void disableButton(String cmd) {
        map.get(cmd).setEnabled(false);
    }
    
    public static synchronized void enableButton(String cmd) {
        map.get(cmd).setEnabled(true);
    }
    
    public static synchronized JComponent getControl(String cmd) {
        return map.get(cmd);
    }
}

