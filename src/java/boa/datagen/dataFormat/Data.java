package boa.datagen.dataFormat;

/**
 * Created by nmtiwari on 10/4/16.
 */
public abstract class Data {
    Domains domainType;

    public Data(Domains typ){
        this.domainType = typ;
    }
}
