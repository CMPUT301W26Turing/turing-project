package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.CreateEventView;

import org.junit.Rule;
import org.junit.Test;


public class CreateEventViewTest {

    @Rule
    public ActivityScenarioRule<CreateEventView> activityRule =
            new ActivityScenarioRule<>(CreateEventView.class);

    @Test
    public void testInputFields() {
        onView(withId(R.id.eventNameInput))
                .perform(scrollTo(), replaceText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.eventNameInput)).check(matches(withText("Test Event")));

        onView(withId(R.id.eventDescriptionInput))
                .perform(scrollTo(), replaceText("test description"), closeSoftKeyboard());
        onView(withId(R.id.eventDescriptionInput)).check(matches(withText("test description")));

        onView(withId(R.id.eventCategoryInput))
                .perform(scrollTo(), replaceText("Testing"), closeSoftKeyboard());

        onView(withId(R.id.winnersToDrawInput))
                .perform(scrollTo(), replaceText("5"), closeSoftKeyboard());
        onView(withId(R.id.winnersToDrawInput)).check(matches(withText("5")));

        onView(withId(R.id.waitlistCapInput))
                .perform(scrollTo(), replaceText("20"), closeSoftKeyboard());
        onView(withId(R.id.waitlistCapInput)).check(matches(withText("20")));
    }

    @Test
    public void testToggleGeolocation() {
        onView(withId(R.id.geoSwitch))
                .perform(scrollTo(), click());
        
        onView(withId(R.id.geoSwitch)).check(matches(isDisplayed()));
    }

    @Test
    public void testPublishButtonVisibility() {
        onView(withId(R.id.publishButton))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }
}
