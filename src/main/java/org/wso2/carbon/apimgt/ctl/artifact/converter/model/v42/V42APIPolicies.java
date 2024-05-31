package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.SequencesMappingUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class V42APIPolicies extends Sequences {
    protected Map<String, JsonObject> sequencesMap = new HashMap<>();
    @Override
    public void importSequences(String srcPath) throws CTLArtifactConversionException {
        String msg = "Method not implemented yet";
        throw new CTLArtifactConversionException(msg);
    }

    @Override
    public void exportSequences(String srcPath, String targetPath, String exportFormat) throws CTLArtifactConversionException {

        String srcMetaInfoDirectory = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        JsonObject v32apiMap = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(srcMetaInfoDirectory, Constants.API_CONFIG));
        JsonObject v42apiMap = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(targetPath, Constants.API_CONFIG));

        SequencesMappingUtil.populateAPIPoliciesInAPI(v32apiMap, v42apiMap, sequencesMap);
        ConfigFileUtil.writeV42APIConfigFile(targetPath, exportFormat, v42apiMap, false);
    }

    public void addAPIPolicy(JsonObject sequence) {
        String key = sequence.get("name").getAsString();
        sequencesMap.put(key, sequence);
    }

    public JsonObject getAPIPolicy(String key) {
        if (sequencesMap.get(key) != null) {
            return sequencesMap.get(key);
        } else {
            return null;
        }
    }

}
