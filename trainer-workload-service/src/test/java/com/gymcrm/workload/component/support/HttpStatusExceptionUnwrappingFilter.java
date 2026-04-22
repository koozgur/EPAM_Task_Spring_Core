package com.gymcrm.workload.component.support;

import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.internal.http.HttpResponseDecorator;
import io.restassured.internal.http.HttpResponseException;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Converts REST-assured 5.4.0 non-POST 4xx/5xx failures from
 * HttpResponseException back into a Response. (workaround)
 *
 * <p>REST-assured installs its failure handler reliably for POST, but not for
 * other methods in this version. This filter catches the escaped exception and
 * rebuilds the response so negative-path tests can assert status codes
 * consistently.
 */
public class HttpStatusExceptionUnwrappingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        try {
            return ctx.next(requestSpec, responseSpec);
        } catch (Throwable t) {
            if (t instanceof HttpResponseException hre) {
                return toResponse(hre.getResponse());
            }
            if (t instanceof RuntimeException re) {
                throw re;
            }
            if (t instanceof Error err) {
                throw err;
            }
            throw new RuntimeException(t);
        }
    }

    private static Response toResponse(HttpResponseDecorator resp) {
        ResponseBuilder builder = new ResponseBuilder()
                .setStatusCode(resp.getStatusLine().getStatusCode())
                .setStatusLine(resp.getStatusLine().toString());

        HttpEntity entity = resp.getEntity();
        if (entity != null) {
            try {
                byte[] body = EntityUtils.toByteArray(entity);
                builder.setBody(body);
                if (entity.getContentType() != null) {
                    builder.setContentType(entity.getContentType().getValue());
                }
            } catch (IOException ignored) {
                // leave body empty - status code + headers are still available
            }
        }

        HeaderIterator headers = resp.headerIterator();
        while (headers.hasNext()) {
            Header h = headers.nextHeader();
            builder.setHeader(h.getName(), h.getValue());
        }

        return builder.build();
    }
}
