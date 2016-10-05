package boa.datagen.dataMapper.farsdata;

import boa.datagen.dataFormat.processeddata.ProcessedData;
import boa.datagen.dataFormat.rawdata.DBFData;
import boa.datagen.dataReader.DataReadingTechnologies;
import com.linuxense.javadbf.DBFField;
import boa.transportation.types.*;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class AccidentDataMapper {


    public ProcessedData processData(DBFData data){
        Transportation.TransportData.Builder processed = Transportation.TransportData.newBuilder();
        DBFField[] fields = data.getFields();
        Object[][] records = data.getRowObjects();

        for(Object[] record: records){
            Accident.AccidentData.Builder accident = Accident.AccidentData.newBuilder();
            for(int i = 0; i< fields.length; i++){
                String name = fields[i].getName();
                processField(name, accident, record[i]);
            }
            processed.addAccidents(accident.build());
        }
        boa.transportation.types.Transportation.TransportData transportation = processed.build();
        ProcessedData processedData = new ProcessedData();
        processedData.setProcessedData(transportation);
        return processedData;
    }

    private void processField(String field, Accident.AccidentData.Builder builder, Object value){
        if("STATE".equals(field)){
            builder.setSTATE((Double) value);
        }else if("ST_CASE".equals(field)){
            builder.setSTATECASE((Double)value);
        }else if("DAY".equals(field)){
            builder.setDAY((Double) value);
        }else if("MONTH".equals(field)){
            builder.setMONTH((Double) value);
        }else if("YEAR".equals(field)){
            builder.setYEAR((Double) value);
        }else if("DAY_WEEK".equals(field)){
            builder.setDAYWEEK((Double) value);
        }else if("HOUR".equals(field)){
            builder.setHOUR((Double) value);
        }else if("MINUTE".equals(field)){
            builder.setMINUTE((Double) value);
        }else if("SCH_BUS".equals(field)){
            builder.setSCHBUS((Double) value);
        }else if("FATALS".equals(field)){
            builder.setFATALS((Double) value);
        }else if("DRUNK_DR".equals(field)){
            builder.setDRUNKDR((Double) value);
        }else if("NOT_HOUR".equals(field)){
            builder.setNOTHOUR((Double) value);
        }else if("NOT_MIN".equals(field)){
            builder.setNOTMIN((Double) value);
        }else if("ARR_HOUR".equals(field)){
            builder.setARRHOUR((Double) value);
        }else if("ARR_MIN".equals(field)){
            builder.setARRMIN((Double) value);
        }else if("HOSP_HR".equals(field)){
            builder.setHOSPHR((Double) value);
        }else if("HOSP_MN".equals(field)){
            builder.setHOSPMIN((Double) value);
        }

    }
}
