package kidscoach;


import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

class SlideTableModel extends DefaultTableModel {        
       
        public SlideTableModel(Project prj) {
            super();
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public int getRowCount() {
//            System.err.println(prj.getSlides().size());
            return Project.getProject().getSlides().size();
        }
        
        public String getColumnName(int col) {
            return "Слайды";
        }
        
        public Object getValueAt(int row, int col) {
            return Project.getProject().getSlides().get(row);
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
        public void setValueAt(Object value, int row, int col) {
        }
    }