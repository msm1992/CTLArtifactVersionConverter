package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32.V32Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42APIPolicies;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequencesMappingUtil {

    public static void mapSequences(Sequences srcSequences, Sequences targetSequences, String srcVersion,
                                    String targetVersion) throws CTLArtifactConversionException {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            mapV32SequencesToV42APIPolicies(srcSequences, targetSequences);
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    private static void mapV32SequencesToV42APIPolicies(Sequences srcSequences, Sequences targetSequences) throws
            CTLArtifactConversionException {
        populateV42APIPoliciesFromV32Sequences(srcSequences, targetSequences, Constants.IN);
        populateV42APIPoliciesFromV32Sequences(srcSequences, targetSequences, Constants.OUT);
        populateV42APIPoliciesFromV32Sequences(srcSequences, targetSequences, Constants.FAULT);
    }

    private static void populateV42APIPoliciesFromV32Sequences(Sequences srcSequences, Sequences targetAPIPolicies,
                                                               String direction) throws CTLArtifactConversionException {
        V42APIPolicies v42APIPolicies = (V42APIPolicies) targetAPIPolicies;
        V32Sequences v32Sequences = (V32Sequences) srcSequences;
        Map<String, List<JsonObject>> sequencesMap = v32Sequences.getSequencesMap();
        if (sequencesMap != null && sequencesMap.get(direction) != null) {
            for (JsonObject sequence : sequencesMap.get(direction)) {
                JsonObject v42Policy = v42APIPolicies.getAPIPolicy(CommonUtil.readElementAsString(sequence, "name"));
                if (v42Policy != null) {
                    //sequence already exists for a different flow, add the flow to the existing sequence's applicable flows
                    CommonUtil.readElementAsJsonArray(v42Policy, "applicableFlows").add(resolveDirectionToFlow(direction));
                } else {
                    v42Policy = mapV32SequenceToV42APIPolicy(sequence, resolveDirectionToFlow(direction));
                    v42APIPolicies.addAPIPolicy(v42Policy);
                }
            }
        }
    }

    private static JsonObject mapV32SequenceToV42APIPolicy(JsonObject srcSequence, String flow) throws
            CTLArtifactConversionException {
        JsonObject v42PolicyObj = new JsonObject();
        String sequenceName = CommonUtil.readElementAsString(srcSequence, "name");
        v42PolicyObj.addProperty("name", sequenceName);
        v42PolicyObj.addProperty("category", Constants.POLICY_CATEGORY_MEDIATION);
        v42PolicyObj.addProperty("version", "v1");
        v42PolicyObj.addProperty("displayName", sequenceName);
        v42PolicyObj.addProperty("description", sequenceName);

        JsonArray flows = new JsonArray();
        flows.add(flow);
        v42PolicyObj.add("applicableFlows", flows);

        JsonArray supportedGateways = new JsonArray();
        supportedGateways.add(Constants.GATEWAY_TYPE_SYNAPSE);
        v42PolicyObj.add("supportedGateways", supportedGateways);

        //todo: check and fix this to add correct API Types
        JsonArray supportedAPITypes = new JsonArray();
        supportedAPITypes.add("HTTP");
        v42PolicyObj.add("supportedApiTypes", supportedAPITypes);

        v42PolicyObj.add("policyAttributes",new JsonArray());

        //store sequence content as a property
        populatePolicySpec(CommonUtil.readElementAsString(srcSequence, "content"), v42PolicyObj);
        return v42PolicyObj;
    }

    private static void populatePolicySpec(String sequenceContent, JsonObject v42PolicyObj) throws
            CTLArtifactConversionException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(sequenceContent);
            ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            Document doc = builder.parse(input);

            NodeList sequenceList = doc.getElementsByTagName("sequence");
            if (sequenceList != null && sequenceList.getLength() > 0) {
                Element sequence = (Element) sequenceList.item(0);
                NodeList children = sequence.getChildNodes();

                StringBuilder xml = new StringBuilder();
                for (int i = 0; i < children.getLength(); i++) {
                    if (children.item(i) instanceof Element) {
                        Element childElement = (Element) children.item(i);
                        String childElementString = convertElementToString(childElement);
                        xml.append(childElementString);
                    }
                }
                v42PolicyObj.addProperty("policySpec", xml.toString());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            String msg = "Error while parsing sequence content";
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    private static String convertElementToString(Element element) throws CTLArtifactConversionException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(Constants.INDENT_PROPERTY, Constants.INDENT_PROPERTY_VALUE);
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(element), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            String msg = "Error while converting xml element to string";
            throw new CTLArtifactConversionException(msg, e);
        }
    }

    public static List<JsonObject> readV32Sequences(String pathToSequences, String direction) throws
            CTLArtifactConversionException {
        List<JsonObject> sequences = new ArrayList<>();
        sequences.addAll(readV32Sequences(pathToSequences, direction, Constants.CUSTOM));
        sequences.addAll(readV32Sequences(pathToSequences, direction, Constants.COMMON));
        return sequences;
    }

    private static List<JsonObject> readV32Sequences(String pathToSequences, String direction, String type) throws
            CTLArtifactConversionException {
        List<JsonObject> sequences = new ArrayList<>();
        pathToSequences = pathToSequences + File.separator + direction + Constants.SEQUENCES_SUFFIX;
        if (Constants.CUSTOM.equals(type)) {
            pathToSequences = pathToSequences + File.separator + Constants.CUSTOM;
        }

        File srcSequencesDirectory = new File(pathToSequences);
        if (srcSequencesDirectory.exists()) {
            File[] files = srcSequencesDirectory.listFiles();
            for (File file : files) {
                //in case the sequence type is common, we need to skip the custom sequences sub-directory
                if (!file.isDirectory()) {
                    String sequenceFileName = file.getName();
                    String pathToSequence = pathToSequences + File.separator + sequenceFileName;
                    String sequenceContent = ConfigFileUtil.readFileContentAsString(pathToSequence);

                    JsonObject seqObj = new JsonObject();
                    seqObj.addProperty("name", sequenceFileName.replace(Constants.XML_EXTENSION, ""));
                    seqObj.addProperty("content", sequenceContent);
                    sequences.add(seqObj);
                }
            }
        } else {
            //log and exit
        }
        return sequences;
    }

    public static void populateAPIPoliciesInAPI(JsonObject v32API, JsonObject v42API, Map<String, JsonObject> sequencesMap) {
        JsonObject apiPolicies = new JsonObject();
        for (String direction : Constants.SEQ_DIRECTIONS) {
            populateAPIPoliciesInAPI(v32API, direction, apiPolicies);
        }
        v42API.get("data").getAsJsonObject().add("apiPolicies", apiPolicies);
    }

    private static void populateAPIPoliciesInAPI(JsonObject v32API,
                                                 String direction, JsonObject apiPolicyConfig) {

        String sequenceName = v32API.get(direction + "Sequence") != null ?
                v32API.get(direction + "Sequence").getAsString() : null;
        if (sequenceName != null && !sequenceName.isEmpty()) {
            JsonObject seqConfig = new JsonObject();
            seqConfig.addProperty("policyName", sequenceName);
            seqConfig.addProperty("policyVersion", "v1");
            seqConfig.add("parameters", new JsonObject());

            JsonArray seqArray = new JsonArray();
            seqArray.add(seqConfig);
            apiPolicyConfig.add(resolveDirectionToFlow(direction), seqArray);
        }
    }

    private static String resolveDirectionToFlow(String direction) {
        switch (direction) {
            case Constants.IN:
                return Constants.REQUEST_FLOW;
            case Constants.OUT:
                return Constants.RESPONSE_FLOW;
            case Constants.FAULT:
                return Constants.FAULT;
            default:
                return null;
        }
    }
}
