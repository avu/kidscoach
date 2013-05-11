
var gobj_type_image = "image";
var gobj_type_geom = "geom";
var gobj_type_target = "target";

function Binding (tid, oid) {
    this.tid = tid;
    this.oid = oid;
}
    
function GObj (type) {
    this.type = type;
    this.selection = null;
    this.resizeNode = null;
    this.node = null;
}

GObj.prototype.getX = function () {
    alert("Calling getX from abstract object");
    return 0;
};

GObj.prototype.getY = function () {
    alert("Calling getY from abstract object");
    return 0;
};

GObj.prototype.setX = function (x) {
    alert("Calling setX from abstract object");
};

GObj.prototype.setY = function (y) {
    alert("Calling setY from abstract object");
};

GObj.prototype.getWidth = function () {
    alert("Calling getWidth from abstract object");
    return 0;
};

GObj.prototype.getHeight = function () {
    alert("Calling getHeight from abstract object");
    return 0;
};

GObj.prototype.setWidth = function (w) {
    alert("Calling setWidth from abstract object");
};

GObj.prototype.setHeight = function (h) {
    alert("Calling setHeight from abstract object");
};

GObj.prototype.select = function() {
    if (!this.selection && this.node) {
        importPackage(Packages.kidscoach);
        if (this.type != "target") {
            Project.getProject().selectObject("objects", "object", this.id);
        } else {
            Project.getProject().selectObject("targets", "target", this.id);            
        }
        
        this.selection = document.createElementNS(svgNS,"rect");
        this.selection.setAttributeNS(null,"id", "selection");	      
        this.selection.setAttributeNS(null,"width",this.getWidth());	
        this.selection.setAttributeNS(null,"height",this.getHeight());		
        this.selection.setAttributeNS(null,"x",0);		
        this.selection.setAttributeNS(null,"y",0);
        if (mode == mode_show) {
            this.selection.setAttributeNS(null,"stroke","black");
            this.selection.setAttributeNS(null,"stroke","none");            
        } else {
            this.selection.setAttributeNS(null,"stroke","black");            
        }
        this.selection.setAttributeNS(null,"fill-opacity","0.0");
      
        this.node.appendChild(this.selection);
        
        if (mode != mode_show) {     
            this.resizeNode = document.createElementNS(svgNS,"rect");
            this.resizeNode.setAttributeNS(null,"id", "resize");	      
            this.resizeNode.setAttributeNS(null,"width",10);	
            this.resizeNode.setAttributeNS(null,"height",10);		
            this.resizeNode.setAttributeNS(null,"x",this.getWidth() - 10);		
            this.resizeNode.setAttributeNS(null,"y",this.getHeight() - 10);	
            this.resizeNode.setAttributeNS(null,"stroke","black");
            this.resizeNode.setAttributeNS(null,"fill-opacity","0.0");
            this.resizeNode.setAttributeNS(null, "onmousedown", "mouseDownResize(evt)");
            
            this.node.appendChild(this.resizeNode);
        }
    }
};

GObj.prototype.resize = function(curW, curH, dw, dh) {
    this.setWidth(curW + dw);
    this.setHeight(curH + dh);
    this.updateNode();
    if (this.selection) {
        this.deselect();
        this.select();
    }
};

GObj.prototype.deselect = function() {
    if (this.selection) {
        this.selection.parentNode.removeChild(this.selection);
        this.selection = null;
    }
        
    if (this.resizeNode) {
        this.resizeNode.parentNode.removeChild(this.resizeNode);
        this.resizeNode = null;
    }
};

GObj.prototype.createNode = function() {
    alert("Cannot create abstract node");
};
    
GObj.prototype.removeNode = function() {
    if (this.node) {
        this.node.parentNode.removeChild(this.node);
        this.node = null;
    }
};

GObj.prototype.updateNode = function() {
    this.removeNode();
    this.createNode();
};

GObj.prototype.addDragProp = function(grp) {
    grp.setAttributeNS(null, "transform", "translate(" + this.getX() + "," + 
                       this.getY() + ")");
    grp.setAttributeNS(null, "id", this.id);
    grp.setAttributeNS(null, "width", this.getWidth());
    grp.setAttributeNS(null, "height", this.getHeight());
    grp.setAttributeNS(null, "dragx", "" + this.getX());
    grp.setAttributeNS(null, "dragy", "" + this.getY());
    grp.setAttributeNS(null, "onmousedown", "mouseDown(evt)");
};

function Target (tid, x, y, w, h) {
    GObj.call(this, gobj_type_target);
    this.id = tid;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.done = false;
    this.ref = 0;
}
    
function SObj (oid, name, x, y, w, h) {
    GObj.call(this, gobj_type_image);
    this.id = oid;
    this.ex = x + w/2.0;
    this.ey = y + w/2.0;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.name = name;
    this.img = null;
}
  
SObj.prototype = Object.create(GObj.prototype);

