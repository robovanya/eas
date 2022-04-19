CREATE TRIGGER "eas_calAccount_defb" BEFORE INSERT, DELETE, UPDATE
ORDER 50 ON "eas"."eas_calAccount"
REFERENCING OLD AS bfOld NEW AS bfNew
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN
     if inserting then 
        set bfNew.c_zapisal = EaS_getCurrentTimeStamp();
        set bfNew.c_zmenil = EaS_getCurrentTimeStamp();
     endif;
     if updating then 
        set bfNew.c_zmenil = EaS_getCurrentTimeStamp()
     endif 
END;
COMMENT ON TRIGGER "eas"."eas_calAccount"."eas_calAccount_defb" IS 'Easys - default before event handler';

