package kidscoach;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.batik.ext.swing.GridBagConstants;

/**
 *
 * @author Alexey Ushakov
 */
public class KidsCoach extends JFrame implements ActionListener {
    
    static KidsCoach mainFrame;
    Project prj;    
    private ToolBar srcBar;
    private ToolBar trmBar;
    private ToolBar animBar;
    private ToolBar primBar;
    private ToolBar rmodeBar;
    
    public final int EDIT_MODE = 0;
    public final int SHOW_MODE = 1;
    
    private int mode = EDIT_MODE;
    
    private String currentPath = "";
    
    public int getMode() {
        return mode;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Select".equals(e.getActionCommand())) {
            prj.selectToolEnable();
        } else if ("Delete".equals(e.getActionCommand())) {
            prj.deleteSelection();
        } else if ("Start".equals(e.getActionCommand())) {
            prj.playSlide(mainFrame);
        } else if ("Stop".equals(e.getActionCommand())) {
            prj.editModeEnable();
            setTitle("Обучение в играх (редактирование)");
        } else if ("SaveProject".equals(e.getActionCommand())) {
            if (prj.isNew()) {
                JFileChooser fc = new JFileChooser(new File(""));
                fc.setSelectedFile(new File(prj.getName()));
                fc.showSaveDialog(this);
                File file = fc.getSelectedFile();
                if (file != null) {
                    if (!prj.save(file.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(
                            this, "Ошибка сохранения проекта");
                    }
                }
            } else {
                if (!prj.save()) {
                        JOptionPane.showMessageDialog(
                            this, "Ошибка сохранения проекта");                    
                }
            }
        } else if ("NewProject".equals(e.getActionCommand())) {
            if (!prj.isSaved()) {
                Object[] options = { "Да", "Отмена" };
                int res = JOptionPane.showOptionDialog(
                    this, "Проект не сохранен. Продолжить?", "Внимание",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
                
                if (res == 0) {
                    if (!prj.create()) {
                        JOptionPane.showMessageDialog(
                            this, "Ошибка создания проекта");
                    }
                }
            } else {
                if (!prj.create()) {
                    JOptionPane.showMessageDialog(
                    this, "Ошибка создания проекта");
                }
            }
        } else if ("NewSlide".equals(e.getActionCommand())) {
            prj.addNewSlide();
        } else if ("OpenProject".equals(e.getActionCommand())) {
            JFileChooser fc = new JFileChooser(new File(""));
                fc.showOpenDialog(fc);
                File file = fc.getSelectedFile();
                if (file != null) {
                    if (!prj.load(file.getAbsolutePath())) {
                        JOptionPane.showMessageDialog(
                            this, "Ошибка загрузки проекта");
                    }
                }
        } else if ("AddResource".equals(e.getActionCommand())) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "svg", "jpeg", "jpg", "png", "bmp", "gif");
            
            JFileChooser fc = new JFileChooser(new File(currentPath));
            fc.setFileFilter(filter);

            fc.showOpenDialog(fc);
            File file = fc.getSelectedFile();
            if (file != null) {
                if (!prj.addResourceToApp(file.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(
                            this, "Ошибка добавления ресурса");
                } else {
                    currentPath = file.getParent();
                    if (currentPath == null) currentPath = "";
                }
            }
        } else if ("AddPicture".equals(e.getActionCommand())) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "svg", "jpeg", "jpg", "png", "bmp", "gif");
            
            JFileChooser fc = new JFileChooser(new File(currentPath));
            fc.setFileFilter(filter);

