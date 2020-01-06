package mainApp;

import mainApp.JobAdvertisement;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Transform {

    public static List<JobAdvertisement> run(String keyword, String fileName) {
        List<JobAdvertisement> jobAdvertisementList = null;
        try {
            String pathName = "C:\\OLX/" + fileName + ".txt";
            File input = new File(pathName);

            if (!input.exists()) {
                throw new FileNotFoundException("HTML file not found: " + pathName);
            } else {
                Document doc = Jsoup.parse(input, "UTF-8", "");

                jobAdvertisementList = loadJobAdvs(doc, keyword);
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("OLX_HTML load error");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobAdvertisementList;
    }

    private static List<JobAdvertisement> loadJobAdvs(Document doc, String keyword) throws ParseException {
        List<JobAdvertisement> jobAdvertisements = new ArrayList<>();
        Elements elements = doc.select("#offers_table > tbody > tr");
        Elements filterElements = new Elements();

        for (Element e : elements) {
            if (e.toString().contains("offer-wrapper")) {
                filterElements.add(e);
            }
        }

        System.out.println("noOffilterElements: " + filterElements.size());

        for (Element e : filterElements) {
            String id = e.select("td > div > table").attr("data-id");
            String title = e.select("td > div > table > tbody > tr:nth-child(1) > td.title-cell > div > h3 > a > strong").text();
            String workAddress = e.select("td > div > table > tbody > tr:nth-child(2) > td.bottom-cell > div > p > small:nth-child(1) > span").text();

            String olxDateFormat = e.select("td > div > table > tbody > tr:nth-child(2) > td.bottom-cell > div > p > small:nth-child(2) > span").text();
            String announcementDate = setProperDateFormat(olxDateFormat);

            String contractType = e.select("td > div > table > tbody > tr:nth-child(2) > td.bottom-cell > div > p > small:nth-child(3) > span").text();
            String specialRequirements = e.select("td > div > table > tbody > tr:nth-child(2) > td.bottom-cell > div > p > small:nth-child(4) > span").text();
            String salary = e.select("td > div > table > tbody > tr:nth-child(1) > td.title-cell > div > div").text();
            jobAdvertisements.add(new JobAdvertisement(
                    keyword, id, title, workAddress, announcementDate, contractType, specialRequirements, salary
            ));

        }
        System.out.println("jobAdvertisements size: " + jobAdvertisements.size());
        return jobAdvertisements;

    }

    /**
     * olx adv posted year indicator
     */
    public static String setProperDateFormat(String announcementDateString) throws ParseException {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
        if (announcementDateString.contains("dzisiaj")) {
            return dtfOut.print(new DateTime(Calendar.getInstance().getTime()));
        } else if (announcementDateString.contains("wczoraj")) {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            return dtfOut.print(new DateTime(cal.getTime()));
        } else {
            DateFormat parser = new SimpleDateFormat("dd MMM", Locale.forLanguageTag("pl-PL"));
            String parsedAnnouncementDate = parser.parse(announcementDateString).toString();
            String[] dateArray = parsedAnnouncementDate.split(" ");
            String monthString = dateArray[1];
            Integer day = Integer.parseInt(dateArray[2]);
            Date month = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(month);
            int monthInt = cal.get(Calendar.MONTH);
            /** olx adv posted year indicator */
            int year = (monthInt <= 12 && monthInt > 9) ? 2019 : 2020;
            return dtfOut.print(new DateTime(new GregorianCalendar(year, cal.get(Calendar.MONTH), day).getTime()));
        }
    }

}
