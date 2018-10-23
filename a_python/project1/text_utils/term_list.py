import logging
from project1.a_main import file_path
import os
log_file = os.path.join(file_path.test_dir, 'log.txt')
logging.basicConfig(filename=log_file, level=logging.DEBUG)


class RowTerm:
    def __init__(self, text: str, term_id: int, doc_id: int):
        self.text = text
        self.term_id = term_id
        self.count = 1
        self.doc_count = 1
        self.last_doc_id = doc_id
        self.active_term_id = -1


class Term:
    def __init__(self, text: str, term_id: int, count: int, doc_count: int):
        self.text = text
        self.term_id = term_id
        self.count = count
        self.doc_count = doc_count


class TermList:
    def __init__(self):
        self.terms = []
        self.term_hash = {}
        self.tot_doc_count = 0
        self.last_doc_id = -1

    def add_term(self, text: str, doc_id: int) -> int:
        if doc_id != self.last_doc_id:
            self.tot_doc_count += 1
            self.last_doc_id = doc_id
        if text in self.term_hash:
            term_id = self.term_hash[text]
            term = self.terms[term_id]
            term.count += 1
            if term.last_doc_id != doc_id:
                term.doc_count += 1
                term.last_doc_id = doc_id
        else:
            term_id = len(self.terms)
            term = RowTerm(text, term_id, doc_id)
            self.terms.append(term)
            self.term_hash[text] = term_id
        return term.term_id

    def init_active_term_list(self, min_count: int, max_doc_frequency: float):
        active_term_list = ActiveTermList(self.tot_doc_count)
        for term in self.term_hash.values():
            if term.count >= min_count and term.doc_count / self.tot_doc_count <= max_doc_frequency:
                term.active_term_id = active_term_list.add_active_term(term)
                logging.info(' init active terms. docs:' + str(self.tot_doc_count) +
                             'terms: ' + str(len(self.term_hash)) +
                             ' active ' + str(len(active_term_list.term_hash)))
        return active_term_list

    def get_active_term_id(self, term_id: int) -> int:
        term = self.terms[term_id]
        return term.active_term_id

    def print_terms(self):
        for term in self.term_hash.values():
            logging.info('term: ' + term.text + ' id: ' + str(term.term_id) +
                         'count: ' + str(term.count) + 'doc count' + str(term.doc_count))


class ActiveTermList:
    def __init__(self, tot_doc_count: int):
        self.terms = []
        self.term_hash = {}
        self.tot_doc_coun = tot_doc_count

    def add_active_term(self, row_term):
        term_id = len(self.terms)
        term = Term(row_term.text, term_id, row_term.count, row_term.doc_count)
        self.terms.append(term)
        self.term_hash[term.text] = term
        return term.term_id

    def print_terms(self):
        for term in self.terms:
            logging.info('term: ' + term.text + ' id: ' + str(term.term_id) +
                         'count: ' + str(term.count) + 'doc count' + str(term.doc_count))
