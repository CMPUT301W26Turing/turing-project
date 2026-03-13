package com.example.turing_eventlottery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.turing_eventlottery.view.AdminDashboardView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminUITest {

    @Rule
    public ActivityScenarioRule<AdminDashboardView> rule =
            new ActivityScenarioRule<>(AdminDashboardView.class);

    // US 03.01.01 Admin remove events (via browse events screen)
    @Test
    public void testUS030101_AdminCanAccessEvents() throws InterruptedException {

        onView(withId(R.id.myEventsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }

    // US 03.02.01 Admin remove profiles
    @Test
    public void testUS030201_AdminCanAccessUserManagement() throws InterruptedException {

        onView(withId(R.id.analyticsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }

    // US 03.04.01 Admin browse events
    @Test
    public void testUS030401_AdminBrowseEvents() throws InterruptedException {

        onView(withId(R.id.myEventsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }

    // US 03.05.01 Admin browse profiles
    @Test
    public void testUS030501_AdminBrowseProfiles() throws InterruptedException {

        onView(withId(R.id.analyticsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }

    // US 03.07.01 Admin remove organizers
    @Test
    public void testUS030701_AdminManageOrganizers() throws InterruptedException {

        onView(withId(R.id.manageOrganizersButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }

    // US 03.08.01 Admin review notification logs
    @Test
    public void testUS030801_AdminViewNotificationLogs() throws InterruptedException {

        onView(withId(R.id.notificationLogsButton))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(2000);

        pressBack();
    }
}