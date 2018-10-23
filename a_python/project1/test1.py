import os 
import logging
from project1.a_main import file_path
print(file_path.test_dir)
log_file = os.path.join(file_path.test_dir, 'log.txt')
print('*** log'+ log_file)
logging.basicConfig(filename=log_file, level=logging.DEBUG)
logging.info('start ') 
