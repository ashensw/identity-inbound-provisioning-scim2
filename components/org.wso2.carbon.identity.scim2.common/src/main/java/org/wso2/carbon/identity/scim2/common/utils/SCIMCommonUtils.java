/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.scim2.common.utils;

import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.util.UserCoreUtil;

/**
 * This class is to be used as a Util class for SCIM common things.
 * TODO:rename class name.
 */
public class SCIMCommonUtils {

    /**
     * Since we need perform provisioning through UserOperationEventListener implementation -
     * SCIMUserOperationListener- there can be cases where multiple methods in the listener are
     * called for same operation - such as when adding a user with claims, both postAddUserListener
     * as well as setClaimValuesListener are called. But we do not need setClaimValuesLister to be
     * called at user creation - it is supposed to do provisioning at user update. So we make use of
     * this thread local variable to skip the second lister.
     */
    private static ThreadLocal<Boolean> threadLocalToSkipSetUserClaimsListeners = new ThreadLocal<>();
    /**
     * Provisioning to other providers is initiated at SCIMUserOperationListener which is invoked
     * by UserStoreManager. It doesn't have any clue about through which path the user management operation
     * came. If it came through SCIMEndPoint, we treat it differently when deciding SCIMConsumerId.
     * Therefore we need this thread local to signal the SCIMUserOperationListener to take the decision.
     */
    private static ThreadLocal<Boolean> threadLocalIsManagedThroughSCIMEP = new ThreadLocal<>();

    public static String getSCIMUserURL(String id) {
        return getSCIMUserURL() + "/" + id;
    }

    public static String getSCIMGroupURL(String id) {
        return getSCIMGroupURL() + "/" + id;
    }

    public static String getSCIMServiceProviderConfigURL(String id){
        return getSCIMServiceProviderConfigURL() ;
    }

    /*Handling ThreadLocals*/

    public static String getSCIMUserURL() {
        String scimURL = IdentityUtil.getServerURL(SCIMCommonConstants.SCIM2_ENDPOINT, true, true);
        String scimUserLocation = scimURL + SCIMCommonConstants.USERS;
        return scimUserLocation;
    }

    public static String getSCIMGroupURL() {
        String scimURL = IdentityUtil.getServerURL(SCIMCommonConstants.SCIM2_ENDPOINT, true, true);
        String scimGroupLocation = scimURL + SCIMCommonConstants.GROUPS;
        return scimGroupLocation;
    }

    public static String getSCIMServiceProviderConfigURL() {
        String scimURL = IdentityUtil.getServerURL(SCIMCommonConstants.SCIM2_ENDPOINT, true, true);
        String scimServiceProviderConfig = scimURL + SCIMCommonConstants.SERVICE_PROVIDER_CONFIG;
        return scimServiceProviderConfig;
    }

    public static String getSCIMResourceTypeURL() {
        String scimURL = IdentityUtil.getServerURL(SCIMCommonConstants.SCIM2_ENDPOINT, true, true);
        String scimResourceType = scimURL + SCIMCommonConstants.RESOURCE_TYPE;
        return scimResourceType;
    }

    public static String getGroupNameWithDomain(String groupName) {

        if (groupName == null) {
            return null;
        }

        if (groupName.indexOf(CarbonConstants.DOMAIN_SEPARATOR) > 0) {
            return groupName;
        } else {
            return UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME
                    + CarbonConstants.DOMAIN_SEPARATOR + groupName;
        }
    }

    public static String getPrimaryFreeGroupName(String groupName) {

        if (groupName == null) {
            return null;
        }

        int index = groupName.indexOf(CarbonConstants.DOMAIN_SEPARATOR);

        // Check whether we have a secondary UserStoreManager setup.
        if (index > 0) {
            // Using the short-circuit. User name comes with the domain name.
            String domain = groupName.substring(0, index);
            if (UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME.equals(domain)) {
                return groupName.substring(index + 1);
            }
        }
        return groupName;
    }

    public static void unsetThreadLocalToSkipSetUserClaimsListeners() {
        threadLocalToSkipSetUserClaimsListeners.remove();
    }

    public static Boolean getThreadLocalToSkipSetUserClaimsListeners() {
        return threadLocalToSkipSetUserClaimsListeners.get();
    }

    public static void setThreadLocalToSkipSetUserClaimsListeners(Boolean value) {
        threadLocalToSkipSetUserClaimsListeners.set(value);
    }

    public static void unsetThreadLocalIsManagedThroughSCIMEP() {
        threadLocalIsManagedThroughSCIMEP.remove();
    }

    public static Boolean getThreadLocalIsManagedThroughSCIMEP() {
        return threadLocalIsManagedThroughSCIMEP.get();
    }

    public static void setThreadLocalIsManagedThroughSCIMEP(Boolean value) {
        threadLocalIsManagedThroughSCIMEP.set(value);
    }

    public static String getGlobalConsumerId() {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    }

    public static String getUserConsumerId() {
        String userName = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
        String currentTenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        return UserCoreUtil.addTenantDomainToEntry(userName, currentTenantDomain);
    }

}
