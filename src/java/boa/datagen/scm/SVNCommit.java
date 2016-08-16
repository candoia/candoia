package boa.datagen.scm;

/**
 * Created by nmtiwari on 7/25/16.
 */

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import boa.types.Shared.Person;

/**
 * Concrete implementation of a commit for SVN.
 *
 * @author rdyer
 */
public class SVNCommit extends AbstractCommit {
    // since SVN uses longs, mirror the commit ID here as a long to avoid boxing/unboxing to String
    private long svnId = -1;


    /** {@inheritDoc} */
    public void setId(final String id) {
        this.svnId = Long.parseLong(id);
        super.setId(id);
    }

    // the repository the commit lives in - should already be connected!
    private final SVNRepository repository;

    public SVNCommit(final SVNRepository repository, SVNConnector conn) {
        super(conn);
        this.repository = repository;
    }

    @Override
    /** {@inheritDoc} */
    protected String getFileContents(final String path) {
        try {
            buffer.reset();
            repository.getFile(path, svnId, null, buffer);
            return buffer.toString();
        } catch (final SVNException e) {
            if (debug)
                System.err.println("SVN Error getting contents for '" + path + "' at revision " + svnId + ": " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    @Override
    /** {@inheritDoc} */
    protected Person parsePerson(final String s) {
        if (s == null) return null;

        final String name = String.valueOf(s.hashCode());

        final Person.Builder person = Person.newBuilder();
        person.setUsername(name);
        person.setRealName(name);
        person.setEmail(name);
        return person.build();
    }
}
