FROM java:openjdk-8u66-jdk

RUN apt-get update
RUN apt-get install -y zip
RUN apt-get install -y junit4
RUN apt-get install -y python
RUN apt-get install -y p7zip-full

RUN mkdir /shared
RUN mkdir /shared/submission

RUN mkdir /graders
RUN mkdir /graders/graph
RUN mkdir /graders/graph/grader
RUN mkdir /graders/data
RUN mkdir /graders/data/scc
RUN mkdir /graders/data/sccAnswers
RUN mkdir /graders/data/answers
RUN mkdir /graders/util

COPY executeGrader.sh /graders/executeGrader.sh
COPY graph/*.java graders/graph/
COPY graph/grader/*.java graders/graph/grader/
COPY util/*.java graders/util/
COPY data/scc/* graders/data/scc/
COPY data/sccAnswers/* graders/data/sccAnswers/
COPY data/answers/* graders/data/answers/
COPY data/*.txt graders/data/


COPY compile_error.py /graders/compile_error.py
COPY no_output.py /graders/no_output.py

RUN chmod a+rwx -R /grader/

ENTRYPOINT ["./grader/executeGrader.sh"]
