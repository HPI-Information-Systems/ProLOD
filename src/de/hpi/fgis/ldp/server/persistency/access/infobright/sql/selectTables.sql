-- %1$s schema name

SELECT table_name FROM information_schema.tables where table_schema = '%1$s'