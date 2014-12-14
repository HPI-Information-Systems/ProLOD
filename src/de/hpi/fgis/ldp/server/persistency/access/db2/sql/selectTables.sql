-- %1$s schema name

SELECT tabname FROM SYSCAT.TABLES where tabschema = '%1$s'