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

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = "RegistrationActivity";
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
        progressDialog = new ProgressDialog(RegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering on our network #feelBlessed...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(Endpoint.class);
    }

    private void attemptLogin() {
        if (validateLogin()) {
            
            User user = getDetails();
            progressDialog.show();
            Call<User> call = apiService.createUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    progressDialog.dismiss();
                    User user = response.body();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("profile_data", user);
                    startActivity(intent);
                    finish();
                }

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

    private User getDetails() {
        int selectedId = gender.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String gender = radioButton.getText().toString();
        return new User(adhaarNumber.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
                emailId.getText().toString(), phoneNumber.getText().toString(), address.getText().toString(),
                Integer.parseInt(age.getText().toString()), gender, bloodGroup.getText().toString(), password.getText().toString());
    }

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
