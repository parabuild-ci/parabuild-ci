/*
 * $Id: nq-noarg1.sql 215 2007-06-10 02:33:27Z unsaved $
 *
 * Test of \q with arg from nested script.
 */

\i nq-noarg1.isql
\q Should not have returned from nested script!
