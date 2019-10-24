package loginscreen.solution.example.com.loginscreen;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static String NAME = "name";
    public static String EMAIL = "email";
    public static String NUMBER = "number";

    private ViewFlipper viewFlipper;
    private LinearLayout nameParent;
    private EditText emailInput;
    private EditText passInput;
    private EditText nameInput;
    private EditText numberInput;
    private Button logIn;
    private Button signUp;

    private Drawable greenColor;
    private Drawable whiteColor;

    private Integer displayedView;

    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        setVariables();

        dbAdapter = new DBAdapter(this);
        dbAdapter.open();

    }

    @Override
    public void finish() {
        super.finish();
        dbAdapter.close();
    }

    private void setVariables(){
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        emailInput = (EditText) findViewById(R.id.et_email);
        passInput = (EditText) findViewById(R.id.et_password);
        nameInput = (EditText) findViewById(R.id.et_name);
        numberInput = (EditText) findViewById(R.id.et_phone);
        nameParent = (LinearLayout) findViewById(R.id.lt_name);

        logIn = (Button) findViewById(R.id.bt_login);
        signUp = (Button) findViewById(R.id.bt_signup);

        greenColor = getDrawable(R.color.buttoncolor);
        whiteColor = getDrawable(R.color.white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void flipView(View v){

        Integer viewID = v.getId();

        if (!viewID.equals(displayedView)){
            nameInput.getText().clear();
            emailInput.getText().clear();
            passInput.getText().clear();
            numberInput.getText().clear();
        }

        if (viewID == logIn.getId()) {
            signUp.setBackground(whiteColor);

            nameParent.setVisibility(View.INVISIBLE);
            viewFlipper.setDisplayedChild(0);
        }
        else {
            logIn.setBackground(whiteColor);

            nameParent.setVisibility(View.VISIBLE);
            nameInput.requestFocus();
            viewFlipper.setDisplayedChild(1);
        }

        displayedView = viewID;
        v.setBackground(greenColor);

    }

    public void validateSignUp(View v){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();
        String number = numberInput.getText().toString();

        Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9\\s]*$");
        Pattern passwordPattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])[^\\s]{6,}$");
        Pattern phoneNumberPattern = Pattern.compile("^[0-9\\\\s]{10}+$");

        if (name.trim().length() == 0 || !alphaNumericPattern.matcher(name).matches()){
            alertUser("Invalid name");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            alertUser(getString(R.string.invalidemail));
            return;
        }

        if (!passwordPattern.matcher(pass).matches()){
            alertUser("Invalid password");
            return;
        }

        if (!phoneNumberPattern.matcher(number).matches()){
            alertUser("Invalid phone number");
            return;
        }

        User user = dbAdapter.insertNewUser(email,name,pass,number);

        if (user != null){
            logInSuccessfully(user);
            return;
        }

        alertUser("Email already registered");
    }

    public void signIn(View v){
        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        User user;
        user = dbAdapter.logInUser(email, pass);

        if (user != null){
            logInSuccessfully(user);
            return;
        }

        alertUser(getString(R.string.loginfail));
    }

    public void alertUser(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void logInSuccessfully(User user){
        Intent intent = new Intent(this,LoginWelcomeActivity.class);
        intent.putExtra(NAME,user.getName());
        intent.putExtra(EMAIL,user.getEmail());
        intent.putExtra(NUMBER,user.getNumber());
        startActivity(intent);
    }
}
