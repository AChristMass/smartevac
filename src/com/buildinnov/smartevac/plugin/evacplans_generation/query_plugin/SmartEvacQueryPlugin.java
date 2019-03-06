package com.buildinnov.smartevac.plugin.evacplans_generation.query_plugin;

import org.bimserver.models.store.ObjectDefinition;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.PluginContext;
import org.bimserver.plugins.queryengine.QueryEngine;
import org.bimserver.plugins.queryengine.QueryEnginePlugin;
import org.bimserver.shared.exceptions.PluginException;

import java.util.*;

public class SmartEvacQueryPlugin implements QueryEnginePlugin {

    private final Map<String, String> examples = new LinkedHashMap();

    @Override
    public void init(PluginContext pluginContext) throws PluginException {
        System.out.println("initialising SmartEvacQueryPlugin");
        this.examples.put("Return something", "Select $Var1");
        this.examples.put("Return all rooted entities of type IfcDoor", "Select $Var1\nWhere $Var1.EntityType = IfcDoor");

    }
    public QueryEngine getQueryEngine(PluginConfiguration pluginConfiguration) {
        return new SmartEvacQueryEngine();
    }

    public Collection<String> getExampleKeys() {
        return this.examples.keySet();
    }

    public String getExample(String key) {
        return (String)this.examples.get(key);
    }

    public ObjectDefinition getSettingsDefinition() {
        return null;
    }
}
