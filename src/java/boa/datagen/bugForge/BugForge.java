/**
 * 
 */
package boa.datagen.bugForge;

import java.util.ArrayList;

import boa.types.Issues.Issue;

/**
 * @author nmtiwari
 *
 */
public interface BugForge {
	public void buildIssue(boa.types.Toplevel.Project.Builder pr, String details); 
}
