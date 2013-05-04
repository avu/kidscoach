function Scene() {
    this.tarr = new Array();
    this.oarr = new Array();
    this.barr = new Array();
    this.parr = new Array();
    this.draggingObject = null;
    this.selectedObject = null;
    this.isResize = false;
    this.movecp = false;
    this.curCPNum = -1;
    this.nMouseOffsetX = 0;
    this.nMouseOffsetY = 0;
    this.mouseDownX = 0;
    this.mouseDownY = 0;
    this.curW = 0;
    this.curH = 0;
    this.fxNode = null;
    this.fxAnim1 = null;
    this.fxAnim2 = null;
    this.fxAnim3 = null;
    this.fxAnim4 = null;
    this.toolStartX = 0;
    this.toolStartY = 0;
    this.constrPrim = null;
}
    
Scene.prototype.addTarget = function (tid, x, y, w, h) {
    var t = new Target(tid, x, y, w, h);
    t.createNode();
    this.tarr.push(t);
}
    
Scene.prototype.removeTarget = function (tid) {
    for (var i = this.barr.length - 1; i >= 0; i--) {
        if (this.barr[i].tid == tid) {
            this.barr.splice(i,i);
        }
    }
                
    for (var i = 0; i < this.tarr.length; i++) {
        if (this.tarr[j].id == tid) {
            this.tarr[j].removeNode();
            this.tarr.splice (j, j);
        }
    }
}
     
Scene.prototype.addObject = function (oid, name, x, y, w, h) {
    var o = new SObj(oid, name, x, y, w, h);
    o.createNode();
    this.oarr.push(o);
}
Scene.prototype.createRect = function (pid, x0, y0, w, h, color) {
    var p = new RectPrim(pid, x0, y0, w, h, color);
    p.createNode();
    this.parr.push(p);
}

Scene.prototype.createEllipse = function (pid, x0, y0, rx, ry, color) {
    var p = new EllipsePrim(pid, x0, y0, rx, ry, color);
    p.createNode();
    this.parr.push(p);
}

Scene.prototype.createLine = function (pid, x0, y0, x1, y1, width, color) {
    var p = new LinePrim(pid, x0, y0, x1, y1, width, color);
    p.createNode();
    this.parr.push(p);
}

Scene.prototype.createPath = function (pid, x, y, coords, color) {
    var p = new PathPrim(pid, x, y, coords, color);
    p.createNode();
    this.parr.push(p);
}

Scene.prototype.createText = function (pid, x, y, str, s, c) {
    var p = new TextPrim(pid, x, y, str, s, c);
    p.createNode();
    this.parr.push(p);    
}

Scene.prototype.createPObj = function (id, name, coords, data) {
    var p = new SPrim(0, coords, [name], data);
    p.createNode();
    this.parr.push(p);
}

Scene.prototype.deselect = function () {
    for (var i = 0; i < this.oarr.length; i++) {
        this.oarr[i].deselect();
    }
    
    for (i = 0; i < this.parr.length; i++) {
        this.parr[i].deselect();       
    }
}
    
Scene.prototype.pressObject = function (evt) {
    var node = evt.currentTarget;
    if (!node) return;
    
    this.deselect();
    
    for (var i = 0; i < this.oarr.length; i++) {
        if (this.oarr[i].node == node) {
            this.draggingObject =  this.oarr[i];
            this.draggingObject.updateNode();
            this.draggingObject.select();
            this.selectedObject = this.oarr[i];
            break;
        }
    }
    
    if (mode != mode_show) {
        for (i = 0; i < this.parr.length; i++) {
            if (this.parr[i].node == node) {
                this.draggingObject =  this.parr[i];
                this.draggingObject.updateNode();
                this.draggingObject.select();
                this.selectedObject = this.parr[i];
                break;    
            }
        }
    }
    if (!this.draggingObject) return;
        
    if (mode == mode_edit && evt.button == 2) {
        importPackage(Packages.kidscoach);
        Project.getProject().popupMenu(
            evt.clientX, evt.clientY, "редактирование");
    } else {
        var p = document.documentElement.createSVGPoint();
        p.x = evt.clientX;
        p.y = evt.clientY;
        
        var m = getScreenCTM(document.documentElement);

        p = p.matrixTransform(m.inverse());
        this.nMouseOffsetX = p.x - parseInt(node.getAttribute("dragx"));
        this.nMouseOffsetY = p.y - parseInt(node.getAttribute("dragy"));
        this.mouseDownX = p.x;
        this.mouseDownY = p.y;
        this.curW = this.draggingObject.getWidth();
        this.curH = this.draggingObject.getHeight();
    }
}
    
