package org.wso2.carbon.apimgt.ctl.artifact.converter.model;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

public abstract class APIJson {
    private JsonObject apiInfo;
    public abstract void importAPIInfo(String srcPath) throws CTLArtifactConversionException;
    public abstract void exportAPIInfo(String srcPath, String targetPath, Boolean isAPIProduct, String exportFormat) throws CTLArtifactConversionException;

    public JsonObject getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(JsonObject apiInfo) {
        this.apiInfo = apiInfo;
    }
}
