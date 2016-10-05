package boa.datagen;

import boa.transportation.types.Transportation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * Created by nmtiwari on 10/5/16.
 */
public class DataSetWriter {
    private SequenceFile.Writer writer;

    public DataSetWriter(String filePathWithFileName){
        Configuration conf = new Configuration();
        FileSystem fileSystem = null;
        try{
            fileSystem = FileSystem.get(conf);
            this.writer = SequenceFile.createWriter(fileSystem, conf,
                    new Path(filePathWithFileName), Text.class, BytesWritable.class);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void write(com.google.protobuf.GeneratedMessage data, String id){
                try {
                    this.writer.append(new Text(id), new BytesWritable(data.toByteArray()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    public void close(){
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
