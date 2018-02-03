package sprasevanje.seznami;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

class customAdapter extends ArrayAdapter {
    /*static private List<String> names12 = new ArrayList<>();
    static private List<String> predcasno12 = new ArrayList<>();
    static private List<String> zapSt12 = new ArrayList<>();
    static private List<String> opomba12 = new ArrayList<>();*/

    public customAdapter(@NonNull Context context, List<String> namaes) {
        super(context,R.layout.jabuk, namaes);
    }

    @Override
    public View getView(int position, View customView, ViewGroup parent) {


        LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
        customView = myCustomInflater.inflate(R.layout.jabuk, parent, false);


        TextView itemText = (TextView) customView.findViewById(R.id.ime);
        TextView itemText1 = (TextView) customView.findViewById(R.id.stevilka);
        TextView itemText2 = (TextView) customView.findViewById(R.id.opomba);
        String jabo = izpisiSeznam.shraniPodatke.getName(position);
        itemText.setText(jabo);

        String nullOpomba = izpisiSeznam.shraniPodatke.getOpomba(position);
        if (!(nullOpomba.equals("e6Kf1Fai"))){
            itemText2.setText("Opomba: " + nullOpomba);
        }
        itemText1.setText(izpisiSeznam.shraniPodatke.getZapSt(position));
        if (izpisiSeznam.shraniPodatke.getPredcasno().contains(izpisiSeznam.shraniPodatke.getZapSt(position)) ){
            int color = Color.parseColor("#F06560");
            itemText.setTextColor(color);
            itemText1.setTextColor(color);
            itemText2.setTextColor(color);
            itemText.setPaintFlags(itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            itemText1.setPaintFlags(itemText1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            itemText2.setPaintFlags(itemText2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        }
        return customView;
    }

     void zeVprasani (final List<String> imena1,final List<String> numbers2, final List<String> help3, final List<String> predcasnik4) {

    }

    }


