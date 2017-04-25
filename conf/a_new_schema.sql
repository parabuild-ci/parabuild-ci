create cached table PROMOTION (
  ID integer not null identity,
  PROJECT_ID integer not null,
  NAME varchar(80) not null,
  DESCRIPTION varchar(1024) not null,
  DELETED char(1) not  null,
  TIMESTAMP bigint not null,
  constraint PROMOTION_UC1 unique (ID),
  constraint PROMOTION_UC2 unique (PROJECT_ID, NAME),
  constraint PROMOTION_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE
);
create index PROMOTION_IX1 on PROMOTION(PROJECT_ID, DELETED);


create cached table PROMOTION_ATTRIBUTE (
  PROMOTION_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint PROMOTION_ATTRIBUTE_UC1 unique (ID),
  constraint PROMOTION_ATTRIBUTE_UC2 unique (PROMOTION_ID, NAME),
  constraint PROMOTION_ATTRIBUTE_FC1 foreign key (PROMOTION_ID) references PROMOTION(ID) ON DELETE CASCADE
);
