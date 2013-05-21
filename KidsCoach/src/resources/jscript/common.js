var svgNS = "http://www.w3.org/2000/svg";
var xlinkNS = "http://www.w3.org/1999/xlink";
var cover_prec = 10;
var default_text_size = "100";
var default_line_width = "1";
var default_prim_color = "#000000";
var default_font_family = "arial";
var default_font_weight = "normal";
var default_font_style = "normal";
var ascii_esc = 27;
var ascii_enter = 10;
var ascii_delete = 127;
var ascii_backspace = 8;

// Following is from Holger Will since ASV3 and O9 do not support getScreenTCM()
// See http://groups.yahoo.com/group/svg-developers/message/50789
function getScreenCTM(doc){
    if(doc.getScreenCTM) {
        return doc.getScreenCTM();
    }
        
    var root=doc
    var sCTM= root.createSVGMatrix()

    var tr= root.createSVGMatrix()
    var par=root.getAttribute("preserveAspectRatio")
    if (par==null || par=="") par="xMidYMid meet"//setting to default value
    parX=par.substring(0,4) //xMin;xMid;xMax
    parY=par.substring(4,8)//YMin;YMid;YMax;
    ma=par.split(" ")
    mos=ma[1] //meet;slice

    //get dimensions of the viewport
    sCTM.a= 1
    sCTM.d=1
    sCTM.e= 0
    sCTM.f=0


    w=root.getAttribute("width")
    if (w==null || w=="") w=innerWidth

    h=root.getAttribute("height")
    if (h==null || h=="") h=innerHeight

    // Jeff Schiller:  Modified to account for percentages - I'm not 
    // absolutely certain this is correct but it works for 100%/100%
    if(w.substr(w.length-1, 1) == "%") {
        w = (parseFloat(w.substr(0,w.length-1)) / 100.0) * innerWidth;
    }
    if(h.substr(h.length-1, 1) == "%") {
        h = (parseFloat(h.substr(0,h.length-1)) / 100.0) * innerHeight;
    }

    // get the ViewBox
    vba=root.getAttribute("viewBox")
    if(vba==null) vba="0 0 "+w+" "+h
    var vb=vba.split(" ")//get the viewBox into an array

    //--------------------------------------------------------------------------
    //create a matrix with current user transformation
    tr.a= root.currentScale
    tr.d=root.currentScale
    tr.e= root.currentTranslate.x
    tr.f=root.currentTranslate.y


    //scale factors
    sx=w/vb[2]
    sy=h/vb[3]


    //meetOrSlice
    if(mos=="slice"){
        s=(sx>sy ? sx:sy)
    }else{
        s=(sx<sy ? sx:sy)
    }

    //preserveAspectRatio="none"
    if (par=="none"){
        sCTM.a=sx//scaleX
        sCTM.d=sy//scaleY
        sCTM.e=- vb[0]*sx //translateX
        sCTM.f=- vb[0]*sy //translateY
        sCTM=tr.multiply(sCTM)//taking user transformations into acount

        return sCTM
    }


    sCTM.a=s //scaleX
    sCTM.d=s//scaleY
    //-------------------------------------------------------
    switch(parX){
        case "xMid":
            sCTM.e=((w-vb[2]*s)/2) - vb[0]*s //translateX

            break;
        case "xMin":
            sCTM.e=- vb[0]*s//translateX
            break;
        case "xMax":
            sCTM.e=(w-vb[2]*s)- vb[0]*s //translateX
            break;
    }
    //------------------------------------------------------------
    switch(parY){
        case "YMid":
            sCTM.f=(h-vb[3]*s)/2 - vb[1]*s //translateY
            break;
        case "YMin":
            sCTM.f=- vb[1]*s//translateY
            break;
        case "YMax":
            sCTM.f=(h-vb[3]*s) - vb[1]*s //translateY
            break;
    }
    sCTM=tr.multiply(sCTM)//taking user transformations into acount

    return sCTM
}

function show_status(str) {
    importPackage(Packages.kidscoach);
    Project.getProject().showStatus(str);
}

if (!Object.create) {
    Object.create = function (o) {
        if (arguments.length > 1) {
            throw new Error('Object.create implementation only accepts the first parameter.');
        }
        function F() {}
        F.prototype = o;
        return new F();
    };
}