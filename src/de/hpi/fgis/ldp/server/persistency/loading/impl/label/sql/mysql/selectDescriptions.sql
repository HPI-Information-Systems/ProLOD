-- %1$s sessionID
-- %2$s max. number of rows to fetch
-- %3$s user view

SELECT CS.CLUSTER_ID, o.object AS TEXT
	FROM CLUSTER_SUBJECTS CS, TEXTDATA TD, CLUSTERS C, objecttable o 
		WHERE C.SESSION_ID = %1$s 
		AND C.USERNAME = '%3$s' 
		AND C.ID= CS.CLUSTER_ID 
		AND CS.subject_id = TD.subject_id
		AND o.tuple_id = TD.tuple_id
			ORDER BY RAND() LIMIT %2$s