/*
 * ====================================================================
 * Copyright (c) 2004-2007 TMate Software Ltd.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://svnkit.com/license.html
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package boa.datagen.forges.sf;

import boa.datagen.scm.SVNConnector;

import java.io.File;
import java.io.IOException;

public class SVNRepositoryCloner {

	// public static void clone(String URL, String repoPath) throws SVNException
	// {
	// SVNURL svnurl = SVNURL.parseURIDecoded(URL);
	// SVNRepository srcRepository = SVNRepositoryFactory.create(svnurl);
	// srcRepository.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager());
	// SVNClientManager ourClientManager = SVNClientManager.newInstance();
	// ourClientManager.setAuthenticationManager(SVNWCUtil.createDefaultAuthenticationManager());
	// SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
	// updateClient.setIgnoreExternals(true);
	//
	// long latestRevision = srcRepository.getLatestRevision();
	// if (updateClient.doCheckout(svnurl, new File(repoPath), SVNRevision.HEAD,
	// SVNRevision.HEAD, SVNDepth.INFINITY,
	// true) == latestRevision) {
	// ourClientManager.dispose();
	// }
	// }

	public static boolean clone(String URL, String repoPath) throws IOException {
		File dest = new File(repoPath);
		if (!dest.exists()) {
			dest.mkdirs();
		}
		String line = "rsync -av " + URL + " " + repoPath;
		Runtime commandPrompt = Runtime.getRuntime();
		try {
			Process powershell = commandPrompt.exec(line);
			powershell.waitFor();
			return true;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

}
