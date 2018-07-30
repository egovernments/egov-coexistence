/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.wtms.service.es;

import org.egov.infra.admin.master.entity.es.CityIndex;
import org.egov.infra.admin.master.service.es.CityIndexService;
import org.egov.wtms.entity.es.WaterChargeConnectionCount;
import org.egov.wtms.entity.es.WaterChargeSurveyDashboardRequest;
import org.egov.wtms.entity.es.WaterChargeSurveyDashboardResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WATER_CHARGES_SCHEME_INDEX;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Service
public class WaterChargeSurveyDashboardService {

    private static final String CITY_NAME = "ulbName";
    private static final String CITY_CODE = "ulbCode";
    private static final String DISTRICT_NAME = "districtName";
    private static final String REGION_NAME = "regionName";
    private static final String FUNCTIONARY_NAME = "functionaryName";
    private static final String LOCALITY_NAME = "localityName";
    private static final String REVENUE_WARD = "revenueWard";
    private static final String BPL_CONNECTION = "bplConnection";
    private static final String NONBPL_CONNECTION = "nonBplConnection";
    private static final String BPL_SANCTION_ISSUED = "bplSanctionOrder";
    private static final String NONBPL_SANCTION_ISSUED = "nonBplSanctionOrder";
    private static final String BPL_EXECUTED = "bplExecution";
    private static final String NON_BPL_EXECUTED = "nonBplExecution";
    private static final String APPLICATION_CREATED_DATE = "applicationCreatedDate";
    private static final String AGGREGATION_WISE = "aggregationWise";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private CityIndexService cityIndexService;

    public List<WaterChargeSurveyDashboardResponse> getAggregatedSurveyDetails(WaterChargeSurveyDashboardRequest surveyDashboardRequest) {
        String aggregationField = REGION_NAME;
        if (isNotBlank(surveyDashboardRequest.getAggregationLevel()))
            aggregationField = surveyDashboardRequest.getAggregationLevel();

        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(AGGREGATION_WISE).field(aggregationField).size(100)
                .subAggregation(AggregationBuilders.topHits("cityDetails").addField(CITY_NAME).addField(CITY_CODE))
                .subAggregation(AggregationBuilders.sum(BPL_CONNECTION).field(BPL_CONNECTION))
                .subAggregation(AggregationBuilders.sum(NONBPL_CONNECTION).field(NONBPL_CONNECTION))
                .subAggregation(AggregationBuilders.sum(BPL_SANCTION_ISSUED).field(BPL_SANCTION_ISSUED))
                .subAggregation(AggregationBuilders.sum(NONBPL_SANCTION_ISSUED).field(NONBPL_SANCTION_ISSUED))
                .subAggregation(AggregationBuilders.sum(BPL_EXECUTED).field(BPL_EXECUTED))
                .subAggregation(AggregationBuilders.sum(NON_BPL_EXECUTED).field(NON_BPL_EXECUTED));


        SearchResponse response = elasticsearchTemplate.getClient().prepareSearch(WATER_CHARGES_SCHEME_INDEX).setSize(0)
                .addAggregation(aggregationBuilder)
                .setQuery(prepareQuery(surveyDashboardRequest))
                .execute().actionGet();

        return setSurveyResponse(aggregationField, response);
    }


    private BoolQueryBuilder prepareQuery(WaterChargeSurveyDashboardRequest surveyDashboardRequest) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        if (isNotBlank(surveyDashboardRequest.getRegionName()))
            boolQuery = boolQuery.filter(termQuery(REGION_NAME, surveyDashboardRequest.getRegionName()));
        else if (isNotBlank(surveyDashboardRequest.getDistrictName()))
            boolQuery = boolQuery.filter(matchQuery(DISTRICT_NAME, surveyDashboardRequest.getDistrictName()));
        else if (isNotBlank(surveyDashboardRequest.getUlbName()))
            boolQuery = boolQuery.filter(matchQuery(CITY_NAME, surveyDashboardRequest.getUlbName()));
        else if (isNotBlank(surveyDashboardRequest.getUlbCode()))
            boolQuery = boolQuery.filter(matchQuery(CITY_CODE, surveyDashboardRequest.getUlbCode()));
        else if (isNotBlank(surveyDashboardRequest.getWardName()))
            boolQuery = boolQuery.filter(matchQuery(REVENUE_WARD, surveyDashboardRequest.getWardName()));
        else if (isNotBlank(surveyDashboardRequest.getLocalityName()))
            boolQuery = boolQuery.filter(matchQuery(LOCALITY_NAME, surveyDashboardRequest.getLocalityName()));
        else if (isNotBlank(surveyDashboardRequest.getFunctionaryName()))
            boolQuery = boolQuery.filter(matchQuery(FUNCTIONARY_NAME, surveyDashboardRequest.getFunctionaryName()));
        if (surveyDashboardRequest.getFromDate() != null)
            boolQuery = boolQuery.filter(rangeQuery(APPLICATION_CREATED_DATE).gte(surveyDashboardRequest.getFromDate()));
        if (surveyDashboardRequest.getToDate() != null)
            boolQuery = boolQuery.filter(rangeQuery(APPLICATION_CREATED_DATE).lte(surveyDashboardRequest.getToDate()));

