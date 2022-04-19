create TRIGGER "eas_x_grpusr_ib" BEFORE INSERT 
--ALTER TRIGGER "eas_x_grpusr_ib" BEFORE INSERT 
ORDER 1 ON "eas"."eas_x_grpusr"
REFERENCING NEW AS "new_name" 
FOR EACH ROW BEGIN
  SET "new_name"."c_user" = (select "c_user" from "eas"."eas_users" 
      where "eas_users"."id_eas_users" = "new_name"."id_eas_users");
  SET "new_name"."c_group" = (select "c_group" from "eas"."eas_usrgrp"
      where "eas_usrgrp"."id_eas_usrgrp" = "new_name"."id_eas_usrgrp");

END