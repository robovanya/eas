select substr(connection_property('MAX_STATEMENT_COUNT'),1,10) as maxstat,
       substr(connection_property('MAX_CURSOR_COUNT'),1,10) as maxcurs;

/*
delete from eas_perms; // where c_appname = 'Nzbd';
delete from eas_permobj; // where c_appname = 'Nzbd';
commit;
*/
//select id_eas_permobj as retval from eas_permobj where c_appname = 'EASYS' 
//and id_parent_permobj is null 
//and c_permobject = 'SYSTEM_EASYS' and c_typpermobj = 'PROGRAM'
//delete from eas_perms; // where c_appname = 'Nzbd';
//select connection_property('MAX_STATEMENT_COUNT');
/*
select id_eas_permobj as retval from eas_permobj where c_appname = 'EASYS' and id_parent_permobj = 657 
       and c_permobject = 'easys.system.prog.sysconfig.Pnl_licencia'and c_typpermobj = 'MENU-ITEM';

INSERT INTO eas_permobj (c_typpermobj, c_permobject, c_popis,id_parent_permobj,c_defaultperm
        , c_zapisal, c_zmenil, c_appname) 
VALUES ('MENU-ITEM','easys.system.prog.sysconfig.Pnl_licencia','MENU-ITEM_Licencia',657,'NUDP'
        ,'2015-11-30 17:31 rvanya','2015-11-30 17:31 rvanya','EASYS');
*/