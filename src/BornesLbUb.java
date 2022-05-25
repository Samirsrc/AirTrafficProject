public class BornesLbUb {
    int lb;
    int ub;


    public BornesLbUb(int lb,int ub) {
        this.lb = lb;
        this.ub = ub;

    }

    public void setLb(int lb) {
        this.lb = lb;
    }

    public void setUb(int ub) {
        this.ub = ub;
    }

    public int getLb() {
        return lb;
    }

    public int getUb() {
        return ub;
    }

    @Override
    public String toString() {
        return "BornesLbUb{" +
                "lb=" + lb +
                ", ub=" + ub +
                '}';
    }
}
