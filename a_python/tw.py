from flask import Flask, jsonify, render_template, request

class s:
    def __init__(self):
        self.count = 0
    def add(self):
        self.count += 1 

app = Flask(__name__)
cnt = s()


@app.route('/gethtml')
def gethtml():
    path = 'C:\\Users\ittai\Flask\\templates\m.html' 
    f = open(path, 'r')
    s = f.read()
    return s


@app.route('/')
def index():
    return render_template('m.html')

@app.route('/load')
def load():
    cnt.add()
    json = '{"cnt":'+str(cnt.count)+'}'+'\n'
    path = 'C:\\Users\ittai\Flask\\count.json'
    f = open(path, 'r')
    s = f.read()
    print (s)
    return str (s)

if __name__ == '__main__':
    app.run()