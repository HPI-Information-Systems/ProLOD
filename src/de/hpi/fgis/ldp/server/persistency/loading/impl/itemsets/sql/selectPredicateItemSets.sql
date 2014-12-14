-- %1$s min frequency


SELECT Tab.ID, Tab.CNT
	FROM predicatetable Tab
		WHERE Tab.CNT >= %1$s