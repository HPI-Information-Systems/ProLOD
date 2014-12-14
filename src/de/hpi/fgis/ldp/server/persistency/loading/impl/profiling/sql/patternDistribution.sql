-- Counts the occurrence of every pattern of a specified property
-- %1$s cluster data partition
-- %2$s Datatype id
-- %3$s Property id
-- %4$s constraint view
-- %5$s initial constraint condition
-- %6$s addidional constraint condition

SELECT pt.id, pt.pattern as pattern, COUNT(*) AS cnt
FROM %1$s mt, patterntable pt %4$s
WHERE pt.id = mt.pattern_id
AND mt.datatype_id = %2$s AND mt.predicate_id IN %3$s %6$s
GROUP BY pt.pattern, pt.id
ORDER BY cnt DESC