-- publish the given partition
-- %1$s cluster id
-- %2$s user view

SELECT PARTITIONNAME FROM clusters WHERE id = %1$s AND username = '%2$s'
