package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;

import java.io.File;
import java.util.Map;

public class V32APIJson extends APIJson {
    private static final Log log = LogFactory.getLog(V32Certificates.class);
    @Override
    public void importAPIInfo(String srcPath) throws CTLArtifactConversionException {

        Map apiMap = ConfigFileUtil.readAPIJsonToMap(srcPath, Constants.API_CONFIG);

        Gson gson = new Gson();
        JsonObject apiJsonObject = gson.fromJson(gson.toJson(apiMap), JsonObject.class);
        setApiInfo(apiJsonObject);
    }

    @Override
    public void exportAPIInfo(String srcPath, String targetPath, Boolean isAPIProduct, String exportFormat) throws CTLArtifactConversionException {
        //not implemented yet
    }
}
