package boa.datagen.forges.sf;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

/**
 * Simple snippet which shows how to clone a repository from a remote source
 *
 * @author dominik.stadler at gmx.at
 */
public class GITRepositoryCloner {

	private static String REMOTE_URL = "";

	public static boolean clone(String URL, String path) {
		// prepare a new folder for the cloned repository
		String localpaths = path;
		String url = URL;
		REMOTE_URL = url;
		File localPath = new File(localpaths);
		if (!localPath.exists())
			localPath.mkdir();
		// then clone
			Git result;
			try {
				result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call();
				result.getRepository().close();
			} catch (GitAPIException | org.eclipse.jgit.api.errors.JGitInternalException e) {
				return false;
			}
			
			return true;
	}

	public static void main(String[] args)
			throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		// prepare a new folder for the cloned repository
		String localpath = args[2];
		System.out.println(localpath);
		String url = args[0]; //+ " " + args[1];
		REMOTE_URL = url;
		File localPath = new File(localpath);
		if (!localPath.exists())
			localPath.mkdir();
		// then clone
		Git result = null;
		try {
			// Note: the call() returns an opened repository already which needs
			// to be closed to avoid file handle leaks!

			// workaround for
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=474093
			result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call();
			result.getRepository().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}