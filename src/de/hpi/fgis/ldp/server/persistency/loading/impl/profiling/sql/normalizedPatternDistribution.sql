-- Counts the occurrence of every normalized pattern of a specified property
-- %1$s cluster data partition
-- %2$s Property id
-- %3$s constraint view
-- %4$s initial constraint condition
-- %5$s addidional constraint condition

SELECT npt.id, npt.normalizedpattern, COUNT(*) AS cnt
FROM %1$s mt, normalizedpatterntable npt %3$s
WHERE npt.id = mt.normalizedpattern_id
AND mt.datatype_id = 3 AND mt.predicate_id IN %2$s %5$s
GROUP BY npt.normalizedpattern, npt.id
ORDER BY cnt DESC