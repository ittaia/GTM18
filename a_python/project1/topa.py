from __future__ import print_function
import sys

import tensorflow as tf

print(sys.path)
print('tt')
hello = tf.constant('Hello, TensorFlow!')
print(555)

# Start tf session
sess = tf.Session()

# Run the op
print(sess.run(hello))
