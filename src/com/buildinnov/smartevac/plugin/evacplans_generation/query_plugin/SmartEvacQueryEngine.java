package com.buildinnov.smartevac.plugin.evacplans_generation.query_plugin;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.plugins.ModelHelper;
import org.bimserver.plugins.Reporter;
import org.bimserver.plugins.queryengine.QueryEngine;
import org.bimserver.plugins.queryengine.QueryEngineException;

public class SmartEvacQueryEngine implements QueryEngine {
    @Override
    public IfcModelInterface query(IfcModelInterface model, String code, Reporter reporter, ModelHelper modelHelper) throws QueryEngineException {
        return null;
    }
}
