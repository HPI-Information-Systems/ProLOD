-- %1$s main schema
-- ? 1 id (db internal schema name)
-- ? 2 cluster root session
-- ? 3 user view

REPLACE INTO %1$s.root_sessions (id, username, root_session) VALUES (?, ?, ?)