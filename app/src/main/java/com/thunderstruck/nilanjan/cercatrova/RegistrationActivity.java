package com.thunderstruck.nilanjan.cercatrova;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.thunderstruck.nilanjan.cercatrova.support.Constants;
import com.thunderstruck.nilanjan.cercatrova.support.Endpoint;
import com.thunderstruck.nilanjan.cercatrova.support.User;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Registration UI Activity
 * @author Debapriya and Nilanjan
 */
public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.firstname) EditText firstName;
    @BindView(R.id.lastname) EditText lastName;
    @BindView(R.id.phone_number) EditText phoneNumber;
    @BindView(R.id.email_id) EditText emailId;
    @BindView(R.id.address) EditText address;
    @BindView(R.id.adhaar_number) EditText adhaarNumber;
    @BindView(R.id.age) EditText age;
    @BindView(R.id.gender) RadioGroup gender;
    @BindView(R.id.blood_group) EditText bloodGroup;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.register) Button register;
    private Endpoint apiService;
    private ProgressDialog progressDialog;

    /**
     * Perform initialization of all fragments and loaders.
     * @param savedInstanceState is a Bundle object containing the activity's previously saved state.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                
            }

        });
        /*
        When the user clicks on the register button after entering the valid credentials,
        a progress dialog is set and a 'Registering' message is displayed.
         */
        progressDialog = new ProgressDialog(RegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering on our network #feelBlessed...");

        /*
          Retrofit is a type-safe REST client for Android, used for interacting with the APIs and sending network requests
         */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Create an implementation of the API endpoints defined by the service interface.
        apiService = retrofit.create(Endpoint.class);
    }

    /**
     *If valid credentials are provided, the user details are stored which is carried forward in the next GUI
     */
    private void attemptLogin() {
        if (validateLogin()) {
            
            User user = getDetails();
            progressDialog.show();
            //An invocation of a Retrofit method that sends a request to a web server and returns a response.
            Call<User> call = apiService.createUser(user);
            //Calls have been handled asynchronously with enqueue method
            call.enqueue(new Callback<User>() {
                /**
                 * onResponse method is invoked for a received HTTP response.
                 * @param call creates a new, identical call to this one which can be enqueued
                 *             or executed even if this call has already been.
                 * @param response synchronously sends the request and returns its response.
                 */
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    progressDialog.dismiss();
                    User user = response.body();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("profile_data", user);
                    startActivity(intent);
                    finish();
                }

                /**
                 * onFailure method is invoked when a network exception occurred talking to the server
                 * or when an unexpected exception occurred creating the request or processing the response.
                 * Displays a toast indicating the registration failed
                 */
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Server error!", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            Toast.makeText(this, "Validation Failed. Please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Instantiating and returning a new user object
     */
    private User getDetails() {
        int selectedId = gender.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String gender = radioButton.getText().toString();
        return new User(adhaarNumber.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                emailId.getText().toString(), phoneNumber.getText().toString(), address.getText().toString(),
                Integer.parseInt(age.getText().toString()), gender, bloodGroup.getText().toString(), password.getText().toString());
    }

    /**
     * validation of credentials entered by the user is done by pattern matching
     * Otherwise an error message is displayed indicating the particular field is invalid
     */
    private boolean validateLogin() {
        int ctr = 0;
        if(!emailValidator(emailId.getText().toString())) {
            emailId.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!phoneNoValidator(phoneNumber.getText().toString())) {
            phoneNumber.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!nameValidator(firstName.getText().toString())) {
            firstName.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!nameValidator(lastName.getText().toString())) {
            lastName.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!adhaarNoValidator(adhaarNumber.getText().toString())) {
            adhaarNumber.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!ageValidator(age.getText().toString())) {
            age.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!bloodGroupValidator(bloodGroup.getText().toString())) {
            bloodGroup.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        if(!passwordValidator(password.getText().toString())) {
            password.setError(getString(R.string.invalid_field));
            ctr = 1;
        }
        return (ctr == 0);
    }

    /**
     * the below section contains the various validators that have been performed with pattern matching
     */
    private boolean emailValidator(String emailId)
    {
        return !Objects.equals(emailId, "") && android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();

    }

    private boolean phoneNoValidator(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("^[0-9]{10}");
        Matcher matcher = pattern.matcher(phoneNumber);
        return !Objects.equals(phoneNumber, "") && matcher.matches();
    }

    private boolean nameValidator(String name)
    {
        return !Objects.equals(name, "") && name.matches("[a-zA-Z]+");
    }

    private boolean adhaarNoValidator(String adhaarNumber)
    {
        Pattern pattern = Pattern.compile("^[0-9]{12}");
        Matcher matcher = pattern.matcher(adhaarNumber);
        return !Objects.equals(adhaarNumber, "") && matcher.matches();
    }

    private boolean ageValidator(String age)
    {
        return !Objects.equals(age, "") && age.matches("^[0]?[1-9]+$");
    }

    private boolean bloodGroupValidator(String bloodGroup)
    {
        return !Objects.equals(bloodGroup, "") && bloodGroup.matches("^(A|B|AB|O)[+-]$");
    }

    private boolean passwordValidator(String password)
    {
        return !Objects.equals(password, "") && password.length() > 5;
    }


}
