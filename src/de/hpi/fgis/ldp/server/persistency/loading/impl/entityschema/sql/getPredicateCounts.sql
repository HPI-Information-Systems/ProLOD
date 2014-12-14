

SELECT count(*) 
FROM MAINTABLE M,CLUSTER_SUBJECTS C
 where M.predicate_id =  ?
 AND M.subject_id = C.subject_ID AND C.Cluster_ID = ?