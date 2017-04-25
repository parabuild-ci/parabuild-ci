/**
 * ParabuildSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class ParabuildSoapBindingStub extends org.apache.axis.client.Stub implements org.parabuild.ci.webservice.Parabuild {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[73];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
        _initOperationDesc5();
        _initOperationDesc6();
        _initOperationDesc7();
        _initOperationDesc8();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVariables");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStartParameter"));
        oper.setReturnClass(org.parabuild.ci.webservice.StartParameter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVariablesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSystemProperties");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfSystemProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.SystemProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSystemPropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProjects");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProject"));
        oper.setReturnClass(org.parabuild.ci.webservice.Project[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("startBuild");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("startBuild");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequest"), org.parabuild.ci.webservice.BuildStartRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("stopBuild");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("resumeBuild");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("requestCleanCheckout");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentBuildStatuses");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatus"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatus[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCurrentBuildStatusesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentBuildStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatus.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCurrentBuildStatusReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCurrentBuildStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatus.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCurrentBuildStatusReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findCurrentBuildStatuses");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatus"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatus[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "findCurrentBuildStatusesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("serverVersion");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "serverVersionReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getGlobalVcsUserMap");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfGlobalVCSUserMap"));
        oper.setReturnClass(org.parabuild.ci.webservice.GlobalVCSUserMap[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getGlobalVcsUserMapReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProjectAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.ProjectAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProjectBuilds");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectBuild"));
        oper.setReturnClass(org.parabuild.ci.webservice.ProjectBuild[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectBuildsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDisplayGroups");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroup"));
        oper.setReturnClass(org.parabuild.ci.webservice.DisplayGroup[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDisplayGroupsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDisplayGroupBuilds");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroupBuild"));
        oper.setReturnClass(org.parabuild.ci.webservice.DisplayGroupBuild[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDisplayGroupBuildsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildFarmConfigurations");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildFarmConfiguration[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildFarmConfigurationsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildFarmConfigurationAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfigurationAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildFarmConfigurationAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildFarmAgents");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmAgent"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildFarmAgent[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildFarmAgentsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAgentConfigurations");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.AgentConfiguration[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAgentConfigurationsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAgentStatuses");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentStatus"));
        oper.setReturnClass(org.parabuild.ci.webservice.AgentStatus[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAgentStatusesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAgentConfiguration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.AgentConfiguration.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getAgentConfigurationReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getActiveBuildConfigurations");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildConfiguration[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getActiveBuildConfigurationsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildConfigurationAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfigurationAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildConfigurationAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildConfigurationAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVersionControlSettings");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVersionControlSetting"));
        oper.setReturnClass(org.parabuild.ci.webservice.VersionControlSetting[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVersionControlSettingsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateVersionControlSettings");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVersionControlSetting"), org.parabuild.ci.webservice.VersionControlSetting[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getScheduleProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.ScheduleProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSchedulePropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getLabelProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLabelProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.LabelProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getLabelPropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getLogConfigurations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.LogConfiguration[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getLogConfigurationsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getLogConfigurationProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfigurationProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.LogConfigurationProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getLogConfigurationPropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVCSUserToEmailMap");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVCSUserToEmailMap"));
        oper.setReturnClass(org.parabuild.ci.webservice.VCSUserToEmailMap[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getVCSUserToEmailMapReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildWatchers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildWatcher"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildWatcher[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildWatchersReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildSequence");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildSequence"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildSequence[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildSequenceReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getScheduleItem");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleItem"));
        oper.setReturnClass(org.parabuild.ci.webservice.ScheduleItem[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getScheduleItemReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIssueTracker");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTracker"));
        oper.setReturnClass(org.parabuild.ci.webservice.IssueTracker[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIssueTrackerReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIssueTrackerProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTrackerProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.IssueTrackerProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIssueTrackerPropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRunCount");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        oper.setReturnClass(int.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunCountReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRun");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRun.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[39] = oper;

    }

    private static void _initOperationDesc5(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCompletedBuildRuns");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRun"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRun[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCompletedBuildRunsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[40] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getLastSuccessfulBuildRun");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRun.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getLastSuccessfulBuildRunReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[41] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findlLastSuccessfulBuildRuns");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRun"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRun[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "findlLastSuccessfulBuildRunsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[42] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRunAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRunAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[43] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRunParticipants");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChangeList"));
        oper.setReturnClass(org.parabuild.ci.webservice.ChangeList[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunParticipantsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[44] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getChanges");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChange"));
        oper.setReturnClass(org.parabuild.ci.webservice.Change[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getChangesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[45] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStepRuns");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRun"));
        oper.setReturnClass(org.parabuild.ci.webservice.StepRun[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getStepRunsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[46] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStepRunRunAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRunAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.StepRunAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getStepRunRunAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[47] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStepLogs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepLog"));
        oper.setReturnClass(org.parabuild.ci.webservice.StepLog[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getStepLogsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[48] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStepResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepResult"));
        oper.setReturnClass(org.parabuild.ci.webservice.StepResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getStepResultsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[49] = oper;

    }

    private static void _initOperationDesc6(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getChangeList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ChangeList"));
        oper.setReturnClass(org.parabuild.ci.webservice.ChangeList.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getChangeListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[50] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getReleaseNotes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfReleaseNote"));
        oper.setReturnClass(org.parabuild.ci.webservice.ReleaseNote[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getReleaseNotesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[51] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIssue");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Issue"));
        oper.setReturnClass(org.parabuild.ci.webservice.Issue.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIssueReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[52] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIssueAttributes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueAttribute"));
        oper.setReturnClass(org.parabuild.ci.webservice.IssueAttribute[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIssueAttributesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[53] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getIssueChangeLists");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueChangeList"));
        oper.setReturnClass(org.parabuild.ci.webservice.IssueChangeList[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getIssueChangeListsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[54] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHourlyStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getHourlyStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[55] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDailyStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDailyStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[56] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMonthlyStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMonthlyStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[57] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getYearlyStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getYearlyStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[58] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHourlyBuildDistributions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildDistribution"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildDistribution[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getHourlyBuildDistributionsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[59] = oper;

    }

    private static void _initOperationDesc7(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getWeekdayBuildDistributions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildDistribution"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildDistribution[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getWeekdayBuildDistributionsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[60] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getHourlyTestStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.TestStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getHourlyTestStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[61] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMonthlyTestStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.TestStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getMonthlyTestStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[62] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDailyTestStatistics");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        oper.setReturnClass(org.parabuild.ci.webservice.TestStatistics[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDailyTestStatisticsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[63] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getResultGroups");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultGroup"));
        oper.setReturnClass(org.parabuild.ci.webservice.ResultGroup[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getResultGroupsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[64] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProjectResultGroups");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectResultGroup"));
        oper.setReturnClass(org.parabuild.ci.webservice.ProjectResultGroup[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getProjectResultGroupsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[65] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getResultConfigurations");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfiguration"));
        oper.setReturnClass(org.parabuild.ci.webservice.ResultConfiguration[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getResultConfigurationsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[66] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getResultConfigurationProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfigurationProperty"));
        oper.setReturnClass(org.parabuild.ci.webservice.ResultConfigurationProperty[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getResultConfigurationPropertiesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[67] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPublishedStepResults");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfPublishedStepResult"));
        oper.setReturnClass(org.parabuild.ci.webservice.PublishedStepResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getPublishedStepResultsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[68] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRunActions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAction"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRunAction[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunActionsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[69] = oper;

    }

    private static void _initOperationDesc8(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTestSuiteNames");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestSuiteName"));
        oper.setReturnClass(org.parabuild.ci.webservice.TestSuiteName[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTestSuiteNamesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[70] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTestCaseNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestCaseName"));
        oper.setReturnClass(org.parabuild.ci.webservice.TestCaseName[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTestCaseNamesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[71] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getBuildRunTests");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunTest"));
        oper.setReturnClass(org.parabuild.ci.webservice.BuildRunTest[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getBuildRunTestsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[72] = oper;

    }

    public ParabuildSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ParabuildSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ParabuildSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.1");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.AgentConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentHost");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.AgentHost.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentStatus");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.AgentStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOf_xsd_string");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.AgentConfiguration[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentConfiguration");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentStatus");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.AgentStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentStatus");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildConfiguration[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildConfiguration");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfigurationAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildConfigurationAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildConfigurationAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildDistribution");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildDistribution[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildDistribution");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmAgent");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmAgent[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmAgent");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmConfiguration[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmConfiguration");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfigurationAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmConfigurationAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRun");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRun[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAction");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunAction[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunAction");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunTest");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunTest[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunTest");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildSequence");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildSequence[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildSequence");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStartRequestParameter");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStartRequestParameter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequestParameter");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStatistics[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatistics");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatus");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStatus[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildWatcher");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildWatcher[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildWatcher");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChange");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.Change[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Change");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChangeList");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ChangeList[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ChangeList");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.DisplayGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "DisplayGroup");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroupBuild");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.DisplayGroupBuild[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "DisplayGroupBuild");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfGlobalVCSUserMap");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.GlobalVCSUserMap[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "GlobalVCSUserMap");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueChangeList");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueChangeList[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueChangeList");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTracker");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueTracker[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueTracker");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTrackerProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueTrackerProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueTrackerProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLabelProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LabelProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LabelProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LogConfiguration[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LogConfiguration");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfigurationProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LogConfigurationProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LogConfigurationProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProject");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.Project[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Project");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectBuild");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectBuild[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectBuild");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectResultGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectResultGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectResultGroup");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfPublishedStepResult");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.PublishedStepResult[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "PublishedStepResult");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfReleaseNote");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ReleaseNote[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ReleaseNote");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultConfiguration[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultConfiguration");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfigurationProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultConfigurationProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultConfigurationProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultGroup[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultGroup");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleItem");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ScheduleItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ScheduleItem");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ScheduleProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ScheduleProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfSourceControlSettingOverride");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.SourceControlSettingOverride[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "SourceControlSettingOverride");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStartParameter");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StartParameter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StartParameter");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepLog");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepLog[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepLog");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepResult");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepResult[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepResult");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRun");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepRun[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepRun");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRunAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepRunAttribute[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepRunAttribute");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfSystemProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.SystemProperty[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "SystemProperty");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestCaseName");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestCaseName[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestCaseName");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestStatistics[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestStatistics");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestSuiteName");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestSuiteName[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestSuiteName");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVCSUserToEmailMap");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.VCSUserToEmailMap[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "VCSUserToEmailMap");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVersionControlSetting");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.VersionControlSetting[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "VersionControlSetting");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildConfigurationAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildConfigurationAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildDistribution");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildDistribution.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmAgent");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmAgent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmConfigurationAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildFarmConfigurationAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRun.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunAction");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunAction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunTest");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildRunTest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildSequence");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildSequence.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequest");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStartRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequestParameter");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStartRequestParameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatistics");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStatistics.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildWatcher");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.BuildWatcher.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Change");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.Change.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ChangeList");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ChangeList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "DisplayGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.DisplayGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "DisplayGroupBuild");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.DisplayGroupBuild.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "GlobalVCSUserMap");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.GlobalVCSUserMap.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Issue");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.Issue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueChangeList");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueChangeList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueTracker");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueTracker.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueTrackerProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.IssueTrackerProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LabelProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LabelProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LogConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LogConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "LogConfigurationProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.LogConfigurationProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Project");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.Project.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectBuild");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectBuild.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectResultGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ProjectResultGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "PublishedStepResult");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.PublishedStepResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ReleaseNote");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ReleaseNote.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultConfiguration");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultConfiguration.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultConfigurationProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultConfigurationProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ResultGroup");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ResultGroup.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ScheduleItem");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ScheduleItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ScheduleProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.ScheduleProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "SourceControlSettingOverride");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.SourceControlSettingOverride.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StartParameter");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StartParameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepLog");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepLog.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepResult");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepRun");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepRun.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepRunAttribute");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.StepRunAttribute.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "SystemProperty");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.SystemProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestCaseName");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestCaseName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestStatistics");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestStatistics.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestSuiteName");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.TestSuiteName.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "VCSUserToEmailMap");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.VCSUserToEmailMap.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "VersionControlSetting");
            cachedSerQNames.add(qName);
            cls = org.parabuild.ci.webservice.VersionControlSetting.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.parabuild.ci.webservice.StartParameter[] getVariables(int in0, int in1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVariables"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), new java.lang.Integer(in1)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.StartParameter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.StartParameter[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.StartParameter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.SystemProperty[] getSystemProperties() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getSystemProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.SystemProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.SystemProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.SystemProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.Project[] getProjects() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjects"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.Project[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.Project[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.Project[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void startBuild(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "startBuild"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void startBuild(int in0, org.parabuild.ci.webservice.BuildStartRequest in1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "startBuild"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void stopBuild(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "stopBuild"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void resumeBuild(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "resumeBuild"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void requestCleanCheckout(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "requestCleanCheckout"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatus[] getCurrentBuildStatuses() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatuses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatus[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatus[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatus[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatus) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatus) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatus.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(java.lang.String in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatus) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatus) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatus.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatus[] findCurrentBuildStatuses(java.lang.String in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "findCurrentBuildStatuses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatus[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatus[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatus[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public java.lang.String serverVersion() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "serverVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.GlobalVCSUserMap[] getGlobalVcsUserMap() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getGlobalVcsUserMap"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.GlobalVCSUserMap[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.GlobalVCSUserMap[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.GlobalVCSUserMap[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ProjectAttribute[] getProjectAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ProjectAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ProjectAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ProjectAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ProjectBuild[] getProjectBuilds(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectBuilds"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ProjectBuild[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ProjectBuild[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ProjectBuild[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.DisplayGroup[] getDisplayGroups() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDisplayGroups"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.DisplayGroup[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.DisplayGroup[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.DisplayGroup[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.DisplayGroupBuild[] getDisplayGroupBuilds(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDisplayGroupBuilds"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.DisplayGroupBuild[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.DisplayGroupBuild[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.DisplayGroupBuild[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildFarmConfiguration[] getBuildFarmConfigurations() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmConfigurations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildFarmConfiguration[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildFarmConfiguration[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildFarmConfiguration[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[] getBuildFarmConfigurationAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmConfigurationAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildFarmAgent[] getBuildFarmAgents(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmAgents"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildFarmAgent[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildFarmAgent[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildFarmAgent[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.AgentConfiguration[] getAgentConfigurations() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentConfigurations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.AgentConfiguration[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.AgentConfiguration[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.AgentConfiguration[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.AgentStatus[] getAgentStatuses() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentStatuses"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.AgentStatus[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.AgentStatus[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.AgentStatus[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.AgentConfiguration getAgentConfiguration(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentConfiguration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.AgentConfiguration) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.AgentConfiguration) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.AgentConfiguration.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildConfiguration[] getActiveBuildConfigurations() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getActiveBuildConfigurations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildConfiguration[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildConfiguration[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildConfiguration[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildConfigurationAttribute[] getBuildConfigurationAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildConfigurationAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildConfigurationAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildConfigurationAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildConfigurationAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.VersionControlSetting[] getVersionControlSettings(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVersionControlSettings"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.VersionControlSetting[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.VersionControlSetting[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.VersionControlSetting[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void updateVersionControlSettings(org.parabuild.ci.webservice.VersionControlSetting[] in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "updateVersionControlSettings"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ScheduleProperty[] getScheduleProperties(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getScheduleProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ScheduleProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ScheduleProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ScheduleProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.LabelProperty[] getLabelProperties(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLabelProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.LabelProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.LabelProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.LabelProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.LogConfiguration[] getLogConfigurations(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLogConfigurations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.LogConfiguration[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.LogConfiguration[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.LogConfiguration[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.LogConfigurationProperty[] getLogConfigurationProperties(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLogConfigurationProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.LogConfigurationProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.LogConfigurationProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.LogConfigurationProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.VCSUserToEmailMap[] getVCSUserToEmailMap(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVCSUserToEmailMap"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.VCSUserToEmailMap[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.VCSUserToEmailMap[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.VCSUserToEmailMap[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildWatcher[] getBuildWatchers(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildWatchers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildWatcher[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildWatcher[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildWatcher[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildSequence[] getBuildSequence(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildSequence"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildSequence[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildSequence[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildSequence[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ScheduleItem[] getScheduleItem(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getScheduleItem"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ScheduleItem[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ScheduleItem[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ScheduleItem[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.IssueTracker[] getIssueTracker(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueTracker"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.IssueTracker[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.IssueTracker[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.IssueTracker[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.IssueTrackerProperty[] getIssueTrackerProperties(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueTrackerProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.IssueTrackerProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.IssueTrackerProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.IssueTrackerProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public int getBuildRunCount(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunCount"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Integer) _resp).intValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRun getBuildRun(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[39]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRun"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRun) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRun) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRun.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRun[] getCompletedBuildRuns(int in0, int in1, int in2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[40]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCompletedBuildRuns"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), new java.lang.Integer(in1), new java.lang.Integer(in2)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRun[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRun[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRun[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRun getLastSuccessfulBuildRun(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[41]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLastSuccessfulBuildRun"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRun) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRun) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRun.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRun[] findlLastSuccessfulBuildRuns(int in0, int in1) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[42]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "findlLastSuccessfulBuildRuns"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), new java.lang.Integer(in1)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRun[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRun[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRun[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRunAttribute[] getBuildRunAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[43]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRunAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRunAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRunAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ChangeList[] getBuildRunParticipants(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[44]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunParticipants"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ChangeList[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ChangeList[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ChangeList[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.Change[] getChanges(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[45]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getChanges"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.Change[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.Change[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.Change[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.StepRun[] getStepRuns(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[46]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepRuns"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.StepRun[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.StepRun[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.StepRun[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.StepRunAttribute[] getStepRunRunAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[47]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepRunRunAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.StepRunAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.StepRunAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.StepRunAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.StepLog[] getStepLogs(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[48]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepLogs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.StepLog[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.StepLog[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.StepLog[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.StepResult[] getStepResults(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[49]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.StepResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.StepResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.StepResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ChangeList getChangeList(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[50]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getChangeList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ChangeList) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ChangeList) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ChangeList.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ReleaseNote[] getReleaseNotes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[51]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getReleaseNotes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ReleaseNote[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ReleaseNote[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ReleaseNote[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.Issue getIssue(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[52]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssue"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.Issue) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.Issue) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.Issue.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.IssueAttribute[] getIssueAttributes(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[53]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueAttributes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.IssueAttribute[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.IssueAttribute[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.IssueAttribute[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.IssueChangeList[] getIssueChangeLists(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[54]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueChangeLists"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.IssueChangeList[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.IssueChangeList[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.IssueChangeList[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getHourlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[55]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getDailyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[56]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDailyStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getMonthlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[57]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getMonthlyStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getYearlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[58]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getYearlyStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildDistribution[] getHourlyBuildDistributions(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[59]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyBuildDistributions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildDistribution[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildDistribution[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildDistribution[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildDistribution[] getWeekdayBuildDistributions(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[60]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getWeekdayBuildDistributions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildDistribution[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildDistribution[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildDistribution[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.TestStatistics[] getHourlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[61]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyTestStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2, new java.lang.Byte(in3)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.TestStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.TestStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.TestStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.TestStatistics[] getMonthlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[62]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getMonthlyTestStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2, new java.lang.Byte(in3)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.TestStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.TestStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.TestStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.TestStatistics[] getDailyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[63]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDailyTestStatistics"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0), in1, in2, new java.lang.Byte(in3)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.TestStatistics[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.TestStatistics[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.TestStatistics[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ResultGroup[] getResultGroups() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[64]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultGroups"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ResultGroup[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ResultGroup[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ResultGroup[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ProjectResultGroup[] getProjectResultGroups(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[65]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectResultGroups"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ProjectResultGroup[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ProjectResultGroup[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ProjectResultGroup[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ResultConfiguration[] getResultConfigurations(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[66]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultConfigurations"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ResultConfiguration[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ResultConfiguration[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ResultConfiguration[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.ResultConfigurationProperty[] getResultConfigurationProperties(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[67]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultConfigurationProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.ResultConfigurationProperty[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.ResultConfigurationProperty[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.ResultConfigurationProperty[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.PublishedStepResult[] getPublishedStepResults(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[68]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getPublishedStepResults"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.PublishedStepResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.PublishedStepResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.PublishedStepResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRunAction[] getBuildRunActions(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[69]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunActions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRunAction[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRunAction[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRunAction[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.TestSuiteName[] getTestSuiteNames() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[70]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getTestSuiteNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.TestSuiteName[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.TestSuiteName[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.TestSuiteName[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.TestCaseName[] getTestCaseNames(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[71]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getTestCaseNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.TestCaseName[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.TestCaseName[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.TestCaseName[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public org.parabuild.ci.webservice.BuildRunTest[] getBuildRunTests(int in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[72]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunTests"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(in0)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.parabuild.ci.webservice.BuildRunTest[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.parabuild.ci.webservice.BuildRunTest[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.parabuild.ci.webservice.BuildRunTest[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
