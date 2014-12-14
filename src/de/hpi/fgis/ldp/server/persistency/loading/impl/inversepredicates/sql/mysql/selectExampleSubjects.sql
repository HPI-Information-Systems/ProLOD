-- %1$s constraint view
-- %2$s constraint condition
-- %3$s number of examples
-- ?1 predicate 1
-- ?2 predicate 2

select	(select subject from subjecttable subj where subj.ID = tab.subject_id)  as X, 
		(select subject from subjecttable subj where subj.ID = tab.subject_id_2 ) as Y 
        from LINKEDSUBJECTS tab %1$s
              where tab.predicate_id = ? 
              and tab.predicate_id_2 = ?
              %2$s
              order by cnt asc, predicate_id desc
                  LIMIT %3$s