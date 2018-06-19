import json

path = 'C:\\TestDir\\LDA\\model\\docsTopics.json'


class TopicsDocs:
    def __init__(self, topics_docs , docs_topics):
        self.topics_docs = topics_docs
        self.docs_topics = docs_topics
        self.num_of_docs = len(docs_topics)
        self.num_of_topics = len(topics_docs)

    def get_topic_docs(self, topic, prob):
        print (1)
        docs = self.topics_docs[topic]
        rdocs = []
        for d in docs:
            if d['prob'] < prob:
                break
            rdocs.append(d)
        return rdocs


with open(path, 'r') as f:
    rec = f.read()
    print(len(rec))
    dic = json.loads(rec)
    dt = dic['docsTopics']
    td = dic['topicsDocs']
    print(len(dt))
    print(len(td))
    c_topics_docs = TopicsDocs(td, dt)
    print(len(c_topics_docs.get_topic_docs(10, 0.5)))
