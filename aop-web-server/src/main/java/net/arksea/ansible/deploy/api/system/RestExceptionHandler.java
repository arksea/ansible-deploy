package net.arksea.ansible.deploy.api.system;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 处理Restful异常.
 */
@ControllerAdvice
public class RestExceptionHandler extends net.arksea.restapi.RestExceptionHandler {

    @ExceptionHandler({UnauthenticatedException.class})
    public final ResponseEntity<?> handleException(UnauthenticatedException ex, WebRequest request) {
        return this.handle(new UnauthenticatedException("用户未认证",ex), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public final ResponseEntity<?> handleException(UnauthorizedException ex, WebRequest request) {
        return this.handle(new UnauthenticatedException("没有操作权限",ex), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({IncorrectCredentialsException.class})
    public final ResponseEntity<?> handleException(IncorrectCredentialsException ex, WebRequest request) {
        return this.handle(new UnauthenticatedException("用户名或密码错误",ex), HttpStatus.UNAUTHORIZED, request);
    }
}
