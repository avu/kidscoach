package kidscoach;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.bridge.UpdateManagerAdapter;
import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.ext.swing.GridBagConstants;
import org.apache.batik.gvt.event.SelectionAdapter;
import org.apache.batik.gvt.event.SelectionEvent;
import org.apache.batik.gvt.event.SelectionListener;
import org.apache.batik.swing.gvt.TextSelectionManager;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author avu
 */
public class Project implements DropTargetListener, ActionListener {
    private static final Logger log = Logger.getLogger(Project.class.getName());
    public static final String DEFAULT_NAME = "noname.zip";
    private static Project sharedProject;
    
    public static Project getProject() {
        if (sharedProject == null) {
            sharedProject = new Project();
        }
        return sharedProject;
    }
    
    SCanvas canvas;
    SCanvas savedCanvas;
    JDialog playDlg;
    JPopupMenu objectEditPopup;
    ResourcePanel resourcePanel;
    SlidePanel slidePanel;
    JLabel statusLine;
    Document prj;
    Document resList;
    String prjName = DEFAULT_NAME;
    Path tempDir;
    int objCount;
    int selectedObject;
    boolean isBuildCompleted;
    boolean isBuildForShowCompleted;
    boolean isNewDocument;
    boolean isSavedDocument;
    
    private DropTarget dropTarget;

    public final int EDIT_MODE = 0;
    public final int SHOW_MODE = 1;
    
    private int mode = EDIT_MODE;

    ArrayList<SlideElem> slides = new ArrayList<SlideElem>();
    int curSlideId = -1;
    int slideCount;
    
    HashMap<SceneElem,SceneElem> targetForObj = 
        new HashMap<SceneElem,SceneElem>();

    ResourcePanel getResourcePanel() {
        if (resourcePanel == null) {
            resourcePanel = new ResourcePanel();
        }
        return resourcePanel;
    }
    
    JLabel getStatusLine() {
        if (statusLine == null) {
            statusLine = new JLabel(" ");
        }
        return statusLine;
    }
    
    void changeWindowSize(int width, int height) {
        canvas.executeScript("document.documentElement.setAttribute(\"width\"," + width + ");" +
                             "document.documentElement.setAttribute(\"height\"," + height + ");");        
    }
    void newLineToolEnable() {
        canvas.executeScript("set_tool(\"new_line\")");
    }

    void newEllipseToolEnable() {
        canvas.executeScript("set_tool(\"new_ellipse\")");
    }

    void newRectangleToolEnable() {
        canvas.executeScript("set_tool(\"new_rectangle\")");
    }

    void newCurvedPathToolEnable() {
        canvas.executeScript("set_tool(\"new_curved_path\")");
    }

    void newTextToolEnable() {
        canvas.executeScript("set_tool(\"new_text\")");
    }

    
    void changeTextSize(String size) {
        canvas.executeScript("set_text_size(\"" + size + "\")");
    }
    
    void changeFontFamily(String ff) {
        canvas.executeScript("set_font_family(\"" + ff + "\")");
    }

    void changeLineWidth(String size) {
        canvas.executeScript("set_line_width(\"" + size + "\")");
    }

    void changeColor(String rgb) {
        canvas.executeScript("change_color(\"" + rgb + "\")");        
    }

    void changePrimColor(String pid, String rgb) {
        canvas.executeScript("change_prim_color(" + pid + ", \"" + rgb + "\")");        
    }
    
    void changePrimText(String pid, String txt) {
        canvas.executeScript("change_prim_text(" + pid + ", \"" + txt + "\")");        
    }    


    void deleteSelection() {
        canvas.executeScript("delete_selection()");        
    }
    
    void deleteElement(int id) {
        canvas.executeScript("delete_element(\"" + id + "\")");
    }
    
    private void createLine(int pid, float x1, float y1, float x2, float y2,
                            float w, String color) 
    {
        canvas.executeScript("create_line(" + pid + "," + x1 + "," + y1 + "," + 
                             x2 + "," + y2 + "," + w + "," + "\"" + color + "\")");
    }

    private void createEllipse(int pid, float cx, float cy, float rx, float ry,
                               String color) {
        canvas.executeScript("create_ellipse(" + pid + "," + cx + "," + cy + "," + 
                             rx + "," + ry + "," + "\"" + color + "\")");
    }

    private void createRect(int pid, float x, float y, float w, float h, 
                            String color) 
    {
        canvas.executeScript("create_rect(" + pid + "," + x + "," + y + "," + 
                             w + "," + h + "," + "\"" + color + "\")");
    }

    private void createPath(int pid, String coords, String color) {
        canvas.executeScript("create_path(" + pid + ",\"" + coords + "\"," + 
                "\"" + color + "\")");
    }

    private void createText(int pid, float x, float y, String txt, String size, 
                            String color, String fontFamily, String fontWeight,
                            String fontStyle) 
    {
        canvas.executeScript("create_text(" + pid + "," + x + "," + y +
                             ",\"" + txt + "\",\""  + size +  "\",\"" + color + 
                             "\",\"" + fontFamily + "\",\"" + fontWeight + 
                             "\",\"" + fontStyle + "\")");
    }

