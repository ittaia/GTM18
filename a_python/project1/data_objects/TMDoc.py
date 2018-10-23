from project1.text_utils import tokens
from project1.text_utils.term_list import TermList, ActiveTermList


class TMDoc:
    def __init__(self, doc_id: int, doc_name: str, header: str, text: str,
                 term_list: TermList):
        self.doc_id = doc_id
        self.doc_name = doc_name
        self.header = header
        self.all_word_array = []
        self.word_array = []
        all_tokens = tokens.get_tokens(header + ' ' + text)
        for t in all_tokens:
            self.all_word_array.append(term_list.add_term(t, doc_id))

    def init_active_word_array(self, term_list: TermList):
        for term_id in self.all_word_array:
            active_term_id = term_list.get_active_term_id(term_id)
            self.word_array.append(active_term_id)
