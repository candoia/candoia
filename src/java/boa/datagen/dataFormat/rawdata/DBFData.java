package boa.datagen.dataFormat.rawdata;

import boa.datagen.dataMapper.farsdata.FARSDataType;
import boa.datagen.dataReader.DataReadingTechnologies;
import com.linuxense.javadbf.DBFField;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class DBFData extends RawData {

    private DBFField[] fields;

    private Object[][] rowObjects;

    private FARSDataType farsDataType;

    public void setFarsDataType(FARSDataType farsDataType) {
        this.farsDataType = farsDataType;
    }

    public FARSDataType getFarsDataType() {
        return farsDataType;
    }

    private DataReadingTechnologies techUsedInReading;

    public DBFField[] getFields() {
        return fields;
    }

    public Object[][] getRowObjects() {
        return rowObjects;
    }

    public void setFields(DBFField[] fields) {
        this.fields = fields;
    }

    public void setRowObjects(Object[][] rowObjects) {
        this.rowObjects = rowObjects;
    }

    public void initializeRecord(int size){
        this.rowObjects = new Object[size][];
    }

    public void setRowObjectsAtIndex(Object[] rowObjects, int index) {
        this.rowObjects[index] = rowObjects;
    }

    public void setTechUsedInReading(DataReadingTechnologies techUsedInReading){
        this.techUsedInReading = techUsedInReading;
    }

    public DataReadingTechnologies getTechUsedInReading(){
        return this.techUsedInReading;
    }
}
