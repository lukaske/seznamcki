package sprasevanje.seznami;

import java.util.ArrayList;
import java.util.List;

public class cachedData {
    static private List<String> names12 = new ArrayList<>();
    static private List<String> predcasno12 = new ArrayList<>();
    static private List<String> zapSt12 = new ArrayList<>();
    static private List<String> opomba12 = new ArrayList<>();


     void cacheData (final List<String> imena1,final List<String> numbers2, final List<String> help3, final List<String> predcasnik4) {

         names12 = imena1;
        zapSt12 = numbers2;
        opomba12 = help3;
        predcasno12 = predcasnik4;

    }

    public String getName(int i){
        return names12.get(i);
    }

    public String getZapSt(int i){
            return zapSt12.get(i);
    }

    public String getOpomba(int i){
        return opomba12.get(i);
    }
    public List getPredcasno(){
        return predcasno12;
    }

}
