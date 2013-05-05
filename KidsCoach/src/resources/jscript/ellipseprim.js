var gobj_type_ellipse = "ellipse";

function EllipsePrim (pid, x0, y0, rx, ry, color) {
    SPrim.call(this, pid, gobj_type_ellipse, x0, y0, [0, 0, rx, ry], ["ellipse"], [color]);
}

EllipsePrim.prototype = Object.create(SPrim.prototype);

EllipsePrim.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    var grp = document.createElementNS(svgNS, "g");    
    
    var p = document.createElementNS(svgNS, "ellipse");
    p.setAttributeNS(null,"cx",this.coords[0]);
    p.setAttributeNS(null,"cy",this.coords[1]);
    p.setAttributeNS(null,"rx",this.coords[2]);
    p.setAttributeNS(null,"ry",this.coords[3]);
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

EllipsePrim.prototype.showControlPoints = function(grp) {
    if (mode == mode_show) return;
    
    var p = document.createElementNS(svgNS, "rect");
    p.setAttributeNS(null,"x",this.coords[0] - this.coords[2]);
    p.setAttributeNS(null,"y",this.coords[1] - this.coords[3]);
    p.setAttributeNS(null,"width",2*this.coords[2]);
    p.setAttributeNS(null,"height",2*this.coords[3]);
    p.setAttributeNS(null,"style", "stroke:rgb(0,0,0);fill:none" +
        ";stroke-width:1;pointer-events:none");
    grp.appendChild(p);
        
    this.addControlPoint(grp, this.coords[0] - this.coords[2], 
        this.coords[1] - this.coords[3]);                                  
    this.addControlPoint(grp, this.coords[0] + this.coords[2], 
        this.coords[1] - this.coords[3]);
    this.addControlPoint(grp, this.coords[0] - this.coords[2], 
        this.coords[1] + this.coords[3]);
    var dragCP = this.addControlPoint(grp, this.coords[0] + this.coords[2], 
        this.coords[1] + this.coords[3]);
    dragCP.setAttributeNS(null, "onmousedown", "mouseDownResize(evt)");
};

EllipsePrim.prototype.getX = function() {
    return this.x;
};

EllipsePrim.prototype.getY = function() {
    return this.y;
};

EllipsePrim.prototype.setX = function(x) {
    this.x = x;
};

EllipsePrim.prototype.setY = function(y) {
    this.y = y;
};

EllipsePrim.prototype.getWidth = function() {
    return this.coords[2];
};

EllipsePrim.prototype.getHeight = function() {
    return this.coords[3];
};

EllipsePrim.prototype.setWidth = function(w) {
    this.coords[2] = w;
};

EllipsePrim.prototype.setHeight = function(h) {
    this.coords[3] = h;
};

EllipsePrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("objects", "ellipse", this.id);
        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
};

EllipsePrim.prototype.cover = function (targ) {
    return Math.abs(this.x - targ.x - targ.w*0.5) < cover_prec &&
    Math.abs(this.y - targ.y - targ.h*0.5) < cover_prec;
};