/**
 * 
 */
package boa.debugger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import boa.debugger.value.Value;
import boa.compiler.BoaCompiler;
import boa.compiler.ast.Node;
import boa.compiler.visitors.AbstractVisitor;
import boa.debugger.Env.EmptyEnv;
import boa.debugger.Env.ExtendEnv;

/**
 * @author nmtiwari
 *
 */
public class Evaluator extends AbstractVisitor<Value, Env<Value>>{
	public static ArrayList<String> pathToDataSet = new ArrayList<>();
	public static boolean DEBUG = false;
	public static Logger LOG = Logger.getLogger(Evaluator.class);
	ByteArrayOutputStream op = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(op);
	PrintStream old = System.out;

	public Evaluator() {
		// TODO Auto-generated constructor stub
	}
	
	protected Env<Value> initEnv() {
		Env<Value> initEnv = new EmptyEnv<Value>();
		return initEnv;
	}
	
	@Override
	public Value start(Node n, Env<Value> env) {
		LOG.info("Starting...");
		env = initEnv();
		return n.accept(this, env);
	}

}
