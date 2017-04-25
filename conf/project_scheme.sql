create cached table PROJECT (
  ID integer not null identity,
  NAME varchar(80) not null,
  DESCRIPTION varchar(1024) not null,
  KEY varchar(80) not null,
  TYPE tinyint not null,
  DELETED char(1) not null,
  TIMESTAMP bigint not null,
  constraint PROJECT_UC1 unique (ID),
  constraint PROJECT_UC2 unique (KEY)
);
create index PROJECT_IX1 on PROJECT(NAME);
create index PROJECT_IX2 on PROJECT(TYPE, DELETED);


create cached table PROJECT_ATTRIBUTE (
  PROJECT_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint PROJECT_ATTRIBUTE_UC1 unique (ID),
  constraint PROJECT_ATTRIBUTE_UC2 unique (PROJECT_ID, NAME),
  constraint PROJECT_ATTRIBUTE_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE
);



create cached table PROJECT_BUILD (
  ID integer not null identity,
  PROJECT_ID integer not null,
  ACTIVE_BUILD_ID integer not null,
  constraint PROJECT_BUILD_UC1 unique (ID),
  constraint PROJECT_BUILD_UC2 unique (PROJECT_ID, ACTIVE_BUILD_ID),
  constraint PROJECT_BUILD_UC3 unique (ACTIVE_BUILD_ID),
  constraint RPROJECT_BUILD_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE,
  constraint RPROJECT_BUILD_FC2 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
)


create cached table PROJECT_RESULT_GROUP (
  ID integer not null identity,
  PROJECT_ID integer not null,
  RESULT_GROUP_ID integer not null,
  constraint PROJECT_RESULT_GROUP_UC1 unique (ID),
  constraint PROJECT_RESULT_GROUP_UC2 unique (PROJECT_ID, RESULT_GROUP_ID),
  constraint PROJECT_RESULT_GROUP_UC3 unique (RESULT_GROUP_ID),
  constraint PROJECT_RESULT_GROUP_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE,
  constraint PROJECT_RESULT_GROUP_FC2 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE
)
