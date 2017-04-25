

/*==============================================================================================*/
/* Quartz database tables creation script for Sybase ASE 12.5 */
/* Written by Pertti Laiho (email: pertti.laiho@deio.net), 9th May 2003 */
/* */
/* Compatible with Quartz version 1.1.2 */
/* */
/* Sybase ASE works ok with the MSSQL delegate class. That means in your Quartz properties */
/* file, you'll need to set: */
/* org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.MSSQLDelegate */
/*==============================================================================================*/

use your_db_name_here
go

/*==============================================================================*/
/* Clear all tables: */
/*==============================================================================*/

delete from qrtz_job_listeners
go
delete from qrtz_trigger_listeners
go
delete from qrtz_fired_triggers
go
delete from qrtz_simple_triggers
go
delete from qrtz_cron_triggers
go
delete from qrtz_blob_triggers
go
delete from qrtz_triggers
go
delete from qrtz_job_details
go
delete from qrtz_calendars
go

/*==============================================================================*/
/* Drop constraints: */
/*==============================================================================*/

alter table qrtz_job_listeners
drop constraint FK_job_listeners_job_details
go

alter table qrtz_triggers
drop constraint FK_triggers_job_details
go

alter table qrtz_cron_triggers
drop constraint FK_cron_triggers_triggers
go

alter table qrtz_simple_triggers
drop constraint FK_simple_triggers_triggers
go

alter table qrtz_trigger_listeners
drop constraint FK_trigger_listeners_triggers
go

alter table qrtz_blob_triggers
drop constraint FK_blob_triggers_triggers
go

/*==============================================================================*/
/* Drop tables: */
/*==============================================================================*/

drop table qrtz_job_listeners
go
drop table qrtz_trigger_listeners
go
drop table qrtz_fired_triggers
go
drop table qrtz_paused_trigger_grps
go
drop table qrtz_scheduler_state
go
drop table qrtz_locks
go
drop table qrtz_simple_triggers
go
drop table qrtz_cron_triggers
go
drop table qrtz_triggers
go
drop table qrtz_job_details
go
drop table qrtz_calendars
go
drop table qrtz_blob_triggers
go

/*==============================================================================*/
/* Create tables: */
/*==============================================================================*/

create table qrtz_calendars (
CALENDAR_NAME varchar(80) not null,
CALENDAR image not null
)
go

create table qrtz_cron_triggers (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
CRON_EXPRESSION varchar(80) not null,
TIME_ZONE_ID varchar(80) null,
)
go

create table qrtz_paused_trigger_grps (
trigger_group  varchar(80) not null, 
)
go

create table qrtz_fired_triggers(
entry_id varchar(95) not null,
trigger_name varchar(80) not null,
trigger_group varchar(80) not null,
is_volatile bit not null,
instance_name varchar(80) not null,
fired_time numeric(13,0) not null,
state varchar(16) not null,
job_name varchar(80) null,
job_group varchar(80) null,
is_stateful bit not null,
requests_recovery bit not null,
)
go

create table qrtz_scheduler_state (
instance_name varchar(80) not null,
last_checkin_time numeric(13,0) not null,
checkin_interval numeric(13,0) not null,
recoverer varchar(80) null,
)
go

create table qrtz_locks (
lock_name  varchar(40) not null, 
)
go

insert into qrtz_locks values('TRIGGER_ACCESS')
go
insert into qrtz_locks values('JOB_ACCESS')
go
insert into qrtz_locks values('CALENDAR_ACCESS')
go
insert into qrtz_locks values('STATE_ACCESS')
go


create table qrtz_job_details (
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
DESCRIPTION varchar(120) null,
JOB_CLASS_NAME varchar(128) not null,
IS_DURABLE bit not null,
IS_VOLATILE bit not null,
IS_STATEFUL bit not null,
REQUESTS_RECOVERY bit not null,
JOB_DATA image null
)
go

create table qrtz_job_listeners (
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
JOB_LISTENER varchar(80) not null
)
go

