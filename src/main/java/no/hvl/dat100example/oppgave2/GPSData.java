package no.hvl.dat100example.oppgave2;

import no.hvl.dat100example.oppgave1.GPSPoint;

public class GPSData {

    private GPSPoint[] gpspoints;

    protected int antall = 0;

    public GPSData(int antall) {
        // TODO - START
        gpspoints = new GPSPoint[antall];
        antall = 0;
        // TODO - SLUTT
    }

    public GPSPoint[] getGPSPoints() {
        return this.gpspoints;
    }

    protected boolean insertGPS(GPSPoint gpspoint) {
        boolean inserted = false;
        // TODO - START
        if (antall < gpspoints.length) {
            gpspoints[antall] = gpspoint;
            antall++;
            inserted = true;
        }
        // TODO - SLUTT
        return inserted;
    }

    public boolean insert(String time, String latitude, String longitude, String elevation) {
        GPSPoint gpspoint;
        // TODO - START
        gpspoint = GPSDataConverter.convert(time, latitude, longitude, elevation);
        boolean inserted = insertGPS(gpspoint);
        // TODO - SLUTT
        return inserted;
    }

    public void print() {
        System.out.println("====== Konvertert GPS Data - START ======");
        // TODO - START
        for (GPSPoint g : gpspoints) {
            System.out.print(g.toString());
        }
        // TODO - SLUTT
        System.out.println("====== Konvertert GPS Data - SLUTT ======");
    }
}
