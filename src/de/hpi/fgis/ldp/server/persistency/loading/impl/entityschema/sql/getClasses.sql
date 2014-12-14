
SELECT class.SUBJECT, class.SUBJECT_ID, label.OBJECT, label.TUPLE_ID
FROM (
   SELECT subjecttable.SUBJECT, T1.SUBJECT_ID FROM MAINTABLE T1, subjecttable, (SELECT ID FROM predicatetable WHERE predicate = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type') predicate_type, (SELECT TUPLE_ID FROM objecttable WHERE object in ('http://www.w3.org/2000/01/rdf-schema#Class','http://www.w3.org/1999/02/22-rdf-syntax-ns#Class', 'http://www.w3.org/2002/07/owl#Class') ) object_class
     WHERE 
       T1.PREDICATE_ID = predicate_type.ID 
       AND T1.tuple_id = object_class.TUPLE_ID
       AND subjecttable.ID = T1.SUBJECT_ID
     ) class
   LEFT OUTER JOIN 
     ( SELECT objecttable.OBJECT, T2.TUPLE_ID, T2.SUBJECT_ID 
        FROM MAINTABLE T2, (SELECT ID FROM predicatetable WHERE predicate = 'http://www.w3.org/2000/01/rdf-schema#label' ) predicate_label, objecttable
        WHERE 
                T2.PREDICATE_ID = predicate_label.ID 
                AND objecttable.TUPLE_ID = T2.TUPLE_ID
     ) label ON class.SUBJECT_ID = label.SUBJECT_ID