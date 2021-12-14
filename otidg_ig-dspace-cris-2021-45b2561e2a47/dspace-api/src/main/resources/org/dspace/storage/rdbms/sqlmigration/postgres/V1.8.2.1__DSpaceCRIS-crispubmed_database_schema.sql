--
-- The contents of this file are subject to the license and copyright
-- detailed in the LICENSE and NOTICE files at the root of the source
-- tree and available online at
--
-- http://www.dspace.org/license/
--

create table cris_pmc_citation (pubmedID int4 not null, numCitations int4 not null, timestampCreated timestamp, timestampLastModified timestamp, primary key (pubmedID));
create table cris_pmc_citation2record (cris_pmc_citation_pubmedID int4 not null, pmcRecords_pmcID int4 not null);
create table cris_pmc_citation_itemIDs (cris_pmc_citation_pubmedID int4 not null, element int4);
create table cris_pmc_record (pmcID int4 not null, authors text, publicationNote text, title text, primary key (pmcID));
create table cris_pmc_record_handles (cris_pmc_record_pmcID int4 not null, element varchar(255));
create table cris_pmc_record_pubmedIDs (cris_pmc_record_pmcID int4 not null, element int4);
alter table cris_pmc_citation2record add constraint FKF1B54E9DE4C0A379 foreign key (pmcRecords_pmcID) references cris_pmc_record;
alter table cris_pmc_citation2record add constraint FKF1B54E9D6A8E17F4 foreign key (cris_pmc_citation_pubmedID) references cris_pmc_citation;
alter table cris_pmc_citation_itemIDs add constraint FKC524AD2C6A8E17F4 foreign key (cris_pmc_citation_pubmedID) references cris_pmc_citation;
alter table cris_pmc_record_handles add constraint FKAFFDDCDCC016C9CD foreign key (cris_pmc_record_pmcID) references cris_pmc_record;
alter table cris_pmc_record_pubmedIDs add constraint FKFBE237FAC016C9CD foreign key (cris_pmc_record_pmcID) references cris_pmc_record;