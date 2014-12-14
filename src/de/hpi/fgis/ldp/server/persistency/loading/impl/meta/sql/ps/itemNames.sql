-- %1$s list of predicate ids


SELECT Tab.ID, Tab.subject
	FROM subjecttable Tab
		WHERE Tab.ID in %1$s