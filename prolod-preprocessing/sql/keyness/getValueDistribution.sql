-- %1$s cluster data partition id
-- %2$s property id
-- %3$s constraint view
-- %4$s initial constraint condition
-- %5$s additional constraint condition

SELECT object as object, COUNT(*) AS cnt
FROM %1$s.maintable mt, %1$s.objecttable objecttable
WHERE mt.tuple_id = objecttable.tuple_id
AND predicate_id = %2$s
GROUP BY object
ORDER BY cnt ASC