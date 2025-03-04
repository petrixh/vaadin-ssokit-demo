FROM eclipse-temurin:17-jdk

ENV DEBIAN_FRONTEND noninteractive

# Get the basic stuff
RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get install -y \
    sudo git docker.io && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# (Optional) Add the ubuntu user to the docker group so that it can access the Docker socket
RUN sudo groupadd docker || true && sudo usermod -aG docker ubuntu

# Set as default user
USER ubuntu
WORKDIR /home/ubuntu

ENV DEBIAN_FRONTEND teletype

CMD ["/bin/bash"]