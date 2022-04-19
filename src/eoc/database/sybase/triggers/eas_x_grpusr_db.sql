create TRIGGER "eas_x_grpusr_db" BEFORE DELETE
--ALTER TRIGGER "eas_x_grpusr_id" BEFORE DELETE
ORDER 1 ON "eas"."eas_x_grpusr" REFERENCING OLD AS old_name 
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN

   declare cnt       integer;
   declare defaultID integer;
   if old_name.c_group = '<bez_skupiny>' then
   begin
 	   if not exists(select 1 from eas_x_grpusr where eas_x_grpusr.id_eas_users = old_name.id_eas_users
         and eas_x_grpusr.c_group <> '<bez_skupiny>') then
         raiserror 90000 'Základnú skupinu (<bez_skupiny>) nemôžete odstrániť bez pridania inej existujúcej skupiny!';
      end if;
   end;
   else 
   begin
      set cnt = (select count(*) from eas_x_grpusr where eas_x_grpusr.id_eas_users = old_name.id_eas_users);
 	   if cnt = 1 then
       // maze sa posledna skupina, automaticky sa prida zakladna (default) skupina
      begin
         set defaultID = (select id_eas_usrgrp from eas_usrgrp where eas_usrgrp.c_group = '<bez_skupiny>');
         insert into eas_x_grpusr (id_eas_usrgrp,id_eas_users) values (defaultID, old_name.id_eas_users);
      end;
      end if;
   end;
   end if;
END