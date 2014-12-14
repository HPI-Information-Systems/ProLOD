-- %1$s user view

SELECT s.id, s.schema_name, r.root_session, s.tuples, s.entities, r.username 
	FROM schemata s, root_sessions r
		WHERE s.id = r.id
		AND r.username = '%1$s'
			ORDER BY s.schema_name ASC