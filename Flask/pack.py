from flask import Flask, jsonify, render_template, request
from TopicsDocs import TopicsDocs
from Docs import Docs 
import ast
import json

app = Flask(__name__)
print ('load topic docs')
topics_docs = TopicsDocs()
print ('load docs') 
docs = Docs () 


@app.route('/load')
def load():
    path = 'C:\\TestDir\\LDARest\\model\\tree.json'
    f = open(path, 'r')
    s = f.read()
    return s 
    #jsonify(s)


@app.route('/')
def index():
    path = 'C:\\Users\\ittai\\Flask\\templates\\pack.html' 
    f = open(path, 'r')
    s = f.read()
    return s

@app.route('/getnum')
def getnum():
    print ('get num') 
    topic_id = int(request.args.get('topicid'))    
    pr  =  int ( request.args.get('prob')  ) 
    prob = pr/100 
    print (f'get num : topic: {topic_id} prob {prob}') 
    num_of_docs = len (topics_docs.get_topic_docs(topic_id, prob)) 
    s ='{"ndocs":'+str(num_of_docs)+'}'
    print (s) 
    return s

@app.route('/gettot')
def gettot():
    print ('get tot') 
    qr_str  = request.args.get('query')
    print (qr_str)
    topic_qr = ast.literal_eval (qr_str) 
    num_of_docs = topics_docs.get_query_docs_num(topic_qr) 
    s ='{"ndocs":'+str(num_of_docs)+'}'
    print (s) 
    return s  

@app.route('/getdocs')
def getdocs():
    print ('get docs') 
    qr_str  = request.args.get('query')
    print (qr_str)
    topic_qr = ast.literal_eval (qr_str) 
    doc_prob_list =  topics_docs.get_query_docs(topic_qr) 
    doc_list =  docs.get_docs (doc_prob_list)
    js = json.dumps (doc_list ,  default=lambda o: o.__dict__)
    return js     

if __name__ == '__main__':
    app.run()