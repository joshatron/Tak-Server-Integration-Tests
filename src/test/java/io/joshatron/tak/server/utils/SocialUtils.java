package io.joshatron.tak.server.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import java.io.IOException;
import java.util.Date;

public class SocialUtils {

    public static void requestFriend(User requester, User other, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.requestFriend(requester.getUsername(), requester.getPassword(), other.getUsername(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void cancelRequest(User requester, User other, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.cancelFriendRequest(requester.getUsername(), requester.getPassword(), other.getUsername(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void respondToRequest(User user, User other, String answer, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.respondToRequest(user.getUsername(), user.getPassword(), other.getUsername(), answer, client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void checkIncoming(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.checkIncomingFriendRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        String contents = EntityUtils.toString(response.getEntity());
        if(included != null && included.length > 0) {
            for(User include : included) {
                //Makes sure there is just one occurrence
                Assert.assertEquals(1, (contents.length() - contents.replace(include.getUsername(), "").length()) / include.getUsername().length());
            }
        }
        if(excluded != null && excluded.length > 0) {
            for(User exclude : excluded) {
                Assert.assertFalse(contents.contains(exclude.getUsername()));
            }
        }
    }

    public static void checkOutgoing(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.checkOutgoingFriendRequests(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        String contents = EntityUtils.toString(response.getEntity());
        if(included != null && included.length > 0) {
            for(User include : included) {
                //Makes sure there is just one occurrence
                Assert.assertEquals(1, (contents.length() - contents.replace(include.getUsername(), "").length()) / include.getUsername().length());
            }
        }
        if(excluded != null && excluded.length > 0) {
            for(User exclude : excluded) {
                Assert.assertFalse(contents.contains(exclude.getUsername()));
            }
        }
    }

    public static void unfriendUser(User requester, User other, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void blockUser(User requester, User other, HttpClient client, int expected) throws IOException {
        HttpResponse response = HttpUtils.blockUser(requester.getUsername(), requester.getPassword(), other.getUsername(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
    }

    public static void unblockUser(User requester, User other, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void checkIfBlocked(User requester, User other, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void checkFriends(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.getFriends(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        String contents = EntityUtils.toString(response.getEntity());
        if(included != null && included.length > 0) {
            for(User include : included) {
                //Makes sure there is just one occurrence
                Assert.assertEquals(1, (contents.length() - contents.replace(include.getUsername(), "").length()) / include.getUsername().length());
            }
        }
        if(excluded != null && excluded.length > 0) {
            for(User exclude : excluded) {
                Assert.assertFalse(contents.contains(exclude.getUsername()));
            }
        }
    }

    public static void checkBlocking(User user, HttpClient client, int expected, User[] included, User[] excluded) throws IOException {
        HttpResponse response = HttpUtils.getBlocked(user.getUsername(), user.getPassword(), client);
        Assert.assertEquals(expected, response.getStatusLine().getStatusCode());
        String contents = EntityUtils.toString(response.getEntity());
        if(included != null && included.length > 0) {
            for(User include : included) {
                //Makes sure there is just one occurrence
                Assert.assertEquals(1, (contents.length() - contents.replace(include.getUsername(), "").length()) / include.getUsername().length());
            }
        }
        if(excluded != null && excluded.length > 0) {
            for(User exclude : excluded) {
                Assert.assertFalse(contents.contains(exclude.getUsername()));
            }
        }
    }

    public static void sendMessage(User sender, User reciever, String message, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void searchMessages(User user, String senders, Date start, Date end, String read, String from, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void markMessagesRead(User user, String[] ids, Date start, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }

    public static void checkSocialNotifications(User user, HttpClient client, int expected) throws IOException {
        //TODO: implement
    }
}
