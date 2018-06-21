import numpy as np 
import time 
len1 = 1200
mat = np.random.rand(len1,len1)
ti = 100 
tj = 1000
print ('start') 
print (str (len (mat [0])))
start = time.time()
m1 = np.matmul(mat,mat)
end = time.time()
print (end-start)
c = 0 
for k in range (0, len1):
    c += mat[ti][k]*mat[k][tj]
print (c) 
print (m1[ti][tj] )