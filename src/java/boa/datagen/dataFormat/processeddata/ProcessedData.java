package boa.datagen.dataFormat.processeddata;

import boa.datagen.dataFormat.Data;
import boa.datagen.dataReader.DataReadingTechnologies;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class ProcessedData extends Data {

    Object processedData;

    public Object getProcessedData() {
        return processedData;
    }

    public void setProcessedData(Object processedData) {
        this.processedData = processedData;
    }

}
