-- %1$s constraint view
-- %2$s constraint condition
-- %3$s number of examples
-- ?1 predicate 1
-- ?2 predicate 2

SELECT	tabX.subject  AS X, tabY.subject AS Y 
        FROM LINKEDSUBJECTS tab ,subjecttable tabX,subjecttable tabY %1$s
              WHERE tab.predicate_id = ? 
              AND tab.predicate_id_2 = ?
              AND tabX.ID = tab.subject_id
              AND tabY.ID = tab.subject_id_2
              %2$s
              ORDER BY tab.cnt ASC, predicate_id DESC
                  LIMIT %3$s