Scene.prototype.endDrag = function () {
    this.draggingObject = null;
}
    
Scene.prototype.getTargetForObject = function (id) {
    for (var i = 0; i < this.barr.length; i++) {
        if (this.barr[i].oid == id) {
            for (var j = 0; j < this.tarr.length; j++) {
                if (this.tarr[j].id == this.barr[i].tid) {
                    return this.tarr[j];
                }
            }
        }
    }
    return null;
}
    
Scene.prototype.completeTargets = function () {
    for (var i = 0; i < this.tarr.length; i++) {
        if (!this.tarr[i].done) return false;
    }
    return true;
}
    
Scene.prototype.getDraggingObject = function () {
    return this.draggingObject;
}

Scene.prototype.getSelectedObject = function () {
    return this.selectedObject;
}
    
Scene.prototype.bindTarget = function (tid, oid) {
    this.barr.push(new Binding(tid, oid));
    for (var i = 0; i < this.tarr.length; i++) {
        if (this.tarr[i].id == tid) {
            this.tarr[i].ref++;
            break;
        }
    }
}
    
Scene.prototype.clearScene = function () {
    for (var i = 0; i < this.tarr.length; i++) {
        this.tarr[i].removeNode();
    }
    this.tarr.length = 0;
    for (i = 0; i < this.oarr.length; i++) {
        this.oarr[i].removeNode();
    }
    for (i = 0; i < this.parr.length; i++) {
        this.parr[i].removeNode();
    }
    this.oarr.length = 0;
    this.barr.length = 0;
    this.parr.length = 0;
        
    this.removeCompleteFX();
}
    
Scene.prototype.resetScene = function () {
    this.deselect();
    
    for (var i = 0; i < this.tarr.length; i++) {
        this.tarr[i].done = false;
    }
        
    for (var i = 0; i < this.oarr.length; i++) {
        this.oarr[i].x = this.oarr[i].ex - this.oarr[i].getWidth()/2.0;
        this.oarr[i].y = this.oarr[i].ey - this.oarr[i].getHeight()/2.0;
        this.oarr[i].updateNode();
    }
        
    this.removeCompleteFX();
}
    
