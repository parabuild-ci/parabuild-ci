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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans._BeansAPI;

/**
 * Gets/creates a {@link DefaultObjectWrapper} singleton instance that's already configured as specified in the
 * properties of this object; this is recommended over using the {@link DefaultObjectWrapper} constructors. The returned
 * instance can't be further configured (it's write protected).
 * 
 * <p>See {@link BeansWrapperBuilder} for more info, as that works identically. 
 * 
 * @since 2.3.21
 */
public class DefaultObjectWrapperBuilder extends DefaultObjectWrapperConfiguration {

    private final static Map<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>>
            INSTANCE_CACHE = new WeakHashMap<>();
    private final static ReferenceQueue<DefaultObjectWrapper> INSTANCE_CACHE_REF_QUEUE
            = new ReferenceQueue<>();
    
    /**
     * Creates a builder that creates a {@link DefaultObjectWrapper} with the given {@code incompatibleImprovements};
     * using at least 2.3.22 is highly recommended. See {@link DefaultObjectWrapper#DefaultObjectWrapper(Version)} for
     * more information about the impact of {@code incompatibleImprovements} values.
     */
    public DefaultObjectWrapperBuilder(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    /** For unit testing only */
    static void clearInstanceCache() {
        synchronized (INSTANCE_CACHE) {
            INSTANCE_CACHE.clear();
        }
    }
    
    /**
     * Returns a {@link DefaultObjectWrapper} instance that matches the settings of this builder. This will be possibly
     * a singleton that is also in use elsewhere. 
     */
    public DefaultObjectWrapper build() {
        return _BeansAPI.getBeansWrapperSubclassSingleton(
                this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, DefaultObjectWrapperFactory.INSTANCE);
    }
    
    private static class DefaultObjectWrapperFactory
        implements _BeansAPI._BeansWrapperSubclassFactory<DefaultObjectWrapper, DefaultObjectWrapperConfiguration> {
    
        private static final DefaultObjectWrapperFactory INSTANCE = new DefaultObjectWrapperFactory(); 
        
        @Override
        public DefaultObjectWrapper create(DefaultObjectWrapperConfiguration bwConf) {
            return new DefaultObjectWrapper(bwConf, true);
        }
    }

}
