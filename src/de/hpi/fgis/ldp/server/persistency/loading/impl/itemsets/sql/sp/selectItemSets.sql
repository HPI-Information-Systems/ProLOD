-- %1$s min frequency
-- %2$s constraint view
-- %3$s constraint condition
-- %4$s maintable partition


	
SELECT predicate_id, count(distinct subject_id)
	FROM %4$s MT %2$s
	WHERE MT.subject_id IS NOT NULL
		AND MT.predicate_id IS NOT NULL
		%3$s  
		      group by predicate_id
		      having count(distinct subject_id)>= %1$s
