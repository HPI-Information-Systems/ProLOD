-- Gets cluster general information
-- %1$s maintable partition
-- %2$s Cluster ID
-- %3$s user view

SELECT
    Label,
    AVG_ERROR,
    cluster_size,
    (SELECT count(*) FROM %1$s as m inner join Cluster_Subjects as c on (m.subject_id = c.subject_id) where c.cluster_id = %2$s),
    username
FROM Clusters
WHERE ID = %2$s
AND username = '%3$s'