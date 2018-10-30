function terms2ciorcle (terms , r , cx , cy) { 
 const start_y = 0.9 ;  
 for (var t = 0 ; t < terms.length ; t ++ ) {         
    var p = terms[t].prob 
    if (p > 0.04) terms[t].font = 300 * p  ; 
    else terms[t].font = 12 ; 
    terms[t].len = terms[t].term.length * terms[t].font/1.6
 }
 var line = 0 ;  
 var dy = r*start_y ; 
 var y = cy+dy 
 var dx = Math.sqrt (r*r - dy*dy) ;
 var x = cx-dx ; 
 var max_line_font = terms[0].font 
 for (var t = 0 ; t < terms.length ; t ++ ) {         
    if (x + terms[t].len > cx+dx) { 
        dy = dy - max_line_font ; 
        y = cy + dy ; 
        dx = Math.sqrt (r*r - dy*dy) ;
        x = cx-dx ; 
    }        
    terms[t].y = y ; 
    terms[t].x = x ; 
    x = x +  terms[t].term.length * terms[t].font/1.6   
 }
 }  