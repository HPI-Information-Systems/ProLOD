-- publish the given partition
-- %1$s list of cluster ids
-- %2$s cluster partition name
-- %3$s schema name
--
--additionally: 
--	AND (SELECT npages FROM SYSCAT.TABLES where tabschema = UPPER('%3$s') and tabname = UPPER('%2$s'))
--		<
--		(SELECT npages FROM SYSCAT.TABLES where tabschema = UPPER('%3$s') and tabname = UPPER(clusters.partitionname))

UPDATE clusters SET PARTITIONNAME = UPPER('%2$s') 
	WHERE id IN %1$s