    public void showEditTextDlg() {
        final JDialog editDlg = new JDialog(KidsCoach.mainFrame);
        GridBagLayout gbl = new GridBagLayout();
        editDlg.getContentPane().setLayout(gbl);
        
        GridBagConstraints constr = new GridBagConstraints();
        final JTextField fld = new JTextField();
        fld.setText(getPrimText(Integer.toString(selectedObject)));
        constr.weightx = 100;
        constr.weighty = 100;
        constr.gridx = 0;
        constr.gridy = 0;
        constr.gridwidth = 2;
        constr.gridheight = 1;
        constr.anchor = GridBagConstants.NORTH;
        constr.fill = GridBagConstants.HORIZONTAL;
        constr.insets = new Insets(40, 20, 0, 20);
        editDlg.add(fld, constr);

        constr = new GridBagConstraints();
        final JButton okBtn = new JButton("Изменить");
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.insets = new Insets(0, 20, 20, 20);
        
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                changePrimText(Integer.toString(selectedObject), fld.getText());
                editDlg.setVisible(false);
                editDlg.dispose();
            }
        });

        editDlg.add(okBtn, constr);
        editDlg.getRootPane().setDefaultButton(okBtn);

        constr = new GridBagConstraints();
        JButton cancelBtn = new JButton("Отмена");
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 1;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.insets = new Insets(0, 20, 20, 20);


        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                editDlg.setVisible(false);
                editDlg.dispose();
            }
        });
        
        editDlg.add(cancelBtn, constr);

        editDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editDlg.setTitle("Изменение текста");
        editDlg.setPreferredSize(new Dimension(320, 180));
        editDlg.pack();
        editDlg.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Delete".equals(e.getActionCommand())) {
            deleteElement(selectedObject);
        } else if ("Color".equals(e.getActionCommand())) {
            Color pColor = JColorChooser.showDialog(getCanvas(),
                                 "Выберите цвет", Color.BLACK);
            changePrimColor(Integer.toString(selectedObject), pColor);
        } else if ("Edit".equals(e.getActionCommand())) {
            showEditTextDlg();
        } else if ("Stop".equals(e.getActionCommand())) {
            playDlg.setVisible(false);
            restoreCanvas();
            playDlg.dispose();
            playDlg = null;
        } else if ("Start".equals(e.getActionCommand())) {
            showModeEnable();
        }
    }

    static class SceneElem {
        String id;
        public SceneElem(String id) {
            this.id = id;
        }
    }
        
    private Project() {
        try {
            tempDir = Files.createTempDirectory("kidscoach");
            Files.createDirectory(tempDir.resolve("resource"));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        dropTarget = 
            new DropTarget(getCanvas(), DnDConstants.ACTION_COPY, this, true, null);

        getCanvas().addUpdateManagerListener(new UpdateManagerAdapter() {
            public void updateCompleted(UpdateManagerEvent e) {
                if (curSlideId >= 0) {
                    SlideElem se = getSlideElem(curSlideId);
                    BufferedImage img = (BufferedImage)canvas.getSnapshot(50, 50);
                    se.setImage(img);
                }
            }
        });
    }
        
    ArrayList<SlideElem> getSlides() {
        return slides;
    }
    
    final SCanvas getCanvas() {
        if (canvas == null) {
            canvas = new SCanvas();
        }
        return canvas;
    }
    
    final JPopupMenu getObjectEditPopup(String type) {
        objectEditPopup = new JPopupMenu();
        if (!"image".equals(type) && !"target".equals(type)) {
            JMenuItem item = new JMenuItem("Цвет");
            item.addActionListener(this);
            item.setActionCommand("Color");
            objectEditPopup.add(item);
        }
        if ("text".equals(type)) {
            JMenuItem item = new JMenuItem("Изменить");
            item.addActionListener(this);
            item.setActionCommand("Edit");
            objectEditPopup.add(item);
        }

        JMenuItem item = new JMenuItem("Удалить");
        item.addActionListener(this);
        item.setActionCommand("Delete");
        objectEditPopup.add(item);
        return objectEditPopup;
    }

    
    final SlidePanel getSlidePanel() {
        if (slidePanel == null) {
            slidePanel = new SlidePanel(this);
        }
        return slidePanel;
    }
    
    String getName() {
        return prjName;
    }
    
    boolean isSaved() {
        return isSavedDocument;
    }
    
    boolean isNew() {
        return isNewDocument;
    }
    
    boolean load(String name) {
        if (tempDir == null) {
            log.severe("tempDir is null");
            return false;

        }
        
        byte[] buffer = new byte[1024];
 
        try{
 
            //create output directory is not exists
            File folder = tempDir.toFile();
 
            //get the zip file content
            ZipInputStream zis = 
    		new ZipInputStream(new FileInputStream(name));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
 
            while(ze!=null){
                String fileName = ze.getName();
                File newFile = FileSystems.getDefault().getPath(tempDir.toString(), fileName).toFile();
 
                System.out.println("file unzip : "+ newFile.getAbsoluteFile());
 
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
 
                FileOutputStream fos = new FileOutputStream(newFile);             
 
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
 
                fos.close();   
                ze = zis.getNextEntry();
            }
 
            zis.closeEntry();
            zis.close();
 
            System.out.println("Done");
 
        }catch(IOException ex){
            log.log(Level.SEVERE, null, ex);
            return false;
        }
        
        prjName = name;
        isSavedDocument = true;
        isNewDocument = false;

        return load();
    }

    Element getSlide(int sid) {
        for (Node obj = prj.getFirstChild().getFirstChild(); obj != null; 
            obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("slides")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                        n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element sobj = (Element)n;
                            if (sobj.getTagName().equals("slide")) {
                                final int id = 
                                    Integer.parseInt(sobj.getAttribute("id"));
                                if (id == sid) {
                                    return sobj;
                                }
                            }
                        }
                    }
                }  
            }
        }
        return null;
    }
    
    SlideElem getSlideElem(int sid) {
        for (int i = 0; i < slides.size(); i++) {
            SlideElem s = slides.get(i);
            if (s.getId() == sid) {
                return s;
            }
        }
        return null;
    }
    
    File getProjectFile(String name) {
        return FileSystems.getDefault().getPath(tempDir.toString(),name).toFile();
    }
    
    void readSlide(int sid) {
        //if (curSlideId == sid) return;
        if (curSlideId >= 0) {
            SlideElem se = getSlideElem(curSlideId);
            BufferedImage img = (BufferedImage)canvas.getSnapshot(50, 50);
            se.setImage(img);
        }
        clearScene();
        Element s = getSlide(sid);
        if (s != null) {
            readSlide(s);
            curSlideId = sid;
        }
    }

    void addNewSlide() {
        for (Node obj = prj.getFirstChild().getFirstChild(); obj != null; 
            obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("slides")) {
                    Element sobj = prj.createElement("slide");
                    String name = "slide"+slideCount + ".png";
                    sobj.setAttributeNS(null, "name", name);
                    sobj.setAttributeNS(null, "id", Integer.toString(slideCount));
                    Element objs = prj.createElement("objects");
                    sobj.appendChild(objs);
                    Element targs = prj.createElement("targets");
                    sobj.appendChild(targs);
                    Element binds = prj.createElement("bindings");
                    sobj.appendChild(binds);

                    String emptySlidePath = null;
                    try {
                        emptySlidePath = (new File(ClassLoader.getSystemClassLoader().getResource("resources/image/EmptySlide.png").toURI())).getPath();
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        Files.copy(FileSystems.getDefault().getPath(emptySlidePath),                        
                           FileSystems.getDefault().getPath(tempDir.toString(), 
                                                            name));
                    } catch (IOException ex) {
                        Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    slides.add(new SlideElem(slideCount, name));
                    
                    slideCount++;
                    objsEl.appendChild(sobj);
                }
            }
        }

        for (Node obj = prj.getFirstChild().getFirstChild(); obj != null; 
            obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element cntEl = (Element)obj;
                if (cntEl.getTagName().equals("count")) {
                    cntEl.setAttribute("value", Integer.toString(slideCount));
                }
            }
        }
    }
    
    void readSlide(Node slide) {
        for (Node obj = slide.getFirstChild(); obj != null; 
             obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("targets")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                        n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element sobj = (Element)n;
                            if (sobj.getTagName().equals("target")) {
                                final int id = 
                                    Integer.parseInt(sobj.getAttribute("id"));
                                final float x = 
                                    Float.parseFloat(sobj.getAttribute("x"));
                                final float y = 
                                    Float.parseFloat(sobj.getAttribute("y"));
                                createTarget(id, x, y);
                            }
                        }
                    }
                } else if (objsEl.getTagName().equals("bindings")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                        n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element bind = (Element)n;
                            if (bind.getTagName().equals("bind")) {
                                final int tid = 
                                    Integer.parseInt(bind.getAttribute("tid"));
                                final int oid = 
                                    Integer.parseInt(bind.getAttribute("oid"));
                                bindTarget(tid, oid);
                            }
                        }
                    }                            
                } else if (objsEl.getTagName().equals("objects")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                        n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element pobj = (Element)n;
                            if (pobj.getTagName().equals("line")) {
                                final int pid = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final float x1 = 
                                    Float.parseFloat(pobj.getAttribute("x1"));
                                final float y1 = 
                                    Float.parseFloat(pobj.getAttribute("y1"));
                                final float x2 = 
                                    Float.parseFloat(pobj.getAttribute("x2"));
                                final float y2 = 
                                    Float.parseFloat(pobj.getAttribute("y2"));
                                float w = Float.parseFloat(pobj.getAttribute("width"));
                                String c = pobj.getAttribute("color");
                                
                                createLine(pid, x1, y1, x2, y2, w, c);
                            } else if (pobj.getTagName().equals("ellipse")) {
                                final int pid = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final float cx = 
                                    Float.parseFloat(pobj.getAttribute("cx"));
                                final float cy = 
                                    Float.parseFloat(pobj.getAttribute("cy"));
                                final float rx = 
                                    Float.parseFloat(pobj.getAttribute("rx"));
                                final float ry = 
                                    Float.parseFloat(pobj.getAttribute("ry"));
                                 String c = pobj.getAttribute("color");

                                createEllipse(pid, cx, cy, rx, ry, c);
                            } else if (pobj.getTagName().equals("rect")) {
                                final int pid = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final float x = 
                                    Float.parseFloat(pobj.getAttribute("x"));
                                final float y = 
                                    Float.parseFloat(pobj.getAttribute("y"));
                                final float w = 
                                    Float.parseFloat(pobj.getAttribute("width"));
                                final float h = 
                                    Float.parseFloat(pobj.getAttribute("height"));
                                String c = pobj.getAttribute("color");

                                createRect(pid, x, y, w, h, c);
                            } else if (pobj.getTagName().equals("path")) {
                                final int pid = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final String coords = pobj.getAttribute("coords");
                                String c = pobj.getAttribute("color");

                                
                                createPath(pid, coords, c);
                            } else if (pobj.getTagName().equals("text")) {
                                final int pid = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final float x = 
                                    Float.parseFloat(pobj.getAttribute("x"));
                                final float y = 
                                    Float.parseFloat(pobj.getAttribute("y"));
                                final String text = pobj.getAttribute("text");
                                final String s = pobj.getAttribute("size");                                
                                String c = pobj.getAttribute("color");
                                final String ff = pobj.getAttribute("font-family");
                                final String fw = pobj.getAttribute("font-weight");
                                final String fs = pobj.getAttribute("font-style");
                                createText(pid, x, y, text, s, c, ff, fw, fs);
                            } else if (pobj.getTagName().equals("object")) {
                                final int id = 
                                    Integer.parseInt(pobj.getAttribute("id"));
                                final String str = 
                                    pobj.getAttribute("name");
                                final float x = 
                                    Float.parseFloat(pobj.getAttribute("x"));
                                final float y = 
                                    Float.parseFloat(pobj.getAttribute("y"));
                                final float w = 
                                    Float.parseFloat(pobj.getAttribute("w"));
                                final float h = 
                                    Float.parseFloat(pobj.getAttribute("h"));

                                canvas.addSVGFile(id, str, x, y, w, h);
                            } else if (objsEl.getTagName().equals("count")) {
                                objCount = Integer.parseInt(objsEl.getAttribute("value"));
                            }
                        }
                    }
                }
            }
        }
    }

    boolean addResourceToApp(String name) {
        try {
            Path rp = FileSystems.getDefault().getPath(name);
            installResourceToApp(rp.toString(), rp.getFileName().toString());

        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        resourcePanel.getTableModel().initFromDir(tempDir.resolve("resource").toString());
        return true;
    }
    
        boolean addResource(String name) {
        try {
            Path rp = FileSystems.getDefault().getPath(name);
            installResource(rp.toString(), rp.getFileName().toString());

        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        resourcePanel.getTableModel().initFromDir(tempDir.resolve("resource").toString());
        return true;
    }

    
    private boolean load() {
        if (tempDir == null) {
            log.severe("tempDir is null");
            return false;
        }
        String scenePath = FileSystems.getDefault().getPath(tempDir.toString(), 
                                                        "scene.svg").toString();
        
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            File f = new File(FileSystems.getDefault().getPath(tempDir.toString(), 
                                                        "prj.xml").toString());
            prj = builder.parse(f);
        } catch (SAXException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (ParserConfigurationException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        }
        isBuildCompleted = false;
        canvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent evt) {
                if (isBuildCompleted) return;
                isBuildCompleted = true;
                
                final TextSelectionManager tmgr = canvas.getTextSelectionManager();
                tmgr.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void selectionChanged(SelectionEvent se) {
                        tmgr.clearSelection();
                    }

                    @Override
                    public void selectionDone(SelectionEvent se) {
                        tmgr.clearSelection();
                    }

                    @Override
                    public void selectionCleared(SelectionEvent se) {
                    }

                    @Override
                    public void selectionStarted(SelectionEvent se) {
                        tmgr.clearSelection();
                    }
                });

                slides.clear();
                
                for (Node obj = prj.getFirstChild().getFirstChild(); obj != null; 
                    obj = obj.getNextSibling()) 
                {
                    if (obj instanceof Element)  {
                        Element objsEl = (Element)obj;
                        if (objsEl.getTagName().equals("slides")) {
                            for (Node n = objsEl.getFirstChild(); n != null; 
                                n = n.getNextSibling()) 
                            {
                                if (n instanceof Element) {
                                    Element sobj = (Element)n;
                                    if (sobj.getTagName().equals("slide")) {
                                        final int id = 
                                            Integer.parseInt(sobj.getAttribute("id"));
                                        final String str = 
                                            sobj.getAttribute("name");
                                        slides.add(new SlideElem(id, str));
                                    }
                                }
                            }
                        } else if (objsEl.getTagName().equals("count")) {
                            slideCount = Integer.parseInt(objsEl.getAttribute("value"));
                        } 
                    }
                }
                if (slides.size() > 0) {
                    readSlide(slides.get(0).getId());
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            getResourcePanel().getTableModel().initFromDir(
                            tempDir.resolve("resource").toString());

                            ((DefaultTableModel)slidePanel.getSource().getModel()).fireTableDataChanged();
                            ((DefaultTableModel)resourcePanel.getSource().getModel()).fireTableDataChanged();
                            resourcePanel.repaint();
                            slidePanel.repaint();
                        }
                    });
                }
            } 
        });
        
        canvas.loadSVGDocument(new File(scenePath).toURI().toString());
    
        return true;
    }
    
    private boolean showCurrentSlide(final int w, final int h) {
        String scenePath = FileSystems.getDefault().getPath(tempDir.toString(), 
                                                        "scene.svg").toString();
        
        isBuildForShowCompleted = false;
        canvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent evt) {

                final TextSelectionManager tmgr = canvas.getTextSelectionManager();
                tmgr.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void selectionChanged(SelectionEvent se) {
                        tmgr.clearSelection();
                    }

                    @Override
                    public void selectionDone(SelectionEvent se) {
                        tmgr.clearSelection();
                    }

                    @Override
                    public void selectionCleared(SelectionEvent se) {
                    }

                    @Override
                    public void selectionStarted(SelectionEvent se) {
                        tmgr.clearSelection();
                    }
                });
