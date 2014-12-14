-- %1$s prolod_main
-- %2$s Constants.db2Schema


-- delete schema entry
DELETE FROM %1$s.schemata WHERE id = '%2$s';
DELETE FROM %1$s.root_sessions WHERE id = '%2$s';


DROP SCHEMA %2$s restrict;