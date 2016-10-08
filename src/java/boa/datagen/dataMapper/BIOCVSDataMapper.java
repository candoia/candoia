package boa.datagen.dataMapper;

import boa.datagen.Domains;
import boa.datagen.dataFormat.rawdata.CVSData;
import boa.datagen.dataFormat.rawdata.DBFData;
import boa.datagen.dataFormat.rawdata.RawData;
import boa.datagen.dataMapper.biodata.BioDataMapper;
import boa.datagen.dataMapper.farsdata.AccidentDataMapper;
import boa.datagen.dataMapper.farsdata.FARSDataType;
import boa.datagen.dataReader.CVSFileReader;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class BIOCVSDataMapper extends DataMapper {

    @Override
    public com.google.protobuf.GeneratedMessage processRawData(RawData rawData) {
        if(rawData.getDomainType() != Domains.BIO){
            throw new IllegalArgumentException("Dataset is not a BIO Data");
        }
        CVSData data = (CVSData)rawData;
        BioDataMapper mapper = new BioDataMapper();
        return mapper.processData(data);
    }
}
