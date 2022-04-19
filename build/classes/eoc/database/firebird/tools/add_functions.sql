CREATE FUNCTION "eas"."EaS_getCurrentTimeStamp"( /* [IN] parameter_name parameter_type [DEFAULT default_value], ... */ )
//ALTER FUNCTION "eas"."getCurrentTimeStamp"( /* [IN] parameter_name parameter_type [DEFAULT default_value], ... */ )
RETURNS VARCHAR(60)
DETERMINISTIC
BEGIN
	DECLARE "retval" VARCHAR(30);
	set retval = (select  cast(DATE(GETDATE()) as CHAR(10)) 
        + ' ' + cast(DATEPART(HOUR, GETDATE()) as CHAR(2))  
        + ':' + cast(DATEPART(MINUTE, GETDATE()) as char(2))
        + ' ' + (select current user));
  
	RETURN "retval";
END;


CREATE FUNCTION "eas"."EaS_NevybavenaHlasudr"( 
idHlasudr INTEGER )
RETURNS SMALLINT
// NOT DETERMINISTIC
BEGIN
	DECLARE "jeNedokoncena" SMALLINT DEFAULT 1;
   declare i integer default 0;
   set i  = (select count(*) from ana.OBJEDN left outer join ana.faktury 
      where OBJEDN.hla_id = idHlasudr 
            /* and faktury.obj_id = null*/ 
            );
   if i=null then set i = 0 endif;
   if i = 0 then set jeNedokoncena = 0
   else set jeNedokoncena = 1 endif;
	RETURN "jeNedokoncena";
END;


CREATE FUNCTION "eas"."eas_getMetaValue" (valName VARCHAR(60), cMetaData char(4096))
RETURNS VARCHAR(60)
BEGIN
	DECLARE "retval" VARCHAR(30);
   DECLARE iEntries     INTEGER;
   DECLARE iCurrEnt     INTEGER;
   DECLARE delimEnt     CHAR(1);
   DECLARE delimSet     CHAR(1);
   DECLARE currSet      VARCHAR(100);
   DECLARE currSetName  VARCHAR(100);
   DECLARE currSetValue VARCHAR(100);
   SET delimEnt  = substr(cMetaData,1,1);
   SET delimSet  = substr(cMetaData,2,1);
   SET cMetaData = substr(cMetaData,3); // skratenie o definiciu delimiterov
   //SET retval = delimEnt + delimSet;
   SET iEntries = ana.EaS_GetNumEntries(cMetaData,delimEnt);
   -- message 'XX-> ', cMetaData, ' ', delimEnt, ' ', iEntries to client;
   SET iCurrEnt = 1;
   blk:
   WHILE iCurrEnt <= iEntries LOOP
       SET currSet = ana.EaS_GetEntry(iCurrEnt,cMetaData,delimEnt);
       SET currSetName = ana.EaS_GetEntry(1,currSet,delimSet);
 --      message 'currSet==', currSetName, ' testing==', valName to client;
       IF currSetName = valName THEN
          SET retval = ana.EaS_GetEntry(2,currSet,delimSet);
          LEAVE blk;
       ENDIF;
       SET iCurrEnt = iCurrEnt + 1;

       --message currSet to client;
   END LOOP;

	--SET retval = (SELECT cast(iEntries as VARCHAR(60)));
   RETURN retval;
END;


CREATE FUNCTION "eas"."eas_setMetaValue" (valName VARCHAR(60), cMetaData char(4096),valStr VARCHAR(60) )
RETURNS VARCHAR(60)
BEGIN
	DECLARE "retval" VARCHAR(30);
   DECLARE iEntries     INTEGER;
   DECLARE iCurrEnt     INTEGER;
   DECLARE iEntFound    INTEGER DEFAULT 0;
   DECLARE delimEnt     CHAR(1);
   DECLARE delimSet     CHAR(1);
   DECLARE currSet      VARCHAR(100);
   DECLARE currSetName  VARCHAR(100);
   
   SET delimEnt  = substr(cMetaData,1,1);
   SET delimSet  = substr(cMetaData,2,1);
   SET cMetaData = substr(cMetaData,3); // skratenie o definiciu delimiterov
   
   SET iEntries = ana.EaS_GetNumEntries(cMetaData,delimEnt);
   SET iCurrEnt = 1;
   blk:
   WHILE iCurrEnt <= iEntries LOOP
       SET currSet = ana.EaS_GetEntry(iCurrEnt,cMetaData,delimEnt);
       SET currSetName = ana.EaS_GetEntry(1,currSet,delimSet);
       IF currSetName = valName THEN
          SET iEntFound = iCurrEnt;
          LEAVE blk;
       ENDIF;
       SET iCurrEnt = iCurrEnt + 1;

   END LOOP;
   IF iEntFound > 0 THEN
       SET cMetaData = ana.EaS_SetEntry(iEntFound,cMetaData,delimEnt,valName + delimSet + valStr);
   ELSE
       SET cMetaData = ana.EaS_SetEntry(iEntFound + 1,cMetaData,delimEnt,valName + delimSet + valStr);
   ENDIF;   
   SET cMetaData = delimEnt + delimSet + cMetaData;
	--SET retval = (SELECT cast(iEntries as VARCHAR(60)));
   RETURN cMetaData;
END;