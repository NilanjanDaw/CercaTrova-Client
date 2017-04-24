package com.thunderstruck.nilanjan.cercatrova;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by DEBAPRIYA on 24-04-2017.
 */

@RunWith(AndroidJUnit4.class)
public class RegistrationInstrumentedTest {

    @Rule
    public IntentsTestRule<RegistrationActivity> mActivityRule
            = new IntentsTestRule<>(RegistrationActivity.class);

    @Test
    public void testRegistrationFirstName() throws Exception {

        onView(withId(R.id.firstname))
                .perform(typeText("Ab12F"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(hasFocus()));

        onView(withId(R.id.firstname)).perform(scrollTo(), clearText());
        onView(withId(R.id.firstname))
                .perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(hasFocus()));

        onView(withId(R.id.firstname)).perform(scrollTo(), clearText());
        onView(withId(R.id.firstname))
                .perform(typeText("a@world"), closeSoftKeyboard());
        onView(withId(R.id.register)).perform(scrollTo(), click());
        onView(withId(R.id.firstname)).check(matches(hasFocus()));

    }

}
