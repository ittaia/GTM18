from flask import Flask, jsonify, render_template, request

app = Flask(__name__)


@app.route('/load')
def load():
    path = 'C:\\Users\ittai\Flask\\data\\tree1.json'
    f = open(path, 'r')
    s = f.read()
    return s 
    #jsonify(s)


@app.route('/')
def index():
    path = 'C:\\Users\\ittai\\Flask\\templates\\pack.html' 
    f = open(path, 'r')
    s = f.read()
    return s
    

if __name__ == '__main__':
    app.run()