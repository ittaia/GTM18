import ast 
from TopicsDocs import TopicsDocs 
from Docs import Docs
print ('load ')
topics_docs = TopicsDocs()
docs = Docs ()
s = '[(105,10) , (106 , 10)]'
qr = ast.literal_eval (s) 
print (qr) 
n = topics_docs.get_query_docs(qr)
print (n)