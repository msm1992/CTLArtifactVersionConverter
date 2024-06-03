package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIOperationPoliciesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIOperationsDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIProductDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.ProductAPIDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIJsonMappingUtil {
    private static final Log log = LogFactory.getLog(APIJsonMappingUtil.class);
    public static void mapAPIJson(APIJson srcAPIInfo, APIJson targetAPIInfo, String srcVersion,
                                  String targetVersion, String srcPath, JsonObject params) throws CTLArtifactConversionException {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            v32tov42APIJson(srcAPIInfo, targetAPIInfo, Constants.API_TYPE, srcPath, params);
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    public static void mapAPIProductInfo(APIJson srcAPIInfo, APIJson targetAPIInfo, String srcVersion,
                                         String targetVersion, String srcPath, JsonObject params) throws CTLArtifactConversionException {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            v32tov42APIJson(srcAPIInfo, targetAPIInfo, Constants.API_PRIDUCT_TYPE, srcPath, params);
        }
    }

    public static void v32tov42APIJson(APIJson srcAPIInfo, APIJson targetAPIInfo, String type, String srcPath, JsonObject params)
            throws CTLArtifactConversionException {
        JsonObject srcAPIInfoJson = srcAPIInfo.getApiInfo();
        String status;
        if (srcAPIInfoJson != null && !srcAPIInfoJson.isEmpty()) {
            JsonObject targetAPIInfoJson = new JsonObject();
            if (Constants.API_PRIDUCT_TYPE.equals(type)) {
                addV32ToV42ProductMappings(srcAPIInfoJson, targetAPIInfoJson, srcPath);
                status = CommonUtil.readElementAsString(srcAPIInfoJson, "state");
            } else {
                addV32ToV42APIMappings(srcAPIInfoJson, targetAPIInfoJson, srcPath);
                status = CommonUtil.readElementAsString(srcAPIInfoJson, "status");
            }
            targetAPIInfo.setApiInfo(targetAPIInfoJson);

            //add deployment-environments config if 3.2.0 API is in published or prototypes states
            if (Constants.PUBLISHED.equals(status) || Constants.PROTOTYPED.equals(status)) {
                if (params != null && params.has("deploymentEnvironments")) {
                    JsonArray deploymentEnvironmentsFromParams = params.getAsJsonArray("deploymentEnvironments");
                    JsonArray deploymentEnvironments = new JsonArray();
                    if (deploymentEnvironmentsFromParams != null && deploymentEnvironmentsFromParams.size() > 0) {
                        deploymentEnvironmentsFromParams.iterator().forEachRemaining(
                                environment -> {
                                    JsonObject environmentObject = new JsonObject();
                                    JsonObject envObj = environment.getAsJsonObject();
                                    environmentObject.addProperty("deploymentEnvironment", CommonUtil.
                                            readElementAsString(envObj, "name"));
                                    environmentObject.addProperty("displayOnDevportal", CommonUtil.
                                            readElementAsBoolean(envObj, "displayOnDevportal"));
                                    deploymentEnvironments.add(environmentObject);
                                }
                        );
//                        ((V42APIInfo) targetAPIInfo).setDeploymentEnvironments(deploymentEnvironments);
                    }
                } else {
                    log.info("Params file was not provided, hence using the deployment environments specified in the " +
                            "src artifact for " + srcPath);
                    JsonArray v32DeploymentEnvironments = CommonUtil.readElementAsJsonArray(srcAPIInfoJson, "environments");
                    JsonArray deploymentEnvironments = new JsonArray();
                    if (v32DeploymentEnvironments != null && v32DeploymentEnvironments.size() > 0) {
                        v32DeploymentEnvironments.iterator().forEachRemaining(
                                environment -> {
                                    JsonObject environmentObject = new JsonObject();
                                    environmentObject.addProperty("deploymentEnvironment", environment.getAsString());
                                    environmentObject.addProperty("displayOnDevportal", true);
                                    deploymentEnvironments.add(environmentObject);
                                }
                        );
//                        ((V42APIInfo) targetAPIInfo).setDeploymentEnvironments(deploymentEnvironments);
                    }
                }
            }
        }
    }

    /**
     * This method will map the common API and Product properties from 3.2.0 to 4.2.0
     *
     * @param src   3.2.0 API/Product info
     * @param target    4.2.0 API/Product info
     * @param srcPath   path of the 3.2.0 API/Product
     */
    public static void addV32ToV42CommonMappings(JsonObject src, JsonObject target, String srcPath) throws
            CTLArtifactConversionException {
        populateSimilarAPIPropertyAsString(src, target, "name");
        populateSimilarAPIPropertyAsString(src, target, "description");
        populateSimilarAPIPropertyAsString(src, target, "context");
        populateSimilarAPIPropertyAsString(src, target, "version");
        populateSimilarAPIPropertyAsString(src, target, "provider");
        populateSimilarAPIPropertyAsString(src, target, "lifeCycleStatus");
        populateSimilarAPIPropertyAsString(src, target, "wsdlInfo");
        populateSimilarAPIPropertyAsString(src, target, "responseCachingEnabled");
        populateSimilarAPIPropertyAsString(src, target, "cacheTimeout");
        populateSimilarAPIPropertyAsString(src, target, "hasThumbnail");
        populateSimilarAPIPropertyAsString(src, target, "isDefaultVersion");
        populateSimilarAPIPropertyAsString(src, target, "enableSchemaValidation");
        populateSimilarAPIPropertyAsString(src, target, "type");
        populateSimilarAPIPropertyAsString(src, target, "transport");
        populateSimilarAPIPropertyAsString(src, target, "tags");
        populateSimilarAPIPropertyAsString(src, target, "policies");
        populateSimilarAPIPropertyAsString(src, target, "apiThrottlingPolicy");
        populateSimilarAPIPropertyAsString(src, target, "authorizationHeader");
        populateSimilarAPIPropertyAsString(src, target, "securityScheme");
        populateSimilarAPIPropertyAsString(src, target, "maxTps");
        populateSimilarAPIPropertyAsString(src, target, "visibility");
        populateSimilarAPIPropertyAsString(src, target, "visibleRoles");
        populateSimilarAPIPropertyAsString(src, target, "visibleTenants");
        populateSimilarAPIPropertyAsString(src, target, "mediationPolicies");
        populateSimilarAPIPropertyAsString(src, target, "subscriptionAvailability");
        populateSimilarAPIPropertyAsString(src, target, "subscriptionAvailableTenants");
        populateSimilarAPIPropertyAsString(src, target, "monetization");
        populateSimilarAPIPropertyAsString(src, target, "accessControl");
        populateSimilarAPIPropertyAsString(src, target, "accessControlRoles");
        populateSimilarAPIPropertyAsString(src, target, "businessInformation");
        populateSimilarAPIPropertyAsString(src, target, "corsConfiguration");
        populateSimilarAPIPropertyAsString(src, target, "workflowStatus");
        populateSimilarAPIPropertyAsString(src, target, "createdTime");
        populateSimilarAPIPropertyAsString(src, target, "lastUpdatedTime");
        populateSimilarAPIPropertyAsString(src, target, "endpointImplementationType");
        populateSimilarAPIPropertyAsString(src, target, "scopes");
        populateSimilarAPIPropertyAsString(src, target, "operations");
        populateSimilarAPIPropertyAsString(src, target, "categories");
        populateSimilarAPIPropertyAsString(src, target, "subscriptionAvailability");

        target.addProperty("gatewayVendor", Constants.GATEWAY_VENDOR_WSO2);

        // TODO
//        populateVisibilityInfo(src, target);
//        populateAccessControlInfo(src, target);
//        populateBusinessInfo(src, target);
//        populateSubscriptionAvailabilityInfo(src, target);
//        populateCorsConfig(src, target);
//        populateAdditionalProperties(src, target);
//        populateCategoriesAndTags(src, target);
//        populateAPISecurity(src, target);
//        populateThrottlePolicies(src, target);
//        populateTransports(src, target);
//        populateWSDLInfo(srcPath, src, target);
//        populateUriTemplates(srcPath, target);
    }
    public static void addV32ToV42ProductMappings(JsonObject src, JsonObject target, String srcPath) throws
            CTLArtifactConversionException {
        addV32ToV42CommonMappings(src, target, srcPath);

        //API Product Specific properties
        JsonObject idObject = src.getAsJsonObject("id");
        if (idObject != null) {
            populateAPIPropertyAsString(idObject, target, "providerName", "provider");
            populateAPIPropertyAsString(idObject, target, "apiProductName", "name");
        }
        populateAPIPropertyAsString(src, target, "state", "state");
        populateDependentAPIs(src, target);

        JsonElement typeElement = src.get("type");
        if (typeElement != null) {
            APIProductDTO.ApiTypeEnum apiTypeEnum = APIProductDTO.ApiTypeEnum.valueOf(typeElement.getAsString().toUpperCase());
            target.addProperty("type", apiTypeEnum.toString());
        } else {
            target.addProperty("type", APIProductDTO.ApiTypeEnum.APIPRODUCT.value());
        }

        target.addProperty("hasThumbnail", !StringUtils.isEmpty(CommonUtil.readElementAsString(src, "thumbnailUrl")));

    }

    private static void addV32ToV42APIMappings(JsonObject src, JsonObject target, String srcPath) throws CTLArtifactConversionException {
        addV32ToV42CommonMappings(src, target, srcPath);

        // API Specific properties
        JsonObject idObject = src.getAsJsonObject("id");
        if (idObject != null) {
            populateAPIPropertyAsString(idObject, target, "providerName", "provider");
            populateAPIPropertyAsString(idObject, target, "apiName", "name");
            populateAPIPropertyAsString(idObject, target, "version", "version");
        }
        populateAPIPropertyAsString(src, target, "status", "lifeCycleStatus");

        JsonElement typeElement = src.get("type");
        if (typeElement != null) {
            APIDTO.TypeEnum apiTypeEnum = APIDTO.TypeEnum.valueOf(typeElement.getAsString());
            target.addProperty("type", apiTypeEnum.toString());
        } else {
            target.addProperty("type", APIDTO.TypeEnum.HTTP.value());
        }

        target.addProperty("responseCachingEnabled", Constants.V32_RESPONSE_CACHING_ENABLED.equals(
                CommonUtil.readElementAsString(src, "responseCache")));
        target.addProperty("isDefaultVersion", CommonUtil.readElementAsBoolean(src, "isDefaultVersion"));
        target.addProperty("gatewayType", Constants.GATEWAY_TYPE_SYNAPSE);
        target.addProperty("enableSubscriberVerification", false);
        populateEndpointInfo(src, target);
    }

    private static void populateUriTemplates(String srcPath, JsonObject target) throws CTLArtifactConversionException {
        String pathToMetaInfo = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        JsonObject swaggerJson = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(pathToMetaInfo,
                Constants.SWAGGER));

        OpenAPIParser parser = new OpenAPIParser();
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolveFully(true);
        OpenAPI openAPI = parser.readContents(swaggerJson.getAsJsonObject().toString(), null, parseOptions).getOpenAPI();

        JsonArray uriTemplates = new JsonArray();
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            Map<PathItem.HttpMethod, Operation> operationsMap = entry.getValue().readOperationsMap();
            for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : operationsMap.entrySet()) {
                JsonObject uriTemplate = new JsonObject();
                Operation op = operationEntry.getValue();
                uriTemplate.addProperty("target", entry.getKey());
                uriTemplate.addProperty("verb", operationEntry.getKey().toString());

                Map<String, Object> extensions =  op.getExtensions();
                if (extensions != null) {
                    if (extensions.get("x-auth-type") != null) {
                        uriTemplate.addProperty("authType", extensions.get("x-auth-type").toString());
                    }
                    if (extensions.get("x-throttling-tier") != null) {
                        uriTemplate.addProperty("throttlingPolicy", extensions.get("x-throttling-tier").toString());
                    }
                }

                List<SecurityRequirement> requirements = op.getSecurity();
                if (requirements != null) {
                    for (SecurityRequirement requirement : requirements) {
                        if (requirement.get(Constants.DEFAULT_SECURITY) != null) {
                            List<String> defaultScopes = requirement.get(Constants.DEFAULT_SECURITY);
                            JsonArray scopes = new JsonArray();
                            for (String scope : defaultScopes) {
                                scopes.add(scope);
                            }
                            uriTemplate.add("scopes", scopes);
                        }
                    }
                }
                uriTemplates.add(uriTemplate);
            }
        }
        target.add("operations", uriTemplates);
    }

    private static void populateWSDLInfo(String srcPath, JsonObject src, JsonObject target) {
        String type = CommonUtil.readElementAsString(src, "type");
        String wsdlDirectory = srcPath + File.separator + Constants.WSDL_DIRECTORY;
        File wsdlDirectoryFile = new File(wsdlDirectory);
        if (Constants.SOAP.equalsIgnoreCase(type)) {
            if (wsdlDirectoryFile.exists() && wsdlDirectoryFile.isDirectory()) {
                JsonObject wsdlInfo = new JsonObject();
                JsonObject idObject = src.getAsJsonObject("id");
                if (idObject != null) {
                    String wsdlFileName = CommonUtil.readElementAsString(idObject, "apiName") + "-" +
                            CommonUtil.readElementAsString(idObject, "version") + Constants.WSDL_EXTENSION;
                    String wsdlArchiveName = CommonUtil.readElementAsString(idObject, "apiName") + "-" +
                            CommonUtil.readElementAsString(idObject, "version") + Constants.ZIP_EXTENSION;
                    File wsdlArchiveFile = new File(wsdlDirectoryFile, wsdlArchiveName);
                    File wsdlFile = new File(wsdlDirectoryFile, wsdlFileName);
                    if (wsdlArchiveFile.exists()) {
                        wsdlInfo.addProperty("type", "ZIP");
                    } else if (wsdlFile.exists()) {
                        wsdlInfo.addProperty("type", "FILE");
                    } else {
                        log.warn("WSDL file/archive not found for API: " + CommonUtil.readElementAsString(idObject, "apiName") +
                                " version: " + CommonUtil.readElementAsString(idObject, "version"));
                    }
                    target.add("wsdlInfo", wsdlInfo);
                }
            } else {
                log.warn("WSDL directory not found for SOAP API: " + CommonUtil.readElementAsString(src, "name") +
                        " version: " + CommonUtil.readElementAsString(src, "version"));
            }
        }

        //no need to populate wsdlUrl in the import artifact. This will be updated in the registry artifact when the
        //wsdl source is saved
    }

    private static void populateDependentAPIs(JsonObject src, JsonObject target) {
        JsonArray productResources = src.get("productResources").getAsJsonArray();
        Map<String, ProductAPIDTO> productAPIDTOsMap = new HashMap<>();
        for (JsonElement pr : productResources) {
            JsonObject productResource = pr.getAsJsonObject();
            JsonObject apiIdentifier = productResource.getAsJsonObject("apiIdentifier");
            if (apiIdentifier != null) {
                String apiName = CommonUtil.readElementAsString(apiIdentifier, "apiName");
                String apiVersion = CommonUtil.readElementAsString(apiIdentifier, "version");
                String key = apiName + "-" + apiVersion;

                List<APIOperationsDTO> operationsDTOS;
                ProductAPIDTO productAPIDTO;
                if (!productAPIDTOsMap.containsKey(key)) {
                    productAPIDTO = new ProductAPIDTO();
                    productAPIDTO.setName(apiName);
                    productAPIDTO.setVersion(apiVersion);
                } else {
                    productAPIDTO = productAPIDTOsMap.get(key);
                }

                if (productAPIDTO != null) {
                    operationsDTOS = productAPIDTO.getOperations();
                    if (operationsDTOS == null || operationsDTOS.isEmpty()) {
                        operationsDTOS = new ArrayList<>();
                    }

                    APIOperationsDTO apiOperationsDTO = new APIOperationsDTO();
                    JsonObject srcURITemplate = productResource.getAsJsonObject("uriTemplate");
                    apiOperationsDTO.setVerb(CommonUtil.readElementAsString(srcURITemplate, "httpVerb"));
                    apiOperationsDTO.setTarget(CommonUtil.readElementAsString(srcURITemplate, "uriTemplate"));
                    apiOperationsDTO.setAuthType(CommonUtil.readElementAsString(srcURITemplate, "authType"));
                    apiOperationsDTO.setThrottlingPolicy(CommonUtil.readElementAsString(srcURITemplate, "throttlingTier"));
                    apiOperationsDTO.setOperationPolicies(new APIOperationPoliciesDTO());
                    apiOperationsDTO.setAmznResourceName(CommonUtil.readElementAsString(srcURITemplate, "amznResourceName"));
                    apiOperationsDTO.setAmznResourceTimeout(CommonUtil.readElementAsInteger(srcURITemplate, "amznResourceTimeout"));
                    JsonArray scopesJson = CommonUtil.readElementAsJsonArray(srcURITemplate, "scopes");
                    List<String> scopes = new ArrayList<>();
                    if (scopesJson != null) {
                        scopesJson.forEach(scope -> { scopes.add(scope.getAsString()); });
                        apiOperationsDTO.setScopes(scopes);
                    }
                    operationsDTOS.add(apiOperationsDTO);

                    //todo: operation policies
                    productAPIDTO.setOperations(operationsDTOS);
                    productAPIDTOsMap.put(key, productAPIDTO);
                }
            } else {
                //log and skip the product resource : highly unlikely to reach here
            }
        }

        List<ProductAPIDTO> productAPIDTOS = new ArrayList<>(productAPIDTOsMap.values());
        JsonArray dependentAPIs = new Gson().toJsonTree(productAPIDTOS).getAsJsonArray();
        target.add("apis", dependentAPIs);
    }

    private static void populateSimilarAPIPropertyAsString(JsonObject src, JsonObject target, String key) {
        String value = CommonUtil.readElementAsString(src, key);
        if (!StringUtils.isEmpty(value)) {
            target.addProperty(key, value);
        }
    }

    private static void populateAPIPropertyAsString(JsonObject src, JsonObject target, String srcKey, String targetKey) {
        String value = CommonUtil.readElementAsString(src, srcKey);
        if (!StringUtils.isEmpty(value)) {
            target.addProperty(targetKey, value);
        }
    }

    private static void populateAPIPropertyAsInteger(JsonObject src, JsonObject target, String srcKey, String targetKey) {
        JsonElement element = src.get(srcKey);
        if (element != null) {
            target.addProperty(targetKey, element.getAsInt());
        }
    }

    private static void populateAdditionalProperties(JsonObject src, JsonObject target) {
        JsonElement additionalPropertiesObj = src.get("additionalProperties");
        if (additionalPropertiesObj != null) {
            JsonObject properties = src.get("additionalProperties").getAsJsonObject();
            JsonArray propertiesArray = new JsonArray();
            JsonObject propertiesMap = new JsonObject();
            if (properties != null & !properties.isEmpty()) {
                for (String key : properties.keySet()) {
                    String value = properties.get(key).getAsString();
                    if (value != null) {
                        JsonObject property = new JsonObject();
                        property.addProperty("name", key.replace("__display", ""));
                        property.addProperty("value", value);
                        property.addProperty("display", key.contains("__display"));
                        propertiesArray.add(property);

                        //when adding to map display is set back to false, otherwise two properties will be added to the API
                        JsonObject mapProperty = new JsonObject();
                        mapProperty.addProperty("name", key.replace("__display", ""));
                        mapProperty.addProperty("value", value);
                        mapProperty.addProperty("display", false);
                        propertiesMap.add(key, mapProperty);
                    }
                }
                target.add("additionalProperties", propertiesArray);
                target.add("additionalPropertiesMap", propertiesMap);
            }
        }
    }

    private static void populateCategoriesAndTags(JsonObject src, JsonObject target) {
        JsonArray categories = CommonUtil.readElementAsJsonArray(src, "apiCategories");
        if (categories != null && !categories.isEmpty()) {
            JsonArray categoryNamesArray = new JsonArray();
            categories.asList().forEach(category -> {
                JsonObject categoryObject = category.getAsJsonObject();
                String categoryName = CommonUtil.readElementAsString(categoryObject, "name");
                if (!StringUtils.isEmpty(categoryName)) {
                    categoryNamesArray.add(categoryName);
                }
            });
            target.add("categories", categoryNamesArray);
        }

        JsonArray tags = CommonUtil.readElementAsJsonArray(src, "tags");
        if (tags != null && !tags.isEmpty()) {
            target.add("tags", tags);
        }
    }

    private static void populateAPISecurity(JsonObject src, JsonObject target) {
        String apiSecurity = CommonUtil.readElementAsString(src, "apiSecurity");
        if (!StringUtils.isEmpty(apiSecurity)) {
            JsonArray apiSecurityArray = new JsonArray();
            Arrays.stream(apiSecurity.split(",")).map(String::trim).forEach(apiSecurityArray::add);
            target.add("securityScheme", apiSecurityArray);
        }
    }

    private static void populateThrottlePolicies(JsonObject src, JsonObject target) {
        JsonArray availableTiers = src.get("availableTiers").getAsJsonArray();
        JsonArray policies = new JsonArray();
        for (JsonElement element : availableTiers.asList()) {
            JsonObject tier = element.getAsJsonObject();
            String tierName = CommonUtil.readElementAsString(tier, "name");
            policies.add(tierName);
        }
        target.add("policies", policies);
    }

    private static void populateTransports(JsonObject src, JsonObject target) {
        String transports = CommonUtil.readElementAsString(src, "transports");
        if (!StringUtils.isEmpty(transports)) {
            JsonArray transportsArray = new JsonArray();
            Arrays.stream(transports.split(",")).map(String::trim).forEach(transportsArray::add);
            target.add("transport", transportsArray);
        }
    }

    private static void populateEndpointInfo(JsonObject src, JsonObject target) {
        String implementation = CommonUtil.readElementAsString(src, "implementation");
        APIDTO.EndpointImplementationTypeEnum endpointImplementationType = APIDTO.EndpointImplementationTypeEnum.ENDPOINT;
        if (!StringUtils.isEmpty(implementation)) {
            endpointImplementationType = APIDTO.EndpointImplementationTypeEnum
                    .fromValue(src.get("implementation").getAsString());
        }
        target.addProperty("endpointImplementationType", endpointImplementationType.toString());

        if (src.get("endpointConfig") != null) {
            String endpointConfigString = src.get("endpointConfig").getAsString();
            JsonObject endpointConfigObject = new Gson().fromJson(endpointConfigString, JsonObject.class);
            target.add("endpointConfig", endpointConfigObject);
        }
    }

    private static void populateBusinessInfo(JsonObject src, JsonObject target) {
        JsonObject businessInfo = new JsonObject();
        String businessOwner = CommonUtil.readElementAsString(src, "businessOwner");
        if (!StringUtils.isEmpty(businessOwner)) {
            businessInfo.addProperty("businessOwner", businessOwner);
        }
        String businessOwnerEmail = CommonUtil.readElementAsString(src, "businessOwnerEmail");
        if (!StringUtils.isEmpty(businessOwnerEmail)) {
            businessInfo.addProperty("businessOwnerEmail", businessOwnerEmail);
        }
        String technicalOwner = CommonUtil.readElementAsString(src, "technicalOwner");
        if (!StringUtils.isEmpty(technicalOwner)) {
            businessInfo.addProperty("technicalOwner", technicalOwner);
        }
        String technicalOwnerEmail = CommonUtil.readElementAsString(src, "technicalOwnerEmail");
        if (!StringUtils.isEmpty(technicalOwnerEmail)) {
            businessInfo.addProperty("technicalOwnerEmail", technicalOwnerEmail);
        }
        target.add("businessInformation", businessInfo);
    }

    private static void populateVisibilityInfo(JsonObject src, JsonObject target) {
        String visibility = CommonUtil.readElementAsString(src, "visibility").toUpperCase();
        if (!StringUtils.isEmpty(visibility)) {
            APIDTO.VisibilityEnum visibilityEnum = Enum.valueOf(APIDTO.VisibilityEnum.class, visibility);
            target.addProperty("visibility", visibilityEnum.toString());

            String visibleRoles = CommonUtil.readElementAsString(src, "visibleRoles");
            if (!StringUtils.isEmpty(visibleRoles)) {
                String[] rolesArray = CommonUtil.fromStringToArray(visibleRoles, ",");
                target.add("visibleRoles", CommonUtil.toJsonArray(rolesArray));
            }
            String visibleTenants = CommonUtil.readElementAsString(src, "visibleTenants");
            if (!StringUtils.isEmpty(visibleTenants)) {
                String[] tenantsArray = CommonUtil.fromStringToArray(visibleTenants, ",");
                target.add("visibleTenants", CommonUtil.toJsonArray(tenantsArray));
            }
        }
    }

    private static void populateSubscriptionAvailabilityInfo(JsonObject src, JsonObject target) {
        String subscriptionAvailability = CommonUtil.readElementAsString(src, "subscriptionAvailability").toUpperCase();
        if (!StringUtils.isEmpty(subscriptionAvailability)) {
            APIDTO.SubscriptionAvailabilityEnum subscriptionAvailabilityEnum =
                    APIDTO.SubscriptionAvailabilityEnum.valueOf(subscriptionAvailability);
            target.addProperty("subscriptionAvailability", subscriptionAvailabilityEnum.toString());

            String subscriptionAvailableTenants = CommonUtil.readElementAsString(src, "subscriptionAvailableTenants");
            if (!StringUtils.isEmpty(subscriptionAvailableTenants)) {
                String[] tenants = CommonUtil.fromStringToArray(subscriptionAvailableTenants, ",");
                target.add("subscriptionAvailableTenants", CommonUtil.toJsonArray(tenants));
            }
        }
    }

    private static void populateAccessControlInfo(JsonObject src, JsonObject target) {
        String accessControl = CommonUtil.readElementAsString(src, "accessControl");
        if (!StringUtils.isEmpty(accessControl)) {
            APIDTO.AccessControlEnum accessControlEnum;
            if (Constants.ACCESS_CONTROL_ALL.equals(accessControl)) {
                accessControlEnum = APIDTO.AccessControlEnum.NONE;
            } else {
                accessControlEnum = APIDTO.AccessControlEnum.RESTRICTED;
                String allowRoles = CommonUtil.readElementAsString(src, "accessControlRoles");
                if (!StringUtils.isEmpty(allowRoles)) {
                    String[] allowRolesArray = CommonUtil.fromStringToArray(allowRoles, ",");
                    target.add("accessControlRoles", CommonUtil.toJsonArray(allowRolesArray));
                }
            }
            target.addProperty("accessControl", accessControlEnum.toString());
        }
    }

    private static void populateCorsConfig(JsonObject src, JsonObject target) {
        JsonObject corsConfig = src.get("corsConfiguration") != null ? src.get("corsConfiguration").getAsJsonObject() : null;
        if (corsConfig != null) {
            JsonObject cors = new JsonObject();
            boolean corsConfigurationEnabled = CommonUtil.readElementAsBoolean(corsConfig, "corsConfigurationEnabled");
            JsonArray accessControlAllowOrigins = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowOrigins");
            boolean accessControlAllowCredentials = CommonUtil.readElementAsBoolean(corsConfig, "accessControlAllowCredentials");
            JsonArray accessControlAllowHeaders = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowHeaders");
            JsonArray accessControlAllowMethods = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowMethods");

            cors.addProperty("corsConfigurationEnabled", corsConfigurationEnabled);
            cors.add("accessControlAllowOrigins", accessControlAllowOrigins);
            cors.addProperty("accessControlAllowCredentials", accessControlAllowCredentials);
            cors.add("accessControlAllowHeaders", accessControlAllowHeaders);
            cors.add("accessControlAllowMethods", accessControlAllowMethods);

            target.add("corsConfiguration", cors);
        }
    }
}
