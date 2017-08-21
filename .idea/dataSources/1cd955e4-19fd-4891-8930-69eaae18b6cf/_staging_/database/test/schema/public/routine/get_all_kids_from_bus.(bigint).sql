create or REPLACE function get_all_kids_from_bus(ik bigint) returns TABLE(id bigint, name text, school_id text, photo text)
LANGUAGE plpgsql
AS $$
DECLARE
  i BIGINT;
  arr BIGINT[] = (SELECT to_school_kids FROM buses WHERE buses.id=ik);
  r RECORD;
BEGIN
  IF array_length(arr,1) = 0 or arr ISNULL THEN  RETURN ;
  END IF;
  FOREACH i IN ARRAY arr
  LOOP
    FOR r in (SELECT *
              from kids WHERE kids.id=i)
    LOOP
      id :=r.id;
      name :=r.name;
      school_id :=r.school_id;
      photo :=r.photo;
    END LOOP;
    RETURN NEXT ;


  END LOOP;
END
$$;