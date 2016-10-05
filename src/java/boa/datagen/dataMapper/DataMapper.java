package boa.datagen.dataMapper;

import boa.datagen.dataFormat.rawdata.RawData;
import boa.datagen.Domains;
import boa.datagen.dataReader.DataReadingTechnologies;

/**
 * Created by nmtiwari on 10/4/16.
 */
public abstract class DataMapper {
    public abstract com.google.protobuf.GeneratedMessage processRawData(RawData data);

    public DataMapper getValidDataMapper(RawData data){
        if(data.getDomainType() == Domains.FARS && data.getUsedTech() == DataReadingTechnologies.DBF){
            return new FARSDBFDataMapper();
        }else if(data.getDomainType() == Domains.MSR && data.getUsedTech() == DataReadingTechnologies.GIT){
            return new MSRGITDataMapper();
        }else if(data.getDomainType() == Domains.MSR && data.getUsedTech() == DataReadingTechnologies.SVN){
            return new MSRSVNDataMapper();
        }else{
            throw new UnsupportedOperationException(data.getDomainType() + " and " + data.getUsedTech() + " is not supported");
        }
    }
}
