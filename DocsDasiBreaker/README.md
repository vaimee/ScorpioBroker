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


# working paths
sub-project: commons
path: eu.neclab.ngsildbroker.commons.storage.dasibreaker
(deprecated)

sub-project: core
path: eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker
(working)


#the idea

![alt text](https://github.com/vaimee/ScorpioBroker/blob/dasi-breaker-main/DocsDasiBreaker/uml.jpg?raw=true)

