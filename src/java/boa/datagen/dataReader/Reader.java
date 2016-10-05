package boa.datagen.dataReader;


import boa.datagen.dataFormat.rawdata.RawData;

import java.util.ArrayList;

/**
 * Created by nmtiwari on 10/4/16.
 */
public abstract class Reader {

    /**
     *
     * @return: Returns the data read by the reader
     */
    public abstract RawData readData(String path);

    public abstract boolean canRead(String path);

}
