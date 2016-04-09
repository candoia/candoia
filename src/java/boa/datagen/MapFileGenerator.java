package boa.datagen;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

public class MapFileGenerator {
	// private final static String SEQ_FILE_PATH =
	// "/Users/nmtiwari/GIT/GeneratedSeqFile/svn/ast";

	public static void main(String[] args) throws Exception {
		if ((args[1] + "/ast").isEmpty()) {
			System.out.println("Missing path to sequence file. Please specify it in the properties file.");
			return;
		}
		String base = "hdfs://boa-njt/";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(args[1] + "/ast");
		SequenceFile.Sorter sort = new SequenceFile.Sorter(fs, Text.class, BytesWritable.class, conf);
		Path[] input = { path };
		path = new Path(args[1] + "/data");
		String name = path.getName();
		sort.sort(input, path, false);
		if (fs.isFile(path)) {
			if (path.getName().equals(MapFile.DATA_FILE_NAME)) {
				MapFile.fix(fs, path.getParent(), Text.class, BytesWritable.class, false, conf);
			} else {
				Path dataFile = new Path(path.getParent(), MapFile.DATA_FILE_NAME);
				fs.rename(path, dataFile);
				Path dir = new Path(path.getParent(), name);
				fs.mkdirs(dir);
				fs.rename(dataFile, new Path(dir, dataFile.getName()));
				MapFile.fix(fs, dir, Text.class, BytesWritable.class, false, conf);
			}
		} else {
			FileStatus[] files = fs.listStatus(path);
			for (FileStatus file : files) {
				path = file.getPath();
				if (fs.isFile(path)) {
					Path dataFile = new Path(path.getParent(), MapFile.DATA_FILE_NAME);
					fs.rename(path, dataFile);
					MapFile.fix(fs, dataFile.getParent(), Text.class, BytesWritable.class, false, conf);
					break;
				}
			}
		}
		File ast = new File(args[1] + "/ast");
		if (ast.exists())
			ast.delete();

		ast = new File(args[1] + "/JSONData1.json");
		if (ast.exists())
			ast.delete();

		fs.close();
	}

	public static void generateMap(String astPath) throws Exception {
		if ((astPath + "/ast").isEmpty()) {
			System.out.println("Missing path to sequence file. Please specify it in the properties file.");
			return;
		}
		String base = "hdfs://boa-njt/";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path(astPath + "/ast.seq");
		SequenceFile.Sorter sort = new SequenceFile.Sorter(fs, Text.class, BytesWritable.class, conf);
		Path[] input = { path };
		path = new Path(astPath + "/data");
		String name = path.getName();
		sort.sort(input, path, false);
		if (fs.isFile(path)) {
			if (path.getName().equals(MapFile.DATA_FILE_NAME)) {
				MapFile.fix(fs, path.getParent(), Text.class, BytesWritable.class, false, conf);
			} else {
				Path dataFile = new Path(path.getParent(), MapFile.DATA_FILE_NAME);
				fs.rename(path, dataFile);
				Path dir = new Path(path.getParent(), name);
				fs.mkdirs(dir);
				fs.rename(dataFile, new Path(dir, dataFile.getName()));
				MapFile.fix(fs, dir, Text.class, BytesWritable.class, false, conf);
			}
		} else {
			FileStatus[] files = fs.listStatus(path);
			for (FileStatus file : files) {
				path = file.getPath();
				if (fs.isFile(path)) {
					Path dataFile = new Path(path.getParent(), MapFile.DATA_FILE_NAME);
					fs.rename(path, dataFile);
					MapFile.fix(fs, dataFile.getParent(), Text.class, BytesWritable.class, false, conf);
					break;
				}
			}
		}
		File ast = new File(astPath + "/ast.seq");
		if (ast.exists()) {
			ast.delete();
		}
		fs.close();

	}
}