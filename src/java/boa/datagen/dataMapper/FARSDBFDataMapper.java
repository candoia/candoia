package boa.datagen.dataMapper;

import boa.datagen.Domains;
import boa.datagen.dataFormat.processeddata.ProcessedData;
import boa.datagen.dataFormat.rawdata.DBFData;
import boa.datagen.dataFormat.rawdata.RawData;
import boa.datagen.dataMapper.farsdata.AccidentDataMapper;
import boa.datagen.dataMapper.farsdata.FARSDataType;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class FARSDBFDataMapper extends DataMapper {

    @Override
    public ProcessedData processRawData(RawData rawData) {
        if(rawData.getDomainType() != Domains.FARS){
            throw new IllegalArgumentException("Dataset is not a FARS Data");
        }
        DBFData data = (DBFData)rawData;
        ProcessedData processedData = null;
        if(data.getFarsDataType() == FARSDataType.ACCIDENT){
            AccidentDataMapper mapper = new AccidentDataMapper();
            processedData = mapper.processData(data);
        }
        return processedData;
    }
}
