EaSys_EOC - PgModeler Bridge 
 -- Generovane z SQL suboru programu pgModeler pre Postgres generatorom systemu Easys V2 - ERISS (Easys Reduced Instructions SQL Set) 


/* odpoznamkovat, ked treba 
CREATE USER "eas" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "eas";
COMMENT ON USER "eas" IS 'Spravca systemu EaSys';
GRANT GROUP TO "eas";

CREATE USER "nzbd" IDENTIFIED BY '***';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "nzbd";
COMMENT ON USER "nzbd" IS 'Spravca systemu NZBD';
GRANT MEMBERSHIP IN GROUP "eas" TO "nzbd";
*/

-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- PostgreSQL version: 9.2
-- Project Site: pgmodeler.com.br
-- Model Author: ---

SET check_function_bodies = false;

-- ddl-end --

-- object: nzbd | type: ROLE --
	CREATE ROLE nzbd WITH
	SUPERUSER
	UNENCRYPTED PASSWORD 'nzbd';

COMMENT ON ROLE "nzbd" IS 'administrator tabuliek "nzbd"';

-- ddl-end --
-- ddl-end --

-- object: eas | type: ROLE --
	CREATE ROLE eas WITH
	UNENCRYPTED PASSWORD 'eas';

-- ddl-end --


-- Database creation must be done outside an multicommand file.
-- These commands were put in this file only for convenience.
-- -- object: easys | type: DATABASE --
-- CREATE DATABASE easys
-- 	TEMPLATE = template0
-- 	ENCODING = 'WIN1250'
-- 	TABLESPACE = pg_default
-- ;
-- -- ddl-end --
-- 

-- object: rest | type: SCHEMA --
CREATE SCHEMA rest;

ALTER SCHEMA rest OWNER TO postgres;

-- ddl-end --

-- object: ana | type: SCHEMA --
CREATE SCHEMA ana;

COMMENT ON SCHEMA "ana" IS 'Odkazovan� tabu�ky databazy Domus ';

-- ddl-end --
-- ddl-end --

-- object: eas | type: SCHEMA --
CREATE SCHEMA eas;

-- ddl-end --

SET search_path TO pg_catalog,public,rest,ana,eas;

-- ddl-end --

-- object: rest.kolkodza | type: DOMAIN --
CREATE DOMAIN rest.kolkodza AS char(124);

COMMENT ON DOMAIN "rest"."kolkodza" IS 'Kolko hod�n zap�sa� (31dni x hhmm = 124charcters)';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_objattr | type: TABLE --
CREATE TABLE "eas"."eas_objattr"(
	"id_eas_objattr" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_objType" varchar(6) NOT NULL,
	"c_objName" varchar(60) NOT NULL,
	"c_attrName" varchar(60) NOT NULL,
	"c_attrVal" varchar(60) NOT NULL,
	"c_datatype" char(10) NOT NULL,
	CONSTRAINT "id_eas_objattr" PRIMARY KEY ("id_eas_objattr")

);

