package org.wso2.carbon.apimgt.ctl.artifact.converter.rest.impl;

import org.wso2.carbon.apimgt.ctl.artifact.converter.*;
import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.*;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.ErrorDTO;
import java.io.File;

import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public interface ArtifactConvertApiService {
      public Response convertCTLArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion, String targetVersion, String exportFormat, String type, InputStream paramsInputStream, Attachment paramsDetail, MessageContext messageContext) throws CTLArtifactConversionException;

      public Response convertAPIArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion, String targetVersion, String exportFormat, String type, InputStream paramsInputStream, Attachment paramsDetail, MessageContext messageContext) throws CTLArtifactConversionException;
}
