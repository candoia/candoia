package boa.datagen.dataFormat.rawdata;

import boa.datagen.dataMapper.farsdata.FARSDataType;
import boa.datagen.dataReader.DataReadingTechnologies;
import com.linuxense.javadbf.DBFField;

import java.util.ArrayList;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class CVSData extends RawData {

    public CVSData(){
        this.fields = new ArrayList<>();
        this.rowObjects = new ArrayList<ArrayList<String>>();
    }

    private ArrayList<String> fields;

    private ArrayList<ArrayList<String>> rowObjects;

    private DataReadingTechnologies techUsedInReading;

    public ArrayList<String> getFields() {
        return fields;
    }

    public ArrayList<ArrayList<String>> getRowObjects() {
        return rowObjects;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public void initializeRecord(){
        this.rowObjects = new ArrayList<ArrayList<String>>();
    }

    public void addRowObjects(ArrayList<String> rowObjects) {
        this.rowObjects.add(rowObjects);
    }

    public void setTechUsedInReading(DataReadingTechnologies techUsedInReading){
        this.techUsedInReading = techUsedInReading;
    }

    public DataReadingTechnologies getTechUsedInReading(){
        return this.techUsedInReading;
    }
}
