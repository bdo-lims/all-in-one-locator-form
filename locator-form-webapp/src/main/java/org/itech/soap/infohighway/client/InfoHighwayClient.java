package org.itech.soap.infohighway.client;

import javax.xml.bind.JAXBElement;

import org.itech.soap.infohighway.ObjectFactory;
import org.itech.soap.infohighway.Query;
import org.itech.soap.infohighway.QueryResponse;
import org.itech.soap.infohighway.QwsInput;
import org.itech.soap.infohighway.QwsInputParam;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoHighwayClient extends WebServiceGatewaySupport {

	private ObjectFactory factory = new ObjectFactory();

	private enum InfoHighwayField {
		CITIZEN_NIC_NUMBER("CITIZEN.NIC_NUMBER"), CITIZEN_SURNAME("CITIZEN.SURNAME"),
		CITIZEN_FIRST_NAME("CITIZEN.FIRST_NAME");

		private String field;

		private InfoHighwayField(String field) {
			this.field = field;
		}

		public String getField() {
			return field;
		}

	}

	private enum QueryId {
		NATIONAL_ID_QUERY_ID("MOH003");

		private String queryId;

		private QueryId(String queryId) {
			this.queryId = queryId;
		}

		public String getQueryId() {
			return queryId;
		}

	}

	private String infoHighwayUsername;
	private char[] infoHighwayPassword;

	public InfoHighwayClient(String infoHighwayUsername, char[] infoHighwayPassword) {
		this.infoHighwayUsername = infoHighwayUsername;
		this.infoHighwayPassword = infoHighwayPassword;
	}

	public QueryResponse getClientByNationalID(String nationalID) {
		Query query = factory.createQuery();
		QwsInputParam qwsInputParam = factory.createQwsInputParam();
		qwsInputParam.getValues().add(nationalID);
		qwsInputParam.setField(InfoHighwayField.CITIZEN_NIC_NUMBER.getField());

		QwsInput qwsInput = factory.createQwsInput();
		qwsInput.setUserId(infoHighwayUsername);
		qwsInput.setPass(new String(infoHighwayPassword));
		qwsInput.setQueryId(QueryId.NATIONAL_ID_QUERY_ID.getQueryId());
		qwsInput.getQwsInputParams().add(qwsInputParam);

		query.setQueryInput(qwsInput);
		log.debug("sending request to infohighway...");
		@SuppressWarnings("unchecked")
		JAXBElement<QueryResponse> response = (JAXBElement<QueryResponse>) getWebServiceTemplate()
				.marshalSendAndReceive(factory.createQuery(query));

		log.debug("received response from infohighway...");
		return response.getValue();
	}

}
