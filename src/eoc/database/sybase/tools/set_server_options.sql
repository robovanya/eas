begin
/* no-limit */
set option PUBLIC.MAX_STATEMENT_COUNT = 0;
set option PUBLIC.MAX_CURSOR_COUNT = 0;
/*
set option PUBLIC.MAX_STATEMENT_COUNT = 250;
set option PUBLIC.MAX_CURSOR_COUNT = 250;
*/
/*
CREATE USER "easys" IDENTIFIED BY '***';
GRANT GROUP, DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, 
   PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "EASYS";
*/
end
