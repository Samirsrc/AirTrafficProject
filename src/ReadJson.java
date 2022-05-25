import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JsonObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadJson {

     ArrayList<Trajectoire4D> lestrajectoires = new ArrayList<Trajectoire4D>();

    public void lireTrajectoire(String fileName) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName+".json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray trajectoireList = (JSONArray) obj;
            // pour chaque trajectoire
            trajectoireList.forEach(t -> {
                Trajectoire4D traj = new Trajectoire4D();
                JSONObject l = (JSONObject) t;
                JSONArray points = (JSONArray) l.get("trajectoire");
                // pour chaque point
                points.forEach(n -> {

                    JSONArray p = (JSONArray) n;

                   //   Point4D poi = new Point4D((Double) p.get(0), (Double) p.get(1), (Double) p.get(2), (Double) p.get(3));
                    Point4D poi = new Point4D(Double.parseDouble(p.get(0).toString()), Double.parseDouble(p.get(1).toString()), Double.parseDouble(p.get(2).toString()), Integer.parseInt(p.get(3).toString()));

                    traj.points.add(poi);
                    traj.point_count++;

                });
                lestrajectoires.add(traj);
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}