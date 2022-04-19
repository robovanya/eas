/* 
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */
/**
 * Author:  vrobo
 * Created: Apr 4, 2022
 */

CREATE OR REPLACE FUNCTION "eas"."EaS_getCurrentTimeStamp"()
  RETURNS VARCHAR(30)
  LANGUAGE PLPGSQL
  AS
$$
DECLARE retval VARCHAR;
BEGIN
        retval :=  to_char(CURRENT_TIMESTAMP,'YYYY-MM-DD HH24:MI:SS') || ' ' || CURRENT_USER;

	RETURN retval;

END;
$$

