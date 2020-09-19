/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.ext.beans;

import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDateModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;

/**
 * Holds {@link BeansWrapper} configuration settings and defines their defaults.
 * You will not use this abstract class directly, but concrete subclasses like {@link BeansWrapperBuilder} and
 * {@link DefaultObjectWrapperBuilder}. Unless, you are developing a builder for a custom {@link BeansWrapper} subclass.
 * 
 * <p>This class is designed so that its instances can be used as lookup keys in a singleton cache. This is also why
 * this class defines the configuration setting defaults for {@link BeansWrapper}, instead of leaving that to
 * {@link BeansWrapper} itself. (Because, the default values influence the lookup key, and the singleton needs to be
 * looked up without creating a {@link BeansWrapper} instance.) However, because instances are mutable, you should
 * deep-clone it with {@link #clone(boolean)} before using it as cache key.
 * 
 * @since 2.3.21
 */
public abstract class BeansWrapperConfiguration implements Cloneable {

    private final Version incompatibleImprovements;
    
    private ClassIntrospectorBuilder classIntrospectorBuilder;
    
    // Properties and their *defaults*:
    private boolean simpleMapWrapper = false;
    private boolean preferIndexedReadMethod;
    private int defaultDateType = TemplateDateModel.UNKNOWN;
    private ObjectWrapper outerIdentity = null;
    private boolean strict = false;
    private boolean useModelCache = false;
    // Attention!
    // - As this object is a cache key, non-normalized field values should be avoided.
    // - Fields with default values must be set until the end of the constructor to ensure that when the lookup happens,
    //   there will be no unset fields.
    // - If you add a new field, review all methods in this class
    
