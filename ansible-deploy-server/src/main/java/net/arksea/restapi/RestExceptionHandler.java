package net.arksea.restapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理Restful异常.
 */
@ControllerAdvice
@SuppressWarnings("PMD.MoreThanOneLogger")
public class RestExceptionHandler {
    protected static final Logger LOGGER = LogManager.getLogger("net.arksea.restapi.logger.InternalError");
    protected static final Logger BADREQ_LOGGER = LogManager.getLogger("net.arksea.restapi.logger.BadRequest");
    /**
     * 处理RestException.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(RestException.class)
    public final ResponseEntity<?> handleRestException(final RestException ex, final WebRequest request) {
        return handle(ex, ex.getCode(), ex.getStatus(), request, ex.getDetail());
    }

    @ExceptionHandler(BindException.class)
    public final ResponseEntity<?> handleException(final BindException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleException(final Exception ex, final WebRequest request) {
        return handle(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public final ResponseEntity<?> handleException(final ConversionNotSupportedException ex, final WebRequest request) {
        return handle(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public final ResponseEntity<?> handleException(final HttpMediaTypeNotAcceptableException ex, final WebRequest request) {
        return handle(ex, HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public final ResponseEntity<?> handleException(final HttpMediaTypeNotSupportedException ex, final WebRequest request) {
        return handle(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<?> handleException(final HttpMessageNotReadableException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public final ResponseEntity<?> handleException(final HttpMessageNotWritableException ex, final WebRequest request) {
        return handle(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<?> handleException(final HttpRequestMethodNotSupportedException ex, final WebRequest request) {
        return handle(ex, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<?> handleException(final MethodArgumentNotValidException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public final ResponseEntity<?> handleException(final MissingPathVariableException ex, final WebRequest request) {
        return handle(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ResponseEntity<?> handleException(final MissingServletRequestParameterException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public final ResponseEntity<?> handleException(final MissingServletRequestPartException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public final ResponseEntity<?> handleException(final TypeMismatchException ex, final WebRequest request) {
        return handle(ex, HttpStatus.BAD_REQUEST, request);
    }

    public final ResponseEntity<?> handle(final Exception ex, final HttpStatus status, final WebRequest request) {
        return handle(ex,1,status,request,"");
    }

    public ResponseEntity<?> handle(final Exception ex, int code, final HttpStatus status, final WebRequest request, final String extDetail) {
        String alarmMsg = RestUtils.getRequestLogInfo(ex,status,code, request,extDetail);
        //外部错误日志用debug级别
        HttpStatus retStatus = getStatus(status, ex);
        if (logDebugLevel(retStatus, ex)) {
            BADREQ_LOGGER.debug(alarmMsg, ex);
        } else {
            LOGGER.warn(alarmMsg, ex);
        }
        request.setAttribute("-restapi-error-logged","true", WebRequest.SCOPE_REQUEST);
        final String reqid = (String) request.getAttribute("restapi-requestid", WebRequest.SCOPE_REQUEST);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        String body = reqid==null ? RestUtils.createError(code, ex.getMessage()) : RestUtils.createError(code, ex.getMessage(), reqid);
        return new ResponseEntity<Object>(body, headers, retStatus);
    }

    public static boolean logDebugLevel(HttpStatus status, Throwable ex) {
        int value = status.value();
        return logDebugLevel(value, ex);
    }

    public static boolean logDebugLevel(int value, Throwable ex) {
        if (value >= 400 && value < 500) {
            return true;
        }
        if (ex instanceof RestException) {
            RestException r = (RestException)ex;
            if ("debug".equalsIgnoreCase(r.getLabel())) {
                return true;
            }
        } else if (ex instanceof NestedServletException && ex.getCause() instanceof HttpMessageNotReadableException) {
            return true;
        }
        return false;
    }

    public static HttpStatus getStatus(HttpStatus status, Throwable ex) {
        if ("org.apache.catalina.connector.ClientAbortException".equals(ex.getClass().getName())) {
            return HttpStatus.valueOf(400);
        } else {
            return status;
        }
    }

    public static void logRequestException(HttpStatus status, Throwable ex, HttpServletRequest request) {
        String alarmMsg = RestUtils.getRequestLogInfo(ex,status,request,"");
        //外部错误日志用debug级别
        HttpStatus retStatus = getStatus(status, ex);
        if (logDebugLevel(retStatus, ex)) {
            BADREQ_LOGGER.debug(alarmMsg, ex);
        } else {
            LOGGER.warn(alarmMsg, ex);
        }
    }
}
