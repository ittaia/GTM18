class A:
    def __init__(self,text): 
        self.t = text
text = 'XYZ'
a = A(text)
print (a.t)
text = '***'
print (a.t) 