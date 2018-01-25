package ku;

import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

/**
 * God is created for simulating network connection between hosts
 * There is at least an cycle to make sure the graph is strongly connected
 *
 * God create the "World"
 * 1. read the config file
 * 2. create the all the host, and conf the host
 * 3. make all the hosts
 *
 * we need a seed to be config !
 */
public class God{
    private static God instance;
    private List<Host> hosts;
    private int scale = 1000;
    private BlockingQueue<Message> messageBox;
    private MessageCreator creator;
    private MessageHandler handler;
    private ExecutorService executor = null;
    private int version;
    private volatile int udpLossRate = 90;
    private LinkedList<Integer> aliveHosts;
    private LinkedList<Integer> deadHosts;
    private double sparse = 0.01;
    private boolean clusterReady = false;
    private MainWindow window;

    public static God getInstance(){
        if (instance == null) {
            synchronized (God.class) {
                if (instance == null) {
                    instance = new God();
                }
            }
        }
        return instance;
    }


    void initCluster(){
        initCluster(scale, sparse, udpLossRate);
    }

    void initCluster(int num, double sparse, int dupLossRates){
        // 放置之前没有删除
        finish();
        version = 1;
        window.clearChart();

        messageBox = new LinkedBlockingQueue<>();
        hosts = Collections.synchronizedList(new ArrayList<>(num));
        executor = Executors.newFixedThreadPool(2);
        aliveHosts = new LinkedList<>();
        deadHosts = new LinkedList<>();
        clusterReady = true;
        scale = num;
        udpLossRate = dupLossRates;


        for (int i = 0; i < num; i++) {
            hosts.add(new Host(i, sparse));
            aliveHosts.add(i);
        }

        creator = new MessageCreator();
        handler = new MessageHandler();

        executor.execute(creator);
        executor.execute(handler);
    }

    void start(){
        if(!clusterReady) return;

        creator.setFreeze(false);
        creator.revive.add(new Message(MsgType.Fix));
    }

    void stop(){
        if(!clusterReady) return;

        creator.setFreeze(true);
    }


    void finish(){
        if(creator != null) {
            creator.setRun(false);
            creator.setFreeze(false);
            creator.revive.add(new Message(MsgType.Fix));
        }

        if(handler != null) {
            handler.setRun(false);
            messageBox.add(new Message(MsgType.Fix, -1, -1, -1));
        }

        if(executor != null) {
            executor.shutdown();
        }
    }

    /**
     * by change the state of host to no receiving and sending
     * after destroy, the graph may be not connected !
     * @param num the number of hosts to be destroyed
     */
    void randomDestroy(int num){
        if(num <= 0){
            System.out.println("Error :  at least destroy one host !");
            return;
        }

        while (aliveHosts.size() > 0 && num > 0){
            int ith = Tools.getRandomNumberInRange(0, aliveHosts.size() - 1);
            int host = aliveHosts.get(ith);
            hosts.get(host).setCrashed(true);

            deadHosts.add(host);
            aliveHosts.remove(ith);

            num --;
        }
    }


    void randomRevive(int num){
        if(num <= 0){
            System.out.println("Error :  at least revive one host !");
            return;
        }

        while (deadHosts.size() > 0 && num > 0){
            int ith = Tools.getRandomNumberInRange(0, deadHosts.size() - 1);
            int host = deadHosts.get(ith);
            hosts.get(host).setCrashed(false);

            aliveHosts.add(host);
            deadHosts.remove(ith);

            num --;
        }
    }


    TreeMap<Integer, Integer> showMessagePercentage(){
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        List<Host> copyHosts = new CopyOnWriteArrayList<>(hosts);
        for (Host h : copyHosts) {
            if (!treeMap.keySet().contains(h.getVersionNum())) {
                treeMap.put(h.getVersionNum(), 1);
            } else {
                treeMap.put(h.getVersionNum(), treeMap.get(h.getVersionNum()) + 1);
            }
        }

        return treeMap;
    }


