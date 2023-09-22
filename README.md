# job-app

Done:
*added entities (jobrequest, joblisting)
*added liquibase
*added services with crud (jobrequest, joblisting)
*added controllers (jobrequest, joblisting)
*added kafka through windows (didnt work)
*added kafka in docker container
*tested simple kafka roundtrip 
*tested pojo roundtrip kafka 
*wired joblisting with kafka
*add security
*Add user entity service and controller
*wire users with security
*kafka integration with users
*kafka integration with jobrequest
*rest tested all functionality
*create eureka server 
*config eureka server 
*create auth-server
*move security to auth-server microservice
*create zuul 


TODO:
*config zuul
*wire access from auth-server to user table 
*add proxy
*make all requests authentificate when going through proxy
*create messaging-service
*wire it with kafka
*write tests