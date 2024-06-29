SET insert_distributed_sync=1;
SET insert_quorum=2;
SET select_sequential_consistency=1;

select name,value from system.settings where name='insert_distributed_sync';
select name,value from system.settings where name='insert_quorum';
select name,value from system.settings where name='select_sequential_consistency';