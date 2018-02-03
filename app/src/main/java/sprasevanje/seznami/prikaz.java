package sprasevanje.seznami;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class prikaz extends AppCompatActivity {
    boolean status;
    List<String> dostopniSeznamiL = new ArrayList<String>();
    List<String> imenaDostopnieznamov = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference snapshot = database.getReference("/list/");
    String imeSeznama;
    String idSeznama;
    String jabuk = "jabuk";
    ArrayAdapter<String> test;



    @Override
    public void onBackPressed() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz);
        TextView prijavlenKot = (TextView) findViewById(R.id.prijavljenKot);
        final String text = "Izberi seznam:";

        imenaDostopnieznamov.add(text);
        dostopniSeznamiL.add(text);
        final SharedPreferences sPrefs = getPreferences(MODE_PRIVATE);
        final Boolean switchValue = sPrefs.getBoolean("kiwi", false);
        final Switch notifiSwitch = (Switch) findViewById(R.id.switch1);
        notifiSwitch.setChecked(switchValue);


        Log.d("jabuk", FirebaseInstanceId.getInstance().getToken());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        prijavlenKot.setText("Prijavljen kot: \n" + name);

        Log.d("jabuk", switchValue.toString());

        final Button ogledSeznam = (Button) findViewById(R.id.ogledSeznama);
        final Spinner seznamSpinner = (Spinner) findViewById(R.id.spinMe);
        final ArrayAdapter<String> seznamAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, imenaDostopnieznamov);
        seznamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seznamSpinner.setAdapter(seznamAdapter);


        ogledSeznam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((imeSeznama != null) && (imeSeznama != text)) {
                        Intent intent = new Intent(getBaseContext(), izpisiSeznam.class);
                        intent.putExtra("jabolko", imeSeznama);
                        intent.putExtra("pomaranca", idSeznama);
                        Log.e("Flasa", imeSeznama);
                        startActivity(intent);}
                else {
                    Toast.makeText(getApplicationContext(), "Izbrati morate seznam.", Toast.LENGTH_SHORT).show();}}});

        snapshot.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot snapshot) {
                                               imenaDostopnieznamov.clear();
                                               dostopniSeznamiL.clear();
                                               Log.e("Count ", "" + snapshot.getChildrenCount());
                                               if (checkForInternet()){
                                               for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                   String post = postSnapshot.getKey();
                                                   String key = snapshot.child(post + "/name").getValue(String.class);
                                                   imenaDostopnieznamov.add(key);
                                                   dostopniSeznamiL.add(post);
                                                   Log.e("jabuk", key);
                                               }}
                                               //int dolzSezn = dostopniSeznamiL.size();
                                               imenaDostopnieznamov.add(0, text);
                                               dostopniSeznamiL.add(0, text);
                                               seznamSpinner.setSelection(0);
                                               ((ArrayAdapter)seznamAdapter).notifyDataSetChanged();
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError firebaseError) {

                                           }
                                       }
        );

        seznamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View v,
                                                                               int pos, long id) {
                                                        if (checkForInternet()){

                                                            idSeznama = parent.getSelectedItem().toString();
                                                            int spinnerValue1 = seznamSpinner.getSelectedItemPosition();
                                                            //int a = imenaDostopnieznamov.indexOf(jabolko);
                                                            imeSeznama = dostopniSeznamiL.get(spinnerValue1);
                                                            //Toast.makeText(prikaz.this, izpisiSeznam.shraniPodatke.getName(0), Toast.LENGTH_SHORT).show();


                                                        }
                                                    else {
                                                            Toast.makeText(prikaz.this, "Ni internetne povezave.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {
                                                        Toast.makeText(prikaz.this, "test1", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
        );

        notifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkForInternet()){
                    if (b){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference snapshot = database.getReference("/uidFcm");
                        Log.d("jabuk", user.getUid());
                        snapshot.child(user.getUid()).setValue(FirebaseInstanceId.getInstance().getToken());
                        SharedPreferences.Editor editor = sPrefs.edit();
                        editor.putBoolean("kiwi", true);
                        editor.commit();
                        Toast.makeText(prikaz.this, "Obvestila: JA", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference("/");
                        mDatabase.child("uidFcm").child(user.getUid()).setValue("");
                        SharedPreferences.Editor editor = sPrefs.edit();
                        editor.putBoolean("kiwi", false);
                        editor.commit();
                        Toast.makeText(prikaz.this,"Obvestila: NE", Toast.LENGTH_SHORT).show();
                        }


                }
                else{
                    Toast.makeText(prikaz.this, "Ni internetne povezave", Toast.LENGTH_SHORT).show();
                    notifiSwitch.setChecked(switchValue);
                }

            }
        });}




    private void odjava() {
        new AsyncTask<Void,Void,Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                {
                    try
                    {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("/");
                mDatabase.child("uidFcm").child(user.getUid()).setValue("");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(prikaz.this, MainActivity.class);
                startActivity(intent);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu1, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();

        if (res_id == R.id.odjava) {
            odjava();
            final SharedPreferences sPrefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putBoolean("kiwi", false);
            editor.commit();
            Toast.makeText(getApplicationContext(), "Odjava uspešna",
                    Toast.LENGTH_SHORT).show();        }
        if (res_id == R.id.info){
            Toast.makeText(getApplicationContext(), "Verzija 1.5.0 \nRazvoj Android aplikacije:\nLuka Skeledžija, Gregor Kržmanc",
                    Toast.LENGTH_SHORT).show();}
        if (res_id == R.id.osvezi){
            recreate();
        }
        if (res_id==R.id.token){
            String token = FirebaseInstanceId.getInstance().getToken();
            Toast.makeText(prikaz.this, token, Toast.LENGTH_SHORT).show();
        }
    return true;
    }

    boolean checkForInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo hruska = connectivityManager.getActiveNetworkInfo();
        return hruska != null && hruska.isConnected();
    }

}