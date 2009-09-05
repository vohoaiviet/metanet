// class blog manager

function BlogManager() {
    
    var blogTable = null;
    var loadedBlogs = null;
    
    this.init = function() {
        createBlogLabels();
        
        blogTable = new Array();
        loadedBlogs = document.getElementById("blogList");
        loadedBlogs.focus();
        
        // register event handler for list items (DOM level 0 Model 2 style)
        // hold focus on blog list
        document.onkeydown = function() {
            loadedBlogs.focus();
        };
        document.onkeyup = function() {
            loadedBlogs.focus();
        };
        loadedBlogs.onchange = blogManager.displaySelectedBlogProperties;
        loadedBlogs.onkeypress = function(e) {
            // mozilla, microsoft
            var e = e || window.event;
            if(e.keyCode == 13) {
                blogManager.loadBlog(getSelectedBlogId());
            }
        };
        loadedBlogs.ondblclick = function() {
            blogManager.loadBlog(getSelectedBlogId());
        };
    };
    
    /***********************************************************************************/
    
    // loads all blogs from the server which are not defined as "onDemand""
    // (blogs with short content like text messages)
    this.loadBlogList = function() {
        var request = GXmlHttp.create();
        request.open("GET", "/tmblog/BlogLoader?action=getBlogList", true);
        request.onreadystatechange = getCallbackFunction(request, processXMLResponse);
        request.send(null);
    };
    
    /***********************************************************************************/
    
    // get the content of a specific blog which is defined as "onDemand"
    this.loadBlog = function(id) {
        var blog = this.getBlog(id);
        
        // check wheather the blog content has to be loaded from the server
        if(blog.getOnDemand() == 1) {
            showMessage("loading on demand...");
            // get blog data from server when blog content is exhausting
            var request = GXmlHttp.create();
            request.open("GET", "/tmblog/BlogLoader?action=getBlog&id="+id, true);
            request.onreadystatechange = getCallbackFunction(request, processXMLResponse);
            //showMessage("request for blog "+id);
            request.send(null);
        }
        // show blog content in map info window
        blogManager.displayBlogContent(id);
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

function processXMLResponse(blogsXML){
    // obtain the array of blogs and loop through it
    var blogTableUpdate = new Array();
    var blogList = blogsXML.documentElement.getElementsByTagName("blog");
    for (var i = 0; i < blogList.length; i++) {
        var id = blogList[i].getElementsByTagName("id")[0].firstChild.nodeValue;
        var date = blogList[i].getElementsByTagName("date")[0].firstChild.nodeValue;
        var journeyName = blogList[i].getElementsByTagName("journeyName")[0].firstChild.nodeValue;
        var message = blogList[i].getElementsByTagName("message")[0].firstChild.nodeValue;
        var photoPath = blogList[i].getElementsByTagName("photoPath")[0].firstChild.nodeValue;
        var photoPathThumb = blogList[i].getElementsByTagName("photoPathThumb")[0].firstChild.nodeValue;
        var photoWidth = parseInt(blogList[i].getElementsByTagName("photoWidth")[0].firstChild.nodeValue);
        var photoHeight = parseInt(blogList[i].getElementsByTagName("photoHeight")[0].firstChild.nodeValue);
        var onDemand = parseInt(blogList[i].getElementsByTagName("onDemand")[0].firstChild.nodeValue);
        
        // get client side blog data
        var blog = blogManager.getBlog(id);
        
        // check if the retrieved blog id is corresponding with the blog id associated with it's waypoint
        if(id == blog.getID()) {
            blog.setCreationDate(date);
            blog.setMessage(message);
            blog.setJourneyName(journeyName);
            blog.setPhotoPath(photoPath);
            blog.setPhotoPathThumb(photoPathThumb);
            blog.setPhotoWidth(photoWidth);
            blog.setPhotoHeight(photoHeight);
            blog.setOnDemand(onDemand);
            // update blog table
            blogManager.setBlog(blog);
            // update blog update table
            blogTableUpdate[i] = blog;
        }
    }
    if(blogTableUpdate.length > 0) {
        updateBlogBrowser(blogTableUpdate);
    }
};

function updateBlogBrowser(newBlogs) {
    // create new option entries
    var blogCounter = loadedBlogs.length;
    for(var i = newBlogs.length-1; i >= 0; i--) {
        var blog = newBlogs[i];
        if(blog != null) {
            var blogName = (blogCounter+1)+".\u0020"+blog.getCreationDate();
            if(blogCounter+1 < 10) {
                blogName = "0"+blogName;
            }
            var option = new Option(blogName, blog.getID());
            loadedBlogs.options[blogCounter] = option;
            blogCounter++;
        }
    }
    // display props of latest blog
    var newIndex = loadedBlogs.length-1;
    loadedBlogs.selectedIndex = newIndex;
    blogManager.displaySelectedBlogProperties();
    
    if(waypointManager.isLiveUpdate()) {
        // show content of latest blog
        blogManager.loadBlog(getSelectedBlogId());
    }
};

/***********************************************************************************/

this.displaySelectedBlogProperties = function() {
    // waypoint information
    waypointManager.displayWaypointValues(getSelectedBlogId());
    // blog content information
    blogManager.displayBlogValues(getSelectedBlogId());
};

function getSelectedBlogId() {
    for (i = 0; i < loadedBlogs.length; ++i) {
        if (loadedBlogs.options[i].selected == true) {
            return loadedBlogs.options[i].value;
        }
    }
    return null;
};

this.setSelectedBlogId = function(id) {
    for (i = 0; i < loadedBlogs.length; ++i) {
        if (loadedBlogs.options[i].value == id) {
            loadedBlogs.selectedIndex = i;
        }
    }
};

this.displayBlogValues = function(id) {
    clearBlogValues();
    
    var blog = blogManager.getBlog(id);
    var blogInfoValue = new Array();
    
    blogInfoValue[0] = blog.getJourneyName();
    blogInfoValue[1] = blog.getPhotoPath();
    blogInfoValue[2] = blog.getMessage();
    
    for (var i = 0; i < blogInfoValue.length; i++) {
        elementValue = document.createElement('span');
        
        if(blogInfoValue[i] == null) {
            if(i == 0) {
                value = document.createTextNode("n/a");
                elementValue.setAttribute('style','color:#CCCC99');
            }
            else {
                value = document.createTextNode("no");
                elementValue.setAttribute('style','color:#CC3300');
            }
        }
        else {
            if(i == 0) {
                value = document.createTextNode(blogInfoValue[i]);
                elementValue.setAttribute('style','font-weight:bold');
            }
            else {
                value = document.createTextNode("yes");
                elementValue.setAttribute('style','color:#009900');
            }
        }
        elementValue.appendChild(value);
        document.getElementById('blogInfoValue').appendChild(elementValue);
        document.getElementById('blogInfoValue').appendChild(document.createElement("br"));
    }
};

function clearBlogValues() {
    var node = document.getElementById('blogInfoValue');
    if(node.hasChildNodes()) {
        while(node.childNodes.length >= 1 ) {
            node.removeChild(node.firstChild);
        }
    }
};

/***********************************************************************************/

this.displayBlogContent = function(id) {
    var blog = this.getBlog(id);
    var waypoint = waypointManager.getWaypoint(id);
    waypoint.showMapBlowup();
    waypoint.openInfoWindowHtml(blog.getContentHtml());
};

this.displayPhotoFull = function(id) {
    var blog = this.getBlog(id);
    var waypoint = waypointManager.getWaypoint(id);
    waypoint.showMapBlowup();
    waypoint.openInfoWindowHtml(blog.getContentPhotoFull());
};

/***********************************************************************************/

function createBlogLabels() {
    
    var blogInfoLabel = new Array();
    blogInfoLabel[0] = "journey:";
    blogInfoLabel[1] = "photo:";
    blogInfoLabel[2] = "message:";
    
    for (var i = 0; i < blogInfoLabel.length; i++) {
        label = document.createTextNode(blogInfoLabel[i]);
        
        elementLabel = document.createElement('span');
        elementLabel.appendChild(label);
        document.getElementById('blogInfoLabel').appendChild(elementLabel);
        document.getElementById('blogInfoLabel').appendChild(document.createElement("br"));
    }
};

/***********************************************************************************/

this.setBlog = function(blog) {
    blogTable[blog.getID()] = blog;
};

this.getBlog = function(id) {
    return blogTable[id];
};

function getBlogTable() {
    return blogTable;
};

};