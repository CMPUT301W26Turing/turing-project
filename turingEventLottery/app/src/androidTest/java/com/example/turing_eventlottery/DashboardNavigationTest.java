package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.BrowseEventsView;
import com.example.turing_eventlottery.view.CreateEventView;
import com.example.turing_eventlottery.view.MyNotificationsView;
import com.example.turing_eventlottery.view.MyProfileView;
import com.example.turing_eventlottery.view.UserDashboardView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DashboardNavigationTest {

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
    public void testBrowseEventsNavigation() {
        onView(withId(R.id.browseEvents)).perform(click());
        intended(hasComponent(BrowseEventsView.class.getName()));
    }

    @Test
    public void testNotificationsNavigation() {
        onView(withId(R.id.myNotifications)).perform(click());
        intended(hasComponent(MyNotificationsView.class.getName()));
    }

    @Test
    public void testProfileNavigation() {
        onView(withId(R.id.myProfile)).perform(click());
        intended(hasComponent(MyProfileView.class.getName()));
    }

    @Test
    public void testNavbarEvents() {
        onView(withId(R.id.nav_events)).perform(click());
        intended(hasComponent(BrowseEventsView.class.getName()));
    }

    @Test
    public void testNavbarNotifications() {
        onView(withId(R.id.nav_alerts)).perform(click());
        intended(hasComponent(MyNotificationsView.class.getName()));
    }

    @Test
    public void testNavbarProfile() {
        onView(withId(R.id.nav_profile)).perform(click());
        intended(hasComponent(MyProfileView.class.getName()));
    }

    @Test
    public void testNavbarFab() {
        onView(withId(R.id.fabCreate)).perform(click());
        intended(hasComponent(CreateEventView.class.getName()));
    }

    @Test
    public void testNavbarHome() {
        onView(withId(R.id.nav_events)).perform(click());
        onView(withId(R.id.nav_home)).perform(click());
        intended(hasComponent(UserDashboardView.class.getName()));
    }
}
