#--------------------------
# Pull the base linux distro as the image base
FROM tomcat:alpine
# Maintainer is you - add your name and email
MAINTAINER "Your name here <yourName@whatever.com>"
# Remove the default Tomcat landing page
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# Copy the actual app, renamed as ROOT
ADD ROOT.war /usr/local/tomcat/webapps/
