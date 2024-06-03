package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;

import java.io.File;

public class V42APIJson extends APIJson {
    JsonArray deploymentEnvironments = new JsonArray();
    @Override
    public void importAPIInfo(String srcPath) throws CTLArtifactConversionException {
        //not implemented yet
    }

    @Override
    public void exportAPIInfo(String srcPath, String targetPath, Boolean isAPIProduct, String exportFormat) throws CTLArtifactConversionException {
        JsonObject apiInfo = getApiInfo();
        if (apiInfo != null && !apiInfo.isEmpty()) {
            if (!isAPIProduct) {
                ConfigFileUtil.writeV42APIConfigFile(targetPath, exportFormat, apiInfo, true);
                ConfigFileUtil.deleteConfigFile(targetPath + File.separator + Constants.META_INFO_DIRECTORY,
                        Constants.API_CONFIG);
            } else {
                ConfigFileUtil.writeV42APIProductConfigFile(targetPath, exportFormat, apiInfo, true);
                ConfigFileUtil.deleteConfigFile(targetPath + File.separator + Constants.META_INFO_DIRECTORY,
                        Constants.API_PRODUCT_CONFIG);
            }

            if (Constants.GRAPHQL.equals(apiInfo.get("type").getAsString())) {
                ConfigFileUtil.writeV42GraphQLSchemaFile(srcPath, targetPath);
            } else {
                ConfigFileUtil.writeV42SwaggerFile(srcPath, targetPath, exportFormat);
            }
            ConfigFileUtil.deleteConfigFile(targetPath + File.separator + Constants.META_INFO_DIRECTORY,
                    Constants.SWAGGER);
            if (deploymentEnvironments != null && deploymentEnvironments.asList().size() > 0) {
                ConfigFileUtil.writeV42DeploymentEnvironmentsFile(deploymentEnvironments, targetPath, exportFormat);
            }
        }
    }

    public void setDeploymentEnvironments(JsonArray deploymentEnvironments) {
        this.deploymentEnvironments = deploymentEnvironments;
    }

    public JsonArray getDeploymentEnvironments() {
        return deploymentEnvironments;
    }
}
