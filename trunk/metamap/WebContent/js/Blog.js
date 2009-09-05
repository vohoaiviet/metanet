// class blog

function Blog() {
    
    var id;
    var date;
    var message;
    var journeyName;
    var longitude;
    var latitude;
    var elevation;
    var photoPath;
    var photoPathThumb;
    var photoWidth;
    var photoHeight;
    var onDemand;
    
    //*********************************************************************************************
    
    // id
    this.setID = function(id) {
        this.id = id;
    };
    
    this.getID = function() {
        return this.id;
    };
    
    //*********************************************************************************************
    
    // date
    this.setCreationDate = function(date) {
        this.date = date;
    };
    
    this.getCreationDate = function() {
        return this.date;
    };
    
    //*********************************************************************************************
    
    // message
    this.setMessage = function(message) {
        this.message = message;
    };
    
    this.getMessage = function() {
        if(this.message == "NULL") {
            return null;
        }
        return this.message;
    };
    
    //*********************************************************************************************
    
    // journey name
    this.setJourneyName = function(journeyName) {
        this.journeyName = journeyName;
    };
    
    this.getJourneyName = function() {
        if(this.journeyName == "NULL") {
            return null;
        }
        return this.journeyName;
    };
    
    //*********************************************************************************************
    
    // longitude
    this.setLongitude = function(longitude) {
        this.longitude = longitude;
    };
    
    this.getLongitude = function() {
        if(this.longitude == "-1") {
            return null;
        }
        return this.longitude;
    };
    
    //*********************************************************************************************
    
    // latitude
    this.setLatitude = function(latitude) {
        this.latitude = latitude;
    };
    
    this.getLatitude = function() {
        if(this.latitude == "-1") {
            return null;
        }
        return this.latitude;
    };
    
    //*********************************************************************************************
    
    // elevation
    this.setElevation = function(elevation) {
        this.elevation = elevation;
    };
    
    this.getElevation = function() {
        if(this.elevation == "-1") {
            return null;
        }
        return this.elevation;
    };
    
    //*********************************************************************************************
    
    // photo path
    this.setPhotoPath = function(path) {
        this.photoPath = path;
    };
    
    this.getPhotoPath = function() {
        if(this.photoPath == "NULL") {
            return null;
        }
        return this.photoPath;
    };
    
    //*********************************************************************************************
    
     // photo path for thumbnail
    this.setPhotoPathThumb = function(path) {
        this.photoPathThumb = path;
    };
    
    this.getPhotoPathThumb = function() {
        if(this.photoPathThumb == "NULL") {
            return null;
        }
        return this.photoPathThumb;
    };
    
    //*********************************************************************************************
    
    // photo width
    this.setPhotoWidth = function(width) {
        this.photoWidth = width;
    };
    
    this.getPhotoWidth = function() {
        if(this.photoWidth == -1) {
            return null;
        }
        return this.photoWidth;
    };
    
    // photo height
    this.setPhotoHeight = function(height) {
        this.photoHeight = height;
    };
    
    this.getPhotoHeight = function() {
        if(this.photoHeight == -1) {
            return null;
        }
        return this.photoHeight;
    };
    
    //*********************************************************************************************
    
    // on demand: defines if the blog content will be loaded from the server on demand
    this.setOnDemand = function(onDemand) {
        this.onDemand = onDemand;
    };
    
    this.getOnDemand = function() {
        return this.onDemand;
    };
    
    //*********************************************************************************************
    
    // html formatted blog content, suitable to be displayed in map info window
    this.getContentHtml = function() {
        var html = '<div>';
        html += '<div style="color:green; padding-left:25px">'+this.date+'</div>';

        if(this.getMessage() != null) {
            html += '<div style="width: 160px; font-size:14px; font-weight:bold; padding-top:5px; padding-left:25px; padding-right:25px">'+this.message+'</div>';
        }
        // insert photo here
        if(this.getPhotoPathThumb() != null) {
            html += '<div style="width: 160px; height: 120px; padding-top:5px; padding-bottom:2px; padding-left:25px; padding-right:25px">';
            html += '<a href="javascript:blogManager.displayPhotoFull('+this.id+')" onmouseover="Tip(\'large\', BGCOLOR, \'#DAF79B\', BORDERCOLOR, \'#848F60\', CLICKCLOSE, true)">';
            html += '<img style="border: 2px solid #848F60" src="'+this.photoPathThumb+'"/>';
            html += '</a>';
            html += '</div>';
        }
        if(this.getJourneyName() != null) {
            html += '<div style="color:#CCCCCC; padding-top: 5px; padding-left:25px">'+this.journeyName+'</div>';
        }
        html += '</div>';
        return html;
    };
    
     //*********************************************************************************************
    
    // html formatted blog content, suitable to be displayed in map info window
    this.getContentPhotoFull = function() {
        var html = '<div style="width: '+this.getPhotoWidth()+'px; height: '+ this.getPhotoHeight()+'px">';
        if(this.getPhotoPath() != null) {
            html += '<a href="javascript:blogManager.displayBlogContent('+this.id+')" onmouseover="Tip(\'small\', BGCOLOR, \'#DAF79B\', BORDERCOLOR, \'#848F60\', CLICKCLOSE, true)">';
            html += '<img style="border: 2px solid #848F60" src="'+this.photoPath+'"/>';
            html += '</a>';
        } else
        {
            html += '<p>no large version.</p>';
        }
        html += '</div>';
        return html;
    };
};

