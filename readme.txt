The service accepts two types of requests:
1. Save payments using following POST-request:  http://localhost:8080/payments/save
    NOTE: request body should contain an array of payments (see an example in save_request_example.json)
2. Get total amount of expenses for the particular sender using following GET-request:  http://localhost:8080/senders/{sender_id}

To run the service locally 3 databases (PostgreSQL) should be created on localhost:5432 firstly. Please run the script from database_creation.sql to create databases and then run the script from database_objects_creation.sql for each created database to create required sequence and table.