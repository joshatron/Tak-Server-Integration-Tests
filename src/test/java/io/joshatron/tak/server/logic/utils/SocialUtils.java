package io.joshatron.tak.server.logic.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.util.Date;

public class SocialUtils {

    public static void requestFriend(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.requestFriend(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void cancelRequest(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.cancelFriendRequest(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void respondToRequest(User user, User other, String answer, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.respondToRequest(user.getUsername(), user.getPassword(), other.getUserId(), answer, client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void checkIncoming(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        Response response = HttpUtils.checkIncomingFriendRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            String contents = response.getContents();
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

    public static void checkOutgoing(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        Response response = HttpUtils.checkOutgoingFriendRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            String contents = response.getContents();
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

    public static void unfriendUser(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.unfriendUser(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void blockUser(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.blockUser(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void unblockUser(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.unblockUser(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void checkIfBlocked(User requester, User other, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.checkIfBlocked(requester.getUsername(), requester.getPassword(), other.getUserId(), client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void checkFriends(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        Response response = HttpUtils.getFriends(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            String contents = response.getContents();
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

    public static void checkBlocking(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        Response response = HttpUtils.getBlocked(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            String contents = response.getContents();
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

    public static void sendMessage(User sender, User reciever, String message, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.sendMessage(sender.getUsername(), sender.getPassword(), reciever.getUserId(), message, client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static JSONArray searchMessages(User user, String senders, Date start, Date end, String read, String from, HttpClient client, int expected, int numExpected) throws IOException {
        Response response = HttpUtils.searchMessages(user.getUsername(), user.getPassword(), senders, start, end, read, from, client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            if(numExpected >= 0) {
                JSONArray array = new JSONArray(response.getContents());
                Assert.assertEquals(numExpected, array.length());
                return array;
            }
        }

        return null;
    }

    public static JSONArray searchAllMessages(User user, HttpClient client, int expected, int numExpected) throws IOException {
        return searchMessages(user, null, null, null, null, null, client, expected, numExpected);
    }

    public static void markMessagesRead(User user, String[] ids, User[] senders, HttpClient client, int expected) throws IOException {
        Response response = HttpUtils.markRead(user.getUsername(), user.getPassword(), ids, senders, client);
        Assert.assertEquals(expected, response.getStatus());
    }

    public static void checkSocialNotifications(User user, HttpClient client, int expected, int expectedRequests, int expectedMessages) throws IOException {
        Response response = HttpUtils.getSocialNotifications(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatus());
        if(response.getStatus() == HttpStatus.SC_OK) {
            JSONObject json = new JSONObject(response.getContents());
            Assert.assertEquals(expectedRequests, json.getInt("incomingRequests"));
            Assert.assertEquals(expectedMessages, json.getInt("incomingMessages"));
        }
    }
}
