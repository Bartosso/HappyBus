

create or REPLACE function get_all_buses_from_school(school_to_find_id text) returns
  TABLE(id bigint, number text, brand text, model text, color text, driver_id text,
        to_school_kids bigint[], to_home_kids bigint[],
    last_gps_cords text,ready_to_school BOOLEAN, ready_to_home BOOLEAN,last_cordinator_id BIGINT )
LANGUAGE plpgsql
AS $$
DECLARE
  d BIGINT;
BEGIN
  FOR d in SELECT  kids.id FROM  kids WHERE kids.school_id=school_to_find_id
  LOOP
    RETURN QUERY SELECT * FROM buses WHERE d = ANY(buses.to_school_kids);
  END loop;
END
$$;
