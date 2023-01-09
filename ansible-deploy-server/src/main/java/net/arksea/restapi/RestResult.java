package net.arksea.restapi;

import javax.servlet.ServletRequest;

/**
 *
 * Created by xiaohaixing on 2018/5/24.
 */
public class RestResult<T> extends BaseResult {
    public final T result;

    public RestResult(T result) {
        super(0);
        this.result = result;
    }

    public RestResult(T result, String reqid) {
        super(0, reqid);
        this.result = result;
    }

    public RestResult(int code, T result, String reqid) {
        super(code, reqid);
        this.result = result;
    }

    public RestResult(int code, T result, ServletRequest httpRequest) {
        super(code, httpRequest);
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