        return boolQuery;
    }

    private List<WaterChargeSurveyDashboardResponse> setSurveyResponse(String aggregationField, SearchResponse response) {

        Terms ulbTerms = response.getAggregations().get(AGGREGATION_WISE);
        Iterable<CityIndex> cities = cityIndexService.findAll();
        List<CityIndex> city = new ArrayList<>();
        cities.forEach(city::add);

        List<WaterChargeSurveyDashboardResponse> responseList = new ArrayList<>();
        for (Bucket bucket : ulbTerms.getBuckets()) {
            WaterChargeSurveyDashboardResponse surveyResponse = new WaterChargeSurveyDashboardResponse();
            getAggregationNames(aggregationField, surveyResponse, city, bucket);
            surveyResponse.setApplicaitonReceived(getApplicationReceivedCount(bucket));
            surveyResponse.setSanctionOrderIssued(getSanctionOrderIssueCount(bucket));
            surveyResponse.setConnectionExecuted(getConnectionExecutedCount(bucket));
            double applicationReceived = surveyResponse.getApplicaitonReceived().getTotalConnection();
            surveyResponse.setPendingSanction(applicationReceived - surveyResponse.getSanctionOrderIssued().getTotalConnection());
            surveyResponse.setPendingExecution(applicationReceived - surveyResponse.getConnectionExecuted().getTotalConnection());
            responseList.add(surveyResponse);
        }
        return responseList;
    }


    private void getAggregationNames(String aggregationField, WaterChargeSurveyDashboardResponse surveyResponse,
                                     List<CityIndex> cities, Bucket bucket) {

        String name = bucket.getKeyAsString();
        if (REGION_NAME.equals(aggregationField))
            surveyResponse.setRegionName(name);
        else if (DISTRICT_NAME.equals(aggregationField))
            cities.stream().forEach(cityIndex -> {
                if (cityIndex.getDistrictname().equals(name))
                    surveyResponse.setRegionName(cityIndex.getRegionname());
                surveyResponse.setDistrictName(cityIndex.getDistrictname());
            });
        else if (CITY_CODE.equals(aggregationField)) {
            surveyResponse.setUlbCode(name);
            cities.stream().forEach(cityIndex -> {
                if (cityIndex.getCitycode().equals(name))
                    surveyResponse.setUlbGrade(cityIndex.getCitygrade());
                surveyResponse.setUlbName(cityIndex.getName());
                surveyResponse.setRegionName(cityIndex.getRegionname());
                surveyResponse.setDistrictName(cityIndex.getDistrictname());
            });
        }
    }

    private WaterChargeConnectionCount getApplicationReceivedCount(Bucket bucket) {
        return getWaterChargeConnectionCount(bucket, BPL_CONNECTION, NONBPL_CONNECTION);
    }

    private WaterChargeConnectionCount getSanctionOrderIssueCount(Bucket bucket) {
        return getWaterChargeConnectionCount(bucket, BPL_SANCTION_ISSUED, NONBPL_SANCTION_ISSUED);
    }

    private WaterChargeConnectionCount getConnectionExecutedCount(Bucket bucket) {
        return getWaterChargeConnectionCount(bucket, BPL_EXECUTED, NON_BPL_EXECUTED);
    }

    private WaterChargeConnectionCount getWaterChargeConnectionCount(Bucket bucket, String bplConnection, String nonBPLConnection) {
        WaterChargeConnectionCount connectionCount = new WaterChargeConnectionCount();
        Sum bplCount = bucket.getAggregations().get(bplConnection);
        Sum nonBPLCount = bucket.getAggregations().get(nonBPLConnection);
        connectionCount.setBplConnection(bplCount.getValue());
        connectionCount.setNonBPLConnection(nonBPLCount.getValue());
        connectionCount.setTotalConnection(bplCount.getValue() + nonBPLCount.getValue());
        return connectionCount;
    }
}