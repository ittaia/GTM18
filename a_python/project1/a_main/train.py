import logging
from project1.utils import ulog
from project1.a_main import file_path
from project1.text_utils import tokens
from project1.text_utils.term_list import TermList 
from project1.data_objects.TMDoc import TMDoc
import os
import json
print(file_path.test_dir)
log_file = os.path.join(file_path.test_dir, 'log.txt')
print('*** log '+ log_file)
ulog.set_logger(log_file)
logging.info('start ') 
def train1():
    docs = []
    term_list = TermList()
    with open(file_path.rest_docs, 'r') as f:
        rec_cnt = 0 
        for rec in f:
            rec_cnt += 1 
            if rec_cnt % 1000 == 0:
                logging.info(rec_cnt)
            rec_dic = json.loads(rec)
            doc_id = rec_cnt
            name = rec_dic['file_id']
            header  = rec_dic['title'] 
            text = rec_dic['text']
            doc = TMDoc(doc_id, name, header, text, term_list)
            docs.append(doc)

        active_term_list = term_list.init_active_term_list(5,0.2)
        for doc in docs:
            doc.init_active_word_array(term_list)
        active_term_list.print_terms()
                
    print (rec_cnt)
train1()