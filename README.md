# ServiceNow-Parity
ServiceNow Parity built with Springboot, in memory H2 database, Thymeleaf templating system, Angular JS and run on Wildfly container

## How to Start Application
```
Start Wildfly
${wildfly_home}/bin/standalone.bat
or
${wildfly_home}/bin/standalone.sh

Using Maven deploy the application
mvn wildfly:deploy
```

## To Access App Navigate to
```
http://localhost:8080/
```
### Home Screen
![Alt text](https://github.com/fallen576/ServiceNow-Parity/blob/master/src/main/resources/static/css/record_view.PNG "Home Screen")

### Table View
![Alt text](https://github.com/fallen576/ServiceNow-Parity/blob/master/src/main/resources/static/css/example_modules.PNG "Table View")

### Record View
![Alt text](https://github.com/fallen576/ServiceNow-Parity/blob/master/src/main/resources/static/css/record_view.PNG "Record View")

### Create a new Table
![Alt text](https://github.com/fallen576/ServiceNow-Parity/blob/master/src/main/resources/static/css/create_table.PNG "Create Table View")

### Database access
The database is an in memory H2 DB and can be accesses by clicking the module H2 Console. Once there you can click connect without providing a password and you will be taken to the following screen:
![Alt text](https://github.com/fallen576/ServiceNow-Parity/blob/master/src/main/resources/static/css/db.PNG "DB View")

