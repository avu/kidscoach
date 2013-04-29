package kidscoach;


import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

class ResourceTableModel extends DefaultTableModel {
    private static final Logger log = 
        Logger.getLogger(ResourceTableModel.class.getName());

    ArrayList<ResElem> elems = new ArrayList<ResElem>();
        
    public ResourceTableModel() {
        super();
    }
        
    public void initFromDir(String dirName) {
        elems.clear();
        File dir = new File(dirName);
        for (File file : dir.listFiles()) {
            try {
                elems.add(new ResElem(file.getName()));
            } catch (Exception e) {
                log.log(Level.SEVERE, null, e);
            }
        }
    }
        
    @Override
    public int getColumnCount() {
        return 1;
    }
        
    @Override
    public int getRowCount() {
        if (elems == null) {
            return 0;
        }
        return elems.size();
    }
        
    @Override
    public String getColumnName(int col) {
        return "Oбъекты";
    }
        
    @Override
    public Object getValueAt(int row, int col) {
        return elems.get(row);
    }
        
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
        
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
        
    @Override
    public void setValueAt(Object value, int row, int col) {
    }

}