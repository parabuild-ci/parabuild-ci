create cached table USERS (
  ID integer not null identity,
  NAME varchar(254) not null,
  EMAIL varchar(254) not null,
  PASSWORD varchar(254) not null,
  IS_ADMIN varchar(3) not null,
  TIMESTAMP bigint not null,
  unique (NAME)
);


create cached table BUILD_CONFIG (
  ID integer not null identity,
  NAME varchar(254) not null,
  RUNNER integer not null,
  SCHEDULE integer not null,
  SCM integer not null,
  ACCESS integer not null,
  STARTUP_STATUS integer not null,
  EMAIL_DOMAIN varchar(254) not null,
  SCM_EMAIL varchar(3) not null,
  TIMESTAMP bigint not null
);
create unique index BUILD_CONFIG_PK on BUILD_CONFIG(ID);
create unique index BUILD_CONFIG_AK1 on BUILD_CONFIG(NAME);

create cached table SYSTEM_PROPERTY (
  ID integer not null identity,
  NAME varchar(254) not null,
  VALUE varchar(2048),
  TIMESTAMP bigint not null,
  unique (ID),
  unique (NAME)
);
create unique index SYSTEM_PROPERTY_PK on SYSTEM_PROPERTY(ID);
create unique index SYSTEM_PROPERTY_AK1 on SYSTEM_PROPERTY(NAME);


