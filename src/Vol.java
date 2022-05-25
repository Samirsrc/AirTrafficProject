public class Vol {
    int id;
    static int idd;
    Trajectoire4D trajectoire4D;
    int retard = 0;
    int instantDeDecollage = 0;

    public Vol(Trajectoire4D trajectoire4D) {
        this.id = idd;
        idd++;
        this.trajectoire4D = trajectoire4D;
    }


    @Override
    public String toString() {
        return "Vol{" +
                "id=" + id +
                ", trajectoire4D=" + trajectoire4D +
                ", retard=" + retard +
                ", instantDeDecollage=" + instantDeDecollage +
                '}';
    }
}
