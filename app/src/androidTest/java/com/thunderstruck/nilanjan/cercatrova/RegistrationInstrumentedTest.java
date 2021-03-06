package com.thunderstruck.nilanjan.cercatrova;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by DEBAPRIYA on 24-04-2017.
 */

@RunWith(AndroidJUnit4.class)
public class RegistrationInstrumentedTest {

    @Rule
    public IntentsTestRule<RegistrationActivity> mActivityRule
            = new IntentsTestRule<>(RegistrationActivity.class);

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                CharSequence error = editText.getError();
                return error != null && error.toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    @Test
    public void testRegistrationFirstName() throws Exception {

        onView(withId(R.id.firstname))
                .perform(typeText("Ab12F"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.firstname)).perform(scrollTo(), clearText());
        onView(withId(R.id.firstname))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.firstname)).perform(scrollTo(), clearText());
        onView(withId(R.id.firstname))
                .perform(typeText("a@world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    @Test
    public void testRegistrationLastName() throws Exception {

        onView(withId(R.id.lastname))
                .perform(typeText("DF199"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.lastname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.lastname)).perform(scrollTo(), clearText());
        onView(withId(R.id.lastname))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.lastname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.lastname)).perform(scrollTo(), clearText());
        onView(withId(R.id.lastname))
                .perform(typeText("a@!world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.lastname)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    @Test
    public void testRegistrationPhoneNumber() throws Exception {

        onView(withId(R.id.phone_number))
                .perform(typeText("Ab12F"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.phone_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.phone_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.phone_number))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.phone_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.phone_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.phone_number))
                .perform(typeText("2574632"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.phone_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.phone_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.phone_number))
                .perform(typeText("9872365412086"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.phone_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    @Test
    public void testRegistrationEmailId() throws Exception {

        onView(withId(R.id.email_id))
                .perform(typeText("DF199"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.email_id)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.email_id)).perform(scrollTo(), clearText());
        onView(withId(R.id.email_id))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.email_id)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.email_id)).perform(scrollTo(), clearText());
        onView(withId(R.id.email_id))
                .perform(typeText("a@$!world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.email_id)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    public void testRegistrationAddress() throws Exception {

        onView(withId(R.id.address)).perform(scrollTo(), clearText());
        onView(withId(R.id.address))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.address)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    public void testRegistrationAdhaarNumber() throws Exception {

        onView(withId(R.id.adhaar_number))
                .perform(typeText("DF199"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.adhaar_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.adhaar_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.adhaar_number))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.adhaar_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.adhaar_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.adhaar_number))
                .perform(typeText("a@$!world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.adhaar_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.adhaar_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.adhaar_number))
                .perform(typeText("9632574896523406"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.adhaar_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.adhaar_number)).perform(scrollTo(), clearText());
        onView(withId(R.id.adhaar_number))
                .perform(typeText("45632574"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.adhaar_number)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    @Test
    public void testRegistrationAge() throws Exception {

        onView(withId(R.id.age)).perform(scrollTo(), clearText());
        onView(withId(R.id.age))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.age)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.age)).perform(scrollTo(), clearText());
        onView(withId(R.id.age))
                .perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.age)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

    }

    @Test
    public void testRegistrationBloodGroup() throws Exception {

        onView(withId(R.id.blood_group))
                .perform(typeText("DF199"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.blood_group)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.blood_group)).perform(scrollTo(), clearText());
        onView(withId(R.id.blood_group))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.blood_group)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.blood_group)).perform(scrollTo(), clearText());
        onView(withId(R.id.blood_group))
                .perform(typeText("a@$!world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.blood_group)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.blood_group)).perform(scrollTo(), clearText());
        onView(withId(R.id.blood_group))
                .perform(typeText("BB+"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.blood_group)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));


    }

    @Test
    public void testRegistrationPassword() throws Exception {

        onView(withId(R.id.password)).perform(scrollTo(), clearText());
        onView(withId(R.id.password))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.password)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));

        onView(withId(R.id.password)).perform(scrollTo(), clearText());
        onView(withId(R.id.password))
                .perform(typeText("a@6"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.password)).check(matches(withError(
                mActivityRule.getActivity().getString(R.string.invalid_field))));
    }

    @Test
    public void validCredentials() {
        onView(withId(R.id.firstname))
                .perform(scrollTo(), typeText("Nilanjan"), closeSoftKeyboard());
        onView(withId(R.id.lastname)).perform(scrollTo(), typeText("Daw"), closeSoftKeyboard());
        onView(withId(R.id.phone_number))
                .perform(scrollTo(), typeText("9831141366"), closeSoftKeyboard());
        onView(withId(R.id.email_id)).perform(scrollTo(), typeText("nil@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.address))
                .perform(scrollTo(), typeText("world"), closeSoftKeyboard());
        onView(withId(R.id.adhaar_number)).perform(scrollTo(), typeText("244255550078"), closeSoftKeyboard());
        onView(withId(R.id.age))
                .perform(scrollTo(), typeText("21"), closeSoftKeyboard());
        onView(withId(R.id.female)).perform(scrollTo(), click());
        onView(withId(R.id.blood_group)).perform(scrollTo(), typeText("A+"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(scrollTo(), typeText("abc123"), closeSoftKeyboard());
        onView(withId(R.id.emergency_name)).perform(scrollTo(), typeText("World"), closeSoftKeyboard());
        onView(withId(R.id.emergency_number)).perform(scrollTo(), typeText("9674362607"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        intended(hasComponent(MainActivity.class.getName()));
    }

}
