package com.altimetrik.greetingservice.config.security;

import com.altimetrik.greetingservice.config.Constants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {

        String exceptionDetail = (String) httpServletRequest.getAttribute(Constants.JWT_EXCEPTION);
        exceptionDetail = exceptionDetail == null ? Constants.EMPTY : exceptionDetail;
        switch (exceptionDetail) {
            case Constants.SIGNATURE_EXCEPTION:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt Signature Invalid");
                break;
            case Constants.MALFORMED_JWT_EXCEPTION:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt Token Malformed");
                break;
            case Constants.EXPIRED_JWT_EXCEPTION:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt Token Expired");
                break;
            case Constants.UNSUPPORTED_JWT_EXCEPTION:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported Jwt Exception");
                break;
            case Constants.ILLEGAL_ARGUMENT_EXCEPTION:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt claims string is empty");
                break;
            default:
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Login details");
                break;

        }
    }
}
