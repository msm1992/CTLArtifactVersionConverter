package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

public class Constants {
    public static final String V320 = "3.2.0";
    public static final String V420 = "4.2.0";
    public static final String DOCS_DIRECTORY = "Docs";
    public static final String ENDPOINT_CERTIFICATES_DIRECTORY = "Endpoint-certificates";
    public static final String CLIENT_CERTIFICATES_DIRECTORY = "Client-certificates";
    public static final String META_INFO_DIRECTORY = "Meta-information";
    public static final String SEQUENCES_DIRECTORY = "Sequences";
    public static final String POLICIES_DIRECTORY = "Policies";
    public static final String INLINE_CONTENTS_DIRECTORY = "InlineContents";
    public static final String FILE_CONTENTS_DIRECTORY = "FileContents";
    public static final String DEFINITIONS_DIRECTORY = "Definitions";
    public static final String WSDL_DIRECTORY = "WSDL";
    public static final String APIS_DIRECTORY = "APIs";
    public static final String DOCS_CONFIG_FILE = "docs";
    public static final String DOCS_YAML = "docs.yaml";
    public static final String DOCS_JSON = "docs.json";
    public static final String DOCUMENT_YAML = "document.yaml";
    public static final String DOCUMENT_JSON = "document.json";
    public static final String ENDPOINT_CERTIFICATES_YAML = "endpoint_certificates.yaml";
    public static final String ENDPOINT_CERTIFICATES_JSON = "endpoint_certificates.json";
    public static final String API_JSON = "api.json";
    public static final String API_YAML = "api.yaml";
    public static final String API_PRODUCT_JSON = "api_product.json";
    public static final String API_PRODUCT_YAML = "api_product.yaml";
    public static final String SWAGGER_YAML = "swagger.yaml";
    public static final String SWAGGER_JSON = "swagger.json";
    public static final String SWAGGER = "swagger";
    public static final String ENDPOINT_CERTIFICATES_CONFIG = "endpoint_certificates";
    public static final String CLIENT_CERTIFICATES_CONFIG = "client_certificates";
    public static final String API_CONFIG = "api";
    public static final String API_PRODUCT_CONFIG = "api";
    public static final String YAML_FORMAT = "YAML";
    public static final String JSON_FORMAT = "JSON";
    public static final String DATA = "data";
    public static final String TYPE = "type";
    public static final String API_DATA_VERSION = "version";
    public static final String API_TYPE = "API";
    public static final String API_PRIDUCT_TYPE = "APIProduct";
    public static final String DEPLOYMENT_ENVIRONMENTS_TYPE = "deployment_environments";
    public static final String DOCUMENT_TYPE = "document";
    public static final String API_PRODUCT_DTO_TYPE = "api_product";
    public static final String API_DTO_TYPE = "api";
    public static final String POLICY_SPEC_TYPE = "operation_policy_specification";
    public static final String ENDPOINT_CERTIFICATES_TYPE = "endpoint_certificates";
    public static final String CLIENT_CERTIFICATES_TYPE = "client_certificates";
    public static final String APIM_420_VERSION = "4.2.0";
    public static final String INLINE_DOC_TYPE = "INLINE";
    public static final String FILE_DOC_TYPE = "FILE";
    public static final String MARKDOWN_DOC_TYPE = "MARKDOWN";
    public static final String URL_DOC_TYPE = "URL";
    public static final String YAML_EXTENSION = ".yaml";
    public static final String JSON_EXTENSION = ".json";
    public static final String XML_EXTENSION = ".xml";
    public static final String CRT_EXTENSION = ".crt";
    public static final String ZIP_EXTENSION = ".zip";
    public static final String WSDL_EXTENSION = ".wsdl";
    public static final String BEGIN_CERTIFICATE_STRING = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERTIFICATE_STRING = "-----END CERTIFICATE-----";
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String FAULT = "fault";
    public static final String REQUEST_FLOW = "request";
    public static final String RESPONSE_FLOW = "response";
    public static final String SEQUENCES_SUFFIX = "-sequence";
    public static final String CUSTOM = "Custom";
    public static final String COMMON = "Common";
    public static final String POLICY_CATEGORY_MEDIATION = "Mediation";
    public static final String GATEWAY_TYPE_SYNAPSE = "Synapse";
    public static final String INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
    public static final String INDENT_PROPERTY_VALUE = "2";

    public static final String[] supportedSourceVersions = { V320 };
    public static final String[] supportedTargetVersions = { V420 };
    public static final String[] SEQ_DIRECTIONS = {IN, OUT, FAULT};

    //Meta-info constants
    public static final String V32_RESPONSE_CACHING_ENABLED = "ENABLED";
    public static final String ACCESS_CONTROL_ALL = "all";
    public static final String GATEWAY_VENDOR_WSO2 = "wso2";
    public static final String GATEWAY_TYPE_WSO2_SYNAPSE = "wso2/synapse";
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static final int TEMP_FILENAME_LENGTH = 5;
    public static final String UPLOAD_FILE_NAME = "APIArchive.zip";
    public static final String UPLOAD_API_JSON = "api.json";

    public static final char ZIP_FILE_SEPARATOR = '/';
    public static final char WIN_ZIP_FILE_SEPARATOR = '\\';
    public static final String PUBLISHED = "PUBLISHED";
    public static final String PROTOTYPED = "PROTOTYPED";
    public static final String GRAPHQL = "GRAPHQL";
    public static final String SOAP = "SOAP";
    public static final String GRAPHQL_SCHEMA_FILE_NAME = "schema.graphql";
    public static final String ENDPOINT_CERT_TYPE = "ENDPOINT_CERTIFICATE";
    public static final String CLIENT_CERT_TYPE = "CLIENT_CERTIFICATE";
    public static final String DEFAULT_SECURITY = "default";

    /**
     * API JSON related properties
     */

    public static final String AJ_API_BUSINESS_OWNER = "businessOwner";
    public static final String AJ_API_BUSINESS_OWNER_EMAIL = "businessOwnerEmail";
    public static final String AJ_API_TECHNICAL_OWNER = "technicalOwner";
    public static final String AJ_API_TECHNICAL_OWNER_EMAIL = "technicalOwnerEmail";
    public static final String AJ_ADDITIONAL_PROPERTIES = "additionalProperties";
    public static final String AJ_ADDITIONAL_PROPERTIES_MAP = "additionalPropertiesMap";
    public static final String AJ_BUSINESS_INFORMATION = "businessInformation";
    public static final String AJ_IS_REVISION = "isRevision";
    public static final String AJ_REVISION_ID = "revisionId";
    public static final String AJ_GATEWAY_VENDOR = "gatewayVendor";
    public static final String AJ_IS_DEFAULT_VERSION = "isDefaultVersion";
    public static final String AJ_GATEWAY_TYPE = "gatewayType";
    public static final String AJ_ASYNC_TRANSPORT_PROTOCOLS = "asyncTransportProtocols";
    public static final String AJ_kEY_MANAGERS = "keyManagers";
    public static final String AJ_IS_MONETIZATION_ENABLED = "isMonetizationEnabled";
    public static final String AJ_MONETIZATION_PROPERTIES = "monetizationProperties";
    public static final String AJ_ENABLE_SCHEMA_VALIDATION = "enableSchemaValidation";
    public static final String AJ_ACCESS_CONTROL = "accessControl";
    public static final String AJ_POLICY_NAME = "policyName";
    public static final String AJ_POLICY_ID = "policyId";
    public static final String AJ_POLICY_VERSION = "policyVersion";
    public static final String AJ_POLICY_PARAMS = "parameters";

}
