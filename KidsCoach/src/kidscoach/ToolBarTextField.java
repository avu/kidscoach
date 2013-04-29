package kidscoach;

import java.awt.*;
import java.text.NumberFormat;
import javax.swing.*;

public class ToolBarTextField extends JFormattedTextField {
    private static final Insets margins =
            new Insets(0, 0, 0, 0);
    
    public ToolBarTextField() {
        super(NumberFormat.getIntegerInstance());
        setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        setColumns(3);
        setMargin(margins);
    }
    
    public ToolBarTextField(String text, String tooltip, String actionCommand) {
        this();
        setText(text);
        setToolTipText(tooltip);
        
        setActionCommand(actionCommand);
    }
}

