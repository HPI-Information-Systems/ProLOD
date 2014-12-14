-- %1$s cluster id
-- %2$s constraint view
-- %3$s property id

SELECT COUNT(DISTINCT st.id) as cnt  
FROM %1$s mt, subjecttable st %2$s
WHERE mt.subject_id = st.id
AND predicate_id = %3$s
