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

package freemarker.template;

import freemarker.ext.beans.BeansWrapperConfiguration;

/**
 * Holds {@link DefaultObjectWrapper} configuration settings and defines their defaults.
 * You will not use this abstract class directly, but concrete subclasses like {@link DefaultObjectWrapperBuilder}.
 * Unless, you are developing a builder for a custom {@link DefaultObjectWrapper} subclass. In that case, note that
 * overriding the {@link #equals} and {@link #hashCode} is important, as these objects are used as {@link ObjectWrapper}
 * singleton lookup keys.
 * 
 * @since 2.3.22
 */
public abstract class DefaultObjectWrapperConfiguration extends BeansWrapperConfiguration {
    
    private boolean useAdaptersForContainers;
    private boolean forceLegacyNonListCollections;
    private boolean iterableSupport;

    protected DefaultObjectWrapperConfiguration(Version incompatibleImprovements) {
        super(DefaultObjectWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements), true);
        _TemplateAPI.checkCurrentVersionNotRecycled(
                incompatibleImprovements,
                "freemarker.configuration", "DefaultObjectWrapper");
        useAdaptersForContainers = getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22;
        forceLegacyNonListCollections = true; // [2.4]: = IcI < _TemplateAPI.VERSION_INT_2_4_0;
    }

    /** See {@link DefaultObjectWrapper#getUseAdaptersForContainers()}. */
    public boolean getUseAdaptersForContainers() {
        return useAdaptersForContainers;
    }

    /** See {@link DefaultObjectWrapper#setUseAdaptersForContainers(boolean)}. */
    public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
        this.useAdaptersForContainers = useAdaptersForContainers;
    }
    
    /** See {@link DefaultObjectWrapper#getForceLegacyNonListCollections()}. */
    public boolean getForceLegacyNonListCollections() {
        return forceLegacyNonListCollections;
    }

    /** See {@link DefaultObjectWrapper#setForceLegacyNonListCollections(boolean)}. */
    public void setForceLegacyNonListCollections(boolean legacyNonListCollectionWrapping) {
        this.forceLegacyNonListCollections = legacyNonListCollectionWrapping;
    }

    /**
     * See {@link DefaultObjectWrapper#getIterableSupport()}.
     * 
     * @since 2.3.25 
     */
    public boolean getIterableSupport() {
        return iterableSupport;
    }

    /**
     * See {@link DefaultObjectWrapper#setIterableSupport(boolean)}.
     * 
     * @since 2.3.25 
     */
    public void setIterableSupport(boolean iterableSupport) {
        this.iterableSupport = iterableSupport;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        final int prime = 31;
        result = result * prime + (useAdaptersForContainers ? 1231 : 1237);
        result = result * prime + (forceLegacyNonListCollections ? 1231 : 1237);
        result = result * prime + (iterableSupport ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (!super.equals(that)) return false;
        final DefaultObjectWrapperConfiguration thatDowCfg = (DefaultObjectWrapperConfiguration) that;
        return useAdaptersForContainers == thatDowCfg.getUseAdaptersForContainers()
                && forceLegacyNonListCollections == thatDowCfg.forceLegacyNonListCollections
                && iterableSupport == thatDowCfg.iterableSupport;
    }

}
