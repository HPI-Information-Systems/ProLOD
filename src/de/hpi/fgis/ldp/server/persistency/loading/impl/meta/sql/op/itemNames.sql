-- %1$s list of predicate ids


SELECT Tab.ID, Tab.predicate
	FROM predicatetable Tab
		WHERE Tab.ID in %1$s