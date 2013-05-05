var gobj_type_line = "line";

function LinePrim (pid, x0, y0, x1, y1, width, color) {
    SPrim.call(this, pid, gobj_type_line, x0, y0, [0, 0, x1 - x0, y1 - y0], ["line"], [width, color]);
}

LinePrim.prototype = Object.create(SPrim.prototype);

LinePrim.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    var grp = document.createElementNS(svgNS, "g");    
    var p  = document.createElementNS(svgNS, "line");
    p.setAttributeNS(null,"x1",this.coords[0]);
    p.setAttributeNS(null,"y1",this.coords[1]);
    p.setAttributeNS(null,"x2",this.coords[2]);
    p.setAttributeNS(null,"y2",this.coords[3]);
    p.setAttributeNS(null,"style", "stroke:" + this.data[1] + 
        ";stroke-width:" + this.data[0]);
    grp.appendChild(p);
    
    var p1  = document.createElementNS(svgNS, "line");
    p1.setAttributeNS(null,"x1",this.coords[0]);
    p1.setAttributeNS(null,"y1",this.coords[1]);
    p1.setAttributeNS(null,"x2",this.coords[2]);
    p1.setAttributeNS(null,"y2",this.coords[3]);
    p1.setAttributeNS(null,"style", "stroke:white;opacity:0.1;" + 
        "stroke-width:6");

    grp.appendChild(p1);
    
    this.addDragProp(grp);
    
    if (this.editMode) {
        this.showControlPoints(grp);
    }
    
    svgRoot.appendChild(grp);
    this.node = grp;
};

LinePrim.prototype.showControlPoints = function(grp) {    
    var cp = this.addControlPoint(grp, this.coords[0], 
        this.coords[1]);
    cp.setAttributeNS(null, "onmousedown", "mouseDownCP(evt,0)");    
    cp = this.addControlPoint(grp, this.coords[2], this.coords[3]);
    cp.setAttributeNS(null, "onmousedown", "mouseDownCP(evt,2)");
};

LinePrim.prototype.getX = function() {
    return this.x;
};

LinePrim.prototype.getY = function() {
    return this.y;
};

LinePrim.prototype.setX = function(x) {
    this.x = x;
};

LinePrim.prototype.setY = function(y) {
    this.y = y;
};

LinePrim.prototype.getWidth = function() {
    return this.coords[2];
};

LinePrim.prototype.getHeight = function() {
    return this.coords[3];
};

LinePrim.prototype.setWidth = function(w) {
    this.coords[2] = w;
};

LinePrim.prototype.setHeight = function(h) {
    this.coords[3] = h;
};

LinePrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("shapes", "line", this.id);
        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
};

LinePrim.prototype.cover = function (targ) {
    return Math.abs(this.x + (this.coords[0] + this.coords[2])*0.5 - 
            targ.x - targ.w*0.5) < cover_prec &&
           Math.abs(this.y + (this.coords[1] + this.coords[3])*0.5 -
            targ.y - targ.h*0.5) < cover_prec;
};