drop database mutation_processor;

CREATE DATABASE mutation_processor;


truncate table mutation_devices;
truncate table mutation_users;
truncate table processor_worker_members;
truncate table processor_checkpoint_head;
truncate table processor_checkpoints;
truncate table processor_partition_leases;
