create cached table USERS (
  ID integer not null identity,
  NAME varchar(100) not null,
  FNAME varchar(100) not null,
  EMAIL varchar(100) not null,
  PASSWORD varchar(100) not null,
  IS_ADMIN varchar(3) not null,
  ROLES varchar(254) not null,
  ENABLED char(1) not null,
  IM_TYPE tinyint not null,
  IM_ADDRESS varchar(100) not null,
  LDAP_AUTH char(1) not null,
  TIMESTAMP bigint not null,
  constraint USERS_UC1 unique (ID)
);
--
-- create unique index USERS_PK on USERS(ID);
-- create unique index USERS_AK1 on USERS(NAME);

create cached table USER_ATTRIBUTE (
  USER_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint USER_ATTRIBUTE_UC1 unique (ID),
  constraint USER_ATTRIBUTE_UC2 unique (USER_ID, NAME),
  constraint USER_ATTRIBUTE_FC1 foreign key (USER_ID) references USERS(ID) ON DELETE CASCADE
);
--
-- create unique index USER_ATTRIBUTE_AK1 on USER_ATTRIBUTE(USER_ID, NAME);
-- create unique index USER_ATTRIBUTE_PK on USER_ATTRIBUTE(ID);
-- create index USER_ATTRIBUTE_FK1 on USER_ATTRIBUTE(USER_ID);


create cached table GROUPS (
  ID integer not null identity,
  NAME varchar(254) not null,
  DESCR varchar(254) not null,
  ENABLED char(1) not null,
  ALLOWED_TO_CREATE_BUILD char(1) not null,
  ALLOWED_TO_DELETE_BUILD char(1) not null,
  ALLOWED_TO_START_BUILD char(1) not null,
  ALLOWED_TO_STOP_BUILD char(1) not null,
  ALLOWED_TO_UPDATE_BUILD char(1) not null,
  ALLOWED_TO_VIEW_BUILD char(1) not null,
  ALLOWED_TO_PUBLISH_RESULTS char(1) not null,
  ALLOWED_TO_DELETE_RESULTS char(1) not null,
  ALLOWED_TO_CREATE_RESULT_GROUP char(1) not null,
  ALLOWED_TO_DELETE_RESULT_GROUP char(1) not null,
  ALLOWED_TO_UPDATE_RESULT_GROUP char(1) not null,
  ALLOWED_TO_VIEW_RESULT_GROUP char(1) not null,
  TIMESTAMP bigint not null,
  constraint GROUPS_UC1 unique (ID),
  constraint GROUPS_UC2 unique (NAME)
);
--
-- create unique index GROUPS_AK1 on GROUPS(NAME);
-- create unique index GROUPS_PK on GROUPS(ID);


create cached table GROUP_ATTRIBUTE (
  GROUP_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint GROUP_ATTRIBUTE_UC1 unique (ID),
  constraint GROUP_ATTRIBUTE_UC2 unique (GROUP_ID, NAME),
  constraint GROUP_ATTRIBUTE_FC1 foreign key (GROUP_ID) references GROUPS(ID) ON DELETE CASCADE
);
--
-- create unique index GROUP_ATTRIBUTE_AK1 on GROUP_ATTRIBUTE(GROUP_ID, NAME);
-- create unique index GROUP_ATTRIBUTE_PK on GROUP_ATTRIBUTE(ID);
-- create index GROUP_ATTRIBUTE_FK1 on GROUP_ATTRIBUTE(GROUP_ID);


create cached table USER_GROUP (
  ID integer not null identity,
  USER_ID integer not null,
  GROUP_ID integer not null,
  constraint USER_GROUP_UC1 unique (ID),
  constraint USER_GROUP_UC2 unique (USER_ID, GROUP_ID),
  constraint USER_GROUP_FC1 foreign key (USER_ID) references USERS(ID) ON DELETE CASCADE,
  constraint USER_GROUP_FC2 foreign key (GROUP_ID) references GROUPS(ID) ON DELETE CASCADE
);
--
-- create unique index USER_GROUP_AK1 on USER_GROUP(USER_ID, GROUP_ID);
-- create unique index USER_GROUP_PK on USER_GROUP(ID);
-- create index USER_GROUP_FK1 on USER_GROUP(GROUP_ID);
-- create index USER_GROUP_FK2 on USER_GROUP(USER_ID);


create cached table SYSTEM_PROPERTY (
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(2048),
  TIMESTAMP bigint not null,
  constraint SYSTEM_PROPERTY_UC1 unique (ID),
  constraint SYSTEM_PROPERTY_UC2 unique (NAME)
);
--
-- create unique index SYSTEM_PROPERTY_AK1 on SYSTEM_PROPERTY(NAME);
-- create unique index SYSTEM_PROPERTY_PK on SYSTEM_PROPERTY(ID);


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


create cached table DISPLAY_GROUP (
    ID integer not null identity,
    NAME varchar(254) not null,
    DESCR varchar(254) not null,
    ENABLED char(1) not null,
    TIMESTAMP bigint not null,
    constraint DISPLAY_GROUPS_UC1 unique (ID),
    constraint DISPLAY_GROUPS_UC2 unique (NAME)
  );


create cached table DISPLAY_GROUP_ATTRIBUTE (
    DISPLAY_GROUP_ID integer not null,
    ID integer not null identity,
    NAME varchar(80) not null,
    VALUE varchar(1024),
    TIMESTAMP bigint not null,
    constraint DISPLAY_GROUP_ATTRIBUTE_UC1 unique (ID),
    constraint DISPLAY_GROUP_ATTRIBUTE_UC2 unique (DISPLAY_GROUP_ID, NAME),
    constraint DISPLAY_GROUP_ATTRIBUTE_FC1 foreign key (DISPLAY_GROUP_ID) references DISPLAY_GROUP(ID) ON DELETE CASCADE
  );


create cached table BUILD_CONFIG (
  ID integer not null identity,
  ORIGNL_CONFIG_ID integer null,
  NAME varchar(80) not null,
  SCHEDULE tinyint not null,
  SCM tinyint not null,
  ACCESS tinyint not null,
  EMAIL_DOMAIN varchar(80) not null,
  SCM_EMAIL varchar(3) not null,
  BUILDER_HOST varchar(80) not null,
  BUILDER_PASSWORD varchar(80) not null,
  SUBORDINATE char(1) not null,
  DISCRIMINATOR char(1) not null,
  TIMESTAMP bigint not null,
  constraint BUILD_CONFIG_UC1 unique (ID),
  constraint BUILD_CONFIG_UC2 unique (ID, DISCRIMINATOR)
);
--  NOTE: simeshev@parabuildci.org -> constraint BUILD_CONFIG_FC1 foreign key (ORIGNL_CONFIG_ID) references BUILD_CONFIG(ID) on delete set null


