package boa.datagen.candoia;

import boa.datagen.forges.AbstractForge;
import boa.types.Code;
import boa.types.Toplevel;
import net.sf.json.JSONObject;

import java.io.Console;
import java.util.Date;

/**
 * Created by nmtiwari on 7/15/16.
 */
public class CandoiaUtilities {

    public static long getLatestRevisionId(Toplevel.Project proj){
        Code.Revision latest = null;
        Date latest_date = new Date();
        for(Code.CodeRepository cr: proj.getCodeRepositoriesList()) {
            for(Code.Revision rev: cr.getRevisionsList()){
                if(rev.hasCommitDate()){
                    Date this_commit = new Date(rev.getCommitDate());
                    if(latest_date == null){
                        latest_date = new Date(rev.getCommitDate());
                    }else if(latest_date.before(this_commit)){
                        latest_date = this_commit;
                    }
                }
            }
        }
        return latest_date.getTime();
    }

    public static JSONObject buildJSONObject(String projName, String projURL, long revId){
        JSONObject json = new JSONObject();
        json.put("name", projName);
        json.put("url", projURL);
        json.put("latest_seen_id", revId);
        return json;
    }
}
