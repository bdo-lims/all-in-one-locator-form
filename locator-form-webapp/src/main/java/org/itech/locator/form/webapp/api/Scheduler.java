package org.itech.locator.form.webapp.api;

import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.hl7.fhir.r4.model.Questionnaire;
import ca.uhn.fhir.rest.api.MethodOutcome;
import lombok.extern.slf4j.Slf4j;
import ca.uhn.fhir.context.FhirContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Scheduler {

    @Autowired
    protected FhirPersistingService fhirPersistingService;

    @Autowired
    protected FhirTransformService fhirTransformService;

    @Autowired
    private FhirContext fhirContext;

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void sheduleCreateQuestionnaireTask() {
        log.trace("executing task...");
        Questionnaire questionnaire = fhirTransformService.createQuestionnaire();
        MethodOutcome outcome = fhirPersistingService.executeTransaction(questionnaire);
        log.trace(fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome.getResource()));
    }
}
