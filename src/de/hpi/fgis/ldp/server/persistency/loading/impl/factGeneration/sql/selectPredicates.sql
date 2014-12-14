--%1$s maintable partition
-- %2$s constraint view
-- %3$s constraint condition 1
-- %4$s existing object

SELECT  MT.predicate_id
	FROM %1$s MT %2$s	
	WHERE MT.predicate_id IS NOT NULL
	AND MT.internallink_id = %4$s
		%3$s