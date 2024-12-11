package gameapi.manager.data.activity;

import gameapi.manager.data.activity.internal.ActivityParkourMonthlyCompetition202412;

/**
 * @author glorydark
 */
public class ActivityRegistry {

    public static void init() {
        ActivityParkourMonthlyCompetition202412.register();
    }
}