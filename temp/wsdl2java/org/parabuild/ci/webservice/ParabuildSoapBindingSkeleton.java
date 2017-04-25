/**
 * ParabuildSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class ParabuildSoapBindingSkeleton implements org.parabuild.ci.webservice.Parabuild, org.apache.axis.wsdl.Skeleton {
    private org.parabuild.ci.webservice.Parabuild impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVariables", _params, new javax.xml.namespace.QName("", "getVariablesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStartParameter"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVariables"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVariables") == null) {
            _myOperations.put("getVariables", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVariables")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getSystemProperties", _params, new javax.xml.namespace.QName("", "getSystemPropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfSystemProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getSystemProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSystemProperties") == null) {
            _myOperations.put("getSystemProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSystemProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getProjects", _params, new javax.xml.namespace.QName("", "getProjectsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProject"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjects"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getProjects") == null) {
            _myOperations.put("getProjects", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getProjects")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("startBuild", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "startBuild"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("startBuild") == null) {
            _myOperations.put("startBuild", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("startBuild")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequest"), org.parabuild.ci.webservice.BuildStartRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("startBuild", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "startBuild"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("startBuild") == null) {
            _myOperations.put("startBuild", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("startBuild")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("stopBuild", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "stopBuild"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("stopBuild") == null) {
            _myOperations.put("stopBuild", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("stopBuild")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("resumeBuild", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "resumeBuild"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("resumeBuild") == null) {
            _myOperations.put("resumeBuild", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("resumeBuild")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("requestCleanCheckout", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "requestCleanCheckout"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("requestCleanCheckout") == null) {
            _myOperations.put("requestCleanCheckout", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("requestCleanCheckout")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getCurrentBuildStatuses", _params, new javax.xml.namespace.QName("", "getCurrentBuildStatusesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatuses"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCurrentBuildStatuses") == null) {
            _myOperations.put("getCurrentBuildStatuses", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCurrentBuildStatuses")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCurrentBuildStatus", _params, new javax.xml.namespace.QName("", "getCurrentBuildStatusReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatus"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCurrentBuildStatus") == null) {
            _myOperations.put("getCurrentBuildStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCurrentBuildStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCurrentBuildStatus", _params, new javax.xml.namespace.QName("", "getCurrentBuildStatusReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCurrentBuildStatus"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCurrentBuildStatus") == null) {
            _myOperations.put("getCurrentBuildStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCurrentBuildStatus")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("findCurrentBuildStatuses", _params, new javax.xml.namespace.QName("", "findCurrentBuildStatusesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "findCurrentBuildStatuses"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("findCurrentBuildStatuses") == null) {
            _myOperations.put("findCurrentBuildStatuses", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("findCurrentBuildStatuses")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("serverVersion", _params, new javax.xml.namespace.QName("", "serverVersionReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "serverVersion"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("serverVersion") == null) {
            _myOperations.put("serverVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("serverVersion")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getGlobalVcsUserMap", _params, new javax.xml.namespace.QName("", "getGlobalVcsUserMapReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfGlobalVCSUserMap"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getGlobalVcsUserMap"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getGlobalVcsUserMap") == null) {
            _myOperations.put("getGlobalVcsUserMap", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getGlobalVcsUserMap")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getProjectAttributes", _params, new javax.xml.namespace.QName("", "getProjectAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getProjectAttributes") == null) {
            _myOperations.put("getProjectAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getProjectAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getProjectBuilds", _params, new javax.xml.namespace.QName("", "getProjectBuildsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectBuild"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectBuilds"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getProjectBuilds") == null) {
            _myOperations.put("getProjectBuilds", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getProjectBuilds")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getDisplayGroups", _params, new javax.xml.namespace.QName("", "getDisplayGroupsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroup"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDisplayGroups"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDisplayGroups") == null) {
            _myOperations.put("getDisplayGroups", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDisplayGroups")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDisplayGroupBuilds", _params, new javax.xml.namespace.QName("", "getDisplayGroupBuildsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfDisplayGroupBuild"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDisplayGroupBuilds"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDisplayGroupBuilds") == null) {
            _myOperations.put("getDisplayGroupBuilds", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDisplayGroupBuilds")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildFarmConfigurations", _params, new javax.xml.namespace.QName("", "getBuildFarmConfigurationsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmConfigurations"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildFarmConfigurations") == null) {
            _myOperations.put("getBuildFarmConfigurations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildFarmConfigurations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildFarmConfigurationAttributes", _params, new javax.xml.namespace.QName("", "getBuildFarmConfigurationAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmConfigurationAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmConfigurationAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildFarmConfigurationAttributes") == null) {
            _myOperations.put("getBuildFarmConfigurationAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildFarmConfigurationAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildFarmAgents", _params, new javax.xml.namespace.QName("", "getBuildFarmAgentsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildFarmAgent"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildFarmAgents"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildFarmAgents") == null) {
            _myOperations.put("getBuildFarmAgents", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildFarmAgents")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getAgentConfigurations", _params, new javax.xml.namespace.QName("", "getAgentConfigurationsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentConfigurations"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAgentConfigurations") == null) {
            _myOperations.put("getAgentConfigurations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAgentConfigurations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getAgentStatuses", _params, new javax.xml.namespace.QName("", "getAgentStatusesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfAgentStatus"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentStatuses"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAgentStatuses") == null) {
            _myOperations.put("getAgentStatuses", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAgentStatuses")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAgentConfiguration", _params, new javax.xml.namespace.QName("", "getAgentConfigurationReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getAgentConfiguration"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAgentConfiguration") == null) {
            _myOperations.put("getAgentConfiguration", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAgentConfiguration")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getActiveBuildConfigurations", _params, new javax.xml.namespace.QName("", "getActiveBuildConfigurationsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getActiveBuildConfigurations"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getActiveBuildConfigurations") == null) {
            _myOperations.put("getActiveBuildConfigurations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getActiveBuildConfigurations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildConfigurationAttributes", _params, new javax.xml.namespace.QName("", "getBuildConfigurationAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildConfigurationAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildConfigurationAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildConfigurationAttributes") == null) {
            _myOperations.put("getBuildConfigurationAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildConfigurationAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVersionControlSettings", _params, new javax.xml.namespace.QName("", "getVersionControlSettingsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVersionControlSetting"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVersionControlSettings"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVersionControlSettings") == null) {
            _myOperations.put("getVersionControlSettings", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVersionControlSettings")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVersionControlSetting"), org.parabuild.ci.webservice.VersionControlSetting[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("updateVersionControlSettings", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "updateVersionControlSettings"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("updateVersionControlSettings") == null) {
            _myOperations.put("updateVersionControlSettings", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("updateVersionControlSettings")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getScheduleProperties", _params, new javax.xml.namespace.QName("", "getSchedulePropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getScheduleProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getScheduleProperties") == null) {
            _myOperations.put("getScheduleProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getScheduleProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLabelProperties", _params, new javax.xml.namespace.QName("", "getLabelPropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLabelProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLabelProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLabelProperties") == null) {
            _myOperations.put("getLabelProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLabelProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLogConfigurations", _params, new javax.xml.namespace.QName("", "getLogConfigurationsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLogConfigurations"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLogConfigurations") == null) {
            _myOperations.put("getLogConfigurations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLogConfigurations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLogConfigurationProperties", _params, new javax.xml.namespace.QName("", "getLogConfigurationPropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfLogConfigurationProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLogConfigurationProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLogConfigurationProperties") == null) {
            _myOperations.put("getLogConfigurationProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLogConfigurationProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVCSUserToEmailMap", _params, new javax.xml.namespace.QName("", "getVCSUserToEmailMapReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfVCSUserToEmailMap"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getVCSUserToEmailMap"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVCSUserToEmailMap") == null) {
            _myOperations.put("getVCSUserToEmailMap", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVCSUserToEmailMap")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildWatchers", _params, new javax.xml.namespace.QName("", "getBuildWatchersReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildWatcher"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildWatchers"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildWatchers") == null) {
            _myOperations.put("getBuildWatchers", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildWatchers")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildSequence", _params, new javax.xml.namespace.QName("", "getBuildSequenceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildSequence"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildSequence"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildSequence") == null) {
            _myOperations.put("getBuildSequence", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildSequence")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getScheduleItem", _params, new javax.xml.namespace.QName("", "getScheduleItemReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfScheduleItem"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getScheduleItem"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getScheduleItem") == null) {
            _myOperations.put("getScheduleItem", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getScheduleItem")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getIssueTracker", _params, new javax.xml.namespace.QName("", "getIssueTrackerReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTracker"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueTracker"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getIssueTracker") == null) {
            _myOperations.put("getIssueTracker", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getIssueTracker")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getIssueTrackerProperties", _params, new javax.xml.namespace.QName("", "getIssueTrackerPropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueTrackerProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueTrackerProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getIssueTrackerProperties") == null) {
            _myOperations.put("getIssueTrackerProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getIssueTrackerProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRunCount", _params, new javax.xml.namespace.QName("", "getBuildRunCountReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunCount"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRunCount") == null) {
            _myOperations.put("getBuildRunCount", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRunCount")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRun", _params, new javax.xml.namespace.QName("", "getBuildRunReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRun"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRun") == null) {
            _myOperations.put("getBuildRun", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRun")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getCompletedBuildRuns", _params, new javax.xml.namespace.QName("", "getCompletedBuildRunsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRun"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getCompletedBuildRuns"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getCompletedBuildRuns") == null) {
            _myOperations.put("getCompletedBuildRuns", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getCompletedBuildRuns")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getLastSuccessfulBuildRun", _params, new javax.xml.namespace.QName("", "getLastSuccessfulBuildRunReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRun"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getLastSuccessfulBuildRun"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getLastSuccessfulBuildRun") == null) {
            _myOperations.put("getLastSuccessfulBuildRun", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getLastSuccessfulBuildRun")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("findlLastSuccessfulBuildRuns", _params, new javax.xml.namespace.QName("", "findlLastSuccessfulBuildRunsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRun"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "findlLastSuccessfulBuildRuns"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("findlLastSuccessfulBuildRuns") == null) {
            _myOperations.put("findlLastSuccessfulBuildRuns", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("findlLastSuccessfulBuildRuns")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRunAttributes", _params, new javax.xml.namespace.QName("", "getBuildRunAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRunAttributes") == null) {
            _myOperations.put("getBuildRunAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRunAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRunParticipants", _params, new javax.xml.namespace.QName("", "getBuildRunParticipantsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChangeList"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunParticipants"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRunParticipants") == null) {
            _myOperations.put("getBuildRunParticipants", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRunParticipants")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getChanges", _params, new javax.xml.namespace.QName("", "getChangesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfChange"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getChanges"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getChanges") == null) {
            _myOperations.put("getChanges", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getChanges")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStepRuns", _params, new javax.xml.namespace.QName("", "getStepRunsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRun"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepRuns"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStepRuns") == null) {
            _myOperations.put("getStepRuns", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStepRuns")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStepRunRunAttributes", _params, new javax.xml.namespace.QName("", "getStepRunRunAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepRunAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepRunRunAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStepRunRunAttributes") == null) {
            _myOperations.put("getStepRunRunAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStepRunRunAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStepLogs", _params, new javax.xml.namespace.QName("", "getStepLogsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepLog"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepLogs"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStepLogs") == null) {
            _myOperations.put("getStepLogs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStepLogs")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStepResults", _params, new javax.xml.namespace.QName("", "getStepResultsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfStepResult"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getStepResults"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStepResults") == null) {
            _myOperations.put("getStepResults", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStepResults")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getChangeList", _params, new javax.xml.namespace.QName("", "getChangeListReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ChangeList"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getChangeList"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getChangeList") == null) {
            _myOperations.put("getChangeList", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getChangeList")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getReleaseNotes", _params, new javax.xml.namespace.QName("", "getReleaseNotesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfReleaseNote"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getReleaseNotes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getReleaseNotes") == null) {
            _myOperations.put("getReleaseNotes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getReleaseNotes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getIssue", _params, new javax.xml.namespace.QName("", "getIssueReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "Issue"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssue"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getIssue") == null) {
            _myOperations.put("getIssue", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getIssue")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getIssueAttributes", _params, new javax.xml.namespace.QName("", "getIssueAttributesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueAttribute"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueAttributes"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getIssueAttributes") == null) {
            _myOperations.put("getIssueAttributes", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getIssueAttributes")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getIssueChangeLists", _params, new javax.xml.namespace.QName("", "getIssueChangeListsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfIssueChangeList"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getIssueChangeLists"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getIssueChangeLists") == null) {
            _myOperations.put("getIssueChangeLists", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getIssueChangeLists")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHourlyStatistics", _params, new javax.xml.namespace.QName("", "getHourlyStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHourlyStatistics") == null) {
            _myOperations.put("getHourlyStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHourlyStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDailyStatistics", _params, new javax.xml.namespace.QName("", "getDailyStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDailyStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDailyStatistics") == null) {
            _myOperations.put("getDailyStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDailyStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMonthlyStatistics", _params, new javax.xml.namespace.QName("", "getMonthlyStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getMonthlyStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMonthlyStatistics") == null) {
            _myOperations.put("getMonthlyStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMonthlyStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getYearlyStatistics", _params, new javax.xml.namespace.QName("", "getYearlyStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getYearlyStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getYearlyStatistics") == null) {
            _myOperations.put("getYearlyStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getYearlyStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHourlyBuildDistributions", _params, new javax.xml.namespace.QName("", "getHourlyBuildDistributionsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildDistribution"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyBuildDistributions"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHourlyBuildDistributions") == null) {
            _myOperations.put("getHourlyBuildDistributions", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHourlyBuildDistributions")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getWeekdayBuildDistributions", _params, new javax.xml.namespace.QName("", "getWeekdayBuildDistributionsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildDistribution"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getWeekdayBuildDistributions"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getWeekdayBuildDistributions") == null) {
            _myOperations.put("getWeekdayBuildDistributions", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getWeekdayBuildDistributions")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getHourlyTestStatistics", _params, new javax.xml.namespace.QName("", "getHourlyTestStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getHourlyTestStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getHourlyTestStatistics") == null) {
            _myOperations.put("getHourlyTestStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getHourlyTestStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getMonthlyTestStatistics", _params, new javax.xml.namespace.QName("", "getMonthlyTestStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getMonthlyTestStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getMonthlyTestStatistics") == null) {
            _myOperations.put("getMonthlyTestStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getMonthlyTestStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in3"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDailyTestStatistics", _params, new javax.xml.namespace.QName("", "getDailyTestStatisticsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestStatistics"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getDailyTestStatistics"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDailyTestStatistics") == null) {
            _myOperations.put("getDailyTestStatistics", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDailyTestStatistics")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getResultGroups", _params, new javax.xml.namespace.QName("", "getResultGroupsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultGroup"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultGroups"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getResultGroups") == null) {
            _myOperations.put("getResultGroups", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getResultGroups")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getProjectResultGroups", _params, new javax.xml.namespace.QName("", "getProjectResultGroupsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfProjectResultGroup"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getProjectResultGroups"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getProjectResultGroups") == null) {
            _myOperations.put("getProjectResultGroups", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getProjectResultGroups")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getResultConfigurations", _params, new javax.xml.namespace.QName("", "getResultConfigurationsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfiguration"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultConfigurations"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getResultConfigurations") == null) {
            _myOperations.put("getResultConfigurations", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getResultConfigurations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getResultConfigurationProperties", _params, new javax.xml.namespace.QName("", "getResultConfigurationPropertiesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfResultConfigurationProperty"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getResultConfigurationProperties"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getResultConfigurationProperties") == null) {
            _myOperations.put("getResultConfigurationProperties", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getResultConfigurationProperties")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getPublishedStepResults", _params, new javax.xml.namespace.QName("", "getPublishedStepResultsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfPublishedStepResult"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getPublishedStepResults"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPublishedStepResults") == null) {
            _myOperations.put("getPublishedStepResults", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPublishedStepResults")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRunActions", _params, new javax.xml.namespace.QName("", "getBuildRunActionsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunAction"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunActions"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRunActions") == null) {
            _myOperations.put("getBuildRunActions", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRunActions")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getTestSuiteNames", _params, new javax.xml.namespace.QName("", "getTestSuiteNamesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestSuiteName"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getTestSuiteNames"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTestSuiteNames") == null) {
            _myOperations.put("getTestSuiteNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTestSuiteNames")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getTestCaseNames", _params, new javax.xml.namespace.QName("", "getTestCaseNamesReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfTestCaseName"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getTestCaseNames"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getTestCaseNames") == null) {
            _myOperations.put("getTestCaseNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getTestCaseNames")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getBuildRunTests", _params, new javax.xml.namespace.QName("", "getBuildRunTestsReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ArrayOfBuildRunTest"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "getBuildRunTests"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getBuildRunTests") == null) {
            _myOperations.put("getBuildRunTests", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getBuildRunTests")).add(_oper);
    }

    public ParabuildSoapBindingSkeleton() {
        this.impl = new org.parabuild.ci.webservice.ParabuildSoapBindingImpl();
    }

    public ParabuildSoapBindingSkeleton(org.parabuild.ci.webservice.Parabuild impl) {
        this.impl = impl;
    }
    public org.parabuild.ci.webservice.StartParameter[] getVariables(int in0, int in1) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.StartParameter[] ret = impl.getVariables(in0, in1);
        return ret;
    }

    public org.parabuild.ci.webservice.SystemProperty[] getSystemProperties() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.SystemProperty[] ret = impl.getSystemProperties();
        return ret;
    }

    public org.parabuild.ci.webservice.Project[] getProjects() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.Project[] ret = impl.getProjects();
        return ret;
    }

    public void startBuild(int in0) throws java.rmi.RemoteException
    {
        impl.startBuild(in0);
    }

    public void startBuild(int in0, org.parabuild.ci.webservice.BuildStartRequest in1) throws java.rmi.RemoteException
    {
        impl.startBuild(in0, in1);
    }

    public void stopBuild(int in0) throws java.rmi.RemoteException
    {
        impl.stopBuild(in0);
    }

    public void resumeBuild(int in0) throws java.rmi.RemoteException
    {
        impl.resumeBuild(in0);
    }

    public void requestCleanCheckout(int in0) throws java.rmi.RemoteException
    {
        impl.requestCleanCheckout(in0);
    }

    public org.parabuild.ci.webservice.BuildStatus[] getCurrentBuildStatuses() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatus[] ret = impl.getCurrentBuildStatuses();
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatus ret = impl.getCurrentBuildStatus(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(java.lang.String in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatus ret = impl.getCurrentBuildStatus(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatus[] findCurrentBuildStatuses(java.lang.String in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatus[] ret = impl.findCurrentBuildStatuses(in0);
        return ret;
    }

    public java.lang.String serverVersion() throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.serverVersion();
        return ret;
    }

    public org.parabuild.ci.webservice.GlobalVCSUserMap[] getGlobalVcsUserMap() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.GlobalVCSUserMap[] ret = impl.getGlobalVcsUserMap();
        return ret;
    }

    public org.parabuild.ci.webservice.ProjectAttribute[] getProjectAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ProjectAttribute[] ret = impl.getProjectAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ProjectBuild[] getProjectBuilds(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ProjectBuild[] ret = impl.getProjectBuilds(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.DisplayGroup[] getDisplayGroups() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.DisplayGroup[] ret = impl.getDisplayGroups();
        return ret;
    }

    public org.parabuild.ci.webservice.DisplayGroupBuild[] getDisplayGroupBuilds(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.DisplayGroupBuild[] ret = impl.getDisplayGroupBuilds(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildFarmConfiguration[] getBuildFarmConfigurations() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildFarmConfiguration[] ret = impl.getBuildFarmConfigurations();
        return ret;
    }

    public org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[] getBuildFarmConfigurationAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[] ret = impl.getBuildFarmConfigurationAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildFarmAgent[] getBuildFarmAgents(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildFarmAgent[] ret = impl.getBuildFarmAgents(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.AgentConfiguration[] getAgentConfigurations() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.AgentConfiguration[] ret = impl.getAgentConfigurations();
        return ret;
    }

    public org.parabuild.ci.webservice.AgentStatus[] getAgentStatuses() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.AgentStatus[] ret = impl.getAgentStatuses();
        return ret;
    }

    public org.parabuild.ci.webservice.AgentConfiguration getAgentConfiguration(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.AgentConfiguration ret = impl.getAgentConfiguration(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildConfiguration[] getActiveBuildConfigurations() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildConfiguration[] ret = impl.getActiveBuildConfigurations();
        return ret;
    }

    public org.parabuild.ci.webservice.BuildConfigurationAttribute[] getBuildConfigurationAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildConfigurationAttribute[] ret = impl.getBuildConfigurationAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.VersionControlSetting[] getVersionControlSettings(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.VersionControlSetting[] ret = impl.getVersionControlSettings(in0);
        return ret;
    }

    public void updateVersionControlSettings(org.parabuild.ci.webservice.VersionControlSetting[] in0) throws java.rmi.RemoteException
    {
        impl.updateVersionControlSettings(in0);
    }

    public org.parabuild.ci.webservice.ScheduleProperty[] getScheduleProperties(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ScheduleProperty[] ret = impl.getScheduleProperties(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.LabelProperty[] getLabelProperties(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.LabelProperty[] ret = impl.getLabelProperties(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.LogConfiguration[] getLogConfigurations(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.LogConfiguration[] ret = impl.getLogConfigurations(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.LogConfigurationProperty[] getLogConfigurationProperties(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.LogConfigurationProperty[] ret = impl.getLogConfigurationProperties(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.VCSUserToEmailMap[] getVCSUserToEmailMap(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.VCSUserToEmailMap[] ret = impl.getVCSUserToEmailMap(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildWatcher[] getBuildWatchers(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildWatcher[] ret = impl.getBuildWatchers(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildSequence[] getBuildSequence(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildSequence[] ret = impl.getBuildSequence(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ScheduleItem[] getScheduleItem(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ScheduleItem[] ret = impl.getScheduleItem(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.IssueTracker[] getIssueTracker(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.IssueTracker[] ret = impl.getIssueTracker(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.IssueTrackerProperty[] getIssueTrackerProperties(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.IssueTrackerProperty[] ret = impl.getIssueTrackerProperties(in0);
        return ret;
    }

    public int getBuildRunCount(int in0) throws java.rmi.RemoteException
    {
        int ret = impl.getBuildRunCount(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRun getBuildRun(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRun ret = impl.getBuildRun(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRun[] getCompletedBuildRuns(int in0, int in1, int in2) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRun[] ret = impl.getCompletedBuildRuns(in0, in1, in2);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRun getLastSuccessfulBuildRun(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRun ret = impl.getLastSuccessfulBuildRun(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRun[] findlLastSuccessfulBuildRuns(int in0, int in1) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRun[] ret = impl.findlLastSuccessfulBuildRuns(in0, in1);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRunAttribute[] getBuildRunAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRunAttribute[] ret = impl.getBuildRunAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ChangeList[] getBuildRunParticipants(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ChangeList[] ret = impl.getBuildRunParticipants(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.Change[] getChanges(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.Change[] ret = impl.getChanges(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.StepRun[] getStepRuns(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.StepRun[] ret = impl.getStepRuns(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.StepRunAttribute[] getStepRunRunAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.StepRunAttribute[] ret = impl.getStepRunRunAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.StepLog[] getStepLogs(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.StepLog[] ret = impl.getStepLogs(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.StepResult[] getStepResults(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.StepResult[] ret = impl.getStepResults(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ChangeList getChangeList(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ChangeList ret = impl.getChangeList(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ReleaseNote[] getReleaseNotes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ReleaseNote[] ret = impl.getReleaseNotes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.Issue getIssue(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.Issue ret = impl.getIssue(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.IssueAttribute[] getIssueAttributes(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.IssueAttribute[] ret = impl.getIssueAttributes(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.IssueChangeList[] getIssueChangeLists(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.IssueChangeList[] ret = impl.getIssueChangeLists(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getHourlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatistics[] ret = impl.getHourlyStatistics(in0, in1, in2);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getDailyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatistics[] ret = impl.getDailyStatistics(in0, in1, in2);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getMonthlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatistics[] ret = impl.getMonthlyStatistics(in0, in1, in2);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildStatistics[] getYearlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildStatistics[] ret = impl.getYearlyStatistics(in0, in1, in2);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildDistribution[] getHourlyBuildDistributions(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildDistribution[] ret = impl.getHourlyBuildDistributions(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildDistribution[] getWeekdayBuildDistributions(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildDistribution[] ret = impl.getWeekdayBuildDistributions(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.TestStatistics[] getHourlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.TestStatistics[] ret = impl.getHourlyTestStatistics(in0, in1, in2, in3);
        return ret;
    }

    public org.parabuild.ci.webservice.TestStatistics[] getMonthlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.TestStatistics[] ret = impl.getMonthlyTestStatistics(in0, in1, in2, in3);
        return ret;
    }

    public org.parabuild.ci.webservice.TestStatistics[] getDailyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.TestStatistics[] ret = impl.getDailyTestStatistics(in0, in1, in2, in3);
        return ret;
    }

    public org.parabuild.ci.webservice.ResultGroup[] getResultGroups() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ResultGroup[] ret = impl.getResultGroups();
        return ret;
    }

    public org.parabuild.ci.webservice.ProjectResultGroup[] getProjectResultGroups(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ProjectResultGroup[] ret = impl.getProjectResultGroups(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ResultConfiguration[] getResultConfigurations(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ResultConfiguration[] ret = impl.getResultConfigurations(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.ResultConfigurationProperty[] getResultConfigurationProperties(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.ResultConfigurationProperty[] ret = impl.getResultConfigurationProperties(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.PublishedStepResult[] getPublishedStepResults(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.PublishedStepResult[] ret = impl.getPublishedStepResults(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRunAction[] getBuildRunActions(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRunAction[] ret = impl.getBuildRunActions(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.TestSuiteName[] getTestSuiteNames() throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.TestSuiteName[] ret = impl.getTestSuiteNames();
        return ret;
    }

    public org.parabuild.ci.webservice.TestCaseName[] getTestCaseNames(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.TestCaseName[] ret = impl.getTestCaseNames(in0);
        return ret;
    }

    public org.parabuild.ci.webservice.BuildRunTest[] getBuildRunTests(int in0) throws java.rmi.RemoteException
    {
        org.parabuild.ci.webservice.BuildRunTest[] ret = impl.getBuildRunTests(in0);
        return ret;
    }

}
