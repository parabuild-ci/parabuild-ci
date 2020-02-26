/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.installer;

import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Upgrader of HSQLDB version from From 1.7 To 1.8.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Dec 4, 2008 9:02:20 PM
 */
final class HSQLDBUpgraderFrom17To18 {

  private static final String HSQLDB_CACHE_FILE_SCALE = "hsqldb.cache_file_scale";
  private static final String HSQLDB_SCRIPT_FORMAT = "hsqldb.script_format";
  private static final String HSQLDB_NIO_DATA_FILE = "hsqldb.nio_data_file";
  private static final String HSQLDB_COMPATIBLE_VERSION = "hsqldb.compatible_version";
  private static final String VERSION = "version";
  private static final String HSQLDB_VERSION = "hsqldb.version";
  private static final String SQL_MONTH = "sql.month";
  private static final String SQL_STRICT_FK = "sql.strict_fk";
  private static final String SQL_STRONG_FK = "sql.strong_fk";
  private static final String HSQLDB_DEFAULT_TABLE_TYPE = "hsqldb.default_table_type";
  private static final String HSQLDB_LOG_SIZE = "hsqldb.log_size";


  /**
   * @noinspection ProhibitedExceptionThrown,ThrowCaughtLocally,NestedTryStatement
   */
  public void upgrade(final File databaseDirectory) throws IOException {

    // Validate this is a correct version
    final Properties databaseProps = InstallerUtils.loadDatabaseProperties(databaseDirectory);
    final String property = databaseProps.getProperty(InstallerConstants.HSQLDB_ORIGINAL_VERSION);
    if (property.indexOf(InstallerConstants.STR_1_8) == 0) {
      // Already upgraded to HSQLDB 1.8
      return;
    } else if (!(property.indexOf(InstallerConstants.STR_1_7) == 0)) {
      throw new IOException("Unknown database version: " + property);
    }

    backup(databaseDirectory);
    try {
      // Save the database in the format acceptable for the upgrade.
      Connection conn = null;
      Statement stmt = null;
      final File databasePath = new File(databaseDirectory, "parabuild");
      try {
        final HSQLDBConnector connector = new HSQLDBConnector(HSQLDBDriverJar.HSQLDB_1_7);
        conn = connector.connect(databasePath);
        stmt = conn.createStatement();
        conn.setAutoCommit(true);
        stmt.executeUpdate("SET SCRIPTFORMAT TEXT");
        stmt.executeUpdate("SHUTDOWN SCRIPT");
      } catch (final SQLException e) {
        throw IoUtils.createIOException(e);
      } finally {
        IoUtils.closeHard(stmt);
        IoUtils.closeHard(conn);
      }

      // Set property hsqldb.cache_file_scale to 8 (8GB)
      FileOutputStream os = null;
      try {
        databaseProps.setProperty(HSQLDB_CACHE_FILE_SCALE, "8");
        databaseProps.setProperty(HSQLDB_COMPATIBLE_VERSION, "1.8.0");
        databaseProps.setProperty(HSQLDB_DEFAULT_TABLE_TYPE, "cached");
        databaseProps.setProperty(HSQLDB_NIO_DATA_FILE, "false");
        databaseProps.setProperty(InstallerConstants.HSQLDB_ORIGINAL_VERSION, "1.8.0");
        databaseProps.setProperty(HSQLDB_SCRIPT_FORMAT, "0");
        databaseProps.setProperty(SQL_MONTH, "true");
        databaseProps.setProperty(SQL_STRICT_FK, "true");
        databaseProps.setProperty(SQL_STRONG_FK, "true");
        databaseProps.setProperty(VERSION, "1.8.0");
        databaseProps.setProperty(HSQLDB_LOG_SIZE, "200");
        databaseProps.remove(HSQLDB_VERSION);
        os = new FileOutputStream(new File(databaseDirectory, InstallerConstants.PARABUILD_PROPERTIES));
        databaseProps.store(os, "HSQL database");
      } finally {
        IoUtils.closeHard(os);
      }

      // Open the database in the new format format acceptable for the upgrade using 1.8 driver.
      try {
        final HSQLDBConnector connector = new HSQLDBConnector(HSQLDBDriverJar.HSQLDB_1_8);
        conn = connector.connect(databasePath);
        stmt = conn.createStatement();
        conn.setAutoCommit(true);
        stmt.executeUpdate("SET SCRIPTFORMAT COMPRESSED");
        stmt.executeUpdate("SHUTDOWN");
      } catch (final SQLException e) {
        throw IoUtils.createIOException(e);
      } finally {
        IoUtils.closeHard(stmt);
        IoUtils.closeHard(conn);
      }
    } catch (final IOException e) {
      restore(databaseDirectory);
    } catch (final RuntimeException e) {
      restore(databaseDirectory);
      throw e;
    }
  }


  /**
   * Backs up the db before the upgrade.
   *
   * @param databaseDirectory
   */
  private void backup(final File databaseDirectory) throws IOException {
    // Get dir
    final File backupDir = getBackupDir(databaseDirectory);
    // Clean up, just in case
    IoUtils.deleteFilesHard(backupDir.listFiles());
    // Backup
    try {
      IoUtils.copyDirectory(databaseDirectory, backupDir);
    } catch (final IOException e) {
      // Try to delete a partial backup
      IoUtils.deleteFileHard(backupDir);
      // Rethrow an exception
      throw e;
    }
  }


  /**
   * Returns fully-qualified path to the backup directory. If the directory doesn't exist, it is created.
   *
   * @param databaseDirectory database directory.
   * @return fully-qualified path to the backup directory. If the directory doesn't exist, it is created.
   * @throws IOException if I/O error occurs.
   */
  private static File getBackupDir(final File databaseDirectory) throws IOException {
    final File backupDir = new File(databaseDirectory.getParentFile(), "backup/before_engine_upgrade");
    if (!backupDir.exists()) {
      if (!backupDir.mkdirs()) {
        throw new IOException("Cannot create a back up directory: " + backupDir);
      }
    }
    return backupDir;
  }


  /**
   * Restores the previously backed db in case of failure
   *
   * @param databaseDir
   */
  private void restore(final File databaseDir) throws IOException {
    // Delete db files
    if (!IoUtils.deleteFilesHard(databaseDir.listFiles())) {
      final StringBuilder leftUndeleted = new StringBuilder(11);
      final File[] files = databaseDir.listFiles();
      for (int i = 0; i < files.length; i++) {
        final File file = files[i];
        leftUndeleted.append(file);
        if (i < files.length - 1) {
          leftUndeleted.append(", ");
        }
      }
      throw new IOException("Database directory cannot be cleaned up: " + leftUndeleted);
    }
    // Restore
    final File backupDir = getBackupDir(databaseDir);
    IoUtils.copyDirectory(backupDir, databaseDir);
  }
}
