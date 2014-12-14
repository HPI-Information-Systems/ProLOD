-- updates the child session of a cluster
-- %1$s cluster id
-- %2$s session id
-- %3$s user view


UPDATE CLUSTERS 
	SET CHILD_SESSION = %2$s 
		WHERE ID = %1$s
		AND USERNAME = '%3$s'