function terms2circle(terms, r) {
    const start_y = 0.65;
    const start_font = 0.4;
    const delta_start = 0.1;
    const space = 0.8
    const large_font_base = 180;
    const min_font = 15;
    const font_len = 0.5;
    const font_line = 0.8;
    const min_prob = 0.007;
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
        if (font > 0) terms[t].incircle = true;
        else terms[t].incircle = false;
    }
    var line = 0;
    var dy = r * start_y;
    var dx = Math.sqrt(r * r - dy * dy);

    var line_font = terms[0].font;
    var x = -dx + delta_start + start_font * line_font;
    console.log('Start line:' + line + ' - ' + t + ' - ' + terms[0].term + ' - ' + ' - ' + x + ' - ' + dy);
    for (var t = 0; t < terms.length; t++) {
        if (terms[t].font > 0) {
            var next_x = x + terms[t].term.length * terms[t].font * font_len;
            if (next_x > dx) {
                line += 1;
                dy = dy - line_font * font_line;
                if (Math.abs(dy) < r * start_y) {
                    dx = Math.sqrt(r * r - dy * dy);
                }
                else {
                    dx = 0;
                    terms[t].incircle = false;
                }
                line_font = terms[t].font;
                x = -dx + delta_start + start_font * line_font;
                console.log('Start line:' + line + ' - ' + t + ' - ' + terms[t].term + ' - ' + ' - ' + x + ' - ' + dy);
            }
        }
        terms[t].y = dy;
        terms[t].x = x;
        x = x + (terms[t].term.length+space) * terms[t].font * font_len ; 
    }
}