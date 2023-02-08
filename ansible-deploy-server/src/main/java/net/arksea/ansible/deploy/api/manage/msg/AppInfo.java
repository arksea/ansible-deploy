package net.arksea.ansible.deploy.api.manage.msg;

import java.io.Serializable;

public class AppInfo implements Serializable {
    private static final long serialVersionUID = 6756425510537952712L;
    public final Long id;
    public final String apptag;
    public final  Long appTypeId;

    public AppInfo(Long id, String apptag, Long appTypeId) {
        this.id = id;
        this.apptag = apptag;
        this.appTypeId = appTypeId;
    }
}
