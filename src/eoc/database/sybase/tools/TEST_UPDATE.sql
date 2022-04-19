CREATE INDEX "x_usr_dtload" ON "eas"."eas_cromsg" 
   ( "c_cro_user" ASC, "d_loaddate" ASC) IN "system";


//update eas_cromsg set d_readdate = '2017-02-02' where id_eas_cromsg = 26;
//update eas_cromsg set d_readdate = null         where id_eas_cromsg = 26;

 