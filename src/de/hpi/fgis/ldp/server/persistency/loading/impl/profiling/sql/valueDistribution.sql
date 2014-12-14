-- %1$s cluster data partition
-- %2$s Property id
-- %3$s datatype id
-- %4$s pattern id comparism
-- %5$s constraint view
-- %6$s initial constraint condition
-- %7$s addidional constraint condition
-- 
--not yet necessary
-- 
--SELECT object as object, COUNT(*) AS cnt, internallink_id
--FROM %1$s mt, objecttable %5$s
--WHERE mt.tuple_id = objecttable.tuple_id
--AND predicate_id IN %2$s AND datatype_id = %3$s AND pattern_id %4$s %7$s
--GROUP BY object, internallink_id
--ORDER BY cnt DESC

SELECT object as object, COUNT(*) AS cnt
FROM %1$s mt, objecttable %5$s
WHERE mt.tuple_id = objecttable.tuple_id
AND predicate_id IN %2$s AND datatype_id = %3$s AND pattern_id %4$s %7$s
GROUP BY object
ORDER BY cnt DESC