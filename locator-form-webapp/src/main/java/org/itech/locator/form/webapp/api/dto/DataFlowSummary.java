package org.itech.locator.form.webapp.api.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class DataFlowSummary {

	private Instant since;

	private Instant until;

	private Instant flaggedUntil;

	private int countSuccess;

	private int countWaiting;

	private int countWaitingFlagged;

	private int countRejected;
}
