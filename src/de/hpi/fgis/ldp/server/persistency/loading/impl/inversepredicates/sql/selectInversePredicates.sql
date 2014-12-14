-- %1$s constraint view (links)
-- %2$s constraint view (linked subjects)
-- %3$s initial constraint condition (links)
-- %4$s additional constraint condition (links)
-- %5$s constraint condition (linked subjects)
-- %6$s minFrequency


select	(select predicate from predicatetable prop where prop.ID = tab.predicate_id)  as X, tab.predicate_id as X_id,   
		(select predicate from predicatetable prop where prop.ID = tab.predicate_id_2 ) as Y, tab.predicate_id_2 as Y_id, 
		count(*) as cntXnY,
        (select count(*) from LINKS subj %1$s where subj.predicate_id = tab.predicate_id %4$s) as cntX,
        (select count(*) from LINKS subj %1$s where subj.predicate_id = tab.predicate_id_2 %4$s) as cntY
        from LINKEDSUBJECTS tab %2$s
	          where tab.predicate_id >= tab.predicate_id_2 %5$s
		              group by tab.predicate_id, tab.predicate_id_2
		              having count(*) > %6$s 