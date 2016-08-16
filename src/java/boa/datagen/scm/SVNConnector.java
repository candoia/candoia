package boa.datagen.scm;

import java.io.File;
import java.util.*;

        import org.tmatesoft.svn.core.*;
        import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
        import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
        import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
        import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
        import org.tmatesoft.svn.core.io.SVNRepository;
        import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
        import org.tmatesoft.svn.core.wc.*;

/**
 * @author hoan
 * @author rdyer
 */
public class SVNConnector extends AbstractConnector {

    static {
        // For using over http:// and https://
        DAVRepositoryFactory.setup();
        // For using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();
        // For using over file:///
        FSRepositoryFactory.setup();
    }

    private SVNRepository repository = null;
    private SVNURL url;

    private ISVNAuthenticationManager authManager;
    private SVNClientManager clientManager = null;

    private long lastSeenRevision = 1l;
    private long latestRevision = 0l;

    public SVNConnector(final String url) {
        this(url, "", "");
    }

    public SVNConnector() {
            this.url = null;
            this.authManager = null;
            this.repository = null;
    }


    public SVNConnector(final String url, final String username, final String password) {
        try {
            this.url = SVNURL.fromFile(new File(url));

            this.authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);

            this.repository = SVNRepositoryFactory.create(this.url);
            this.repository.setAuthenticationManager(this.authManager);

            this.latestRevision = this.repository.getLatestRevision();
        } catch (final SVNException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        repository.closeSession();
    }

    @Override
    public boolean clear() {
        this.close();
        return true;
    }

    @Override
    public boolean initialize(String path) {
    	File f = new File(path);
    	if(f.isDirectory()){
    		if(f.listFiles().length == 1){
    			f = f.listFiles()[0];
    		}
    	}
        try {
            this.url = SVNURL.fromFile(f);
            this.authManager = SVNWCUtil.createDefaultAuthenticationManager();
            this.repository = SVNRepositoryFactory.create(this.url);
            this.repository.setAuthenticationManager(this.authManager);
            this.latestRevision = this.repository.getLatestRevision();
        } catch (final SVNException e) {
            return false;
        }
        return true;
    }

    public String getLastCommitId() {
        if (latestRevision == 0l) return null;
        return "" + latestRevision;
    }

    public void setLastSeenCommitId(final String id) {
        lastSeenRevision = Long.parseLong(id);
    }

    @Override
    public AbstractConnector getNewInstance() {
        return new SVNConnector();
    }

    protected void setRevisions() {
        if (latestRevision < 1l) return;

        try {
            final Collection<SVNLogEntry> logEntries = repository.log(new String[] {""}, null, lastSeenRevision + 1l, latestRevision, true, true);

            for (final SVNLogEntry logEntry : logEntries) {
                final SVNCommit revision = new SVNCommit(repository, this);

                revision.setId("" + logEntry.getRevision());
                if(logEntry.getAuthor()==null)
                    revision.setCommitter(logEntry.getAuthor());
                else
                    revision.setCommitter("anonymous");
                revision.setDate(logEntry.getDate());
                revision.setMessage(logEntry.getMessage());

                if (logEntry.getChangedPaths() != null && logEntry.getChangedPaths().size() > 0) {
                    final HashMap<String, String> rChangedPaths = new HashMap<String, String>();
                    final HashMap<String, String> rRemovedPaths = new HashMap<String, String>();
                    final HashMap<String, String> rAddedPaths = new HashMap<String, String>();
                    for (final Iterator changedPaths = logEntry.getChangedPaths().keySet().iterator(); changedPaths.hasNext(); ) {
                        final SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                        if (repository.checkPath(entryPath.getPath(), logEntry.getRevision()) == SVNNodeKind.FILE) {
                            if (entryPath.getType() == SVNLogEntryPath.TYPE_DELETED)
                                rRemovedPaths.put(entryPath.getPath(), entryPath.getCopyPath());
                            else if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED)
                                rAddedPaths.put(entryPath.getPath(), entryPath.getCopyPath());
                            else
                                rChangedPaths.put(entryPath.getPath(), entryPath.getCopyPath());
                        }
                    }
                    revision.setChangedPaths(rChangedPaths);
                    revision.setRemovedPaths(rRemovedPaths);
                    revision.setAddedPaths(rAddedPaths);
                }

                this.revisions.add(revision);
            }
        } catch (final SVNException e) {
                e.printStackTrace();
        }
    }

    public void getTags(final List<String> names, final List<String> commits) {
        // TODO
    }

    public void getBranches(final List<String> names, final List<String> commits) {
        // TODO
    }
}


