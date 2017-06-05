package com.xartifex.moneytransfers.server.config;

import com.xartifex.moneytransfers.server.model.Const;
import com.xartifex.moneytransfers.server.model.ServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * author: xartifex
 * since: 02.06.2017
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public Response toResponse(Exception exception) {
        logger.error("Internal Error occurred: ", exception);
        if (exception instanceof javax.ws.rs.NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ServerError("Resource not found!", Response.Status.NOT_FOUND.getStatusCode()))
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } else if (exception instanceof javax.ws.rs.NotAllowedException) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .entity(new ServerError("Method not allowed!", Response.Status.METHOD_NOT_ALLOWED.getStatusCode()))
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } else if (exception instanceof org.codehaus.jackson.map.exc.UnrecognizedPropertyException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ServerError("Unrecognized field!", Response.Status.BAD_REQUEST.getStatusCode()))
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                    entity(new ServerError(Const.INTERNAL_SERVER_ERROR_MESSAGE, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())).
                    type(MediaType.APPLICATION_JSON).build();
        }
    }
}