package com.gumba.kevin.translinktravel;

public class CONFIG {
    public static final String API_KEY = "apikey";
    public static final int REQUEST_CODE_ASK_PERMISSION = 1;

    public static final String SETTINGS = "Settings";
    public static final String CANCEL = "Cancel";
    public static final String DIALOG_REQUEST_TURN_ON_LOCATION_SETTINGS = "Please turn on Location Settings to use this feature";

    public static final int MIN_TIME_LOCATION_UPDATES = 1000;
    public static final int MIN_DISTANCE_LOCATION_UPDATES = 1;
    public static final long ONE_MINUTE_IN_MILLISECONDS = 1 * 60 * 1000;

    public static final String TRANSLINK_NEARBY_BUS_STOP_BASE_URL = "http://api.translink.ca/rttiapi/v1/stops?";
    public static final String TRANSLINK_BUS_STOP_DETAILS_BASE_URL = "http://api.translink.ca/rttiapi/v1/stops/{0}/estimates?"; // Replace {0} with bus number

    public static final String LONGITUDE = "long";
    public static final String LATITUDE = "lat";
    public static final String TITLE_BUS_STOP_NUMBER = "Bus Stop #";
    public static final String BUS_STOP_NUMBER = "StopNo";
    public static final String BUS_ROUTE_NUMBER = "RouteNo";
    public static final String BUS_STOP_NUMBER_PREFIX = "Stop#:";
    public static final String BUS_STOP_NAME = "Name";
    public static final String BUS_ROUTE_NAME = "RouteName";
    public static final String BUS_STOP_DISTANCE = "Distance";
    public static final String BUS_STOP_DISTANCE_SUFFIX = "m away";
    public static final String BUS_STOP_SCHEDULES = "Schedules";
    public static final String BUS_STOP_EXPECTED_LEAVE_TIME = "ExpectedLeaveTime";
    public static final String BUS_STOP_EXPECTED_COUNTDOWN = "ExpectedCountdown";
    public static final String BUS_STOP_EXPECTED_COUNTDOWN_SUFFIX = "mins";
    public static final String NOT_AVAILABLE = "N/A";
}
