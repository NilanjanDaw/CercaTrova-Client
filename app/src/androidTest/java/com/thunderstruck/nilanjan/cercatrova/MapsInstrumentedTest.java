package com.thunderstruck.nilanjan.cercatrova;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.thunderstruck.nilanjan.cercatrova.support.EmergencyPersonnel;
import com.thunderstruck.nilanjan.cercatrova.support.Location;
import com.thunderstruck.nilanjan.cercatrova.support.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
/**
 * Created by DEBAPRIYA on 28-04-2017.
 */

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MapsInstrumentedTest {

    @Rule
    public IntentsTestRule<MapsActivity> mActivityRule =
            new IntentsTestRule<MapsActivity>(MapsActivity.class) {

        @Override
        protected Intent getActivityIntent() {
            ArrayList<Double> arrayList = new ArrayList<>();
            arrayList.add(99.0);
            arrayList.add(102.45);
            Location location = new Location("Point", arrayList);
            ArrayList<Double> arrayList1 = new ArrayList<>();
            arrayList1.add(67.0);
            arrayList1.add(106.9);
            Location location1 = new Location("Point", arrayList1);
            User user = new User("222244445555", "Dia", "Paul", "p20@gmail.com", "9831141355", "earth", 21, "F", "A+", "abc123",
                    location, "dev12345");
            EmergencyPersonnel emergencyPersonnel = new EmergencyPersonnel("P7942","111122223333","Debapriya","Paul","912345678","WB241977",
                    1 ,"Kankurgachi",location1);
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, MainActivity.class);
            result.putExtra("profile_data", user);
            result.putExtra("emergency_responder", emergencyPersonnel);
            return result;

        }
    };

    @Test
    public void testPersonnel_details() throws Exception {
        Thread.sleep(3000);
        onView((withId(R.id.name))).check(matches(withText("Debapriya Paul")));
        onView((withId(R.id.id))).check(matches(withText("P7942")));
        onView((withId(R.id.base))).check(matches(withText("Kankurgachi")));
        onView((withId(R.id.car_number))).check(matches(withText("WB241977")));
    }

    @Test
    public void contactButton() {
        String phoneNumber = "9831141377";
        onView(withId(R.id.contact)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse("tel:" + phoneNumber))
        ));
    }

}


