/*
update eas_crotim SET i_num_new_messages = 0
 WHERE id_eas_crotim = 1;
commit;
*/
begin
declare por char(2);
set por = '17';
INSERT INTO eas_cromsg 
   (c_cro_user,c_odkoho,d_msgdate,c_msgtime,c_msgheader,c_msgbody,c_msgtype)
values
   ('rvanya','spolak',null,null,'Hlavicka ' + por + '. spravy'
    ,'text ' + por + ' spravy pre roba od polaka' ,'USR');
commit;
end;
//   ('pklobucnik','rvanya','2017-01-30',5000,'Hlavicka prvej spravy','text prvej spravy' ,'USR');
/*
COMMENT ON COLUMN eas_crotim.i_num_new_messages IS 'Po�et nov�ch spr�v';
ALTER TABLE eas_crotim ADD i_num_unreaded_messages INTEGER DEFAULT 0;
COMMENT ON COLUMN eas_crotim.i_num_unreaded_messages IS 'Po�et nepre��tan�ch spr�v';
*/
