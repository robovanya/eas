--select 'A', * from  "eas"."eas_usrgrp";
delete from eas.eas_usrgrp; -- where id_eas_usrgrp = 50;
commit;
select 'B', * from  "eas"."eas_usrgrp" where id_eas_usrgrp = 50;
--insert into eas.eas_usrgrp (c_group,c_popis,c_poznamka) values ('gruppb3','popiscok gruppy2','poznamka gruppa');
--commit;