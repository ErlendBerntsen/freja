// package no.hvl.dat100example.oppgave5;
// 
// import javax.swing.JOptionPane;
// 
// import easygraphics.EasyGraphics;
// import no.hvl.dat100example.oppgave1.GPSPoint;
// import no.hvl.dat100example.oppgave2.GPSData;
// import no.hvl.dat100example.oppgave2.GPSDataFileReader;
// import no.hvl.dat100example.oppgave3.GPSUtils;
// import no.hvl.dat100example.oppgave4.GPSComputer;
// 
// public class ShowSpeed extends EasyGraphics {
// 
// private static int MARGIN = 50;
// private static int BARHEIGHT = 200; // assume no speed above 200 km/t
// 
// private GPSComputer gpscomputer;
// private GPSPoint[] gpspoints;
// 
// public ShowSpeed() {
// 
// String filename = JOptionPane.showInputDialog("GPS data filnavn: ");
// gpscomputer = new GPSComputer(filename);
// 
// gpspoints = gpscomputer.getGPSPoints();
// 
// }
// 
// // read in the files and draw into using EasyGraphics
// public static void main(String[] args) {
// launch(args);
// }
// 
// public void run() {
// 
// makeWindow("Speed profile",
// 2 * MARGIN +
// 2 * gpscomputer.speeds().length, 2 * MARGIN + BARHEIGHT);
// 
// showSpeedProfile(MARGIN + BARHEIGHT);
// }
// 
// public void showSpeedProfile(int ybase) {
// 
// System.out.println("Angi tidsskalering i tegnevinduet ...");
// int timescaling = Integer.parseInt(getText("Tidsskalering"));
// 
// double[] speeds = gpscomputer.speeds();
// int x = MARGIN,y;
// 
// // TODO - START
// 
// // UTVIDELSE: angi gjennomsnitshastighet i vinduet
// double avgspeed = gpscomputer.averageSpeed();
// 
// setColor(0,255,0); // green
// 
// // tegn gjennomsnitslinje i grønn
// int N = speeds.length;
// 
// drawLine(MARGIN,ybase - (int)avgspeed, MARGIN + 2 * N,ybase - (int)avgspeed);
// 
// setColor(0, 0, 255); // blue
// 
// for (int i = 0; i<speeds.length; i++) {
// 
// y = ybase - (int)(speeds[i]);
// 
// drawLine(x,ybase,x,y);
// 
// // UTVIDELSE: pause mellom punkter
// if (i+1 < gpspoints.length) {
// pause ((gpspoints[i+1].getTime()-gpspoints[i].getTime())*(1000/timescaling));
// }
// 
// x = x+2;
// }
// 
// // TODO - SLUTT
// }
// }
