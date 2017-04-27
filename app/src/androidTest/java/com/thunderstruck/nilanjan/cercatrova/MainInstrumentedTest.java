package com.thunderstruck.nilanjan.cercatrova;

/**
 * Created by DEBAPRIYA on 27-04-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.thunderstruck.nilanjan.cercatrova.support.Location;
import com.thunderstruck.nilanjan.cercatrova.support.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainInstrumentedTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<MainActivity>(MainActivity.class) {

                @Override
                protected Intent getActivityIntent() {
                    ArrayList<Double> arrayList = new ArrayList<>();
                    arrayList.add(99.0);
                    arrayList.add(102.45);
                    Location location = new Location("Point", arrayList);
                    User user = new User("222244445555","Dia","Paul","p20@gmail.com","9831141355","earth",21,"F","A+","abc123",
                            location, "dev12345" );
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra("profile_data", user);
                    return result;
                }
            };


    @Test
    public void testPolice() throws Exception {
        onView(withId(R.id.police)).perform(click());
            onView(withText(startsWith("Failed to find help. Please Try again"))).
                    inRoot(withDecorView(
                            not(is(mActivityRule.getActivity().
                                    getWindow().getDecorView())))).
                    check(matches(isDisplayed()));
    }

    @Test
    public void testAmbulance() throws Exception {
        onView(withId(R.id.ambulance)).perform(click());
        onView(withText(startsWith("Failed to find help. Please Try again"))).
                inRoot(withDecorView(
                        not(is(mActivityRule.getActivity().
                                getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void testFireFighter() throws Exception {
        onView(withId(R.id.fire_fighter)).perform(click());
        onView(withText(startsWith("Failed to find help. Please Try again"))).
                inRoot(withDecorView(
                        not(is(mActivityRule.getActivity().
                                getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

}






