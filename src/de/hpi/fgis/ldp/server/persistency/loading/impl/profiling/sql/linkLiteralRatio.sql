-- Counts the absolute number of object values, the number of internal links and external links
-- %1$s cluster data partition
-- %2$s constraint view
-- %3$s initial constraint condition
-- %4$s addidional constraint condition

SELECT COUNT(*), COUNT(internallink_id), (SELECT COUNT(*) FROM %1$s %2$s WHERE datatype_id = 67 AND internallink_id IS NULL %4$s)
FROM %1$s %2$s
%3$s