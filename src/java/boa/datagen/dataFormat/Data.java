package boa.datagen.dataFormat;

import boa.datagen.*;
import boa.datagen.Domains;
import boa.datagen.dataReader.DataReadingTechnologies;

/**
 * Created by nmtiwari on 10/4/16.
 */
public abstract class Data {

    boa.datagen.Domains domainType;

    DataReadingTechnologies usedTech;

    public void setDomainType(boa.datagen.Domains domainType) {
        this.domainType = domainType;
    }

    public Domains getDomainType() {
        return domainType;
    }

    public DataReadingTechnologies getUsedTech() {
        return usedTech;
    }
}
