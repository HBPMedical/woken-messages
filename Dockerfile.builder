# Pull base image
FROM hbpmip/scala-base-build:6d7528a

MAINTAINER Arnaud Jutzeler <arnaud.jutzeler@chuv.ch>

# Override startup script
COPY build-in-docker.sh /
