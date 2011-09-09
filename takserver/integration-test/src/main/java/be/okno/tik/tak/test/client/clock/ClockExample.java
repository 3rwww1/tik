package be.okno.tik.tak.test.client.clock;

import be.okno.tik.tak.commons.model.Clock;

public enum ClockExample {
    Esplanade("esplanade", 0D, 0D, 0D),
 //   Constitution("constitution", 0D, 0D, 0D),
 //   Okno("okno", 0D, 0D, 0D),
 //   Argonne("argonne", 0D, 0D, 0D),
 //   SwissWaterPiece("swisswaterpiece", 0D, 0D, 0D),
 //   Palinka("palinka", 0D, 0D, 0D),
 //   NiggerSt("niggerst", 0D, 0D, 0D),
 //  QueensBoulevard("queensboulevard", 0D, 0D, 0D),
 //   NoordWijk("noordwijk", 0D, 0D, 0D),
 //   GoteBorg("goteborg", 0D, 0D, 0D)
    ;
    
    final Clock clock;
    
    ClockExample(String name, Double longitude, Double latitude, Double altitude) {
    	this.clock = new Clock();
    	clock.setName(name);
    	clock.setLongitude(longitude);
    	clock.setLatitude(latitude);
    	clock.setAltitude(altitude);
    }
}
