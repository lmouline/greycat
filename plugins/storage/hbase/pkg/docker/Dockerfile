FROM lwieske/java-8:jdk-8u112-slim
MAINTAINER Fouquet Francois <fouquet.f@gmail.com>
RUN apk update && apk add ca-certificates && apk add wget && apk add bash && apk add openssh-client && update-ca-certificates && apk add openssl && rm -rf /var/cache/apk/*
ENV HBASE_VERSION=1.2.4
RUN adduser -D -u 1000 hbase
USER hbase
ENV HOME=/home/hbase
RUN cd /home/hbase && wget -O - -q http://www-eu.apache.org/dist/hbase/stable/hbase-${HBASE_VERSION}-bin.tar.gz | tar -xzvf - && cp -R hbase-${HBASE_VERSION}/* . && rm -Rf hbase-${HBASE_VERSION}
ADD ./conf/ /home/hbase/conf/
USER root
RUN chown -R hbase:hbase /home/hbase/conf
USER hbase
RUN mkdir /home/hbase/data
RUN mkdir /home/hbase/logs
VOLUME /home/hbase/data
VOLUME /home/hbase/logs

EXPOSE 60000

#EXPOSE 60010
#EXPOSE 60020
#EXPOSE 60030

WORKDIR /home/hbase

#CMD /home/hbase/bin/hbase master start
CMD /bin/bash