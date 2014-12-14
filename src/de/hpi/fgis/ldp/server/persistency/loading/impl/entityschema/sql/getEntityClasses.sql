
SELECT T1.SUBJECT_ID, T1.INTERNALLINK_ID FROM MAINTABLE T1, subjecttable, (SELECT ID FROM predicatetable WHERE predicate = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type') predicate_type
     WHERE 
       T1.PREDICATE_ID = predicate_type.ID 
       AND subjecttable.ID = T1.SUBJECT_ID
       AND T1.INTERNALLINK_ID IS NOT NULL