import json
from patents.patent_data import PatentData
class PatentFile:
    def __init__(self,file_name):
        self.file_name = file_name
    def extract(self,file_out):
        print ('out '+ file_out)
        file_in = open (self.file_name,'r')
        file_out = open (file_out,'w')
        lines = 0
        docs = 0
        bad = 0
        doc_str = ''
        for line in file_in:
            lines += 1
            if lines % 1000000 == 0:
                print (lines)
            if line.find('?xml') > 0:
                if len (doc_str) > 0:
                    data = PatentData (doc_str)
                    if len(data.file_id) > 0:
                        c = json.dumps(data.__dict__)+'\n'
                        file_out.write(c)
                        docs += 1
                    else:
                        bad += 1
                    doc_str = ''
            doc_str += line
        if len(doc_str) > 0:
            data = PatentData(doc_str)
            if len(data.file_id) > 0:
                c = json.dumps(data.__dict__)
                file_out.write(c)
                docs += 1
            else:
                bad += 1
        file_in.close()
        file_out.close()
        print ('end' + file_out)
        print ('lines '+ lines)
        print ('docs '+ docs)
        print('bad '+ bad)