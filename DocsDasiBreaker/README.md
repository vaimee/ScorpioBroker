# Setup
Using Ubuntu 21.04 (always use “sudo” for run eclipse too)
Start 3 require services through docker 
File docker: https://github.com/vaimee/ScorpioBroker/blob/dasi-breaker-main/DocsDasiBreaker/docker-compose-aaio-no-skorpio.yml
docker-compose -f docker-compose-aaio-no-skorpio.yml up 
change host and service names on application.yml for packages:
AllInOne:
dest: ./AllInOneRunner/src/main/resources/application.yml
src: ./DocsDasiBreaker/ymls/allinone.application.yml
Gateway
dest:  ./SpringCloudModules/gateway/src/main/resources/application.yml
src: ./DocsDasiBreaker/ymls/agateway.application.yml
After started the 3 service on docker, start eclipse as sudoer
“Maven Update”  on the root project (it will take some mins)
On root dir open terminal and “sudo mvn clean package -DskipTests” 
re-update eclipse with “Maven Update”  on the root project
Start and wait for each of the following sub-project on eclipse (following the order):
Eureka-Server: ./SpringCloudModules/eureka/src/main/java/eu/neclab/ngsildbroker/eurekaserver/EurekaServerApplication.java
Config-Server: ./SpringCloudModules/config-server/src/main/java/eu/neclab/ngsildbroker/configserver/ConfigServerApplication.java
AllInOne: ./home/tsg/Documents/ScorpioBrokerFork/AllInOneRunner/src/main/java/eu/neclab/ngsildbroker/runner/Runner.java
Gateway: ./SpringCloudModules/gateway/src/main/java/eu/neclab/ngsildbroker/gateway/GatewayApplication.java

# Build
register github credential at /home/user/.m2/settings.xml, to allow maven to access at github repositories.

sudo JAVA_HOME=/path/jdk-17.0.1/ mvn clean install --settings /home/user/.m2/settings.xml -DskipTests

sudo JAVA_HOME=/path/jdk-17.0.1/ mvn clean package --settings /home/user/.m2/settings.xml -DskipTests -DskipDefault -Pdocker-aaio

# Environment variables

SEPA_HOST		=	"localhost"
SEPA_PORT		=	8000
SEPA_SCHEME		=	"http"
SEPA_TIME_OUT		=	60000
SEPA_HTTP_METHOD_QUERY	=	"post" || "encoded post" || "get"
SEPA_HTTP_METHOD_UPDATE	=	"post" || "encoded post"
SEPA_PATH_QUERY		=	"/query"
SEPA_PATH_UPDATE	=	"/update"


