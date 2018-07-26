function terms2rect(terms, height, width) {
    console.log('Width ' + width);
    if (terms.length < 1) return ([])
    const start_y = 0.80;
    const start_font = 0.4;
    const delta_start = 0;
    const large_font_base = 100;
    const min_font = 15;
    const font_len = 0.6;
    const font_line = 0.8;
    const min_prob = 0.008;
    var large_font = large_font_base;
    for (var t = 0; t < terms.length; t++) {
        var p = terms[t].prob
        var font = 0;
        if (p > min_prob) {
            font = large_font * p;
            if (font < min_font) font = min_font;
        }
        terms[t].font = font;
        terms[t].len = terms[t].term.length * font * font_len
        if (font > 0) terms[t].inrect = true;
        else terms[t].inrect = false;
    }
    var line_font = terms[0].font;
    var line = 0;
    var dy = start_y + line_font * font_line;
    var x = 0;


    var x = x + delta_start + start_font * line_font;
    var inrect = true 

    for (var t = 0; t < terms.length; t++) {
        if (terms[t].font > 0) {
            var next_x = x + terms[t].len
            if (next_x > width) {
                console.log('break line:' + line + ' - ' + t + ' - ' + terms[t].term +
                    ' - ' + ' X- ' + x + ' Next- ' + next_x);
                line += 1;
                x = 0;
                dy = dy + line_font * font_line;
                line_font = terms[t].font;
                if (dy + line_font * font_line > height) {
                    inrect = false 
                }
               
                x = x + delta_start + start_font * line_font;
                // console.log ('Start line:' + line + ' - '+  t + ' - '+ terms[t].term + 
                //' - ' + ' - '+ x + ' - ' + dy ) ;  
            }
        }
        terms[t].y = dy;
        terms[t].x = x;
        x = x + terms[t].len
        terms[t].inrect = inrect 
    }
}