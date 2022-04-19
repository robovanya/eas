// 2017-01-13
/*
*/
set option PUBLIC.MAX_STATEMENT_COUNT = 0;
set option PUBLIC.MAX_CURSOR_COUNT = 0;

ALTER TABLE eas_crotim ADD i_num_new_messages INTEGER DEFAULT 0;
COMMENT ON COLUMN eas_crotim.i_num_new_messages IS 'Po�et nov�ch spr�v';

ALTER TABLE eas_crotim MODIFY i_last_time bigint;

CREATE UNIQUE INDEX "idx_c_cro_user" ON "eas"."eas_crotim" ( "c_cro_user" ASC ) IN "system";

// 2017-02-02
/*
*/
ALTER TABLE eas_cromsg RENAME i_msgtime TO c_msgtime;
ALTER TABLE eas_cromsg MODIFY c_msgbody varchar(4096);
ALTER TABLE eas_cromsg MODIFY c_msgtime char(8);
COMMENT ON COLUMN eas_cromsg.c_msgtime IS '�as vzniku spr�vy vo tvare HH:MM:SS';

ALTER TABLE eas_cromsg RENAME _readtime TO c_readtime;
ALTER TABLE eas_cromsg MODIFY c_readtime char(8);
COMMENT ON COLUMN eas_cromsg.c_readtime IS '�as pre��tania spr�vy vo tvare HH:MM:SS';

CREATE INDEX "x_odkoho_usr_dt_time" ON "eas"."eas_cromsg" 
   ("c_odkoho" ASC, "c_cro_user" ASC, "d_msgdate" ASC, "c_msgtime" ASC) IN "system";

CREATE INDEX "x_usr_odkoho_dt_time" ON "eas"."eas_cromsg" 
   ( "c_cro_user" ASC, "c_odkoho" ASC, "d_msgdate" ASC, "c_msgtime" ASC) IN "system";
// 2017-02-03
ALTER TABLE eas_crotim ADD i_num_unreaded_messages INTEGER DEFAULT 0;
COMMENT ON COLUMN eas_crotim.i_num_unreaded_messages IS 'Po�et nepre��tan�ch spr�v';
// 2017-02-05
ALTER TABLE "eas"."eas_cromsg" ADD "d_loaddate" DATE NULL;
COMMENT ON COLUMN "eas"."eas_cromsg"."d_loaddate" IS 'D�tum na��tania spr�vy';
ALTER TABLE "eas"."eas_cromsg" ADD "c_loadtime" CHAR(8) NULL;
COMMENT ON COLUMN "eas"."eas_cromsg"."c_loadtime" IS '�as na��tania spr�vy vo tvare HH:MM:SS';
CREATE INDEX "x_usr_dtload" ON "eas"."eas_cromsg" 
   ( "c_cro_user" ASC, "d_loaddate" ASC) IN "system";

CREATE OR REPLACE FUNCTION "eas"."EaS_getCurrentTime"( fmt VARCHAR(3), dtm datetime)
RETURNS VARCHAR(30)
BEGIN

	DECLARE "retval" VARCHAR(30) DEFAULT '';
    DECLARE prt CHAR(2);

    IF fmt IS null THEN SET fmt = 'HMS';  ENDIF; // Default
    IF dtm IS null THEN SET dtm = now(); ENDIF; // Default

    SET dtm = now();
    IF CHARINDEX('H', fmt) > 0 THEN
        SET prt = cast(DATEPART(HOUR, dtm) as CHAR(2));
        IF length(prt) = 1 THEN SET prt = '0'+ prt; ENDIF;
        SET retval = retval + ':' + prt;
    ENDIF;

    IF CHARINDEX('M', fmt) > 0 THEN
        SET prt = cast(DATEPART(MINUTE, dtm) as CHAR(2));
        IF length(prt) = 1 THEN SET prt = '0'+ prt; ENDIF;
        SET retval = retval + ':' + prt;
    ENDIF;

    IF CHARINDEX('S', fmt) > 0 THEN
        SET prt = cast(DATEPART(SECOND, dtm) as CHAR(2));
        IF length(prt) = 1 THEN SET prt = '0'+ prt; ENDIF;
        SET retval = retval + ':' + prt;
    ENDIF;

    IF length(retval) > 1  THEN SET retval = substr(retval,2); ENDIF;

	RETURN "retval";

