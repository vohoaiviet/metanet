// class waypoint manager

function WaypointManager() {
    
    var waypointTable = null;
    var pollInterval = null;
    var isLiveUpdate;
    
    this.init = function() {
        createWaypointLabels();
        
        waypointTable = new Array();
        pollInterval = 5000;
        
        loadWaypointList();
    };
    
    // load available waypoints from server
    function loadWaypointList() {
        if(map != null) {
            // create browser-safe XMLHttpRequest object
            var request = GXmlHttp.create();
            // Prepare an asynchronous HTTP request to the server
            request.open("GET", "/tmblog/BlogLoader?action=getWaypointList", true);
            // Returned data will be processed by this processWaypoints
            request.onreadystatechange = getCallbackFunction(request, processXMLResponse);
            request.send(null);
            isLiveUpdate = false;
        }
    };
    
    /***********************************************************************************/
    
    this.loadWaypointUpdateList = function() {
        var request = GXmlHttp.create();
        request.open("GET", "/tmblog/BlogLoader?action=getWaypointUpdateList", true);
        request.onreadystatechange = getCallbackFunction(request, processXMLResponse);
        request.send(null);
        isLiveUpdate = true;
    };
    
    /***********************************************************************************/
    
    // return an anonymous function that listens to the XMLHttpRequest instance
    function getCallbackFunction(request, processXMLResponse) {  
        return function () {
            // request complete and ok
            if (request.readyState == 4) {
                if (request.status == 200) {
                    // Pass the XML payload of the response to the handler function
                    processXMLResponse(request.responseXML);
                } else {
                // http error
                alert("HTTP error: "+request.status);
            }
        }
    }
};

/***********************************************************************************/

function processXMLResponse(waypointsXML){
    // obtain the array of waypoints and loop through it
    var waypointList = waypointsXML.documentElement.getElementsByTagName("waypoint");
    
    for (var i = 0; i < waypointList.length; i++) {
        // obtain the attributes of each marker
        var id = waypointList[i].getElementsByTagName("id")[0].firstChild.nodeValue;
        var lng = parseFloat(waypointList[i].getElementsByTagName("longitude")[0].firstChild.nodeValue);
        var lat = parseFloat(waypointList[i].getElementsByTagName("latitude")[0].firstChild.nodeValue);
        var ele = parseFloat(waypointList[i].getElementsByTagName("elevation")[0].firstChild.nodeValue);
        
        // create a new blog to be displayed on demand
        var blog = new Blog();
        blog.setID(id);
        blog.setLongitude(lng);
        blog.setLatitude(lat);
        blog.setElevation(ele);
        
        // store new blog
        blogManager.setBlog(blog);
        
        // create a display a new waypoint for the map
        var params = new Array();
        params[0] = id;
        params[1] = lat;
        params[2] = lng;
        
        createWaypoint(params);
    }
    if(waypointList.length > 0) {
        // set map center according to latest blog
        //map.setCenter(GLatLng(lat,lng), 8);
        // load all blogs from the server which are associated with a waypoint and which
        // are not defined as "onDemand"
        blogManager.loadBlogList();
    }
    // schedule next server poll
    setTimeout("waypointManager.loadWaypointUpdateList();", pollInterval);
};

/***********************************************************************************/

function createWaypoint(params) {
    var id = params[0];
    var waypoint = new GMarker(new GLatLng(params[1], params[2]));
    
    // register event handler for waypoint
    GEvent.addListener(waypoint, "click", function() {
        blogManager.loadBlog(id);
        blogManager.setSelectedBlogId(id);
    });
    GEvent.addListener(waypoint, "mouseover", function() {
        waypointManager.displayWaypointValues(id);
        blogManager.displayBlogValues(id);
    });
    GEvent.addListener(waypoint, "mouseout", function() {
        blogManager.displaySelectedBlogProperties();
    });
    
    // store new waypoint
    setWaypoint(id, waypoint);
    
    // show waypoint on map
    map.addOverlay(waypoint);
};

/***********************************************************************************/

this.displayWaypointValues = function(id) {
    clearWaypointValues();
    
    var blog = blogManager.getBlog(id);
    var waypointInfoValue = new Array();
    var unit = new Array();
    
    waypointInfoValue[0] = blog.getElevation();
    waypointInfoValue[1] = blog.getLongitude();
    waypointInfoValue[2] = blog.getLatitude();
    
    unit[0] = " m";
    unit[1] = " \u00B0";
    unit[2] = " \u00B0";
    
    for (var i = 0; i < waypointInfoValue.length; i++) {
        elementValue = document.createElement('span');
        
        if(waypointInfoValue[i] == null) {
            value = document.createTextNode("n/a");
            valueUnit = document.createTextNode("");
            elementValue.setAttribute('style','color:#CCCC99');
        } else
        {
            value = document.createTextNode(waypointInfoValue[i]);
            valueUnit = document.createTextNode(unit[i]);
        }
        elementValue.appendChild(value);
        elementValue.appendChild(valueUnit);
        document.getElementById('waypointInfoValue').appendChild(elementValue);
        document.getElementById('waypointInfoValue').appendChild(document.createElement("br"));
    }
};

function clearWaypointValues() {
    var node = document.getElementById('waypointInfoValue');
    if(node.hasChildNodes()) {
        while(node.childNodes.length >= 1 ) {
            node.removeChild(node.firstChild);
        }
    }
};

/***********************************************************************************/

function createWaypointLabels() {
    
    var waypointInfoLabel = new Array();
    waypointInfoLabel[0] = "altitude:";
    waypointInfoLabel[1] = "longitude:";
    waypointInfoLabel[2] = "latitude:";
    
    for (var i = 0; i < waypointInfoLabel.length; i++) {
        label = document.createTextNode(waypointInfoLabel[i]);
        
        elementLabel = document.createElement('span');
        elementLabel.appendChild(label);
        document.getElementById('waypointInfoLabel').appendChild(elementLabel);
        document.getElementById('waypointInfoLabel').appendChild(document.createElement("br"));
    }
};

this.isLiveUpdate = function() {
    return isLiveUpdate;
};

function setWaypoint(id, waypoint) {
    waypointTable[id] = waypoint;
};

this.getWaypoint = function(id) {
    return waypointTable[id];
};

this.getWaypointTable = function() {
    return waypointTable;
};


};