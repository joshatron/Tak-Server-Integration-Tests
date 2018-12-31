package io.joshatron.tak.server.logic.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.Date;

public class GameUtils {

    public static void requestGame(User requester, User other, Integer size, String requesterColor, String first, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.requestGame(requester.getUsername(), requester.getPassword(), other.getUserId(), size, requesterColor, first, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void deleteGameRequest(User requester, User other, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.deleteGameRequest(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void respondToGameRequest(User requester, User other, String answer, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.respondToGameRequest(requester.getUsername(), requester.getPassword(), other.getUserId(), answer, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void checkIncoming(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.checkIncomingGameRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            if (included != null && included.length > 0) {
                for (User include : included) {
                    //Makes sure there is just one occurrence
                    Assert.assertEquals(1, (contents.length() - contents.replace(include.getUserId(), "").length()) / include.getUsername().length());
                }
            }
            if (excluded != null && excluded.length > 0) {
                for (User exclude : excluded) {
                    Assert.assertFalse(contents.contains(exclude.getUserId()));
                }
            }
        }
    }

    public static void checkOutgoing(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.checkOutgoingGameRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            if (included != null && included.length > 0) {
                for (User include : included) {
                    //Makes sure there is just one occurrence
                    Assert.assertEquals(1, (contents.length() - contents.replace(include.getUsername(), "").length()) / include.getUsername().length());
                }
            }
            if (excluded != null && excluded.length > 0) {
                for (User exclude : excluded) {
                    Assert.assertFalse(contents.contains(exclude.getUsername()));
                }
            }
        }
    }

    public static void requestRandomGame(User requester, int size, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.requestRandomGame(requester.getUsername(), requester.getPassword(), size, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void deleteRandomGameRequest(User requester, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.deleteRandomGameRequest(requester.getUsername(), requester.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void getRandomRequestSize(User requester, HttpClient client, int expected, int expectedSize) throws IOException {
        HttpResponse response = HttpUtils.getRandomRequestSize(requester.getUsername(), requester.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            Assert.assertTrue(contents.contains(Integer.toString(expectedSize)));
        }
    }

    public static JSONArray searchGames(User requester, String opponents, Date start, Date end, String complete, String pending, String sizes, String winner, String color, HttpClient client, int expected, int numExpected) throws IOException {
        HttpResponse response = HttpUtils.searchGames(requester.getUsername(), requester.getPassword(), opponents, start, end, complete, pending, sizes, winner, color, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            if(numExpected >= 0) {
                JSONArray array = new JSONArray(contents);
                Assert.assertEquals(numExpected, array.length());
                return array;
            }
        }

        return null;
    }

    public static void getGame(User requester, String gameId, HttpClient client, int expected, String expectedWhite, String expectedBlack, String[] expectedTurns) throws IOException {
        HttpResponse response = HttpUtils.getGame(requester.getUsername(), gameId, requester.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(contents);
            Assert.assertEquals(expectedWhite, json.getString("white"));
            Assert.assertEquals(expectedBlack, json.getString("black"));
            if(expectedTurns != null) {
                JSONArray array = json.getJSONArray("turns");
                for (int i = 0; i < expectedTurns.length; i++) {
                    Assert.assertEquals(expectedTurns[i], array.getString(i));
                }
            }
        }
    }

    public static void getPossibleMoves(User requester, String gameId, HttpClient client, int expected, int numExpected) throws IOException {
        HttpResponse response = HttpUtils.getPossibleMoves(requester.getUsername(), gameId, requester.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            if(numExpected >= 0) {
                JSONArray array = new JSONArray(contents);
                Assert.assertEquals(numExpected, array.length());
            }
        }
    }

    public static void playTurn(User requester, String gameId, String turn, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.playTurn(requester.getUsername(), gameId, turn, requester.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void checkSocialNotifications(User user, HttpClient client, int expected, int expectedRequests, int expectedGames) throws IOException {
        HttpResponse response = HttpUtils.getGameNotifications(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String contents = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(contents);
            Assert.assertEquals(expectedRequests, json.getInt("incomingRequests"));
            Assert.assertEquals(expectedGames, json.getInt("pendingGames"));
        }
    }
}
