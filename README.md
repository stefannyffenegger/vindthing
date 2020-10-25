# VindThing
Finds your things!

## Getting started
#### MongoDB
- Install MongoDB
- Create DB "vindthing"
- Create collection "roles"
- Add roles to collection
```
[
   { "name": "ROLE_USER" },
   { "name": "ROLE_MODERATOR" },
   { "name": "ROLE_ADMIN" },
]
```

#### Backend
- Run Spring Boot Application (vindthing)

#### Frontend
- Run nuxt.js (vindthing-ui):
- In PowerShell ```cd``` to project folder
- Run with ```npm run dev```
- Website available at http://localhost:3000

## Run Spring Boot application
```
mvn spring-boot:run
```