END;

CREATE OR REPLACE TRIGGER "eas_crotim_iub" BEFORE INSERT, UPDATE
ORDER 1 ON "eas"."eas_crotim"
 REFERENCING OLD AS oBuff NEW AS nBuff 
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN
   IF NOT EXISTS (SELECT 1 FROM eas_users WHERE eas_users.c_user = nBuff.c_cro_user) THEN 
       RAISERROR 90000 'U��vate� ' + nBuff.c_cro_user + ' neexistuje.';
   ENDIF; 
END;

//*****************************************************************************************
CREATE OR REPLACE TRIGGER "eas_cromsgs_iudb" BEFORE INSERT, UPDATE, DELETE
ORDER 1 ON "eas"."eas_cromsg"
 REFERENCING OLD AS old_name NEW AS new_name 
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN

   DECLARE crotimID INTEGER;
   DECLARE dnes     DATE; 

   IF NOT EXISTS (SELECT 1 FROM eas_users WHERE eas_users.c_user = new_name.c_cro_user) THEN 
       RAISERROR 90000 'U��vate�/pr�jemca spr�vy: ' + new_name.c_cro_user + ' neexistuje.';
   ENDIF; 

   IF NOT EXISTS (SELECT 1 FROM eas_users WHERE eas_users.c_user = new_name.c_odkoho) THEN 
       RAISERROR 90000 'U��vate�/odosielate� spr�vy: ' + new_name.c_odkoho + ' neexistuje.';
   ENDIF;

   IF new_name.d_msgdate IS null THEN 
      SET new_name.d_msgdate = today();
   ENDIF;

   IF new_name.c_msgtime IS null THEN 
      SET new_name.c_msgtime = EaS_getCurrentTime(null,null);
   ENDIF;

   SET crotimID = (SELECT FIRST id_eas_crotim FROM eas_crotim WHERE eas_crotim.c_cro_user = new_name.c_cro_user); 

   IF crotimID IS null THEN 
       INSERT INTO eas_crotim (c_cro_user,d_last_date,i_last_time,c_message) 
             VALUES (new_name.c_cro_user,'1970-01-01',0,'conto_created');
       SET crotimID = @@identity;
   ENDIF;
   message 'crotimID == ', crotimID;
   IF INSERTING THEN 
      UPDATE eas.eas_crotim SET i_num_new_messages = i_num_new_messages + 1, 
                                i_num_unreaded_messages = i_num_unreaded_messages + 1 WHERE id_eas_crotim = crotimID;
   ENDIF; 
   IF UPDATING THEN 
      IF old_name.d_readdate IS NULL AND new_name.d_readdate IS NOT NULL THEN 
         UPDATE eas.eas_crotim SET i_num_new_messages = i_num_new_messages - 1 WHERE id_eas_crotim = crotimID;
         IF new_name.c_readtime IS NULL THEN
              SET new_name.c_readtime = EaS_getCurrentTime(null,null); 
         ENDIF;
      ENDIF;
      IF old_name.d_readdate IS NOT NULL AND new_name.d_readdate IS NULL THEN 
         UPDATE eas.eas_crotim SET i_num_new_messages = i_num_new_messages + 1 WHERE id_eas_crotim = crotimID;
         SET new_name.c_readtime = null; 
      ENDIF;
   ENDIF; 
   IF DELETING THEN 
      IF old_name.d_readdate IS NULL THEN 
         UPDATE eas.eas_crotim SET i_num_new_messages = i_num_new_messages - 1 WHERE id_eas_crotim = crotimID;
         SET new_name.c_readtime = null; 
      ENDIF;
   ENDIF; 

END;

CREATE INDEX "idx_filename" ON "ana"."LIBDOCS" ( "filename" ASC ) IN "system";
COMMENT ON INDEX "ana"."LIBDOCS"."idx_filename" IS 'NZBD - vytvoreny pre java-rozhranie spravy dokumentov';
