package net.arksea.ansible.deploy.mariadb;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class MariaDB4jSpringService extends ch.vorburger.mariadb4j.MariaDB4jService {

    private final static Logger logger = LogManager.getLogger(MariaDB4jSpringService.class);

    public final static String PORT = "mariaDB4j.port";
    public final static String SOCKET = "mariaDB4j.socket";
    public final static String DATA_DIR = "mariaDB4j.dataDir";
    public final static String TMP_DIR = "mariaDB4j.tmpDir";
    public final static String BASE_DIR = "mariaDB4j.baseDir";
    public final static String LIB_DIR = "mariaDB4j.libDir";
    public final static String UNPACK = "mariaDB4j.unpack";
    public final static String OS_USER = "mariaDB4j.osUser";
    public final static String CHAR_SET = "mariaDB4j.charSet";
    public final static String COLLATION = "mariaDB4j.collation";

    protected Exception lastException;

    @Value("${" + PORT + ":-1}")
    int port;

    @Value("${" + SOCKET + ":NA}")
    String socket;

    @Value("${" + DATA_DIR + ":NA}")
    String dataDir;

    @Value("${" + TMP_DIR + ":NA}")
    String tmpDir;

    @Value("${" + BASE_DIR + ":NA}")
    String baseDir;

    @Value("${" + LIB_DIR + ":NA}")
    String libDir;

    @Value("${" + UNPACK + ":#{null}}")
    Boolean unpack;

    @Value("${" + OS_USER + ":NA}")
    String osUser;

    @Value("${" + CHAR_SET + ":NA}")
    String charSet;

    @Value("${" + COLLATION + ":NA}")
    String collation;

    @Override
    public void start() { // no throws ManagedProcessException
        logger.info("MariaDB4jSpringService.start()");
        try {
            if (isRunning()) {
                return;
            }
            logger.info("do MariaDB4jSpringService.start()");
            DBConfigurationBuilder cfg = getConfiguration();
            if (port != -1)
                cfg.setPort(port);
            if (!"NA".equals(socket))
                cfg.setSocket(socket);
            if (!"NA".equals(dataDir))
                cfg.setDataDir(dataDir);
            if (!"NA".equals(tmpDir))
                cfg.setTmpDir(tmpDir);
            if (!"NA".equals(baseDir))
                cfg.setBaseDir(baseDir);
            if (!"NA".equals(libDir))
                cfg.setLibDir(libDir);
            if (unpack != null)
                cfg.setUnpackingFromClasspath(unpack);
            if (!"NA".equals(osUser))
                cfg.addArg("--user=" + osUser);
            if (!"NA".equals(charSet))
                cfg.setDefaultCharacterSet(charSet);
            if (!"NA".equals(collation))
                cfg.addArg("--collation-server="+collation);
            db = DBPatched.newEmbeddedDB(cfg.build());
            db.start();
        } catch (ManagedProcessException e) {
            lastException = e;
            throw new IllegalStateException("MariaDB4jSpringService start() failed", e);
        }
    }

    @Override
    public void stop() {
        logger.info("MariaDB4jSpringService.stop()");
        try {
            super.stop();
        } catch (Exception e) {
            lastException = e;
            throw new IllegalStateException("MariaDB4jSpringService stop() failed", e);
        }
    }

}
