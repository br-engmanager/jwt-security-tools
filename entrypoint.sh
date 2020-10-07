FROM java:8
ENV ENVIRONMENT PRO
ENV URL_PARAM https://sgl.paas.santanderbr.corp/apiparametros/parametroController
VOLUME /tmp
ADD target/srv-importa-metricas.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]