SObj.prototype.getX = function() {
    return this.x;
};

SObj.prototype.getY = function() {
    return this.y;
};

SObj.prototype.setX = function(x) {
    this.x = x;
};

SObj.prototype.setY = function(y) {
    this.y = y;
};

SObj.prototype.getWidth = function() {
    return this.w;
};

SObj.prototype.getHeight = function() {
    return this.h;
};

SObj.prototype.setWidth = function(w) {
    this.w = w;
};

SObj.prototype.setHeight = function(h) {
    this.h = h;
};

SObj.prototype.resize = function(curW, curH, dw, dh) {        
    var d = Math.max(dw, dh);
    this.setWidth(curW + d);
    this.setHeight(curH + d*curH/curW);
    this.img.setAttribute("width", this.getWidth());
    this.img.setAttribute("height", this.getHeight());
    //this.updateNode();
    if (this.selection) {
        this.deselect();
        this.select();
    }
};

function SPrim (pid, type, x, y, coords, prims, data) {
    GObj.call(this, type);
    this.id = pid;
    this.coords = coords;
    this.prims = prims;
    this.data = data;
    this.node = null;
    this.editMode = false;
    this.ex = x;
    this.ey = y;
    this.x = x;
    this.y = y;
}

SPrim.prototype = Object.create(GObj.prototype);

SPrim.prototype.showControlPoints = function(grp) {
    if (this.editMode && this.prims[0] != "text") {
        for (var i = 0; i < this.coords.length; i += 2) {
            this.addControlPoint(grp, this.coords[i], this.coords[i+1]);
        }
    }
};


SPrim.prototype.addControlPoint = function(grp, x, y) {
    var c = document.createElementNS(svgNS, "circle");
    c.setAttributeNS(null,"cx",x);
    c.setAttributeNS(null,"cy",y);
    c.setAttributeNS(null,"r",5);
    c.setAttributeNS(null,"style", "stroke:rgb(0,0,0);stroke-width:2;fill:red");
    grp.appendChild(c);
    return c;
};

SPrim.prototype.setColor = function(c) {
    alert("Cannot set color to abstract primitive");  
};
    
Target.prototype.contains = function (obj) {
    return this.x < obj.x && (this.x + this.w) > (obj.x + obj.w) &&
    this.y < obj.y && (this.y + this.h) > (obj.y + obj.h);
};

Target.prototype = Object.create(GObj.prototype);

Target.prototype.getX = function() {
    return this.x;
};

Target.prototype.getY = function() {
    return this.y;
};

Target.prototype.setX = function(x) {
    this.x = x;
};

Target.prototype.setY = function(y) {
    this.y = y;
};

Target.prototype.getWidth = function() {
    return this.w;
};

Target.prototype.getHeight = function() {
    return this.h;
};

Target.prototype.setWidth = function(w) {
    this.w = w;
};

Target.prototype.setHeight = function(h) {
    this.h = h;
};

Target.prototype.createNode = function() {
    if (mode == mode_show) return;
    var g = document.documentElement;
    var img = document.createElementNS(svgNS, "image");
    img.setAttributeNS(null,"width",this.w);	
    img.setAttributeNS(null,"height",this.h);
    img.setAttributeNS(xlinkNS, "xlink:href", "targ.svg");
        
    
    var grp = document.createElementNS(svgNS, "g");
                  
    grp.appendChild(img);
    this.addDragProp(grp);
    
    var s = document.getElementById("scn");
    g.appendChild(grp);
    this.node = grp;
};

   
Target.prototype.removeNode = function() {
    if (this.node) {
        this.node.parentNode.removeChild(this.node);
        this.node = null;
    }
};

Target.prototype.updateNode = function() {
    this.removeNode();
    this.createNode();
};
    
SObj.prototype.contains = function (targ) {
    return this.x < targ.x && (this.x + this.w) > (targ.x + targ.w) &&
    this.y < targ.y && (this.y + this.h) > (targ.y + targ.h);
};

SObj.prototype.cover = function (targ) {
    return Math.abs(this.x + this.w*0.5 - targ.x - targ.w*0.5) < cover_prec &&
    Math.abs(this.y + this.h*0.5 - targ.y - targ.h*0.5) < cover_prec;
};
    
SObj.prototype.createNode = function() {
    var svgRoot = document.documentElement;
    this.img = document.createElementNS(svgNS, "image");
    this.img.setAttributeNS(null,"width",this.w);	
    this.img.setAttributeNS(null,"height",this.h);
    this.img.setAttributeNS(xlinkNS, "xlink:href", "resource/" + this.name);
                
    var grp = document.createElementNS(svgNS, "g");
                  
    grp.appendChild(this.img);
    this.addDragProp(grp);
        
    svgRoot.appendChild(grp);
    this.node = grp;
};
    

SObj.prototype.removeNode = function() {
    if (this.node) {
        this.node.parentNode.removeChild(this.node);
        this.node = null;
    }
};