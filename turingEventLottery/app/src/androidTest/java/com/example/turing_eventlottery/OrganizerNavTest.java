package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
    
    @Test
    public void testSendNotificationsNav() {
        onView(withId(R.id.myNotifications)).perform(click());
        onView(withId(R.id.sendNotificationButton)).perform(click());
        intended(hasComponent(SendNotificationView.class.getName()));
    }
    
    @Test
    public void testManageEventsNav() {
        onView(withId(R.id.browseEvents)).perform(click());
        onView(withText("Manage My Events")).perform(click());
        onView(withId(R.id.manageContainer)).check(matches(isDisplayed()));
    }
}
