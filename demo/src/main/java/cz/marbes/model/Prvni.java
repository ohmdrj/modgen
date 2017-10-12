package cz.marbes.model;

import cz.test.Modgen;

import java.util.List;

@Modgen
public abstract class Prvni {

    public abstract Integer getId();

    @Modgen(name = "Jmeno", desc = "Silene dlouhy popis entity")
    public abstract String getName();

    @Modgen(name = "Seznam")
    public abstract List<Druha> getDruhe();
}
