package no.hvl.dat100example.oppgave2;

import no.hvl.dat100example.oppgave1.GPSPoint;

public class GPSDataConverter {

    // konverter tidsinformasjon i gps data punkt til antall sekunder fra midnatt
    // dvs. ignorer information om dato og omregn tidspunkt til sekunder
    // Eksempel - tidsinformasjon (som String): 2017-08-13T08:52:26.000Z
    // skal omregnes til sekunder (som int): 8 * 60 * 60 + 52 * 60 + 26
    // startindex for tidspunkt i timestr
    private static int TIME_STARTINDEX = 11;

    public static int toSeconds(String timestr) {
        int secs;
        int hr, min, sec;
        // TODO
        // OPPGAVE - START
        String ts = timestr.substring(TIME_STARTINDEX);
        hr = Integer.parseInt(ts.substring(0, 2));
        min = Integer.parseInt(ts.substring(3, 5));
        sec = Integer.parseInt(ts.substring(6, 8));
        secs = hr * 60 * 60 + min * 60 + sec;
        // OPPGAVE - SLUTT
        return secs;
    }

    public static GPSPoint convert(String timeStr, String latitudeStr, String longitudeStr, String elevationStr) {
        GPSPoint gpspoint;
        // TODO - START ;
        int time = toSeconds(timeStr);
        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);
        double elevation = Double.parseDouble(elevationStr);
        gpspoint = new GPSPoint(time, latitude, longitude, elevation);
        // OPPGAVE - SLUTT ;
        return gpspoint;
    }
}
