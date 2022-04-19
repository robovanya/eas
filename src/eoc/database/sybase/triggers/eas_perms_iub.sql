CREATE TRIGGER "eas_perms_iub" BEFORE INSERT, UPDATE
-- ALTER TRIGGER "eas_perms_ib" BEFORE INSERT, UPDATE
ORDER 1 ON "eas"."eas_perms"
REFERENCING OLD AS old_name NEW AS new_name 
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN
    DECLARE iGorU_id int default 0;
    if new_name.c_usertype = 'GROUP' then
       set iGorU_id = (select id_eas_usrgrp from eas_usrgrp where c_group = new_name.c_uname);
    endif;
    if new_name.c_usertype = 'USER' then
       set iGorU_id = (select id_eas_users from eas_users where c_user = new_name.c_uname);
    endif;
    set new_name.id_usrgrp_or_users = iGorU_id;
END
