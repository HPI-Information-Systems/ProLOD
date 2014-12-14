-- Creates a new cluster
-- ? 1 session id
-- ? 2 cluster index
-- ? 3 cluster size
-- ? 4 child session
-- ? 5 avg error

INSERT INTO CLUSTERS 
	(SESSION_ID, SESSION_LOCAL_ID, CLUSTER_SIZE, CHILD_SESSION, AVG_ERROR, USERNAME)  
	VALUES (?, ?, ?, ?, ?, ?)