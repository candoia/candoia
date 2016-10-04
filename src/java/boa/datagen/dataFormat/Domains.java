package boa.datagen.dataFormat;

/**
 * Created by nmtiwari on 10/4/16.
 */
public enum Domains {
    FARS, BIO, MSR, RAW;

    public static Domains getDomain(String name){
        for(Domains d: Domains.values()){
            if(d.name().equalsIgnoreCase(name))
                return d;
        }
        throw new IllegalArgumentException("Domain of type: " + name  + " is not supported. For extending Candoia visit www.candoia.org");
    }
}
