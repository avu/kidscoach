var gobj_type_text = "text";

function TextPrim (pid, x0, y0, str, size, color, ffamily, fweight, fstyle) {
    SPrim.call(this, pid, gobj_type_text, x0, y0, [0, 0], ["text"], 
        [str, size, color, ffamily, fweight, fstyle]);
}

TextPrim.prototype = Object.create(SPrim.prototype);

TextPrim.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    var grp = document.createElementNS(svgNS, "g");    
    
    var p = document.createElementNS(svgNS, "text");
    p.setAttributeNS(null,"x",this.coords[0]);
    p.setAttributeNS(null,"y",this.coords[1]);
    p.setAttributeNS(null,"font-size",this.data[1]);
    p.setAttributeNS(null,"fill", this.data[2]);
    p.setAttributeNS(null,"style", "font-family:" + this.data[3] + 
                     "; font-weight:" + this.data[4] + 
                     "; font-style:" + this.data[5]);
                 
    var v = document.createTextNode(this.data[0]);
    p.appendChild(v);
    grp.appendChild(p);

    if (this.editMode) {
        this.showControlPoints(grp);
    }
    this.addDragProp(grp);
    
    svgRoot.appendChild(grp);
    this.node = grp;
}

TextPrim.prototype.showControlPoints = function(grp) {
    if (mode == mode_show) return;

    this.addControlPoint(grp, this.coords[0], this.coords[1]);
}

TextPrim.prototype.getX = function() {
    return this.x;
}

TextPrim.prototype.getY = function() {
    return this.y;
}

TextPrim.prototype.setX = function(x) {
    this.x = x;
}

TextPrim.prototype.setY = function(y) {
    this.y = y;
}

TextPrim.prototype.getWidth = function() {
    return 0;
}

TextPrim.prototype.getHeight = function() {
    return 0;
}

TextPrim.prototype.setWidth = function(w) {
}

TextPrim.prototype.setHeight = function(h) {
}

TextPrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("objects", "text", this.id);

        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
}

TextPrim.prototype.addControlPoint = function(grp, x, y) {
    var c = document.createElementNS(svgNS, "circle");
    c.setAttributeNS(null,"cx",x);
    c.setAttributeNS(null,"cy",y);
    c.setAttributeNS(null,"r",5);
    c.setAttributeNS(null,"style", "stroke:rgb(0,0,0);stroke-width:2;fill:red;pointer-events:none");
    grp.appendChild(c);
    return c;
}

TextPrim.prototype.setColor = function(c) {
    this.data[2] = c;
    if (this.node) {
        var list = this.node.getElementsByTagName("text");
        list.item(0).setAttributeNS(null,"fill", this.data[2]);
    }
};

TextPrim.prototype.setText = function(txt) {
    this.data[0] = txt;
    if (this.node) {
        var list = this.node.getElementsByTagName("text");
        var p = list.item(0);
        var vo = p.firstChild;
        p.removeChild(vo);
        var v = document.createTextNode(this.data[0]);
        p.appendChild(v);
    }
};

TextPrim.prototype.getText = function() {
    return this.data[0];
};
TextPrim.prototype.setTextFamilyWeightSyle = function(txt, ff, fw, fs) {
    this.data[0] = txt;
    this.data[3] = ff;
    this.data[4] = fw;
    this.data[5] = fs;
    if (this.node) {
        var list = this.node.getElementsByTagName("text");
        var p = list.item(0);
        this.node.removeChild(p);
        p = document.createElementNS(svgNS, "text");
        p.setAttributeNS(null,"x",this.coords[0]);
        p.setAttributeNS(null,"y",this.coords[1]);
        p.setAttributeNS(null,"font-size",this.data[1]);
        p.setAttributeNS(null,"fill", this.data[2]);
        p.setAttributeNS(null,"style", "font-family:" + this.data[3] + 
                     "; font-weight:" + this.data[4] + 
                     "; font-style:" + this.data[5]);
                 
        var v = document.createTextNode(this.data[0]);
        p.appendChild(v);
        this.node.appendChild(p);
    }
};

TextPrim.prototype.getFontFamily = function() {
    return this.data[3];
};

TextPrim.prototype.getFontWeight = function() {
    return this.data[4];
};

TextPrim.prototype.getFontStyle = function() {
    return this.data[5];
};