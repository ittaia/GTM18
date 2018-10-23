import logging
def set_logger(log_file):
    logging.basicConfig(filename=log_file, level=logging.DEBUG)
    logging.getLogger().addHandler(logging.StreamHandler())
    logger = logging.getLogger()