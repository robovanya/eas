DO $$ 
declare
    bZapExist  int default 0; 
    bZmeExist  int default 0;
    bDefbExist boolean default false;
    cDefbCode  varchar default '';
    tablesRow record;
    columnsRow record;
    cCommand varchar default '';
BEGIN

CREATE OR REPLACE FUNCTION trg_upd_zmenil()
  RETURNS trigger AS
$func$
DECLARE
   op      text := TG_OP;
   fulltbl text := quote_ident(TG_TABLE_SCHEMA) || '.'
                   || quote_ident(TG_TABLE_NAME);
   tbl text := quote_ident(TG_TABLE_NAME);
   cCommand varchar default '';
   myid integer;
 
BEGIN

CASE TG_OP
WHEN 'INSERT' THEN
NEW.c_zapisal = "eas"."EaS_getCurrentTimeStamp"();
NEW.c_zmenil = "eas"."EaS_getCurrentTimeStamp"();
RETURN NEW;
WHEN 'UPDATE' THEN
NEW.c_zmenil = "eas"."EaS_getCurrentTimeStamp"();
RETURN NEW;
WHEN 'DELETE' THEN 
   OLD.c_zmenil = "eas"."EaS_getCurrentTimeStamp"();
RETURN OLD;
ELSE
   RAISE EXCEPTION 'Neznamy TG_OP: "%" !', TG_OP;
END CASE;

END
$func$  LANGUAGE plpgsql;



FOR tablesRow IN 
SELECT * FROM pg_catalog.pg_tables WHERE pg_catalog.pg_tables.schemaname = 'eas'
LOOP
   raise notice  '% %', '#### table_name:', tablesRow.tablename;
   bZapExist := 0;
   FOR columnsRow IN 
   SELECT * FROM information_schema.columns WHERE table_schema = tablesROw.schemaname
             AND table_name   = tablesRow.tablename
   LOOP
      -- raise info '% %', '______column_name:', columnsRow.column_name;
       if columnsRow.column_name = 'c_zapisal' then bZapExist := 1; end if;
       if columnsRow.column_name = 'c_zmenil' then bZmeExist := 1; end if;
   END LOOP;

bDefbExist := exists(select 
       event_object_schema as table_schema,
       event_object_table as table_name,
       trigger_schema,
       trigger_name,
       string_agg(event_manipulation, ',') as event,
       action_timing as activation,
       action_condition as condition,
       action_statement as definition
from information_schema.triggers
where event_object_schema = 'eas'
and event_object_table = 'eas_attrib'
and trigger_name = 'upd_zmenil'
group by 1,2,3,4,6,7,8
order by table_schema,
         table_name);

   -- IF bDefbExist IS NULL THEN set bDefbExist = 0; END IF;

   raise info '% % %', bZapExist, bZmeExist, bDefbExist;

   IF bZapExist = 0 THEN 
       cCommand :=  concat('ALTER TABLE eas."',tablesRow.tablename,'" ADD "c_zapisal" VARCHAR(50)');
       raise info '% %', '**COMMANDAAABBBB:', cCommand;
       EXECUTE cCommand;
--       :'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zapisal IS ' + '''D�tum, �as a meno - vlo�enie riadku''';
   END IF;
  IF bZmeExist = 0 THEN 
       cCommand :=  concat('ALTER TABLE eas."',tablesRow.tablename,'" ADD "c_zmenil" VARCHAR(50)');
       raise info '% %', '**COMMANDAAABBBB:', cCommand;
       EXECUTE cCommand;
--       :'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zapisal IS ' + '''D�tum, �as a meno - vlo�enie riadku''';
   END IF;
   
   IF NOT bDefbExist THEN
   cCommand := 'CREATE TRIGGER upd_zmenil AFTER INSERT OR UPDATE ON eas."' ||
              tablesRow.tablename || '" FOR EACH ROW EXECUTE PROCEDURE trg_upd_zmenil();';
   EXECUTE cCommand;
   END IF;

END LOOP;

END; $$ LANGUAGE plpgsql;


--SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'eas';

/*
SELECT *
FROM pg_catalog.pg_tables
WHERE schemaname != 'pg_catalog' AND 
    schemaname != 'information_schema';


SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'eas' -- and table_name like '%perm%'
ORDER BY table_name;
*/
/* old sybase code
   select table_id as tblid, table_name as tblname,  creator as crtr, 
       (select user_name from sysuser where user_id = crtr) as ownr
       from systable
       where (substr(table_name,1,3) ='nz_' OR substr(table_name,1,4) = 'eas_')
         and (ownr = 'eas' OR ownr = 'nzbd')
*/

/*  QQQQQQ
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
       'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zapisal IS ' + '''D�tum, �as a meno - vlo�enie riadku''';

   ENDIF;

   IF bZmeExist = 0 THEN 
       EXECUTE IMMEDIATE 
       'ALTER TABLE ' + ownr + '.' + tblname + ' ADD "c_zmenil" VARCHAR(50) NOT NULL;';
       EXECUTE IMMEDIATE 
       'COMMENT ON COLUMN '  + ownr + '.' + tblname + '.c_zmenil IS ' + '''D�tum, �as a meno - posledn� �prava riadku''';
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
QQQQQQ*/
/* old sybase code
   END FOR;
*/