create table qrtz_simple_triggers (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
REPEAT_COUNT numeric(13,0) not null,
REPEAT_INTERVAL numeric(13,0) not null,
TIMES_TRIGGERED numeric(13,0) not null
)
go

create table qrtz_blob_triggers (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
BLOB_DATA image null
)
go

create table qrtz_trigger_listeners (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
TRIGGER_LISTENER varchar(80) not null
)
go

create table qrtz_triggers (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
IS_VOLATILE bit not null,
DESCRIPTION varchar(120) null,
NEXT_FIRE_TIME numeric(13,0) null,
PREV_FIRE_TIME numeric(13,0) null,
TRIGGER_STATE varchar(16) not null,
TRIGGER_TYPE varchar(8) not null,
START_TIME numeric(13,0) not null,
END_TIME numeric(13,0) null,
CALENDAR_NAME varchar(80) null,
MISFIRE_INSTR smallint null
)
go

/*==============================================================================*/
/* Create primary key constraints: */
/*==============================================================================*/

alter table qrtz_calendars
add constraint PK_qrtz_calendars primary key clustered (CALENDAR_NAME)
go

alter table qrtz_cron_triggers
add constraint PK_qrtz_cron_triggers primary key clustered (TRIGGER_NAME, TRIGGER_GROUP)
go

alter table qrtz_fired_triggers
add constraint PK_qrtz_fired_triggers primary key clustered (entry_id)
go

alter table qrtz_paused_trigger_grps
add constraint PK_qrtz_paused_trigger_grps primary key clustered (trigger_group)
go

alter table qrtz_scheduler_state
add constraint PK_qrtz_scheduler_state primary key clustered (instance_name)
go

alter table qrtz_locks
add constraint PK_qrtz_locks primary key clustered (lock_name)
go

alter table qrtz_job_details
add constraint PK_qrtz_job_details primary key clustered (JOB_NAME, JOB_GROUP)
go

alter table qrtz_job_listeners
add constraint PK_qrtz_job_listeners primary key clustered (JOB_NAME, JOB_GROUP, JOB_LISTENER)
go

alter table qrtz_simple_triggers
add constraint PK_qrtz_simple_triggers primary key clustered (TRIGGER_NAME, TRIGGER_GROUP)
go

alter table qrtz_trigger_listeners
add constraint PK_qrtz_trigger_listeners primary key clustered (TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_LISTENER)
go

alter table qrtz_triggers
add constraint PK_qrtz_triggers primary key clustered (TRIGGER_NAME, TRIGGER_GROUP)
go

alter table qrtz_blob_triggers
add constraint PK_qrtz_blob_triggers primary key clustered (TRIGGER_NAME, TRIGGER_GROUP)
go


/*==============================================================================*/
/* Create foreign key constraints: */
/*==============================================================================*/

alter table qrtz_cron_triggers
add constraint FK_cron_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references qrtz_triggers (TRIGGER_NAME,TRIGGER_GROUP)
go

alter table qrtz_job_listeners
add constraint FK_job_listeners_job_details foreign key (JOB_NAME,JOB_GROUP)
references qrtz_job_details (JOB_NAME,JOB_GROUP)
go

alter table qrtz_simple_triggers
add constraint FK_simple_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references qrtz_triggers (TRIGGER_NAME,TRIGGER_GROUP)
go

alter table qrtz_trigger_listeners
add constraint FK_trigger_listeners_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references qrtz_triggers (TRIGGER_NAME,TRIGGER_GROUP)
go

alter table qrtz_triggers
add constraint FK_triggers_job_details foreign key (JOB_NAME,JOB_GROUP)
references qrtz_job_details (JOB_NAME,JOB_GROUP)
go

alter table qrtz_blob_triggers
add constraint FK_blob_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references qrtz_triggers (TRIGGER_NAME,TRIGGER_GROUP)
go

/*==============================================================================*/
/* End of script. */
/*==============================================================================*/
