package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;

public abstract class AbstractFeature implements IFeature {
    
    protected Application app;
    protected final String name;
    
    public AbstractFeature(String name) {
        this.name = name;
    }
    
    @Override
    public final void setup(Application app) {
        this.app = app;
        doSetup();
    }
    
    protected abstract void doSetup();
    
    @Override
    public String getName() {
        return name;
    }
}
