package io.github.timofeevvr.petstore;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingFilter implements Filter {

    /**
     * Prints requests and responses to Slf4j logger.
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        // request
        String n = System.lineSeparator();
        log.info("{}Request method: {}{}Request URI: {}{}Body: {}{}", n, requestSpec.getMethod(),
                n, requestSpec.getURI(), n, new Prettifier().getPrettifiedBodyIfPossible(requestSpec), n);
        // response
        Response response = ctx.next(requestSpec, responseSpec);
        String body = new Prettifier().getPrettifiedBodyIfPossible(response, response);
        if (body.length() > 500) {
            body = body.substring(0, 500) + "...";
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            log.info("{}{}{}{}{}", n, response.statusLine(), n, body, n);
        } else {
            log.error("{}{}{}{}{}", n, response.statusLine(), n, body, n);
        }
        return response;
    }
}
