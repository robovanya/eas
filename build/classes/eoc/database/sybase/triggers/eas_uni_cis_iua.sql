CREATE TRIGGER "eas_uni_cis_iua" AFTER INSERT, UPDATE
--ALTER TRIGGER "eas_uni_cis_iua" AFTER INSERT, UPDATE
ORDER 1 ON "eas"."eas_uni_cis"
referencing old as old_buffer new as new_buffer
FOR EACH ROW /* WHEN( search_condition ) */
BEGIN

declare calAccountID integer default null;

if (new_buffer.c_typ_cis = 'CisVoz') then

   set calAccountID = (select id_eas_calAccount from eas_calAccount 
                        where id_c_owner_table = new_buffer.id_eas_uni_cis
                          and c_accountOwnerType = 'Voz');
   if (calAccountID is null) then 
	       insert into eas_calAccount (c_accountOwnerType,c_owner_table,id_c_owner_table,c_owner_name,c_owner_pozn)
               values ('Voz','eas_uni_cis',new_buffer.id_eas_uni_cis,new_buffer.c_skratka,new_buffer.c_naz_pol_cis);
   endif;

endif;

if (new_buffer.c_typ_cis = 'Udrzbari') then

   set calAccountID = (select id_eas_calAccount from eas_calAccount 
                        where id_c_owner_table = new_buffer.id_eas_uni_cis
                          and c_accountOwnerType = 'Udrzbar');
   if (calAccountID is null) then 
	       insert into eas_calAccount (c_accountOwnerType,c_owner_table,id_c_owner_table,c_owner_name,c_owner_pozn)
               values ('Udrzbar','eas_uni_cis',new_buffer.id_eas_uni_cis,new_buffer.c_skratka,new_buffer.c_naz_pol_cis);
   endif;

endif;

END