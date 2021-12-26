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

### Example query

```
curl --request POST \
  --url http://localhost:8899/measurements \
  --header 'Content-Type: application/json' \
  --data '{"client_id":"ca4129ca-13d0-11e8-befd-0d1f3cdd831b", "timestamp":"1640535254000000000", "sensor_id":"600001", "measurement":"29.4933707255977", "voltage":"5.07447084177985", "signal_strength":"-67.6988342776428", "version":3, "checksum":"e2ee88491ec5eb1c714e19075ce309bd8157580b"}'
```

## Publish

```
docker push hkroger/measurinator-api:1.0
```

