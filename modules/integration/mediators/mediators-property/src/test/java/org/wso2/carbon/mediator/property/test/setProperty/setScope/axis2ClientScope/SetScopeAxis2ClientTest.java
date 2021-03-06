package org.wso2.carbon.mediator.property.test.setProperty.setScope.axis2ClientScope;

/*  Copyright (c) WSO2 Inc. (http://wso2.com) All Rights Reserved.

  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.integration.core.AuthenticateStub;
import org.wso2.carbon.integration.core.FrameworkSettings;
import org.wso2.carbon.integration.core.TestTemplate;
import org.wso2.carbon.integration.core.utils.ArtifactReader;
import org.wso2.carbon.integration.core.utils.StockQuoteClient;
import org.wso2.carbon.mediation.configadmin.stub.ConfigServiceAdminStub;

public class SetScopeAxis2ClientTest extends TestTemplate {
    private static final Log log = LogFactory.getLog(SetScopeAxis2ClientTest.class);

    @Override
    public void init() {
        log.info("Initializing Property Mediator Axis2ClientScope Tests");
        log.debug("Property Mediator Axis2ClientScope Tests Initialised");
    }

    @Override
    public void runSuccessCase() {
        log.debug("Property Mediator Axis2ClientScope SuccessCase ");

        StockQuoteClient stockQuoteClient = new StockQuoteClient();
        OMElement result = null;

        try {
            AuthenticateStub authenticateStub = new AuthenticateStub();
            ConfigServiceAdminStub configServiceAdminStub = new ConfigServiceAdminStub("https://" + FrameworkSettings.HOST_NAME + ":" + FrameworkSettings.HTTPS_PORT + "/services/ConfigServiceAdmin");
            authenticateStub.authenticateAdminStub(configServiceAdminStub, sessionCookie);
            ArtifactReader artifactReader = new ArtifactReader();

           OMElement omElement = artifactReader.getOMElement(SetScopeAxis2ClientTest.class.getResource("/setProperty/setScope/axis2ClientScope/synapse.xml").getPath());

            configServiceAdminStub.updateConfiguration(omElement);

            /*Sending a StockQuoteClient request*/
            if (FrameworkSettings.STRATOS.equalsIgnoreCase("false")) {
                result = stockQuoteClient.stockQuoteClientforProxy("http://" + FrameworkSettings.HOST_NAME + ":" + FrameworkSettings.HTTP_PORT, null, "IBM");
            } else if (FrameworkSettings.STRATOS.equalsIgnoreCase("true")) {
                result = stockQuoteClient.stockQuoteClientforProxy("http://" + FrameworkSettings.HOST_NAME + ":" + FrameworkSettings.HTTP_PORT + "/services/" + FrameworkSettings.TENANT_NAME + "/", null, "IBM");
            }
            log.info(result);

            System.out.println(result);

            if (!result.toString().contains("IBM")) {
                Assert.fail();
                log.error("Property Mediator Axis2ClientScope Test Failed");
            }
        }
        catch (Exception e) {
            log.error("Property Mediator Axis2ClientScope doesn't work : " + e.getMessage());

        }
    }

    @Override
    public void runFailureCase() {

    }

    @Override
    public void cleanup() {

    }
}
