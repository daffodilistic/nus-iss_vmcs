# VMCS application for ISS_BG_2021_11 module
This repository contains the source code for the Bridging Module Project for the MTech Software Engineering

## Backend Project Setup
1. Oracle JDK 17.0.1 (aka Java 17)
2. Apache Maven 3.8.3
3. Helidon CLI: https://helidon.io/docs/v2/#/about/05_cli
4. Docker Desktop (for PostgreSQL image)
### Starting Helidon Development
1. Run `docker-compose -f docker-compose.yml up -d` in the root folder
2. Run `helidon dev` in the `vmcs` subfolder 
### Deployment
TODO

### Running Tests
Just type `mvn test` in the `vmcs` subfolder

## Frontend Project Setup
1. NodeJS v14 or later
2. Buefy: https://buefy.org/documentation
3. VueJS: https://vuejs.org/v2/guide/

### Starting Frontend Development
Run `npx live-server` in the `frontend` folder
