/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this 
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It 
	   is required that all modified versions of this material be marked in 
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program 
	   with regards to rights under trademark law for use of the trade names 
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.collection.entity;

import java.util.Date;

import org.egov.commons.CVoucherHeader;
import org.egov.commons.EgwStatus;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.annotation.Search;
import org.egov.infstr.models.ServiceDetails;
import org.joda.time.DateTime;

/**
 * ReceiptHeader generated by hbm2java
 */
public class Challan extends StateAware {
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An instance of <code>ReceiptHeader</code> against which this challan is 
	 * created
	 */
	private ReceiptHeader receiptHeader;
	
	/**
	 * An instance of <code>EgwStatus</code> representing the challan status
	 */
	private EgwStatus status;
	
	/**
	 * A unique value representing the challan number
	 */
	private String challanNumber;
	
	/**
	 *  <code>Date</code> indicating when the challan is effective from
	 */
	private DateTime challanDate;
	
	/**
	 * A <code>Date</code> indicating till when the challan is valid
	 */
	private Date validUpto;
	
	private ServiceDetails service;
	
	private String reasonForCancellation;
	
	private CVoucherHeader voucherHeader;
	
	public Challan(){
	}
	
	public ReceiptHeader getReceiptHeader() {
		return receiptHeader;
	}


	public void setReceiptHeader(ReceiptHeader receiptHeader) {
		this.receiptHeader = receiptHeader;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

	@Search(searchOp = Search.Operator.contains)
	public String getChallanNumber() {
		return challanNumber;
	}


	public void setChallanNumber(String challanNumber) {
		this.challanNumber = challanNumber;
	}


	public Date getValidUpto() {
		return validUpto;
	}


	public void setValidUpto(Date validUpto) {
		this.validUpto = validUpto;
	}
	
	@Search(searchOp = Search.Operator.between)
	public DateTime getChallanDate() {
		return challanDate;
	}


	public void setChallanDate(DateTime challanDate) {
		this.challanDate = challanDate;
	}
	
	public ServiceDetails getService() {
		return service;
	}


	public void setService(ServiceDetails service) {
		this.service = service;
	}
	
	public String getReasonForCancellation() {
		return reasonForCancellation;
	}


	public void setReasonForCancellation(String reasonForCancellation) {
		this.reasonForCancellation = reasonForCancellation;
	}
	
	public CVoucherHeader getVoucherHeader() {
		return voucherHeader;
	}

	public void setVoucherHeader(CVoucherHeader voucherHeader) {
		this.voucherHeader = voucherHeader;
	}

	@Override
	public String getStateDetails() {
		return "Challan - " + challanNumber;
	}
	
	/* public String toString(){
		String str="Challan[ challanNo : " + this.challanNumber + ", validUpto : " + 
		this.validUpto + " , challanDate : " + this.challanDate + ", createdDate : " + this.createdDate + ",createdBy : " + this.createdBy 
		//+ "receiptHeader : " + this.receiptHeader 
		+ "]";
		return str;
	} */
	
}
