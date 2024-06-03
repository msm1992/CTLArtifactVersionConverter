package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.ResourceVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.factory.ResourceFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.APIInfoMappingUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.APIJsonMappingUtil;

public class APIJsonConverter extends ResourceVersionConverter {
    APIJson srcAPIInfo;
    APIJson targetAPIInfo;
    boolean isAPIProduct = false;
    JsonObject params;

    public APIJsonConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                            boolean isAPIProduct, JsonObject params, String exportFormat) {
        super(srcVersion, targetVersion, srcPath, targetPath, exportFormat);
        this.srcAPIInfo = ResourceFactory.getAPIJsonRepresentation(srcVersion);
        this.targetAPIInfo = ResourceFactory.getAPIJsonRepresentation(targetVersion);
        this.isAPIProduct = isAPIProduct;
        this.params = params;
    }
    @Override
    public void convert() throws CTLArtifactConversionException {

        srcAPIInfo.importAPIInfo(srcPath);

        //Map imported API info to target API Info format
        if (!isAPIProduct) {
            APIJsonMappingUtil.mapAPIJson(srcAPIInfo, targetAPIInfo, srcVersion, targetVersion, srcPath, params);
        } else {
//            APIJsonMappingUtil.mapAPIProductInfo(srcAPIInfo, targetAPIInfo, srcVersion, targetVersion, srcPath, params);
        }

        //Export mapped certificates to target artifact
        targetAPIInfo.exportAPIInfo(srcPath, targetPath, isAPIProduct, exportFormat);
    }
}
