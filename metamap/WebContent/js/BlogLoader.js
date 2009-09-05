var map = null;
var blogManager;
var waypointManager;

window.onload = function() {
    loadMap();
    
    waypointManager = new WaypointManager();
    blogManager = new BlogManager();
    
    waypointManager.init();
    blogManager.init();
};

// unload map - deallocate browser resources
window.onunload = function() {
    GUnload();
};

/***********************************************************************************/

function loadMap() {
    if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map"));
        
        map.addControl(new GSmallMapControl());
        map.addControl(new GOverviewMapControl());
        map.addControl(new GMapTypeControl());
        map.addControl(new GZoomControl(
            /* first set of options is for the visual overlay.*/
            {
                nOpacity:.2,
                sBorder:"2px solid #848F60"
            },
            /* second set of options is for everything else */
            {
                sButtonHTML:"<img src='/tmblog/style/images/loupe.gif' />",
                sButtonZoomingHTML:"<img style='border:2px solid #848F60' src='/tmblog/style/images/loupe.gif' />",
                oButtonStartingStyle:{width:'26px',height:'26px'}
            },
            /* third set of options specifies callbacks */
            {
//                buttonClick:function(){display("Looks like you activated GZoom!")},
//                dragStart:function(){display("Started to Drag . . .")},
//                dragging:function(x1,y1,x2,y2){display("Dragging, currently x="+x2+",y="+y2)},
//                dragEnd:function(nw,ne,se,sw,nwpx,nepx,sepx,swpx){display("Zoom! NE="+ne+";SW="+sw)},
            }
        ));	
        
        map.clearOverlays();
        // start position - Ottakring (19 is highest zoom level)
        map.setCenter(new GLatLng(48.2105, 16.331043), 10);
        map.setMapType(G_HYBRID_TYPE);
    }
};


