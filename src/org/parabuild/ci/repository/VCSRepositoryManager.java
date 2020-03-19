package org.parabuild.ci.repository;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.VCSRepository;
import org.parabuild.ci.object.VCSRepositoryAttribute;
import org.parabuild.ci.object.VCSServer;
import org.parabuild.ci.object.VCSServerAttribute;

import java.util.List;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * Persistence manager for repository management functionality.
 */
@SuppressWarnings("unchecked")
public final class VCSRepositoryManager {

  final static VCSRepositoryManager instance = new VCSRepositoryManager();


  /**
   * Returns the singleton.
   */
  public static VCSRepositoryManager getInstance() {
    return instance;
  }


  public VCSServer createServer(final Session session, final int type, final String name, final String description)
          throws HibernateException {

    final VCSServer vcsServer = new VCSServer();
    vcsServer.setDescription(description);
    vcsServer.setName(name);
    vcsServer.setType(type);
    session.saveOrUpdate(vcsServer);
    return vcsServer;
  }


  public VCSServer loadServer(final int id) {

    return (VCSServer) runInHibernate(new TransactionCallback() {
      @Override
      public Object runInTransaction() throws Exception {
        // Load
        return session.load(VCSServer.class, id);
      }
    });
  }


  public VCSRepository loadRepository(final int id) {

    return (VCSRepository) runInHibernate(new TransactionCallback() {
      @Override
      public Object runInTransaction() throws Exception {
        // Load
        return session.load(VCSRepository.class, id);
      }
    });
  }


  public VCSRepository createRepository(final String description, final String name, final Session session,
                                        final Integer serverId, final int type) throws HibernateException {

    final VCSRepository vcsRepository = new VCSRepository();
    vcsRepository.setDescription(description);
    vcsRepository.setServerId(serverId);
    vcsRepository.setDeleted(false);
    vcsRepository.setName(name);
    vcsRepository.setType(type);
    session.saveOrUpdate(vcsRepository);
    return vcsRepository;
  }


  public VCSServerAttribute loadServerAttribute(final Integer id) {

    return (VCSServerAttribute) runInHibernate(new TransactionCallback() {
      @Override
      public Object runInTransaction() throws Exception {
        // Load
        return session.load(VCSServerAttribute.class, id);
      }
    });
  }


  public VCSServerAttribute createServerAttribute(final Session session, final Integer serverId, final String name,
                                                  final String value)
          throws HibernateException {

    final VCSServerAttribute serverAttribute = new VCSServerAttribute();
    serverAttribute.setValue(value);
    serverAttribute.setServerId(serverId);
    serverAttribute.setName(name);
    session.saveOrUpdate(serverAttribute);
    return serverAttribute;
  }


  public VCSRepositoryAttribute createRepositoryAttribute(final String attributeName, final Session session, final Integer repositoryId, final String value) throws HibernateException {

    final VCSRepositoryAttribute repositoryAttribute = new VCSRepositoryAttribute();
    repositoryAttribute.setRepositoryId(repositoryId);
    repositoryAttribute.setName(attributeName);
    repositoryAttribute.setValue(value);

    session.saveOrUpdate(repositoryAttribute);
    return repositoryAttribute;
  }


  public VCSRepositoryAttribute loadRepositoryAttribute(final int id) {

    return (VCSRepositoryAttribute) runInHibernate(new TransactionCallback() {
      @Override
      public Object runInTransaction() throws Exception {

        // Load
        return session.load(VCSServerAttribute.class, id);
      }
    });
  }


  public List<VCSServer> getVCSServers(final Session session) throws HibernateException {

    // Query servers
    final Query query = session.createQuery("select srv from VCSServer srv order by srv.name");
    query.setCacheable(true);

    // List
    return query.list();
  }


  private List<VCSServer> getVCSServers() {
    return (List<VCSServer>) runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        return getVCSServers(session);
      }
    });
  }


  public List<VCSServerAttribute> getVCSServerAttributes(final Session session) throws HibernateException {

    // Query servers
    final Query query = session.createQuery("select srvAttr from VCSServerAttribute srvAttr");
    query.setCacheable(true);

    // List
    return query.list();
  }
}
