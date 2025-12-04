# Read Me First

This project was developed as a tutorial on how to build a Spring Boot RESTful web service.

For sample data the project uses musical artists and albums.

This project showcases:
* How to implement GET, POST, PUT and DELETE controller methods
* How to return the correct HTTP response codes
* How to test the GET, POST, PUT and DELETE controller methods
* The "Controller-Service-Repository" pattern
* The use of Lombok to reduce boilerplate code
* How to use an in-memory H2 database
* How to seed a database on startup
* The dependencies required to support REST in Spring Boot
* How to interact with controller methods using cURL

# Getting Started

1. Build and run tests
```shell
./gradlew clean build
```
2. Run locally
```shell
./gradlew bootRun
```

# Sample cURL Commands

1. Get all albums
```shell
curl localhost:8080/api/v1/albums
```

2. Get an album by id
```shell
curl localhost:8080/api/v1/albums/{id}
```

3. Get all albums by an artist
```shell
curl localhost:8080/api/v1/albums/artist/{name}
```

4. Create a new album
```shell
curl -X POST localhost:8080/api/v1/albums \
     -H "Content-Type: application/json" \
     -d '{ "artist": <name>, "name": <name>, "songCount": <n>, "yearReleased": <year> }'
```

5. Update an existing album
```shell
curl -X PUT localhost:8080/api/v1/albums/{id} \
     -H "Content-Type: application/json" \
     -d '{ "artist": <name>, "name": <name>, "songCount": <n>, "yearReleased": <year> }'
```

6. Delete an album by id
```shell
curl -X DELETE localhost:8080/api/v1/albums/{id}
```