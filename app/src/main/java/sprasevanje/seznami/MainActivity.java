package sprasevanje.seznami;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String email;
    String password;

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
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Button button = (Button) findViewById(R.id.loginButton);
        final EditText eEdit = (EditText) findViewById(R.id.emailField);
        final EditText pEdit = (EditText)findViewById(R.id.passwordField);
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          if (checkForInternet()) {
                                              email = eEdit.getText().toString();
                                              password = pEdit.getText().toString();
                                              Jabolko();
                                          }
                                          else{
                                              Toast.makeText(MainActivity.this, "Ni internetne povezave.", Toast.LENGTH_SHORT).show();
                                          }

                                      }
                                  }
        );
    Button registracija = (Button) findViewById(R.id.button21);
        registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://seznami-za-sprasevanje-79729.firebaseapp.com/pwdrst.html"));
                startActivity(intent);
            }
        });

    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            //Toast toast1 = Toast.makeText(getApplicationContext(), "Prijavite se", Toast.LENGTH_SHORT);
            //toast1.show();
        }
        else {

zazeniActivity();
        }
    }


    private void Jabolko(){
        if((email.equals("")) || (password.equals(""))){
        }
        else{
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast toast1 = Toast.makeText(getApplicationContext(),"Prijava uspešna", Toast.LENGTH_SHORT);
                            toast1.show();
                            zazeniActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Prijava neuspešna.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });}}


private void zazeniActivity() {
    new AsyncTask<Void,Void,Void>()
    {
        @Override
        protected Void doInBackground(Void... params)
        {

            String token = FirebaseInstanceId.getInstance().getToken();
            while(token == null)//this is used to get firebase token until its null so it will save you from null pointer exeption
            {
                token = FirebaseInstanceId.getInstance().getToken();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            /*
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference snapshot = database.getReference("/uidFcm");
            Log.d("jabuk", user.getUid());
            snapshot.child(user.getUid()).setValue(FirebaseInstanceId.getInstance().getToken());
*/
            Intent intent = new Intent(MainActivity.this, prikaz.class);
            startActivity(intent);
        }
    }.execute();

}

    boolean checkForInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo hruska = connectivityManager.getActiveNetworkInfo();
        return hruska != null && hruska.isConnected();
    }
}