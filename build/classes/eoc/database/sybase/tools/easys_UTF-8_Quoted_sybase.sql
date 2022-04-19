-- Generovane z SQL suboru programu pgModeler pre Sybase
 -- generatorom systemu Easys V2 - ERISS(Easys Reduced Instructions - SQL Set) 


/* odpoznamkovat, ked treba 
*/
CREATE USER "nzbd" IDENTIFIED BY 'nzbd';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "nzbd";
COMMENT ON USER "nzbd" IS 'Spr·vca systÈmu Nzbd';

CREATE USER "eas" IDENTIFIED BY 'eas';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "eas";
COMMENT ON USER "eas" IS 'Konto pre jadro systemu Easys';

CREATE USER "easys" IDENTIFIED BY 'easys';
GRANT DBA, RESOURCE, REMOTE DBA, BACKUP, VALIDATE, PROFILE, READFILE, READCLIENTFILE, WRITECLIENTFILE TO "easys";
COMMENT ON USER "easys" IS 'Spr·vca systÈmu Easys';

GRANT GROUP TO "eas";
GRANT MEMBERSHIP IN GROUP "eas" TO "nzbd";
GRANT MEMBERSHIP IN GROUP "eas" TO "easys";

 GRANT GROUP TO "eas";
 GRANT MEMBERSHIP IN GROUP "eas" TO "nzbd";
 CREATE TABLE "nzbd"."nz_cisVytahy"(
 	"id_nz_cisVytahy" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_PRAC" integer NOT NULL,
 	"id_nz_vyCisTypov" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_VCHOD" integer NOT NULL,
 	"i_rokVyroby" integer NOT NULL,
 	"c_evidCisPodlaServisOrg" char(16) NOT NULL,
 	"c_vyrobca" char(50) NOT NULL,
 	"c_nazPrevadzkovatel" char(50) NOT NULL,
 	"c_montazOrg" char(50) NOT NULL,
 	"c_AdrPrevadzkovatel" char(50) NOT NULL,
 	"c_typVytahu" char(16) NOT NULL,
 	"i_nosnost_kg" integer NOT NULL,
 	"i_nosnost_osob" smallint NOT NULL,
 	"n_rychlost" decimal(8,2) NOT NULL,
 	"c_systemRiadenia" char(30) NOT NULL,
 	"n_zdvyh" decimal(8,2) NOT NULL,
 	"n_priemerNosnProstr" decimal(8,2) NOT NULL,
 	"i_PocetNosnProstr" smallint NOT NULL,
 	"n_DlzkaNosnProstr" decimal(8,2) NOT NULL,
 	"c_konstrNosnProstr" char(30) NOT NULL,
 	"i_pocetStanic" smallint NOT NULL,
 	"i_PocetNakladisk" smallint NOT NULL,
 	"c_typ_OR" char(10) NOT NULL,
 	"c_napatieNapajVedenia" char(30) NOT NULL,
 	"i_Istic" decimal(5,2),
 	"c_VyrCisHnacMotora" char(10),
 	"c_typHnacMotora" char(16),
 	"i_RokVyrobyHnacMotora" integer NOT NULL,
 	"d_individVyskusania" date,
 	"d_zapisPasport" date,
 	"d_overovaciaSkuska" date,
 	"c_overSkuskuVykonal" char(50),
 	"c_odborPrehlVykonava" char(50),
 	"d_kolaudRozhodn" date,
 	"c_cisloKolaudRozhodn" char(16),
 	"c_kolaudRozhodnVydal" char(50),
 	"c_zvlastVynimkyPouz" varchar(256),
 	"c_vyrobneCislo" char(20) NOT NULL,
 	CONSTRAINT "id_nz_cisVytahy" PRIMARY KEY ("id_nz_cisVytahy")
 
 );
 CREATE UNIQUE INDEX "c_evidCisPodlaServisOrg" ON "nzbd"."nz_cisVytahy"
 	
 	(
 			"c_evidCisPodlaServisOrg" ASC 
 	);
 
 
 COMMENT ON TABLE "nzbd"."nz_cisVytahy" IS '»ÌselnÌk v˝ùahov˝ch strojov';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_rokVyroby" IS 'Rok v˝roby';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_evidCisPodlaServisOrg" IS 'EvidenËnÈ ËÌslo pridelenÈ servisnou organiz·ciou ( asi Id_pas ? )';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_nazPrevadzkovatel" IS 'Meno/n·zov prev·dzkovateæa';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_AdrPrevadzkovatel" IS 'Adresa (ulica,mesto) prev·dzkovateæa';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_typVytahu" IS 'Druh/typ v˝ùahu';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_nosnost_kg" IS 'Nosnosù v kilogramoch';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_nosnost_osob" IS 'Nosnosù - poËet osÙb';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."n_rychlost" IS 'R˝chlosù v meter/sekunda';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_systemRiadenia" IS 'SystÈm riadenia v˝ùahu (jednoduch˝,tlaËidlov˝, ...)';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."n_zdvyh" IS 'Zdvyh v metroch';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."n_priemerNosnProstr" IS 'Priemer nosn˝ch prostriedkov v metroch';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_PocetNosnProstr" IS 'PoËet nosn˝ch prostriedkov';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."n_DlzkaNosnProstr" IS 'Dæûka jednÈho nosnÈho prostriedku v metroch';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_konstrNosnProstr" IS 'Konötrukcia nosn˝ch prostriedkov podæa STN';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_pocetStanic" IS 'PoËet stanÌc';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_PocetNakladisk" IS 'PoËet n·kladÌsk';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_typ_OR" IS 'Typ OR ';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_napatieNapajVedenia" IS 'Nap‰tie nap·jacieho vedenia (popis: PEN,Volt·û,Hz)';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_Istic" IS 'IstiË (AmpÈry)';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_VyrCisHnacMotora" IS 'V˝robnÈ ËÌslo hnacieho elektromotora';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_typHnacMotora" IS 'Typ hnacieho elektromotora';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."i_RokVyrobyHnacMotora" IS 'Rok v˝roby hnacieho motora';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."d_individVyskusania" IS 'D·tum individu·lnho vsk˙öaia';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."d_zapisPasport" IS 'D·tum z·pisu v pasporte';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."d_overovaciaSkuska" IS 'D·tum overovacej sk˙öky';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_overSkuskuVykonal" IS 'Overovaciu sk˙öku vykonal (meno, funkcia, popis)';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_odborPrehlVykonava" IS 'OdbornÈ prehliadky vykon·va';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."d_kolaudRozhodn" IS 'D·tum vydania kolaudaËnÈho rozhodnutia';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_cisloKolaudRozhodn" IS 'Jednacie ËÌslo kolaudaËnÈho rozhodnutia';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_kolaudRozhodnVydal" IS 'Vyd·vateæ kolaudaËnÈho rozhodnutia';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_zvlastVynimkyPouz" IS 'Zvl·ötne v˝nimky pre pouûÌvanie v˝ùahu (naprÌklad v˝nimky z STN a podobne)';
 COMMENT ON COLUMN "nzbd"."nz_cisVytahy"."c_vyrobneCislo" IS 'V˝robnÈ ËÌslo v˝ùahu';
 CREATE TABLE "nzbd"."nz_vyPrOdbPr"(
 	"id_nz_vyPrOdbPr" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_nz_cisVytahy" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"n_ZnizenaNosnost_kg" decimal(8,2),
 	"d_Pouzivatelna_do" date,
 	"d_vykonania" date NOT NULL,
 	"c_zavady" varchar(1024) NOT NULL,
 	"b_prevadzkySchopna" smallint,
 	CONSTRAINT "id_nz_vy_protOdbPreh" PRIMARY KEY ("id_nz_vyPrOdbPr")
 
 );
 COMMENT ON TABLE "nzbd"."nz_vyPrOdbPr" IS 'OdbornÈ prehliadky v˝ùahov';
 COMMENT ON COLUMN "nzbd"."nz_vyPrOdbPr"."n_ZnizenaNosnost_kg" IS 'ZnÌûen· nosnosù v kg';
 COMMENT ON COLUMN "nzbd"."nz_vyPrOdbPr"."d_Pouzivatelna_do" IS 'D·tum, dokedy je v˝ùah povolenÈ pouûÌvaù ';
 COMMENT ON COLUMN "nzbd"."nz_vyPrOdbPr"."d_vykonania" IS 'D·tum vykonania odbornej prehliadky';
 COMMENT ON COLUMN "nzbd"."nz_vyPrOdbPr"."c_zavady" IS 'Z·vady k bodom';
 COMMENT ON COLUMN "nzbd"."nz_vyPrOdbPr"."b_prevadzkySchopna" IS 'Prevadzkyschopnosù v˝ùahu';
 CREATE TABLE "nzbd"."nz_vyCisSkKonDie"(
 	"id_nz_vyCisSkKonDie" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"i_PorCisKontrSkup" integer NOT NULL,
 	"c_NazovKotrSkup" smallint NOT NULL,
 	CONSTRAINT "id_nz_vyt_cis_skupKontrDiel" PRIMARY KEY ("id_nz_vyCisSkKonDie")
 
 );
 COMMENT ON TABLE "nzbd"."nz_vyCisSkKonDie" IS '»ÌselnÌk skupÌn kontrolovan˝ch dielov v˝ùahov';
 COMMENT ON COLUMN "nzbd"."nz_vyCisSkKonDie"."i_PorCisKontrSkup" IS 'PoradovÈ ËÌslo kotrolovanej skupiny v protokole odbornej prehliadky';
 COMMENT ON COLUMN "nzbd"."nz_vyCisSkKonDie"."c_NazovKotrSkup" IS 'N·zov kontrolovanej skupiny';
 CREATE TABLE "nzbd"."nz_vyCisKonDie"(
 	"id_nz_vyCisKonDie" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_nz_vyCisSkKonDie" integer DEFAULT AUTOINCREMENT,
 	"i_PorCisKontrSkup" integer NOT NULL,
 	"i_PorCisKontrDiel" integer NOT NULL,
 	"c_NazovKontrDiel" char(30) NOT NULL,
 	CONSTRAINT "id_nz_vyt_cis_KontrDiel" PRIMARY KEY ("id_nz_vyCisKonDie")
 
 );
 COMMENT ON TABLE "nzbd"."nz_vyCisKonDie" IS '»ÌselnÌk kontrolovan˝ch dielov v˝ùahov';
 COMMENT ON COLUMN "nzbd"."nz_vyCisKonDie"."i_PorCisKontrSkup" IS 'PoradovÈ ËÌslo kotrolovanej skupiny v protokole odbornej prehliadky';
 COMMENT ON COLUMN "nzbd"."nz_vyCisKonDie"."i_PorCisKontrDiel" IS 'PoradovÈ ËÌslo kontrolovanÈho dielu v˝ùahu';
 COMMENT ON COLUMN "nzbd"."nz_vyCisKonDie"."c_NazovKontrDiel" IS 'N·zov kontrolovanÈho dielu v˝ùahu';
 ALTER TABLE "nzbd"."nz_vyCisKonDie" ADD CONSTRAINT "nz_vyCisSkKonDie_fk" FOREIGN KEY ("id_nz_vyCisSkKonDie")
 REFERENCES "nzbd"."nz_vyCisSkKonDie" ("id_nz_vyCisSkKonDie") MATCH FULL
 ON DELETE SET NULL ON UPDATE CASCADE ;
 CREATE TABLE "nzbd"."nz_vyCisTypov"(
 	"id_nz_vyCisTypov" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_typVytahu" char(16) NOT NULL,
 	"c_popis" varchar(128),
 	CONSTRAINT "id_nz_vyt_cisTypov" PRIMARY KEY ("id_nz_vyCisTypov")
 
 );
 CREATE UNIQUE INDEX "c_typVytahu" ON "nzbd"."nz_vyCisTypov"
 	
 	(
 			"c_typVytahu" ASC 
 	);
 
 
 COMMENT ON TABLE "nzbd"."nz_vyCisTypov" IS '»ÌselnÌk typov v˝ùahov';
 COMMENT ON COLUMN "nzbd"."nz_vyCisTypov"."c_typVytahu" IS 'Druh-/typ v˝ùahu';
 ALTER TABLE "nzbd"."nz_vyPrOdbPr" ADD CONSTRAINT "nz_vyPrOdbPr_nz_cisVytahy_fk" FOREIGN KEY ("id_nz_cisVytahy")
 REFERENCES "nzbd"."nz_cisVytahy" ("id_nz_cisVytahy") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 ALTER TABLE "nzbd"."nz_cisVytahy" ADD CONSTRAINT "nz_vyCisTypov_fk" FOREIGN KEY ("id_nz_vyCisTypov")
 REFERENCES "nzbd"."nz_vyCisTypov" ("id_nz_vyCisTypov") MATCH FULL
  ON UPDATE CASCADE  ;
 CREATE TABLE "nzbd"."nz_vyProtOdbPreSk"(
 	"id_nz_vyProtOdbPreSk" integer DEFAULT AUTOINCREMENT,
 	"i_PorCisKontrSkup" integer NOT NULL,
 	"c_NazovKotrSkup" smallint NOT NULL,
 	"id_nz_vyPrOdbPr" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_nz_vy_protOdbrPreh_Sk" PRIMARY KEY ("id_nz_vyProtOdbPreSk")
 
 );
 CREATE UNIQUE INDEX "i_PorCisKontrSkup" ON "nzbd"."nz_vyProtOdbPreSk"
 	
 	(
 			"i_PorCisKontrSkup" ASC 
 	);
 
 
 COMMENT ON TABLE "nzbd"."nz_vyProtOdbPreSk" IS 'SkupÌny kontrolovan˝ch dielov v˝ùahov v protokole';
 COMMENT ON COLUMN "nzbd"."nz_vyProtOdbPreSk"."i_PorCisKontrSkup" IS 'PoradovÈ ËÌslo kotrolovanej skupiny v protokole odbornej prehliadky';
 COMMENT ON COLUMN "nzbd"."nz_vyProtOdbPreSk"."c_NazovKotrSkup" IS 'N·zov kontrolovanej skupiny';
 CREATE TABLE "nzbd"."nz_vyProtOdbPreDie"(
 	"id_nz_vyProtOdbPreDie" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"i_PorCisKontrDiel" integer NOT NULL,
 	"c_NazovKontrDiel" char(30) NOT NULL,
 	"i_PorCisKontrSkup" integer NOT NULL,
 	"id_nz_vyProtOdbPreSk" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_nz_vyt_protOdborPrehl_Diel" PRIMARY KEY ("id_nz_vyProtOdbPreDie")
 
 );
 CREATE UNIQUE INDEX "i_PorCisKontrDiel" ON "nzbd"."nz_vyProtOdbPreDie"
 	
 	(
 			"i_PorCisKontrDiel" ASC 
 	);
 CREATE UNIQUE INDEX "i_PorCis_SkupDiel" ON "nzbd"."nz_vyProtOdbPreDie"
 	
 	(
 			"i_PorCisKontrSkup" ASC ,
 			"i_PorCisKontrDiel" ASC 
 	);
 
 
 COMMENT ON TABLE "nzbd"."nz_vyProtOdbPreDie" IS '»ÌselnÌk kontrolovan˝ch dielov v˝ùahov';
 COMMENT ON COLUMN "nzbd"."nz_vyProtOdbPreDie"."i_PorCisKontrDiel" IS 'PoradovÈ ËÌslo kontrolovanÈho dielu v˝ùahu';
 COMMENT ON COLUMN "nzbd"."nz_vyProtOdbPreDie"."c_NazovKontrDiel" IS 'N·zov kontrolovanÈho dielu v˝ùahu';
 COMMENT ON COLUMN "nzbd"."nz_vyProtOdbPreDie"."i_PorCisKontrSkup" IS 'PoradovÈ ËÌslo kotrolovanej skupiny v protokole odbornej prehliadky';
 ALTER TABLE "nzbd"."nz_vyProtOdbPreDie" ADD CONSTRAINT "nz_vyProtOdbPreSk_fk" FOREIGN KEY ("id_nz_vyProtOdbPreSk")
 REFERENCES "nzbd"."nz_vyProtOdbPreSk" ("id_nz_vyProtOdbPreSk") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_cal_MH"(
 	"id_eas_cal_MH" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_calEvt" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_OdHod" char(5),
 	"c_DoHod" char(5),
 	"b_FullDay" smallint NOT NULL DEFAULT 0,
 	"i_FullDayOdHod" integer NOT NULL DEFAULT 7,
 	"i_FullDayDoHod" smallint NOT NULL DEFAULT 15,
 	CONSTRAINT "id_eas_cal_MH" PRIMARY KEY ("id_eas_cal_MH")
 
 );
 COMMENT ON TABLE "eas"."eas_cal_MH" IS 'Udalosti kalend·ra v min˙tach';
 COMMENT ON COLUMN "eas"."eas_cal_MH"."c_OdHod" IS '»as zaËiatku v tvare HH:MM, pokial to nie je bFullDay';
 COMMENT ON COLUMN "eas"."eas_cal_MH"."c_DoHod" IS '»as ukonËenia, pokial to nie je bFullDay';
 COMMENT ON COLUMN "eas"."eas_cal_MH"."b_FullDay" IS 'plati pre celÌ deÚ';
 CREATE TABLE "eas"."nz_VykPrac_DM"(
 	"id_nz_VykPrac_DM" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_nz_VykPrac_DM" PRIMARY KEY ("id_nz_VykPrac_DM")
 
 );
 COMMENT ON TABLE "eas"."nz_VykPrac_DM" IS 'MesaËnÈ v˝kony zamestnancov - po dÚoch';
 CREATE TABLE "eas"."nz_VykPrac_MR"(
 	"id_nz_VykPrac_MR" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_nz_VykPrac_MR" PRIMARY KEY ("id_nz_VykPrac_MR")
 
 );
 COMMENT ON TABLE "eas"."nz_VykPrac_MR" IS 'RoËnÈ v˝kony zamestnancov - po mesiacoch';
 CREATE DOMAIN kolkodza AS char(124);
 CREATE TABLE "nzbd"."nz_VykPracInp"(
 	"id_nz_VykPracInp" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_PRAC" integer NOT NULL,
 	"id_dokl" smallint,
 	"c_typDokl" varchar(5) NOT NULL,
 	CONSTRAINT "id_nz_VykPrac_input" PRIMARY KEY ("id_nz_VykPracInp")
 
 );
 COMMENT ON TABLE "nzbd"."nz_VykPracInp" IS 'Z·znamy v˝konov, vytvorenÈ na z·klade rÙznych dokladov';
 COMMENT ON COLUMN "nzbd"."nz_VykPracInp"."id_dokl" IS 'ID vety dokladu v tabuæke podæa typu dokladu (v prÌpade typu POZN m· hodnotu null)';
 COMMENT ON COLUMN "nzbd"."nz_VykPracInp"."c_typDokl" IS 'Typ dokladu, na ktorom v˝kon vznikol  (NAHL,OBJ,POZN,....)';
 CREATE TABLE "eas"."eas_objattr"(
 	"id_eas_objattr" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_objType" varchar(6) NOT NULL,
 	"c_objName" varchar(60) NOT NULL,
 	"c_attrName" varchar(60) NOT NULL,
 	"c_attrVal" varchar(60) NOT NULL,
 	"c_datatype" char(10) NOT NULL,
 	CONSTRAINT "id_eas_objattr" PRIMARY KEY ("id_eas_objattr")
 
 );
 CREATE UNIQUE INDEX x_txpe_name_attr ON "eas"."eas_objattr"
 	
 	(
 			"c_objType" ASC ,
 			"c_objName" ASC ,
 			"c_attrName" ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_objattr" IS 'Atrib˙ty systÈmov˝ch a in˝ch objektov - z·kladn·, nechr·nen· vezia pre beûnÈ objekty EaSys';
 COMMENT ON COLUMN "eas"."eas_objattr"."c_objType" IS 'Typ objektu (MDL(modul),ZAM(zamestnanec),VYT(vytah), a.t.d)';
 COMMENT ON COLUMN "eas"."eas_objattr"."c_objName" IS 'Identifik·tor objektu ( id_{tabulka}, alebo nieco jedineËnÈ )';
 COMMENT ON COLUMN "eas"."eas_objattr"."c_attrName" IS 'N·zov attrib˙tu';
 COMMENT ON COLUMN "eas"."eas_objattr"."c_attrVal" IS 'Hodota attrib˙tu objektu (NULL sa musi zapisat ako <NULL>)';
 COMMENT ON COLUMN "eas"."eas_objattr"."c_datatype" IS 'D·tov˝ typ hodnoty vlastrnosti objektu-INT,DATE,NUME,CHAR,BOOL (I/D/N/C/B)';
 ALTER TABLE "nzbd"."nz_vyProtOdbPreSk" ADD CONSTRAINT "nz_vyPrOdbPr_fk" FOREIGN KEY ("id_nz_vyPrOdbPr")
 REFERENCES "nzbd"."nz_vyPrOdbPr" ("id_nz_vyPrOdbPr") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE SEQUENCE nzbd.tblmasterid
 	INCREMENT BY 1
 	MINVALUE 0
 	MAXVALUE 2147483647
 	START WITH 1
 	CACHE 1
 	NO CYCLE
 	;
 CREATE TABLE "nzbd"."nz_ZaznSkusk"(
 	"id_nz_ZaznSkusk" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_nz_cisVytahy" integer DEFAULT AUTOINCREMENT,
 	"d_skusky" date NOT NULL,
 	"c_Vykonal" char(40) NOT NULL,
 	"c_Organizacia" char(16) NOT NULL,
 	"c_druh" char(10) NOT NULL,
 	CONSTRAINT "id_nz_Zazn_o_skuskach" PRIMARY KEY ("id_nz_ZaznSkusk")
 
 );
 COMMENT ON TABLE "nzbd"."nz_ZaznSkusk" IS 'Z·znamy o sk˙ökach';
 COMMENT ON COLUMN "nzbd"."nz_ZaznSkusk"."d_skusky" IS 'D·tum sk˙öky';
 COMMENT ON COLUMN "nzbd"."nz_ZaznSkusk"."c_Vykonal" IS 'Meno a titul osoby';
 COMMENT ON COLUMN "nzbd"."nz_ZaznSkusk"."c_Organizacia" IS 'Organiz·cia';
 ALTER TABLE "nzbd"."nz_ZaznSkusk" ADD CONSTRAINT "nz_ZaznSkusk_nz_cisVytahy_fk" FOREIGN KEY ("id_nz_cisVytahy")
 REFERENCES "nzbd"."nz_cisVytahy" ("id_nz_cisVytahy") MATCH FULL
 ON DELETE SET NULL ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_usrgrp"(
 	"id_eas_usrgrp" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_group" character(30) NOT NULL,
 	"c_popis" char(60),
 	"c_poznamka" char(250),
 	CONSTRAINT "id_eas_usrgrp" PRIMARY KEY ("id_eas_usrgrp")
 
 );
 CREATE UNIQUE INDEX x_c_group ON "eas"."eas_usrgrp"
 	
 	(
 			c_group ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_usrgrp" IS 'Skupiny uûÌvateæov - N·zvy uûÌvateæsk˝ch skupÌn';
 CREATE TABLE "eas"."eas_users"(
 	"id_eas_users" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_user" char(30) NOT NULL,
 	"c_popis" char(60),
 	"c_poznamka" char(250),
 	"b_sysadmin" integer DEFAULT 0,
 	CONSTRAINT "id_eas_users" PRIMARY KEY ("id_eas_users")
 
 );
 CREATE UNIQUE INDEX x_c_user ON "eas"."eas_users"
 	
 	(
 			c_user ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_users" IS 'UûÌvatelia - Kont· uûÌvateæov systÈmu Easys';
 COMMENT ON COLUMN "eas"."eas_users"."c_user" IS 'Prihlasovacie meno uûÌvateæa';
 COMMENT ON COLUMN "eas"."eas_users"."b_sysadmin" IS 'SystÈmov˝ administr·tor';
 CREATE TABLE "eas"."eas_attrib"(
 	"id_eas_attrib" integer NOT NULL DEFAULT AUTOINCREMENT,
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
 CREATE UNIQUE INDEX x_owner_obj_vlastn ON "eas"."eas_attrib"
 	
 	(
 			c_owner ASC ,
 			c_objekt ASC ,
 			c_vlastnost ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_attrib" IS 'Atrib˙ty systÈmov˝ch a in˝ch objektov - rozöÌren·,nastaviteln· verzia';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_owner" IS 'UûÌvateæ/objekt, ktorÈmu nastavenie vlastnosti danÈho objektu patrÌ';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_objekt" IS 'N·zov objektu, ktorÈmu aktu·lna vlastnosù patrÌ';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_vlastnost" IS 'N·zov vlastnosti objektu';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_hodnota" IS 'Nastaven· hodnota vlastnosti objektu';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_popis" IS 'Popis vlastnosti objektu';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_default" IS 'Preddefinovan· hodnota';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_datatype" IS 'D·tov˝ typ hodnoty vlastrnosti objektu-INT,DATE,NUME,CHAR,BOOL (I/D/N/C/B)';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_format" IS 'Form·t pre zad·vanie hodnoty';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_pomoc" IS '>>_runs.ose.ovladac-obsluh.prog. (musÌ mat 1 i-o par. typu char) !!_1,2,3,4-prÌpustnÈ hodnoty (prvÈ tri znaky ( >>_,!!_,... ) s˙ riad. sekv.)';
 COMMENT ON COLUMN "eas"."eas_attrib"."b_EnabForUsr" IS 'UûÌvatelsky nastaviteæn˝ attrib˙t';
 COMMENT ON COLUMN "eas"."eas_attrib"."b_hidden" IS 'Veta je skryt· pre uûÌvateæa';
 COMMENT ON COLUMN "eas"."eas_attrib"."b_RootOnly" IS 'T˙to vlastnosù mÙûe modifikovaù iba spr·vca';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_ValidateProg" IS 'Program pre valid·ciu zadanej hodnoty. MusÌ maù parametre: objekt, attrib˙t a hodnota attrib˙tu . (vr·ti "" keÔ je hodnota spr·vna)';
 COMMENT ON COLUMN "eas"."eas_attrib"."c_PermProg" IS 'Program na urËenie moûnosti opravy hodnoty atrib˙tu.Parametre: Meno_objektu, Meno_atributu.Vr·ti pr·zn˝ string, keÔ sa d· opravovaù.';
 CREATE TABLE "eas"."eas_crotim"(
 	"id_eas_crotim" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_cro_user" char(30) NOT NULL,
 	"d_last_date" date NOT NULL,
 	"i_last_time" integer NOT NULL,
 	"c_message" char(60) NOT NULL,
 	CONSTRAINT "id_eas_crotim" PRIMARY KEY ("id_eas_crotim")
 
 );
 COMMENT ON TABLE "eas"."eas_crotim" IS 'RelogovacÌ s˙bor ËasovaËa';
 COMMENT ON COLUMN "eas"."eas_crotim"."c_cro_user" IS 'Prihlasovacie meno uûÌvateæa';
 COMMENT ON COLUMN "eas"."eas_crotim"."d_last_date" IS 'PoslednÈ hl·senie od ËasovaËa';
 COMMENT ON COLUMN "eas"."eas_crotim"."i_last_time" IS '»as posl. prihl·senia v sekund·ch od polnoci';
 COMMENT ON COLUMN "eas"."eas_crotim"."c_message" IS 'PrÌpadn· spr·va od ËasovaËa';
 CREATE TABLE "eas"."eas_x_grpusr"(
 	"id_eas_x_grpusr" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_group" char(30) NOT NULL,
 	"c_user" char(30) NOT NULL,
 	"id_eas_usrgrp" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_users" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_eas_x_grpusr" PRIMARY KEY ("id_eas_x_grpusr")
 
 );
 CREATE INDEX x_x_c_group ON "eas"."eas_x_grpusr"
 	
 	(
 			c_group ASC 
 	);
 CREATE INDEX x_x_c_user ON "eas"."eas_x_grpusr"
 	
 	(
 			c_user ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_x_grpusr" IS 'Krossreferencia eas_usrgrp-eas_users';
 COMMENT ON COLUMN "eas"."eas_x_grpusr"."c_group" IS 'Skupiny uûÌvateæov - N·zvy uûÌvateæsk˝ch skupÌn';
 COMMENT ON COLUMN "eas"."eas_x_grpusr"."c_user" IS 'Prihlasovacie meno uûÌvateæa';
 ALTER TABLE "eas"."eas_x_grpusr" ADD CONSTRAINT "eas_usrgrp_fk" FOREIGN KEY ("id_eas_usrgrp")
 REFERENCES "eas"."eas_usrgrp" ("id_eas_usrgrp") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_cromsg"(
 	"id_eas_cromsg" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_cro_user" char(30) NOT NULL,
 	"c_odkoho" char(30) NOT NULL,
 	"d_msgdate" date NOT NULL,
 	"i_msgtime" integer NOT NULL,
 	"c_msgheader" char(60) NOT NULL,
 	"c_msgbody" char NOT NULL,
 	"c_msgtype" char(10) NOT NULL,
 	"d_readdate" date,
 	"_readtime" integer,
 	CONSTRAINT "id_eas_cromsg" PRIMARY KEY ("id_eas_cromsg")
 
 );
 COMMENT ON TABLE "eas"."eas_cromsg" IS 'Spr·vy pre uûÌvateæa cez ËasovaË';
 COMMENT ON COLUMN "eas"."eas_cromsg"."c_cro_user" IS 'Prihlasovacie meno uûÌvateæa';
 COMMENT ON COLUMN "eas"."eas_cromsg"."c_odkoho" IS 'Prihlasovacie meno odosielatela spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."d_msgdate" IS 'D·tum vzniku spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."i_msgtime" IS '»as vzniku spr·vy v sekund·ch od polnoci';
 COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgheader" IS 'HlaviËka spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgbody" IS 'Obsah spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."c_msgtype" IS 'Typ spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."d_readdate" IS 'D·tum preËÌtania spr·vy';
 COMMENT ON COLUMN "eas"."eas_cromsg"."_readtime" IS '»as preËÌtania spr·vy v sekund·ch od polnoci';
 CREATE TABLE "eas"."eas_permobj"(
 	"id_eas_permobj" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_appname" char(20) NOT NULL,
 	"c_typpermobj" char(10) NOT NULL,
 	"c_permobject" char(256) NOT NULL,
 	"c_popis" char(60) NOT NULL,
 	"id_parent_permobj" integer,
 	"c_defaultperm" char(6) NOT NULL,
 	CONSTRAINT "id_eas_permobj" PRIMARY KEY ("id_eas_permobj")
 
 );
 CREATE UNIQUE INDEX "x_app_parentID_prog" ON "eas"."eas_permobj"
 	
 	(
 			c_appname ASC ,
 			id_parent_permobj ASC ,
 			c_permobject ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_permobj" IS 'Objekty pre prÌstupovÈ pr·va';
 COMMENT ON COLUMN "eas"."eas_permobj"."c_appname" IS 'N·zov applik·cie, ku ktorej objekt prÌstupov˝ch pr·v patrÌ';
 COMMENT ON COLUMN "eas"."eas_permobj"."c_typpermobj" IS 'Typ objektu mÙûe byù: UI, PROGRAM, ZOSTAVA, MENU-ITEM, SUB-MENU,TABLE, FIELD,a.t.Ô.';
 COMMENT ON COLUMN "eas"."eas_permobj"."c_permobject" IS 'N·zov objektu prÌstupovÈho pr·va';
 COMMENT ON COLUMN "eas"."eas_permobj"."c_popis" IS 'Zrozumiteln˝ popis objektu pr·v ';
 COMMENT ON COLUMN "eas"."eas_permobj"."id_parent_permobj" IS 'Odkaz na nadraden˝ objekt prÌstupov˝ch pr·v';
 COMMENT ON COLUMN "eas"."eas_permobj"."c_defaultperm" IS 'PreddefinovanÈ prÌstupovÈ pr·vo k objektu ( (R(ead),N(ew)U(pdate)D(elete),C(all)) alebo niË)';
 ALTER TABLE "eas"."eas_x_grpusr" ADD CONSTRAINT "eas_users_fk" FOREIGN KEY ("id_eas_users")
 REFERENCES "eas"."eas_users" ("id_eas_users") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_perms"(
 	"id_eas_perms" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_permobj" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_permmask" char(250) NOT NULL DEFAULT '*',
 	"id_usrgrp_or_users" integer NOT NULL,
 	"c_usertype" char(5) NOT NULL,
 	"c_uname" char(30) NOT NULL,
 	"c_permobject" char(256) NOT NULL,
 	"c_appname" char(20) NOT NULL,
 	"c_permstr" char(6) NOT NULL,
 	CONSTRAINT "id_eas_perms" PRIMARY KEY ("id_eas_perms")
 
 );
 CREATE UNIQUE INDEX x_app_usr_obj ON "eas"."eas_perms"
 	
 	(
 			c_appname ASC ,
 			c_usertype ASC ,
 			c_uname ASC ,
 			c_permobject ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_perms" IS 'PridelenÈ prÌstupovÈ pr·va skupÌn alebo uûÌvateæov ';
 COMMENT ON COLUMN "eas"."eas_perms"."c_permmask" IS 'Maska ˙rovne prÌstupu (stred=410, stred like XY, a.t.Ô. alebo *=˙pln˝ prÌstup)';
 COMMENT ON COLUMN "eas"."eas_perms"."id_usrgrp_or_users" IS 'Id y tabuæky eas_usrgrp alebo eas_users';
 COMMENT ON COLUMN "eas"."eas_perms"."c_usertype" IS 'Typ uûÌvateæa (GROUP,USER)';
 COMMENT ON COLUMN "eas"."eas_perms"."c_uname" IS 'Meno uûÌvateæa/skupiny';
 COMMENT ON COLUMN "eas"."eas_perms"."c_permobject" IS 'N·zov objektu prÌstupovÈho pr·va';
 COMMENT ON COLUMN "eas"."eas_perms"."c_appname" IS 'N·zov applik·cie, ku ktorej objekt prÌstupov˝ch pr·v patrÌ';
 COMMENT ON COLUMN "eas"."eas_perms"."c_permstr" IS 'NastavenÈ prÌstupovÈ pr·vo (R(ead),N(ew)U(pdate)D(elete),C(all) alebo niË )';
 ALTER TABLE "eas"."eas_perms" ADD CONSTRAINT "eas_permobj_fk" FOREIGN KEY ("id_eas_permobj")
 REFERENCES "eas"."eas_permobj" ("id_eas_permobj") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_calEvt"(
 	"id_eas_calEvt" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_calAccount" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"i_perc_dokoncenosti" smallint NOT NULL DEFAULT 0,
 	"c_opakovanie" char NOT NULL DEFAULT 'Ne',
 	"c_pripomenutie" char(3) NOT NULL DEFAULT 'Nep',
 	"c_popis_ucelu" char(1024),
 	"c_typ_dokl_ulohy" char(2),
 	"id_dokl_ulohy" integer,
 	"c_nazov_ulohy" char(50) NOT NULL,
 	"c_tab_miesto_konania" char(40),
 	"id_miesto_konania" integer,
 	"c_miesto_konania" char(30) NOT NULL,
 	"c_kategoria" char(2) NOT NULL DEFAULT 'Ne',
 	"d_od_date" date NOT NULL,
 	"c_od_time" char(8) NOT NULL,
 	"d_do_date" date NOT NULL,
 	"c_do_time" char(8) NOT NULL,
 	"b_spojity_cas_usek" smallint NOT NULL DEFAULT 0,
 	"c_stav" char(2) NOT NULL DEFAULT 'Ne',
 	"d_ukoncenia" date,
 	"c_cas_ukoncenia" char(8),
 	CONSTRAINT "id_eas_calEvt" PRIMARY KEY ("id_eas_calEvt")
 
 );
 CREATE INDEX id_miesto_cas ON "eas"."eas_calEvt"
 	
 	(
 			id_miesto_konania ASC ,
 			d_od_date ASC ,
 			c_od_time ASC 
 	);
 CREATE INDEX miesto_cas ON "eas"."eas_calEvt"
 	
 	(
 			c_miesto_konania ASC ,
 			d_do_date ASC ,
 			c_do_time ASC 
 	);
 CREATE INDEX tab_miesto ON "eas"."eas_calEvt"
 	
 	(
 			c_tab_miesto_konania ASC 
 	);
 CREATE INDEX miesto ON "eas"."eas_calEvt"
 	
 	(
 			c_miesto_konania ASC 
 	);
 CREATE INDEX idx_account_cas ON "eas"."eas_calEvt"
 	
 	(
 			"id_eas_calAccount" ASC ,
 			d_od_date ASC ,
 			c_od_time ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_calEvt" IS 'Udalosti kalend·ra';
 COMMENT ON COLUMN "eas"."eas_calEvt"."i_perc_dokoncenosti" IS 'DokonËenosù ˙lohy v percent·ch (0 aû 100) %';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_opakovanie" IS 'Typ opakovania ˙lohyi (Ne),(De),(PD),(Ty),(2T),(Me),(Ro)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_pripomenutie" IS 'Okamih pripomenutia (Nep)ripomÌnaù, (##m)in,(##h)od,(##d)nÌ,(##t)˝ûdnov';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_popis_ucelu" IS 'Popis toho Ëo ˙loha rieöÌ';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_typ_dokl_ulohy" IS 'Typ dokladu, s ktor˝m je ˙loha spojen· (NH,VO,DO,FA, ...?)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."id_dokl_ulohy" IS 'ID dokladu, s ktor˝m je ˙loha spojen· (NH,VO,DO,FA, ...?)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_nazov_ulohy" IS 'N·zov ˙lohy/udalosti';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_tab_miesto_konania" IS 'Tabuæka, popisuj˙ca miesto konania (VCS,DOM,VCHOD,BYT)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."id_miesto_konania" IS 'id tabuæky miesta konania (VCS,VCHOD,DOM,BYT)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_miesto_konania" IS 'Miesto konania (vcs, dom, vchod, byt, ine ...)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_kategoria" IS 'KategÛria ˙lohy/udalosti ((Ne)specifikovna,(Vo)da,(Pl)yn,(Vy)tahy,(ES)U, ....';
 COMMENT ON COLUMN "eas"."eas_calEvt"."d_od_date" IS 'Rezervovane od dÚa';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_od_time" IS 'Od Ëasu (vo formate HH:MM(:SS))';
 COMMENT ON COLUMN "eas"."eas_calEvt"."d_do_date" IS 'Do dÚa';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_do_time" IS 'Do Ëasu (vo formate HH:MM(:SS))';
 COMMENT ON COLUMN "eas"."eas_calEvt"."b_spojity_cas_usek" IS 'Spojit˝ Ëasov˝ ˙sek (nemÙûe sa prekr˝vaù s inou ˙lohou)';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_stav" IS 'Stav ˙lohy (Ne)öpeifikovan˝ (Vy)ûaduje akciu,(Pr)ebieha, (Do)konËen·,(Zr)uöen·';
 COMMENT ON COLUMN "eas"."eas_calEvt"."d_ukoncenia" IS 'DeÚ ukonËenia/zruöenia';
 COMMENT ON COLUMN "eas"."eas_calEvt"."c_cas_ukoncenia" IS '»as ukonËenia/zruöenia ˙lohy';
 CREATE TABLE "nzbd"."nz_vr_poh_vozid"(
 	"id_nz_vr_poh_vozid" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_uni_cis" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_popis_ucelu" char(60),
 	"c_prevzal" char(30) NOT NULL,
 	"c_cas_prevzal" char(8) NOT NULL,
 	"d_den_prevzal" date NOT NULL,
 	"d_den_odovzdal" date,
 	"c_odovzdal" char(30),
 	"c_poznamky" varchar(255),
 	"c_cas_odovzdal" char(8),
 	"c_SPZ" char(10) NOT NULL,
 	"c_ucel" char(2) NOT NULL DEFAULT 'Sl',
 	CONSTRAINT "id_nz_vr_poh_vozid" PRIMARY KEY ("id_nz_vr_poh_vozid")
 
 );
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_popis_ucelu" IS 'Popis ˙Ëelu (adresa,pouûitie)';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_prevzal" IS 'Meno preberajucej osoby/zamestnanca';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_cas_prevzal" IS '»as prevzatia vo form·te HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."d_den_prevzal" IS 'DeÚ prevzatia';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."d_den_odovzdal" IS 'DeÚ odovzdania';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_odovzdal" IS 'Odovzd·vaj˙ca osoba/zamestnanec';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_poznamky" IS 'Pozn·mky k prÌpadu';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_cas_odovzdal" IS '»as odovzdania vo form·te HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_SPZ" IS 'SPZ vozidla';
 COMMENT ON COLUMN "nzbd"."nz_vr_poh_vozid"."c_ucel" IS '⁄Ëel pouûitia vozidla (Su)kromne,(Sl)uûobne';
 CREATE TABLE "nzbd"."nz_vr_navstevy"(
 	"id_nz_vr_navstevy" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"d_dna" date NOT NULL,
 	"i_PorCisZaDen" integer NOT NULL,
 	"c_priezvisko" char(30) NOT NULL,
 	"c_meno" char(30) NOT NULL,
 	"c_cas_od" char(8) NOT NULL,
 	"c_cas_do" char(8),
 	"c_navstiveny" char(30) NOT NULL,
 	"c_poznamky" char(255),
 	CONSTRAINT "id_vr_navstevy" PRIMARY KEY ("id_nz_vr_navstevy")
 
 );
 COMMENT ON TABLE "nzbd"."nz_vr_navstevy" IS 'Z·znamy o n·vötev·ch';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."d_dna" IS 'DeÚ n·vötevy';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."i_PorCisZaDen" IS 'PoradovÈ ËÌslo n·vötevy za deÚ';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_priezvisko" IS 'Priezvisko n·vötevnÌka';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_meno" IS 'KrstnÈ meno n·vötevnÌka';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_cas_od" IS '»as zaËatia n·vötevy v tvare HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_cas_do" IS '»as ukonËenia n·vötevy v tvare HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_navstiveny" IS 'Meno navötÌvenej osoby';
 COMMENT ON COLUMN "nzbd"."nz_vr_navstevy"."c_poznamky" IS 'Pozn·mky k n·vöteve ';
 CREATE TABLE "nzbd"."nz_vr_hlas_zavad"(
 	"id_nz_vr_hlas_zavad" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_prac" integer,
 	"c_cas_hlasenia" char(8) NOT NULL,
 	"c_subj_zavady" char NOT NULL,
 	"id_subj_zavady" integer NOT NULL,
 	"c_popis_zavady" char(60) NOT NULL,
 	"c_poznamky" char(255) NOT NULL,
 	"c_nahlasil" char(30) NOT NULL,
 	"c_udrzbar" char(5) NOT NULL,
 	"c_kontakt" char(60) NOT NULL,
 	"c_stav_riesenia" char(2) NOT NULL DEFAULT 'Ne',
 	"d_den_vyriesenia" date,
 	"c_cas_vyriesenia" char(8),
 	"c_pozn_udrzbara" char(255),
 	"c_typ_zavady" char(2) NOT NULL,
 	"d_den_hlasenia" date NOT NULL,
 	CONSTRAINT "id_nz_vr_hlas_zavad" PRIMARY KEY ("id_nz_vr_hlas_zavad")
 
 );
 CREATE INDEX c_udrzbar_den_cas_hlasenia ON "nzbd"."nz_vr_hlas_zavad"
 	
 	(
 			c_udrzbar ASC ,
 			d_den_hlasenia ASC ,
 			c_cas_hlasenia ASC 
 	);
 CREATE INDEX c_udrzbar_den_cas_vyriesenia ON "nzbd"."nz_vr_hlas_zavad"
 	
 	(
 			c_udrzbar ASC ,
 			d_den_vyriesenia ASC ,
 			c_cas_vyriesenia ASC 
 	);
 
 
 COMMENT ON TABLE "nzbd"."nz_vr_hlas_zavad" IS 'Z·znamy o hl·sen˝ch z·vad·ch';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_cas_hlasenia" IS '»as hl·senia z·vady v tvare HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_subj_zavady" IS 'Subjekt z·vady (D)om,(V)chod,(B)yt';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_popis_zavady" IS 'Popis z·vady';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_nahlasil" IS 'Meno hlasiacej osoby';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_udrzbar" IS 'OsobnÈ ËÌslo ˙drûbra';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_kontakt" IS 'Kontakt na hl·siacu osobu (telefÛn, mobil,mailadresa, a.t.d.)';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_stav_riesenia" IS 'Stav riesenia (Ne)rieöen· (Hl)·sen· oprav·rovi, (Ri)eöi sa to, (Od)str·nen·';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."d_den_vyriesenia" IS 'DeÚ vyrieöenia z·vady';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_cas_vyriesenia" IS '»as vyrieöenia z·vady v tvare HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_pozn_udrzbara" IS 'Pozn·mky ˙drûb·ra';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."c_typ_zavady" IS 'Typ z·vady (Pl)yn,(Vo)da,(Vy)tah,(In)y';
 COMMENT ON COLUMN "nzbd"."nz_vr_hlas_zavad"."d_den_hlasenia" IS 'DeÚ hl·senia z·vady';
 CREATE TABLE "nzbd"."nz_ESU"(
 	"id_nz_ESU" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_typ_el_spisu" char NOT NULL,
 	"c_typ_subjektu" char NOT NULL,
 	"id_subjektu" integer,
 	"c_ucel_spisu" char(255) NOT NULL,
 	"c_zalozil" char(30) NOT NULL,
 	"d_den_zalozil" date NOT NULL,
 	"c_cas_zalozil" char(8) NOT NULL,
 	"i_num_docs" integer NOT NULL DEFAULT 0,
 	"i_num_all_docs" integer NOT NULL DEFAULT 0,
 	CONSTRAINT "id_nz_el_spis" PRIMARY KEY ("id_nz_ESU")
 
 );
 COMMENT ON TABLE "nzbd"."nz_ESU" IS 'ElektronickÈ spisy ˙loh technickÈho ˙seku';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."c_typ_el_spisu" IS 'Typ elektronickÈho spisu (S)tavba, (O)prava, S(F)RB, ....';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."c_typ_subjektu" IS 'Typ subjektu (- D)om,(V)chod,(B)yt,(O)bjednavka';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."id_subjektu" IS 'ID subjektu z jeho evidenËnej tabuæky ';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."c_zalozil" IS 'UûÌvatelskÈ meno zakladaj˙cej osoby';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."d_den_zalozil" IS 'DeÚ zaloûenia spisu';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."c_cas_zalozil" IS '»as zaloûenia spisu v tvare HH:MM';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."i_num_docs" IS 'PoËet pripojen˝ch dokumentov';
 COMMENT ON COLUMN "nzbd"."nz_ESU"."i_num_all_docs" IS 'PoËet vöetk˝ch pripojen˝ch dokumentov (aj dokumenty ˙loh)';
 CREATE TABLE "nzbd"."nz_ESU_oprOsoby"(
 	"id_nz_ESU_oprOsoby" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_nz_ESU" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"id_eas_users" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_uroven_opravnenia" char NOT NULL DEFAULT '',
 	CONSTRAINT "id_nz_ESU_oprOsoby" PRIMARY KEY ("id_nz_ESU_oprOsoby")
 
 );
 COMMENT ON TABLE "nzbd"."nz_ESU_oprOsoby" IS 'Opr·vnenia osÙb k elektronick˝m spisom ˙loh';
 COMMENT ON COLUMN "nzbd"."nz_ESU_oprOsoby"."c_uroven_opravnenia" IS '⁄roveÚ prÌstupu k elektronickÈmu spisu (U)pdate+(D)elete + (R)ead+(P)rint aleno (A)all';
 ALTER TABLE "nzbd"."nz_ESU_oprOsoby" ADD CONSTRAINT "nz_ESU_fk" FOREIGN KEY ("id_nz_ESU")
 REFERENCES "nzbd"."nz_ESU" ("id_nz_ESU") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 ALTER TABLE "nzbd"."nz_ESU_oprOsoby" ADD CONSTRAINT "eas_users_fk" FOREIGN KEY ("id_eas_users")
 REFERENCES "eas"."eas_users" ("id_eas_users") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "nzbd"."nz_ESU_ulohy"(
 	"id_nz_ESU_ulohy" integer,
 	"id_nz_ESU" integer DEFAULT AUTOINCREMENT,
 	"i_cis_kroku_v_ESU" integer NOT NULL,
 	"c_krok" char(30) NOT NULL,
 	"i_num_docs" integer NOT NULL DEFAULT 0,
 	"c_pozn_k_ulohe" varchar(1024) NOT NULL,
 	"c_stav" char NOT NULL DEFAULT 'N',
 	"c_pozn_vybavovanie" varchar(1024) NOT NULL,
 	CONSTRAINT "id_nz_ESU_zozn_krokov" PRIMARY KEY ("id_nz_ESU_ulohy")
 
 );
 COMMENT ON TABLE "nzbd"."nz_ESU_ulohy" IS 'Zoznam krokov v r·mci elektronickÈho spisu';
 COMMENT ON COLUMN "nzbd"."nz_ESU_ulohy"."i_cis_kroku_v_ESU" IS 'PoradovÈ ËÌslo kroku v elektronickom spise';
 COMMENT ON COLUMN "nzbd"."nz_ESU_ulohy"."i_num_docs" IS 'PoËet pripojen˝ch dokumentov/prÌloh (dokumenty XLS,DOC,PDF, a.t.Ô.)';
 COMMENT ON COLUMN "nzbd"."nz_ESU_ulohy"."c_pozn_k_ulohe" IS 'Pozn·mky k ˙lpohe, nes˙visiace s vybavovanÌm';
 COMMENT ON COLUMN "nzbd"."nz_ESU_ulohy"."c_stav" IS 'Stav vybavovania  - (N)evybavuje sa,(R)ieöÌ sa, (Uzavret˝)';
 COMMENT ON COLUMN "nzbd"."nz_ESU_ulohy"."c_pozn_vybavovanie" IS 'Pozn·mky, s˙visiace s vybavovanÌm';
 ALTER TABLE "nzbd"."nz_ESU_ulohy" ADD CONSTRAINT "nz_ESU_fk" FOREIGN KEY ("id_nz_ESU")
 REFERENCES "nzbd"."nz_ESU" ("id_nz_ESU") MATCH FULL
 ON DELETE SET NULL ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_uni_cis"(
 	"id_eas_uni_cis" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_typ_cis" char(8) NOT NULL,
 	"c_skratka" char(16) NOT NULL,
 	"c_naz_pol_cis" char(30) NOT NULL,
 	"c_popis" char(60),
 	"c_metadata" char(4096),
 	"id_eas_uni_cis_def" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_domus_table" char(60),
 	"id_domus_table" integer,
 	CONSTRAINT "id_eas_uni_cis" PRIMARY KEY ("id_eas_uni_cis")
 
 );
 CREATE UNIQUE INDEX x_c_typ_c_skratka ON "eas"."eas_uni_cis"
 	
 	(
 			c_typ_cis ASC ,
 			c_skratka ASC 
 	);
 CREATE UNIQUE INDEX x_c_typ_c_naz_pol_cis ON "eas"."eas_uni_cis"
 	
 	(
 			c_typ_cis ASC ,
 			c_skratka ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_uni_cis" IS 'Univerz·lny ËÌselnÌk pre rÙzne skupiny poloûiek, rozlÌöenÈ hodnotou typu ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis"."c_typ_cis" IS 'Typ ËÌselnÌka (sl˙ûi na filtrovanie konkrÈtnÈho ËÌselnÌka)';
 COMMENT ON COLUMN "eas"."eas_uni_cis"."c_skratka" IS 'Skratka/identifik·tor poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis"."c_naz_pol_cis" IS 'N·zov poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis"."c_metadata" IS 'Meta ˙daj, spracovan˝ dekompozÌtorom do ötrukt˙ry poæa parametrov';
 CREATE TABLE "eas"."eas_calAccount"(
 	"id_eas_calAccount" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_accountOwnerType" char(8) NOT NULL,
 	"c_owner_table" char(60) NOT NULL,
 	"id_c_owner_table" integer NOT NULL,
 	"c_owner_name" char(30) NOT NULL,
 	"c_owner_pozn" char(60) NOT NULL,
 	CONSTRAINT "id_eas_calAccount" PRIMARY KEY ("id_eas_calAccount")
 
 );
 COMMENT ON TABLE "eas"."eas_calAccount" IS 'UûÌvatelskÈ konto pre kalend·r-rozhranie';
 COMMENT ON COLUMN "eas"."eas_calAccount"."c_accountOwnerType" IS 'Typ majiteæa ˙Ëtu (V)ozidlo, (U)ûÌvateæ,(E)SU, (Z)amestnanec...';
 COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_table" IS 'Tabuæka, eviduj˙ca majiteæa ˙Ëtu ';
 COMMENT ON COLUMN "eas"."eas_calAccount"."id_c_owner_table" IS 'Identifik·tor z tabuæky,eviduj˙cej majiteæa ˙Ëtu';
 COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_name" IS 'Meno uûÌvateæa konta (NaprÌklad: Dr·fiov· M·ria, NZ498AF)';
 COMMENT ON COLUMN "eas"."eas_calAccount"."c_owner_pozn" IS 'Pozn·mka k majiteæovi konta';
 ALTER TABLE "eas"."eas_calEvt" ADD CONSTRAINT "eas_calAccount_fk" FOREIGN KEY ("id_eas_calAccount")
 REFERENCES "eas"."eas_calAccount" ("id_eas_calAccount") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 ALTER TABLE "nzbd"."nz_vr_poh_vozid" ADD CONSTRAINT "eas_uni_cis_fk" FOREIGN KEY ("id_eas_uni_cis")
 REFERENCES "eas"."eas_uni_cis" ("id_eas_uni_cis") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_uni_cis_def"(
 	"id_eas_uni_cis_def" integer NOT NULL DEFAULT AUTOINCREMENT,
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
 CREATE UNIQUE INDEX x_c_typ_cis ON "eas"."eas_uni_cis_def"
 	
 	(
 			c_typ_cis ASC 
 	);
 
 
 COMMENT ON TABLE "eas"."eas_uni_cis_def" IS 'DefinÌcie povolen˝ch univerz·lnych ËÌselnÌkov';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_typ_cis" IS 'Typ ËÌselnÌka (sl˙ûi na filtrovanie konkrÈtnÈho ËÌselnÌka)';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_nazov_cis" IS 'N·zov ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_popis_cis" IS 'PodrobnejöÌ popis urËenia ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_skrat_pol_label" IS 'N·zov stæpca skratky poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_skrat_pol_length" IS 'PoËet povolen˝ch pÌsmen v skratke poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_naz_pol_label" IS 'N·zov stæpca n·zvu poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_naz_pol_length" IS 'PoËet povolen˝ch pÌsmen v n·zve poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."c_popis_pol_label" IS 'N·zov popisu poloûky ËÌselnÌka';
 COMMENT ON COLUMN "eas"."eas_uni_cis_def"."i_popis_pol_length" IS 'PoËet povolen˝ch pÌsmen n·zou popisu poloûky ËÌselnÌka';
 ALTER TABLE "eas"."eas_uni_cis" ADD CONSTRAINT "eas_uni_cis_def_fk" FOREIGN KEY ("id_eas_uni_cis_def")
 REFERENCES "eas"."eas_uni_cis_def" ("id_eas_uni_cis_def") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
 CREATE TABLE "eas"."eas_LIBDOCS"(
 	"id_eas_LIBDOCS" integer NOT NULL DEFAULT AUTOINCREMENT,
 	"c_ownername" char(50) NOT NULL,
 	"id_owner" integer NOT NULL,
 	"c_docname" char(101) NOT NULL,
 	"c_filename" char(255) NOT NULL,
 	"i_platny" smallint NOT NULL DEFAULT 1,
 	CONSTRAINT "id_eas_LIBDOCS" PRIMARY KEY ("id_eas_LIBDOCS")
 
 );
 COMMENT ON TABLE "eas"."eas_LIBDOCS" IS 'Dokumenty systÈmu Easys';
 COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_ownername" IS 'ESU,TBL_nazovtabulky, USR_username, atd.';
 COMMENT ON COLUMN "eas"."eas_LIBDOCS"."id_owner" IS 'Identifik·tor owner objektu';
 COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_docname" IS 'N·zov s˙boru dokumentu';
 COMMENT ON COLUMN "eas"."eas_LIBDOCS"."c_filename" IS '⁄pln˝ n·zov dokumentu s cestou';
 COMMENT ON COLUMN "eas"."eas_LIBDOCS"."i_platny" IS 'Platnosù dokumentu (1-ano, 2-nie, 3-mazan˝)';
 CREATE INDEX idx_account_cas ON "eas"."eas_calEvt"
 	
 	(
 			"id_eas_calAccount" ASC ,
 			d_od_date ASC ,
 			c_od_time ASC 
 	);
 CREATE TABLE "eas_cp1"."eas_cal_HD"(
 	"id_eas_cal_MH" integer NOT NULL DEFAULT AUTOINCREMENT,
 	CONSTRAINT "id_eas_cal_MH" PRIMARY KEY ("id_eas_cal_MH")
 
 );
 COMMENT ON TABLE "eas_cp1"."eas_cal_HD" IS 'Udalosti kalend·ra v hodin·ch';
 ALTER TABLE "eas"."eas_cal_MH" ADD CONSTRAINT "eas_calEvt" FOREIGN KEY ("id_eas_calEvt")
 REFERENCES "eas"."eas_calEvt" ("id_eas_calEvt") MATCH FULL
 ON DELETE RESTRICT ON UPDATE CASCADE ;