    /**
     * @param incompatibleImprovements
     *            See the corresponding parameter of {@link BeansWrapper#BeansWrapper(Version)}. Not {@code null}. Note
     *            that the version will be normalized to the lowest version where the same incompatible
     *            {@link BeansWrapper} improvements were already present, so for the returned instance
     *            {@link #getIncompatibleImprovements()} might returns a lower version than what you have specified
     *            here.
     * @param isIncompImprsAlreadyNormalized
     *            Tells if the {@code incompatibleImprovements} parameter contains an <em>already normalized</em> value.
     *            This parameter meant to be {@code true} when the class that extends {@link BeansWrapper} needs to add
     *            additional breaking versions over those of {@link BeansWrapper}. Thus, if this parameter is
     *            {@code true}, the versions where {@link BeansWrapper} had breaking changes must be already factored
     *            into the {@code incompatibleImprovements} parameter value, as no more normalization will happen. (You
     *            can use {@link BeansWrapper#normalizeIncompatibleImprovementsVersion(Version)} to discover those.)
     * 
     * @since 2.3.22
     */
    protected BeansWrapperConfiguration(Version incompatibleImprovements, boolean isIncompImprsAlreadyNormalized) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);

        // We can't do this in the BeansWrapper constructor, as by that time the version is normalized.
        if (!isIncompImprsAlreadyNormalized) {
            _TemplateAPI.checkCurrentVersionNotRecycled(
                    incompatibleImprovements,
                    "freemarker.beans", "BeansWrapper");
        }
        
        incompatibleImprovements = isIncompImprsAlreadyNormalized
                ? incompatibleImprovements
                : BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        this.incompatibleImprovements = incompatibleImprovements;
        
        preferIndexedReadMethod = incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_27;
        
        classIntrospectorBuilder = new ClassIntrospectorBuilder(incompatibleImprovements);
    }
    
    /**
     * Same as {@link #BeansWrapperConfiguration(Version, boolean) BeansWrapperConfiguration(Version, false)}.
     */
    protected BeansWrapperConfiguration(Version incompatibleImprovements) {
        this(incompatibleImprovements, false);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + incompatibleImprovements.hashCode();
        result = prime * result + (simpleMapWrapper ? 1231 : 1237);
        result = prime * result + (preferIndexedReadMethod ? 1231 : 1237);
        result = prime * result + defaultDateType;
        result = prime * result + (outerIdentity != null ? outerIdentity.hashCode() : 0);
        result = prime * result + (strict ? 1231 : 1237);
        result = prime * result + (useModelCache ? 1231 : 1237);
        result = prime * result + classIntrospectorBuilder.hashCode();
        return result;
    }

    /**
     * Two {@link BeansWrapperConfiguration}-s are equal exactly if their classes are identical ({@code ==}), and their
     * field values are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BeansWrapperConfiguration other = (BeansWrapperConfiguration) obj;
        
        if (!incompatibleImprovements.equals(other.incompatibleImprovements)) return false;
        if (simpleMapWrapper != other.simpleMapWrapper) return false;
        if (preferIndexedReadMethod != other.preferIndexedReadMethod) return false;
        if (defaultDateType != other.defaultDateType) return false;
        if (outerIdentity != other.outerIdentity) return false;
        if (strict != other.strict) return false;
        if (useModelCache != other.useModelCache) return false;
        if (!classIntrospectorBuilder.equals(other.classIntrospectorBuilder)) return false;
        
        return true;
    }
    
    protected Object clone(boolean deepCloneKey) {
        try {
            BeansWrapperConfiguration clone = (BeansWrapperConfiguration) super.clone();
            if (deepCloneKey) {
                clone.classIntrospectorBuilder
                        = (ClassIntrospectorBuilder) classIntrospectorBuilder.clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone BeansWrapperConfiguration", e);
        }
    }
    
    public boolean isSimpleMapWrapper() {
        return simpleMapWrapper;
    }

    /** See {@link BeansWrapper#setSimpleMapWrapper(boolean)}. */
    public void setSimpleMapWrapper(boolean simpleMapWrapper) {
        this.simpleMapWrapper = simpleMapWrapper;
    }
    
    /** @since 2.3.27 */
    public boolean getPreferIndexedReadMethod() {
        return preferIndexedReadMethod;
    }

    /** See {@link BeansWrapper#setPreferIndexedReadMethod(boolean)}. @since 2.3.27 */
    public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
        this.preferIndexedReadMethod = preferIndexedReadMethod;
    }

    public int getDefaultDateType() {
        return defaultDateType;
    }

    /** See {@link BeansWrapper#setDefaultDateType(int)}. */
    public void setDefaultDateType(int defaultDateType) {
        this.defaultDateType = defaultDateType;
    }

    public ObjectWrapper getOuterIdentity() {
        return outerIdentity;
    }

    /**
     * See {@link BeansWrapper#setOuterIdentity(ObjectWrapper)}, except here the default is {@code null} that means
     * the {@link ObjectWrapper} that you will set up with this {@link BeansWrapperBuilder} object.
     */
    public void setOuterIdentity(ObjectWrapper outerIdentity) {
        this.outerIdentity = outerIdentity;
    }

    public boolean isStrict() {
        return strict;
    }

    /** See {@link BeansWrapper#setStrict(boolean)}. */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean getUseModelCache() {
        return useModelCache;
    }

    /** See {@link BeansWrapper#setUseCache(boolean)} (it means the same). */
    public void setUseModelCache(boolean useModelCache) {
        this.useModelCache = useModelCache;
    }

    public Version getIncompatibleImprovements() {
        return incompatibleImprovements;
    }
    
    public int getExposureLevel() {
        return classIntrospectorBuilder.getExposureLevel();
    }

    /** See {@link BeansWrapper#setExposureLevel(int)}. */
    public void setExposureLevel(int exposureLevel) {
        classIntrospectorBuilder.setExposureLevel(exposureLevel);
    }

    public boolean getExposeFields() {
        return classIntrospectorBuilder.getExposeFields();
    }

    /** See {@link BeansWrapper#setExposeFields(boolean)}. */
    public void setExposeFields(boolean exposeFields) {
        classIntrospectorBuilder.setExposeFields(exposeFields);
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return classIntrospectorBuilder.getMemberAccessPolicy();
    }

    /** See {@link BeansWrapper#setMemberAccessPolicy(MemberAccessPolicy)}. */
    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        classIntrospectorBuilder.setMemberAccessPolicy(memberAccessPolicy);
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return classIntrospectorBuilder.getTreatDefaultMethodsAsBeanMembers();
    }

    /** See {@link BeansWrapper#setTreatDefaultMethodsAsBeanMembers(boolean)} */
    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        classIntrospectorBuilder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return classIntrospectorBuilder.getMethodAppearanceFineTuner();
    }

    /**
     * See {@link BeansWrapper#setMethodAppearanceFineTuner(MethodAppearanceFineTuner)}; additionally,
     * note that currently setting this to non-{@code null} will disable class introspection cache sharing, unless
     * the value implements {@link SingletonCustomizer}.
     */
    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        classIntrospectorBuilder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
    }

    MethodSorter getMethodSorter() {
        return classIntrospectorBuilder.getMethodSorter();
    }

    void setMethodSorter(MethodSorter methodSorter) {
        classIntrospectorBuilder.setMethodSorter(methodSorter);
    }

    ClassIntrospectorBuilder getClassIntrospectorBuilder() {
        return classIntrospectorBuilder;
    }
 
}
