-- renames the cluster
-- ? 1 cluster label
-- ? 2 cluster id
-- ? 3 user view

UPDATE CLUSTERS_WRITABLE SET LABEL = ? WHERE ID = ? AND USERNAME = ?