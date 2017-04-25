//$Id: Settings.java,v 1.9 2004/11/26 05:20:00 steveebersole Exp $
package net.sf.hibernate.cfg;

import java.util.Map;

import net.sf.hibernate.cache.CacheProvider;
import net.sf.hibernate.cache.QueryCacheFactory;
import net.sf.hibernate.connection.ConnectionProvider;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.transaction.TransactionFactory;
import net.sf.hibernate.transaction.TransactionManagerLookup;
import net.sf.hibernate.exception.SQLExceptionConverter;

/**
 * Settings that affect the behaviour of Hibernate at runtime.
 * 
 * @author Gavin King
 */
public final class Settings {
	
	private boolean showSql;
	private boolean outerJoinFetchEnabled;
	private Integer maximumFetchDepth;
	private Map querySubstitutions;
	private Dialect dialect;
	private int jdbcBatchSize;
	private boolean scrollableResultSetsEnabled;
	private boolean getGeneratedKeysEnabled;
	private String defaultSchemaName;
	private Integer statementFetchSize;
	private ConnectionProvider connectionProvider;
	private TransactionFactory transactionFactory;
	private TransactionManagerLookup transactionManagerLookup;
	private String sessionFactoryName;
	private boolean autoCreateSchema;
	private boolean autoDropSchema;
	private boolean autoUpdateSchema;
	private CacheProvider cacheProvider;
	private boolean queryCacheEnabled;
	private QueryCacheFactory queryCacheFactory;
	private boolean minimalPutsEnabled;
	private boolean jdbcBatchVersionedData;
	private SQLExceptionConverter sqlExceptionConverter;
	private boolean wrapResultSetsEnabled;

	public String getDefaultSchemaName() {
		return defaultSchemaName;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public int getJdbcBatchSize() {
		return jdbcBatchSize;
	}

	public Map getQuerySubstitutions() {
		return querySubstitutions;
	}

	public boolean isShowSqlEnabled() {
		return showSql;
	}

	public boolean isOuterJoinFetchEnabled() {
		return outerJoinFetchEnabled;
	}

	public boolean isScrollableResultSetsEnabled() {
		return scrollableResultSetsEnabled;
	}

	public boolean isGetGeneratedKeysEnabled() {
		return getGeneratedKeysEnabled;
	}

	public boolean isMinimalPutsEnabled() {
		return minimalPutsEnabled;
	}

	void setDefaultSchemaName(String string) {
		defaultSchemaName = string;
	}

	void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	void setJdbcBatchSize(int i) {
		jdbcBatchSize = i;
	}

	void setQuerySubstitutions(Map map) {
		querySubstitutions = map;
	}

	void setShowSqlEnabled(boolean b) {
		showSql = b;
	}

	void setMinimalPutsEnabled(boolean b) {
		minimalPutsEnabled = b;
	}

	void setOuterJoinFetchEnabled(boolean b) {
		outerJoinFetchEnabled = b;
	}

	void setScrollableResultSetsEnabled(boolean b) {
		scrollableResultSetsEnabled = b;
	}

	void setGetGeneratedKeysEnabled(boolean b) {
		getGeneratedKeysEnabled = b;
	}

	public Integer getStatementFetchSize() {
		return statementFetchSize;
	}

	void setStatementFetchSize(Integer integer) {
		statementFetchSize = integer;
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	void setConnectionProvider(ConnectionProvider provider) {
		connectionProvider = provider;
	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	void setTransactionFactory(TransactionFactory factory) {
		transactionFactory = factory;
	}

	public String getSessionFactoryName() {
		return sessionFactoryName;
	}

	void setSessionFactoryName(String string) {
		sessionFactoryName = string;
	}

	public boolean isAutoCreateSchema() {
		return autoCreateSchema;
	}

	public boolean isAutoDropSchema() {
		return autoDropSchema;
	}

	public boolean isAutoUpdateSchema() {
		return autoUpdateSchema;
	}

	void setAutoCreateSchema(boolean b) {
		autoCreateSchema = b;
	}

	void setAutoDropSchema(boolean b) {
		autoDropSchema = b;
	}

	void setAutoUpdateSchema(boolean b) {
		autoUpdateSchema = b;
	}

	public Integer getMaximumFetchDepth() {
		return maximumFetchDepth;
	}

	void setMaximumFetchDepth(Integer i) {
		maximumFetchDepth = i;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}

	void setCacheProvider(CacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	public TransactionManagerLookup getTransactionManagerLookup() {
		return transactionManagerLookup;
	}

	void setTransactionManagerLookup(TransactionManagerLookup lookup) {
		transactionManagerLookup = lookup;
	}

	public boolean isQueryCacheEnabled() {
		return queryCacheEnabled;
	}

	void setQueryCacheEnabled(boolean b) {
		queryCacheEnabled = b;
	}

	public QueryCacheFactory getQueryCacheFactory() {
		return queryCacheFactory;
	}

	public void setQueryCacheFactory(QueryCacheFactory queryCacheFactory) {
		this.queryCacheFactory = queryCacheFactory;
	}

	public boolean isJdbcBatchVersionedData() {
		return jdbcBatchVersionedData;
	}

	public void setJdbcBatchVersionedData(boolean jdbcBatchVersionedData) {
		this.jdbcBatchVersionedData = jdbcBatchVersionedData;
	}

	public SQLExceptionConverter getSQLExceptionConverter() {
		return sqlExceptionConverter;
	}

	void setSQLExceptionConverter(SQLExceptionConverter sqlExceptionConverter) {
		this.sqlExceptionConverter = sqlExceptionConverter;
	}

	public boolean isWrapResultSetsEnabled() {
		return wrapResultSetsEnabled;
	}

	public void setWrapResultSetsEnabled(boolean wrapResultSetsEnabled) {
		this.wrapResultSetsEnabled = wrapResultSetsEnabled;
	}
}