Scene.prototype.completeFX = function () {
    var svgRoot = document.documentElement;
    var docw = svgRoot.getBBox().width;
    var doch = svgRoot.getBBox().height;
    var ew = 100;
    var eh = 100;
    var fx = (docw - ew)/2;
    var fy = (doch - eh)/2;
           
    this.fxNode = document.createElementNS(svgNS, "image");
    this.fxNode.setAttributeNS(null,"x", fx);	
    this.fxNode.setAttributeNS(null,"y", fy);
    this.fxNode.setAttributeNS(null,"width", ew);	
    this.fxNode.setAttributeNS(null,"height", eh);
    this.fxNode.setAttributeNS(xlinkNS, "xlink:href", "face.svg");                
    this.fxNode.setAttributeNS(null, "onmousedown", "mouseDownFX(evt)");
        
    this.fxAnim1 = document.createElementNS(svgNS, "animate");
    this.fxAnim1.setAttributeNS(null, "attributeName", "width");
    this.fxAnim1.setAttributeNS(null, "attributeType", "XML");
    this.fxAnim1.setAttributeNS(null, "from", ew);
    this.fxAnim1.setAttributeNS(null, "to", "600");
    this.fxAnim1.setAttributeNS(null, "begin", "0s");
    this.fxAnim1.setAttributeNS(null, "dur", "15s");
    this.fxAnim1.setAttributeNS(null, "repeatCount", "1");
    this.fxAnim1.setAttributeNS(null, "fill", "freeze");

    this.fxAnim2 = document.createElementNS(svgNS, "animate");
    this.fxAnim2.setAttributeNS(null, "attributeName", "height");
    this.fxAnim2.setAttributeNS(null, "attributeType", "XML");
    this.fxAnim2.setAttributeNS(null, "from", eh);
    this.fxAnim2.setAttributeNS(null, "to", "600");
    this.fxAnim2.setAttributeNS(null, "begin", "0s");
    this.fxAnim2.setAttributeNS(null, "dur", "15s");
    this.fxAnim2.setAttributeNS(null, "repeatCount", "1");
    this.fxAnim2.setAttributeNS(null, "fill", "freeze");

    this.fxAnim3 = document.createElementNS(svgNS, "animate");
    this.fxAnim3.setAttributeNS(null, "attributeName", "x");
    this.fxAnim3.setAttributeNS(null, "attributeType", "XML");
    this.fxAnim3.setAttributeNS(null, "from", fx);
    this.fxAnim3.setAttributeNS(null, "to", fx - 300);
    this.fxAnim3.setAttributeNS(null, "begin", "0s");
    this.fxAnim3.setAttributeNS(null, "dur", "15s");
    this.fxAnim3.setAttributeNS(null, "repeatCount", "1");
    this.fxAnim3.setAttributeNS(null, "fill", "freeze");

    this.fxAnim4 = document.createElementNS(svgNS, "animate");
    this.fxAnim4.setAttributeNS(null, "attributeName", "y");
    this.fxAnim4.setAttributeNS(null, "attributeType", "XML");
    this.fxAnim4.setAttributeNS(null, "from", fy);
    this.fxAnim4.setAttributeNS(null, "to", fy - 300);
    this.fxAnim4.setAttributeNS(null, "begin", "0s");
    this.fxAnim4.setAttributeNS(null, "dur", "15s");
    this.fxAnim4.setAttributeNS(null, "repeatCount", "1");
    this.fxAnim4.setAttributeNS(null, "fill", "freeze");
        
    this.fxNode.appendChild(this.fxAnim1);
    this.fxNode.appendChild(this.fxAnim2);
    this.fxNode.appendChild(this.fxAnim3);
    this.fxNode.appendChild(this.fxAnim4);
        
    svgRoot.appendChild(this.fxNode);
    this.fxAnim1.beginElement();
    this.fxAnim2.beginElement();        
    this.fxAnim3.beginElement();        
    this.fxAnim4.beginElement();        

}
    
Scene.prototype.removeCompleteFX = function () {
    if (this.fxNode) {
        scn.fxNode.parentNode.removeChild(scn.fxNode);
        scn.fxAnim1.parentNode.removeChild(scn.fxAnim1);
        scn.fxAnim2.parentNode.removeChild(scn.fxAnim2);
        scn.fxAnim3.parentNode.removeChild(scn.fxAnim3);
        scn.fxAnim4.parentNode.removeChild(scn.fxAnim4);
        scn.fxAnim1 = null;
        scn.fxAnim2 = null;
        scn.fxAnim3 = null;
        scn.fxAnim4 = null;
        scn.fxNode = null;
    }
}
    
Scene.prototype.startNewLine = function(p) {
    this.commitPrim();
    this.deselect();
    this.toolStartX = p.x;
    this.toolStartY = p.y;

    this.constrPrim = 
        new LinePrim(0, this.toolStartX, this.toolStartY, p.x, p.y, 
                     line_width, prim_color);
                     
    this.constrPrim.editMode = true;
    this.constrPrim.createNode();
}
 
Scene.prototype.endNewLine = function(p) {
    if (!this.constrPrim) return;
    if (this.toolStartX == p.x && this.toolStartY == p.y) {
        this.constrPrim.editMode = false;
        this.constrPrim.updateNode();
        this.constrPrim = null;
        return;
    }
    
    importPackage(Packages.kidscoach);
    var lid = Project.getProject().createNewLine(this.toolStartX, 
                                                 this.toolStartY, p.x, p.y, 
                                                 this.constrPrim.data[0], 
                                                 this.constrPrim.data[1]);
    this.constrPrim.editMode = false;
    
    this.constrPrim.id = lid;
    this.constrPrim.coords[2] = p.x - this.constrPrim.x;
    this.constrPrim.coords[3] = p.y - this.constrPrim.y;
    this.constrPrim.updateNode();
    this.constrPrim.select();
    this.parr.push(this.constrPrim);
    this.constrPrim = null;
}

