create or REPLACE function get_bus_cords_by_child(childid bigint) returns text
LANGUAGE plpgsql
AS $$
DECLARE
  r RECORD;
BEGIN
SELECT * FROM buses WHERE childID = ANY (buses.to_home_kids) INto r;
IF r ISNULL THEN SELECT * FROM buses WHERE childID = ANY (buses.to_school_kids) INTO r;
END IF ;
  IF is_bus_on_route(r.id) THEN RETURN r.last_gps_cords; ELSE RETURN NULL ;
  END IF ;
END;
$$;
