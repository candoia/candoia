package boa.datagen.dataMapper;

import boa.datagen.Domains;
import boa.datagen.dataFormat.rawdata.RawData;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class MSRGITDataMapper extends DataMapper {

    @Override
    public com.google.protobuf.GeneratedMessage processRawData(RawData rawData) {
        if(rawData.getDomainType() != Domains.MSR){
            throw new IllegalArgumentException("Dataset is not a MSR Data");
        }
        throw new UnsupportedOperationException();
    }
}
