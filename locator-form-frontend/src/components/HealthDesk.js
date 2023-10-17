import React from 'react'
import { useEffect } from "react";
import { Field, Formik, Form, setNestedObjectValues } from 'formik'
import { FormattedMessage, injectIntl } from 'react-intl'
import { Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9, Step10, Confirmation, Success } from './formSteps'
import { MyTextInput, MyHiddenInput ,StyledFieldSet ,StyledLegend ,MySelect, MyCheckbox} from './inputs/MyInputs'
import {Search} from './SearchBar';
import { healthDeskValidationSchema, pioValidationSchema } from './formModel/validationSchema'
import FormikErrorFocus from 'formik-error-focus'
import { healthOfficesList ,LocalityList ,getHealthOffice} from './data/healthOffices'
import { CircularProgress } from '@material-ui/core'

class HealthDesk extends React.Component {

  constructor(props) {
    super(props)
    this.formRef = React.createRef();
    this.state = {
      submitErrorKey: '',
      isSubmitting: false,
      submitSuccess: false,
      summaryAccessInfo: {},
      passengerSelected: false,
	  newSearch:true,
      formValues: {},
	  formKey: '1',
    }
  }

  submitForm = (values) => {
    this.setState({ isSubmitting: true })
    var json = JSON.stringify(values)
    console.log(json)
	const url = this.props.keycloak.hasRealmRole('health-desk-user') ? `${process.env.REACT_APP_DATA_IMPORT_API}/health-desk` : `${process.env.REACT_APP_DATA_IMPORT_API}/pio`;
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem("react-token")}`,
      },
      body: json
    }).then(async response => {
      this.setState({ isSubmitting: false })
      const summaryAccessInfo = await response.json()
      if (response.ok) {
        this.onSuccess(summaryAccessInfo)
      } else {
        throw new Error("didn't receive ok")
      }
    }).catch(err => {
      this.setState({ isSubmitting: false })
      console.log(err)
      this.setState({ 'submitErrorKey': 'error.submit' })
    })
  }

  handleSubmit = (values, actions) => {
    this.submitForm(values);
  }

  scrollToTopOfPage = () => {
	useEffect(() => {
		window.scrollTo(0, 0)
	  }, [])
  }

  scrollToElement = (element) => {
	const elementPosition = element.getBoundingClientRect().top;
    const offsetPosition = elementPosition - 175;
	window.scrollBy({
		top: offsetPosition,
		behavior: "smooth"
   });
  }

  scrollToHighestError = (formikErrors) => {
	  const errorKeys = Object.keys(formikErrors);
	  var topElementPosition;
	  for (var i = 0; i < errorKeys.length; i++) {
		const elementName = errorKeys[i];
		const element = document.getElementsByName(elementName)[0];
		const curElementPosition = element.getBoundingClientRect().top;
		if (!topElementPosition || topElementPosition > curElementPosition) {
			topElementPosition = curElementPosition;
		}
	  }

    const offsetPosition = topElementPosition - 175;
	window.scrollBy({
		top: offsetPosition,
		behavior: "smooth"
   });
  }

  onSuccess = (summaryAccessInfo) => {
    this.setState({ submitErrorKey: '', submitSuccess: true, passengerSelected: false, formValues: {}, formKey: "1"  });
	this.scrollToTopOfPage();
    
  }
  
  searchSuccess = (locatorForm, key) => {
	locatorForm.arrivalDateOverride = false;
	this.setState({newSearch: true, passengerSelected: true, submitSuccess: false, formValues: locatorForm, formKey: key,});
  }

  handleReset= () => {
	this.setState({formValues: {} ,passengerSelected: false})
  }

  render() {
		    const  currentValidationShema = this.props.keycloak.hasRealmRole('health-desk-user') ? healthDeskValidationSchema : pioValidationSchema;
		    const role = this.props.keycloak.hasRealmRole('health-desk-user') ? 'healthDesk' : 'pio';
			return (
		      <>
		        <div className="container-fluid d-flex min-vh-100 flex-column content">
		          <div className="row dark-row">
	              <div className="col-lg-11 ">
	              </div>
	              <div className="col-lg-1 ">
	              	<button style={{display:'none'}} id="logout-button" type="button" onClick={this.logout}>
	              		<FormattedMessage id="nav.item.logout" defaultMessage="logout" />
	              	</button>
	              </div>
	              </div>
		          <div className="row dark-row">
	              <div className="col-lg-12 ">
		                <div className="container pt-3">
		                	<div className="container">  
		                  		<h3 className="question-header"> 
									{this.props.keycloak.hasRealmRole('health-desk-user') &&
										<FormattedMessage id="nav.item.header.healthdesk" defaultMessage="Health Desk" />
									}
									{this.props.keycloak.hasRealmRole('pio-user') &&
										<FormattedMessage id="nav.item.header.pio" defaultMessage="Passport And Immigration Office Module" />
									} 
								  </h3>
		                    </div>
		                </div>
		              </div>
		          </div>
		          <div className="row light-row flex-grow-1">
		              <div className="col-lg-12 ">
		              	<div className="container pt-3">
							{this.state.submitSuccess && 
								<div className="success-message">
								<FormattedMessage id="nav.item.success" defaultMessage="Save Successful"/>
								</div>
							}
		              			<Search onSearchSuccess={this.searchSuccess} intl={this.props.intl}/>
		              		{this.state.passengerSelected && 
		              	
		        <Formik
		          initialValues={this.state.formValues}
		          enableReinitialize
		          validationSchema={currentValidationShema}
		          onSubmit={this.handleSubmit}
		          validateOnMount
		          key={this.state.formKey}
				  onReset={this.handleReset}
		        >{formikProps => {
					if (!formikProps.values.finalized) {
						useEffect(() => {
							const validateFormik = async () => {
								const formikErrors = await formikProps.validateForm();
								delete formikErrors.reviewed;
								if (Object.keys(formikErrors).length > 0) {
									formikProps.setTouched(setNestedObjectValues(formikErrors, true));
									this.scrollToHighestError(formikErrors);
								}
							}
							validateFormik();
							}, [this.state.formKey]);
					}
 
							const locality = formikProps.values.locality;
							const healthOffice = formikProps.values.healthOffice;
							formikProps.values.healthOffice = locality ? getHealthOffice(locality) : healthOffice 
						
					return (
		          <Form>				  	
				  {console.log(formikProps)}
		            <div className="questions" id="questions">
						<StyledFieldSet>
							<StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.passenderDetails' })}</StyledLegend>
							{this.props.keycloak.hasRealmRole('health-desk-user') &&
								<Step1 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							}
							<Step3 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							<Step7 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							<Step8 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							{this.props.keycloak.hasRealmRole('health-desk-user') &&
								<Step9 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							}
						</StyledFieldSet>
						<StyledFieldSet>
							<StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.flightDetails' })}</StyledLegend>
							<Step2 role={role} formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							{(formikProps.errors.arrivalDate === 'error.date.future' 
								|| (formikProps.values.arrivalDateOverride && !formikProps.errors.arrivalDate )) 
								&& 
								<MyCheckbox
									name="arrivalDateOverride"
									checkboxDescription={<FormattedMessage id="nav.item.overrideArrival" defaultMessage="Date of Arrival is not today" />}
								/>
							}
						</StyledFieldSet>
						<StyledFieldSet>
							<StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.details' })}</StyledLegend>
							<Step4 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							{this.props.keycloak.hasRealmRole('health-desk-user') &&
								<Step5 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							}
						</StyledFieldSet>

						{this.props.keycloak.hasRealmRole('health-desk-user') &&
							<StyledFieldSet>
								<StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.vaccine' })}</StyledLegend>
								<Step6 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} />
							</StyledFieldSet>
						}
					 
		            	{/* <Step10 formikProps={formikProps} intl={this.props.intl} disabled={this.state.formValues.finalized} /> */}
						<div className="row">
						<div className="col-lg-4 form-group">
							<MyHiddenInput
								name="arrivalDateOverride"
								type="hidden"
							/>
							<MyHiddenInput
								name="taskId"
								type="hidden"
							/>
							<MyHiddenInput
								name="subTaskId"
								type="hidden"
							/>
							<MyHiddenInput
								name="serviceRequestId"
								type="hidden"
							/>
							<MyHiddenInput
								name="patientId"
								type="hidden"
							/>
							<MyHiddenInput
								name="specimenId"
								type="hidden"
							/>
							{formikProps.values.familyTravelCompanions.length > 0 &&
									formikProps.values.familyTravelCompanions.map((comp, index) => (
										<React.Fragment key={index}>
										<Field className="form-control"
														name={`familyTravelCompanions.${index}.subTaskId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field  className="form-control"
														name={`familyTravelCompanions.${index}.serviceRequestId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field className="form-control"
														name={`familyTravelCompanions.${index}.patientId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field className="form-control"
														name={`familyTravelCompanions.${index}.specimenId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										</React.Fragment>
										
							))}
							{formikProps.values.nonFamilyTravelCompanions.length > 0 &&
									formikProps.values.nonFamilyTravelCompanions.map((comp, index) => (
										<React.Fragment key={index}>
										<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.subTaskId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field  className="form-control"
														name={`nonFamilyTravelCompanions.${index}.serviceRequestId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.patientId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"
															
														/>
														}
										</Field>
										<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.specimenId`}>
														{({ field, form, meta }) =>
															<MyHiddenInput
															name={field.name}
															type="hidden"														
														/>
														}
										</Field>
										</React.Fragment>
										
							))}
							{this.props.keycloak.hasRealmRole('health-desk-user') && 
							  <>							
							<StyledFieldSet>
								 <StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.healthOffice' })}</StyledLegend>			 								 
									<Field name="locality">
										{({ field, form, meta }) =>
											<MySelect form={form} 
												name={field.name}
												options={LocalityList}
												isMulti={false}
												isSearchable={true}
												placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
												label={<FormattedMessage id="nav.item.form.label.locality" defaultMessage="Locality" />}
												disabled={this.state.formValues.finalized}
												requireField={true} 
											/>}
									</Field>								
								 <Field name="healthOffice">
								  {({ field, form, meta }) =>
									<MySelect form={form}
										name={field.name}
										options={healthOfficesList}
										isMulti={false}
										isSearchable={true}
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										label={<FormattedMessage id="nav.item.form.label.healthOffice" defaultMessage="Health Office" />}
										disabled={this.state.formValues.finalized}
									/>}
							     </Field>
							 </StyledFieldSet>
							    <StyledFieldSet>
									<StyledLegend>{this.props.intl.formatMessage({ id: 'nav.item.form.label.testkit' })}</StyledLegend>			
									<MyTextInput
										label={<FormattedMessage id="nav.item.testKitId" defaultMessage="Test Kit ID" />}
										name="testKitId"
										requireField={false}
										displayErrorBeforeTouched={true}
										type="text"
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.form.search.placeholder.testkit' })}
										disabled={this.state.formValues.finalized}
										// disabled={this.state.searching || this.state.confirming} 
									/>
								</StyledFieldSet>
								</>}
						</div>
								</div>
								</div>
								{this.props.keycloak.hasRealmRole('health-desk-user') && 
									<div className="row">
									<div className="col-lg-4 form-group">
									<MyCheckbox
										name="reviewed"
										checkboxDescription={<FormattedMessage id="nav.item.healthdesk.reviewed" defaultMessage="All Information Reviewed" />}
									/>
									</div>
									</div>
								}
								<div className="row">
								<div className="col-lg-4 form-group">
		                  <button
		                    disabled={this.state.isSubmitting || !formikProps.isValid}
		                    type="submit"
		                    className={'confirm-button'}
		                  >
		                    {<FormattedMessage id="nav.item.submit" defaultMessage="Submit" /> }
		                  </button>

							<button
								disabled={this.state.isSubmitting || !formikProps.isValid}
								type="reset"
								className={'confirm-button'}
							>
								{<FormattedMessage id="nav.item.cancel" defaultMessage="Cancel and Reset Form" />}
							</button>

		                  {this.state.isSubmitting && (
		                    <CircularProgress
		                      size={24}
		                    />
		                  )}
		                  {this.state.submitErrorKey &&
		                    <div className="error"><FormattedMessage id={this.state.submitErrorKey} defaultMessage="Error"/></div>
		                  }
		                </div>
		            </div>
					<FormikErrorFocus
						// See scroll-to-element for configuration options: https://www.npmjs.com/package/scroll-to-element
						offset={-175}
						align={'top'}
						focusDelay={200}
						ease={'linear'}
						duration={100}
					/>
		          </Form >
		        )}}
		        </Formik>
		        }
		      </div>
		      </div>
		      </div>
		      </div>
		      </>
		    );
		        }
}

export default injectIntl(HealthDesk)