Scene.prototype.startNewEllipse = function(p) {
    this.commitPrim();
    this.deselect();
    this.toolStartX = p.x;
    this.toolStartY = p.y;
    this.constrPrim = 
        new EllipsePrim(0, this.toolStartX, this.toolStartY,
                        Math.abs(p.x - this.toolStartX),
                        Math.abs(p.y - this.toolStartY),
                        prim_color);
                        
    this.constrPrim.editMode = true;
    this.constrPrim.createNode();
}

Scene.prototype.endNewEllipse = function(p) {
    if (!this.constrPrim) return;
    if (this.toolStartX == p.x && this.toolStartY == p.y) {
        this.constrPrim.editMode = false;
        this.constrPrim.updateNode();
        this.constrPrim = null;
        return;
    }
    
    importPackage(Packages.kidscoach);
    var lid = Project.getProject().createNewEllipse(
        this.toolStartX, this.toolStartY, Math.abs(p.x - this.toolStartX),
        Math.abs(p.y - this.toolStartY), this.constrPrim.data[0]);

    this.constrPrim.editMode = false;
    this.constrPrim.id = lid;
    this.constrPrim.coords[2] = Math.abs(p.x - this.constrPrim.x);
    this.constrPrim.coords[3] = Math.abs(p.y - this.constrPrim.y);
    this.constrPrim.updateNode();
    this.constrPrim.select();
    this.parr.push(this.constrPrim);
    this.constrPrim = null;
}

Scene.prototype.startNewRect = function(p) {
    this.commitPrim();
    this.deselect();
    this.toolStartX = p.x;
    this.toolStartY = p.y;
    
    this.constrPrim = new RectPrim(0, this.toolStartX, this.toolStartY, 
                                   Math.abs(p.x - this.toolStartX), 
                                   Math.abs(p.y - this.toolStartY),
                                   prim_color);
    this.constrPrim.editMode = true;
                               
    this.constrPrim.createNode();
}

Scene.prototype.endNewRect = function(p) {
    if (!this.constrPrim) return;
    if (this.toolStartX == p.x && this.toolStartY == p.y) {
        this.constrPrim.editMode = false;
        this.constrPrim.updateNode();
        this.constrPrim = null;
        return;
    }
    
    importPackage(Packages.kidscoach);
    var lid = Project.getProject().createNewRect(
        this.toolStartX, this.toolStartY, Math.abs(p.x - this.toolStartX),
        Math.abs(p.y - this.toolStartY), this.constrPrim.data[0]);

    this.constrPrim.editMode = false;
    this.constrPrim.id = lid;
    this.constrPrim.coords[2] = Math.abs(p.x - this.constrPrim.x);
    this.constrPrim.coords[3] = Math.abs(p.y - this.constrPrim.y);
    this.constrPrim.updateNode();
    this.constrPrim.select();
    this.parr.push(this.constrPrim);
    this.constrPrim = null;
}

Scene.prototype.startNewPath = function(p) {
    this.commitPrim();
    this.deselect();
    this.toolStartX = p.x;
    this.toolStartY = p.y;
    this.constrPrim = new PathPrim(0, this.toolStartX, this.toolStartY, [0,0],
                                   prim_color);
    
                                
    this.constrPrim.editMode = true;
    this.constrPrim.createNode();
}

Scene.prototype.addPointToPath = function(p) {
    if (!this.constrPrim) return false;
    if (Math.abs(p.x + 6 - this.toolStartX) < 20 && 
        Math.abs(p.y + 6 - this.toolStartY) < 20) 
    {
        importPackage(Packages.kidscoach);
        var str = "";
        for (var i = 0; i < this.constrPrim.coords.length; i++) {
            str = str + this.constrPrim.coords[i];
            if (i != this.constrPrim.coords.length - 1) {
                str = str + " ";
            }
        }
        var lid = Project.getProject().createNewPath(str);
        this.constrPrim.id = lid;
        this.parr.push(this.constrPrim);
        this.constrPrim.editMode = false;

        this.constrPrim.updateNode();
        this.constrPrim = null;
        return false;
    } else {
        this.constrPrim.coords.push(p.x - this.constrPrim.x);
        this.constrPrim.coords.push(p.y - this.constrPrim.y);
        this.constrPrim.updateNode();
        return true;
    }
}

