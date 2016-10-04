package boa.datagen.dataReader;

import boa.datagen.dataFormat.Domains;
import boa.datagen.dataFormat.RawData;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class DBFFileReader extends Reader {

    @Override
    public RawData readData(String path) {
        try{
            InputStream inputStream  = new FileInputStream(path); // path is path to dbf file
            DBFReader reader = new DBFReader(inputStream);

            // get the field count if you want for some reasons like the following
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
}
