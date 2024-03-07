# Gym Tracker Server

A back-end web service for saving gym workouts to a PostgreSQL database.

## How to run

```
mvn spring-boot:run
```

### Run in local profile

```
 mvn spring-boot:run -Dspring-boot.run.profiles.local
```

## Setting up server on AWS EC2

### SSH into EC2 instance

```
ssh -i "./Downloads/gym-tracker-key-pair.pem" ec2-user@ec2-35-178-24-92.eu-west-2.compute.amazonaws.com
```

### Install Java 21

See what java 21 is called specifically

```
yum list | grep java-21
```

Install java headless

```
sudo yum install java-21-amazon-corretto-headless.x86_64
```

### Build server jar

```
mvn clean install
```

### Copy to server

From home directory

```
scp -i /home/josh/Downloads/gym-tracker-key-pair.pem ./gym-tracker-server/target/gym-tracker-server-0.0.1-SNAPSHOT.jar ec2-user@ec2-35-178-24-92.eu-west-2.compute.amazonaws.com:/home/ec2-user
```

### Run jar on server

```
java -jar gym-tracker-server-0.0.1-SNAPSHOT.jar
```

## Setting up postgres database locally

Postgres should just be running?

Switch to postgres user

```
sudo -i -u postgres
```

Start psql client

```
psql
```

Exit psql

```
\q
```

Create new database (must be as postgres user)

```
createdb db_name
```

Switch to new database

```
psql -d db_name
```

View connection info

```
\conninfo
```

### Logging into server

- Username - should be `postgres`
- Password - default is `postgres`

#### Set password

Start psql client

```
\password postgres
```

### pg_dump

From postgres user

```
pg_dump -h gym-tracker.ctvjp0tbn6qi.eu-west-2.rds.amazonaws.com -p 4323 -U postgres -Fc -b -v -f dump.sql
```

## Server test requests

To be entered with git bash (single quotes don't work with windows terminal)

```
curl -v -X POST localhost:8080/register -H 'Content-type:application/json' -d '{"email": "thing6@email.com", "password": "1234"}'
```

```
curl -X POST localhost:8080/login -H 'Content-type:application/json' -d '{"email": "something", "password": "asdasdaw"}'
```

```
curl -X POST localhost:8080/workouts -H 'Content-type:application/json' -d '{"date":"2023-12-23T22:42:14.330Z","duration":3600,"exercises":[{"name":"Squat","sets":[{"type":0,"weight":30,"reps":5},{"type":0,"weight":35,"reps":5},{"type":0,"weight":37.5,"reps":8},{"type":0,"weight":30,"reps":5}]},{"name":"Bench Press","sets":[{"type":0,"weight":25,"reps":5},{"type":0,"weight":27.5,"reps":5},{"type":0,"weight":32.5,"reps":8}]}]}'
```

```
curl -X GET localhost:8080/workout
```
