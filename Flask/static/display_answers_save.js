function display_answers(body,queries) {
    awindow = window.open("", "_blank",
            "toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=900,width=700,height=700");
    body = d3.select(awindow.document.body);
    query = '[';
    for (var t = 0; t < queries.length; t++) {
        query += '(' + queries[t].Id + ',' + queries[t].prob + ')'
        if (t < queries.length - 1) query += ',';
    }
    query += ']'
    url = '/getdocs' + '?query=' + query;
    d3.json(url, function (error, doc_list) {
        if (error) throw error;
        //alert ("  j1  "+numj  + "  j2  "+JSON.stringify(numj)) ; 
       tabulate (body , doc_list , queries) ; 
       awindow.focus() ; 
    });
}
function tabulate(body, doc_list , queries) {
    columns = ['name' , 'prob' , 'text']
    var table = body
        .append('table')
    var thead = table.append('thead')
    var tbody = table.append('tbody');  
    thead.append('tr')
        .selectAll('th')
        .data(columns).enter()
        .append('th')
        .text(function (column) { return column; });  
    var rows = tbody.selectAll('tr').data(doc_list).enter().append('tr');           
    var cells = rows.selectAll('td')
        .data (function (row) 
            { return columns.map(function (column)  {
                if (column == 'text')  { 
                    return { column: column, value: color_text (row['text'] , queries) };  
                }
                else { 
                    return { column: column, value: row[column]  };  
                }
            })
        })

        .enter().append('td').text(function (d) {
            return d.value;
        });
    return table;    
} 
function color_text(text , queries)  { 
    words = text.split(" ") ; 
    var rtext = [] 
    for (var i = 0 ; i < words.length ; i ++) { 
        word = words [i] ; 
        var word_query = -1 ; 
        var word_max_prob = 0 ; 
        for (var q = 0 ; q < queries.length ; q ++) { 
            query = queries[q] ; 
            for (var t = 0 ; t < query.terms.length ; t ++ ) { 
                if (word == query.terms[t].term) { 
                    if (query.terms[t].prob > word_max_prob) { 
                        word_query = q ; 
                        word_max_prob = query.terms[t].prob ; 
                        break ; 
                    } 
                }
            }
        }
        var word_text = {'word' : word , 'color' : -1} ;  
        if (word_query > -1) { 
            word_text.color =   queries[word_query].color ;   
        } 
        rtext.push(word_text);          
    }
    return rtext ; 
}  