-- %1$s main schema
-- ? 1 id (db internal schema name)
-- ? 2 cluster root session
-- ? 3 user view

MERGE INTO %1$s.root_sessions AS t
  USING TABLE(VALUES(CAST (? AS VARCHAR(30)), CAST (? AS VARCHAR(50)),
                     CAST (? AS INTEGER))) as 
                     s(id, username, root_session)
  ON t.id = s.id AND t.username = s.username
  WHEN MATCHED THEN
     UPDATE SET
        root_session = s.root_session
  WHEN NOT MATCHED THEN
     INSERT
        (id, username, root_session)
        VALUES (s.id, s.username, s.root_session)