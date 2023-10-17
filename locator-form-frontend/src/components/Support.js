import React from "react";
import { Field, Formik, Form } from "formik";
import { FormattedMessage, injectIntl } from "react-intl";
import { MyTextInput, MySelect, datetimeNow } from "./inputs/MyInputs";
import ReactJson from "react-json-view";

class Summary extends React.Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef();
    this.state = {
      submitErrorKey: "",
      advancedSearchMode: "",
      dataflowSummaryJson: {},
    };
  }

  getDefaultDataFlowSummaryBounded = () => {
    fetch(
      `${process.env.REACT_APP_DATA_IMPORT_API}/support/dataFlowSummary/bounded/calculated`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("react-token")}`,
        },
      }
    )
      .then(async (response) => {
        if (response.ok) {
          this.setState({ dataflowSummaryJson: await response.json() });
        } else {
          throw new Error("didn't receive ok");
        }
      })
      .catch((err) => {
        console.log(err);
        this.setState({ submitErrorKey: "error.submit" });
      });
  };

  getDefaultDataFlowSummaryUnbounded = () => {
    fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/support/dataFlowSummary`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("react-token")}`,
      },
    })
      .then(async (response) => {
        if (response.ok) {
          this.setState({ dataflowSummaryJson: await response.json() });
        } else {
          throw new Error("didn't receive ok");
        }
      })
      .catch((err) => {
        console.log(err);
        this.setState({ submitErrorKey: "error.submit" });
      });
  };

  getDataFlowSummaryBounded = (values) => {
    var params = "";
    for (var key in values) {
		params += key + "=" + encodeURIComponent(values[key]) + "&";
    }
    params = params.slice(0, params.length - 1);
    fetch(
      `${process.env.REACT_APP_DATA_IMPORT_API}/support/dataFlowSummary/bounded?${params}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("react-token")}`,
        },
      }
    )
      .then(async (response) => {
        if (response.ok) {
          this.setState({ dataflowSummaryJson: await response.json() });
        } else {
          throw new Error("didn't receive ok");
        }
      })
      .catch((err) => {
        console.log(err);
        this.setState({ submitErrorKey: "error.submit" });
      });
  };

  getDataFlowSummaryBoundedCalculated = (values) => {
    var params = "";
    for (var key in values) {
      params += key + "=" + encodeURIComponent(values[key]) + "&";
    }
    params = params.slice(0, params.length - 1);
    fetch(
      `${process.env.REACT_APP_DATA_IMPORT_API}/support/dataFlowSummary/bounded/calculated?${params}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("react-token")}`,
        },
      }
    )
      .then(async (response) => {
        if (response.ok) {
          this.setState({ dataflowSummaryJson: await response.json() });
        } else {
          throw new Error("didn't receive ok");
        }
      })
      .catch((err) => {
        console.log(err);
        this.setState({ submitErrorKey: "error.submit" });
      });
  };

  selectAdvancedSearch = (e) => {
    // Update distance in state via setState()
    this.setState({ advancedSearchMode: e.target.value });
  };

  render() {
    return (
      <>
        <div className="container-fluid d-flex min-vh-100 flex-column content">
          <div className="row dark-row">
            <div className="col-lg-12 ">
              <div className="container pt-3">
                <div className="container">
                  <h3 className="question-header">
                    <FormattedMessage
                      id="nav.item.header.support"
                      defaultMessage="Support"
                    />
                  </h3>
                </div>
              </div>
            </div>
          </div>
          <div className="row light-row flex-grow-1">
            <div className="col-lg-12 ">
              <div className="container pt-3">
                <div className="row dark-row">
                  <div className="col-lg-4">
                    <h4>Data Flow Summary</h4>
                  </div>
                </div>
                <div className="row dark-row">
                  <div className="col-lg-4">
                    <button onClick={this.getDefaultDataFlowSummaryBounded}>
                      <FormattedMessage
                        id="nav.item.defaultdataflowsummary"
                        defaultMessage="Default Data Flow Summary"
                      />
                    </button>
                  </div>
                  <div className="col-lg-4">
                    <button onClick={this.getDefaultDataFlowSummaryUnbounded}>
                      <FormattedMessage
                        id="nav.item.defaultdataflowsummaryall"
                        defaultMessage="Default Data Flow Summary - All Time"
                      />
                    </button>
                  </div>
                </div>
                <div className="row dark-row">
                  <div className="col-lg-4">
                    <select
                      id="advancedSearchmode"
                      name="advancedSearchmode"
                      onChange={this.selectAdvancedSearch}
                    >
                      <option value="">select advanced search mode</option>
                      <option value="datetime">Date/Time</option>
                      <option value="offsets">Offsets</option>
                    </select>
                  </div>
                </div>

                {this.state.advancedSearchMode === "datetime" && (
                  <Formik
                    initialValues={{
                      since: datetimeNow(),
                      until: datetimeNow(),
                      flaggedUntil: datetimeNow(),
					  timezone: "+04:00",
                    }}
                    onSubmit={this.getDataFlowSummaryBounded}
                  >
                    {(formikProps) => {
                      return (
                        <Form>
                          <div className="row dark-row">
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Since"
                                type="datetime-local"
                                name="since"
                                step={1}
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Until"
                                type="datetime-local"
                                name="until"
                                step={1}
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Flag Until"
                                type="datetime-local"
                                name="flaggedUntil"
                                step={1}
                              />
                            </div>
                            <div className="col-lg-3">
							<Field name="timezone">
								{({ field, form, meta }) =>
									<MySelect label={<FormattedMessage id="nav.item.timezone" defaultMessage="Time Zone" />}
										name={field.name} 
										form={form}
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										options={[{"label": "MUT", value: "+04:00"}, {"label": "UTC", value: "Z"}]}
									/>
								}
							</Field>
                            </div>
                          </div>
                          <div className="row dark-row">
                            <div className="col-lg-4">
                              <button type="submit">
                                <FormattedMessage
                                  id="nav.item.dataflowsummarydatetime"
                                  defaultMessage="Data Flow Summary by Date/Time"
                                />
                              </button>
                            </div>
                          </div>
                        </Form>
                      );
                    }}
                  </Formik>
                )}

                {this.state.advancedSearchMode === "offsets" && (
                  <Formik
                    initialValues={{
                      sinceDays: 0,
                      sinceHours: 0,
                      sinceMinutes: 30,
                      sinceSeconds: 0,
                      untilDays: 0,
                      untilHours: 0,
                      untilMinutes: 0,
                      untilSeconds: 0,
                      daysFlagged: 0,
                      hoursFlagged: 0,
                      minutesFlagged: 0,
                      secondsFlagged: 0,
                    }}
                    onSubmit={this.getDataFlowSummaryBoundedCalculated}
                  >
                    {(formikProps) => {
                      return (
                        <Form>
                          <div className="row dark-row">
                            <div className="col-lg-12"><label>Since</label></div>
                          </div>
                          <div className="row dark-row">
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Days"
                                type="number"
                                step="1"
                                name="sinceDays"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Hours"
                                type="number"
                                step="1"
                                name="sinceHours"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Minutes"
                                type="number"
                                step="1"
                                name="sinceMinutes"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Seconds"
                                type="number"
                                step="1"
                                name="sinceSeconds"
                              />
                            </div>
                          </div>

                          <div className="row dark-row">
                            <div className="col-lg-12"><label>Until</label></div>
                          </div>
                          <div className="row dark-row">
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Days"
                                type="number"
                                step="1"
                                name="untilDays"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Hours"
                                type="number"
                                step="1"
                                name="untilHours"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Minutes"
                                type="number"
                                step="1"
                                name="untilMinutes"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Seconds"
                                type="number"
                                step="1"
                                name="untilSeconds"
                              />
                            </div>
                          </div>

                          <div className="row dark-row">
                            <div className="col-lg-12"><label>Flag Until</label></div>
                          </div>
                          <div className="row dark-row">
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Days"
                                type="number"
                                step="1"
                                name="daysFlagged"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Hours"
                                type="number"
                                step="1"
                                name="hoursFlagged"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Minutes"
                                type="number"
                                step="1"
                                name="minutesFlagged"
                              />
                            </div>
                            <div className="col-lg-3">
                              <MyTextInput
                                label="Seconds"
                                type="number"
                                step="1"
                                name="secondsFlagged"
                              />
                            </div>
                          </div>
                          <div className="row dark-row">
                            <div className="col-lg-4">
                              <button type="submit">
                                <FormattedMessage
                                  id="nav.item.dataflowsummarydatetime"
                                  defaultMessage="Data Flow Summary by Offsets"
                                />
                              </button>
                            </div>
                          </div>
                        </Form>
                      );
                    }}
                  </Formik>
                )}
                <div className="row dark-row">
                  <div className="col-lg-12">
                    <ReactJson
                      src={this.state.dataflowSummaryJson}
                      name={false}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </>
    );
  }
}

export default injectIntl(Summary);
