package org.wso2.carbon.apimgt.ctl.artifact.converter.rest.impl;


import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;



import java.io.InputStream;

import javax.ws.rs.core.Response;


public interface ArtifactConvertApiService {
      public Response convertCTLArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion, String targetVersion, String exportFormat, String type, InputStream paramsInputStream, Attachment paramsDetail, MessageContext messageContext) throws CTLArtifactConversionException;

      public Response convertRESTApiArtifact(InputStream fileInputStream, String srcVersion, String targetVersion, String type, InputStream paramsInputStream) throws CTLArtifactConversionException;
}
