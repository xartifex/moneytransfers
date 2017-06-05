# Java/Scala Test
Design and implement a RESTful API (including data model and the backing implementation) for money
transfers between internal users/accounts.
### Explicit requirements:
1. keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked
by another internal system/service)
2. use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement
\#1
3. the datastore should run in-memory for the sake of this test
4. the final result should be executable as a standalone program (should not require a pre-installed
container/server)
5. demonstrate with tests that the API works as expected
### Implicit requirements:
6. the code produced by you is expected to be of good quality.
7. there are no detailed requirements, use common sense.
Please put your work on github or bitbucket.

# Screenshots
see https://github.com/xartifex/moneytransfers/wiki

# Requirements 
1. Java 8
2. Maven 3
3. git

# Usage
1. git clone <this repo>
2. cd moneytransfers
3. mvn install
4. cd target
5. java -jar moneytransfers-1.0-SNAPSHOT.jar
6. open your favorite browser
7. go to http://localhost:8080/?raml=api.raml

ER=there will be UI for trying out MoneyTransfers API
