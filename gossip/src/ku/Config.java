package ku;

/**
 * 仅仅用于封装数据, 真实数据来自于God中间
 */
public class Config {
    private int scale;
    private int udpLossRate;
    private double sparse;

    public Config(int scale, int udpLossRate, double sparse) {
        this.scale = scale;
        this.udpLossRate = udpLossRate;
        this.sparse = sparse;
    }

    public int getScale() {
        return scale;
    }

    public int getUdpLossRate() {
        return udpLossRate;
    }

    public double getSparse() {
        return sparse;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setUdpLossRate(int udpLossRate) {
        this.udpLossRate = udpLossRate;
    }

    public void setSparse(double sparse) {
        this.sparse = sparse;
    }
}
