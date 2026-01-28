package edu.kis.powp.jobs2d.features;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import edu.kis.powp.appbase.Application;

public class FeatureManager {
    
    private final List<IFeature> features = new ArrayList<>();
    private final Map<String, IFeature> featureMap = new HashMap<>();
    private Application app;
    
    public void setApplication(Application app) {
        this.app = app;
    }
    
    public void registerFeature(IFeature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        if (featureMap.containsKey(feature.getName())) {
            throw new IllegalArgumentException("Feature with name '" + feature.getName() + "' already registered");
        }
        features.add(feature);
        featureMap.put(feature.getName(), feature);
    }
    
    public void setupAll() {
        if (app == null) {
            throw new IllegalStateException("Application not set. Call setApplication() first");
        }
        for (IFeature feature : features) {
            feature.setup(app);
        }
    }
    
    public IFeature getFeature(String name) {
        return featureMap.get(name);
    }
    
    public List<IFeature> getAllFeatures() {
        return new ArrayList<>(features);
    }
}