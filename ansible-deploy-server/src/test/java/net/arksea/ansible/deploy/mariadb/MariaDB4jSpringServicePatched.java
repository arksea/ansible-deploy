package net.arksea.ansible.deploy.mariadb;

import ch.vorburger.exec.ManagedProcessException;
import org.springframework.beans.factory.annotation.Value;

public class MariaDB4jSpringServicePatched extends ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService {

    public final static String CHAR_SET = "mariaDB4j.charSet";
    public final static String COLLATION = "mariaDB4j.collation";

    @Value("${" + CHAR_SET + ":NA}")
    public void setDefaultCharset(String characterSet) {
        if (!"NA".equals(characterSet))
            getConfiguration().setDefaultCharacterSet(characterSet);

    }

    @Value("${" + COLLATION + ":NA}")
    public void setDefaultCollation(String collation) {
        if (!"NA".equals(collation))
            getConfiguration().addArg("--collation-server="+collation);
    }

    @Override
    public void start() { // no throws ManagedProcessException
        try {
            if (isRunning()) {
                return;
            }
            db = DBPatched.newEmbeddedDB(getConfiguration().build());
            db.start();
        } catch (ManagedProcessException e) {
            lastException = e;
            throw new IllegalStateException("MariaDB4jSpringService start() failed", e);
        }
    }

}
