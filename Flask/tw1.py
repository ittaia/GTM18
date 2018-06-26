from flask import Flask, jsonify, render_template, request

class s:
    def __init__(self):
        self.count = 0
    def add(self):
        self.count += 1 

app = Flask(__name__)
cnt = s()



@app.route('/')
def index():
    return render_template('m.html')

@app.route('/load')
def load():
    cnt.add()
    s = '{"cnt":'+str(cnt.count)+'}'
    print (s)
    return s

if __name__ == '__main__':
    app.run()