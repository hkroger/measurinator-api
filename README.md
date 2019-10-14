# TODO


* use LWT for min/max saving or store multiple min/max values in separate tables and figure a way to query that efficiently
    * Current implementation might fail if data is not available because of Cassandra being not in sync
* Daily avg/min/max use wrong date format.
    * Maybe even create a new table with proper format
* 

# How to build and deploy

## Build

```
sbt docker:publishLocal
```

## Run

```
docker run -e CASSANDRA_HOSTS=192.168.1.105 -p 8899:8899 hkroger/measurinator-api:1.0
```


## Publish

```
docker push hkroger/measurinator-api:1.0
```

