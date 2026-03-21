package com.example.turing_eventlottery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.turing_eventlottery.model.User;
import com.example.turing_eventlottery.view.AdminDashboardView;
import com.example.turing_eventlottery.view.UserDashboardView;
import com.google.android.material.button.MaterialButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserDashboardViewTest {
    private User adminUser;
    private User regularUser;

    @Before
    public void setUp() {
        adminUser = new User("admin01", "adminName", "admin@example.com", null, true, false, false);
        regularUser = new User("entrant01", "entrantName", "entrant@example.com", "null", false, false, false);
    }

    @Test
    public void testRegularUserUI() {
        try (ActivityScenario<UserDashboardView> scenario =
                     ActivityScenario.launch(UserDashboardView.class)) {

            scenario.onActivity(activity -> {
                activity.runOnUiThread(() -> {
                    activity.findViewById(R.id.adminButton)
                            .setVisibility(MaterialButton.INVISIBLE);
                    ((android.widget.TextView) activity.findViewById(R.id.greetingMessage))
                            .setText("Hello, " + regularUser.getUserName().split(" ")[0]);
                });

                // Assertions
                MaterialButton adminButton = activity.findViewById(R.id.adminButton);
                assertEquals(MaterialButton.INVISIBLE, adminButton.getVisibility());

                // Check greeting message text
                assertEquals("Hello, entrantName",
                        ((android.widget.TextView)activity.findViewById(R.id.greetingMessage)).getText().toString());
            });
        }
    }

    @Test
    public void testAdminUserUI() {
        try (ActivityScenario<UserDashboardView> scenario =
                     ActivityScenario.launch(UserDashboardView.class)) {

            scenario.onActivity(activity -> {
                MaterialButton adminButton = activity.findViewById(R.id.adminButton);
                activity.runOnUiThread(() -> {
                    adminButton.setVisibility(adminUser.isAdmin() ? MaterialButton.VISIBLE : MaterialButton.INVISIBLE);
                });

                assertEquals(MaterialButton.VISIBLE, adminButton.getVisibility());

                assertTrue(adminButton.isClickable());
            });
        }
    }
}
