FROM openjdk:8 as ssl_jdk

USER root

RUN \
    mkdir -p /usr/local/project  && \
    cd /usr/local/project && \
    git clone --progress --verbose https://github.com/markhuang19994/log-parser-server.git && \
    chmod +x -R log-parser-server/src/main/resources/script

RUN \
    cd /usr/local/project/log-parser-server/docker/resources && \
    mv apache-maven-3.6.2-bin.tar.gz /tmp/apache-maven-3.6.2-bin.tar.gz && \
#    mv repository.tar.gz             /tmp/repository.tar.gz && \
    \
    tar xf /tmp/apache-maven-*.tar.gz -C /opt && \
    rm -rf /temp/apache-maven* && \
    ln -s /opt/apache-maven-3.6.2 /opt/maven
#    mkdir -p /root/.m2 && \
#    tar xf /tmp/repository.tar.gz -C /root/.m2 && \
#    rm -rf /temp/repository.tar.gz

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}

WORKDIR /usr/local/project/log-parser-server
CMD ["src/main/resources/script/start.sh"]
