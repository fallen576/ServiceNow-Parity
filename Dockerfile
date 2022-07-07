# Use latest jboss/base-jdk:11 image as the base
FROM jboss/base-jdk:11

# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 24.0.0.Final
ENV WILDFLY_SHA1 391346c9ed2772647ff07aeae39deb838ee11dcf
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

# Add the WildFly distribution to /opt, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place.
RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

# Expose the ports in which we're interested
EXPOSE 8080
EXPOSE 9990
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent
# Set the default command to run on boot
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

#USER root

#RUN mkdir -p /app/snp/

#COPY . /app/snp/

#RUN chown -R jboss /app/snp/

#USER jboss

#CMD ["jar", "-cf", "SNP.war", "/app/snp/src/*"]
#RUN jar -cf SNP.war /app/snp/src/*
#CMD ["cp", "SNP.war", "/opt/jboss/wildfly/standalone/deployments/"]

# Copy war to deployments folder
COPY target/SNP.war $JBOSS_HOME/standalone/deployments/

# Modify owners war
#RUN chown jboss:jboss $JBOSS_HOME/standalone/deployments/SNP.war