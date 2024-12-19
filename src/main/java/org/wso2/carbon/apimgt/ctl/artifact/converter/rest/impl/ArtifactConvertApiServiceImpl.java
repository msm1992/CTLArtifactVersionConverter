package org.wso2.carbon.apimgt.ctl.artifact.converter.rest.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.wso2.carbon.apimgt.ctl.artifact.converter.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.ApiJsonConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;
import org.wso2.carbon.apimgt.rest.api.common.RestApiConstants;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import javax.ws.rs.core.Response;

public class ArtifactConvertApiServiceImpl implements ArtifactConvertApiService {
    private static final Log log = LogFactory.getLog(ArtifactConvertApiServiceImpl.class);

    public Response convertCTLArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion,
                                       String targetVersion, String exportFormat, String type, InputStream paramsInputStream, Attachment paramsDetail,
                                       MessageContext messageContext) {
        try {
            log.info("Migrating API artifact from version " + srcVersion + " to " + targetVersion);
            //Transfer input stream to APIArchive.zip inside tmp folder
            File tempDirectory = CommonUtil.createTempDirectory();
            String absolutePathToTempDirectory = tempDirectory.getAbsolutePath() + File.separator;
            CommonUtil.transferFile(fileInputStream, Constants.UPLOAD_FILE_NAME, absolutePathToTempDirectory);

            //Extract APIArchive.zip
            File apiArchive = new File(absolutePathToTempDirectory + Constants.UPLOAD_FILE_NAME);
            String apiArtifactName = CommonUtil.extractArchive(apiArchive, absolutePathToTempDirectory);

            //Copy extracted API artifact to a new location in tmp folder
            String srcArtifactPath = absolutePathToTempDirectory + apiArtifactName;
            File srcAPIArtifactFile = new File(srcArtifactPath);
            File tempTargetDirectory = CommonUtil.createTempDirectory();
            String targetArtifactPath = tempTargetDirectory.getAbsolutePath() + File.separator + apiArtifactName;
            File copyAPIArtifactFile = new File(targetArtifactPath);
            FileUtils.copyDirectory(srcAPIArtifactFile, copyAPIArtifactFile);

            JsonObject paramsJson = readParamsYaml(paramsInputStream, paramsDetail);

            if (Constants.API_PRIDUCT_TYPE.equals(type)) {
                APIProductArtifactConversionManager productConversionManager = new APIProductArtifactConversionManager(
                        srcVersion, targetVersion, srcArtifactPath, targetArtifactPath, exportFormat, paramsJson);
                productConversionManager.convert();
            } else {
                APIArtifactConversionManager apiConversionManager = new APIArtifactConversionManager(srcVersion,
                        targetVersion, srcArtifactPath, targetArtifactPath, exportFormat, paramsJson);
                apiConversionManager.convert();
            }

            CommonUtil.archiveDirectory(tempTargetDirectory.getAbsolutePath());
            FileUtils.deleteQuietly(new File(tempTargetDirectory.getAbsolutePath()));
            File file = new File(tempTargetDirectory.getAbsolutePath() + Constants.ZIP_EXTENSION);
            return Response.ok(file).header(RestApiConstants.HEADER_CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getName() + "\"").build();
        } catch (CTLArtifactConversionException | IOException e) {
            log.error("Error occurred while converting the artifact", e);
            return Response.serverError().entity("Error occurred while converting the artifact").build();
        }
    }

    @Override
    public Response convertRESTApiArtifact(InputStream fileInputStream, String srcVersion, String targetVersion,
           String type, InputStream paramsInputStream) {

        try {
            log.info("Migrating api.json from version " + srcVersion + " to " + targetVersion);
            //Transfer input stream to APIArchive.zip inside tmp folder
            File tempDirectory = CommonUtil.createTempDirectory();
            String absolutePathToTempDirectory = tempDirectory.getAbsolutePath() + File.separator;
            CommonUtil.transferFile(fileInputStream, Constants.UPLOAD_API_JSON, absolutePathToTempDirectory);


            //Copy API artifact to a new location in tmp folder
            String srcArtifactPath = absolutePathToTempDirectory + Constants.UPLOAD_API_JSON;
            File srcAPIArtifactFile = new File(srcArtifactPath);
            File tempTargetDirectory = CommonUtil.createTempDirectory();
            String targetArtifactPath = tempTargetDirectory.getAbsolutePath() + File.separator + Constants.UPLOAD_API_JSON;
            File targetAPIArtifactFile = new File(targetArtifactPath);

            JsonObject paramsJson = readParamsFromStream(paramsInputStream);

            ApiJsonConverter apiJsonConverter = new ApiJsonConverter(srcVersion, targetVersion, srcArtifactPath,
                    targetArtifactPath, paramsJson, Constants.API_PRIDUCT_TYPE.equals(type));
            apiJsonConverter.convert();

            return Response.ok(targetAPIArtifactFile).header(RestApiConstants.HEADER_CONTENT_DISPOSITION,
                    "attachment; filename=\"" + targetAPIArtifactFile.getName() + "\"").build();
        } catch (CTLArtifactConversionException e) {
            log.error("Error occurred while converting the artifact", e);
            return Response.serverError().entity("Error occurred while converting the artifact").build();
        } catch (IOException e) {
            log.error("Error occurred while processing the json file", e);
            return Response.serverError().entity("Error occurred while processing the json file").build();
        } catch (ParseException e) {
            return Response.serverError().entity("Error occurred while processing the lastUpdatedTime values").build();
        }

    }

    private JsonObject readParamsFromStream (InputStream paramsInputStream) throws
            CTLArtifactConversionException {
        JsonObject paramsJson = new JsonObject();
        try {
            if (paramsInputStream != null) {
                byte[] paramsBytes = IOUtils.toByteArray(paramsInputStream);
                String paramsString = new String(paramsBytes, StandardCharsets.UTF_8);

                if (!StringUtils.isEmpty(paramsString)) {
                    paramsJson = new Gson().fromJson(paramsString, JsonObject.class);
                }
            }
        } catch (IOException e) {
            throw new CTLArtifactConversionException("Error occurred while reading the params file", e);
        }
        return paramsJson;
    }

    private JsonObject readParamsYaml (InputStream paramsInputStream, Attachment paramsDetail) throws
            CTLArtifactConversionException {
        JsonObject paramsJson = new JsonObject();
        try {
            if (paramsInputStream != null && paramsDetail != null) {
                ContentDisposition contentDisposition = paramsDetail.getContentDisposition();
                String fileName = contentDisposition.getParameter(RestApiConstants.CONTENT_DISPOSITION_FILENAME);

                byte[] paramsBytes = IOUtils.toByteArray(paramsInputStream);
                String paramsString = new String(paramsBytes, StandardCharsets.UTF_8);

                if (!StringUtils.isEmpty(fileName) && fileName.endsWith(Constants.JSON_EXTENSION)) {
                    if (!StringUtils.isEmpty(paramsString)) {
                        paramsJson = new Gson().fromJson(paramsString, JsonObject.class);
                    }
                } else if (!StringUtils.isEmpty(fileName) && fileName.endsWith(Constants.YAML_EXTENSION)) {
                    Yaml yaml = new Yaml();
                    Object yamlObject = yaml.load(paramsString);
                    String jsonString = new Gson().toJson(yamlObject);
                    paramsJson = new Gson().fromJson(jsonString, JsonObject.class);
                }
            }
        } catch (IOException e) {
            throw new CTLArtifactConversionException("Error occurred while reading the params file", e);
        }
        return paramsJson;
    }
}