//
                showModeEnable();
                changeWindowSize(w, h);
                clearScene();
                Element s = getSlide(curSlideId);
                if (s != null) {
                    readSlide(s);
                }
            } 
        });
        
        canvas.loadSVGDocument(new File(scenePath).toURI().toString());
        
    
        return true;
    }
    
    boolean save(String name) {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", 
                                "2");
            
            t.transform(new DOMSource(prj), 
                        new StreamResult(
                            new FileOutputStream(
                                FileSystems.getDefault().getPath(
                                tempDir.toString(), "prj.xml").toString())));
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (TransformerException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        }
        
        
        byte[] buffer = new byte[1024];
        List<String> fileList = new ArrayList<String>();
        generateFileList(fileList, tempDir.toFile());
 
        try{
 
            FileOutputStream fos = new FileOutputStream(name);
            ZipOutputStream zos = new ZipOutputStream(fos);
 
            System.out.println("Output to Zip : " + name);
 
            for(String file : fileList){
 
    		System.out.println("File Added : " + file);
    		ZipEntry ze= new ZipEntry(file);
        	zos.putNextEntry(ze);
 
        	FileInputStream in = 
                       new FileInputStream(tempDir.toFile().toString() + File.separator + file);
 
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
 
        	in.close();
            }
 
            zos.closeEntry();
            //remember close it
            zos.close();
 
            System.out.println("Done");
        }catch(IOException ex){
            log.log(Level.SEVERE, null, ex);
            return false;
        }
        
        isSavedDocument = true;
        isNewDocument = false;
        
        prjName = name;
        return true;
    }
    
    public void generateFileList(List<String> fileList, File node){
 
    	//add file only
	if(node.isFile()){
		fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
	}
 
	if(node.isDirectory()){
		String[] subNote = node.list();
		for(String filename : subNote){
			generateFileList(fileList, new File(node, filename));
		}
	}
 
    }
    
    private String generateZipEntry(String file){
    	return file.substring(tempDir.toString().length()+1, file.length());
    }
    
    boolean save() {
        return save(prjName);
        
        
    }
    
    void installFile(String from, String to) throws IOException {
        String resName = null;
        try {
            resName = (new File(ClassLoader.getSystemClassLoader().
                    getResource(from).toURI())).getPath();
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        Files.copy(FileSystems.getDefault().getPath(resName),
                   FileSystems.getDefault().getPath(tempDir.toString(),to));
    }
    
    boolean installExtFile(String from, String to) {
        try {
            Files.copy(FileSystems.getDefault().getPath(from),
                    FileSystems.getDefault().getPath(tempDir.toString()+"/resource", to),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    void installResourceFromBundle(String from, String to) throws IOException {
        String resName = null;
        try {
            resName = (new File(ClassLoader.getSystemClassLoader().
                    getResource(from).toURI())).getPath();
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        Files.copy(FileSystems.getDefault().getPath(resName),
                   FileSystems.getDefault().getPath(tempDir.toString(),"resource").resolve(to));
    }

    void installResource(String from, String to) throws IOException {
        String resName = new File(from).getPath();
        
        Files.copy(FileSystems.getDefault().getPath(resName),
                   FileSystems.getDefault().getPath(tempDir.toString(),"resource").resolve(to));
    }
        
    void installResourceToApp(String from, String to) throws IOException {
        String resName = new File(from).getPath();
        
        Files.copy(FileSystems.getDefault().getPath(resName),
                   FileSystems.getDefault().getPath(tempDir.toString(),"resource").resolve(to));
        
        String usrPath = null;
        
        try {
            usrPath = (new File(ClassLoader.getSystemClassLoader().
                    getResource("user").toURI())).getPath();
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        Files.copy(FileSystems.getDefault().getPath(resName),
            FileSystems.getDefault().getPath(usrPath).resolve(to));
        
        Element usr = lookupElement(resList.getFirstChild(), "user");
        assert(usr != null);
        Element f = resList.createElement("file");
        f.setAttribute("from", "user/" + to);
        f.setAttribute("to", to);
        usr.appendChild(f);
        
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", 
                                "2");
            
            File cfgFile = new File(ClassLoader.getSystemClassLoader().
                    getResource("resources/res_list.xml").toURI());
            t.transform(new DOMSource(resList), 
                new StreamResult(
                    new FileOutputStream(cfgFile)));
        } catch (FileNotFoundException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    
    boolean loadResList() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            File f = new File(ClassLoader.getSystemClassLoader().
                    getResource("resources/res_list.xml").toURI());
            resList = builder.parse(f);
        } catch (SAXException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (ParserConfigurationException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        } catch (URISyntaxException ex) {
            log.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    boolean installResList() {
        Element sys = lookupElement(resList.getFirstChild(), "system");
        assert(sys != null);
        for (Node n = sys.getFirstChild(); n != null;
                n = n.getNextSibling()) {
            if (n instanceof Element) {
                Element sobj = (Element) n;
                if (sobj.getTagName().equals("file")) {
                    final String from =
                            sobj.getAttribute("from");
                    final String to =
                            sobj.getAttribute("to");
                    try {
                        installFile(from, to);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, null, e);
                        return false;
                    }
                }
            }
        }
        
        Element usr = lookupElement(resList.getFirstChild(), "user");
        assert(usr != null);
        for (Node n = usr.getFirstChild(); n != null;
                n = n.getNextSibling()) {
            if (n instanceof Element) {
                Element sobj = (Element) n;
                if (sobj.getTagName().equals("file")) {
                    final String from =
                            sobj.getAttribute("from");
                    final String to =
                            sobj.getAttribute("to");
                    try {
                        installResourceFromBundle(from, to);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, null, e);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    boolean create() {
        if (tempDir == null) {
            log.severe("tempDir is null");
            return false;
        }
        
        if (tempDir.toFile().list().length != 0) {
            clear();
            try {
                tempDir = Files.createTempDirectory("kidscoach");
                Files.createDirectory(tempDir.resolve("resource"));
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        
        if (!loadResList()) {
            return false;
        }
        
        if (!installResList()) {
            return false;
        }
    
        if (load()) 
        {
            prjName = DEFAULT_NAME;
            isSavedDocument = false;
            isNewDocument = true;
            
            return true;
        }
        
        return false;
    }
    
    boolean clear() {
        if (tempDir == null) return true;
 
        try {
            Utils.delete(tempDir.toFile());
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }
        return true;
    }
    
    boolean close() {
        if (!clear()) {
            return false;
        }
        
        prjName = DEFAULT_NAME;
        isSavedDocument = false;
        isNewDocument = true;
        try {
            tempDir = Files.createTempDirectory("kidscoach");
            Files.createDirectory(tempDir.resolve("resource"));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        isBuildCompleted = false;

        return true;
    }
    
    public void clearScene() {
        canvas.executeScript("clear_scene()");        
    }
    public void selectToolEnable() {
        canvas.executeScript("set_tool(\"select\")");
    }
    
    public void deleteToolEnable() {
        canvas.executeScript("set_tool(\"delete\")");
    }
    
    public void showModeEnable() {
        canvas.executeScript("set_mode(\"show\")");
        mode = SHOW_MODE;
    }
    
    public void editModeEnable() {
        canvas.executeScript("set_mode(\"edit\")");
        mode = EDIT_MODE;
    }
    
    public void createTarget(int id, float x, float y) {
        canvas.executeScript("create_target(" + id + "," + x + "," + y + ")");
    }
    
    private void bindTarget(int tid, int oid) {
        canvas.executeScript("bind_target(" + tid + "," + oid + ")");        
    }

    // Implementation of the DropTargetListener interface
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        log.log(Level.FINE, "dragEnter, drop action = {0}", 
                dtde.getDropAction());

        processDrag(dtde);
   }

    @Override
    public void dragExit(DropTargetEvent dte) {
        log.fine("DropTarget dragExit");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        log.log(Level.FINE, "DropTarget dragOver, drop action = {0}", 
                    dtde.getDropAction());

        processDrag(dtde);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        log.log(Level.FINE, "DropTarget dropActionChanged, drop action = {0}", 
                    dtde.getDropAction());

        processDrag(dtde);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        log.log(Level.FINE, "DropTarget drop, drop action = {0}",
                dtde.getDropAction());

        // Check the the drop action
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            // Accept the drop and get the transfer data 
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = dropSVGFile(transferable, dtde.getLocation().x, 
                                          dtde.getLocation().y);

            dtde.dropComplete(result);
            
            SlideElem se = getSlideElem(curSlideId);
            BufferedImage img = (BufferedImage)canvas.getSnapshot(50, 50);
            se.setImage(img);
            
            System.out.println("Drop completed, success: " + result);
         } catch (Exception e) {
            System.out.println("Exception while handling drop " + e);

            dtde.dropComplete(false);
            }
         } else {
           System.out.println("Drop target rejected drop");
            dtde.rejectDrop();
         }
    }

    protected boolean processDrag(DropTargetDragEvent dtde) {
        int dropAction = dtde .getDropAction ();
        int sourceActions = dtde.getSourceActions();
      
        if(log.isLoggable(Level.FINE)) {
            DataFlavor[] dtf = dtde.getCurrentDataFlavors();
            for (DataFlavor d:dtf) {
                log.fine(d.getHumanPresentableName());
            }
      
            log.log(Level.FINE, "Source actions are {0}", 
                    new Integer(sourceActions));
            
            log.log(Level.FINE, "Drop action is {0}",
                    new Integer(dropAction));
        }
        
        // Reject if the object being transferred
        // or the operations available are not acceptable.
        if (!dtde.isDataFlavorSupported(DataFlavor.stringFlavor) ||
            (sourceActions & DnDConstants.ACTION_COPY) == 0) 
        {
            log.fine("Drop target rejecting drag");
            dtde.rejectDrag();
            return false;
        } else if ((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
            // Not offering copy or move - suggest a copy
            log.fine("Drop target offering COPY");
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
            return true;
        } else {
            // Offering an acceptable operation: accept
            log.fine("Drop target accepting drag");
            dtde.acceptDrag(dropAction);
            return true;
        }
   }

    public boolean addFileToSlide(String from, String to, float x, float y) {
        canvas.addSVGFile(objCount, to, x, y, 100, 100);
        Element slide = getSlide(curSlideId);
        for (Node obj = slide.getFirstChild(); obj != null;
                obj = obj.getNextSibling()) {
            if (obj instanceof Element) {
                Element objsEl = (Element) obj;
                if (objsEl.getTagName().equals("objects")) {
                    Element sobj = prj.createElement("object");
                    sobj.setAttribute("name", to);
                    sobj.setAttribute("x", Float.toString(x));
                    sobj.setAttribute("y", Float.toString(y));
                    sobj.setAttribute("w", "100");
                    sobj.setAttribute("h", "100");
                    sobj.setAttribute("id", Integer.toString(objCount));
                    objsEl.appendChild(sobj);
                }
            }
        }

        Element countEl = lookupElement(prj.getFirstChild(), "count");
        countEl.setAttribute("value", Integer.toString(objCount + 1));

        objCount++;

        SlideElem se = getSlideElem(curSlideId);
        BufferedImage img = (BufferedImage) canvas.getSnapshot(50, 50);
        se.setImage(img);
        return true;
    }
   
   // This method handles a drop for svg file
   private boolean dropSVGFile(Transferable transferable, float x, float y)
                throws IOException, UnsupportedFlavorException,
                MalformedURLException {
      String str = (String)transferable.getTransferData(DataFlavor.stringFlavor);
//      log.log(Level.FINE, "Resource file is {0}", str);
      
      String file = null;
      file = tempDir.resolve("resource").resolve(str).toString();
      if (!addFileToSlide(file, str, x, y)) {
          return false;
      }      

      return true;
   }
   
    public int addTarget(String oid, float x, float y) {
        Element slide = getSlide(curSlideId);
        Element targsEl = lookupElement(slide, "targets");
        
        int res = objCount;
        if (targsEl != null) {
            Element sobj = prj.createElement("target");
            sobj.setAttribute("id", Integer.toString(res));
            sobj.setAttribute("x", Float.toString(x));
            sobj.setAttribute("y", Float.toString(y));

            targsEl.appendChild(sobj);
            
            Element countEl = lookupElement(prj.getFirstChild(), "count");
            countEl.setAttribute("value", Integer.toString(objCount + 1));
            objCount++;
        
        
            Element bindsEl = lookupElement(slide, "bindings");
        
            if (bindsEl != null) {
                Element bnd = prj.createElement("bind");
                bnd.setAttribute("tid", Integer.toString(res));
                bnd.setAttribute("oid", oid);
                bindsEl.appendChild(bnd);
            }
            return res;
        }
        return -1;
    }
   
   public int createNewLine(float x0, float y0, float x1, float y1, float w, String color) {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
      {
          if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  Element sobj = prj.createElement("line");
                  sobj.setAttribute("id", Integer.toString(objCount));
                  sobj.setAttribute("x1", Float.toString(x0));
                  sobj.setAttribute("y1", Float.toString(y0));
                  sobj.setAttribute("x2", Float.toString(x1));
                  sobj.setAttribute("y2", Float.toString(y1));
                  sobj.setAttribute("width", Float.toString(w));
                  sobj.setAttribute("color", color);

                  objsEl.appendChild(sobj);
              }
          }
      }
      Element countEl = lookupElement(prj.getFirstChild(), "count");
      countEl.setAttribute("value", Integer.toString(objCount + 1));
            
      int res = objCount;
      objCount++;
      return res;
   }
   
   public int createNewEllipse(float cx, float cy, float rx, float ry, String color) {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
      {
          if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  Element sobj = prj.createElement("ellipse");
                  sobj.setAttribute("id", Integer.toString(objCount));
                  sobj.setAttribute("cx", Float.toString(cx));
                  sobj.setAttribute("cy", Float.toString(cy));
                  sobj.setAttribute("rx", Float.toString(rx));
                  sobj.setAttribute("ry", Float.toString(ry));
                  sobj.setAttribute("color", color);

                  objsEl.appendChild(sobj);
              }
          }
      }
       
      Element countEl = lookupElement(prj.getFirstChild(), "count");
      countEl.setAttribute("value", Integer.toString(objCount + 1));
      int res = objCount;
      objCount++;
      return res;
   }
   
   public int createNewRect(float x, float y, float width, float height, 
                            String color) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
      {
          if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  Element sobj = prj.createElement("rect");
                  sobj.setAttribute("id", Integer.toString(objCount));
                  sobj.setAttribute("x", Float.toString(x));
                  sobj.setAttribute("y", Float.toString(y));
                  sobj.setAttribute("width", Float.toString(width));
                  sobj.setAttribute("height", Float.toString(height));
                  sobj.setAttribute("color", color);

                  objsEl.appendChild(sobj);
              }
          }
      }
       
      Element countEl = lookupElement(prj.getFirstChild(), "count");
      countEl.setAttribute("value", Integer.toString(objCount + 1));

      int res = objCount;
      objCount++;
      return res; 
   }

   public int createNewText(float x, float y, String str, String s, 
                            String color, String ff, String fw, String fs) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
      {
          if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  Element sobj = prj.createElement("text");
                  sobj.setAttribute("id", Integer.toString(objCount));
                  sobj.setAttribute("x", Float.toString(x));
                  sobj.setAttribute("y", Float.toString(y));
                  sobj.setAttribute("text", str);
                  sobj.setAttribute("size", s);
                  sobj.setAttribute("color", color);
                  sobj.setAttribute("font-family", ff);
                  sobj.setAttribute("font-weight", fw);
                  sobj.setAttribute("font-style", fs);

                  objsEl.appendChild(sobj);
              }
          }
      }
       
      Element countEl = lookupElement(prj.getFirstChild(), "count");
      countEl.setAttribute("value", Integer.toString(objCount + 1));

      int res = objCount;
      objCount++;
      return res;
   }

   public int createNewPath(String coords, String color) {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
      {
          if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  Element sobj = prj.createElement("path");
                  sobj.setAttributeNS(null, "id", Integer.toString(objCount));
                  
                  sobj.setAttributeNS(null, "coords", coords);
                  sobj.setAttributeNS(null, "color", color);
                  
                  objsEl.appendChild(sobj);
              }
          }
      }
      
      Element countEl = lookupElement(prj.getFirstChild(), "count");
      countEl.setAttribute("value", Integer.toString(objCount + 1));

      int res = objCount;
      objCount++;
      return res; 
   }
   
   public Element lookupElement(Node root, String tagName) 
   {
       for (Node obj = root.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals(tagName)) {
                    return objsEl;
                }
            }
        }
       return null;
   }
   
   public Element lookupElement(Node root, String tagName, String id) 
   {
       for (Node obj = root.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element el = (Element)obj;
                if (el.getTagName().equals(tagName) && 
                    el.getAttribute("id").equals(id)) 
                {
                    return el;
                }
            }
        }
        return null;
   }
   
   public Element lookupElement(Node root, int id) 
   {
       for (Node obj = root.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element el = (Element)obj;
                if (el.getAttribute("id").equals(Integer.toString(id))) 
                {
                    return el;
                }
            }
        }
        return null;
   }
   
   public Element lookupElementByAttr(Node root, String attr, String val) 
   {
       for (Node obj = root.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element el = (Element)obj;
                if (el.getAttribute(attr).equals(val)) 
                {
                    return el;
                }
            }
        }
        return null;
   }
   
   public int changeText(String id, float x, float y, String txt, String size, 
                         String color) 
   {
       Element slide = getSlide(curSlideId);

       Element objects = lookupElement(slide, "objects");
       if (objects != null) {
           Element text = lookupElement(objects, "text", id);
           text.setAttribute("x", Float.toString(x));
           text.setAttribute("y", Float.toString(y));
           text.setAttribute("text", txt);
           text.setAttribute("size", size);
           text.setAttribute("color", color);
       }
       return 0;       
   }
   
   public int changeRect(String id, float x, float y, float width, float height) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element gobj = (Element)n;
                            if (gobj.getTagName().equals("rect") && 
                                gobj.getAttribute("id").equals(id)) 
                            {
                                gobj.setAttribute("x", Float.toString(x));
                                gobj.setAttribute("y", Float.toString(y));
                                gobj.setAttribute("width", Float.toString(width));
                                gobj.setAttribute("height", Float.toString(height));
                            }
                        }
                    }
              }
          }
      }
      return 0;
   }
   
   public int selectObject(String container, String name, String id) 
   {
       Element slide = getSlide(curSlideId);
       Element objects = lookupElement(slide, container);
       if (objects != null) {
           Element gobj = lookupElement(objects, name, id);
           objects.removeChild(gobj);
           objects.appendChild(gobj);
       }
       return 0;
   }
   
   String getPrimText(String pid) {
       Element slide = getSlide(curSlideId);
       Element objects = lookupElement(slide, "objects");
       if (objects != null) {
           Element gobj = lookupElement(objects, "text", pid);
           return gobj.getAttribute("text");
       }
       return null;
   }    

   public int changeLine(String id, float x0, float y0, float x1, float y1, 
                         float w, String color) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element gobj = (Element)n;
                            if (gobj.getTagName().equals("line") && 
                                gobj.getAttribute("id").equals(id)) 
                            {
                                gobj.setAttribute("x1", Float.toString(x0));
                                gobj.setAttribute("y1", Float.toString(y0));
                                gobj.setAttribute("x2", Float.toString(x1));
                                gobj.setAttribute("y2", Float.toString(y1));
                                gobj.setAttribute("width", Float.toString(w));
                                gobj.setAttribute("color", color);
                            }
                        }
                    }
              }
          }
      }
      return 0;
   }

   public int changePath(String id, String coords, String color) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element gobj = (Element)n;
                            if (gobj.getTagName().equals("path") && 
                                gobj.getAttribute("id").equals(id)) 
                            {
                                gobj.setAttribute("coords", coords);
                                gobj.setAttribute("color", color);
                            }
                        }
                    }
              }
          }
      }
      return 0;
   }
   
   public int changeEllipse(String id, float cx, float cy, float rx, float ry) 
   {
       Element slide = getSlide(curSlideId);

       for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
              Element objsEl = (Element)obj;
              if (objsEl.getTagName().equals("objects")) {
                  for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element gobj = (Element)n;
                            if (gobj.getTagName().equals("ellipse") && 
                                gobj.getAttribute("id").equals(id)) 
                            {
                                gobj.setAttribute("cx", Float.toString(cx));
                                gobj.setAttribute("cy", Float.toString(cy));
                                gobj.setAttribute("rx", Float.toString(rx));
                                gobj.setAttribute("ry", Float.toString(ry));
                            }
                        }
                    }
              }
          }
      }
      return 0;
   }
   
   public void changePrimColor(String id, Color col) {
        Element slide = getSlide(curSlideId);
      
        Element objs = lookupElement(slide, "objects");
        if (objs != null) {
            Element e = lookupElement(objs, Integer.parseInt(id));
            if (e != null) {
                String color = "#" + Integer.toHexString(col.getRGB()).substring(2);
                e.setAttribute("color", color);
                changePrimColor(id, color);
            }
        }
    }
   
    public void changeObject(String id, float x, float y, float w, float h) {
        Element slide = getSlide(curSlideId);

        for (Node obj = slide.getFirstChild(); obj != null; 
            obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("objects")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element sobj = (Element)n;
                            if (sobj.getTagName().equals("object")) {
                                if (sobj.getAttribute("id").equals(id)) {
                                    sobj.setAttribute("x", Float.toString(x));
                                    sobj.setAttribute("y", Float.toString(y));
                                    sobj.setAttribute("w", Float.toString(w));
                                    sobj.setAttribute("h", Float.toString(h));
                                }
                            }
                        }
                    }
                }
            }
        }
   }
   
   public void changeTarget(String id, float x, float y) {
        Element slide = getSlide(curSlideId);

        for (Node obj = slide.getFirstChild(); obj != null; 
            obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("targets")) {
                    for (Node n = objsEl.getFirstChild(); n != null; 
                         n = n.getNextSibling()) 
                    {
                        if (n instanceof Element) {
                            Element sobj = (Element)n;
                            if (sobj.getTagName().equals("target")) {
                                if (sobj.getAttribute("id").equals(id)) {
                                    sobj.setAttribute("x", Float.toString(x));
                                    sobj.setAttribute("y", Float.toString(y));
                                }
                            }
                        }
                    }
                }
            }
        }
   }

    public void addObject(String id, String name, float x, float y) {
        Element slide = getSlide(curSlideId);

        for (Node obj = slide.getFirstChild(); obj != null; 
           obj = obj.getNextSibling()) 
        {
            if (obj instanceof Element)  {
                Element objsEl = (Element)obj;
                if (objsEl.getTagName().equals("objects")) {
                    Element sobj = prj.createElement("object");
                    sobj.setAttributeNS(null, "name", name);
                    sobj.setAttributeNS(null, "id", name);
                    sobj.setAttributeNS(null, "x", Float.toString(x));
                    sobj.setAttributeNS(null, "y", Float.toString(y));

                    objsEl.appendChild(sobj);
                }
            }
        }
    }
    
    public void deleteElement(String id) {
        Element slide = getSlide(curSlideId);
      
        Element objs = lookupElement(slide, "objects");
        Element bnds = lookupElement(slide, "bindings");
        assert(objs != null);
        assert(bnds != null);
        
        Element e = lookupElement(objs, Integer.parseInt(id));
        if (e != null) {
            e.getParentNode().removeChild(e);            
            Element b = lookupElementByAttr(bnds, "oid", id);
            if (b != null) {
                b.getParentNode().removeChild(b);
            }
        }

        
        Element targs = lookupElement(slide, "targets");
        assert(targs != null);
        e = lookupElement(targs, Integer.parseInt(id));
        if (e != null) {
            e.getParentNode().removeChild(e);
            Element b = lookupElementByAttr(bnds, "tid", id);
            if (b != null) {
                b.getParentNode().removeChild(b);
            }
        }
    }
    
    public void popupMenu(int x, int y, int id, String type) {
        selectedObject = id;
        getObjectEditPopup(type).show(getCanvas(), x, y);
    }
    
    public void showStatus(String str) {
        getStatusLine().setText(str);
    }
    
    public void saveCanvas() {
        savedCanvas = canvas;
    }
    
    public void restoreCanvas() {
        canvas = savedCanvas;
    }
    
    public void setCanvas(SCanvas nc) {
        canvas = nc;
    }
    
    public void playSlide(JFrame mainFrame) {
        saveCanvas();
        setCanvas(null);
        playDlg = new JDialog(mainFrame);
        GridBagLayout gbl = new GridBagLayout();
        playDlg.getContentPane().setLayout(gbl);
        
        ToolBar showAnimBar = new ToolBar(this, new String[][] {
            {"Start", "s32/media-playback-start.png", "Запуск"},
            {"Stop", "s32/media-playback-stop.png", "Остановить"}
        });

        GridBagConstraints constr = new GridBagConstraints();
        
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 0;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        constr.anchor = GridBagConstants.NORTH;
        playDlg.add(showAnimBar, constr);

        constr = new GridBagConstraints();
        
        constr.weightx = 100;
        constr.weighty = 0;
        constr.gridx = 0;
        constr.gridy = 1;
        constr.gridwidth = 1;
        constr.gridheight = 1;
        
        
        playDlg.add(getCanvas(), constr);

        //playDlg.pack();
        playDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        playDlg.setTitle("Обучение в играх (игра)");

        playDlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                restoreCanvas();
            }
        });
                
        
        GraphicsDevice gd =
            GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice();

        //boolean isFullScreen = false;
        boolean isFullScreen = gd.isFullScreenSupported();

        if (isFullScreen) {
            int w = ((gd.getDisplayMode().getHeight() - 50)* 800)/600;
            int h = gd.getDisplayMode().getHeight() - 50;
            showCurrentSlide(w, h);

            getCanvas().setPreferredSize(
                    new Dimension(w, h));


            playDlg.setUndecorated(true);
            playDlg.setResizable(false);
            playDlg.validate();
            gd.setFullScreenWindow(playDlg);
            playDlg.setVisible(true);

        } else {
            getCanvas().setPreferredSize(new Dimension(800,600));
            showCurrentSlide(800, 600);
            playDlg.setModal(true);
            playDlg.pack();
            playDlg.setVisible(true);
        }
    }
}
