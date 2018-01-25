package ku;

/**
 * Message send between hosts
 */
public class Message {
    MsgType msgType; // God and Host all send message
    private int versionNum; // version number is transparent to Host, only god know it
    private int receiver;
    private int sender;


    public MsgType getMsgType() {
        return msgType;
    }

    public int getVersionNum() {
        return versionNum;
    }


    public Message(MsgType msgType, int versionNum, int sender,  int receiver) {
        this.msgType = msgType;
        this.versionNum = versionNum;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getReceiver() {
        return receiver;
    }

    public int getSender() {
        return sender;
    }

    public Message(MsgType msgType) {
        this.msgType = msgType;

    }
}
