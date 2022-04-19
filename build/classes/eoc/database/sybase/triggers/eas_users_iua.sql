CREATE TRIGGER "eas_users_iua" AFTER INSERT, UPDATE
--ALTER TRIGGER "eas_users_ua" AFTER INSERT, UPDATE
ORDER 1 ON "eas"."eas_users"
REFERENCING OLD AS old_name NEW AS new_name 
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN
    declare calAccountID integer default null;

 	 if not exists(select 1 from eas.eas_x_grpusr where eas_x_grpusr.id_eas_users = new_name.id_eas_users) then
    BEGIN
       declare defaultID integer;
       set defaultID = (select id_eas_usrgrp from eas.eas_usrgrp where eas_usrgrp.c_group = '<bez_skupiny>');
       insert into eas_x_grpusr (id_eas_usrgrp,id_eas_users,c_zapisal,c_zmenil) 
            values (defaultID, new_name.id_eas_users, new_name.c_zapisal, new_name.c_zmenil);
    END;
    end if;

   set calAccountID = (select id_eas_calAccount from eas_calAccount 
                        where id_c_owner_table = new_name.id_eas_users
                          and c_accountOwnerType = 'Uzivatel'); // E = easys-user

   if (calAccountID is null) then 
	       insert into eas_calAccount (c_accountOwnerType,c_owner_table,id_c_owner_table,c_owner_name,c_owner_pozn)
               values ('Uzivatel','eas_users',new_name.id_eas_users,new_name.c_user,new_name.c_popis);
   else 
       if new_name.c_user <> old_name.c_user or new_name.c_popis <> old_name.c_popis then
	        update eas_calAccount 
              set c_owner_name = new_name.c_user ,c_owner_pozn = new_name.c_popis
            where id_eas_calAccount = calAccountID;
       endif;
   endif;

END