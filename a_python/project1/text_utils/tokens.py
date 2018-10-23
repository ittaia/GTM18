import re
tokens_re = '[\\s][\\s]*'
word_re = '[A-Za-z][A-Za-z\\-]*'
stop_chars = {'.', ',', ';', '!', '?', '-'}
stop_words = '''a,able,about,across,after,all,almost,also,am,among,an,and,any,
                are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,
                ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,
                its,just,least,let,like,likely,may,me,might,most,must,my,neither,
                no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,
                says,she,should,since,so,some,than,that,the,their,them,then,there,
                these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,
                which,while,who,whom,why,will,with,would,yet,you,your'''
stop_word_set = set([])


def get_tokens(text):
    a_tokens = re.split(tokens_re, text)
    ret_tokens = []
    for token in a_tokens:
        clean_token = clean(token)
        if is_word(clean_token):
            ret_tokens.append(clean_token)
    return ret_tokens


def stop_word(token):
    if len(stop_word_set) == 0:
        for sw in stop_words.split(','):
            stop_word_set.add(sw)
    return token in stop_word_set


def clean(token):
    if len(token) == 0:
        rt = ''
    elif token[-1] in stop_chars:
        rt = token[:-1]
    else:
        rt = token
    return rt


def is_word(token):
    r1 = False
    if re.match(word_re, token):
        r1 = True
    return (r1 and not stop_word(token))
