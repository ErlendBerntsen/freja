package no.hvl.dat100example.oppgave4;

import no.hvl.dat100example.oppgave1.GPSPoint;
import no.hvl.dat100example.oppgave2.GPSData;
import no.hvl.dat100example.oppgave2.GPSDataConverter;
import no.hvl.dat100example.oppgave2.GPSDataFileReader;
import no.hvl.dat100example.oppgave3.GPSUtils;

public class GPSComputer {

    private GPSPoint[] gpspoints;

    public GPSComputer(String filename) {
        GPSData gpsdata = GPSDataFileReader.readGPSFile(filename);
        gpspoints = gpsdata.getGPSPoints();
    }

    public GPSComputer(GPSPoint[] gpspoints) {
        this.gpspoints = gpspoints;
    }

    public GPSPoint[] getGPSPoints() {
        return this.gpspoints;
    }

    // beregn total distances (i meter)
    public double totalDistance() {
        double distance = 0;
        // TODO - START
        for (int i = 0; i < gpspoints.length - 1; i++) {
            distance = distance + GPSUtils.distance(gpspoints[i], gpspoints[i + 1]);
        }
        // TODO - SLUTT
        return distance;
    }

    // beregn totale høydemeter (i meter)
    public double totalElevation() {
        double elevation = 0;
        // TODO
        // OPPGAVE - START
        for (int i = 0; i < gpspoints.length - 1; i++) {
            double diff = gpspoints[i + 1].getElevation() - gpspoints[i].getElevation();
            // sum only if we are going up between two points
            if (diff > 0) {
                elevation = elevation + diff;
            }
        }
        // OPPGAVE - SLUTT
        return elevation;
    }

    // beregn total tiden for hele turen (i sekunder)
    public int totalTime() {
        return gpspoints[gpspoints.length - 1].getTime() - gpspoints[0].getTime();
    }

    // beregn gjennomsnitshastighets mellom hver av gps punktene
    public double[] speeds() {
        double[] speeds = new double[gpspoints.length - 1];
        // TODO
        // OPPGAVE - START
        for (int i = 0; i < gpspoints.length - 1; i++) {
            double speed = GPSUtils.speed(gpspoints[i], gpspoints[i + 1]);
            speeds[i] = speed;
        }
        // OPPGAVE - SLUTT
        return speeds;
    }

    public double maxSpeed() {
        double maxspeed = 0;
        // TODO - START
        double[] speeds = speeds();
        maxspeed = GPSUtils.findMax(speeds);
        // TODO - SLUTT
        return maxspeed;
    }

    public double averageSpeed() {
        double average = 0;
        // TODO - START
        int totaltime = totalTime();
        double distance = totalDistance();
        average = ((distance / totaltime) * 60 * 60) / 1000;
        // TODO - SLUTT
        return average;
    }

    /*
	 * bicycling, <10 mph, leisure, to work or for pleasure 4.0 bicycling,
	 * general 8.0 bicycling, 10-11.9 mph, leisure, slow, light effort 6.0
	 * bicycling, 12-13.9 mph, leisure, moderate effort 8.0 bicycling, 14-15.9
	 * mph, racing or leisure, fast, vigorous effort 10.0 bicycling, 16-19 mph,
	 * racing/not drafting or >19 mph drafting, very fast, racing general 12.0
	 * bicycling, >20 mph, racing, not drafting 16.0
	 */
    // conversion factor m/s to miles per hour
    public static double MS = 2.236936;

    // beregn kcal gitt weight og tid der kjøres med en gitt hastighet
    public double kcal(double weight, int secs, double speed) {
        double kcal;
        // MET: Metabolic equivalent of task angir (kcal x kg-1 x h-1)
        double met = 0;
        double speedmph = speed * MS;
        // TODO - START
        if (speedmph < 10.0) {
            met = 4.0;
        } else if (speedmph < 12.0) {
            met = 6.0;
        } else if (speedmph < 14.0) {
            met = 8.0;
        } else if (speedmph < 16.0) {
            met = 10.0;
        } else if (speedmph < 20.0) {
            met = 12.0;
        } else {
            met = 16.0;
        }
        // Energy Expended (kcal) = MET x Body Weight (kg) x Time (h)
        kcal = met * weight * (secs / (60.0 * 60.0));
        // TODO - SLUTT
        return kcal;
    }

    public double totalKcal(double weight) {
        double totalkcal = 0;
        // TODO - START
        double[] speeds = speeds();
        for (int i = 0; i < speeds.length; i++) {
            int secs = gpspoints[i + 1].getTime() - gpspoints[i].getTime();
            double speed = speeds[i];
            double kcal = kcal(weight, secs, speed);
            totalkcal = totalkcal + kcal;
        }
        // TODO - SLUTT
        return totalkcal;
    }

    private static double WEIGHT = 80.0;

    public void displayStatistics() {
        System.out.println("==============================================");
        // TODO - START
        System.out.println("Total Time     : " + GPSUtils.formatTime(totalTime()));
        System.out.println("Total distance : " + GPSUtils.formatDouble(totalDistance() / 1000.0) + " km");
        System.out.println("Total elevation: " + GPSUtils.formatDouble(totalElevation()) + " m");
        System.out.println("Max speed      : " + GPSUtils.formatDouble(maxSpeed()) + " km/t");
        System.out.println("Average speed  : " + GPSUtils.formatDouble(averageSpeed()) + " km/t");
        System.out.println("Energy         : " + GPSUtils.formatDouble(totalKcal(WEIGHT)) + " kcal");
        // TODO - SLUTT
        System.out.println("==============================================");
    }
}
