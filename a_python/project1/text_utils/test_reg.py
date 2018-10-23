from project1.a_main import file_path
import re
word_re = '[A-Za-z][A-Za-z\\-]*'
word_re9 = '[A-Za-z\\-]*'
word_re1 = '[-]*'
word_re0 = '[-]'
m = re.fullmatch(word_re, 'aZ---')
print(m)
