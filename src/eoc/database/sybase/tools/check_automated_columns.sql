/*
 * doplni chybajuce povinne stlpce tabuliek eas_ a nz_ (c_zapisal, c_zmenil)
 *
 * 2015-10-27  -  Robo Vanya
 *
 * 2016-04-13 - generovania kodu *_defb triggerov
 ***************************************************************************************/
BEGIN

declare bZapExist  int default 0; 
declare bZmeExist  int default 0;
declare bDefbExist int default 0;
declare cDefbCode  varchar default '';

for c as c cursor for 
   select table_id as tblid, table_name as tblname,  creator as crtr, 
       (select user_name from sysuser where user_id = crtr) as ownr
       from systable
       where (substr(table_name,1,3) ='nz_' OR substr(table_name,1,4) = 'eas_')
         and (ownr = 'eas' OR ownr = 'nzbd')
       order by table_name
   DO

   SET bZapExist  = (select 1 from syscolumn where syscolumn.table_id = tblid and column_name = 'c_zapisal');
   IF bZapExist IS NULL THEN set bZapExist = 0; END IF;

   SET bZmeExist  = (select 1 from syscolumn where syscolumn.table_id = tblid and column_name = 'c_zmenil');
   IF bZmeExist IS NULL THEN set bZmeExist = 0; END IF;

   SET bDefbExist = (select 1 from systriggers where tname = tblname and trigname = 'def_biud_' + tblname);
   IF bDefbExist IS NULL THEN set bDefbExist = 0; END IF;

   message tblid,' ',tblname, ' bZapExist=', bZapExist, ' bZmeExist=', bZmeExist, ' bDefbExist=', bDefbExist to client;

   IF bZapExist = 0 THEN 
       EXECUTE IMMEDIATE 
       'ALTER TABLE ' + ownr + '.' + tblname + ' ADD "c_zapisal" AS VARCHAR(50) NOT NULL;';
       EXECUTE IMMEDIATE 
       'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zapisal IS ' + '''Dátum, èas a meno - vloženie riadku''';

   ENDIF;

   IF bZmeExist = 0 THEN 
       EXECUTE IMMEDIATE 
       'ALTER TABLE ' + ownr + '.' + tblname + ' ADD "c_zmenil" VARCHAR(50) NOT NULL;';
       EXECUTE IMMEDIATE 
       'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zmenil IS ' + '''Dátum, èas a meno - posledná úprava riadku''';
   ENDIF;
   
   EXECUTE IMMEDIATE 
       'GRANT ALL ON ' + ownr + '.' + tblname + ' TO PUBLIC;';

   commit;

   IF bDefbExist = 0 THEN
//   SET cDefbCode = 
   EXECUTE IMMEDIATE 
   'CREATE TRIGGER "def_biud_' + tblname + '" BEFORE INSERT, DELETE, UPDATE\n' +
   'ORDER 50 ON "' + ownr + '"."' + tblname+ '"\n' +
   'REFERENCING OLD AS bfOld NEW AS bfNew\n' +
   'FOR EACH ROW BEGIN\n' +
     'if inserting then\n' +
        'set bfNew.c_zapisal = EaS_getCurrentTimeStamp();\n' +
        'set bfNew.c_zmenil = EaS_getCurrentTimeStamp();\n' +
     'endif;\n' +
     'if updating then\n' +
        'set bfNew.c_zmenil = EaS_getCurrentTimeStamp()\n' +
     'endif\n' +
'END;\n';
   ENDIF;
  EXECUTE IMMEDIATE 
 'COMMENT ON TRIGGER "' + ownr + '"."' + tblname + '"."def_biud_' + tblname 
        + '" IS ' + '''Easys - default before event handler''';

   END FOR;

END
