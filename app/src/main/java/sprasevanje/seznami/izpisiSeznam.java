package sprasevanje.seznami;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Vibrator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static sprasevanje.seznami.R.id.enterOpomba;
import static sprasevanje.seznami.R.id.ime;
import static sprasevanje.seznami.R.id.item1;
import static sprasevanje.seznami.R.id.snap;
import static sprasevanje.seznami.R.layout.vpis_dialog;
import android.view.ViewGroup.LayoutParams;


public class izpisiSeznam extends AppCompatActivity {
    List<String> uids = new ArrayList<>();
    String seznamID;
    static List<String> dummyZapSt = new ArrayList<>();
    static List<String> zapSt = new ArrayList<>();
    static List<String> imena = new ArrayList<>();
    static List<String> opomba = new ArrayList<>();
    static List<String> predcasnoVprasan = new ArrayList<>();
    List<String> prostaMesta = new ArrayList<>();
    List<String> copyOfProstaMesta = new ArrayList<>();
    String hruska;
    String jabuk = "/seznami/";
    String pathToSeznam;
    String mestoSeznama;
    String zapisOpombe;
    public static cachedData shraniPodatke = new cachedData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izpisi_seznam);
        Bundle extras = getIntent().getExtras();
        seznamID = extras.getString("jabolko");
        setTitle(extras.getString("pomaranca"));
        jabuk = "/seznami/" + seznamID + "/";
        hruska = "/poz/" + seznamID;
        final ListView seznamek = (ListView) findViewById(R.id.seznam);
        final ListAdapter seznamAdapter = new customAdapter(this, imena);
        seznamek.setAdapter(seznamAdapter);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference snapshot1 = database.getReference("/");
        pathToSeznam = jabuk;
        snapshot1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                zapSt.clear();
                imena.clear();
                opomba.clear();
                prostaMesta.clear();
                uids.clear();
                predcasnoVprasan.clear();
                for (int x = 1; x <= 30; x++) {
                    String num = Integer.toString(x);
                    prostaMesta.add(num);
                }
                DataSnapshot seznam = snapshot.child(jabuk);
                for (DataSnapshot postSnapshot : seznam.getChildren()) {
                    String zap = postSnapshot.getKey();
                    String ime = seznam.child("/" + zap).child("/author").getValue(String.class);
                    String uid = seznam.child("/" + zap).child("/uid").getValue(String.class);
                    uids.add(uid);
                    zapSt.add(zap);
                    imena.add(ime);
                    prostaMesta.remove(prostaMesta.indexOf(zap));
                    if (seznam.hasChild("/" + zap + "/opomba")) {
                        String opom = seznam.child("/" + zap).child("/opomba").getValue(String.class);
                        opomba.add(opom);
                    } else {
                        opomba.add("e6Kf1Fai");
                    }
                }
                seznam = snapshot.child(hruska);
                if (seznam.hasChild("pv")) {


                    DataSnapshot seznam1 = seznam.child("pv");
                    for (DataSnapshot postSnapshot : seznam1.getChildren()) {
                        String keyOdVprasanega = postSnapshot.getKey();
                        if (zapSt.contains(keyOdVprasanega)) {
                            predcasnoVprasan.add(keyOdVprasanega);
                        }
                        else {
                            DatabaseReference abx = database.getReference(hruska).child("/pv/" + keyOdVprasanega);
                            abx.removeValue();
                        }
                    }

                }

                getSyntax();
                ((customAdapter)seznamAdapter).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}});
        final Vibrator v;
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        seznamek.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkForInternet()) {


                    v.vibrate(50);
                    if (predcasnoVprasan.contains(dummyZapSt.get(i))) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference snapshot = database.getReference(hruska).child("/pv/").child(dummyZapSt.get(i));
                        snapshot.removeValue();
                    } else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference snapshot = database.getReference(hruska).child("/pv/");

                        HashMap<String, Object> vpis = new HashMap<>();
                        vpis.put("pv", "true");
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(dummyZapSt.get(i), vpis);
                        snapshot.updateChildren(childUpdates);
                    }
                }
                else {
                    Toast.makeText(izpisiSeznam.this, "Ni internetne povezave", Toast.LENGTH_SHORT).show();
                }
                return false;
            }});}


     void getSyntax(){
         List<String> names1 = new ArrayList<>(imena);
          List<String> zapSt1 = new ArrayList<>(zapSt);
          List<String> predcasno1 = new ArrayList<>(predcasnoVprasan);
          List<String> opomba1 = new ArrayList<>(opomba);

        for (int x = 0; x < predcasno1.size(); x++) {
            String num = predcasno1.get(x);
            int poVrsti = zapSt1.indexOf(num);
            zapSt1.remove(poVrsti);
            zapSt1.add(num);
            num = opomba1.get(poVrsti);
            opomba1.remove(poVrsti);
            opomba1.add(num);
            num = names1.get(poVrsti);
            names1.remove(poVrsti);
            names1.add(num);
        }
        dummyZapSt = zapSt1;
        shraniPodatke.cacheData(names1,zapSt1 ,opomba1 ,predcasno1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int res_id = item.getItemId();
        if (res_id==R.id.item1){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (!uids.contains(user.getUid())) {
                copyOfProstaMesta = prostaMesta;
                final ArrayAdapter<String> izborMesta = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copyOfProstaMesta);
                final Dialog dialog = new Dialog(izpisiSeznam.this);
            dialog.setContentView(vpis_dialog);
            final Spinner izberiMesto = dialog.findViewById(R.id.numberPicker);
            izborMesta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            izberiMesto.setAdapter(izborMesta);
                ((ArrayAdapter)izborMesta).notifyDataSetChanged();
                dialog.show();

            final EditText opomba = dialog.findViewById(enterOpomba);
            Button vpis = dialog.findViewById(R.id.vpisiMeButton);

            vpis.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    zapisOpombe = opomba.getText().toString();
                    if (writeToSeznam()){dialog.hide();}
                    else{
                        copyOfProstaMesta = prostaMesta;
                        ((ArrayAdapter)izborMesta).notifyDataSetChanged();
                        izberiMesto.setAdapter(izborMesta);
                    }


                }
            });
            izberiMesto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View v,
                                           int pos, long id) {
                    mestoSeznama = parent.getSelectedItem().toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(izpisiSeznam.this, "test1",Toast.LENGTH_SHORT).show();
                }}
            );
     }
            else {
                Toast.makeText(izpisiSeznam.this, "Na seznam se ne morete vpisati dvakrat.", Toast.LENGTH_SHORT).show();
            }
        }

     if (res_id==R.id.item2) {
         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         String uidOfUser = user.getUid();
         if (uids.contains(uidOfUser)) {
             int zaporedje = uids.indexOf(uidOfUser);
             //Toast.makeText(this, "funkcija", Toast.LENGTH_SHORT).show();

             final String zaIzbris = zapSt.get(zaporedje);
             //Toast.makeText(this, zaIzbris, Toast.LENGTH_SHORT).show();
             if (predcasnoVprasan.contains(zaIzbris)){
                 Toast.makeText(this, "Bili ste že vprašani.", Toast.LENGTH_SHORT).show();
             }
             else{

             new AlertDialog.Builder(this)
                     .setTitle("Se želite izpisati iz " + zaIzbris + ". mesta?")
                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                         public void onClick(DialogInterface dialog, int whichButton) {
                             if (checkForInternet()) {
                                 FirebaseDatabase database = FirebaseDatabase.getInstance();
                                 DatabaseReference snapshot = database.getReference(pathToSeznam).child(zaIzbris);
                                 snapshot.removeValue();
                                 snapshot = database.getReference(hruska).child("/pv/").child(zaIzbris);
                                 snapshot.removeValue();
                             }
                             else {
                                 Toast.makeText(izpisiSeznam.this, "Ni internetne povezave", Toast.LENGTH_SHORT).show();
                             }

                         }
                     })
                     .setNegativeButton("Prekliči", null).show();
         }}

         else {
             Toast.makeText(this, "Najprej se vpišite na seznam", Toast.LENGTH_SHORT).show();
         }
     }

     if (res_id==R.id.item3){
         Toast.makeText(izpisiSeznam.this, "Osvežujem...", Toast.LENGTH_SHORT).show();
         recreate();
     }

    return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        zapSt.clear();
        imena.clear();
        opomba.clear();
        prostaMesta.clear();
        uids.clear();
        predcasnoVprasan.clear();
        finish();
    }

    boolean writeToSeznam() {
        if (checkForInternet()) {
            if (prostaMesta.contains(mestoSeznama)){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference snapshot = database.getReference(pathToSeznam);
            String key = snapshot.child(mestoSeznama).getKey();

            HashMap<String, Object> vpis = new HashMap<>();
            vpis.put("author", user.getDisplayName());
            vpis.put("uid", user.getUid());
            if (!(zapisOpombe.equals(""))) {
                vpis.put("opomba", zapisOpombe);
            }
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, vpis);
            snapshot.updateChildren(childUpdates);
            prostaMesta.remove(prostaMesta.indexOf(mestoSeznama));
                return true;
        }
        else{
                Toast.makeText(this, "Mesto je bilo ravnokar zasedeno.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(this, "Ni internetne povezave", Toast.LENGTH_SHORT).show();
            return true;
        }
       }

    @Override
    public void onRestart(){
        super.onRestart();
        zapSt.clear();
        imena.clear();
        opomba.clear();
        prostaMesta.clear();
        uids.clear();
        predcasnoVprasan.clear();
        finish();
    }

    boolean checkForInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo hruska = connectivityManager.getActiveNetworkInfo();
        return hruska != null && hruska.isConnected();
    }
   }


