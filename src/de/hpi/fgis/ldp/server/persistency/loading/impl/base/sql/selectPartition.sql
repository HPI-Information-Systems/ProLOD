-- %1$s cluster id
-- %2$s username

SELECT partitionname FROM clusters WHERE id = %1$s AND username = '%2$s' 