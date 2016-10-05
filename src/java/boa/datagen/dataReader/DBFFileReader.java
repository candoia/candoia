package boa.datagen.dataReader;

import boa.datagen.dataFormat.rawdata.DBFData;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class DBFFileReader extends Reader {

    @Override
    public DBFData readData(String path) {
        DBFData domaindata = new DBFData();
        try{
            InputStream inputStream  = new FileInputStream(path); // path is path to dbf file
            DBFReader reader = new DBFReader(inputStream);
            int numberOfFields = reader.getFieldCount();

            DBFField[] dataFields = new DBFField[numberOfFields];

            for( int i=0; i<numberOfFields; i++) {
                DBFField field = reader.getField( i);
//                System.out.println("name: " + field.getName());
                dataFields[i] = field;
            }

            Object []rowObjects;
            int recordCounter = reader.getRecordCount();
            Object[][] dataRecords = new Object[recordCounter][];

            int counter = 0;
            domaindata.initializeRecord(recordCounter);
            while( (rowObjects = reader.nextRecord()) != null) {
                domaindata.setRowObjectsAtIndex(rowObjects, counter);
                counter++;
            }
            inputStream.close();
            domaindata.setFields(dataFields);
//            domaindata.setRowObjects(dataRecords);
        }catch(IOException e){
            e.printStackTrace();
        }
        return domaindata;
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
