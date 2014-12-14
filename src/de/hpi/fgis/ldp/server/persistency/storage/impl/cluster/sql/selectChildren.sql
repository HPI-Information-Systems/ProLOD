-- select all children clusters of a cluster
-- %1$s list of parent cluster ids
-- %2$s user view

SELECT child.id 
	FROM clusters parent, clusters child 
		WHERE parent.child_session = child.session_id 
		AND parent.username = '%2$s'
		AND child.username = '%2$s'
		AND parent.id in %1$s