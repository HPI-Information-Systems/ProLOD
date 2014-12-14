-- %1$s maintable partition
-- %2$s constraint view
-- %3$s constraint condition


SELECT count(*) 
	FROM %1$s MT %2$s
		WHERE MT.internallink_id IS NOT NULL
		AND MT.predicate_id IS NOT NULL
		%3$s