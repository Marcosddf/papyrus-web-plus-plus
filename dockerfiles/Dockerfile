#############################################################################
#  Copyright (c) 2025 CEA LIST.
#  
#  This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License 2.0
#  which accompanies this distribution, and is available at
#  https://www.eclipse.org/legal/epl-2.0/
# 
#  SPDX-License-Identifier: EPL-2.0
#  
#  Contributors:
#   Marcos Didonet Del Fabro (CEA LIST) - Initial implementation
#
#############################################################################

# Build instructions. Under the root folder
# docker build --build-arg USERNAME=${GIT_USERNAME} PASSWORD=${GIT_AUTH_TOKEN} -t papyrusweb ..
# Launch instructions. Note that it is necessary to have a running postgres image or local database
# docker run --name=papyrusweb-container -p 8080:8080 papyrusweb:latest

# Image for full compilation

FROM node:22.16 AS papyrusweb-build

ADD . /build
WORKDIR /build/

ARG USERNAME
ARG PASSWORD

# Compile the frontend in one layer

RUN echo '//npm.pkg.github.com/:_authToken=${PASSWORD}' >> /build/frontend/.npmrc &&\
  cd frontend && npm install && npm ci && npm run build
RUN mkdir -p backend/papyrus-web-frontend/src/main/resources/static &&\
  cp -R frontend/papyrus-web/dist/* backend/papyrus-web-frontend/src/main/resources/static

# Update packages and compile the backend in another layer

RUN apt-get update &&\
  apt-get install openjdk-17-jdk -y &&\
  apt-get install maven -y &&\
  mvn clean verify -DskipTests -Dcheckstyle.skip -f backend/pom.xml --settings settings.xml
 
RUN export WEBAPP_VERSION=$(grep version frontend/package.json | awk -F \" '{print $4}') &&\
    cp backend/papyrus-web/target/papyrus-web-${WEBAPP_VERSION}.jar backend/papyrus-web/target/papyrus-web.jar 

# Image to run Papyrus Web

FROM eclipse-temurin:17-jre AS papyrus-web

COPY --from=papyrusweb-build /build/backend/papyrus-web/target/papyrus-web.jar /papyrus-web.jar

RUN ls -la /

EXPOSE 8080

ENTRYPOINT ["java","-jar","/papyrus-web.jar"]

