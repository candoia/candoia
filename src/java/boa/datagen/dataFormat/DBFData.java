package boa.datagen.dataFormat;

import boa.datagen.dataReader.DataReadingTechnologies;
import com.linuxense.javadbf.DBFReader;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class DBFData extends  RawData{
    private DBFReader data;
    private DataReadingTechnologies techUsedInReading;


    public void setData(DBFReader val){
        this.data = val;
    }


    public DBFReader getData(){
        return this.data;
    }

    public void setTechUsedInReading(DataReadingTechnologies techUsedInReading){
        this.techUsedInReading = techUsedInReading;
    }

    public DataReadingTechnologies getTechUsedInReading(){
        return this.techUsedInReading;
    }
}
