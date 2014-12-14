-- %1$s list of subject ids


SELECT Tab.ID, Tab.subject
	FROM SUBJECTTABLE Tab
		WHERE Tab.ID in %1$s