package net.arksea.ansible.deploy.mariadb;

import ch.vorburger.exec.ManagedProcess;
import ch.vorburger.exec.ManagedProcessBuilder;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfiguration;

import java.io.File;
import java.io.IOException;

public class DBPatched extends DB {
    protected DBPatched(DBConfiguration config) {
        super(config);
    }

    public static DB newEmbeddedDB(DBConfiguration config) throws ManagedProcessException {
        DBPatched db = new DBPatched(config);
        db.prepareDirectories();
        db.unpackEmbeddedDb();
        db.install();
        return db;
    }

    protected ManagedProcess createDBInstallProcess() throws ManagedProcessException, IOException {
        if (!configuration.isWindows()) {
            return super.createDBInstallProcess();
        }
        File baseDir = new File(configuration.getBaseDir());
        File libDir = new File(configuration.getLibDir());
        File dataDir = new File(configuration.getDataDir());
        //File tmpDir = new File(configuration.getTmpDir());
        File installDbCmdFile = configuration.getExecutable(DBConfiguration.Executable.InstallDB);
        ManagedProcessBuilder builder = new ManagedProcessBuilder(installDbCmdFile);
        builder.setOutputStreamLogDispatcher(getOutputStreamLogDispatcher("mysql_install_db"));
        builder.getEnvironment().put(configuration.getOSLibraryEnvironmentVarName(), libDir.getAbsolutePath());
        builder.setWorkingDirectory(baseDir);
        builder.addFileArgument("--datadir", dataDir.getCanonicalFile());
        //当前版本mysql_install_db不支持--tmpdir参数
        //builder.addFileArgument("--tmpdir", tmpDir.getCanonicalFile());
        return builder.build();
    }
}
