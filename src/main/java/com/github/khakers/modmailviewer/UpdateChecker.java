package com.github.khakers.modmailviewer;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static final Logger logger = LogManager.getLogger();

    private static final String REPO_OWNER = "khakers";
    private static final String REPO_NAME = "modmail-viewer";

    private static final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static boolean isDockerContainer() {
        return false;
    }

    public boolean isSemVerUpdateAvailable(String version) {
        Request request = new Request.Builder()
                .url(String.format("https://api.github.com/repos/%s/%s/releases", REPO_OWNER, REPO_NAME))
                .header("accept", "application/vnd.github+json")
                .get()
                .build();
        logger.trace("request: {}", request.toString());


        try (Response response = client.newCall(request).execute()) {
            logger.debug("Got github release data with status {}", response.code());

            assert response.body() != null;
            var body = response.body().string();


            var releases = mapper.readTree(body);

            var gitTag = releases
                    .get(0)
                    .get("tag_name")
                    .asText();
            var latestVersion = new Version(gitTag);
            var currentVersion = new Version(version);
            logger.debug("found version {} from github API. Current version is {}", latestVersion, currentVersion);


            if (currentVersion.compareTo(latestVersion) > 0) {
                var url = releases.get(0).get("html_url");
                logger.warn("An update is available! Version v{} can be downloaded at {}. Out of date versions are not supported.", latestVersion.asVersionString(), url);
                return true;
            }


        } catch (IOException e) {
            logger.error(e);
            return false;
        }
        return false;
    }


    public boolean isUpdateAvailable() {
        if (ModmailViewer.isSemVerRelease()) {
            return isSemVerUpdateAvailable(ModmailViewer.TAG);
        } else if (ModmailViewer.BRANCH.equalsIgnoreCase("develop")) {
            logger.info("You're running on a development build.");
        }
        return false;
    }


}

class Version implements Comparable<Version> {

    private static final Pattern SEMVER_PATTERN = Pattern.compile("^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");
    int major;
    int minor;
    int patch;
    @Nullable
    String prerelease;
    @Nullable
    String metaData;

    public Version(@NotNull String prerelease) {
        var matcher = SEMVER_PATTERN.matcher(prerelease);
        matcher.find();
        this.major = Integer.parseInt(matcher.group("major"));
        this.minor = Integer.parseInt(matcher.group("minor"));
        this.patch = Integer.parseInt(matcher.group("patch"));
        this.prerelease = matcher.group("prerelease");
        this.metaData = matcher.group("buildmetadata");

    }

    public String asVersionString() {
        var string = new StringBuilder();
        string
                .append(this.major)
                .append('.')
                .append(this.minor)
                .append('.')
                .append(this.patch);
        if (this.prerelease != null && !this.prerelease.isBlank()) {
            string.append("-").append(this.prerelease);
        }

        if (this.metaData != null && !this.metaData.isBlank()) {
            string.append("-").append(this.metaData);
        }
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch && Objects.equals(prerelease, version.prerelease) && Objects.equals(metaData, version.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prerelease, metaData, major, minor, patch);
    }

    @Override
    public String toString() {
        return "Version{" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", versionString='" + prerelease + '\'' +
                ", metaData='" + metaData + '\'' +
                '}';
    }

    /**
     *
     * Does not calculate PRE-RELEASE versions
     * <p>
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(@NotNull Version version) {

        if (this.equals(version))
            return 0;

        if (version.major > this.major)
            return 1;
        else if (version.major < this.major) {
            return -1;
        }

        if (version.minor > this.minor)
            return 1;
        else if (version.minor < this.minor) {
            return -1;
        }

        if (version.patch > this.patch)
            return 1;
        else if (version.patch < this.patch) {
            return-1;
        }

        if (version.prerelease != null && this.prerelease != null) {
            return version.prerelease.compareTo(this.prerelease);
        }

        return 0;
    }
}

