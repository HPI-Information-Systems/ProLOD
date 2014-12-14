-- ?1 sessionID
-- ?2 user view

SELECT parent.ID, parent.SESSION_LOCAL_ID, parent.LABEL, parent.CHILD_SESSION, parent.AVG_ERROR, count(children.subject_id) 
	FROM CLUSTERS parent, CLUSTER_SUBJECTS children
		WHERE parent.SESSION_ID = ? 
		AND parent.USERNAME = ?
		AND children.CLUSTER_ID = parent.ID
			GROUP BY parent.ID, parent.SESSION_LOCAL_ID, parent.LABEL, parent.CHILD_SESSION, parent.AVG_ERROR
				ORDER BY SESSION_LOCAL_ID ASC