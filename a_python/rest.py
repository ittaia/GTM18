from pathlib import PurePath
from JsonDoc import JsonDoc 
import csv
import re
import logging
logging.basicConfig(filename='C:\\TestDir\\log.txt',level=logging.DEBUG)
logger = logging.getLogger('a')
class Review:
    def __init__(self,row,key0):
        self.rest_key = row['gid']+'-'+row['did']
        self.doc_key = row[key0]
        self.url = row['url']
        
        self.date = row['review_date']
        self.header = row['review_header']
        self.text = row['review_text']
class RestReviews:
    def __init__(self,review):
        self.rest_key = review.rest_key 
        self.error = 0 
        self.rest_name = '** unk'
        self.rest_name = self.get_name(review.url)                   
        self.reviews = [review]
        self.text_len = len (review.header+review.text)
    def add_review(self,review):
        name = self.get_name (review.url)
        if not self.rest_name == name: 
            logger.info ('name not match : '+ self.rest_name + '- '+ name) 
        self.reviews.append(review)
        self.text_len += len (review.header+review.text)
    def get_name(self,url):
        name = '' 
        try: 
            if url.find ('/Restaurant_Review') >= 0: 
                name = url.split('-Reviews-')[1].replace ('.html','')
            elif url.find ('/ShowUserReviews') >= 0: 
                name = re.split ('-r\\d*-',url)[1].replace ('.html','')
            else:
                logger.info ('un known format  '+ review.url)        
        except Exception as ex: 
             logger.info ('bad url  '+ review.url)  
             logger.info (ex)  
             self.error = 1 
        return name   
    def get_docs(self):
        r_text = []
        sorted(self.reviews, key=lambda review:review.date, reverse=True )  
        text = ''         
        for i in range (0 , len(self.reviews)):
            review = self.reviews[i]
            text1 = review.header + ' ' + review.text 
            if len (text) > 0 and len(text) + len(text1) > 800: 
                file_id= self.rest_key+'-'+review.date
                doc =  JsonDoc (file_id , '' , text)
                text = ''
                r_text.append (doc.get_json())
            text += ' ' + text1 
        if len(text) > 10:
            file_id= self.rest_key+'-'+review.date
            doc =  JsonDoc (file_id , '' , text)
            r_text.append (doc.get_json())
        return r_text       

file = 'tripadvisor_reviews_full_results_combined_chicago.csv'
path = 'C:\\Corpuses\\Rest'
rest_dic = {}
key0 = '' 
file_path = PurePath(path , file)
logger.info (file_path)
with open (file_path , 'r', encoding='utf-8') as f:
    csvr = csv.DictReader (f , delimiter=',', quotechar='"')
    c = 0 
    for row in csvr:
        c += 1 
        if c==1:
            for key in row.keys():
                if key.find('chd_id'):
                    key0 = key
                    break
        review = Review(row,key0)        
        if review.rest_key not in rest_dic:
            rest_review = RestReviews (review)
            rest_dic[review.rest_key] = rest_review 
            if rest_review.error > 0: 
                break 
        else:
            rest_dic[review.rest_key].add_review (review)  
logger.info ('num of rows: ' + str (c))   
logger.info ('num of rest:' + str (len (rest_dic)))
out_path = PurePath (path , 'rest.json')
out_docs = 0 
with open(out_path , 'w') as out:
    for rest in rest_dic.values():
        for json_doc in rest.get_docs(): 
            out.write(json_doc+'\n') 
            out_docs += 1 
logger.info (f'out docs {out_docs}')    