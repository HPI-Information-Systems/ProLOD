-- Generates the statistics for every existing datatype
-- Count, Min, Max, Avg and Sqrt(variance)
-- %1$s cluster data partition
-- %2$s Property ids
-- %3$s constraint view
-- %4$s initial constraint condition
-- %5$s addidional constraint condition

SELECT 
  datatype_id, 
  COUNT(datatype_id) AS cnt, 
  MIN(parsed_value) AS minimum, 
  MAX(parsed_value) AS maximum, 
  AVG(parsed_value) AS average, 
  SQRT(VARIANCE(parsed_value)) AS standardDevation
FROM %1$s %3$s
WHERE predicate_id IN %2$s %5$s
GROUP BY datatype_id
ORDER BY CNT DESC