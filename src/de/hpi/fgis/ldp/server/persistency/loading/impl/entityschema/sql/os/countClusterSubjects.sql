-- %1$s Constants.db2Schema
-- %2$s clusterID

SELECT count(DISTINCT CS.subject_id) 
	FROM CLUSTER_SUBJECTS CS
		WHERE CS.subject_id IS NOT NULL
		AND CS.CLUSTER_ID = %2$s