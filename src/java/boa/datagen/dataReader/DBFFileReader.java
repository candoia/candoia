package boa.datagen.dataReader;

import boa.datagen.dataFormat.RawData;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import boa.datagen.dataFormat.*;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class DBFFileReader extends Reader {

    @Override
    public DBFData readData(String path) {
        try{
            InputStream inputStream  = new FileInputStream(path); // path is path to dbf file
            DBFReader reader = new DBFReader(inputStream);

            int numberOfFields = reader.getFieldCount();

            for( int i=0; i<numberOfFields; i++) {
                DBFField field = reader.getField( i);
                System.out.println( field.getName());
            }

            Object []rowObjects;
            while( (rowObjects = reader.nextRecord()) != null) {
                for( int i=0; i<rowObjects.length; i++) {
                    System.out.println( rowObjects[i]);
                }
            }
            inputStream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean canRead(String path) {
        File dir = new File(path);
        boolean isRedable = true;
        if(dir.isDirectory()){
            for(File f: dir.listFiles()){
                isRedable = isRedable && f.getName().endsWith(".dbf");
            }
        }else{
            isRedable = isRedable && dir.getName().endsWith(".dbf");
        }
        return isRedable;
    }
}
