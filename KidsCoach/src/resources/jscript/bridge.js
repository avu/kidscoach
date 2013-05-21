var mode_edit = "edit";
var mode_show = "show";
var mode = mode_edit;
var text_size = default_text_size;
var line_width = default_line_width;
var prim_color = default_prim_color;
var font_family = default_font_family;
var font_weight = default_font_weight;
var font_style = default_font_style;
    
var tool_select = "select";
var tool_delete = "delete";
var tool_new_line = "new_line";
var tool_new_ellipse = "new_ellipse";
var tool_new_rectangle = "new_rectangle";
var tool_new_curved_path = "new_curved_path";
var tool_new_text = "new_text";
    
var tool = tool_select;
    
function set_tool(t) {
    if (t == tool_select || t == tool_delete || t == tool_new_line ||
        t == tool_new_ellipse || t == tool_new_rectangle ||
        t == tool_new_curved_path || t == tool_new_text
        ) 
        {
        tool = t;
    } else {
        alert("Unsupported tool");
    }
}
        


var scn = new Scene();

function init() {
    document.documentElement.addEventListener("keypress", function(e) {scn.keyboard(e)},true);
}
function clear_scene() {
    scn.clearScene();
}

function delete_selection() {
    scn.deleteSelection();
}

function delete_element(id) {
    scn.deleteElement(id);
}

function bind_target(tid, oid) {
    scn.bindTarget(tid,oid);
}
    
function add_object(oid, name, x, y, w, h) {
    scn.addObject(oid, name, x-w/2.0, y-h/2.0, w, h);
}
    
function set_mode(m) {
    if (m == mode_edit || m == mode_show) {
        mode = m;
        scn.resetScene();
    } else {
        alert("Unsupported mode");
    }
}
        
function mouseDown(evt) { 
    scn.pressObject(evt);
}
    
function mouseDownResize(evt) {
    scn.isResize = true;
}
 
function mouseDownCP(evt,num) {
    scn.curCPNum = num;
    scn.movecp = true;
}

function create_target(id, x, y) {
    scn.addTarget(id, x - 30, y - 30, 60, 60);
}

function create_line(id, x1, y1, x2, y2, w, c) {
    scn.createLine(id, x1, y1, x2, y2, w, c);
}

function create_ellipse(id, cx, cy, rx, ry, c) {
    scn.createEllipse(id, cx, cy, rx, ry, c);
}

function create_rect(id, x, y, w, h, c) {
    scn.createRect(id, x, y, w, h, c);
}

function create_path(id, str, c) {
    var arr = str.split(" ");
    var coords = new Array();
    var x = parseFloat(arr[0]);
    var y = parseFloat(arr[1]);
    
    coords.push(0);
    coords.push(0);
    for (var i = 2; i < arr.length; i+=2) {
        coords.push(parseFloat(arr[i]) - x);
        coords.push(parseFloat(arr[i+1]) - y);
    }
    scn.createPath(id, x, y, coords, c);
}

function create_text(id, x, y, str, s, c, ff, fw, fs) {
    scn.createText(id, x, y, str, s, c, ff, fw, fs);
}

function set_text_size(size) {
    text_size = size;
}

function set_line_width(size) {
    line_width = size;    
}

function set_font_family(ff) {
    font_family = ff;
}
function change_color(c) {
    prim_color= c;
}

function change_prim_color(id, c) {
    scn.changePrimColor(id, c);    
}

function change_prim_text(id, txt) {
    return scn.changePrimText(id, txt);
}

function setTargetForObject(sobj,p) {
    if (sobj.type == gobj_type_target) return;
    var targ = scn.getTargetForObject(sobj.id);
    if (targ) {
        Project.getProject().changeTarget(targ.id, p.x, p.y);
        targ.x = p.x - 30;
        targ.y = p.y - 30;
        targ.removeNode();
        targ.createNode();
    } else {
        var tid = Project.getProject().addTarget(sobj.id, p.x, p.y);
        scn.addTarget(tid, p.x - 30, p.y - 30, 60, 60);
        scn.bindTarget(tid, sobj.id);
    }
}

function mouseDownScene(evt) {
    var sobj = scn.getSelectedObject();
    scn.prevSelectedObject = sobj;
    var p = document.documentElement.createSVGPoint();
    p.x = evt.clientX;
    p.y = evt.clientY;
        
    var m = getScreenCTM(document.documentElement);
    p = p.matrixTransform(m.inverse());
             
    importPackage(Packages.kidscoach);
    if(mode == mode_edit && sobj && tool == tool_select && sobj.type != gobj_type_target)
    {   
        setTargetForObject(sobj,p);
    } else if (tool == tool_new_line) {
        scn.startNewLine(p);
    } else if (tool == tool_new_ellipse) {
        scn.startNewEllipse(p);        
    } else if (tool == tool_new_rectangle) {
        scn.startNewRect(p);
    } else if (tool == tool_new_curved_path) {
        if (scn.constrPrim) {
            scn.addPointToPath(p);
        } else {
            scn.startNewPath(p);
        }
    } else if (tool == tool_new_text) {
        scn.startNewText(p);
    }
}
    
function mouseDownFX(evt) {
    scn.removeCompleteFX();
}
    
