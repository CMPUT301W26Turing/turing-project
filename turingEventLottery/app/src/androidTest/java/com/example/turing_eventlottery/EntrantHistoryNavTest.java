package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.MyHistory;
import com.example.turing_eventlottery.view.UserDashboardView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests navigation to the My History screen.
 */
public class EntrantHistoryNavTest {

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
    public void testHistoryNavigation() {
        onView(withId(R.id.myHistory)).perform(scrollTo(), click());

        intended(hasComponent(MyHistory.class.getName()));

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }
}
