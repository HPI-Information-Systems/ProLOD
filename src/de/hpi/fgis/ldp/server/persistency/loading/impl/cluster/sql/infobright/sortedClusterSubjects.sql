-- Gets all Sujects in one cluster or a cluster-subset
-- %1$s constraint view
-- %2$s constraint condition
-- %3$s fetch size



SELECT DISTINCT ID, subject, CNT 
	FROM SubjectTable %1$s
		%2$s
		ORDER BY subject
		LIMIT %3$s