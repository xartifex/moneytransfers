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

8. in order to stop the server just press _Ctrl+C_ in the console window where step 5 was performed

# Performance testing
after step 6 of _Usage_ section execute the following:
mvn jmeter:jmeter

ER=there will be two main things to observe
* summary in console like this
```
 [INFO] summary +    289 in 00:00:06 =   50,3/s Avg:  1261 Min:   373 Max:  2239 Err:     0 (0,00%) Active: 100 Started: 100 Finished: 0
 [INFO] summary +   2812 in 00:00:30 =   93,7/s Avg:  1058 Min:     7 Max:  5441 Err:     0 (0,00%) Active: 100 Started: 100 Finished: 0
 [INFO] summary =   3101 in 00:00:36 =   86,7/s Avg:  1077 Min:     7 Max:  5441 Err:     0 (0,00%)
 [INFO] summary +   2461 in 00:00:27 =   91,1/s Avg:  1113 Min:     5 Max:  6391 Err:     0 (0,00%) Active: 0 Started: 100 Finished: 100
 [INFO] summary =   5562 in 00:01:03 =   88,6/s Avg:  1093 Min:     5 Max:  6391 Err:     0 (0,00%)
```
* result file target/jmeter/results/jmeterTests.jtl, which can be opened by JMeter or used by special plugins to plot graphs:
see _jmeter graph: response time_ graph on https://github.com/xartifex/moneytransfers/wiki as an example of what can be generated from jtl

The test is configured to run for ~1 minute with around ~100 requests per second. 
You can open [jmeterTests.jmx](src/test/jmeter/jmeterTests.jmx) in JMeter for detailed overview and configuration of performance tests.
 
