-- finishes mean schema entry creation for a cluster
-- ? 1 cluster id
-- ? 2 predicate id
-- ? 3 rank


INSERT INTO cluster_meanschema_writable (cluster_id, predicate_id, RANK) VALUES (?, ?, ?)