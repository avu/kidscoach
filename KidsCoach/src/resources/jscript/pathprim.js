var gobj_type_path = "path";


function PathPrim (pid, x, y, coords, color) {
    SPrim.call(this, pid, gobj_type_path, x, y, coords, ["path"], [color]);
    this.x0 = 0;
    this.y0 = 0;
    this.x1 = 0;
    this.y1 = 0;
}

PathPrim.prototype = Object.create(SPrim.prototype);

PathPrim.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    var grp = document.createElementNS(svgNS, "g");    
    
    if (this.coords.length >= 6) {
        var p = document.createElementNS(svgNS, "path");
        var d = "M " + this.coords[0] + " " + this.coords[1] + " ";
        for (var i = 6; i <= this.coords.length; i += 4) {
            d = d + "Q " + this.coords[i - 4] + " " + this.coords[i - 3] + " ";
            d = d + this.coords[i - 2] + " " + this.coords[i - 1] + " "
        }
        d = d + " Z";
        p.setAttributeNS(null,"d",d);
        p.setAttributeNS(null,"style", "fill:" + this.data[0] + 
            ";stroke-width:2");
        grp.appendChild(p);
    }
    
    this.addDragProp(grp);
    
    if (this.editMode) {
        this.showControlPoints(grp);
    }
    
    svgRoot.appendChild(grp);
    this.node = grp;
};

PathPrim.prototype.showControlPoints = function(grp) {
    if (mode == mode_show) return;

    for (var i = 0; i < this.coords.length; i+=2) {
        var cp = this.addControlPoint(grp, this.coords[i], 
            this.coords[i + 1]);
        cp.setAttributeNS(null, "onmousedown", "mouseDownCP(evt," + i + ")");    
    }
};

PathPrim.prototype.getX = function() {
    return this.x;
};

PathPrim.prototype.getY = function() {
    return this.y;
};

PathPrim.prototype.setX = function(x) {
    this.x = x;
};

PathPrim.prototype.setY = function(y) {
    this.y = y;
};

PathPrim.prototype.getWidth = function() {
    return 0;
};

PathPrim.prototype.getHeight = function() {
    return 0;
};

PathPrim.prototype.setWidth = function(w) {
};

PathPrim.prototype.setHeight = function(h) {
};

PathPrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("shapes", "path", this.id);
        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
};

PathPrim.prototype.addControlPoint = function(grp, x, y) {
    var c = document.createElementNS(svgNS, "circle");
    c.setAttributeNS(null,"cx",x);
    c.setAttributeNS(null,"cy",y);
    c.setAttributeNS(null,"r",5);
    c.setAttributeNS(null,"style", "stroke:rgb(0,0,0);stroke-width:2;fill:red");
    grp.appendChild(c);
    if (x < this.x0) this.x0 = x;
    if (y < this.y0) this.y0 = y;
    if (x > this.x1) this.x1 = x;
    if (y > this.y1) this.y1 = y;
    return c;
};

PathPrim.prototype.cover = function (targ) {
    show_status("this.x=" + this.x0);
    return Math.abs(this.x + (this.x0 + this.x1)*0.5 - 
                    targ.x - targ.w*0.5) < cover_prec &&
           Math.abs(this.y + (this.y0 + this.y1)*0.5 -
                    targ.y - targ.h*0.5) < cover_prec;
};