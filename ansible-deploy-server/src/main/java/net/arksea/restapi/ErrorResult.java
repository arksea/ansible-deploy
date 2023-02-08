package net.arksea.restapi;

import javax.servlet.ServletRequest;

/**
 *
 * Created by xiaohaixing on 2019/5/23.
 */
public class ErrorResult extends BaseResult {
    public final String error;

    public ErrorResult(int code, String reqid, String error) {
        super(code, reqid);
        this.error = error;
    }

    public ErrorResult(int code, ServletRequest httpRequest, String error) {
        super(code, httpRequest);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
