package org.wso2.carbon.apimgt.ctl.artifact.converter;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APIInfoVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APIJsonConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APISequencesConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.CertificateVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.DocumentVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class APIJsonConversionManager {
    private List<ResourceVersionConverter> converters = new ArrayList<>();
    private String srcVersion;
    private String targetVersion;
    private String srcPath;
    private String targetPath;
    private String format;
    private JsonObject params;


    public APIJsonConversionManager(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                    String format, JsonObject params) throws CTLArtifactConversionException {
        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.format = format;
        this.params = params;
        init();
    }

    private void init() throws CTLArtifactConversionException {
        if (CommonUtil.validateSrcAndTargetVersions(srcVersion, targetVersion)) {
            converters.add(new APIJsonConverter(srcVersion, targetVersion, srcPath, targetPath, false,
                    params, format));
        } else {
            String msg = "Invalid source or target version";
            throw new CTLArtifactConversionException(msg);
        }

    }
    public boolean convert() throws CTLArtifactConversionException {
        for (ResourceVersionConverter converter : converters) {
            converter.convert();
        }
        return true;
    }
}
