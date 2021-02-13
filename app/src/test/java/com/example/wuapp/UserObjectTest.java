package com.example.wuapp;

import android.view.MotionEvent;

import com.example.wuapp.model.Event;
import com.example.wuapp.model.Game;
import com.example.wuapp.model.User;
import com.example.wuapp.model.UserLoginToken;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the User Object
 */
public class UserObjectTest {

    private static User user;

    private static Future<List<Game>> mockedFutureGames = Mockito.mock(Future.class);

    private static Future<List<Event>> mockedFutureEvents = Mockito.mock(Future.class);

    private static Future<List<Event>> mockedEmptyEvents = Mockito.mock(Future.class);

    private static Future<List<Event>> mockedNullEvents = Mockito.mock(Future.class);

    @BeforeClass
    public static void setupUserObjects() throws Exception{
        //user = new User(new UserLoginToken(null, null, null , null), "testUser", null, null, null);
        List<Game> gamesList = getGamesList();
        List<Event> eventsList = getEventList();

        when(mockedFutureGames.get()).thenReturn(gamesList);
        when(mockedFutureEvents.get()).thenReturn(eventsList);
        when(mockedEmptyEvents.get()).thenReturn(new ArrayList<>());
        when(mockedNullEvents.get()).thenReturn(null);
    }

    private static List<Game> getGamesList() {
        List<Game> list = new ArrayList<>();

        Game g1 = Mockito.mock(Game.class);
        when(g1.isReportable()).thenReturn(true);
        when(g1.isUpcoming()).thenReturn(true);
        list.add(g1);

        Game g2 = Mockito.mock(Game.class);
        when(g2.isReportable()).thenReturn(true);
        when(g2.isUpcoming()).thenReturn(false);
        list.add(g2);

        Game g3 = Mockito.mock(Game.class);
        when(g3.isReportable()).thenReturn(false);
        when(g3.isUpcoming()).thenReturn(true);
        list.add(g3);

        Game g4 = Mockito.mock(Game.class);
        when(g4.isReportable()).thenReturn(false);
        when(g4.isUpcoming()).thenReturn(false);
        list.add(g4);

        return list;
    }

    private static List<Event> getEventList(){
        List<Event> list = new ArrayList<>();

        Event e1 = Mockito.mock(Event.class);
        when(e1.getName()).thenReturn("Event1");
        list.add(e1);

        Event e2 = Mockito.mock(Event.class);
        when(e1.getName()).thenReturn("Event2");
        list.add(e2);

        Event e3 = Mockito.mock(Event.class);
        when(e1.getName()).thenReturn("Event3");
        list.add(e3);

        return list;
    }

    @Test
    public void User_GetEventWithCorrectName_ReturnsEvent() {
        user.setData(mockedFutureEvents, mockedFutureGames);
        Event result = user.getEvent("Event1");
        assertNotNull("The Event should not be null", result);
        assertEquals(result, "Event1");
    }

    @Test
    public void User_GetEventWithIncorrectName_ReturnsNull() {
        user.setData(mockedFutureEvents, mockedFutureGames);
        Event result = user.getEvent("Evment1");
        assertNull("The Event should be null", result);
    }

    @Test
    public void User_GetEventNullName_ReturnsNull() {
        user.setData(mockedFutureEvents, mockedFutureGames);
        Event result = user.getEvent(null);
        assertNull("The Event should be null", result);
    }

    @Test
    public void UserWithNoEvents_GetEvent_ReturnsNull() {
        user.setData(mockedEmptyEvents, mockedFutureGames);
        Event result = user.getEvent("Event1");
        assertNull("The Event should be null", result);
    }

    @Test
    public void UserWithNullEvents_GetEvent_Returns(){
        user.setData(mockedNullEvents, mockedFutureGames);
        Event result = user.getEvent("Event1");
        assertNull("The Event should be null", result);
    }



}
