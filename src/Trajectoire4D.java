import java.util.ArrayList;

public class Trajectoire4D {
    //nombre de points
    int point_count = 0;
    //liste des points
    ArrayList<Point4D> points = new ArrayList<Point4D>();

    public Trajectoire4D(int point_count, ArrayList<Point4D> points) {
        this.point_count = point_count;
        this.points = points;
    }

    public Trajectoire4D() {

    }

    @Override
    public String toString() {
        return "Trajectoire4D{" +
                "point_count=" + point_count +
                ", points=" + points +
                '}';
    }
}
