package net.arksea.restapi;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {
	private static final long serialVersionUID = 4046774595854568532L;
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; //Http状态码
	private int code = 1; //附加(业务)错误码
	private String label = ""; //附加标记
	private String detail = "";//附加详细描述
	public RestException(final String message) {
		super(message);
	}
	public RestException(final String message, final Throwable ex) {
		super(message, ex);
	}
	public RestException(final String message, final String label) {
		super(message);
		this.label = label;
	}
	public RestException(final String message, final String label, final Throwable ex) {
		super(message, ex);
		this.label = label;
	}
	public RestException(final HttpStatus status) {
		super(status.getReasonPhrase());
		this.status = status;
	}
	public RestException(final HttpStatus status, final String message) {
		super(message);
		this.status = status;
	}
	public RestException(final HttpStatus status, final Throwable ex) {
		super(status.getReasonPhrase(), ex);
		this.status = status;
	}
	public RestException(final HttpStatus status, final String message, final String label) {
		super(message);
		this.status = status;
		this.label = label;
	}
	public RestException(final HttpStatus status, final String message, final Throwable ex) {
		super(message, ex);
		this.status = status;
	}
	public RestException(final HttpStatus status, final String message, final String label, final Throwable ex) {
		super(message, ex);
		this.status = status;
		this.label = label;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
