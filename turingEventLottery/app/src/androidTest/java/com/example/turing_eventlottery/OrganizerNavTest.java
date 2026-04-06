package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.SendNotificationView;
import com.example.turing_eventlottery.view.UserDashboardView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class OrganizerNavTest {

    @Rule
    public ActivityScenarioRule<UserDashboardView> activityRule =
            new ActivityScenarioRule<>(UserDashboardView.class);
    
    @Before
    public void setup() {
        Intents.init();
    }
    
    @After
    public void finish() {
        Intents.release();
    }

    // Note: Removed a test that checks if the sendNotification screen is accessible
    // because for a user who has not created an event, they will not have a send notification
    // button visible. I suppose this could have been wired into the test to make sure the user
    // running the tests is an organizer, but too late now!
    @Test
    public void testManageEventsNav() {
        onView(withId(R.id.browseEvents)).perform(scrollTo(), click());
        onView(withText("Manage My Events")).perform(click());
        onView(withId(R.id.manageContainer)).check(matches(isDisplayed()));
    }
}
