package com.sabre.sabresonic.mockserver.core.registry.file;

import com.sabre.sabresonic.mockserver.core.config.Config;
import com.sabre.sabresonic.mockserver.core.exception.ServiceException;
import com.sabre.sabresonic.mockserver.core.registry.ServiceRegistry;
import com.sabre.sabresonic.mockserver.core.servicegroup.FileServiceGroup;
import com.sabre.sabresonic.mockserver.core.servicegroup.ServiceGroup;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukasz.Kwiatkowski@sabre.com (SG0218182)
 */
public class FileServicesProvider {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FileServicesProvider.class);
    private File servicesDirectory;
    private ServiceRegistry serviceRegistry;
    private long pollingInterval = 5 * 1000;

    public FileServicesProvider(final ServiceRegistry serviceRegistry) {
        this(serviceRegistry, new File(Config.getServicePath()));
    }

    public FileServicesProvider(final ServiceRegistry serviceRegistry, final File servicesDirectory) {
        this.serviceRegistry = serviceRegistry;
        this.servicesDirectory = servicesDirectory;
        try {
            FileUtils.forceMkdir(servicesDirectory);
        } catch (IOException ex) {
            LOG.error(null, ex);
        }
        initialize();
    }

    private void initialize() {
        try {
            final FileAlterationObserver observer = new FileAlterationObserver(this.servicesDirectory) {
                @Override
                public void checkAndNotify() {
                    LOG.debug("Checking services directory for changes. " + servicesDirectory);
                    super.checkAndNotify();
                }
            };

            final FileAlterationMonitor monitor = new FileAlterationMonitor(this.pollingInterval);
            monitor.addObserver(observer);
            observer.addListener(new FileChangeListener() {
//                @Override
//                public void onStart(final FileAlterationObserver observer) {
//                    
//                }

                @Override
                public void onDirectoryCreate(final File directory) {
                    LOG.debug("Directory created: " + directory.getPath());
                }

                @Override
                public void onDirectoryChange(final File directory) {
                    LOG.debug("Directory changed: " + directory.getPath());
                }

                @Override
                public void onDirectoryDelete(final File directory) {
                    LOG.debug("Directory deleted: " + directory.getPath());
                }

                @Override
                public void onFileCreate(final File file) {
                    addService(file);
                    LOG.debug("File created: " + file.getPath());
                }

                @Override
                public void onFileChange(final File file) {
                    LOG.debug("File changed: " + file.getPath());
                    addService(file);
                }

                @Override
                public void onFileDelete(final File file) {
                    LOG.debug("File removed: " + file.getPath());
                    removeService(file);
                }
            });
            loadAllServices();
            monitor.start();
//            Thread.sleep(1000);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    protected void loadAllServices() {
        for (File file : FileUtils.listFiles(this.servicesDirectory, null, true)) {
            try {
                addService(file);
            } catch (Exception ex) {
                LOG.warn(null, ex);
            }
        }
    }

    protected void addService(final File file) {
        try {
            if (file.isFile()) {
                ServiceGroup serviceGroup = new FileServiceGroup(createServiceName(file), file);
                serviceRegistry.put(serviceGroup.getName(), serviceGroup);
            }
        } catch (Exception ex) {
            LOG.warn("Can not create service from file: " + file.getPath(), ex);
        }
    }

    protected void removeService(final File removedFile) {
        try {
            serviceRegistry.remove(createServiceName(removedFile));
            LOG.debug("Removed file: " + removedFile.getPath());
        } catch (Exception ex) {
            LOG.warn("Can not remove service from registry: " + createServiceName(removedFile), ex);
        }
    }

    protected String createServiceName(final File file) {
        String servicePath = new File(Config.getServicePath()).getAbsolutePath();
        String path = file.getAbsolutePath().replace(servicePath, "");
        return FilenameUtils.separatorsToUnix(path);
    }
}
