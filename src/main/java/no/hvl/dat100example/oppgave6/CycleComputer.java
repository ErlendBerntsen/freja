// package no.hvl.dat100example.oppgave6;
// 
// import javax.swing.JOptionPane;
// 
// import easygraphics.*;
// import no.hvl.dat100example.oppgave1.GPSPoint;
// import no.hvl.dat100example.oppgave2.GPSData;
// import no.hvl.dat100example.oppgave2.GPSDataFileReader;
// import no.hvl.dat100example.oppgave3.GPSUtils;
// import no.hvl.dat100example.oppgave4.GPSComputer;
// 
// public class CycleComputer extends EasyGraphics {
// 
// private static int SPACE = 10;
// private static int MARGIN = 20;
// 
// // FIXME: take into account number of measurements / gps points
// private static int ROUTEMAPXSIZE = 800;
// private static int ROUTEMAPYSIZE = 400;
// private static int HEIGHTSIZE = 200;
// private static int TEXTWIDTH = 200;
// 
// private GPSComputer gpscomp;
// private GPSPoint[] gpspoints;
// 
// private int N = 0;
// 
// private double minlon, minlat, maxlon, maxlat;
// 
// private double xstep, ystep;
// 
// public CycleComputer() {
// 
// String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
// 
// gpscomp = new GPSComputer(filename);
// gpspoints = gpscomp.getGPSPoints();
// 
// }
// 
// public static void main(String[] args) {
// launch(args);
// }
// 
// public void run() {
// 
// N = gpspoints.length; // number of gps points
// 
// minlon = GPSUtils.findMin(GPSUtils.getLongitudes(gpspoints));
// minlat = GPSUtils.findMin(GPSUtils.getLatitudes(gpspoints));
// 
// maxlon = GPSUtils.findMax(GPSUtils.getLongitudes(gpspoints));
// maxlat = GPSUtils.findMax(GPSUtils.getLatitudes(gpspoints));
// 
// xstep = xstep();
// ystep = ystep();
// 
// makeWindow("Cycle Computer",
// 2 * MARGIN + ROUTEMAPXSIZE,
// 2 * MARGIN + ROUTEMAPYSIZE + HEIGHTSIZE + SPACE);
// 
// bikeRoute();
// 
// }
// 
// 
// public void bikeRoute() {
// 
// setFont("Courier",14);
// 
// int timescaling = Integer.parseInt(getText("Tidsskalering"));
// 
// currenttime = drawString("00:00:00",MARGIN,MARGIN);
// currentspeed = drawString("0", MARGIN, MARGIN+20);
// 
// for (int i = 0; i < gpspoints.length; i++) {
// 
// showCurrent(i);
// 
// showHeight(HEIGHTSIZE + SPACE, i);
// 
// showPosition(i);
// 
// if (i + 1 < gpspoints.length) {
// pause((gpspoints[i + 1].getTime() - gpspoints[i].getTime()) * 1000 / timescaling);
// }
// 
// }
// }
// 
// public double xstep() {
// 
// double xstep = ROUTEMAPXSIZE / (Math.abs(maxlon - minlon));
// 
// return xstep;
// }
// 
// public double ystep() {
// 
// double ystep = ROUTEMAPYSIZE / (Math.abs(maxlat - minlat));
// 
// return ystep;
// }
// 
// private int currentspeed;
// private int currenttime;
// 
// public void showCurrent(int i) {
// setColor(0, 0, 0);
// 
// if (i > 0) {
// 
// // disable previous text
// setVisible(currenttime,false);
// setVisible(currentspeed,false);
// int secs = gpspoints[i].getTime()-gpspoints[0].getTime();
// 
// double speed = GPSUtils.speed(gpspoints[i - 1], gpspoints[i]);
// currentspeed = drawString("Speed: " + GPSUtils.formatDouble(speed) + " km/t", MARGIN, HEIGHTSIZE);
// currenttime = drawString("Time:     " + GPSUtils.formatTime(secs),MARGIN, HEIGHTSIZE-20);
// }
// }
// 
// public void showHeight(int ybase, int i) {
// setColor(0, 0, 255);
// int x1, y1, x2, y2;
// 
// x1 = i * 2 + MARGIN + TEXTWIDTH;
// x2 = x1; // vertical line
// 
// y1 = ybase;
// 
// y2 = ybase - (int) Math.ceil(Double.max(gpspoints[i].getElevation(), 0.0));
// 
// drawLine(x1, y1, x2, y2);
// }
// 
// public void showPosition(int i) {
// 
// int x = MARGIN + (int) ((gpspoints[i].getLongitude() - minlon) * xstep); // TODO:ceil?
// 
// int ybase = HEIGHTSIZE + ROUTEMAPYSIZE + 2 * SPACE;
// int y = ybase - (int) ((gpspoints[i].getLatitude() - minlat) * ystep);
// 
// setColor(0, 0, 255);
// 
// // determine colour of point
// if (i > 0) {
// double diff = gpspoints[i].getElevation() - gpspoints[i - 1].getElevation();
// 
// if (diff > 0) {
// setColor(255, 0, 0); // up
// } else if (diff < 0) {
// setColor(0, 255, 0); // down
// }
// 
// }
// fillCircle(x, y, 3);
// }
// }
