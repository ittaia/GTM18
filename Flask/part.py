from flask import Flask, jsonify, render_template, request

app = Flask(__name__)


@app.route('/load')
def load():
    print ('kuku') 
    path = 'C:\\TestDir\\LDARest\\model\\treeh.json'
    f = open(path, 'r')
    s = f.read()
    return s 
    #jsonify(s)


@app.route('/')
def index():
    path = 'C:\\Users\ittai\Flask\\templates\part.html' 
    f = open(path, 'r')
    s = f.read()
    return s
    

if __name__ == '__main__':
    app.run()