FROM openjdk:18.0.1.1-jdk

ADD scripts/wait-for-it.sh /app/
ADD build/libs/app.jar /app/





