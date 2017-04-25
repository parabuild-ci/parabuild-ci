/*
 * $Id: nq-noarg1.sql,v 1.1 2007/08/09 03:28:37 unsaved Exp $
 *
 * Test of \q with arg from nested script.
 */

\i nq-noarg1.isql
\q Should not have returned from nested script!