Scene.prototype.endNewPath = function() {
    if (this.constrPrim.coords.length < 6) {
        this.constrPrim.removeNode();
        this.constrPrim = null;
        return;
    }

    importPackage(Packages.kidscoach);
    var str = "";
    for (var i = 0; i < this.constrPrim.coords.length; i++) {
        str = str + this.constrPrim.coords[i] + " ";
    }
    var lid = Project.getProject().createNewPath(str,this.constrPrim.data[0]);
    this.constrPrim.id = lid;
    this.parr.push(this.constrPrim);
    
    this.constrPrim.editMode = false;
    this.parr.push(this.constrPrim);
    this.constrPrim.updateNode();
    this.constrPrim.select();
    this.constrPrim = null;
}

Scene.prototype.startNewText = function (p) {
    this.commitPrim();
    this.deselect();
    this.toolStartX = p.x;
    this.toolStartY = p.y;
    this.constrPrim = new TextPrim(0, this.toolStartX, this.toolStartY, 
                                   "", text_size, prim_color);
    this.constrPrim.editMode = true;
    this.constrPrim.createNode();
}

Scene.prototype.endNewText = function () {
    if (this.constrPrim.data[0].length == 0) {
        this.constrPrim.removeNode();
        this.constrPrim = null;
        return;
    }
    
    importPackage(Packages.kidscoach);
    var lid = Project.getProject().createNewText(
        this.toolStartX, this.toolStartY, this.constrPrim.data[0], 
        this.constrPrim.data[1], this.constrPrim.data[2]);
                                                 
    this.constrPrim.id = lid;
                
    this.constrPrim.editMode = false;
    this.parr.push(this.constrPrim);
    this.constrPrim.updateNode();
    this.constrPrim.select();
    this.constrPrim = null;
}

Scene.prototype.commitPrim = function() {
    if (this.constrPrim) {
        if (this.constrPrim.prims[0] == "path") {
            this.endNewPath();
        } else if (this.constrPrim.prims[0] == "text") {
            this.endNewText();
        }
    }
}

Scene.prototype.keyboard = function(e) {
    if (e.charCode == ascii_esc) {
        if (this.constrPrim) {
            this.constrPrim.removeNode();
            this.constrPrim = null;
        }
    } else if (e.charCode == ascii_enter) {
        this.commitPrim();
    } else if (e.charCode == ascii_backspace || e.charCode == ascii_delete) {
        if (this.constrPrim) {
            if (this.constrPrim.prims[0] == "text") {
                var s = this.constrPrim.data[0];
                if (s.length > 0) {
                    this.constrPrim.data[0] = s.slice(0,s.length - 1);
                    this.constrPrim.updateNode();
                }    
            }
        } else {
            this.deleteSelection();            
        }
    } else if (e.charCode != 65535) {
        var letter = String.fromCharCode(e.charCode);
        if (e.shiftKey) {
            letter = letter.toUpperCase();
        }

        if (this.constrPrim && this.constrPrim.prims[0] == "text") {
            this.constrPrim.data[0] = this.constrPrim.data[0] + letter;
            this.constrPrim.updateNode();
        }
    }
};

Scene.prototype.deleteSelection = function() {
    if (mode != mode_edit) {
        return;
    }
    
    importPackage(Packages.kidscoach);
    var r = [];
    
    for (var i = 0; i < this.oarr.length; i++) {
        if (this.oarr[i].selection) {
            r.push(i);
        }
    }
      
    var q = [];
    
    for (i = 0; i < this.parr.length; i++) {
        if (this.parr[i].selection) {
            q.push(i);
        }       
    }

    this.deselect();
    for (i = r.length - 1; i >= 0; i--) {
        this.oarr[r[i]].removeNode();
        Project.getProject().deleteElement(this.oarr[r[i]].id);
        this.oarr.splice (r[i], r[i]);
    }

    for (i = q.length - 1; i >= 0; i--) {
        this.parr[q[i]].removeNode();
        Project.getProject().deleteElement(this.parr[q[i]].id);            
        this.parr.splice (q[i], q[i]);
    }
};