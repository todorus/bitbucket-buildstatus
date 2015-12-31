package com.todorus.bitbucketbuildstatus;
import com.todorus.bitbucketbuildstatus.control.BuildStatusClient;
import com.todorus.bitbucketbuildstatus.control.BuildStatusController;
import com.todorus.bitbucketbuildstatus.control.RetrofitAdapter;
import com.todorus.bitbucketbuildstatus.model.BuildStatus;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.*;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.BuildData;
import hudson.tasks.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link BuildStatusNotifier} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #owner})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class BuildStatusNotifier extends Notifier {

    private static final String TAG = "Bitbucket BuildStatus: ";

    private final String owner;
    private final String slug;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public BuildStatusNotifier(String owner, String slug) {
        this.owner = owner;
        this.slug = slug;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        BuildData buildData = build.getAction(BuildData.class);
        if (buildData == null) {
            logger.println(TAG+"Could not get build data from build.");
            return false;
        }

        Revision rev = buildData.getLastBuiltRevision();
        if (rev == null) {
            logger.println(TAG+"Could not get revision from build.");
            return false;
        }

        String commitHash = rev.getSha1String();
        if (commitHash == null) {
            logger.println(TAG+"Could not get commit hash from build data.");
            return false;
        }

        BuildStatus buildStatus = new BuildStatus.Builder()
                .setRootUrl(Jenkins.getInstance().getRootUrl())
                .setBuild(build)
                .build();

        BuildStatusClient client = RetrofitAdapter.getAdapter().create(BuildStatusClient.class);
        RetrofitAdapter.setUsername(getDescriptor().getUsername());
        RetrofitAdapter.setPassword(getDescriptor().getPassword());
        BuildStatusController controller = new BuildStatusController(client);

        String eOwner = build.getEnvironment(listener).expand(owner);
        String eSlug  = build.getEnvironment(listener).expand(slug);

        try {
            controller.postStatus(buildStatus, eOwner, eSlug, commitHash);
        } catch (Exception e){
            logger.println(TAG+"could not post status to BitBucket");
            logger.println(e);
            return false;
        }


        return true;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getSlug() {
        return slug;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        // Let the plugin run after the status of the build is finalized, else it will post a BuildStatus.STATUS_IN_PROGRESS state
        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String username;

        private String password;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Post Build status to Bitbucket";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            username = formData.getString("user");
            password = formData.getString("password");

            save();

            return super.configure(req, formData);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

    }
}

