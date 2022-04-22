// package no.hvl.dat100example.oppgave5;
// 
// import easygraphics.EasyGraphics;
// import no.hvl.dat100example.oppgave1.GPSPoint;
// import no.hvl.dat100example.oppgave2.GPSData;
// import no.hvl.dat100example.oppgave2.GPSDataConverter;
// import no.hvl.dat100example.oppgave2.GPSDataFileReader;
// import no.hvl.dat100example.oppgave4.GPSComputer;
// 
// import javax.swing.JOptionPane;
// 
// public class ShowProfile extends EasyGraphics {
// 
// private static final int MARGIN = 50;		// margin on the sides
// 
// //FIXME: use highest point and scale accordingly
// private static final int MAXBARHEIGHT = 500; // assume no height above 500 meters
// 
// private GPSPoint[] gpspoints;
// 
// public ShowProfile() {
// 
// String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
// GPSComputer gpscomputer =  new GPSComputer(filename);
// 
// gpspoints = gpscomputer.getGPSPoints();
// 
// }
// 
// public static void main(String[] args) {
// launch(args);
// }
// 
// public void run() {
// 
// int N = gpspoints.length; // number of data points
// 
// makeWindow("Height profile", 2 * MARGIN + 3 * N, 2 * MARGIN + MAXBARHEIGHT);
// 
// // top margin + height of drawing area
// showHeightProfile(MARGIN + MAXBARHEIGHT);
// }
// 
// public void showHeightProfile(int ybase) {
// 
// // ybase indicates the position on the y-axis where the columns should start
// int x = MARGIN,y;
// 
// // TODO - START
// 
// System.out.println("Angi tidsskalering i tegnevinduet ...");
// int timescaling = Integer.parseInt(getText("Tidsskalering"));
// 
// setColor(0, 0, 255);
// 
// for (int i = 0; i < gpspoints.length; i++) {
// 
// // HUSK: kun positive høyder skal tegnes
// y = ybase - (int) Math.ceil(Double.max(gpspoints[i].getElevation(), 0.0));
// 
// drawLine(x, ybase, x, y);
// 
// // UTVIDELSE: Pause mellom punkter
// if (i + 1 < gpspoints.length) {
// pause((gpspoints[i + 1].getTime() - gpspoints[i].getTime()) * 1000 / timescaling);
// }
// 
// // UTVIDELSE: tegn kun topp av søjler og forbind via punkter
// 
// x = x + 2; // neste linje to pixels fremme
// 
// }
// 
// // TODO - SLUTT
// }
// 
// }
