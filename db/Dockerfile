FROM postgres:9.6.6
MAINTAINER sourcekick@gmail.com

COPY ./migrations/*.sql /docker-entrypoint-initdb.d/

RUN chmod o+x /docker-entrypoint-initdb.d/ \
    && chmod o+r /docker-entrypoint-initdb.d/*
