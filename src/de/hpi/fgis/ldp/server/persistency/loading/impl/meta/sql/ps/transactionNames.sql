-- %1$s list of subject ids


SELECT Tab.ID, Tab.predicate
	FROM predicateTABLE Tab
		WHERE Tab.ID in %1$s