package no.hvl.dat100ptc.oppgave1;

import no.hvl.annotations.CopyOption;
import no.hvl.annotations.Implement;
import no.hvl.annotations.SolutionEnd;
import no.hvl.annotations.SolutionStart;

public class GPSPoint {

    // TODO - objektvariable

    @Implement(number = {1,1}, copyOption = CopyOption.REMOVE_EVERYTHING, replacementId = "")
    private int time;
    @Implement(number = {1,1}, copyOption = CopyOption.REMOVE_EVERYTHING, replacementId = "")
    private double latitude;
    @Implement(number = {1,1}, copyOption = CopyOption.REMOVE_EVERYTHING, replacementId = "")
    private double longitude;
    @Implement(number = {1,1}, copyOption = CopyOption.REMOVE_EVERYTHING, replacementId = "")
    private double elevation;

    @Implement(number = {1,1}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "1")
    public GPSPoint(int time, double latitude, double longitude, double elevation) {

        // TODO - konstruktur


        SolutionStart s;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        SolutionEnd e;
    }

    // TODO - get/set metoder
    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public int getTime() {
        return time;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void setTime(int time) {
        this.time = time;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public double getLatitude() {
        return latitude;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public double getLongitude() {
        return longitude;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public double getElevation() {
        return elevation;
    }

    @Implement(number = {1,2}, copyOption = CopyOption.REPLACE_BODY, replacementId = "2")
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Implement(number = {1,3}, copyOption = CopyOption.REPLACE_SOLUTION, replacementId = "2")
    public String toString() {
        String str;
        SolutionStart s;
        str =   Integer.toString(time) + " " +
                "(" + Double.toString(latitude) + "," +
                Double.toString(longitude) + ") " +
                Double.toString(elevation) + "\n";
        return str;
    }
}