-- ddl-end --
-- object: x_txpe_name_attr | type: INDEX --
CREATE UNIQUE INDEX x_txpe_name_attr ON "eas"."eas_objattr"
	USING btree
	(
			"c_objType" ASC NULLS LAST,
			"c_objName" ASC NULLS LAST,
			"c_attrName" ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_objattr" IS 'Atrib�ty syst�mov�ch a in�ch objektov - z�kladn�, nechr�nen� vezia pre be�n� objekty EaSys';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_objattr"."c_objType" IS 'Typ objektu (MDL(modul),ZAM(zamestnanec),VYT(vytah), a.t.d)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_objattr"."c_objName" IS 'Identifik�tor objektu ( id_{tabulka}, alebo nieco jedine�n� )';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_objattr"."c_attrName" IS 'N�zov attrib�tu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_objattr"."c_attrVal" IS 'Hodota attrib�tu objektu (NULL sa musi zapisat ako <NULL>)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_objattr"."c_datatype" IS 'D�tov� typ hodnoty vlastrnosti objektu-INT,DATE,NUME,CHAR,BOOL (I/D/N/C/B)';

-- ddl-end --
-- ddl-end --

-- object: rest.tblmasterid | type: SEQUENCE --
CREATE SEQUENCE rest.tblmasterid
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: eas.eas_usrgrp | type: TABLE --
CREATE TABLE "eas"."eas_usrgrp"(
	"id_eas_usrgrp" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_group" character(30) NOT NULL,
	"c_popis" char(60),
	"c_poznamka" char(250),
	CONSTRAINT "id_eas_usrgrp" PRIMARY KEY ("id_eas_usrgrp")

);

-- ddl-end --
-- object: x_c_group | type: INDEX --
CREATE UNIQUE INDEX x_c_group ON "eas"."eas_usrgrp"
	USING btree
	(
			c_group ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_usrgrp" IS 'Skupiny u��vate�ov - N�zvy u��vate�sk�ch skup�n';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_users | type: TABLE --
CREATE TABLE "eas"."eas_users"(
	"id_eas_users" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_user" char(30) NOT NULL,
	"c_popis" char(60),
	"c_poznamka" char(250),
	"b_sysadmin" integer DEFAULT 0,
	CONSTRAINT "id_eas_users" PRIMARY KEY ("id_eas_users")

);

-- ddl-end --
-- object: x_c_user | type: INDEX --
CREATE UNIQUE INDEX x_c_user ON "eas"."eas_users"
	USING btree
	(
			c_user ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_users" IS 'U��vatelia - Kont� u��vate�ov syst�mu Easys';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_users"."c_user" IS 'Prihlasovacie meno u��vate�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_users"."b_sysadmin" IS 'Syst�mov� administr�tor';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_attrib | type: TABLE --
CREATE TABLE "eas"."eas_attrib"(
	"id_eas_attrib" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_owner" char(16) NOT NULL,
	"c_objekt" char(30) NOT NULL,
	"c_vlastnost" char(30) NOT NULL,
	"c_hodnota" char(30) NOT NULL,
	"c_popis" char(50) NOT NULL,
	"c_default" char(30) NOT NULL,
	"c_datatype" char(10) NOT NULL,
	"c_format" char(30) NOT NULL,
	"c_pomoc" char(100) NOT NULL,
	"b_EnabForUsr" smallint NOT NULL DEFAULT 0,
	"b_hidden" smallint NOT NULL DEFAULT 0,
	"b_RootOnly" smallint NOT NULL DEFAULT 0,
	"c_ValidateProg" char(100) NOT NULL,
	"c_PermProg" char(100) NOT NULL,
	CONSTRAINT "id_eas_attrib" PRIMARY KEY ("id_eas_attrib")

);

-- ddl-end --
-- object: x_owner_obj_vlastn | type: INDEX --
CREATE UNIQUE INDEX x_owner_obj_vlastn ON "eas"."eas_attrib"
	USING btree
	(
			c_owner ASC NULLS LAST,
			c_objekt ASC NULLS LAST,
			c_vlastnost ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_attrib" IS 'Atrib�ty syst�mov�ch a in�ch objektov - roz��ren�,nastaviteln� verzia';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_owner" IS 'U��vate�/objekt, ktor�mu nastavenie vlastnosti dan�ho objektu patr�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_objekt" IS 'N�zov objektu, ktor�mu aktu�lna vlastnos� patr�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_vlastnost" IS 'N�zov vlastnosti objektu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_hodnota" IS 'Nastaven� hodnota vlastnosti objektu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_popis" IS 'Popis vlastnosti objektu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_default" IS 'Preddefinovan� hodnota';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_datatype" IS 'D�tov� typ hodnoty vlastrnosti objektu-INT,DATE,NUME,CHAR,BOOL (I/D/N/C/B)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_format" IS 'Form�t pre zad�vanie hodnoty';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_pomoc" IS '>>_runs.ose.ovladac-obsluh.prog. (mus� mat 1 i-o par. typu char) !!_1,2,3,4-pr�pustn� hodnoty (prv� tri znaky ( >>_,!!_,... ) s� riad. sekv.)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."b_EnabForUsr" IS 'U��vatelsky nastavite�n� attrib�t';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."b_hidden" IS 'Veta je skryt� pre u��vate�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."b_RootOnly" IS 'T�to vlastnos� m��e modifikova� iba spr�vca';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_ValidateProg" IS 'Program pre valid�ciu zadanej hodnoty. Mus� ma� parametre: objekt, attrib�t a hodnota attrib�tu . (vr�ti "" ke� je hodnota spr�vna)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_attrib"."c_PermProg" IS 'Program na ur�enie mo�nosti opravy hodnoty atrib�tu.Parametre: Meno_objektu, Meno_atributu.Vr�ti pr�zn� string, ke� sa d� opravova�.';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_crotim | type: TABLE --
CREATE TABLE "eas"."eas_crotim"(
	"id_eas_crotim" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_cro_user" char(30) NOT NULL,
	"d_last_date" date NOT NULL,
	"i_last_time" bigint NOT NULL,
	"c_message" char(60) NOT NULL,
	"i_num_new_messages" integer NOT NULL DEFAULT 0,
	"i_num_unreaded_messages" integer NOT NULL DEFAULT 0,
	CONSTRAINT "id_eas_crotim" PRIMARY KEY ("id_eas_crotim")

);

-- ddl-end --
-- object: idx_c_cro_user | type: INDEX --
CREATE UNIQUE INDEX idx_c_cro_user ON "eas"."eas_crotim"
	USING btree
	(
			c_cro_user ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_crotim" IS 'Relogovac� s�bor �asova�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."c_cro_user" IS 'Prihlasovacie meno u��vate�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."d_last_date" IS 'Posledn� hl�senie od �asova�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."i_last_time" IS '�as posl. prihl�senia v sekund�ch od polnoci';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."c_message" IS 'Pr�padn� spr�va od �asova�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."i_num_new_messages" IS 'Po�et nov�ch spr�v';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_crotim"."i_num_unreaded_messages" IS 'Po�et nepre��tan�ch spr�v';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_x_grpusr | type: TABLE --
CREATE TABLE "eas"."eas_x_grpusr"(
	"id_eas_x_grpusr" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_group" char(30) NOT NULL,
	"c_user" char(30) NOT NULL,
	"id_eas_usrgrp" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"id_eas_users" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	CONSTRAINT "id_eas_x_grpusr" PRIMARY KEY ("id_eas_x_grpusr")

);

-- ddl-end --
-- object: x_x_c_group | type: INDEX --
CREATE INDEX x_x_c_group ON "eas"."eas_x_grpusr"
	USING btree
	(
			c_group ASC NULLS LAST
	);

-- ddl-end --

-- object: x_x_c_user | type: INDEX --
CREATE INDEX x_x_c_user ON "eas"."eas_x_grpusr"
	USING btree
	(
			c_user ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_x_grpusr" IS 'Krossreferencia eas_usrgrp-eas_users';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_x_grpusr"."c_group" IS 'Skupiny u��vate�ov - N�zvy u��vate�sk�ch skup�n';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_x_grpusr"."c_user" IS 'Prihlasovacie meno u��vate�a';

-- ddl-end --
-- ddl-end --

-- object: eas_usrgrp_fk | type: CONSTRAINT --
ALTER TABLE "eas"."eas_x_grpusr" ADD CONSTRAINT "eas_usrgrp_fk" FOREIGN KEY ("id_eas_usrgrp")
REFERENCES "eas"."eas_usrgrp" ("id_eas_usrgrp") MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;

-- ddl-end --


-- object: eas.eas_cromsg | type: TABLE --
CREATE TABLE "eas"."eas_cromsg"(
	"id_eas_cromsg" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_cro_user" char(30) NOT NULL,
	"c_odkoho" char(30) NOT NULL,
	"d_msgdate" date NOT NULL,
	"c_msgtime" char(8) NOT NULL,
	"c_msgheader" char(60) NOT NULL,
	"c_msgbody" char(4096) NOT NULL,
	"c_msgtype" char(10) NOT NULL,
	"d_readdate" date,
	"c_readtime" char(8),
	"d_loaddate" date,
	"c_loadtime" char(8),
	CONSTRAINT "id_eas_cromsg" PRIMARY KEY ("id_eas_cromsg")

);

-- ddl-end --
-- object: x_odkoho_usr_dt_time | type: INDEX --
CREATE INDEX x_odkoho_usr_dt_time ON "eas"."eas_cromsg"
	USING btree
	(
			c_odkoho ASC NULLS LAST,
			c_cro_user ASC NULLS LAST,
			d_msgdate ASC NULLS LAST,
			c_msgtime ASC NULLS LAST
	);

-- ddl-end --

-- object: x_usr_odkoho_dt_time | type: INDEX --
CREATE INDEX x_usr_odkoho_dt_time ON "eas"."eas_cromsg"
	USING btree
	(
			c_cro_user ASC NULLS LAST,
			c_odkoho ASC NULLS LAST,
			d_msgdate ASC NULLS LAST,
			c_msgtime ASC NULLS LAST
	);

-- ddl-end --

-- object: x_usr_dtload | type: INDEX --
CREATE INDEX x_usr_dtload ON "eas"."eas_cromsg"
	USING btree
	(
			c_cro_user ASC NULLS LAST,
			d_loaddate ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_cromsg" IS 'Spr�vy pre u��vate�a cez �asova�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_cro_user" IS 'Prihlasovacie meno u��vate�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_odkoho" IS 'Prihlasovacie meno odosielatela spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."d_msgdate" IS 'D�tum vzniku spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgtime" IS '�as vzniku spr�vy vo tvare HH:MM:SS';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgheader" IS 'Hlavi�ka spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgbody" IS 'Obsah spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgtype" IS 'Typ spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."d_readdate" IS 'D�tum pre��tania spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_readtime" IS '�as pre��tania spr�vy vo tvare HH:MM:SS';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."d_loaddate" IS 'D�tum na��tania spr�vy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_cromsg"."c_loadtime" IS '�as na��tania spr�vy vo tvare HH:MM:SS';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_permobj | type: TABLE --
CREATE TABLE "eas"."eas_permobj"(
	"id_eas_permobj" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_appname" char(20) NOT NULL,
	"c_typpermobj" char(10) NOT NULL,
	"c_permobject" char(256) NOT NULL,
	"c_popis" char(60) NOT NULL,
	"id_parent_permobj" integer,
	"c_defaultperm" char(6) NOT NULL,
	CONSTRAINT "id_eas_permobj" PRIMARY KEY ("id_eas_permobj")

);

-- ddl-end --
-- object: "x_app_parentID_prog" | type: INDEX --
CREATE UNIQUE INDEX "x_app_parentID_prog" ON "eas"."eas_permobj"
	USING btree
	(
			c_appname ASC NULLS LAST,
			id_parent_permobj ASC NULLS LAST,
			c_permobject ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_permobj" IS 'Objekty pre pr�stupov� pr�va';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."c_appname" IS 'N�zov applik�cie, ku ktorej objekt pr�stupov�ch pr�v patr�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."c_typpermobj" IS 'Typ objektu m��e by�: UI, PROGRAM, ZOSTAVA, MENU-ITEM, SUB-MENU,TABLE, FIELD,a.t.�.';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."c_permobject" IS 'N�zov objektu pr�stupov�ho pr�va';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."c_popis" IS 'Zrozumiteln� popis objektu pr�v ';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."id_parent_permobj" IS 'Odkaz na nadraden� objekt pr�stupov�ch pr�v';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_permobj"."c_defaultperm" IS 'Preddefinovan� pr�stupov� pr�vo k objektu ( (R(ead),N(ew)U(pdate)D(elete),C(all)) alebo ni�)';

-- ddl-end --
-- ddl-end --

-- object: eas_users_fk | type: CONSTRAINT --
ALTER TABLE "eas"."eas_x_grpusr" ADD CONSTRAINT "eas_users_fk" FOREIGN KEY ("id_eas_users")
REFERENCES "eas"."eas_users" ("id_eas_users") MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;

-- ddl-end --


-- object: eas.eas_perms | type: TABLE --
CREATE TABLE "eas"."eas_perms"(
	"id_eas_perms" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"id_eas_permobj" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_permmask" char(250) NOT NULL DEFAULT '*',
	"id_usrgrp_or_users" integer NOT NULL,
	"c_usertype" char(5) NOT NULL,
	"c_uname" char(30) NOT NULL,
	"c_permobject" char(256) NOT NULL,
	"c_appname" char(20) NOT NULL,
	"c_permstr" char(6) NOT NULL,
	CONSTRAINT "id_eas_perms" PRIMARY KEY ("id_eas_perms")

);

-- ddl-end --
-- object: x_app_usr_obj | type: INDEX --
CREATE UNIQUE INDEX x_app_usr_obj ON "eas"."eas_perms"
	USING btree
	(
			c_appname ASC NULLS LAST,
			c_usertype ASC NULLS LAST,
			c_uname ASC NULLS LAST,
			c_permobject ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_perms" IS 'Pridelen� pr�stupov� pr�va skup�n alebo u��vate�ov ';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_permmask" IS 'Maska �rovne pr�stupu (stred=410, stred like XY, a.t.�. alebo *=�pln� pr�stup)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."id_usrgrp_or_users" IS 'Id y tabu�ky eas_usrgrp alebo eas_users';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_usertype" IS 'Typ u��vate�a (GROUP,USER)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_uname" IS 'Meno u��vate�a/skupiny';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_permobject" IS 'N�zov objektu pr�stupov�ho pr�va';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_appname" IS 'N�zov applik�cie, ku ktorej objekt pr�stupov�ch pr�v patr�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_perms"."c_permstr" IS 'Nastaven� pr�stupov� pr�vo (R(ead),N(ew)U(pdate)D(elete),C(all) alebo ni� )';

-- ddl-end --
-- ddl-end --

-- object: eas_permobj_fk | type: CONSTRAINT --
ALTER TABLE "eas"."eas_perms" ADD CONSTRAINT "eas_permobj_fk" FOREIGN KEY ("id_eas_permobj")
REFERENCES "eas"."eas_permobj" ("id_eas_permobj") MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;

-- ddl-end --


-- object: eas."eas_calEvt" | type: TABLE --
CREATE TABLE "eas"."eas_calEvt"(
	"id_eas_calEvt" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"id_eas_calAccount" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"b_spojity_cas_usek" smallint NOT NULL DEFAULT 0,
	"c_owner" char(30) NOT NULL DEFAULT ' ',
	"i_id_owner" integer NOT NULL DEFAULT 0,
	"id_subjekt" integer NOT NULL DEFAULT 0,
	"c_stav" char(2) NOT NULL DEFAULT 'Ne',
	"d_ukoncenia" date,
	"c_cas_ukoncenia" char(8),
	"i_perc_dokoncenosti" smallint NOT NULL DEFAULT 0,
	"c_opakovanie" char(2) NOT NULL DEFAULT 'Ne',
	"c_pripomenutie" char(3) NOT NULL DEFAULT 'Nep',
	"c_popis_ucelu" char(1024) NOT NULL DEFAULT ' ',
	"c_typ_dokl_ulohy" char(2) NOT NULL DEFAULT ' ',
	"id_dokl_ulohy" integer NOT NULL DEFAULT 0,
	"b_celodenna_uloha" smallint NOT NULL DEFAULT 0,
	"c_tab_miesto_konania" char(40) NOT NULL DEFAULT ' ',
	"id_miesto_konania" integer NOT NULL DEFAULT 0,
	"c_miesto_konania" char(30) NOT NULL,
	"c_kategoria" char(2) NOT NULL DEFAULT 'Ne',
	"c_stavy" varchar(4096) NOT NULL DEFAULT ' ',
	"i_pripomenutie" smallint NOT NULL DEFAULT 0,
	"c_mj_pripomenutie" char NOT NULL DEFAULT 'm',
	"c_nazov_ulohy" char(50) NOT NULL,
	"d_od_date" date NOT NULL,
	"c_od_time" char(8) NOT NULL,
	"d_do_date" date NOT NULL,
	"c_do_time" char(8) NOT NULL,
	CONSTRAINT "id_eas_calEvt" PRIMARY KEY ("id_eas_calEvt")

);

-- ddl-end --
-- object: id_miesto_cas | type: INDEX --
CREATE INDEX id_miesto_cas ON "eas"."eas_calEvt"
	USING btree
	(
			id_miesto_konania ASC NULLS LAST,
			d_od_date ASC NULLS LAST,
			c_od_time ASC NULLS LAST
	);

-- ddl-end --

-- object: miesto_cas | type: INDEX --
CREATE INDEX miesto_cas ON "eas"."eas_calEvt"
	USING btree
	(
			c_miesto_konania ASC NULLS LAST,
			d_do_date ASC NULLS LAST,
			c_do_time ASC NULLS LAST
	);

-- ddl-end --

-- object: tab_miesto | type: INDEX --
CREATE INDEX tab_miesto ON "eas"."eas_calEvt"
	USING btree
	(
			c_tab_miesto_konania ASC NULLS LAST
	);

-- ddl-end --

-- object: miesto | type: INDEX --
CREATE INDEX miesto ON "eas"."eas_calEvt"
	USING btree
	(
			c_miesto_konania ASC NULLS LAST
	);

-- ddl-end --

-- object: idx_account_cas | type: INDEX --
CREATE INDEX idx_account_cas ON "eas"."eas_calEvt"
	USING btree
	(
			"id_eas_calAccount" ASC NULLS LAST,
			d_od_date ASC NULLS LAST,
			c_od_time ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_calEvt" IS 'Udalosti kalend�ra';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."b_spojity_cas_usek" IS 'Spojit� �asov� �sek (nem��e sa prekr�va� s inou �lohou)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_owner" IS 'Tvorca z�znamu (tbl_XY, alebo in� identifik�tor)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."i_id_owner" IS 'ID tvorcu z�znamu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_stav" IS 'Stav �lohy (Ne)�peifikovan� (Vy)�aduje akciu,(Pr)ebieha, (Do)kon�en�,(Zr)u�en�';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."d_ukoncenia" IS 'De� ukon�enia/zru�enia';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_cas_ukoncenia" IS '�as ukon�enia/zru�enia �lohy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."i_perc_dokoncenosti" IS 'Dokon�enos� �lohy v percent�ch (0 a� 100) %';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_opakovanie" IS 'Typ opakovania �lohyi (Ne),(De),(PD),(Ty),(2T),(Me),(Ro)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_pripomenutie" IS 'Okamih pripomenutia (Nep)ripom�na�, (##m)in,(##h)od,(##d)n�,(##t)��dnov';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_popis_ucelu" IS 'Popis toho �o �loha rie��';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_typ_dokl_ulohy" IS 'Typ dokladu, s ktor�m je �loha spojen� (NH,VO,DO,FA, ...?)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."id_dokl_ulohy" IS 'ID dokladu, s ktor�m je �loha spojen� (NH,VO,DO,FA, ...?)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."b_celodenna_uloha" IS 'Celodenn� �loha';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_tab_miesto_konania" IS 'Tabu�ka, popisuj�ca miesto konania (VCS,DOM,VCHOD,BYT)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."id_miesto_konania" IS 'id tabu�ky miesta konania (VCS,VCHOD,DOM,BYT)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_miesto_konania" IS 'Miesto konania (vcs, dom, vchod, byt, ine ...)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_kategoria" IS 'Kateg�ria �lohy/udalosti ((Ne)specifikovna,(Vo)da,(Pl)yn,(Vy)tahy,(ES)U, ....';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_stavy" IS 'Zaznamenan� stavy (%%;
pozn;
kto;
dt,cas#...#...)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."i_pripomenutie" IS 'doba pripomenutia pred za�iatkom udalosti/�lohy';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_mj_pripomenutie" IS 'Mern� jednotka pripomenutia ((m)inut/(h)odin/(d)ni/(t)��d�ov)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_nazov_ulohy" IS 'N�zov �lohy/udalosti';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."d_od_date" IS 'Rezervovane od d�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_od_time" IS 'Od �asu (vo formate HH:MM(:SS))';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."d_do_date" IS 'Do d�a';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calEvt"."c_do_time" IS 'Do �asu (vo formate HH:MM(:SS))';

-- ddl-end --
-- ddl-end --

-- object: eas.eas_uni_cis | type: TABLE --
CREATE TABLE "eas"."eas_uni_cis"(
	"id_eas_uni_cis" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"id_eas_uni_cis_def" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_differentation" char(10),
	"c_typ_cis" char(8) NOT NULL,
	"c_skratka" char(16) NOT NULL,
	"c_naz_pol_cis" char(30) NOT NULL,
	"c_popis" char(60),
	"c_metadata" char(4096),
	CONSTRAINT "id_eas_uni_cis" PRIMARY KEY ("id_eas_uni_cis")

);

-- ddl-end --
-- object: x_c_typ_c_skratka | type: INDEX --
CREATE UNIQUE INDEX x_c_typ_c_skratka ON "eas"."eas_uni_cis"
	USING btree
	(
			c_typ_cis ASC NULLS LAST,
			c_skratka ASC NULLS LAST
	);

-- ddl-end --

-- object: x_c_typ_c_naz_pol_cis | type: INDEX --
CREATE UNIQUE INDEX x_c_typ_c_naz_pol_cis ON "eas"."eas_uni_cis"
	USING btree
	(
			c_typ_cis ASC NULLS LAST,
			c_skratka ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_uni_cis" IS 'Univerz�lny ��seln�k pre r�zne skupiny polo�iek, rozl�en� hodnotou typu ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis"."c_differentation" IS 'Rozli�ovac� znak';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis"."c_typ_cis" IS 'Typ ��seln�ka (sl��i na filtrovanie konkr�tn�ho ��seln�ka)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis"."c_skratka" IS 'Skratka/identifik�tor polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis"."c_naz_pol_cis" IS 'N�zov polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis"."c_metadata" IS 'Meta �daj, spracovan� dekompoz�torom do �trukt�ry po�a parametrov';

-- ddl-end --
-- ddl-end --

-- object: eas."eas_calAccount" | type: TABLE --
CREATE TABLE "eas"."eas_calAccount"(
	"id_eas_calAccount" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_accountOwnerType" char(8) NOT NULL,
	"c_owner_table" char(60) NOT NULL,
	"id_c_owner_table" integer NOT NULL,
	"c_owner_name" char(30) NOT NULL,
	"c_owner_pozn" char(60) NOT NULL,
	CONSTRAINT "id_eas_calAccount" PRIMARY KEY ("id_eas_calAccount")

);

-- ddl-end --
COMMENT ON TABLE "eas"."eas_calAccount" IS 'U��vatelsk� konto pre kalend�r-rozhranie';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calAccount"."c_accountOwnerType" IS 'Typ majite�a ��tu (V)ozidlo, (U)��vate�,(E)SU, (Z)amestnanec...';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_table" IS 'Tabu�ka, eviduj�ca majite�a ��tu ';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calAccount"."id_c_owner_table" IS 'Identifik�tor z tabu�ky,eviduj�cej majite�a ��tu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_name" IS 'Meno u��vate�a konta (Napr�klad: Dr�fiov� M�ria, NZ498AF)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_pozn" IS 'Pozn�mka k majite�ovi konta';

-- ddl-end --
-- ddl-end --

-- object: "eas_calAccount_fk" | type: CONSTRAINT --
ALTER TABLE "eas"."eas_calEvt" ADD CONSTRAINT "eas_calAccount_fk" FOREIGN KEY ("id_eas_calAccount")
REFERENCES "eas"."eas_calAccount" ("id_eas_calAccount") MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;

-- ddl-end --


-- object: eas.eas_uni_cis_def | type: TABLE --
CREATE TABLE "eas"."eas_uni_cis_def"(
	"id_eas_uni_cis_def" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_typ_cis" char(8) NOT NULL,
	"c_nazov_cis" char(30) NOT NULL,
	"c_popis_cis" char(60) NOT NULL,
	"c_metadata" char(4096),
	"c_skrat_pol_label" char(20) NOT NULL,
	"i_skrat_pol_length" integer NOT NULL,
	"c_naz_pol_label" char(30) NOT NULL,
	"i_naz_pol_length" integer NOT NULL,
	"c_popis_pol_label" char(30) NOT NULL,
	"i_popis_pol_length" integer NOT NULL,
	CONSTRAINT "id_eas_uni_cis_def" PRIMARY KEY ("id_eas_uni_cis_def")

);

-- ddl-end --
-- object: x_c_typ_cis | type: INDEX --
CREATE UNIQUE INDEX x_c_typ_cis ON "eas"."eas_uni_cis_def"
	USING btree
	(
			c_typ_cis ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_uni_cis_def" IS 'Defin�cie povolen�ch univerz�lnych ��seln�kov';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_typ_cis" IS 'Typ ��seln�ka (sl��i na filtrovanie konkr�tn�ho ��seln�ka)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_nazov_cis" IS 'N�zov ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_popis_cis" IS 'Podrobnej�� popis ur�enia ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_skrat_pol_label" IS 'N�zov st�pca skratky polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_skrat_pol_length" IS 'Po�et povolen�ch p�smen v skratke polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_naz_pol_label" IS 'N�zov st�pca n�zvu polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_naz_pol_length" IS 'Po�et povolen�ch p�smen v n�zve polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_popis_pol_label" IS 'N�zov popisu polo�ky ��seln�ka';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_popis_pol_length" IS 'Po�et povolen�ch p�smen n�zou popisu polo�ky ��seln�ka';

-- ddl-end --
-- ddl-end --

-- object: eas_uni_cis_def_fk | type: CONSTRAINT --
ALTER TABLE "eas"."eas_uni_cis" ADD CONSTRAINT "eas_uni_cis_def_fk" FOREIGN KEY ("id_eas_uni_cis_def")
REFERENCES "eas"."eas_uni_cis_def" ("id_eas_uni_cis_def") MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;

-- ddl-end --


-- object: eas."eas_LIBDOCS" | type: TABLE --
CREATE TABLE "eas"."eas_LIBDOCS"(
	"id_eas_LIBDOCS" integer NOT NULL DEFAULT nextval('tblmasterid'::regclass),
	"c_ownername" char(50) NOT NULL,
	"id_owner" integer NOT NULL,
	"c_docname" char(101) NOT NULL,
	"c_srcfilename" char(255) NOT NULL,
	"c_trgfilename" varchar(255),
	"i_platny" smallint NOT NULL DEFAULT 1,
	"c_creationevent" char,
	CONSTRAINT "id_eas_LIBDOCS" PRIMARY KEY ("id_eas_LIBDOCS")

);

-- ddl-end --
-- object: id_owner_c_docname | type: INDEX --
CREATE UNIQUE INDEX id_owner_c_docname ON "eas"."eas_LIBDOCS"
	USING btree
	(
			c_ownername ASC NULLS LAST,
			id_owner ASC NULLS LAST,
			c_docname ASC NULLS LAST
	);

-- ddl-end --


COMMENT ON TABLE "eas"."eas_LIBDOCS" IS 'Dokumenty syst�mu Easys';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_ownername" IS 'ESU,TBL_nazovtabulky, USR_username, atd.';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."id_owner" IS 'Identifik�tor owner objektu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_docname" IS 'N�zov s�boru dokumentu';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_srcfilename" IS '�pln� n�zov zdrojov�ho dokumentu s cestou';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_trgfilename" IS '�pln� n�zov cielovej k�pie dokumentu s cestou';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."i_platny" IS 'Platnos� dokumentu (1-ano, 2-nie, 3-mazan�)';

-- ddl-end --
COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_creationevent" IS 'Udalos� vytvorenia - (C)opy/(M)ove';

-- ddl-end --
-- ddl-end --

-- object: idx_account_cas | type: INDEX --
CREATE INDEX idx_account_cas ON "eas"."eas_calEvt"
	USING btree
	(
			"id_eas_calAccount" ASC NULLS LAST,
			d_od_date ASC NULLS LAST,
			c_od_time ASC NULLS LAST
	);

-- ddl-end --

-- object: grant_6c6a3a4406 | type: PERMISSION --
GRANT CREATE,USAGE
			ON SCHEMA rest
			TO postgres;

;

-- ddl-end --


