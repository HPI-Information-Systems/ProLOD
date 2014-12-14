
SELECT T1.SUBJECT_ID FROM MAINTABLE T1, subjecttable, (SELECT ID FROM predicatetable WHERE predicate = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type') predicate_type, (SELECT TUPLE_ID FROM objecttable WHERE object in ('http://www.w3.org/2000/01/rdf-schema#Class','http://www.w3.org/1999/02/22-rdf-syntax-ns#Class', 'http://www.w3.org/2002/07/owl#Class') ) object_class
 WHERE 
   T1.PREDICATE_ID = predicate_type.ID 
   AND T1.tuple_id = object_class.TUPLE_ID
   AND subjecttable.ID = T1.SUBJECT_ID	
   AND subjecttable.SUBJECT = 'http://www.w3.org/2002/07/owl#Thing'