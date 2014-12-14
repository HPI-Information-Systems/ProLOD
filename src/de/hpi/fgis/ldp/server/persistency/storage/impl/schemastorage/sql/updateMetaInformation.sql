-- %1$s main schema
-- %2$s id (db internal schema name)

UPDATE %1$s.schemata SET tuples = (SELECT count(*) FROM %2$s.maintable), entities = (SELECT count(*) FROM %2$s.subjecttable) WHERE id = '%2$s' 
