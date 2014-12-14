

SELECT T1.SUBJECT_ID, T1.INTERNALLINK_ID FROM MAINTABLE T1, (SELECT ID FROM predicatetable WHERE predicate = 'http://www.w3.org/2000/01/rdf-schema#domain') predicate_type
  WHERE 
    T1.PREDICATE_ID = predicate_type.ID