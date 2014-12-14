-- %1$s schema
-- %2$s user view

SELECT s.id, s.schema_name, r.root_session, s.tuples, s.entities, r.username 
	FROM schemata s, root_sessions r
		WHERE s.id = r.id
		AND s.id = '%1$s'
		AND r.username = '%2$s'
			ORDER BY s.schema_name ASC