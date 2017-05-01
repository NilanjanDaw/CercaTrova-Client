package com.thunderstruck.nilanjan.cercatrova;

import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * Created by nilanjan on 28-Apr-17.
 * Project CercaTrova
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTesting {

    @Rule
    public IntentsTestRule <LoginActivity> mLoginActivityRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void a_testLoginUserName() throws Exception {

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(matches(hasFocus()));

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(matches(hasFocus()));

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.email)).check(matches(hasFocus()));
    }

    @Test
    public void b_invalidPassword() throws Exception {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.password)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(hasFocus()));

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.password)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(hasFocus()));

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.password)).perform(typeText("abc12"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.password)).check(matches(hasFocus()));
    }

    @Test
    public void  c_invalidCredentials() {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("b@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withText("Login Failed"))
                .inRoot(withDecorView(not(is(mLoginActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));

        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc12345"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withText("Login Failed"))
                .inRoot(withDecorView(not(is(mLoginActivityRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void d_validCredentials() {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void e_testAmbulance() throws Exception {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.ambulance)).perform(click());
        Activity activity = getCurrentActivity();
        onView(withText(startsWith("Failed to find help. Please Try again"))).
                inRoot(withDecorView(
                        not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void f_testFireFighter() throws Exception {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.fire_fighter)).perform(click());
        Activity activity = getCurrentActivity();
        onView(withText(startsWith("Failed to find help. Please Try again"))).
                inRoot(withDecorView(
                        not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void g_testPolice() throws Exception {
        onView(withId(R.id.email)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.email))
                .perform(typeText("a@world.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        onView(withId(R.id.police)).perform(click());
        intended(hasComponent(MapsActivity.class.getName()));
    }

    @Test
    public void h_MapsActivityTest() throws Exception {

    }

    private Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        onView(isRoot()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                activity[0] = (Activity) view.getContext();
            }
        });
        return activity[0];
    }


}
