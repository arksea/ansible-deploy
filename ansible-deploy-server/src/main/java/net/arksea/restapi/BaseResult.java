package net.arksea.restapi;

import javax.servlet.ServletRequest;
import java.util.UUID;

/**
 *
 * Created by xiaohaixing on 2019/5/23.
 */
public class BaseResult {
    public final int code;
    public final String reqid;

    public BaseResult(int code) {
        this.code = code;
        this.reqid = UUID.randomUUID().toString();
    }

    public BaseResult(int code, String reqid) {
        this.code = code;
        this.reqid = reqid;
    }

    public BaseResult(int code, ServletRequest httpRequest) {
        String reqid = (String)httpRequest.getAttribute("restapi-requestid");
        this.code = code;
        this.reqid = reqid;
    }

    public int getCode() {
        return code;
    }

    public String getReqid() {
        return reqid;
    }
}
