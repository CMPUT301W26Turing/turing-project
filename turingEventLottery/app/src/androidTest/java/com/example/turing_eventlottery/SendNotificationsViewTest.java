package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.SendNotificationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SendNotificationsViewTest {

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void finish() {
        Intents.release();
    }

    @Rule
    public ActivityScenarioRule<SendNotificationView> activityRule = 
            new ActivityScenarioRule<>(SendNotificationView.class);
    
    @Test
    public void testAudienceSelector() {
        onView(withId(R.id.audienceSelected)).perform(click());
        // these need to be commented out for now because this text won't show up if an event
        // isn't selected - we don't know if the device running the test is associated with
        // a user with an event, so we couldn't guarantee that an event exists/can be selected
//        onView(withId(R.id.audienceSummary))
//                .check(matches(withText(containsString("participants list"))));

        onView(withId(R.id.audienceCancelled)).perform(click());
//        onView(withId(R.id.audienceSummary))
//                .check(matches(withText(containsString("cancelled list"))));

        onView(withId(R.id.audienceWaiting)).perform(click());
//        onView(withId(R.id.audienceSummary))
//                .check(matches(withText(containsString("waiting list"))));
    }

    @Test
    public void testSendFails() {
        onView(withId(R.id.sendButton)).perform(click());
        // make sure we're still in the send notification screen (failed)
        onView(withId(R.id.sendButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testMessageInput() {
        onView(withId(R.id.messageInput)).perform(typeText("Test Message"), closeSoftKeyboard());
        onView(withId(R.id.messageInput)).check(matches(withText("Test Message")));
    }
}
