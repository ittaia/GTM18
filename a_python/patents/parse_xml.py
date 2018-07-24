import os
import logging
from patents.paths import *
from patents.patents_file import PatentFile
logging.basicConfig(filename=pathLog,level=logging.DEBUG)
path_download = patents_download
path_data = patents_data
files = os.listdir(path_download)
for name in files:
    file_name = os.path.join(path_download , name)
    file_out = os.path.join (path_data , name.replace('.xml' , '.json'))
    patent_file = PatentFile (file_name)
    patent_file.extract(file_out)