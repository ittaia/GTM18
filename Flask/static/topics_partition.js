function topics_partition (tree,width,basic_height) { 
    const line_height = 40 ; 
    const col_width = 400 ; 
    const level_width  = [ 0 , 1 ,2] ; 
    const start_x = 1 ;
    const start_y = 1 ; 
    const padding = 1 ; 
    function set_x_y (d , s_x   , s_y) { 
        d.x0 = s_x + padding ; 
        d.y0 =1 * s_y ; 
        if (d.active) { 
            d.y1 = d.y0 + col_width * level_width[d.depth] ;   
            d.x1 = d.x0 + line_height  ;           
        }
        else { 
            d.y1 = 1* d.y0 ; 
            d.x1 = 1 * d.x0 ; 
        }
        xd =  s_x ; 
        //console.log (d.depth + '- '+ yd) ; 
        if (d.children) { 
            for (var dd of d.children) { 
                xd += set_x_y (dd , xd , d.y1) ; 
            }
        }
        if (d.x1 < xd) d.x1 = 1 * xd ; 
        return (d.x1 - s_x) ; 
    } 
    set_x_y (tree , start_x , start_y) ; 
}