    private void showData(){
        TreeMap<Integer, Integer> a = showMessagePercentage();
        for (Map.Entry<Integer, Integer> entry: a.entrySet()){
            System.out.printf("%d %d\n", entry.getKey(), entry.getValue());
        }
    }


    int getScale(){
        return scale;
    }

    int getUdpLossRate(){
        return udpLossRate;
    }

    double getSparse(){
        return sparse;
    }

    void setWindow(MainWindow mainWindow){
        window = mainWindow;
    }

    void setClusterConfig(Config config){
        int scale = config.getScale();
        int udpLossRate = config.getUdpLossRate();
        double sparse = config.getSparse();

        if(scale > 0 && scale <= 10000)  this.scale = scale;
        if(udpLossRate >=0 && udpLossRate <= 100) this.udpLossRate = udpLossRate;
        if(sparse > 0 && sparse <= 1 ) this.sparse = sparse;

        initCluster();
    }

    public void printGraph() {
        for(Host h: hosts){
            System.out.printf("%d :", h.getId());
            for(int f: h.getFriends()){
                System.out.printf(" %d", f);
            }
            System.out.println();
        }
    }

    public void insertNewMessage() {
        if(!clusterReady) return;

        // random choose a alive host and send a new version to it
        int choose = Tools.getRandomNumberInRange(0, aliveHosts.size() - 1);
        choose = aliveHosts.get(choose);

        System.out.printf("send %d to  %d\n", version, choose);
        messageBox.add(new Message(MsgType.Normal, version++, -1, choose));
    }

    public void addNewHosts(int num) {
        if(!clusterReady) return;
        // added new hosts are alive
        for (int i = scale; i < scale + num; i++) {
            aliveHosts.add(i);
        }


        // old hosts should add friends form new one
        for (int i = 0; i < scale; i++) {
            hosts.get(i).addFriends(scale, scale + num - 1, sparse);
        }

        // new hosts have friend from every every
        int originalScale = scale;
        scale = scale + num;
        for (int i = originalScale; i < originalScale + num; i++) {
            hosts.add(new Host(i, sparse));
        }
    }

    class MessageCreator implements Runnable{
        volatile boolean run = true;
        volatile boolean freeze = true;
        ArrayList<Message> messages;
        BlockingQueue<Message> revive = new LinkedBlockingQueue<>();
        MessageCreator(){
            messages = new ArrayList<>();
        }

        public void setRun(boolean run) {
            this.run = run;
        }

        public void setFreeze(boolean freeze) {
            this.freeze = freeze;
        }


        @Override
        public void run() {
            while (run){
                try {
                    for (int i = 0; i < scale; i++) {
                        messages.addAll(hosts.get(i).sendMsg());
                    }
                    Collections.shuffle(messages);

                    if(messageBox.size() > 0){
                        sleep(1000);
                    }

                    if(freeze){
                        revive.take();
                    }

                    showData();
                    window.addRenderingMsg();

                    messageBox.addAll(messages);
                    messages.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Message Creator over");
        }

    }

    class MessageHandler implements Runnable{
        volatile boolean run = true;
        public void setRun(boolean run) {
            this.run = run;
        }

        @Override
        public void run() {
            while (run){
                try {
                    Message m = messageBox.take();


                    // come from system
                    if(m.getSender() == -1){
                        if(m.msgType == MsgType.Fix) continue;
                        hosts.get(m.getReceiver()).setVersionNum(m.getVersionNum());
                        continue;
                    }

                    Host rec = hosts.get(m.getReceiver());
                    Host sed = hosts.get(m.getSender());

                    // Crashed host can not send or receive
                    if(rec.isCrashed() || sed.isCrashed()) continue;
                    if(sed.getVersionNum() == -1) continue;

                    // Udp may loss the data
                    int toss = Tools.getRandomNumberInRange(1, 100);
                    if(toss < udpLossRate) continue;


                    if(m.getVersionNum() > rec.getVersionNum()){
                        rec.setVersionNum(m.getVersionNum());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Msg Handler over");
        }
    }
}
