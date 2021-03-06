package surveyapp.thesmader.com.surveyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public String subjectCode;
    public String yearValue;
    public String semesterValue;
    public String email;
    FloatingActionButton mFab;
    public RadioGroup stream1,stream2;
    String midendsem,stream;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> user = new HashMap<>();

    public String correspond(String a)
    {
        if (stream.equals("B.Tech"))
            return "0";
        else if (stream.equals("M.A"))return "1";
        else if (stream.equals("M.B.A"))return "2";
        else if (stream.equals("B.Arch"))return "3";
        else if (stream.equals("M.Sc"))return "4";
        else if (stream.equals("Integrated M.Sc"))return "5";
        else if (stream.equals("M.Tech(Res)"))return "6";
        else if (stream.equals("Dual Degree"))return "7";
        else if (stream.equals("Ph.D"))return "8";
        else return "-1";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        midendsem="Mid Semester";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stream="B.Tech";
        stream1=(RadioGroup)findViewById(R.id.stream1);
        stream2=(RadioGroup)findViewById(R.id.stream2);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,interimActivity.class));
                overridePendingTransition(R.anim.l2r,R.anim.r2l);
                finish();
            }
        });
        mFab = findViewById(R.id.floatingActionButton);
        mFab.setTransitionName("reveal");

    }
    public void addMSListener(View view)
    {
        RadioGroup midend=(RadioGroup)findViewById(R.id.sem);
        int selectedId=midend.getCheckedRadioButtonId();
        RadioButton rb=findViewById(selectedId);
        midendsem=rb.getText().toString();
        Toast.makeText(MainActivity.this,
                rb.getText(), Toast.LENGTH_SHORT).show();
    }
    public void streamChoice1(View view)
    {

        int selectedId=stream1.getCheckedRadioButtonId();
        RadioButton rb=findViewById(selectedId);
        stream=rb.getText().toString();
        stream2.clearCheck();
        Toast.makeText(MainActivity.this,
                rb.getText(), Toast.LENGTH_SHORT).show();
    }
    public void streamChoice2(View view)
    {

        int selectedId=stream2.getCheckedRadioButtonId();
        RadioButton rb=findViewById(selectedId);
        stream=rb.getText().toString();
        stream1.clearCheck();
        Toast.makeText(MainActivity.this,
                rb.getText(), Toast.LENGTH_SHORT).show();
    }

    public String semCorrespond(String s)
    {
        if(s.equals("Autumn Semester"))
            return "1";
        else if(s.equals("Spring Semester"))
            return "0";
        else return "2";
    }

    public String meCorrespond(String s)
    {
        if(s.equals("End Sem"))
            return "1";
            else return "0";
    }
    public String yrCorrespond(String s)
    {
        if(s.equals("1st"))
            return "1";
        else if(s.equals("2nd"))
            return "2";
        else if(s.equals("3rd"))
            return "3";
        else if(s.equals("4th"))
            return "4";
        else return "5";


    }

    public void dataEntry(View view) {
        entryActivity x = new entryActivity();

        EditText editText = (EditText) findViewById(R.id.subject_code);
        String subjectCode = editText.getText().toString();
        Spinner year_select = (Spinner) findViewById(R.id.year_select);
        yearValue = year_select.getSelectedItem().toString();
        Spinner semester_select = (Spinner) findViewById(R.id.semester_select);
        semesterValue = semester_select.getSelectedItem().toString();
        x.scode = subjectCode;
        x.semesterValue = semesterValue;
        x.yearValue = yearValue;
        FirebaseUser users=FirebaseAuth.getInstance().getCurrentUser();;
        if (yearValue.equals("Choose Year") || semesterValue.equals("Choose Semester") || midendsem==null || stream==null)
            Toast.makeText(getApplicationContext(), "Please provide appropriate input", Toast.LENGTH_SHORT).show();
        else {
            user.put("Subject Code",subjectCode.toUpperCase());
            user.put("Year",yrCorrespond(yearValue));
            user.put("Semester",semCorrespond(semCorrespond(semesterValue)));
            user.put("Stream",correspond(stream));
            user.put("Mid or End sem",meCorrespond(midendsem));
            db.collection(users.getEmail())
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("FirestoreDemo", "DocumentSnapshot added with ID " + documentReference.getId());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FirestoreDemo", "Error adding document", e);
                        }
                    });
            Intent i=new Intent(getApplicationContext(),entryActivity.class);
            i.putExtra("subject",subjectCode);
            i.putExtra("year",yearValue);
            i.putExtra("semester",semesterValue);
            i.putExtra("stream",stream);
            i.putExtra("MidEnd",midendsem);
            i.putExtra("source","main");
            startActivity(i);
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.l2r,R.anim.r2l);
    }
}