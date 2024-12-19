package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ApiJsonConverterUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiJsonConverter {

    String srcVersion;
    String targetVersion;
    String srcPath;
    String targetPath;
    JsonObject paramsJson;
    ObjectMapper objectMapper;
    File sourceFile;
    boolean isApiProduct;

    public ApiJsonConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                            JsonObject paramsJson, boolean isApiProduct) throws IOException {

        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.paramsJson = paramsJson;
        this.isApiProduct = isApiProduct;

        objectMapper = new ObjectMapper();
        sourceFile = new File(srcPath);

    }

    public void convert() throws IOException, CTLArtifactConversionException, ParseException {
        JsonObject resultingJson = null;
        Map sourceApiMap = objectMapper.readValue(sourceFile, new TypeReference<LinkedHashMap>(){});
        if (srcVersion.equals(Constants.V320)) {
            if (targetVersion.equals(Constants.V420)) {
                resultingJson = ApiJsonConverterUtil.convertFromV32To42(sourceApiMap, paramsJson, isApiProduct);
            } else {
                throw new CTLArtifactConversionException("Unsupported target version");
            }
        } else {
            throw new CTLArtifactConversionException("Unsupported source version");
        }
        // TODO : Other version conversions

        // write to target
        CommonUtil.writeFile(targetPath, String.valueOf(resultingJson));

    }

}
