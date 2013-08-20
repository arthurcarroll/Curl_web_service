package com.tireintelligence.rest.example.service.endpoint;

import com.tireintelligence.rest.example.data.CurlResponse;
import com.tireintelligence.rest.example.service.impl.CurlClientImpl;
import com.wordnik.swagger.annotations.*;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Arthur Carroll
 *
 */
@Path("/CurlClient.json")
@Api(value = "/CurlClient", description = "A client which can used to run arbitrary curl commands and return exactly what is presented on the terminal")
@Produces({"application/json"})
public class CurlClient extends CurlClientImpl {

    @POST
    @Path("/runCurlScript")
    @ApiOperation(
            value = "Run a curl command",
            notes = "Run a base 64 encode shell command as it is written and return exactly what would have appeared on the shell",
            responseClass = "com.tireintelligence.rest.example.data.CurlResponse")
    @Override
    public CurlResponse runCurlCommand(
            @ApiParam(value = "The curl command to run exaclty as it was typed on the shell", required = true, defaultValue = "ls") @FormParam("commandToRun") @DefaultValue("pwd") String commandToRun,
            @ApiParam(value = "Should the response body be base 64 encoded?", required = false, defaultValue = "false") @FormParam("base64EncodeResponseBody") @DefaultValue("false") boolean base64EncodeResponseBody) {
        return super.runCurlCommand(commandToRun, base64EncodeResponseBody);
    }
}
