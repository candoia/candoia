package boa.datagen.dataMapper.biodata;

import boa.bio.types.Bio;
import boa.bio.types.Biology;
import boa.datagen.dataFormat.rawdata.CVSData;

import java.util.ArrayList;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class BioDataMapper {


    public com.google.protobuf.GeneratedMessage processData(CVSData data){
        Biology.BiologyDataset.Builder processed = Biology.BiologyDataset.newBuilder();
        ArrayList<ArrayList<String>> records = data.getRowObjects();
        for(int j = 0; j < 3000; j++){
            ArrayList<String> record = records.get(j);
//        for(ArrayList<String> record: records){
            Bio.BioData.Builder biodata = Bio.BioData.newBuilder();
            for(int i = 0; i< record.size(); i++){
                if(i == 0){
                    biodata.setExperimentAccession(record.get(0));
                }else if(i == 1){
                    biodata.setExperimentTitle(record.get(1));
                }else if(i == 2){
                    biodata.setOrganismName(record.get(2));
                }else if(i == 3){
                    biodata.setInstrument(record.get(3));
                }else if(i == 4){
                    biodata.setSubmitter(record.get(4));
                }else if(i == 5){
                    biodata.setStudyAccession(record.get(5));
                }else if(i == 6){
                    biodata.setStudyTitle(record.get(6));
                }else if(i == 7){
                    biodata.setSampleAccession(record.get(7));
                }else if(i == 8){
                    biodata.setSampleTitle(record.get(8));
                }else if(i == 9){
                    biodata.setTotalSize(Float.parseFloat(record.get(9)));
                }else if(i == 10){
                    biodata.setTotalRUNs(Long.parseLong(record.get(10)));
                }else if(i == 11){
                    String number = record.get(11);
                    if(number.length() > 0)
                        biodata.setTotalSpots(Long.parseLong(number));
                    else
                        biodata.setTotalSpots(0);
                }else if(i == 12){
                    String n = record.get(12);
                    if(n.length() > 0)
                        biodata.setTotalBases(Double.valueOf(record.get(12)).longValue());
                    else
                        biodata.setTotalBases(0);
                }else if(i == 13){
                    biodata.setLibraryName(record.get(13));
                }else if(i == 14){
                    biodata.setLibraryStrategy(record.get(14));
                }else if(i == 15){
                    biodata.setLibrarySource(record.get(15));
                }else if(i == 16){
                    biodata.setLibrarySelection(record.get(16));
                }
            }
            processed.addBio(biodata);
        }
        Biology.BiologyDataset bio = processed.build();
        return bio;
    }

}