            fc.showOpenDialog(fc);
            File file = fc.getSelectedFile();
            if (file != null) {
                if (!prj.addResource(file.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(
                            this, "Ошибка добавления картинки");
                } else if (!prj.addFileToSlide(file.getAbsolutePath(), 
                                               file.getName(), 400, 300)) 
                {
                    JOptionPane.showMessageDialog(
                            this, "Ошибка добавления картинки");
                } else {
                    currentPath = file.getParent();
                    if (currentPath == null) currentPath = "";
                }
            }
        } else if ("NewLine".equals(e.getActionCommand())) {
            prj.newLineToolEnable();
        } else if ("NewEllipse".equals(e.getActionCommand())) {
            prj.newEllipseToolEnable();
        } else if ("NewRectangle".equals(e.getActionCommand())) {
            prj.newRectangleToolEnable();            
        } else if ("NewCurvedPath".equals(e.getActionCommand())) {
            prj.newCurvedPathToolEnable();
        } else if ("NewText".equals(e.getActionCommand())) {
            prj.newTextToolEnable();
        } else if ("ChangeTextSize".equals(e.getActionCommand())) {
            JTextField fld = (JTextField)e.getSource();
            prj.changeTextSize(fld.getText());
        } else if ("ChangeLineWidth".equals(e.getActionCommand())) {
            JTextField fld = (JTextField)e.getSource();
            prj.changeLineWidth(fld.getText());
        } else if ("ChangeColor".equals(e.getActionCommand())) {
            Color pColor = JColorChooser.showDialog(this,
                                 "Выберите цвет", Color.BLACK);
            if (pColor != null) {
                JButton btn = (JButton)e.getSource();
                BufferedImage img = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(pColor);
                g.fillRect(0, 0, 32, 32);
                g.setColor(Color.BLACK);
                g.drawRect(1, 1, 30, 30);
            
                btn.setIcon(new ImageIcon(img));
                
                prj.changeColor("#" + Integer.toHexString(pColor.getRGB()).substring(2));
            }
        } else if ("FontFamily".equals(e.getActionCommand())) {
            JComboBox<String> cb = (JComboBox<String>)e.getSource();
            prj.changeFontFamily((String)cb.getSelectedItem());
        }
    }

    public KidsCoach() {
        super("Обучение в играх");
        prj = Project.getProject();
        Dimension canvasSize = new Dimension(800,600);
        prj.getCanvas().setPreferredSize(canvasSize);
     
        prj.create();

        setTitle("Обучение в играх (редактирование)");
        
        JMenuBar menuBar = new JMenuBar();
        Menu menu = new Menu(this, "Файл", new String[][] {
            {"NewProject", "s16/document-new.png", "Новый"},
            {"NewSlide", "s16/window-new.png", "Новый слайд"},
            {"OpenProject", "s16/document-open.png", "Открыть"},
            {"SaveProject", "s16/document-save.png", "Сохранить"},
            {"AddResource", "s16/image-x-generic.png", "Добавить ресурс"}
        });
        
        menuBar.add(menu);
        
        menu = new Menu(this, "Правка", new String[][] {
            {"Select", "s16/selection.png", "Выбрать объект"},
            {"Delete", "s16/edit-cut.png", "Удалить объект"}});
        
        menuBar.add(menu);
        
        menu = new Menu(this, "Запуск", new String[][] {
            {"Start", "s16/media-playback-start.png", "Запуск"},
//            {"Stop", "s16/media-playback-stop.png", "Остановить"}
        });
        
        menuBar.add(menu);
        
        menu = new Menu(this, "Графика", new String[][] {
            {"NewLine", "s16/draw_line.png", "Отрезок"},
            {"NewEllipse", "s16/ellipse.png", "Эллипс"},
            {"NewRectangle", "s16/draw_rectangle.png", "Прямоугольник"},
            {"NewCurvedPath", "s16/curved_path.png", "Путь"},
            {"NewText", "s16/draw_text.png", "Текст"},
        });
        menuBar.add(menu);
        
        menu = new Menu(this, "Rendering", new String[][] {
            {"Draw", "Draw_m.png", "Draw Shape"},
            {"Fill", "Fill_m.png", "Fill Shape"}
        });
        //menuBar.add(menu);
        
        menu = new Menu(this, "Помощь", new String[][] {
            {"HelpFile", "", "Работа с документом"},
            {"HelpEdit", "", "Редактирование"},
            {"HelpPrimitives", "", "Добавление объектов в сцену"},
        });
        
        menuBar.add(menu);
        
        menuBar.add(menu);
        setJMenuBar(menuBar);
        
        srcBar = new ToolBar(this, new String[][] {
            {"NewProject", "s32/document-new.png", "Новый Проект"},
            {"NewSlide", "s32/window-new.png", "Новый Слайд"},
            {"OpenProject", "s32/document-open.png", "Открыть Проект"},
            {"SaveProject", "s32/document-save.png", "Сохранить Проект"},
            {"AddPicture", "s32/image-x-generic.png", "Добавить картинку"},
        });
        
        trmBar = new ToolBar(this, new String[][] {
            {"Select", "s32/selection.png", "Select Object"},
            {"Delete", "s32/edit-cut.png", "Delete Object"}});
        
        animBar = new ToolBar(this, new String[][] {
            {"Start", "s32/media-playback-start.png", "Запуск"},
//            {"Stop", "s32/media-playback-stop.png", "Остановить"}
        });
        
        primBar = new ToolBar(this, new String[][] {
            {"NewLine", "s32/draw_line.png", "Отрезок"},
            //{"WidthLabel", "", "Ширина", ToolBar.LABEL},            
            {"ChangeLineWidth", "1", "Ширина", ToolBar.TEXT_FIELD_INT},
            {"NewEllipse", "s32/ellipse.png", "Эллипс"},
            {"NewRectangle", "s32/draw_rectangle.png", "Прямоугольник"},
            {"NewCurvedPath", "s32/curved_path.png", "Путь"},
            {"ChangeColor", "s32/draw_rectangle.png", "Цвет", ToolBar.COLOR},            
            {"NewText", "s32/draw_text.png", "Текст"},
            //{"TextSizeLabel", "", "Шрифт", ToolBar.LABEL},
            {"FontFamily", "font-family", "", ToolBar.LIST},            
            {"ChangeTextSize", "100", "Шрифт", ToolBar.TEXT_FIELD_INT},
        });
        
        rmodeBar = new ToolBar(this, new String[][] {
            {"Draw", "Draw.png", "Draw Shape"},
            {"Fill", "Fill.png", "Fill Shape"}
        });
        
        JPanel toolPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPane.add(srcBar, BorderLayout.WEST);
        toolPane.add(trmBar, BorderLayout.WEST);
        toolPane.add(animBar, BorderLayout.WEST);
        toolPane.add(primBar, BorderLayout.WEST);
        //toolPane.add(rmodeBar, BorderLayout.WEST);
        
        GridBagLayout gbl = new GridBagLayout();
        getContentPane().setLayout(gbl);
        GridBagConstraints constr = new GridBagConstraints();
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 0;
        constr.gridwidth = 3;
        constr.gridheight = 1;
        constr.anchor = GridBagConstants.NORTHWEST;
        
        getContentPane().add(toolPane, constr);
        
        constr = new GridBagConstraints();
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 2;
        constr.gridwidth = 3;
        constr.gridheight = 1;

        getContentPane().add(prj.getStatusLine(), constr);

        constr = new GridBagConstraints();
        constr.weightx = 0;
        constr.weighty = 100;
        constr.gridx = 0;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.fill = GridBagConstants.VERTICAL;

        prj.getSlidePanel().setPreferredSize(new Dimension(100,600));
        getContentPane().add(prj.getSlidePanel(), constr);

        
        constr = new GridBagConstraints();
        constr.weightx = 100;
        constr.weighty = 100;
        constr.gridx = 1;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;

        getContentPane().add(prj.getCanvas(), constr);

        constr = new GridBagConstraints();
        constr.weightx = 0;
        constr.weighty = 100;
        constr.gridx = 2;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.anchor = GridBagConstants.WEST;
        constr.fill = GridBagConstants.VERTICAL;
        prj.getResourcePanel().setPreferredSize(new Dimension(100,600));

        getContentPane().add(prj.getResourcePanel(), constr);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent _closeEvent) {
                prj.clear();
                System.exit(0);
            }
        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel");
        }
                
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                KidsCoach.mainFrame = new KidsCoach();
            }
        });
        
    }
}
