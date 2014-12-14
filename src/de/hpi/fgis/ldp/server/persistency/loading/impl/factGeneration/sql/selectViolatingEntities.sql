--%1$s maintable partition
-- %2$s constraint view
-- %3$s constraint condition 1
-- %4$s existing object
-- %5$s missing object

SELECT distinct MT.subject_id, predicate_id 
	FROM  %1$s MT %2$s
	WHERE MT.subject_id IN (
		(SELECT distinct MT.subject_id
			FROM %1$s MT %2$s
			WHERE MT.subject_id IS NOT NULL
			AND MT.internallink_id = %4$s
			%3$s )
		EXCEPT
		(SELECT distinct MT.subject_id
			FROM %1$s MT %2$s
			WHERE MT.subject_id IS NOT NULL	
			AND MT.internallink_id = %5$s
			%3$s )
	)	

ORDER BY MT.subject_id