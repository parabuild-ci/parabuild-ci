# thanks to George Papastamatopoulos for submitting this ... and Marko Lahma for
# updating it.
#
# In your Quartz properties file, you'll need to set 
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.MSSQLDelegate
#
# you shouse enter your DB instance's name on the next line in place of "enter_db_name_here"
#
#
# From a helpful (but anonymous) Quartz user:
#
# Regarding this error message:  
#
#     [Microsoft][SQLServer 2000 Driver for JDBC]Can't start a cloned connection while in manual transaction mode.
#
#
#     I added "SelectMethod=cursor;" to my Connection URL in the config file. 
#     It Seems to work, hopefully no side effects.
#
#		example:
#		"jdbc:microsoft:sqlserver://dbmachine:1433;SelectMethod=cursor"; 
#

use [enter_db_name_here]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_job_listeners_qrtz_job_details]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_job_listeners] DROP CONSTRAINT FK_qrtz_job_listeners_qrtz_job_details
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_triggers_qrtz_job_details]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_triggers] DROP CONSTRAINT FK_qrtz_triggers_qrtz_job_details
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_cron_triggers_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_cron_triggers] DROP CONSTRAINT FK_qrtz_cron_triggers_qrtz_triggers
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_simple_triggers_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_simple_triggers] DROP CONSTRAINT FK_qrtz_simple_triggers_qrtz_triggers
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_qrtz_trigger_listeners_qrtz_triggers]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[qrtz_trigger_listeners] DROP CONSTRAINT FK_qrtz_trigger_listeners_qrtz_triggers
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_calendars]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_calendars]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_cron_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_cron_triggers]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_fired_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_fired_triggers]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_paused_trigger_grps]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_paused_trigger_grps]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_scheduler_state]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_scheduler_state]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_locks]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_locks]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_job_details]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_job_details]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_job_listeners]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_job_listeners]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_simple_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_simple_triggers]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_trigger_listeners]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_trigger_listeners]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[qrtz_triggers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[qrtz_triggers]
GO

CREATE TABLE [dbo].[qrtz_calendars] (
  [CALENDAR_NAME] [varchar] (80)  NOT NULL ,
  [CALENDAR] [image] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_cron_triggers] (
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [CRON_EXPRESSION] [varchar] (80)  NOT NULL ,
  [TIME_ZONE_ID] [varchar] (80) 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_fired_triggers] (
  [ENTRY_ID] [varchar] (95)  NOT NULL ,
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [IS_VOLATILE] [varchar] (1)  NOT NULL ,
  [INSTANCE_NAME] [varchar] (80)  NOT NULL ,
  [FIRED_TIME] [bigint] NOT NULL ,
  [STATE] [varchar] (16)  NOT NULL,
  [JOB_NAME] [varchar] (80)  NULL ,
  [JOB_GROUP] [varchar] (80)  NULL ,
  [IS_STATEFUL] [varchar] (1)  NULL ,
  [REQUESTS_RECOVERY] [varchar] (1)  NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_paused_trigger_grps] (
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_scheduler_state] (
  [INSTANCE_NAME] [varchar] (80)  NOT NULL ,
  [LAST_CHECKIN_TIME] [bigint] NOT NULL ,
  [CHECKIN_INTERVAL] [bigint] NOT NULL ,
  [RECOVERER] [varchar] (80)  NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_locks] (
  [LOCK_NAME] [varchar] (40)  NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_job_details] (
  [JOB_NAME] [varchar] (80)  NOT NULL ,
  [JOB_GROUP] [varchar] (80)  NOT NULL ,
  [DESCRIPTION] [varchar] (120) NULL ,
  [JOB_CLASS_NAME] [varchar] (128)  NOT NULL ,
  [IS_DURABLE] [varchar] (1)  NOT NULL ,
  [IS_VOLATILE] [varchar] (1)  NOT NULL ,
  [IS_STATEFUL] [varchar] (1)  NOT NULL ,
  [REQUESTS_RECOVERY] [varchar] (1)  NOT NULL ,
  [JOB_DATA] [image] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_job_listeners] (
  [JOB_NAME] [varchar] (80)  NOT NULL ,
  [JOB_GROUP] [varchar] (80)  NOT NULL ,
  [JOB_LISTENER] [varchar] (80)  NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_simple_triggers] (
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [REPEAT_COUNT] [bigint] NOT NULL ,
  [REPEAT_INTERVAL] [bigint] NOT NULL ,
  [TIMES_TRIGGERED] [bigint] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_blob_triggers] (
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [BLOB_DATA] [image] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_trigger_listeners] (
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [TRIGGER_LISTENER] [varchar] (80)  NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[qrtz_triggers] (
  [TRIGGER_NAME] [varchar] (80)  NOT NULL ,
  [TRIGGER_GROUP] [varchar] (80)  NOT NULL ,
  [JOB_NAME] [varchar] (80)  NOT NULL ,
  [JOB_GROUP] [varchar] (80)  NOT NULL ,
  [IS_VOLATILE] [varchar] (1)  NOT NULL ,
  [DESCRIPTION] [varchar] (120) NULL ,
  [NEXT_FIRE_TIME] [bigint] NULL ,
  [PREV_FIRE_TIME] [bigint] NULL ,
  [TRIGGER_STATE] [varchar] (16)  NOT NULL ,
  [TRIGGER_TYPE] [varchar] (8)  NOT NULL ,
  [START_TIME] [bigint] NOT NULL ,
  [END_TIME] [bigint] NULL ,
  [CALENDAR_NAME] [varchar] (80)  NULL ,
  [MISFIRE_INSTR] [smallint] NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_calendars] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_calendars] PRIMARY KEY  CLUSTERED
  (
    [CALENDAR_NAME]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_cron_triggers] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_cron_triggers] PRIMARY KEY  CLUSTERED
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_fired_triggers] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_fired_triggers] PRIMARY KEY  CLUSTERED
  (
    [ENTRY_ID]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_paused_trigger_grps] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_paused_trigger_grps] PRIMARY KEY  CLUSTERED
  (
    [trigger_group]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_scheduler_state] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_scheduler_state] PRIMARY KEY  CLUSTERED
  (
    [instance_name]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_locks] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_locks] PRIMARY KEY  CLUSTERED
  (
    [lock_name]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_job_details] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_job_details] PRIMARY KEY  CLUSTERED
  (
    [JOB_NAME],
    [JOB_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_job_listeners] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_job_listeners] PRIMARY KEY  CLUSTERED
  (
    [JOB_NAME],
    [JOB_GROUP],
    [JOB_LISTENER]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_simple_triggers] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_simple_triggers] PRIMARY KEY  CLUSTERED
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_trigger_listeners] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_trigger_listeners] PRIMARY KEY  CLUSTERED
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP],
    [TRIGGER_LISTENER]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_triggers] WITH NOCHECK ADD
  CONSTRAINT [PK_qrtz_triggers] PRIMARY KEY  CLUSTERED
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[qrtz_cron_triggers] ADD
  CONSTRAINT [FK_qrtz_cron_triggers_qrtz_triggers] FOREIGN KEY
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[qrtz_job_listeners] ADD
  CONSTRAINT [FK_qrtz_job_listeners_qrtz_job_details] FOREIGN KEY
  (
    [JOB_NAME],
    [JOB_GROUP]
  ) REFERENCES [dbo].[qrtz_job_details] (
    [JOB_NAME],
    [JOB_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[qrtz_simple_triggers] ADD
  CONSTRAINT [FK_qrtz_simple_triggers_qrtz_triggers] FOREIGN KEY
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[qrtz_trigger_listeners] ADD
  CONSTRAINT [FK_qrtz_trigger_listeners_qrtz_triggers] FOREIGN KEY
  (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[qrtz_triggers] (
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[qrtz_triggers] ADD
  CONSTRAINT [FK_qrtz_triggers_qrtz_job_details] FOREIGN KEY
  (
    [JOB_NAME],
    [JOB_GROUP]
  ) REFERENCES [dbo].[qrtz_job_details] (
    [JOB_NAME],
    [JOB_GROUP]
  )
GO

INSERT INTO [dbo].[qrtz_locks] values('TRIGGER_ACCESS');
INSERT INTO [dbo].[qrtz_locks] values('JOB_ACCESS');
INSERT INTO [dbo].[qrtz_locks] values('CALENDAR_ACCESS');
INSERT INTO [dbo].[qrtz_locks] values('STATE_ACCESS');
INSERT INTO [dbo].[qrtz_locks] values('MISFIRE_ACCESS');

