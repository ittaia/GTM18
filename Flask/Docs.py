import json
import os
import pathlib 
 

path_headers = 'C:\\TestDir\\LDA\\model\\docheaders.json'
path_docs = 'C:\\TestDir\\LDA\\data'

class Doc: 
    def __init__(self,dic):
        self.id = dic ['docId'] 
        self.name =dic['docName']
        self.header = dic['header']
        self.prob = 0 
    def setText (self,   text): 
        header = '' 
        if self.header is not None:
            header = self.header 
        self.text = header + " " + text ; 


class Docs:
    def __init__(self):
        self.id2doc = {} 
        self.name2doc = {}        
        with open(path_headers, 'r' , encoding="utf-8") as f:            
            for  rec in f.readlines(): 
                rdic = json.loads(rec)            
                doc = Doc (rdic)
                self.id2doc[doc.id] = doc.name
                self.name2doc [doc.name] = doc
        print ('docs '+ str (len(self.id2doc)))
        for docs_file in os.listdir(path_docs): 
            docs_file_path = pathlib.Path (path_docs , docs_file)
            with open(docs_file_path, 'r') as f1:
                for rec in  f1.readlines():
                    rdic = json.loads(rec)
                    name = rdic ['file_id'] 
                    if not name in self.name2doc: 
                        print ('key not found ' + name) 
                    else: 
                        doc = self.name2doc [name] 
                        title = rdic['title']
                        if not title.lower() == doc.header:
                            print ('not match '+ title + " - " + doc.header )
                        text = '' 
                        if rdic['text']:
                            text = rdic['text']
                        doc.setText (text) 

    def get_docs (self,doc_prob_list):
        doc_list = []
        for doc_prob in doc_prob_list:
            doc_name = self.id2doc[doc_prob.indx]  
            doc = self.name2doc [doc_name]
            doc.prob = doc_prob.prob  
            doc_list.append (doc) 
        return doc_list 