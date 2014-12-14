-- renames the cluster
-- ? 1 cluster label
-- ? 2 cluster id
-- ? 3 user view

UPDATE CLUSTERS SET LABEL = ? WHERE ID = ? AND USERNAME = ?