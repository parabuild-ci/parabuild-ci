//$Id: SchemaExportTask.java,v 1.11 2004/11/23 17:10:25 turin42 Exp $
package net.sf.hibernate.tool.hbm2ddl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.NamingStrategy;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.ReflectHelper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * An Ant task for <tt>SchemaExport</tt>.
 * 
 * <pre>
 * &lt;taskdef name="schemaexport"
 *     classname="net.sf.hibernate.tool.hbm2ddl.SchemaExportTask"
 *     classpathref="class.path"/&gt;
 *
 * &lt;schemaexport
 *     properties="${build.classes.dir}/hibernate.properties"
 *     quiet="no"
 *     text="no"
 *     drop="no"
 *     delimiter=";"
 *     output="${build.dir}/schema-export.sql"&gt;
 *     &lt;fileset dir="${build.classes.dir}"&gt;
 *         &lt;include name="*.hbm.xml"/&gt;
 *     &lt;/fileset&gt;
 * &lt;/schemaexport&gt;
 * </pre>
 * 
 * @see SchemaExport
 * @author Rong C Ou
 */
public class SchemaExportTask extends MatchingTask {
	
	private List fileSets = new LinkedList();
	private File propertiesFile = null;
	private String configurationFile = null;
	private String outputFile = null;
	private boolean quiet = false;
	private boolean text = false;
	private boolean drop = false;
	private String delimiter = null;
	private String namingStrategy = null;

	public void addFileset(FileSet set) {
		fileSets.add(set);
	}
	
	/**
	 * Set a properties file
	 * @param propertiesFile the properties file name
	 */
	public void setProperties(File propertiesFile) {
		if ( !propertiesFile.exists() ) {
			throw new BuildException("Properties file: " + propertiesFile + " does not exist.");
		}

		log("Using properties file " + propertiesFile, Project.MSG_DEBUG);
		this.propertiesFile = propertiesFile;
	}
	
	/**
	 * Set a <literal>.cfg.xml</literal> file, which will be
	 * loaded as a resource, from the classpath
	 * @param configurationFile the path to the resource
	 */
	public void setConfig(String configurationFile) {
		this.configurationFile = configurationFile;
	}
	
	/**
	 * Enable "quiet" mode. The schema will not be
	 * written to standard out.
	 * @param quiet true to enable quiet mode
	 */
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}
	
	/**
	 * Enable "text-only" mode. The schema will not
	 * be exported to the database.
	 * @param text true to enable text-only mode
	 */
	public void setText(boolean text) {
		this.text = text;
	}
	
	/**
	 * Enable "drop" mode. Database objects will be
	 * dropped but not recreated.
	 * @param drop true to enable drop mode
	 */
	public void setDrop(boolean drop) {
		this.drop = drop;
	}
	
	/**
	 * Set the end of statement delimiter for the generated script
	 * @param delimiter the delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * Set the script output file
	 * @param outputFile the file name
	 */
	public void setOutput(String outputFile) {
		this.outputFile = outputFile;
	}
	
	/**
	 * Execute the task
	 */
	public void execute() throws BuildException {
		try {
			Configuration cfg = getConfiguration();
			SchemaExport schemaExport = getSchemaExport(cfg);

			if (drop) {
				schemaExport.drop(!quiet, !text);
			} 
			else {
				schemaExport.create(!quiet, !text);
			}
		} 
		catch (HibernateException e) {
			throw new BuildException("Schema text failed: " + e.getMessage(), e);
		} 
		catch (FileNotFoundException e) {
			throw new BuildException("File not found: " + e.getMessage(), e);
		}
		catch (IOException e) {
			throw new BuildException("IOException : " + e.getMessage(), e);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private String[] getFiles() {
		
		List files = new LinkedList();
		for ( Iterator i = fileSets.iterator(); i.hasNext(); ) {
			
			FileSet fs = (FileSet) i.next();
			DirectoryScanner ds = fs.getDirectoryScanner(project);

			String[] dsFiles = ds.getIncludedFiles();
			for (int j = 0; j < dsFiles.length; j++) {
				File f = new File(dsFiles[j]);
				if ( !f.isFile() ) {
					f = new File( ds.getBasedir(), dsFiles[j] );
				}

				files.add( f.getAbsolutePath() );
			}
		}

		return (String[]) files.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
	}

	private Configuration getConfiguration() throws Exception {
		Configuration cfg = new Configuration();
		if (namingStrategy!=null) cfg.setNamingStrategy(
			(NamingStrategy) ReflectHelper.classForName(namingStrategy).newInstance()
		);
		if (configurationFile != null) cfg.configure( new File(configurationFile) );

		String[] files = getFiles();
		for (int i = 0; i < files.length; i++) {
			String filename = files[i];
			if ( filename.endsWith(".jar") ) {
				cfg.addJar( new File(filename) );
			} 
			else {
				cfg.addFile(filename);
			}
		}
		return cfg;
	}

	private SchemaExport getSchemaExport(Configuration cfg) throws HibernateException, IOException {
		SchemaExport schemaExport;
		if (propertiesFile == null) {
			schemaExport = new SchemaExport(cfg);
		} 
		else {
			Properties properties = new Properties();
			properties.load( new FileInputStream(propertiesFile) );
			schemaExport = new SchemaExport(cfg, properties);
		}
		schemaExport.setOutputFile(outputFile);
		schemaExport.setDelimiter(delimiter);
		return schemaExport;
	}

	public void setNamingStrategy(String namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

}

