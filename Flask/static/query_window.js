function query_window(queries) {
    if (qwindow == null) {
        qwindow = window.open("", "_blank",
            "toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=700,height=700");
    }
    var body = d3.select(qwindow.document.body);
    body.select('#totdiv').remove();
    body.select('#dsply').remove();
    var query_line = body.selectAll(".query")
        .data(queries)
        .enter()
        .append('div')
        .attr ('id' , function (d) {return 'div' + d.Id ; })
        .attr('class', 'query') ; 
    var del = query_line
        .append('button')
        .text('remove')
        .on('click', function (d) { remove_query(body , queries , d.Id); });
    var label = query_line
        .append('label')
        .attr("for", function (d) { return "x" + d.Id; })
        .style("display", "inline-block")
        .style("width", 240)
        .style("text-align", "left")
        .text(function (d) { return d.Id + " - " + d.header + "   : "; });
    var span = label
        .append("span")
        .attr("id", function (d) { return "x" + d.Id + "v"; })
        .style("color", "blue")
        .text(" **");

    var input = query_line
        .append("input")
        .attr("type", "range")
        .attr("id", function (d) { return "x" + d.Id; })
        .attr("min", 0)
        .attr("max", 100)
        .on("input", function (d) {
            update(body, d.Id, +this.value);
            clear_answers(body, d.Id);
            clear_tot(body);
        })
        .on("mouseup", function (d) {
            update(body, d.Id, +this.value);
            update_answers(body, d.Id, +this.value);
        });
    var h_answers = query_line
        .append("span")
        .style("color", "red")
        .text("   documents:");
    var answers = query_line
        .append("span")
        .attr("id", function (d) { return "x" + d.Id + "an"; })
        .style("color", "blue")
        .text(" **");
    var tot_answers = body
        .append('div')
        .attr("id", "totdiv")
        .append("span")
        .style("color", "red")
        .text("tot   documents:")
        .append("span")
        .attr("id", "xtot")
        .style("color", "blue")
        .text("--");
    var dsply =body
        .append ('div')
        .attr("id", "dsply")
        .append('button')
        .text('Display documents -->')
        .on('click', function () { display_answers(body,queries); });

     display_q_window(body , queries) ; 
}
function display_q_window (body , queries) {        
    for (var t = 0; t < queries.length; t++) {
        update(body, queries[t].Id, queries[t].prob);
        update_answers(body, queries[t].Id, queries[t].prob);
    }
    update_tot(body);
    qwindow.focus();
}
function update(body, val, p) {
    body.select("#x" + val + "v").text(p);
    body.select("#x" + val).property("value", p);
}
function clear_answers(body, val) {
    body.select("#x" + val + "an").text('**');
}
function update_answers(body, val, p) {
    url = '/getnum' + '?topicid=' + val + '&prob=' + p
    d3.json(url, function (error, numj) {
        if (error) throw error;
        //alert ("  j1  "+numj  + "  j2  "+JSON.stringify(numj)) ; 
        num_of_docs = numj.ndocs;
        body.select("#x" + val + "an").text(num_of_docs);
    });
    for (var t = 0; t < queries.length; t++) {
        if (queries[t].Id == val) queries[t].prob = p;
    }
    update_tot(body);
}
function update_tot(body) {
    query = '[';
    for (var t = 0; t < queries.length; t++) {
        query += '(' + queries[t].Id + ',' + queries[t].prob + ')'
        if (t < queries.length - 1) query += ',';
    }
    query += ']'
    url = '/gettot' + '?query=' + query;
    d3.json(url, function (error, numj) {
        if (error) throw error;
        //alert ("  j1  "+numj  + "  j2  "+JSON.stringify(numj)) ; 
        num_of_docs = numj.ndocs;
        body.select("#xtot").text(num_of_docs);
    });
}
function clear_tot(body) {
    body.select("#xtot").text('**');
}
function remove_query (body , queries , id) { 
    body.select ('#div'+id).remove() ; 
    for (var i = 0 ; i < queries.length ; i ++) { 
        if (queries[i].Id == id ) { 
            queries.splice (i,1) ; 
            break ; 
        }
    }
    display_q_window(body , queries) ; 
}