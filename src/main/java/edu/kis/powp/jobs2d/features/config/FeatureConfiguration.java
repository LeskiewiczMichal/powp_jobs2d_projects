package edu.kis.powp.jobs2d.features.config;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.features.FeatureManager;
import edu.kis.powp.jobs2d.features.IFeature;
import java.util.ArrayList;
import java.util.List;

public class FeatureConfiguration {
    
    private final List<IFeature> features = new ArrayList<>();
    
    public FeatureConfiguration addFeature(IFeature feature) {
        features.add(feature);
        return this;
    }
    
    public void applyConfiguration(Application app) {
        FeatureManager manager = new FeatureManager();
        manager.setApplication(app);
        for (IFeature feature : features) {
            manager.registerFeature(feature);
        }
        manager.setupAll();
    }
}
