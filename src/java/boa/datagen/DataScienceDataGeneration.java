package boa.datagen;

import boa.datagen.candoia.CandoiaConfiguration;
import boa.datagen.dataFormat.rawdata.DBFData;
import boa.datagen.dataMapper.FARSDBFDataMapper;
import boa.datagen.dataMapper.farsdata.FARSDataType;
import boa.datagen.dataReader.DBFFileReader;
import boa.transportation.types.Transportation;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;

/**
 * Created by nmtiwari on 10/5/16.
 */
public class DataScienceDataGeneration {
    CommandLine commandLine;

    public DataScienceDataGeneration(CommandLine commandLine){
        this.commandLine = commandLine;
    }

    public ArrayList<com.google.protobuf.GeneratedMessage> generateProcessedData(){
        String domaintype = commandLine.getOptionValue(CandoiaConfiguration.getDomainTypeCMDOption());
        Domains dataDomain = Domains.getDomain(domaintype);
        if((domaintype == null) || ("".equals(domaintype.trim()))) {
            throw new IllegalArgumentException("Please provide the domain type such as msr, fars, bio etc");
        }

        switch(dataDomain){
            case BIO:
                throw new IllegalArgumentException("Candoia has not been extended to support the domain of " + domaintype);

            case FARS: return getFARSProcessedData();

            case MSR:
                throw new IllegalArgumentException("Candoia has not been extended to support the domain of " + domaintype);

            default:
                throw new IllegalArgumentException("Candoia has not been extended to support the domain of " + domaintype);
        }
    }


    private ArrayList<com.google.protobuf.GeneratedMessage> getFARSProcessedData(){
        ArrayList<com.google.protobuf.GeneratedMessage> processedDatas = new ArrayList<>();
        String[] datapath = null;
        datapath= commandLine.getOptionValue("data").split(",");
        DBFFileReader reader = new DBFFileReader();
        for(String path: datapath){
            if(reader.canRead(path)){
                DBFData data  = reader.readData(path);
                data.setFarsDataType(FARSDataType.getFARSDATAType(path));
                data.setDomainType(Domains.FARS);
                FARSDBFDataMapper mapper = new FARSDBFDataMapper();
                com.google.protobuf.GeneratedMessage pData = mapper.processRawData(data);
                processedDatas.add(pData);
            }
        }
        return processedDatas;
    }

  public boolean generateCompleteDataset(String path){
      ArrayList<com.google.protobuf.GeneratedMessage> messages = generateProcessedData();
      DataSetWriter writer = new DataSetWriter(path);
      Integer  counter = 0;
      for(com.google.protobuf.GeneratedMessage message: messages){
        writer.write(message, counter.toString());
      }
      writer.close();
      return true;
  }

}
