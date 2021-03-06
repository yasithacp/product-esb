/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.mediator.tests.fault;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axis2.AxisFault;
import org.testng.annotations.Test;
import org.wso2.esb.integration.ESBIntegrationTestCase;
import org.wso2.esb.integration.axis2.StockQuoteClient;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class Soap12FaultDetailAsElementTestCase extends ESBIntegrationTestCase {
    private StockQuoteClient axis2Client;

    public void init() throws Exception {
        axis2Client = new StockQuoteClient();
        String filePath = "/mediators/fault/soap12_fault_detail_as_element_synapse.xml";
        loadESBConfigurationFromClasspath(filePath);
    }

    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.2 fault details as Element")
    public void testSOAP12FaultDetailAsElement() throws AxisFault {
        OMElement response;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(
                    getMainSequenceURL(),
                    "http://localhost:9010/services/NonExistingService",
                    "WSO2");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Test passed with Fault Message : " + expected.getMessage());
            assertTrue(expected.getReason().contains("Connection refused"), "ERROR Message mismatched");
            assertEquals(expected.getFaultCode().getLocalPart(), "VersionMismatch", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap12Env", "Fault code prefix mismatched");
            SOAPFaultDetail detailElm = expected.getFaultDetailElement();
            OMElement statusOME = detailElm.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "StatusCode", "axis2ns1"));
            System.out.println(statusOME.getText());
            assertNotNull(statusOME, "Fault detail element StatusCode null");
            assertEquals(statusOME.getText(), "1000", "Fault detail StatusCode mismatched");

            OMElement messageOME = detailElm.getFirstChildWithName(new QName("http://ws.apache.org/ns/synapse", "message", "axis2ns1"));
            assertNotNull(messageOME, "Fault detail element message null");
            assertEquals(messageOME.getText(), "fault details by automation", "Fault detail message mismatched");

        }

    }

    @Override
    protected void cleanup() {
        super.cleanup();
        axis2Client.destroy();
    }
}
