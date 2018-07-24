import json
class JsonDoc:
    def __init__(self,file_id,title,text):
        self.file_id = file_id 
        self.title = title 
        self.text = text 
    def get_json(self):
        c = json.dumps(self.__dict__) 
        return c