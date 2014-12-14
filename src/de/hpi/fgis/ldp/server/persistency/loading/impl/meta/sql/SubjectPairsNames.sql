-- %1$s maintable partition
-- %2$s constraint view
-- %3$s constraint condition
-- %4$s predicate id


SELECT Tab.ID, Tab.subject
	FROM SUBJECTTABLE Tab
		WHERE Tab.ID in (
SELECT  MT.subject_id
	FROM %1$s MT %2$s
	WHERE MT.subject_id IS NOT NULL
	AND MT.predicate_id = %4$s
	%3$s
	)