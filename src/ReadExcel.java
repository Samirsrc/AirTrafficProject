
import org.apache.poi.ss.usermodel.Row;
        import org.apache.poi.xssf.usermodel.XSSFSheet;
        import org.apache.poi.xssf.usermodel.XSSFWorkbook;

        import java.io.File;
        import java.io.FileInputStream;
        import java.util.ArrayList;
        import java.util.Iterator;

public class ReadExcel {
    static ArrayList<Vol> vols = new ArrayList<Vol>();

    static public void lireTrajectoire() {

        int j = 0;
        for (int n = 0; n < 7; n++) {

            vols.add(new Vol(new Trajectoire4D()));

            try {
                String path = "data/benchmarkCyril/man_15ac_1err_" + n + ".xlsx";
                FileInputStream fs = new FileInputStream(new File(path));
                //data/benchmarkCyril/man_15ac_1err_1
                XSSFWorkbook wb = new XSSFWorkbook(fs);
                XSSFSheet sheet = wb.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.iterator();

                int i = 0;
                while (rowIterator.hasNext()) {

                    Row row = rowIterator.next();
                    String ligne[] = String.valueOf(row.getCell(0).getStringCellValue()).split(" ");
                    //  System.out.println(ligne[0]);
                    if (ligne[1].matches("0")) {

                        if (i > 149) {
                            j++;
                            vols.add(new Vol(new Trajectoire4D()));
                            i = 0;

                        }
                        vols.get(j).trajectoire4D.points.add(new Point4D(Double.parseDouble(ligne[6]), Double.parseDouble(ligne[5]), Double.parseDouble(ligne[3]), Integer.parseInt(ligne[2])));
                        i++;


                    }


                }

                fs.close();
                wb.close();
                rowIterator.remove();

            } catch (Exception e) {
                e.printStackTrace();

            }
        }


    }
    //      System.out.println(vols);
    //   System.out.println(vols.size());


}




