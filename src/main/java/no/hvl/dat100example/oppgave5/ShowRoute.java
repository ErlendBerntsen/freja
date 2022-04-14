// package no.hvl.dat100example.oppgave5;
// 
// import javax.swing.JOptionPane;
// 
// import easygraphics.EasyGraphics;
// import no.hvl.dat100example.oppgave1.GPSPoint;
// import no.hvl.dat100example.oppgave3.GPSUtils;
// import no.hvl.dat100example.oppgave4.GPSComputer;
// 
// public class ShowRoute extends EasyGraphics {
// 
// private static int MARGIN = 50;
// private static int MAPXSIZE = 800;
// private static int MAPYSIZE = 800;
// 
// private GPSPoint[] gpspoints;
// private GPSComputer gpscomputer;
// 
// public ShowRoute() {
// 
// String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
// gpscomputer = new GPSComputer(filename);
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
// makeWindow("Route", MAPXSIZE + 2 * MARGIN, MAPYSIZE + 2 * MARGIN);
// 
// showRouteMap(MARGIN + MAPYSIZE);
// 
// playRoute(MARGIN + MAPYSIZE);
// 
// showStatistics();
// }
// 
// // antall x-pixels per lengdegrad
// public double xstep() {
// 
// double maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
// double minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
// 
// double xstep = MAPXSIZE / (Math.abs(maxlon - minlon));
// 
// return xstep;
// }
// 
// // antall y-pixels per breddegrad
// public double ystep() {
// 
// double ystep;
// 
// // TODO - START
// 
// double maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
// double minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
// 
// ystep = MAPYSIZE / (Math.abs(maxlat - minlat));
// 
// // TODO - SLUTT
// 
// return ystep;
// }
// 
// public void showRouteMap(int ybase) {
// 
// // TODO - START
// 
// double xstep = xstep();
// double ystep = ystep();
// 
// double minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
// double minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
// 
// setColor(0, 255, 0); // blue
// 
// int prevx = 0, prevy = 0; // UTVIDELSE: tegner linje mellom punkter
// 
// // draw the map
// for (int i = 0; i < gpspoints.length; i++) {
// 
// int x = MARGIN + (int) ((gpspoints[i].getLongitude() - minlon) * xstep);
// 
// int y = ybase - (int) ((gpspoints[i].getLatitude() - minlat) * ystep);
// 
// fillCircle(x, y, 3);
// 
// if (i > 0) {
// drawLine(prevx, prevy, x, y);
// }
// 
// prevx = x;
// prevy = y;
// }
// 
// // TODO - SLUTT
// }
// 
// public void showStatistics() {
// 
// int TEXTDISTANCE = 20;
// 
// setColor(0,0,0);
// setFont("Courier",12);
// 
// // TODO - START
// 
// drawString("Total Time     : " + GPSUtils.formatTime(gpscomputer.totalTime()), MARGIN, 1 * TEXTDISTANCE);
// drawString("Total distance : " + GPSUtils.formatDouble(gpscomputer.totalDistance() / 1000.0) + " km", MARGIN, 2 * TEXTDISTANCE);
// drawString("Total elevation: " + GPSUtils.formatDouble(gpscomputer.totalElevation()) + " m", MARGIN, 3 * TEXTDISTANCE);
// drawString("Max speed      : " + GPSUtils.formatDouble(gpscomputer.maxSpeed()) + " km/t", MARGIN, 4 * TEXTDISTANCE);
// drawString("Average speed  : " + GPSUtils.formatDouble(gpscomputer.averageSpeed()) + " km/t", MARGIN, 5 * TEXTDISTANCE);
// drawString("Energy         : " + GPSUtils.formatDouble(gpscomputer.totalKcal(80.0)) + " kcal", MARGIN, 6 * TEXTDISTANCE);
// 
// // TODO - SLUTT;
// }
// 
// public void playRoute(int ybase) {
// 
// // TODO - START
// 
// double minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
// double minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
// 
// double xstep = xstep();
// double ystep = ystep();
// 
// setColor(0, 0, 255); // blue;
// 
// // make a circle in the first point
// int x = MARGIN + (int) ((gpspoints[0].getLongitude() - minlon) * xstep);
// int y = ybase - (int) ((gpspoints[0].getLatitude() - minlat) * ystep);
// 
// int movingcircle = fillCircle(x, y, 7);
// 
// // get the speed
// double[] speeds = gpscomputer.speeds();
// double maxspeed = gpscomputer.maxSpeed();
// 
// for (int i = 1; i < gpspoints.length; i++) {
// 
// int nextx = MARGIN + (int) ((gpspoints[i].getLongitude() - minlon) * xstep);
// 
// int nexty = MARGIN + MAPYSIZE - (int) ((gpspoints[i].getLatitude() - minlat) * ystep);
// 
// double speed = speeds[i - 1];
// 
// // easygraphics speed is between 1 and 10.
// int animationspeed = (int) (1 + Math.ceil(((speed / maxspeed) * 5)));
// 
// setSpeed(animationspeed);
// moveCircle(movingcircle, nextx, nexty);
// }
// 
// // TODO - SLUTT
// }
// 
// }
