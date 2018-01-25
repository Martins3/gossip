package ku;

import java.util.ArrayList;
import java.util.List;

/**
 * simulate for a hosts
 * configuration for hosts:
 * 1. friends
 * 2. Message
 * 3. id
 * 4. sending rate
 * 5. UDP loss rate
 * 6. state
 *
 */


public class Host{
    private int id;
    private boolean crashed;
    private int sendingRate; // send times in 1 seconds, form 1 to 1000
    private ArrayList<Integer> friends;
    private God god;
    private int versionNum;


    /**
     * For new added friends, renew the scale first !
     * @param id hosts id
     * @param spare determines how many friends to be added !
     */
    Host(int id, double spare) {
        // init
        this.id = id;
        crashed = false;
        god = God.getInstance();

        versionNum = -1;

        friends = new ArrayList<>();
        int friendNum = (int) (spare * god.getScale());
        assert friendNum <= god.getScale();
        assert friendNum > 0;
        friends.addAll(Tools.shuffleNum(0, god.getScale() - 1, friendNum));
        if (!friends.contains((id + 1) % god.getScale()))
            friends.add((id + 1) % god.getScale()); // make sure there is cycle !


        sendingRate = Tools.getRandomNumberInRange(1, 10);
    }

    /**
     * When adding more nodes to the cluster, the previous hosts should add friends to the new one !
     * @param startNum inclusive
     * @param endNum inclusive
     * @param sparse the original graph sparse
     */
    void addFriends(int startNum, int endNum, double sparse){
        int friendNum = (int) (sparse * god.getScale());
        friends.addAll(Tools.shuffleNum(startNum, endNum, friendNum));
    }




    /**
     * sending rate can be base on sendingRate
     * @return adding some msg to
     */
    ArrayList<Message> sendMsg(){
        ArrayList<Message> s = new ArrayList<>();
        for (int i = 0; i < sendingRate; i++) {
            // random choose a friend
            int friendIndex = Tools.getRandomNumberInRange(0, friends.size() - 1);
            int friend = friends.get(friendIndex);
            s.add(new Message(MsgType.Normal, versionNum, id, friend));
        }
        return s;
    }

    // getter and setter


    public int getVersionNum() {
        return versionNum;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public void setCrashed(boolean crashed){
        this.crashed = crashed;
    }
}

