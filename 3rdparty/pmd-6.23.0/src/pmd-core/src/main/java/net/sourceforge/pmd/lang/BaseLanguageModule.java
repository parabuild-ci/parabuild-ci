/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Created by christoferdutz on 21.09.14.
 */
public abstract class BaseLanguageModule implements Language {

    protected String name;
    protected String shortName;
    protected String terseName;
    protected Class<?> ruleChainVisitorClass;
    protected List<String> extensions;
    private final List<LanguageVersion> distinctVersions = new ArrayList<>();
    protected Map<String, LanguageVersion> versions;
    protected LanguageVersion defaultVersion;

    public BaseLanguageModule(String name, String shortName, String terseName, Class<?> ruleChainVisitorClass,
            String... extensions) {
        this.name = name;
        this.shortName = shortName;
        this.terseName = terseName;
        this.ruleChainVisitorClass = ruleChainVisitorClass;
        this.extensions = Arrays.asList(extensions);
    }

    @Experimental
    protected void addVersions(LanguageVersionHandler languageVersionHandler, boolean isDefault, String ... languageVersions) {
        if (versions == null) {
            versions = new HashMap<>();
        }

        LanguageVersion languageVersion = new LanguageVersion(this, languageVersions[0], languageVersionHandler);

        distinctVersions.add(languageVersion);

        for (String version : languageVersions) {
            versions.put(version, languageVersion);
        }

        if (isDefault) {
            assert defaultVersion == null
                : "Default version already set to " + defaultVersion + ", cannot set it to " + languageVersion;
            defaultVersion = languageVersion;
        }
    }

    protected void addVersion(String version, LanguageVersionHandler languageVersionHandler, boolean isDefault) {
        addVersions(languageVersionHandler, isDefault, version);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortName() {
        return (shortName != null) ? shortName : name;
    }

    @Override
    public String getTerseName() {
        return terseName;
    }

    @Override
    public Class<?> getRuleChainVisitorClass() {
        return ruleChainVisitorClass;
    }

    @Override
    public List<String> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    @Override
    public boolean hasExtension(String extension) {
        return extensions != null && extensions.contains(extension);
    }

    @Override
    public List<LanguageVersion> getVersions() {
        return new ArrayList<>(distinctVersions);
    }

    @Override
    public boolean hasVersion(String version) {
        return versions != null && versions.containsKey(version);
    }

    @Override
    public LanguageVersion getVersion(String versionName) {
        if (versions != null) {
            return versions.get(versionName);
        }
        return null;
    }

    @Override
    public LanguageVersion getDefaultVersion() {
        assert defaultVersion != null : "Null default version for language " + this;
        return defaultVersion;
    }

    @Override
    public String toString() {
        return "LanguageModule:" + name + '(' + this.getClass().getSimpleName() + ')';
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BaseLanguageModule)) {
            return false;
        }
        BaseLanguageModule other = (BaseLanguageModule) obj;
        return name.equals(other.name);
    }

    @Override
    public int compareTo(Language o) {
        return getName().compareTo(o.getName());
    }
}
