package org.wso2.carbon.apimgt.ctl.artifact.converter.rest;

import org.wso2.carbon.apimgt.ctl.artifact.converter.rest.impl.ArtifactConvertApiService;
import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.ErrorDTO;
import java.io.File;

import org.wso2.carbon.apimgt.ctl.artifact.converter.rest.impl.ArtifactConvertApiServiceImpl;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

@Path("/artifact-convert")

@Api(description = "the artifact-convert API")




public class ArtifactConvertApi  {

    @Context MessageContext securityContext;

    ArtifactConvertApiService delegate = new ArtifactConvertApiServiceImpl();


    @POST

    @Consumes({ "multipart/form-data" })
    @Produces({ "application/zip", "application/json" })
    @ApiOperation(value = "Convert export API or API Product artifacts from one version to another ", notes = "This operation allows to convert CTL artifacts from one version to another.  Note : As of now only 3.2.0 to 4.2.0 conversion is supported. ", response = File.class, tags={ "Convert Artifact" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK. Converted artifact binary is returned. ", response = File.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ErrorDTO.class) })
    public Response convertCTLArtifact( @Multipart(value = "file") InputStream fileInputStream, @Multipart(value = "file" ) Attachment fileDetail, @ApiParam(value = "Source artifact version ", allowableValues="3.2.0", defaultValue="3.2.0") @DefaultValue("3.2.0")  @QueryParam("srcVersion") String srcVersion, @ApiParam(value = "Target artifact version ", allowableValues="4.2.0", defaultValue="4.2.0") @DefaultValue("4.2.0")  @QueryParam("targetVersion") String targetVersion, @ApiParam(value = "Export artifact format (YAML or JSON) ", allowableValues="YAML, JSON", defaultValue="YAML") @DefaultValue("YAML")  @QueryParam("exportFormat") String exportFormat, @ApiParam(value = "Export artifact type (API or APIProduct) ", allowableValues="API, APIProduct", defaultValue="API") @DefaultValue("API")  @QueryParam("type") String type,  @Multipart(value = "params", required = false) InputStream paramsInputStream, @Multipart(value = "params" , required = false) Attachment paramsDetail) throws CTLArtifactConversionException {
        return delegate.convertCTLArtifact(fileInputStream, fileDetail, srcVersion, targetVersion, exportFormat, type, paramsInputStream, paramsDetail, securityContext);
    }

    @POST
    @Path("/api")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/zip", "application/json" })
    @ApiOperation(value = "Convert api.json artifacts from one version to another ", notes = "This operation allows to convert api.json artifacts from one version to another.  Note : As of now only 3.2.0 to 4.2.0 conversion is supported. ", response = File.class, tags={ "Convert Artifact" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK. Converted artifact binary is returned. ", response = File.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ErrorDTO.class) })
    public Response convertAPIArtifact( @Multipart(value = "file") InputStream fileInputStream, @Multipart(value = "file" ) Attachment fileDetail, @ApiParam(value = "Source artifact version ", allowableValues="3.2.0", defaultValue="3.2.0") @DefaultValue("3.2.0")  @QueryParam("srcVersion") String srcVersion, @ApiParam(value = "Target artifact version ", allowableValues="4.2.0", defaultValue="4.2.0") @DefaultValue("4.2.0")  @QueryParam("targetVersion") String targetVersion, @ApiParam(value = "Export artifact type (API or APIProduct) ", allowableValues="API, APIProduct", defaultValue="API") @DefaultValue("API")  @QueryParam("type") String type,  @Multipart(value = "params", required = false) InputStream paramsInputStream) throws CTLArtifactConversionException {
        return delegate.convertRESTApiArtifact(fileInputStream, srcVersion, targetVersion, type, paramsInputStream);
    }
}
