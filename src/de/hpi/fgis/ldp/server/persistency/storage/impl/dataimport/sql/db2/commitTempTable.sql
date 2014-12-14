-- commit tmp table
-- %1$s schema

COMMIT;
LOCK TABLE import_tmp IN EXCLUSIVE MODE;
ALTER TABLE import_tmp ACTIVATE NOT LOGGED INITIALLY;
ALTER TABLE import_tmp APPEND ON;