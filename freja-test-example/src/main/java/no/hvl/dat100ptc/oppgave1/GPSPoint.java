package no.hvl.dat100ptc.oppgave1;

import no.hvl.annotations.TransformOption;
import no.hvl.annotations.Exercise;
import no.hvl.annotations.SolutionEnd;
import no.hvl.annotations.SolutionStart;

@SuppressWarnings("ALL")
public class GPSPoint {

    // TO DO - objektvariable

    @Exercise(id = {1,1}, transformOption = TransformOption.REMOVE_EVERYTHING, replacementId = "")
    private int time;
    @Exercise(id = {1,1}, transformOption = TransformOption.REMOVE_EVERYTHING, replacementId = "")
    private double latitude;
    @Exercise(id = {1,1}, transformOption = TransformOption.REMOVE_EVERYTHING, replacementId = "")
    private double longitude;
    @Exercise(id = {1,1}, transformOption = TransformOption.REMOVE_EVERYTHING, replacementId = "")
    private double elevation;

    @Exercise(id = {1,1}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "1")
    public GPSPoint(int time, double latitude, double longitude, double elevation) {

        // TO DO - konstruktur


        SolutionStart s;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        SolutionEnd e;
    }

    // TO DO - get/set metoder
    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public int getTime() {
        return time;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void setTime(int time) {
        this.time = time;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public double getLatitude() {
        return latitude;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public double getLongitude() {
        return longitude;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public double getElevation() {
        return elevation;
    }

    @Exercise(id = {1,2}, transformOption = TransformOption.REPLACE_BODY, replacementId = "2")
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Exercise(id = {1,3}, transformOption = TransformOption.REPLACE_SOLUTION, replacementId = "2")
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
