TODO
----

* use LWT for min/max saving or store multiple min/max values in separate tables and figure a way to query that efficiently
    * Current implementation might fail if data is not available because of Cassandra being not in sync
* Daily avg/min/max use wrong date format.
    * Maybe even create a new table with proper format
* 
