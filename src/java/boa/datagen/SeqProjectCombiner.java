/*
 * Copyright 2015, Hridesh Rajan, Robert Dyer, Hoan Nguyen
 *                 and Iowa State University of Science and Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boa.datagen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import com.google.protobuf.CodedInputStream;

import boa.datagen.candoia.CandoiaConfiguration;
import boa.types.Ast.ASTRoot;
import boa.types.Toplevel.Project;

/**
 * @author hoan
 * @author hridesh
 */
public class SeqProjectCombiner {

	public static void main(String[] args) {
		try {
			combine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void combine() throws IOException {
		System.out.println("Combiner method has been called");
		Configuration conf = new Configuration();
		// conf.set("fs.default.name", "hdfs://boa-njt/");
		FileSystem fileSystem = FileSystem.get(conf);
		String base = DefaultProperties.GH_JSON_CACHE_PATH;

		HashMap<String, String> sources = new HashMap<String, String>();
		HashSet<String> marks = new HashSet<String>();
		FileStatus[] files = fileSystem.listStatus(new Path(base));
		PrintWriter cache = new PrintWriter(DefaultProperties.GH_JSON_CACHE_PATH+ "/" + CandoiaConfiguration.getCachefilename());
		for (int i = 0; i < files.length; i++) {
			FileStatus file = files[i];
			String name = file.getPath().getName();
			if (name.contains("projects") && name.endsWith(".seq")) {
				SequenceFile.Reader r = new SequenceFile.Reader(fileSystem, file.getPath(), conf);
				final Text key = new Text();
				final BytesWritable value = new BytesWritable();
				try {
					while (r.next(key, value)) {
						String s = key.toString();
						if (marks.contains(s))
							continue;
						Project p = Project
								.parseFrom(CodedInputStream.newInstance(value.getBytes(), 0, value.getLength()));
						if (p.getCodeRepositoriesCount() > 0 && p.getCodeRepositories(0).getRevisionsCount() > 0)
							marks.add(s);
						sources.put(s, name);
						cache.println(p.getProjectUrl());
					}
				} catch (Exception e) {
					System.err.println(name);
					e.printStackTrace();
				}
				r.close();
				cache.close();
			}
			else if (name.contains("ast") && name.endsWith(".seq")) {
				SequenceFile.Reader r = new SequenceFile.Reader(fileSystem, file.getPath(), conf);
				final Text key = new Text();
				final BytesWritable value = new BytesWritable();
				try {
					while (r.next(key, value)) {
						String s = key.toString();
						if (marks.contains(s))
							continue;
						ASTRoot p = ASTRoot
								.parseFrom(CodedInputStream.newInstance(value.getBytes(), 0, value.getLength()));
						if (p.getNamespacesCount() > 0)
							marks.add(s);
						sources.put(s, name);
					}
				} catch (Exception e) {
					System.err.println(name);
					e.printStackTrace();
				}
				r.close();
			}
		}
		SequenceFile.Writer w = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/projects.seq"),
				Text.class, BytesWritable.class);
		SequenceFile.Writer astWriter = SequenceFile.createWriter(fileSystem, conf, new Path(base + "/ast.seq"),
				Text.class, BytesWritable.class);
		for (int i = 0; i < files.length; i++) {
			FileStatus file = files[i];
			String name = file.getPath().getName();
			if (name.contains("projects") && name.endsWith(".seq")) {
				SequenceFile.Reader r = new SequenceFile.Reader(fileSystem, file.getPath(), conf);
				final Text key = new Text();
				final BytesWritable value = new BytesWritable();
				try {
					while (r.next(key, value)) {
						String s = key.toString();
						if (sources.get(s).equals(name))
							w.append(key, value);
					}
				} catch (Exception e) {
					System.err.println(name);
					e.printStackTrace();
				}
				r.close();
			} 
			else if (name.contains("ast") && name.endsWith(".seq")) {
				SequenceFile.Reader r = new SequenceFile.Reader(fileSystem, file.getPath(), conf);
				final Text key = new Text();
				final BytesWritable value = new BytesWritable();
				try {
					while (r.next(key, value)) {
						String s = key.toString();
						if (sources.get(s).equals(name))
							astWriter.append(key, value);
					}
				} catch (Exception e) {
					System.err.println(name);
					e.printStackTrace();
				}
				r.close();
			}
		}
		astWriter.close();
		w.close();

		fileSystem.close();
		clean(DefaultProperties.GH_JSON_CACHE_PATH);
	}

	public static void clean(String path){
		File dir = new File(path);
		for(File file :  dir.listFiles()){
			if((!"ast.seq".equals(file.getName())) && (!"projects.seq".equals(file.getName())) && (!CandoiaConfiguration.getCachefilename().equals(file.getName()))){
				file.delete();
			}
		}
	}
	
}