function mouseUp(evt) { 
    scn.endDrag();
    scn.isResize = false;
    scn.movecp = false;
    scn.nMouseOffsetX = 0;
    scn.nMouseOffsetY = 0;
    var p = document.documentElement.createSVGPoint();
    p.x = evt.clientX;
    p.y = evt.clientY;
        
    var m = getScreenCTM(document.documentElement);
    p = p.matrixTransform(m.inverse());
        
    if (tool == tool_new_line) {
        scn.endNewLine(p);
    } else if (tool == tool_new_ellipse) {
        scn.endNewEllipse(p);        
    } else if (tool == tool_new_rectangle) {
        scn.endNewRect(p);                
    } 
}
    
function mouseMove(evt) {
    var p = document.documentElement.createSVGPoint();
    p.x = evt.clientX;
    p.y = evt.clientY;
    var ex = 0;
    var ey = 0;    
    var m = getScreenCTM(document.documentElement);

    p = p.matrixTransform(m.inverse());
    
    if (scn.constrPrim) {
        if (tool == tool_new_line) {
            scn.constrPrim.coords[2] = p.x - scn.constrPrim.x;
            scn.constrPrim.coords[3] = p.y - scn.constrPrim.y;
            scn.constrPrim.updateNode();
        } else if (tool == tool_new_ellipse || tool == tool_new_rectangle) {
            scn.constrPrim.coords[2] = Math.abs(p.x - scn.constrPrim.x);
            scn.constrPrim.coords[3] = Math.abs(p.y - scn.constrPrim.y);
            scn.constrPrim.updateNode();
        }
        
        for (i = 0; i < scn.tarr.length; i++) {
            scn.tarr[i].updateNode();
        }
        return;
    }
    
    
    var drobj = scn.getDraggingObject();
    if(drobj) {
        if (scn.isResize) {
            var dw = p.x - scn.mouseDownX;
            var dh = p.y - scn.mouseDownY;
            drobj.resize(scn.curW, scn.curH, dw, dh);
        } else if (scn.movecp) {
            var dx = p.x - scn.mouseDownX;
            var dy = p.y - scn.mouseDownY;
            
            drobj.coords[scn.curCPNum] = p.x - drobj.x;
            drobj.coords[scn.curCPNum + 1] = p.y - drobj.y;
            
            drobj.updateNode();
            drobj.deselect();
            drobj.select();                
        } else {
            p.x -= scn.nMouseOffsetX;
            p.y -= scn.nMouseOffsetY;
            drobj.node.setAttribute("dragx", p.x);
            drobj.node.setAttribute("dragy", p.y);
            drobj.node.setAttribute("transform", "translate(" + p.x + "," + p.y + ")");
            drobj.x = p.x;
            drobj.y = p.y;
            if (mode == mode_show) {
                var t = scn.getTargetForObject(drobj.id);
                if (t && !t.done && drobj.cover(t)) 
                {
                    scn.endDrag();
                    t.done = true;
                        
                    if (scn.completeTargets()) {
                        scn.completeFX();
                    }
                        
                    scn.nMouseOffsetX = 0;
                    scn.nMouseOffsetY = 0;
                }
                
                return;
            } else { // mode == mode_edit
                ex = p.x;
                ey = p.y;
            }
        }
        
        
        importPackage(Packages.kidscoach);
        
        drobj.ex = ex;
        drobj.ey = ey;

        if (drobj.type == gobj_type_image) {
            drobj.ex += drobj.getWidth()/2.0;
            drobj.ey += drobj.getHeight()/2.0;
            Project.getProject().changeObject(drobj.id, 
                ex+drobj.getWidth()/2.0, ey+drobj.getHeight()/2.0, 
                drobj.getWidth(), drobj.getHeight());
        } else if (drobj.type == gobj_type_rect) {
            Project.getProject().changeRect(drobj.id, 
                ex, ey, drobj.getWidth(), drobj.getHeight());            
        } else if (drobj.type == gobj_type_ellipse) {
            Project.getProject().changeEllipse(drobj.id, 
                ex, ey, drobj.getWidth(), drobj.getHeight());
        } else if (drobj.type == gobj_type_line) {
            Project.getProject().changeLine(drobj.id, 
                drobj.x + drobj.coords[0], drobj.y + drobj.coords[1],
                drobj.x + drobj.coords[2], drobj.y + drobj.coords[3],
                drobj.data[0], drobj.data[1]);            
        } else if (drobj.type == gobj_type_path) {
            var str = "";
            for (var i = 0; i < drobj.coords.length; i+=2) {
                str = str + (drobj.coords[i] + drobj.x) + " ";
                str = str + (drobj.coords[i+1] + drobj.y);
                if (i != drobj.coords.length - 2) {
                    str = str + " ";
                }
            }
            Project.getProject().changePath(drobj.id, 
                str, drobj.data[0]);
                
        } else if (drobj.type == gobj_type_text) {
            Project.getProject().changeText(drobj.id,
                ex, ey, drobj.data[0], 
                drobj.data[1], drobj.data[2]);
        } else if (drobj.type == gobj_type_target) {
            drobj.ex += drobj.getWidth()/2.0;
            drobj.ey += drobj.getHeight()/2.0;
            Project.getProject().changeTarget(drobj.id, 
                ex+drobj.getWidth()/2.0, ey+drobj.getHeight()/2.0);
        }
    }
}