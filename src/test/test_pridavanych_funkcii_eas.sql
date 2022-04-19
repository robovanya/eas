DROP FUNCTION IF EXISTS "eas"."eas_getMetaValue";
DROP FUNCTION IF EXISTS "eas"."eas_setMetaValue";
/* POZOR - uvolnit na novej DB
*/
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

begin
	DECLARE cFormat VARCHAR(30);
   SET cFormat = (SELECT FIRST eas_getMetaValue('format',c_metadata) 
                 from eas_uni_cis where c_typ_cis = 'CisRad' 
                      and c_skratka = 'STA' 
                      and eas_getMetaValue('rok',c_metadata) = String(2016));


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

//select first c_metadata from eas_uni_cis where c_typ_cis = 'CisRad';
select //c_metadata, 
       //eas_getMetaValue('rok',c_metadata) as rok 
       //,eas_getMetaValue('poslednaHodnota',c_metadata) as lastVal 
       //,
       eas_getMetaValue('format',c_metadata) as formatStr,
       eas_setMetaValue('format',c_metadata,'KKKKRRRNNNN') as newMetaData 
 
  from eas_uni_cis where c_typ_cis = 'CisRad' 
       and c_skratka = 'STA' and eas_getMetaValue('rok',c_metadata) = '2016';

//DROP FUNCTION "eas"."eas_getMetaValue";
//DROP FUNCTION "eas"."eas_setMetaValue";

end
