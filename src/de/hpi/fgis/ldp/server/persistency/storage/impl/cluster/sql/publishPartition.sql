-- publish the given partition
-- %1$s list of cluster ids
-- %2$s cluster partition name
-- %3$s schema name

UPDATE clusters SET PARTITIONNAME = '%2$s' WHERE id IN %1$s