create cached table SOURCE_CONTROL_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(254) not null,
  TIMESTAMP bigint not null,
  VALUE varchar(254),
  unique (ID),
  unique (BUILD_ID, NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index SOURCE_CONTROL_PROPERTY_PK on SOURCE_CONTROL_PROPERTY(ID);
create unique index SOURCE_CONTROL_PROPERTY_AK1 on SOURCE_CONTROL_PROPERTY(BUILD_ID,NAME);
create index SOURCE_CONTROL_PROPERTY_FK1 on SOURCE_CONTROL_PROPERTY(BUILD_ID);


create cached table SCHEDULE_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(254) not null,
  VALUE varchar(254),
  TIMESTAMP bigint not null,
  unique (ID),
  unique (BUILD_ID, NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table BUILD_ATTRIBUTE (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(254) not null,
  VALUE varchar(254),
  TIMESTAMP bigint not null,
  unique (ID),
  unique (BUILD_ID, NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index BUILD_ATTRIBUTE_PK on BUILD_ATTRIBUTE(ID);
create unique index BUILD_ATTRIBUTE_AK1 on BUILD_ATTRIBUTE(BUILD_ID, NAME);
create index BUILD_ATTRIBUTE_FK1 on BUILD_ATTRIBUTE(BUILD_ID);


create cached table LABEL_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(254) not null,
  VALUE varchar(254),
  TIMESTAMP bigint not null,
  unique (ID),
  unique (BUILD_ID, NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table VCS_USER_TO_EMAIL_MAP (
  BUILD_ID integer not null,
  USER_MAP_ID integer not null identity,
  USER_MAP_NAME varchar(254) not null,
  USER_MAP_EMAIL varchar(254),
  USER_MAP_DISABLED varchar(3) not null,
  USER_MAP_IM_ADDRESS varchar(50) null,
  USER_MAP_IM_TYPE integer not null,
  USER_MAP_TIMESTAMP bigint not null,
  unique (USER_MAP_ID),
  unique (BUILD_ID, USER_MAP_NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table BUILD_WATCHER (
  BUILD_ID integer not null,
  WATCHER_ID integer not null identity,
  WATCHER_EMAIL varchar(254) not null,
  WATCHER_LEVEL integer not null,
  WATCHER_DISABLED varchar(3) not null,
  WATCHER_IM_ADDRESS varchar(50) null,
  WATCHER_IM_TYPE integer not null,
  WATCHER_TIMESTAMP bigint not null,
  unique (WATCHER_ID),
  unique (BUILD_ID, WATCHER_EMAIL),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index BUILD_WATCHER_PK on BUILD_WATCHER(WATCHER_ID);
create index BUILD_WATCHER_FK1 on BUILD_WATCHER(BUILD_ID);


create cached table BUILD_SEQUENCE (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(254) not null,
  SCRIPT varchar(1024) not null,
  SUCCESS_PATTERNS varchar(1024),
  FAILURE_PATTERNS varchar(1024),
  TIMEOUT int not null,
  TIMESTAMP bigint not null,
  unique (ID),
  unique (BUILD_ID, NAME),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table SCHEDULE_ITEM (
  BUILD_ID integer not null,
  ID integer not null identity,
  HOUR varchar(3) not null,
  WEEK_DAY varchar(50) not null,
  MONTH_DAY varchar(50) not null,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index SCHEDULE_ITEM_PK on SCHEDULE_ITEM(ID);
create index SCHEDULE_ITEM_FK1 on SCHEDULE_ITEM(BUILD_ID);


create cached table BUILD_RUN (
  BUILD_ID integer not null,
  BUILD_RUN_ID integer not null identity,
  BUILD_RUN_NUMBER integer not null,
  BUILD_RUN_COMPLETE integer not null,
  BUILD_RUN_STARTED_AT datetime not null,
  BUILD_RUN_FINISHED_AT datetime,
  BUILD_RUN_RESULT  integer,
  BUILD_RUN_RESULT_DESCRIPTION varchar(1024),
  BUILD_RUN_LABEL  varchar(254) null,
  BUILD_RUN_TIMESTAMP bigint not null,
  unique (BUILD_RUN_ID),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index BUILD_RUN_PK on BUILD_RUN(BUILD_RUN_ID);
create unique index BUILD_RUN_IX1 on BUILD_RUN(BUILD_RUN_ID, BUILD_RUN_COMPLETE);
create index BUILD_RUN_FK1 on BUILD_RUN(BUILD_ID);


create cached table SEQUENCE_RUN (
  BUILD_RUN_ID integer not null,
  ID integer not null identity,
  NAME varchar(100) not null,
  RESULT  integer,
  RESULT_DESCRIPTION varchar(1024),
  STARTED_AT datetime not null,
  FINISHED_AT datetime not null,
  LABEL  varchar(254) null,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (BUILD_RUN_ID) references BUILD_RUN(BUILD_RUN_ID) ON DELETE CASCADE
);
create unique index SEQUENCE_RUN_PK on SEQUENCE_RUN(ID);


create cached table SEQUENCE_LOG (
  SEQUENCE_RUN_ID integer not null,
  ID integer not null identity,
  FILE varchar(256) not null,
  DESCRIPTION varchar(1024) not null,
  TYPE int not null,
  FOUND int not null,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (SEQUENCE_RUN_ID) references SEQUENCE_RUN(ID) ON DELETE CASCADE
);
create index SEQUENCE_LOG_IX1 on SEQUENCE_LOG(SEQUENCE_RUN_ID, TYPE);


create cached table CHANGELIST (
  BUILD_ID integer not null,
  ID integer not null identity,
  CREATED datetime not null,
  NEW char(1) not null,
  NUMBER varchar(10) null,
  EMAIL varchar(50) null,
  USER varchar(50) not null,
  CLIENT varchar(50) null,
  DESCRIPTION varchar(1024) not null,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
)
create unique index CHANGELIST_PK on CHANGELIST(ID);
create index CHANGELIST_FK1 on CHANGELIST(BUILD_ID);
create index CHANGELIST_IX1 on CHANGELIST(BUILD_ID, NEW);
create index CHANGELIST_IX2 on CHANGELIST(BUILD_ID, CREATED);

create cached table BUILD_RUN_PARTICIPANT (
  BUILD_RUN_ID integer not null,
  CHANGELIST_ID integer not null,
  ID integer not null identity,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (BUILD_RUN_ID) references BUILD_RUN(BUILD_RUN_ID) ON DELETE CASCADE,
  foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
)
create unique index BUILD_RUN_PARTICIPANT_PK on BUILD_RUN_PARTICIPANT(ID);
create index BUILD_RUN_PARTICIPANT_IX1 on BUILD_RUN_PARTICIPANT(BUILD_RUN_ID, CHANGELIST_ID);


create cached table CHANGE (
  CHANGELIST_ID integer not null,
  ID integer not null identity,
  REVISION varchar(100) not null,
  TYPE int not null,
  FILE_PATH varchar(1024) not null,
  TIMESTAMP bigint not null,
  unique (ID),
  foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
)
create unique index CHANGE_PK on CHANGE(ID);
create index CHANGE_FK1 on CHANGE(CHANGELIST_ID);


insert into USERS (ID, NAME, PASSWORD, IS_ADMIN, EMAIL, TIMESTAMP) values(1, 'admin', 'admin', 'Y', '', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (1, 'autobuild.schema.version', '2', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (2, 'autobuild.date.format', 'MM/dd/yyyy', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (3, 'autobuild.date.time.format', 'hh:mm a MM/dd/yyyy', 1);
