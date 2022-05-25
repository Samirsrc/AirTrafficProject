public class Point4D {
    double latitude;
    double longitude;
    double altitude;
    int instantDePassage;

    public Point4D(double latitude, double longitude, double altitude, int instantDePassage) {
        this.instantDePassage = instantDePassage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "Point4D{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", instantDePassage=" + instantDePassage +
                '}';
    }


}
