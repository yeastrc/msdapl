	//You need an anonymous function to wrap around your function to avoid conflict  
(function($){  
   
     //Attach this new method to jQuery  
     $.fn.extend({   
           
         //Plugin's name is "highlight" 
         highlightPeptide: function(options) {  
   
	    	  //Set the default values 
	          var defaults = {  
	              highlightColor: '#FFFF00', 
	              borderColor: '#888888',
	              labelColor : '#000000',
	              colors: {}
	          }  
	                
	          var options =  $.extend(defaults, options); 
	          
	          // Color source: http://en.wikipedia.org/wiki/Web_colors
	          var defaultColors = new Array();
	          defaultColors[0] = {'highlightColor': '#4169E1', 'textColor': '#FFFFFF'};  // Royal Blue
	          defaultColors[1] = {'highlightColor': '#FFD700', 'textColor': '#000000'};  // Gold 
	          defaultColors[2] = {'highlightColor': '#DA70D6', 'textColor': '#000000'};  // Orchid
	          defaultColors[3] = {'highlightColor': '#9ACD32', 'textColor': '#FFFFFF'};  // YellowGreen 
	          defaultColors[4] = {'highlightColor': '#FF7F50', 'textColor': '#FFFFFF'};  // Coral 
	    	 
	         //Iterate over the current set of matched elements  
	         return this.each(function() {  
	         
	        	  var o = options;  
                  //Assign current element to variable; should be a <span>  
                  var obj = $(this);
                  
                  //console.log( obj.html() );
                  
	           	  var text = obj.text();
	           	  
	           	  //console.log(text);
	           	  var newText = "";
	           	  
	           	  var i = 0;
	           	 
	           	  for(var i = 1; i < text.length; i += 1)  {
	           		  
	           	  	  var prevChar = text[i-1];
	           	  	  //console.log(prevChar);
	           	  	  var thisChar = text[i];
	           	  	  
	           	  	  if(thisChar == '[') {
	           	  	  	  var j = i+1;
	           	  	      while(text[j] != ']') j += 1;
	           	  	      
	           	  	      var label = text.substring(i+1, j);
	           	  	      var tooltip = label; // text within the square brackets will be used as the tooltip
	           	  	      
	           	  	      // remove a leading '+' or a '-' from the label
	           	  	      while(label.match("^\\+") || label.match("^\\-"))
	           	  	    	  label = label.substring(1,label.length);
	           	  	      
	           	  	      
	           	  	      var h_color = null; var t_color = o.labelColor;
	           	  	      
	           	  	      // If the user has provided their own highlight color use it
	           	  	      var label_key = prevChar+"_"+label;
	           	  	      //console.log(label_key);
	           	  	      var color_info = o.colors[label_key];
	           	  	      
	           	  	      
	           	  	      if(color_info)
	           	  	    	  h_color = color_info.highlightColor;
	           	  	      
	           	  	      // If we still don't have the color use a default color
	           	  	      if(!h_color) {
	           	  	    	  //console.log("getting colors");
	           	  	    	  if( defaultColors[( label * label.length ) % defaultColors.length] === undefined ) {
	           	  	    		  var foo = 'bar';
	           	  	    	  }
	           	  	    	  h_color = defaultColors[( label * label.length ) % defaultColors.length].highlightColor;
	           	  	    	  t_color = defaultColors[( label * label.length ) % defaultColors.length].textColor;
	           	  	      }
	           	  	      
	           	  	      
	           	  	      //console.log(label+" h_color: "+h_color+"; t_color: "+t_color+"; index: "+(label % defaultColors.length));
	           	  	      
	           	  	      newText += '<span style=\"background-color:'+h_color+
	           	  	      			 '; color: '+t_color+
	           	  	                 '; border:0px solid '+o.borderColor+
	           	  	                 '; cursor: help;\" title=\"'+tooltip+'\">';
	           	  	      newText += prevChar;
	           	  	      newText += '</span>';
	           	  	      i = j+1;
	           	  	      //console.log("printing highlight");
	           	  	  }
	           	  	  else {
	           	  		  	//console.log("in else; printing: "+prevChar);
	           	  	  		newText += prevChar;
	           	  	  }
	           	  	  if(i == text.length - 1) {
	           	  		//console.log("last printing: "+thisChar);
	           	  	  	newText += text[i];
	           	  	  }
	           	  }
	           	  obj.html(newText);
	         });  
         }  
     });  
       
 //pass jQuery to the function,   
 //So that we will able to use any valid Javascript variable name   
 //to replace "$" SIGN. But, we'll stick to $ (I like dollar sign: ) )         
 })(jQuery);

