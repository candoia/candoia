package boa.datagen.dataReader;

import boa.datagen.dataFormat.Data;
import boa.datagen.dataFormat.Domains;
import boa.datagen.dataFormat.RawData;


/**
 * Created by nmtiwari on 10/4/16.
 */
public abstract class Reader {


    /**
     *
     * @return: Returns the data read by the reader
     */
    public abstract RawData readData(String path);

}
