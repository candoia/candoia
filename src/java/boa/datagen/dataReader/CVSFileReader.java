package boa.datagen.dataReader;

import boa.datagen.dataFormat.rawdata.CVSData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nmtiwari on 10/4/16.
 */
public class CVSFileReader extends Reader {
    private BufferedReader br = null;

    @Override
    public CVSData readData(String path) {
        CVSData domaindata = new CVSData();
        String line = "";
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(path));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] row = line.trim().split(cvsSplitBy);
                ArrayList<String> rows = new ArrayList();
                Collections.addAll(rows, row);
                domaindata.addRowObjects(rows);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return domaindata;
    }

    @Override
    public boolean canRead(String path) {
        File dir = new File(path);
        boolean isRedable = true;
        if(dir.isDirectory()){
            for(File f: dir.listFiles()){
                isRedable = isRedable && f.getName().endsWith(".csv");
            }
        }else{
            isRedable = isRedable && dir.getName().endsWith(".csv");
        }
        return isRedable;
    }
}