create cached table ACTIVE_BUILD (
  ID integer not null,
  STARTUP_STATUS integer not null,
  DELETED char(1) not null,
  SEQUENCE_NUMBER integer not null,
  TIMESTAMP bigint not null,
  constraint ACTIVE_BUILD_UC1 unique (ID),
  constraint ACTIVE_BUILD_UC2 unique (SEQUENCE_NUMBER),
  constraint ACTIVE_BUILD_FC1 foreign key (ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table ACTIVE_BUILD_ATTRIBUTE (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint ACTIVE_BUILD_ATTRIBUTE_UC1 unique (ID),
  constraint ACTIVE_BUILD_ATTRIBUTE_UC2 unique (ID, NAME),
  constraint ACTIVE_BUILD_ATTRIBUTE_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);
--
-- create unique index ACTIVE_BUILD_ATTRIBUTE_AK1 on ACTIVE_BUILD_ATTRIBUTE(ID, NAME);


create cached table PROJECT_BUILD (
  ID integer not null identity,
  PROJECT_ID integer not null,
  ACTIVE_BUILD_ID integer not null,
  constraint PROJECT_BUILD_UC1 unique (ID),
  constraint PROJECT_BUILD_UC2 unique (PROJECT_ID, ACTIVE_BUILD_ID),
  constraint PROJECT_BUILD_UC3 unique (ACTIVE_BUILD_ID),
  constraint PROJECT_BUILD_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE,
  constraint PROJECT_BUILD_FC2 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
)


create cached table BUILD_ACCESS (
  ID integer not null identity,
  GROUP_ID integer not null,
  BUILD_ID integer not null,
  TIMESTAMP bigint not null,
  constraint BUILD_ACCESS_UC1 unique (ID),
  constraint BUILD_ACCESS_UC2 unique (BUILD_ID, GROUP_ID),
  constraint BUILD_ACCESS_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE,
  constraint BUILD_ACCESS_FC2 foreign key (GROUP_ID) references GROUPS(ID) ON DELETE CASCADE
);
--
-- create unique index BUILD_ACCESS_AK1 on BUILD_ACCESS(BUILD_ID, GROUP_ID);
-- create unique index BUILD_ACCESS_PK on BUILD_ACCESS(ID);
-- create index BUILD_ACCESS_FK1 on BUILD_ACCESS(GROUP_ID);
-- create index BUILD_ACCESS_FK2 on BUILD_ACCESS(BUILD_ID);


create cached table DISPLAY_GROUP_BUILD (
    ID integer not null identity,
    DISPLAY_GROUP_ID integer not null,
    BUILD_ID integer not null,
    TIMESTAMP bigint not null,
    constraint DISPLAY_GROUP_BUILD_UC1 unique (ID),
    constraint DISPLAY_GROUP_BUILD_UC2 unique (BUILD_ID, DISPLAY_GROUP_ID),
    constraint DISPLAY_GROUP_BUILD_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE,
    constraint DISPLAY_GROUP_BUILD_FC2 foreign key (DISPLAY_GROUP_ID) references DISPLAY_GROUP(ID) ON DELETE CASCADE
  );


create cached table SUBORDINATE (
  ID integer not null identity,
  LEAD_ID integer not null,
  MANAGED_ID integer not null,
  constraint SUBORDINATE_UC1 unique (ID),
  constraint SUBORDINATE_FC1 foreign key (LEAD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE,
  constraint SUBORDINATE_FC2 foreign key (MANAGED_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index SUBORDINATE_PK on SUBORDINATE(ID);
-- TODO: simeshev - concider AK -- create unique index SUBORDINATE_FK1 on SUBORDINATE(MANAGED_ID);
-- create index SUBORDINATE_FK2 on SUBORDINATE(LEAD_ID);


create cached table SUBORDINATE_ATTRIBUTE (
  SUBORDINATE_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint SUBORDINATE_ATTRIBUTE_UC1 unique (ID),
  constraint SUBORDINATE_ATTRIBUTE_UC2 unique (SUBORDINATE_ID, NAME),
  constraint SUBORDINATE_ATTRIBUTE_FC1 foreign key (SUBORDINATE_ID) references SUBORDINATE(ID) ON DELETE CASCADE
);
--
-- create unique index SUBORDINATE_ATTRIBUTE_AK1 on SUBORDINATE_ATTRIBUTE(SUBORDINATE_ID, NAME);
-- create unique index SUBORDINATE_ATTRIBUTE_PK on SUBORDINATE_ATTRIBUTE(ID);
-- create index SUBORDINATE_ATTRIBUTE_FK1 on SUBORDINATE_ATTRIBUTE(SUBORDINATE_ID);


create cached table SOURCE_CONTROL_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  TIMESTAMP bigint not null,
  VALUE varchar(4096),
  constraint SOURCE_CONTROL_PROPERTY_UC1 unique (ID),
  constraint SOURCE_CONTROL_PROPERTY_UC2 unique (BUILD_ID, NAME),
  constraint SOURCE_CONTROL_PROPERTY_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index SOURCE_CONTROL_PROPERTY_AK1 on SOURCE_CONTROL_PROPERTY(BUILD_ID,NAME);
-- create unique index SOURCE_CONTROL_PROPERTY_PK on SOURCE_CONTROL_PROPERTY(ID);
-- create index SOURCE_CONTROL_PROPERTY_FK1 on SOURCE_CONTROL_PROPERTY(BUILD_ID);


create cached table SCM_PATH (
  BUILD_ID integer not null,
  ID integer not null identity,
  PATH varchar(1024) not null,
  TIMESTAMP bigint not null,
  constraint SCM_PATH_UC1 unique (ID),
  constraint SCM_PATH_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index SCM_PATH_PK on SCM_PATH(ID);
-- create index SCM_PATH_FK1 on SCM_PATH(BUILD_ID);


create cached table SCHEDULE_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint SCHEDULE_PROPERTY_UC1 unique (ID),
  constraint SCHEDULE_PROPERTY_UC2 unique (BUILD_ID, NAME),
  constraint SCHEDULE_PROPERTY_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index SCHEDULE_PROPERTY_AK1 on SCHEDULE_PROPERTY(BUILD_ID, NAME);
-- create unique index SCHEDULE_PROPERTY_PK on SCHEDULE_PROPERTY(ID);
-- create index SCHEDULE_PROPERTY_FK1 on SCHEDULE_PROPERTY(BUILD_ID);


create cached table BUILD_ATTRIBUTE (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint BUILD_ATTRIBUTE_UC1 unique (ID),
  constraint BUILD_ATTRIBUTE_UC2 unique (BUILD_ID, NAME),
  constraint BUILD_ATTRIBUTE_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index BUILD_ATTRIBUTE_AK1 on BUILD_ATTRIBUTE(BUILD_ID, NAME);
-- create unique index BUILD_ATTRIBUTE_PK on BUILD_ATTRIBUTE(ID);
-- create index BUILD_ATTRIBUTE_FK1 on BUILD_ATTRIBUTE(BUILD_ID);


create cached table LABEL_PROPERTY (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint LABEL_PROPERTY_UC1 unique (ID),
  constraint LABEL_PROPERTY_UC2 unique (BUILD_ID, NAME),
  constraint LABEL_PROPERTY_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index LABEL_PROPERTY_AK1 on LABEL_PROPERTY(BUILD_ID, NAME);
-- create unique index LABEL_PROPERTY_PK on LABEL_PROPERTY(ID);
-- create index LABEL_PROPERTY_FK1 on LABEL_PROPERTY(BUILD_ID);


create cached table LOG_CONFIG (
  ID integer not null identity,
  BUILD_ID integer not null,
  DESCRIPTION varchar(80) not null,
  PATH varchar(1024) not null,
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint LOG_CONFIG_UC1 unique (ID),
  constraint LOG_CONFIG_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index LOG_CONFIG_PK on LOG_CONFIG(ID);
-- create index LOG_CONFIG_FK1 on LOG_CONFIG(BUILD_ID);


create cached table LOG_CONFIG_PROPERTY (
  ID integer not null identity,
  LOG_CONFIG_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint LOG_CONFIG_PROPERTY_UC1 unique (ID),
  constraint LOG_CONFIG_PROPERTY_UC2 unique (LOG_CONFIG_ID, NAME),
  constraint LOG_CONFIG_PROPERTY_FC1 foreign key (LOG_CONFIG_ID) references LOG_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index LOG_CONFIG_PROPERTY_AK1 on LOG_CONFIG_PROPERTY(LOG_CONFIG_ID, NAME);
-- create unique index LOG_CONFIG_PROPERTY_PK on LOG_CONFIG_PROPERTY(ID);
-- create index LOG_CONFIG_PROPERTY_FK1 on LOG_CONFIG_PROPERTY(LOG_CONFIG_ID);


create cached table VCS_USER_TO_EMAIL_MAP (
  ID integer not null identity,
  BUILD_ID integer not null,
  USER_NAME varchar(254) not null,
  USER_EMAIL varchar(254),
  DISABLED varchar(3) not null,
  IM_ADDRESS varchar(100) null,
  IM_TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint VCS_USER_TO_EMAIL_MAP_UC1 unique (ID),
  constraint VCS_USER_TO_EMAIL_MAP_UC2 unique (BUILD_ID, USER_NAME),
  constraint VCS_USER_TO_EMAIL_MAP_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index VCS_USER_TO_EMAIL_MAP_AK1 on VCS_USER_TO_EMAIL_MAP(BUILD_ID, USER_NAME);
-- create unique index VCS_USER_TO_EMAIL_MAP_PK on VCS_USER_TO_EMAIL_MAP(ID);
-- create index VCS_USER_TO_EMAIL_MAP_FK1 on VCS_USER_TO_EMAIL_MAP(BUILD_ID);


create cached table BUILD_WATCHER (
  BUILD_ID integer not null,
  ID integer not null identity,
  EMAIL varchar(254) not null,
  LEVEL tinyint not null,
  DISABLED varchar(3) not null,
  IM_ADDRESS varchar(100) null,
  IM_TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint BUILD_WATCHER_UC1 unique (ID),
  constraint BUILD_WATCHER_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index BUILD_WATCHER_AK1 on BUILD_WATCHER(BUILD_ID, EMAIL);
-- create unique index BUILD_WATCHER_PK on BUILD_WATCHER(ID);
-- create index BUILD_WATCHER_FK1 on BUILD_WATCHER(BUILD_ID);


create cached table BUILD_SEQUENCE (
  BUILD_ID integer not null,
  ID integer not null identity,
  NAME varchar(50) not null,
  SCRIPT varchar(1024) not null,
  SUCCESS_PATTERNS varchar(1024),
  FAILURE_PATTERNS varchar(1024),
  RESPECT_ERROR_CODE varchar(3) not null,
  TIMEOUT int not null,
  DISABLED char(1) not null,
  CONTINUE char(1) not null,
  INITIALIZER char(1) not null,
  FINALIZER char(1) not null,
  LINE_NUMBER integer not null,
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint BUILD_SEQUENCE_UC1 unique (ID),
  constraint BUILD_SEQUENCE_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create index BUILD_SEQUENCE_IX1 on BUILD_SEQUENCE(BUILD_ID, TYPE, NAME);
create index BUILD_SEQUENCE_IX2 on BUILD_SEQUENCE(BUILD_ID, TYPE, LINE_NUMBER);
create index BUILD_SEQUENCE_IX3 on BUILD_SEQUENCE(BUILD_ID, TYPE, DISABLED);

create cached table SCHEDULE_ITEM (
  BUILD_ID integer not null,
  ID integer not null identity,
  HOUR varchar(3) not null,
  WEEK_DAY varchar(50) not null,
  MONTH_DAY varchar(50) not null,
  CLEAN_CHECKOUT char(1) not null,
  RUN_IF_NO_CHANGES char(1) not null,
  TIMESTAMP bigint not null,
  constraint SCHEDULE_ITEM_UC1 unique (ID),
  constraint SCHEDULE_ITEM_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
--
-- create unique index SCHEDULE_ITEM_PK on SCHEDULE_ITEM(ID);
-- create index SCHEDULE_ITEM_FK1 on SCHEDULE_ITEM(BUILD_ID);


create cached table ISSUE_TRACKER (
  BUILD_ID integer not null,
  ID integer not null identity,
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint ISSUE_TRACKER_UC1 unique(ID),
  constraint ISSUE_TRACKER_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create index ISSUE_TRACKER_IX1 on ISSUE_TRACKER(BUILD_ID, TYPE);
--
-- create unique index ISSUE_TRACKER_PK on ISSUE_TRACKER(ID);
-- create index ISSUE_TRACKER_FK1 on ISSUE_TRACKER(BUILD_ID);


create cached table ISSUE_TRACKER_PROPERTY (
  ISSUE_TRACKER_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint ISSUE_TRACKER_PROPERTY_UC1 unique (ID),
  constraint ISSUE_TRACKER_PROPERTY_UC2 unique (ISSUE_TRACKER_ID, NAME),
  constraint ISSUE_TRACKER_PROPERTY_FC1 foreign key (ISSUE_TRACKER_ID) references ISSUE_TRACKER(ID) ON DELETE CASCADE
);
--
-- create unique index ISSUE_TRACKER_PROPERTY_AK1 on ISSUE_TRACKER_PROPERTY(ISSUE_TRACKER_ID, NAME);
-- create unique index ISSUE_TRACKER_PROPERTY_PK on ISSUE_TRACKER_PROPERTY(ID);
-- create index ISSUE_TRACKER_PROPERTY_FK1 on ISSUE_TRACKER_PROPERTY(ISSUE_TRACKER_ID);


create cached table MANUAL_RUN_PARAMETER (
  ID integer not null identity,
  BUILD_ID integer not null,
  DESCRIPTION varchar(100) not null,
  ENABLED char(1) not null,
  NAME varchar(100) not null,
  REQUIRED char(1) not null,
  TIMESTAMP bigint not null,
  TYPE tinyint not null,
  VALUE varchar(1024),
  RUNTIME_VALUE varchar(1024),
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC1 unique (ID),
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_UC2 unique (BUILD_ID, NAME),
  constraint MANUAL_RUN_PARAMETER_ATTRIBUTE_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);


create cached table BUILD_RUN (
  BUILD_ID integer not null,
  ACTIVE_BUILD_ID integer not null,
  ID integer not null identity,
  NUMBER integer not null,
  COMPLETE tinyint not null,
  STARTED_AT datetime not null,
  FINISHED_AT datetime,
  RESULT  tinyint,
  RESULT_DESCRIPTION varchar(1024),
  LABEL  varchar(254) null,
  MANUAL_LABEL  varchar(254) null,
  LABEL_STATUS tinyint not null,
  BUILD_NAME  varchar(254) not null,
  SYNC_NOTE  varchar(2048) not null,
  CHANGELIST_NUM  varchar(10) not null,
  LAST_STEP_NAME  varchar(50) not null,
  RERUN char(1) not null,
  DEPENDENCE tinyint not null,
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint BUILD_RUN_UC1 unique (ID),
  constraint BUILD_RUN_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE,
  constraint BUILD_RUN_FC2 foreign key (ACTIVE_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create unique index BUILD_RUN_IX1 on BUILD_RUN(ID, COMPLETE, TYPE);
create index BUILD_RUN_IX2 on BUILD_RUN(BUILD_ID, COMPLETE, RESULT, RERUN);
create index BUILD_RUN_IX3 on BUILD_RUN(ACTIVE_BUILD_ID, COMPLETE, TYPE);
create index BUILD_RUN_IX4 on BUILD_RUN(ACTIVE_BUILD_ID, NUMBER);


create cached table BUILD_RUN_ATTRIBUTE (
  ID integer not null identity,
  BUILD_RUN_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint BUILD_RUN_ATTRIBUTE_UC1 unique (ID),
  constraint BUILD_RUN_ATTRIBUTE_UC2 unique (BUILD_RUN_ID, NAME),
  constraint BUILD_RUN_ATTRIBUTE_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);
--
-- create unique index BUILD_RUN_ATTRIBUTE_AK1 on BUILD_RUN_ATTRIBUTE(BUILD_RUN_ID, NAME);
-- create unique index BUILD_RUN_ATTRIBUTE_PK on BUILD_RUN_ATTRIBUTE(ID);
-- create index BUILD_RUN_ATTRIBUTE_FK1 on BUILD_RUN_ATTRIBUTE(BUILD_RUN_ID);


create cached table BUILD_RUN_DEPENDENCE (
  ID integer not null identity,
  LEADER_BUILD_RUN_ID integer not null,
  DEPENDENT_BUILD_RUN_ID integer not null,
  constraint BUILD_RUN_DEPENDENCE_UC1 unique (ID),
  constraint BUILD_RUN_DEPENDENCE_FC1 foreign key (LEADER_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE,
  constraint BUILD_RUN_DEPENDENCE_FC2 foreign key (DEPENDENT_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);
create unique index BUILD_RUN_DEPENDENCE_AK1 on BUILD_RUN_DEPENDENCE(LEADER_BUILD_RUN_ID, DEPENDENT_BUILD_RUN_ID);
create index BUILD_RUN_DEPENDENCE_IX1 on BUILD_RUN_DEPENDENCE(LEADER_BUILD_RUN_ID);
create index BUILD_RUN_DEPENDENCE_IX2 on BUILD_RUN_DEPENDENCE(DEPENDENT_BUILD_RUN_ID);


create cached table STEP_RUN (
  BUILD_RUN_ID integer not null,
  ID integer not null identity,
  NAME varchar(100) not null,
  RESULT  tinyint,
  RESULT_DESCRIPTION varchar(1024),
  STARTED_AT datetime not null,
  FINISHED_AT datetime not null,
  TIMESTAMP bigint not null,
  constraint STEP_RUN_UC1 unique (ID),
  constraint STEP_RUN_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);
create index STEP_RUN_IX1 on STEP_RUN(BUILD_RUN_ID, STARTED_AT);
--
-- create unique index STEP_RUN_PK on STEP_RUN(ID);
-- create index STEP_RUN_FK1 on STEP_RUN(BUILD_RUN_ID);


create cached table STEP_RUN_ATTRIBUTE (
  ID integer not null identity,
  STEP_RUN_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint STEP_RUN_ATTRIBUTE_UC1 unique (ID),
  constraint STEP_RUN_ATTRIBUTE_UC2 unique (STEP_RUN_ID, NAME),
  constraint STEP_RUN_ATTRIBUTE_FC1 foreign key (STEP_RUN_ID) references STEP_RUN(ID) ON DELETE CASCADE
);

--
-- create unique index STEP_RUN_ATTRIBUTE_AK1 on STEP_RUN_ATTRIBUTE(STEP_RUN_ID, NAME);
-- create unique index STEP_RUN_ATTRIBUTE_PK on STEP_RUN_ATTRIBUTE(ID);
-- create index STEP_RUN_ATTRIBUTE_FK1 on STEP_RUN_ATTRIBUTE(STEP_RUN_ID);


create cached table STEP_LOG (
  STEP_RUN_ID integer not null,
  ID integer not null identity,
  FILE varchar(256) not null,
  PATH varchar(256) not null,
  DESCRIPTION varchar(1024) not null,
  TYPE tinyint not null,
  PATH_TYPE tinyint not null,
  FOUND tinyint not null,
  TIMESTAMP bigint not null,
  constraint STEP_LOG_UC1 unique (ID),
  constraint STEP_LOG_FC1 foreign key (STEP_RUN_ID) references STEP_RUN(ID) ON DELETE CASCADE
);
create index STEP_LOG_IX1 on STEP_LOG(STEP_RUN_ID, TYPE);
create index STEP_LOG_IX2 on STEP_LOG(STEP_RUN_ID, FOUND, FILE);
create index STEP_LOG_IX3 on STEP_LOG(STEP_RUN_ID, FOUND, PATH_TYPE, PATH);
--
-- create index STEP_LOG_FK1 on STEP_LOG(STEP_RUN_ID);


create cached table STEP_RESULT (
  ID integer not null identity,
  STEP_RUN_ID integer not null,
  FILE varchar(512) not null,
  PATH varchar(512) not null,
  DESCRIPTION varchar(1024) not null,
  PATH_TYPE tinyint not null,
  FOUND char(1) not null,
  PINNED char(1) not null,
  constraint STEP_RESULT_UC1 unique (ID),
  constraint STEP_RESULT_FC1 foreign key (STEP_RUN_ID) references STEP_RUN(ID) ON DELETE CASCADE
);
create index STEP_RESULT_IX1 on STEP_RESULT(STEP_RUN_ID, FOUND);
create index STEP_RESULT_IX2 on STEP_RESULT(STEP_RUN_ID, FOUND, FILE);
create index STEP_RESULT_IX3 on STEP_RESULT(STEP_RUN_ID, PINNED, FOUND, FILE);
--
-- create unique index STEP_RESULT_PK on STEP_RESULT (ID);
-- create index STEP_RESULT_FK1 on STEP_RESULT(STEP_RUN_ID);


create cached table STEP_RESULT_ATTRIBUTE (
  ID integer not null identity,
  STEP_RESULT_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  constraint STEP_RESULT_ATTRIBUTE_UC1 unique (ID),
  constraint STEP_RESULT_ATTRIBUTE_UC2 unique (STEP_RESULT_ID, NAME),
  constraint STEP_RESULT_ATTRIBUTE_FC1 foreign key (STEP_RESULT_ID) references STEP_RESULT(ID) ON DELETE CASCADE
);
--
-- create unique index STEP_RESULT_ATTRIBUTE_AK1 on STEP_RESULT_ATTRIBUTE(STEP_RESULT_ID, NAME);
-- create unique index STEP_RESULT_ATTRIBUTE_PK on STEP_RESULT_ATTRIBUTE(ID);
-- create index STEP_RESULT_ATTRIBUTE_FK1 on STEP_RESULT_ATTRIBUTE(STEP_RESULT_ID);

create cached table CHANGELIST (
  ID integer not null identity,
  CREATED datetime not null,
  NUMBER varchar(10) null,
  EMAIL varchar(50) null,
  USER varchar(50) not null,
  CLIENT varchar(50) null,
  BRANCH varchar(50) null,
  DESCRIPTION varchar(1024) not null,
  TRUNCATED char(1) not null,
  ORIGINAL_SIZE integer not null,
  constraint CHANGELIST_UC1 unique (ID)
);
create index CHANGELIST_IX1 on CHANGELIST(CREATED);
create index CHANGELIST_IX2 on CHANGELIST(NUMBER, USER, CREATED);
--
-- create unique index CHANGELIST_PK on CHANGELIST(ID);


create cached table BUILD_CHANGELIST (
  ID integer not null identity,
  BUILD_ID integer not null,
  CHANGELIST_CREATED datetime not null,
  CHANGELIST_ID integer not null,
  NEW char(1) not null,
  constraint BUILD_CHANGELIST_UC1 unique (ID),
  constraint BUILD_CHANGELIST_UC2  unique (BUILD_ID, CHANGELIST_ID),
  constraint BUILD_CHANGELIST_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE,
  constraint BUILD_CHANGELIST_FC2 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
)
create index BUILD_CHANGELIST_IX2 on BUILD_CHANGELIST(BUILD_ID, NEW, CHANGELIST_CREATED);
--
-- create unique index BUILD_CHANGELIST_IX1 on BUILD_CHANGELIST(BUILD_ID, CHANGELIST_ID);
-- create unique index BUILD_CHANGELIST_PK on BUILD_CHANGELIST(ID);
-- create index BUILD_CHANGELIST_FK1 on BUILD_CONFIG(ID);
-- create index BUILD_CHANGELIST_FK2 on CHANGELIST(ID);


create cached table BUILD_RUN_PARTICIPANT (
  BUILD_RUN_ID integer not null,
  CHANGELIST_ID integer not null,
  ID integer not null identity,
  FIRST_BUILD_RUN_ID integer not null,
  FIRST_BUILD_RUN_NUMBER integer not null,
  constraint BUILD_RUN_PARTICIPANT_UC1 unique (ID),
  constraint BUILD_RUN_PARTICIPANT_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE,
  constraint BUILD_RUN_PARTICIPANT_FC2 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE,
  constraint BUILD_RUN_PARTICIPANT_FC3 foreign key (FIRST_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);
create index BUILD_RUN_PARTICIPANT_IX1 on BUILD_RUN_PARTICIPANT(BUILD_RUN_ID, CHANGELIST_ID);
create index BUILD_RUN_PARTICIPANT_IX2 on BUILD_RUN_PARTICIPANT(BUILD_RUN_ID, FIRST_BUILD_RUN_ID);
--
-- create unique index BUILD_RUN_PARTICIPANT_PK on BUILD_RUN_PARTICIPANT(ID);
-- create index BUILD_RUN_PARTICIPANT_FK1 on BUILD_RUN_PARTICIPANT(BUILD_RUN_ID);
-- create index BUILD_RUN_PARTICIPANT_FK2 on BUILD_RUN_PARTICIPANT(CHANGELIST_ID);
-- create index BUILD_RUN_PARTICIPANT_FK3 on BUILD_RUN_PARTICIPANT(FIRST_BUILD_RUN_ID);


create cached table CHANGE (
  CHANGELIST_ID integer not null,
  ID integer not null identity,
  REVISION varchar(100) not null,
  TYPE tinyint not null,
  FILE_PATH varchar(1024) not null,
  constraint CHANGE_UC1 unique (ID),
  constraint CHANGE_FC1 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
);
--
-- create unique index CHANGE_PK on CHANGE(ID);
-- create index CHANGE_FK1 on CHANGE(CHANGELIST_ID);


create cached table ISSUE (
  ID integer not null identity,
  TRACKER_TYPE tinyint not null,
  KEY varchar(50) not null,
  DESCRIPTION varchar(100) not null,
  PRODUCT varchar(50) default '' not null,
  VERSION varchar(50) default '' not null,
  PROJECT varchar(50) default '' not null,
  STATUS varchar(50) default '' not null,
  PRIORITY varchar(50) default '' not null,
  CLOSED datetime null,
  CLOSED_BY varchar(50) null,
  RECEIVED datetime not null,
  URL varchar(200) null,
  constraint ISSUE_UC1 unique (ID)
);
create index ISSUE_IX1 on ISSUE(KEY);
create index ISSUE_IX2 on ISSUE(TRACKER_TYPE, KEY, PRODUCT, VERSION);
create index ISSUE_IX3 on ISSUE(TRACKER_TYPE, KEY, PROJECT, VERSION);
--
-- create unique index ISSUE_PK on ISSUE(ID);


create cached table ISSUE_ATTR (
  ID integer not null identity,
  ISSUE_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  constraint ISSUE_ATTR_UC1 unique (ID),
  constraint ISSUE_ATTR_UC2 unique (ISSUE_ID, NAME),
  constraint ISSUE_ATTR_FC1 foreign key (ISSUE_ID) references ISSUE(ID) ON DELETE CASCADE
);
--
-- create unique index ISSUE_ATTR_AK1 on ISSUE_ATTR(ISSUE_ID, NAME);
-- create unique index ISSUE_ATTR_PK on ISSUE_ATTR(ID);
-- create index ISSUE_ATTR_FK1 on ISSUE_ATTR(ISSUE_ID);


create cached table PENDING_ISSUE (
  ID integer not null identity,
  BUILD_ID integer not null,
  ISSUE_ID integer not null,
  constraint PENDING_ISSUE_UC1 unique (ID),
  constraint PENDING_ISSUE_UC2 unique (BUILD_ID, ISSUE_ID),
  constraint PENDING_ISSUE_FC1 foreign key (BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE,
  constraint PENDING_ISSUE_FC2 foreign key (ISSUE_ID) references ISSUE(ID) ON DELETE CASCADE
);
--
-- create unique index PENDING_ISSUE_AK1 on PENDING_ISSUE(BUILD_ID, ISSUE_ID);
-- create unique index PENDING_ISSUE_PK on PENDING_ISSUE(ID);
-- create index PENDING_ISSUE_FK1 on PENDING_ISSUE(BUILD_ID);
-- create index PENDING_ISSUE_FK2 on PENDING_ISSUE(ISSUE_ID);


create cached table ISSUE_CHANGELIST (
  ID integer not null identity,
  ISSUE_ID integer not null,
  CHANGELIST_ID integer not null,
  constraint ISSUE_CHANGELIST_UC1 unique (ID),
  constraint ISSUE_CHANGELIST_UC2 unique (ISSUE_ID, CHANGELIST_ID),
  constraint ISSUE_CHANGELIST_FC1 foreign key (ISSUE_ID) references ISSUE(ID) ON DELETE CASCADE,
  constraint ISSUE_CHANGELIST_FC2 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
);
--
-- create unique index ISSUE_CHANGELIST_AK1 on ISSUE_CHANGELIST(ISSUE_ID, CHANGELIST_ID);
-- create unique index ISSUE_CHANGELIST_PK on ISSUE_CHANGELIST(ID);
-- create index ISSUE_CHANGELIST_FK1 on ISSUE_CHANGELIST(ISSUE_ID);
-- create index ISSUE_CHANGELIST_FK2 on ISSUE_CHANGELIST(CHANGELIST_ID);


create cached table RELEASE_NOTE (
  ID integer not null identity,
  BUILD_RUN_ID integer not null,
  ISSUE_ID integer not null,
  constraint RELEASE_NOTE_UC1 unique (ID),
  constraint RELEASE_NOTE_UC2 unique (BUILD_RUN_ID, ISSUE_ID),
  constraint RELEASE_NOTE_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE,
  constraint RELEASE_NOTE_FC2 foreign key (ISSUE_ID) references ISSUE(ID) ON DELETE CASCADE
)
--
-- create unique index RELEASE_NOTE_AK1 on RELEASE_NOTE(BUILD_RUN_ID, ISSUE_ID);
-- create unique index RELEASE_NOTE_PK on RELEASE_NOTE(ID);
-- create index RELEASE_NOTE_FK1 on RELEASE_NOTE(BUILD_RUN_ID);
-- create index RELEASE_NOTE_FK2 on RELEASE_NOTE(ISSUE_ID);


create cached table DAILY_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_BUILD_COUNT integer not null,
  SUCCESSFUL_BUILD_PERCENT integer not null,
  FAILED_BUILD_COUNT integer not null,
  FAILED_BUILD_PERCENT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint DAILY_STATS_UC1 unique (ID),
  constraint DAILY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME),
  constraint DAILY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table HOURLY_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_BUILD_COUNT integer not null,
  SUCCESSFUL_BUILD_PERCENT integer not null,
  FAILED_BUILD_COUNT integer not null,
  FAILED_BUILD_PERCENT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint HOURLY_STATS_UC1 unique (ID),
  constraint HOURLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME),
  constraint HOURLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table MONTHLY_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_BUILD_COUNT integer not null,
  SUCCESSFUL_BUILD_PERCENT integer not null,
  FAILED_BUILD_COUNT integer not null,
  FAILED_BUILD_PERCENT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint MONTHLY_STATS_UC1 unique (ID),
  constraint MONTHLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME),
  constraint MONTHLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table YEARLY_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_BUILD_COUNT integer not null,
  SUCCESSFUL_BUILD_PERCENT integer not null,
  FAILED_BUILD_COUNT integer not null,
  FAILED_BUILD_PERCENT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint YEARLY_STATS_UC1 unique (ID),
  constraint YEARLY_STATS_UC2 unique (ACTIVE_BUILD_ID, SAMPLE_TIME),
  constraint YEARLY_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);



create cached table HOURLY_DISTRIBUTION (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TARGET integer,
  SUCCESSFUL_BUILD_COUNT integer not null,
  FAILED_BUILD_COUNT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint HOURLY_DISTRIBUTION_UC1 unique (ID),
  constraint HOURLY_DISTRIBUTION_UC2 unique (ACTIVE_BUILD_ID, TARGET),
  constraint HOURLY_DISTRIBUTION_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table WEEKDAY_DISTRIBUTION (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TARGET integer,
  SUCCESSFUL_BUILD_COUNT integer not null,
  FAILED_BUILD_COUNT integer not null,
  TOTAL_BUILD_COUNT integer not null,
  CHANGE_LIST_COUNT integer not null,
  ISSUE_COUNT integer not null,
  constraint WEEKDAY_DISTRIBUTION_UC1 unique (ID),
  constraint WEEKDAY_DISTRIBUTION_UC2 unique (ACTIVE_BUILD_ID, TARGET),
  constraint WEEKDAY_DISTRIBUTION_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table CLUSTER (
    ID integer not null identity,
    NAME varchar(254) not null,
    DESCR varchar(254) not null,
    ENABLED char(1) not null,
    TIMESTAMP bigint not null,
    constraint CLUSTER_UC1 unique (ID),
    constraint CLUSTER_UC2 unique (NAME)
);

create cached table CLUSTER_ATTRIBUTE (
    CLUSTER_ID integer not null,
    ID integer not null identity,
    NAME varchar(80) not null,
    VALUE varchar(1024),
    TIMESTAMP bigint not null,
    constraint CLUSTER_ATTRIBUTE_UC1 unique (ID),
    constraint CLUSTER_ATTRIBUTE_UC2 unique (CLUSTER_ID, NAME),
    constraint CLUSTER_ATTRIBUTE_FC1 foreign key (CLUSTER_ID) references CLUSTER(ID) ON DELETE CASCADE
);

create cached table CLUSTER_MEMBER (
    ID integer not null identity,
    CLUSTER_ID integer not null,
    BUILDER_HOST varchar(80) not null,
    ENABLED char(1) not null,
    TIMESTAMP bigint not null,
    constraint CLUSTER_MEMBER_UC1 unique (ID),
    constraint CLUSTER_MEMBER_UC2 unique (CLUSTER_ID, BUILDER_HOST),
    constraint CLUSTER_MEMBER_FC2 foreign key (CLUSTER_ID) references CLUSTER(ID) ON DELETE CASCADE
);

create cached table DAILY_TEST_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TEST_CODE tinyint not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_TEST_COUNT integer not null,
  SUCCESSFUL_TEST_PERCENT integer not null,
  ERROR_TEST_COUNT integer not null,
  ERROR_TEST_PERCENT integer not null,
  FAILED_TEST_COUNT integer not null,
  FAILED_TEST_PERCENT integer not null,
  TOTAL_TEST_COUNT integer not null,
  BUILD_COUNT integer not null,
  constraint DAILY_TEST_STATS_UC1 unique (ID),
  constraint DAILY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME),
  constraint DAILY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table HOURLY_TEST_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TEST_CODE tinyint not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_TEST_COUNT integer not null,
  SUCCESSFUL_TEST_PERCENT integer not null,
  ERROR_TEST_COUNT integer not null,
  ERROR_TEST_PERCENT integer not null,
  FAILED_TEST_COUNT integer not null,
  FAILED_TEST_PERCENT integer not null,
  TOTAL_TEST_COUNT integer not null,
  BUILD_COUNT integer not null,
  constraint HOURLY_TEST_STATS_UC1 unique (ID),
  constraint HOURLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME),
  constraint HOURLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table MONTHLY_TEST_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TEST_CODE tinyint not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_TEST_COUNT integer not null,
  SUCCESSFUL_TEST_PERCENT integer not null,
  ERROR_TEST_COUNT integer not null,
  ERROR_TEST_PERCENT integer not null,
  FAILED_TEST_COUNT integer not null,
  FAILED_TEST_PERCENT integer not null,
  TOTAL_TEST_COUNT integer not null,
  BUILD_COUNT integer not null,
  constraint MONTHLY_TEST_STATS_UC1 unique (ID),
  constraint MONTHLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME),
  constraint MONTHLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


create cached table YEARLY_TEST_STATS (
  ID integer not null identity,
  ACTIVE_BUILD_ID integer not null,
  TEST_CODE tinyint not null,
  SAMPLE_TIME datetime,
  SUCCESSFUL_TEST_COUNT integer not null,
  SUCCESSFUL_TEST_PERCENT integer not null,
  ERROR_TEST_COUNT integer not null,
  ERROR_TEST_PERCENT integer not null,
  FAILED_TEST_COUNT integer not null,
  FAILED_TEST_PERCENT integer not null,
  TOTAL_TEST_COUNT integer not null,
  BUILD_COUNT integer not null,
  constraint YEARLY_TEST_STATS_UC1 unique (ID),
  constraint YEARLY_TEST_STATS_UC2 unique (ACTIVE_BUILD_ID, TEST_CODE, SAMPLE_TIME),
  constraint YEARLY_TEST_STATS_FC1 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE
);


--
-- Defines global build result accessible from front pages.
--
create cached table RESULT_GROUP (
  ID integer not null identity,
  NAME varchar(100) not null,
  DESCRIPTION varchar(1024) not null,
  LAST_PUBLISHED datetime null,
  ENABLED char(1) not null,
  DELETED char(1) not null,
  TIMESTAMP bigint not null,
  constraint RESULT_GROUP_UC1 unique (ID)
);
create index RESULT_GROUP_IX1 on RESULT_GROUP(ENABLED, DELETED);


create cached table RESULT_GROUP_ATTRIBUTE (
  ID integer not null identity,
  RESULT_GROUP_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint RESULT_GROUP_ATTRIBUTE_UC1 unique (ID),
  constraint RESULT_GROUP_ATTRIBUTE_UC2 unique (RESULT_GROUP_ID, NAME),
  constraint RESULT_GROUP_ATTRIBUTE_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE
);


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


create cached table RESULT_CONFIG (
  ID integer not null identity,
  BUILD_ID integer not null,
  AUTOPUBLISH_GROUP_ID integer null,
  DESCRIPTION varchar(80) not null,
  PATH varchar(1024) not null,
  TYPE tinyint not null,
  TIMESTAMP bigint not null,
  constraint RESULT_CONFIG_UC1 unique (ID),
  constraint RESULT_CONFIG_FC1 foreign key (BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE,
  constraint RESULT_CONFIG_FC2 foreign key (AUTOPUBLISH_GROUP_ID) references RESULT_GROUP(ID) ON DELETE SET NULL
);


create cached table RESULT_CONFIG_PROPERTY (
  ID integer not null identity,
  RESULT_CONFIG_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint RESULT_CONFIG_PROPERTY_UC1 unique (ID),
  constraint RESULT_CONFIG_PROPERTY_UC2 unique (RESULT_CONFIG_ID, NAME),
  constraint RESULT_CONFIG_PROPERTY_FC1 foreign key (RESULT_CONFIG_ID) references RESULT_CONFIG(ID) ON DELETE CASCADE
);


--
-- List of step results incuded into given result.
--
create cached table PUBLISHED_STEP_RESULT (
  ID integer not null identity,
  RESULT_GROUP_ID integer not null,
  ACTIVE_BUILD_ID integer not null,
  BUILD_RUN_ID integer not null,
  PUBLISHER_BUILD_RUN_ID integer not null,
  STEP_RESULT_ID integer not null,
  PUBLISH_DATE  datetime not null,
  BUILD_NAME  varchar(254) not null,
  BUILD_RUN_DATE  datetime not null,
  BUILD_RUN_NUMBER integer not null,
  DESCRIPTION varchar(1024) not null,
  constraint PUBLISHED_STEP_RESULT_UC1 unique (ID),
  constraint PUBLISHED_STEP_RESULT_UC2 unique (RESULT_GROUP_ID, STEP_RESULT_ID),
  constraint PUBLISHED_STEP_RESULT_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE,
  constraint PUBLISHED_STEP_RESULT_FC2 foreign key (STEP_RESULT_ID) references STEP_RESULT(ID) ON DELETE CASCADE,
  constraint PUBLISHED_STEP_RESULT_FC3 foreign key (ACTIVE_BUILD_ID) references ACTIVE_BUILD(ID) ON DELETE CASCADE,
  constraint PUBLISHED_STEP_RESULT_FC4 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE,
  constraint PUBLISHED_STEP_RESULT_FC5 foreign key (PUBLISHER_BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);
create index PUBLISHED_STEP_RESULT_IX1 on PUBLISHED_STEP_RESULT(RESULT_GROUP_ID, PUBLISH_DATE);


create cached table PUBLISHED_STEP_RESULT_ATTRIBUTE (
  ID integer not null identity,
  PUBLISHED_STEP_RESULT_ID integer not null,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_UC1 unique (ID),
  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_UC2 unique (PUBLISHED_STEP_RESULT_ID, NAME),
  constraint PUBLISHED_STEP_RESULT_ATTRIBUTE_FC1 foreign key (PUBLISHED_STEP_RESULT_ID) references PUBLISHED_STEP_RESULT(ID) ON DELETE CASCADE
);

--
-- Defines what user groups have access to results.
--
create cached table RESULT_GROUP_ACCESS (
  ID integer not null identity,
  GROUP_ID integer not null,
  RESULT_GROUP_ID integer not null,
  TIMESTAMP bigint not null,
  constraint RESULT_GROUP_ACCESS_UC1 unique (ID),
  constraint RESULT_GROUP_ACCESS_UC2 unique (RESULT_GROUP_ID, GROUP_ID),
  constraint RESULT_GROUP_ACCESS_FC1 foreign key (RESULT_GROUP_ID) references RESULT_GROUP(ID) ON DELETE CASCADE,
  constraint RESULT_GROUP_ACCESS_FC2 foreign key (GROUP_ID) references GROUPS(ID) ON DELETE CASCADE
);


--
-- Defines log of actions against a build run.
-- It is insert-only, so no need for time stamp.
--
create cached table BUILD_RUN_ACTION (
  ID integer not null identity,
  BUILD_RUN_ID integer not null,
  CODE tinyint not null,
  ACTION varchar(1024) not null,
  ACTION_DATE datetime not null,
  DESCRIPTION varchar(1024) not null,
  USER_ID integer not null,
  constraint BUILD_RUN_ACTION_UC1 unique (ID),
  constraint BUILD_RUN_ACTION_FC1 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE,
  constraint BUILD_RUN_ACTION_FC2 foreign key (USER_ID) references USERS(ID) ON DELETE CASCADE
);
create index BUILD_RUN_ACTION_IX1 on BUILD_RUN_ACTION(BUILD_RUN_ID, ACTION_DATE);



create cached table MERGE_CONFIGURATION (
  ID integer not null identity,
  ACTIVE_MERGE_CONFIGURATION_ID integer null,
  BRANCH_VIEW varchar(32000) not null,
  BRANCH_VIEW_NAME varchar(512) not null,
  BRANCH_VIEW_SOURCE tinyint not null,
  CONFLICT_RESOLUTION_MODE tinyint not null,
  DESCRIPTION varchar(1024) not null,
  DISCRIMINATOR char(1) not null,
  INDIRECT_MERGE char(1) not null,
  MARKER varchar(1024),
  MERGE_MODE tinyint not null,
  NAME varchar(80) not null,
  PRESERVE_MARKER char(1) not null,
  REVERSE_BRANCH_VIEW char(1) not null,
  SOURCE_BUILD_ID integer not null,
  TARGET_BUILD_ID integer not null,
  TIMESTAMP bigint not null,
  constraint MERGE_CONFIGURATION_UC1 unique (ID),
  constraint MERGE_CONFIGURATION_FC1 foreign key (SOURCE_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE,
  constraint MERGE_CONFIGURATION_FC2 foreign key (TARGET_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE
);
create index MERGE_CONFIGURATION_IX1 on MERGE_CONFIGURATION(ID, DISCRIMINATOR);


create cached table MERGE_CONFIGURATION_ATTRIBUTE (
  MERGE_CONFIGURATION_ID integer not null,
  ID integer not null identity,
  NAME varchar(80) not null,
  VALUE varchar(1024),
  TIMESTAMP bigint not null,
  constraint MERGE_CONFIGURATION_ATTRIBUTE_UC1 unique (ID),
  constraint MERGE_CONFIGURATION_ATTRIBUTE_UC2 unique (MERGE_CONFIGURATION_ID, NAME),
  constraint MERGE_CONFIGURATION_ATTRIBUTE_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE
);


create cached table MERGE_SERVICE_CONFIGURATION (
  ID integer not null,
  PROJECT_ID integer not null,
  DELETED char(1) not  null,
  STARTUP_MODE tinyint not null,
  TIMESTAMP bigint not null,
  constraint MERGE_SERVICE_CONFIGURATION_UC1 unique (ID),
  constraint MERGE_SERVICE_CONFIGURATION_UC2 unique (ID, DELETED),
  constraint MERGE_SERVICE_CONFIGURATION_FC1 foreign key (ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE,
  constraint MERGE_SERVICE_CONFIGURATION_FC2 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE
);


create cached table BRANCH_CHANGELIST (
  ID integer not null identity,
  MERGE_CONFIGURATION_ID integer not null,
  CHANGELIST_ID integer not null,
  MERGE_STATUS tinyint not null,
  constraint BRANCH_CHANGELIST_CONFIGURATION_UC1 unique (ID),
  constraint BRANCH_CHANGELIST_CONFIGURATION_UC2 unique (MERGE_CONFIGURATION_ID, CHANGELIST_ID),
  constraint BRANCH_CHANGELIST_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE,
  constraint BRANCH_CHANGELIST_FC2 foreign key (CHANGELIST_ID) references CHANGELIST(ID) ON DELETE CASCADE
);
create index BRANCH_CHANGELIST_IX1 on BRANCH_CHANGELIST(MERGE_CONFIGURATION_ID, MERGE_STATUS);


create cached table BRANCH_BUILD_RUN_PARTICIPANT (
    ID integer not null identity,
    BRANCH_CHANGELIST_ID integer not null,
    BUILD_RUN_PARTICIPANT_ID integer not null,
    constraint BRANCH_BUILD_RUN_PARTICIPANT_UC1 unique (ID),
    constraint BRANCH_BUILD_RUN_PARTICIPANT_UC2 unique (BRANCH_CHANGELIST_ID, BUILD_RUN_PARTICIPANT_ID),
    constraint BRANCH_BUILD_RUN_PARTICIPANT_FC1 foreign key (BRANCH_CHANGELIST_ID) references BRANCH_CHANGELIST(ID) ON DELETE CASCADE,
    constraint BRANCH_BUILD_RUN_PARTICIPANT_FC2 foreign key (BUILD_RUN_PARTICIPANT_ID) references BUILD_RUN_PARTICIPANT(ID) ON DELETE CASCADE
);


create cached table MERGE (
      ID integer not null identity,
      MERGE_CONFIGURATION_ID integer not null,
      CREATED  datetime not null,
      VALIDATED char(1) not null,
      RESULT_CODE tinyint not null,
      constraint MERGE_UC1 unique (ID),
      constraint MERGE_FC1 foreign key (MERGE_CONFIGURATION_ID) references MERGE_CONFIGURATION(ID) ON DELETE CASCADE
);

create cached table MERGE_CHANGELIST (
      ID integer not null identity,
      MERGE_ID integer not null,
      BRANCH_CHANGELIST_ID integer not null,
      RESULT_CODE tinyint not null,
      RESULT_DESCRIPTION varchar(50000) not null,
      constraint MERGE_CHANGELIST_UC1 unique (ID),
      constraint MERGE_CHANGELIST_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,
      constraint MERGE_CHANGELIST_FC2 foreign key (BRANCH_CHANGELIST_ID) references BRANCH_CHANGELIST(ID) ON DELETE CASCADE
);

create cached table MERGE_SOURCE_BUILD_RUN (
      ID integer not null identity,
      MERGE_ID integer not null,
      BUILD_RUN_ID integer not null,
      constraint MERGE_SOURCE_BUILD_RUN_UC1 unique (ID),
      constraint MERGE_SOURCE_BUILD_RUN_UC2 unique (MERGE_ID),
      constraint MERGE_SOURCE_BUILD_RUN_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,
      constraint MERGE_SOURCE_BUILD_RUN_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);

create cached table MERGE_TARGET_BUILD_RUN (
      ID integer not null identity,
      MERGE_ID integer not null,
      BUILD_RUN_ID integer not null,
      constraint MERGE_TARGET_BUILD_RUN_UC1 unique (ID),
      constraint MERGE_TARGET_BUILD_RUN_UC2 unique (MERGE_ID),
      constraint MERGE_TARGET_BUILD_RUN_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE,
      constraint MERGE_TARGET_BUILD_RUN_FC2 foreign key (BUILD_RUN_ID) references BUILD_RUN(ID) ON DELETE CASCADE
);

insert into USERS (ID, NAME, FNAME, PASSWORD, IS_ADMIN, EMAIL, ROLES, ENABLED, IM_TYPE, IM_ADDRESS, LDAP_AUTH, TIMESTAMP) values(0, 'admin', '', '21232F297A57A5A743894A0E4A801FC3', 'Y', '', 'admin', 'Y', 0, '', 'N', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (0, 'parabuild.schema.version', '57', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (1, 'parabuild.date.format', 'MM/dd/yyyy', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (2, 'parabuild.date.time.format', 'hh:mm a MM/dd/yyyy', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (3, 'parabuild.enable.anon.builds', 'checked', 1);
insert into SYSTEM_PROPERTY (ID, NAME, VALUE, TIMESTAMP) values (4, 'parabuild.build.sequence.number', '0', 1);
insert into GROUPS (ID, NAME, DESCR, ENABLED, ALLOWED_TO_CREATE_BUILD, ALLOWED_TO_DELETE_BUILD, ALLOWED_TO_START_BUILD, ALLOWED_TO_STOP_BUILD, ALLOWED_TO_UPDATE_BUILD, ALLOWED_TO_VIEW_BUILD, ALLOWED_TO_PUBLISH_RESULTS, ALLOWED_TO_DELETE_RESULTS, ALLOWED_TO_CREATE_RESULT_GROUP, ALLOWED_TO_DELETE_RESULT_GROUP, ALLOWED_TO_UPDATE_RESULT_GROUP, ALLOWED_TO_VIEW_RESULT_GROUP, TIMESTAMP) values (0, 'Anonymous', 'Users that are not logged in', 'Y', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'Y', 0);
insert into PROJECT (ID, NAME, DESCRIPTION, KEY, TYPE, DELETED, TIMESTAMP) values (0, 'System', 'System project', 'SYSTEM', 1, 'N', 0);
insert into PROJECT (ID, NAME, DESCRIPTION, KEY, TYPE, DELETED, TIMESTAMP) values (1, 'My project', 'Change this project name to reflect actual needs of your system', 'MYPROJECT', 2, 'N', 0);
