import xml.etree.ElementTree as ET
import logging
import json
logger = logging.getLogger('patents')
class PatentData:
    afile = 'file'
    abib = 'us-bibliographic-data-application'
    atitle = 'invention-title'
    aabstract = 'abstract'
    ap= 'p'
    def __init__(self,doc_str):
        try:
            root = ET.fromstring(doc_str)
            if PatentData.afile in root.attrib:
                self.file_id = root.attrib[PatentData.afile]
                self.title = root.find(PatentData.abib).find(PatentData.atitle).text
                self.text = root.find(PatentData.aabstract).find(PatentData.ap).text
            else:
                self.file_id = ''
                logger.info('no file --' + doc_str)
        except:
            self.file_id = ''
            logger.info('bad xml --' + doc_str)
    def out(self,out_file):
        print (out_file)
        print (self.file_id)
        print (self.title)
        print (self.abstract)
        c = json.dumps(self.__dict__)
        print (c)

