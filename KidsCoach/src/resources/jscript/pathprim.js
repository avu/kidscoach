var gobj_type_path = "path";


function PathPrim (pid, x, y, coords, color) {
    SPrim.call(this, pid, gobj_type_path, x, y, coords, ["path"], [color]);
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
}

PathPrim.prototype.showControlPoints = function(grp) {
    for (var i = 0; i < this.coords.length; i+=2) {
        var cp = this.addControlPoint(grp, this.coords[i], 
            this.coords[i + 1]);
        cp.setAttributeNS(null, "onmousedown", "mouseDownCP(evt," + i + ")");    
    }
}

PathPrim.prototype.getX = function() {
    return this.x;
}

PathPrim.prototype.getY = function() {
    return this.y;
}

PathPrim.prototype.setX = function(x) {
    this.x = x;
}

PathPrim.prototype.setY = function(y) {
    this.y = y;
}

PathPrim.prototype.getWidth = function() {
    return 0;
}

PathPrim.prototype.getHeight = function() {
    return 0;
}

PathPrim.prototype.setWidth = function(w) {
}

PathPrim.prototype.setHeight = function(h) {
}

PathPrim.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        Project.getProject().selectObject("shapes", "path", this.id);
        this.selection = document.createElementNS(svgNS,"g");
        this.showControlPoints(this.selection);
        this.node.appendChild(this.selection);
    }
}