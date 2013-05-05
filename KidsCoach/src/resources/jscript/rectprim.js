var gobj_type_rect = "rect";

function RectPrim (pid, x0, y0, w, h, color) {
    SPrim.call(this, pid, gobj_type_rect, x0, y0, [0, 0, w, h], ["rect"], [color]);
}

RectPrim.prototype = Object.create(SPrim.prototype);

RectPrim.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    var grp = document.createElementNS(svgNS, "g");    

    
    var p = document.createElementNS(svgNS, "rect");
    p.setAttributeNS(null,"x",this.coords[0]);
    p.setAttributeNS(null,"y",this.coords[1]);
    p.setAttributeNS(null,"width",this.coords[2]);
    p.setAttributeNS(null,"height",this.coords[3]);
    p.setAttributeNS(null,"style", "fill:" + this.data[0] + 
        ";stroke-width:2");
    grp.appendChild(p);
    this.addDragProp(grp);
    if (this.editMode) {
        this.showControlPoints(grp);
    }
    svgRoot.appendChild(grp);
    this.node = grp;
};

RectPrim.prototype.showControlPoints = function(grp) {
    var p = document.createElementNS(svgNS, "rect");
    p.setAttributeNS(null,"x",this.coords[0]);
    p.setAttributeNS(null,"y",this.coords[1]);
    p.setAttributeNS(null,"width",this.coords[2]);
    p.setAttributeNS(null,"height",this.coords[3]);
    p.setAttributeNS(null,"style", "stroke:rgb(0,0,0);fill:none" +
        ";stroke-width:1;pointer-events:none");
    grp.appendChild(p);
        
    this.addControlPoint(grp, this.coords[0], 
        this.coords[1]);                                  
    this.addControlPoint(grp, this.coords[0] + this.coords[2], 
        this.coords[1]);
    this.addControlPoint(grp, this.coords[0],
        this.coords[1] + this.coords[3]);
    var dragCP = this.addControlPoint(grp, this.coords[0] + this.coords[2], 
        this.coords[1] + this.coords[3]);
    dragCP.setAttributeNS(null, "onmousedown", "mouseDownResize(evt)");
};


RectPrim.prototype.getX = function() {
    return this.x;
};

RectPrim.prototype.getY = function() {
    return this.y;
};

RectPrim.prototype.setX = function(x) {
    this.x = x;
};

RectPrim.prototype.setY = function(y) {
    this.y = y;
};

RectPrim.prototype.getWidth = function() {
    return this.coords[2];
};

RectPrim.prototype.getHeight = function() {
    return this.coords[3];
};

RectPrim.prototype.setWidth = function(w) {
    this.coords[2] = w;
};

RectPrim.prototype.setHeight = function(h) {
    this.coords[3] = h;
};

RectPrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("shapes", "rect", this.id);
        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
};

RectPrim.prototype.cover = function (targ) {
    return Math.abs(this.x  + this.getWidth()*0.5 - targ.x - targ.w*0.5) < cover_prec &&
        Math.abs(this.y + this.getHeight()*0.5 - targ.y - targ.h*0.5) < cover_prec;
};