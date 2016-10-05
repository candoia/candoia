package boa.datagen.dataMapper.farsdata;

/**
 * Created by nmtiwari on 10/4/16.
 */
public enum FARSDataType {
    ACCIDENT;

    public static FARSDataType getFARSDATAType(String path){
        String fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
        for(FARSDataType typ: FARSDataType.values()){
            if(typ.name().equalsIgnoreCase(fileName)){
                return typ;
            }
        }
        throw new UnsupportedOperationException("No FARSTYPe found for:" + path);
    }
}
