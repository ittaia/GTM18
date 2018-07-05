import json
import copy 
from DocProb import DocProb 

path = 'C:\\TestDir\\LDA\\model\\docsTopics.json'

class TopicsDocs:    
    def __init__(self):
        with open(path, 'r') as f:
            rec = f.read()
            print(len(rec))
            dic = json.loads(rec)
            self.docs_topics = dic['docsTopics']
            self.topics_docs = dic['topicsDocs']
            self.num_of_docs = len(self.docs_topics)
            self.num_of_topics = len(self.topics_docs)
            print (f'load Topics Docs. Topics: {self.num_of_docs} . Docs {self.num_of_docs}')

    def get_topic_docs(self, topic, prob):
        docs = self.topics_docs[topic]
        rdocs = []
        for d in docs:
            if d['prob'] < prob:
                break
            rdocs.append(DocProb(d['indx'] , d['prob']))
        return rdocs

    def get_topic_docs_dic (self, topic, prob):
        docs = self.topics_docs[topic]
        rdic = {}
        for d in docs:
            if d['prob'] < prob:
                break
            rdic[d['indx']] = d['prob'] 
        return rdic
    
    def get_query_docs(self, query):
        
        docs_dic = self.get_query_docs_dic (query)
        print (len (docs_dic))
        rdocs = []
        for doc_id in docs_dic.keys():
            rdocs.append (DocProb (doc_id , docs_dic[doc_id]))          
        rdocs.sort (key = lambda x : -x.prob )
        print ('rdocs len '+ str (len (rdocs)) )
        return rdocs  
    
    def get_query_docs_dic(self, query):
        if len(query) <1:
            return {}
        topic_prob = query [0] 
        topic = topic_prob[0] 
        prob = topic_prob [1]/100 
        docs_dic = self.get_topic_docs_dic (topic , prob)
        print (len (docs_dic))
                   
        for i in range (1 , len(query)):
            new_dic = {}
            topic_prob = query [i]
            topic = topic_prob[0] 
            prob = topic_prob[1] / 100 
            docs_dic1 = self.get_topic_docs_dic (topic , prob)
            print (len(docs_dic1))
            for doc_id in docs_dic.keys():
                if doc_id in docs_dic1:
                    new_dic [doc_id] = docs_dic [doc_id] * docs_dic1 [doc_id]
            docs_dic = new_dic

        return docs_dic

    def get_query_docs_num(self, query):       
        return (len (self.get_query_docs_dic(query)))