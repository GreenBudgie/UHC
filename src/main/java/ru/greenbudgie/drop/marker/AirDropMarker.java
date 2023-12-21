package ru.greenbudgie.drop.marker;

import ru.greenbudgie.drop.AirDrop;

public class AirDropMarker extends DropMarker<AirDrop> {

    public AirDropMarker(AirDrop drop) {
        super(drop);
    }

    @Override
    public void onDrop() {
        remove();
    }
}
