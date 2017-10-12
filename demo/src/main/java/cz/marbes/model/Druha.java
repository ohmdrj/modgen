package cz.marbes.model;

import cz.test.Modgen;

@Modgen
public abstract class Druha {

    public abstract Integer getId();

    @Modgen(name = "Nadrazena")
    public abstract Druha getParent();

    @Modgen(name = "CamelCase")
    public abstract String getClamelCase();

}
