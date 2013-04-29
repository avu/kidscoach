package kidscoach;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;


class TableRowTransferHandler extends TransferHandler {
    
    @Override protected Transferable createTransferable(JComponent c) {
        JTable table = (JTable) c;
        ResourceTableModel model = (ResourceTableModel)table.getModel();
        int i = table.getSelectedRow();
        File f = new File(((ResElem)model.getValueAt(i, 0)).getUri());
        
        return new StringSelection(f.getName());
    }
    
    @Override public boolean canImport(TransferSupport info) {
        
        return info.isDrop() && info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
    
    @Override public int getSourceActions(JComponent c) {
        return COPY;
    }
}