create cached table MANUAL_RUN_PARAMETER (
  ID integer not null identity,
  BUILD_ID integer not null,
  NAME varchar(100) not null,
  DESCRIPTION varchar(100) not null,
  VALUE varchar(1024),
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC1 unique (ID),
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC2 unique (BUILD_ID, NAME),
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
