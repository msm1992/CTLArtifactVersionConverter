package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ConfigFileUtil {
    private static final Log log = LogFactory.getLog(ConfigFileUtil.class);
    public static List readV32ConfigFileToList(String pathToConfigDirectory, String configFileName) throws CTLArtifactConversionException {
        List data = new ArrayList<>();
        File yamlConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.YAML_EXTENSION);
        File jsonConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.JSON_EXTENSION);
        try {
            if (yamlConfig.exists()) {
                InputStream inputStream = new FileInputStream(yamlConfig);
                Yaml yaml = new Yaml();
                data = yaml.load(inputStream);
            } else if (jsonConfig.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(jsonConfig, new TypeReference<List<LinkedHashMap>>(){});
            } else {
                log.info("No " + configFileName + " config file not found at " + pathToConfigDirectory);
            }
        } catch (FileNotFoundException e) {
            String msg = configFileName + " config file not found at " + pathToConfigDirectory;
            throw new CTLArtifactConversionException(msg, e);
        } catch (IOException e) {
            String msg = "Error while reading " + configFileName + ".json config file";
            throw new CTLArtifactConversionException(msg, e);
        }
        return data;
    }

    public static Map readConfigFileToMap(String pathToConfigDirectory, String configFileName) throws CTLArtifactConversionException {
        Map data = new LinkedHashMap();
        File yamlConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.YAML_EXTENSION);
        File jsonConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.JSON_EXTENSION);
        try {
            if (yamlConfig.exists()) {
                InputStream inputStream = new FileInputStream(yamlConfig);
                Yaml yaml = new Yaml();
                data = yaml.load(inputStream);
            } else if (jsonConfig.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(jsonConfig, new TypeReference<LinkedHashMap>(){});
            } else {
                //todo error: no config file found
            }
        } catch (FileNotFoundException e) {
            String msg = configFileName + " config file not found";
            throw new CTLArtifactConversionException(msg, e);
        } catch (IOException e) {
            String msg = "Error while reading " + configFileName + ".json config file";
            throw new CTLArtifactConversionException(msg, e);
        }
        return data;
    }

    public static Map readAPIJsonToMap(String pathToConfigDirectory, String configFileName) throws CTLArtifactConversionException {
        Map data = new LinkedHashMap();
        File yamlConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.YAML_EXTENSION);
        File jsonConfig = new File(pathToConfigDirectory + File.separator + configFileName + Constants.JSON_EXTENSION);
        try {
            if (yamlConfig.exists()) {
                InputStream inputStream = new FileInputStream(yamlConfig);
                Yaml yaml = new Yaml();
                data = yaml.load(inputStream);
            } else if (jsonConfig.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(jsonConfig, new TypeReference<LinkedHashMap>(){});
            } else {
                //todo error: no config file found
            }
        } catch (FileNotFoundException e) {
            String msg = configFileName + " config file not found";
            throw new CTLArtifactConversionException(msg, e);
        } catch (IOException e) {
            String msg = "Error while reading " + configFileName + ".json config file";
            throw new CTLArtifactConversionException(msg, e);
        }
        return data;
    }

    public static void writeV42DTOFile(String filePath, String format, String type, JsonElement dtoObj)
            throws CTLArtifactConversionException {
        JsonObject config = addTypeAndVersionToFile(type, Constants.APIM_420_VERSION, dtoObj);
        if (Constants.YAML_FORMAT.equals(format)) {
            writeYamlConfigFile(filePath + Constants.YAML_EXTENSION, config);
        } else if (Constants.JSON_FORMAT.equals(format)) {
            writeJsonConfigFile(filePath + Constants.JSON_EXTENSION, config);
        }
    }

    public static void writeV42DocumentContentFile(String srcDocDirectoryPath, String targetDocDirectoryPath,
                                                 JsonObject document) throws CTLArtifactConversionException {
        String documentType = document.get("sourceType").getAsString();
        if (documentType.equals(Constants.URL_DOC_TYPE)) {
            return;
        }

        String docName = document.get("name").getAsString();
        File srcFile = null;
        File targetFile = null;
        try {
            if (documentType.equals(Constants.INLINE_DOC_TYPE) || documentType.equals(Constants.MARKDOWN_DOC_TYPE)) {
                String inlineContentsDirectoryPath = srcDocDirectoryPath + File.separator +
                        Constants.INLINE_CONTENTS_DIRECTORY;
                srcFile = new File(inlineContentsDirectoryPath + File.separator + docName);
                targetFile = new File(targetDocDirectoryPath + File.separator + docName +
                        File.separator + docName);
            } else if (documentType.equals(Constants.FILE_DOC_TYPE)) {
                String fileContentsDirectoryPath = srcDocDirectoryPath + File.separator +
                        Constants.FILE_CONTENTS_DIRECTORY;
                String filepath = CommonUtil.readElementAsString(document, "fileName");
                if (filepath != null) {
                    srcFile = new File(fileContentsDirectoryPath + File.separator + filepath);
                    targetFile = new File(targetDocDirectoryPath + File.separator + docName +
                            File.separator + filepath);
                }
            }

            if (srcFile != null && srcFile.exists() && targetFile != null) {
                FileUtils.copyFile(srcFile, targetFile);
            } else {
                //this is not an error scenario - just the document is not yet associated with any content or file
                String msg = "Document content not found for document: " + docName + " of type: " + documentType;
                log.info(msg);
            }

        } catch (IOException e) {
            String msg = "Error while writing document content file for document: " + docName;
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    public static void writeV42APIConfigFile(String metaInfoDirectory, String format, JsonObject api,
                                             boolean addTypeAndVersion) throws CTLArtifactConversionException {
        JsonObject config = api;
        if (addTypeAndVersion) {
            config = addTypeAndVersionToFile(Constants.API_DTO_TYPE, Constants.APIM_420_VERSION, api);
        }
        if (Constants.YAML_FORMAT.equals(format)) {
            writeYamlConfigFile(metaInfoDirectory + File.separator + Constants.API_YAML, config);
        } else if (Constants.JSON_FORMAT.equals(format)) {
            writeJsonConfigFile(metaInfoDirectory + File.separator + Constants.API_JSON, config);
        }
    }

    public static void writeV42APIProductConfigFile(String metaInfoDirectory, String format, JsonObject api,
                                             boolean addTypeAndVersion) throws CTLArtifactConversionException {
        JsonObject config = api;
        if (addTypeAndVersion) {
            config = addTypeAndVersionToFile(Constants.API_PRODUCT_DTO_TYPE, Constants.APIM_420_VERSION, api);
        }
        if (Constants.YAML_FORMAT.equals(format)) {
            writeYamlConfigFile(metaInfoDirectory + File.separator + Constants.API_PRODUCT_YAML, config);
        } else if (Constants.JSON_FORMAT.equals(format)) {
            writeJsonConfigFile(metaInfoDirectory + File.separator + Constants.API_PRODUCT_JSON, config);
        }
    }

    public static void writeV42DeploymentEnvironmentsFile(JsonArray deploymentEnvironments, String targetPath,
                                                          String exportFormat) throws CTLArtifactConversionException {
        writeV42DTOFile(targetPath + File.separator + Constants.DEPLOYMENT_ENVIRONMENTS_TYPE, exportFormat,
                Constants.DEPLOYMENT_ENVIRONMENTS_TYPE, deploymentEnvironments);
    }

    public static void writeV42GraphQLSchemaFile(String srcPath, String targetPath) throws CTLArtifactConversionException {
         String pathToSDL = srcPath + File.separator + Constants.META_INFO_DIRECTORY + File.separator +
                Constants.GRAPHQL_SCHEMA_FILE_NAME;
         String targetSDLPath = targetPath + File.separator + Constants.DEFINITIONS_DIRECTORY + File.separator +
                Constants.GRAPHQL_SCHEMA_FILE_NAME;
         File srcSDLFile = new File(pathToSDL);
         File targetSDLFile = new File(targetSDLPath);
            try {
                if (srcSDLFile.exists()) {
                    FileUtils.copyFile(srcSDLFile, targetSDLFile);
                } else {
                    log.error("GraphQL schema file not found at " + pathToSDL);
                }
            } catch (IOException e) {
                String msg = "Error while writing GraphQL schema file";
                throw new CTLArtifactConversionException(msg, e);
            }

    }

    public static void writeV42SwaggerFile(String srcPath, String targetPath, String exportFormat) throws
            CTLArtifactConversionException {
        String swaggerJsonPath = srcPath + File.separator + Constants.META_INFO_DIRECTORY + File.separator +
                Constants.SWAGGER_JSON;
        String swaggerYamlPath = srcPath + File.separator + Constants.META_INFO_DIRECTORY + File.separator +
                Constants.SWAGGER_YAML;

        String swaggerFilePath;
        if (new File(swaggerYamlPath).exists()) {
            swaggerFilePath = swaggerYamlPath;
        } else {
            swaggerFilePath = swaggerJsonPath;
        }

        String definitionsDirectory = targetPath + File.separator + Constants.DEFINITIONS_DIRECTORY;
        String v42SwaggerFilePath;
        if (Constants.JSON_FORMAT.equals(exportFormat)) {
            v42SwaggerFilePath = definitionsDirectory + File.separator + Constants.SWAGGER + Constants.JSON_EXTENSION;
        } else {
            v42SwaggerFilePath = definitionsDirectory + File.separator + Constants.SWAGGER + Constants.YAML_EXTENSION;
        }
        CommonUtil.cleanDirectory(definitionsDirectory, true);
        try {
            Files.copy(new File(swaggerFilePath).toPath(), new File(v42SwaggerFilePath).toPath());
        } catch (IOException e) {
            String msg = "Error while copying swagger file from " + swaggerFilePath + " to " + v42SwaggerFilePath;
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    public static void deleteConfigFile(String filePath, String filename) throws CTLArtifactConversionException {
        try {
            String yamlPath = filePath + File.separator + filename + Constants.YAML_EXTENSION;
            String jsonPath = filePath + File.separator + filename + Constants.JSON_EXTENSION;

            File yamlFile = new File(yamlPath);
            File jsonFile = new File(jsonPath);

            Files.deleteIfExists(yamlFile.toPath());
            Files.deleteIfExists(jsonFile.toPath());
        } catch (IOException e) {
            String msg = "Error while deleting file: " + filePath;
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    public static String getSwaggerFilePath(String pathToMetaInfo) {
        File yamlFile = new File(pathToMetaInfo + File.separator + Constants.SWAGGER_YAML);
        File jsonFile = new File(pathToMetaInfo + File.separator + Constants.SWAGGER_JSON);

        if (yamlFile.exists()) {
            return yamlFile.getAbsolutePath();
        } else {
            return jsonFile.getAbsolutePath();
        }
    }

    /**
     * Add the type and the version to the artifact file when exporting.
     *
     * @param type        Type of the artifact to be exported
     * @param version     API Manager version
     * @param element      JSON element to be added as data
     * @return The artifact object with the type and version added to it
     */
    public static JsonObject addTypeAndVersionToFile(String type, String version, JsonElement element) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.TYPE, type);
        jsonObject.addProperty(Constants.API_DATA_VERSION, version);
        jsonObject.add(Constants.DATA, element);
        return jsonObject;
    }

    public static void writeJsonConfigFile(String filePath, JsonObject config) throws CTLArtifactConversionException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(config);
        CommonUtil.writeFile(filePath, jsonString);
    }

    public static void writeYamlConfigFile(String filePath, JsonObject config) throws CTLArtifactConversionException {
        try {
            String yamlString = jsonToYaml(config.toString());
            CommonUtil.writeFile(filePath, yamlString);
        } catch (IOException e) {
            String msg = "Error while writing config file at " + filePath;
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    /**
     * Converts JSON to YAML.
     *
     * @param json json representation
     * @return json file as a yaml document
     * @throws IOException If an error occurs while converting JSON to YAML
     */
    public static String jsonToYaml(String json) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        // Convert JsonObject to YAML string
        Yaml yaml = new Yaml(options);
        String yamlString = yaml.dump(new Gson().fromJson(json, Map.class));
        return yamlString;
    }

    public static String readFileContentAsString(String filePath) throws CTLArtifactConversionException {
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                content.append(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
            } else {
                String msg = "File not found at " + filePath;
                throw new CTLArtifactConversionException(msg);
            }
        } catch (IOException e) {
            String msg = "Error while reading file at " + filePath;
            throw new CTLArtifactConversionException(msg, e);
        }
        return content.toString();
    }
}
