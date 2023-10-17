import React from "react"
import { FormattedMessage } from 'react-intl'
import { Field } from 'formik'
import { faSearch } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { MyTextInput, MySelect, dateInputToday } from '../inputs/MyInputs'
import { CircularProgress } from '@material-ui/core'

class Step3 extends React.Component {

	constructor(props) {
		super(props)
		this.state = {
			confirming: false,
			searching: false,
			searchFinalized: false,
			searchFailed: false,
			displayFailureReason: false,
			failureReason: ''
		}
	}

	checkNationalIdOnServer = (nationalId) => {
		console.log('National Id: ' + nationalId)
		this.setState({ searching: true });
		fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/resident/${nationalId}`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
			},
		}).then(async response => {
			const status = response.status;
			if (response.ok) {
				const resident = await response.json();
				this.onSuccess(resident)
			} else if (status === 404) {
				return this.onNotFound();
			} else { 
				throw new Error("didn't receive resident due to error")
			}
		}).catch(err => {
			console.log(err)
			this.setState({ 
				searching: false ,
				searchFailed: true,
				searchFinalized: true,
				displayFailureReason: false,
				failureReason: 'error.search.infohighway.error' 
			})

		})
	}

	onNotFound = () => {
		console.log("national ID not found")
		this.setState({
			searching: false, 
			searchFailed: true,
			searchFinalized: false,
			displayFailureReason: true,
			failureReason: 'error.search.infohighway.notfound' 
		});
	}

	onSuccess = (resident) => {
		console.log(JSON.stringify(resident))
		this.props.formikProps.setFieldValue('firstName', resident.firstName);
		this.props.formikProps.setFieldValue('lastName', resident.lastInitial);
		this.props.formikProps.setFieldValue('nationalID', resident.nationalID);
		this.setState({ 
			confirming: true, 
			searchFinalized: true,
			searching: false,
			displayFailureReason: false,
			searchFailed: false,});
	}

	confirm = () => {
		this.setState({confirming: false});
	}

	reject = () => {
		this.props.formikProps.setFieldValue('firstName', '');
		this.props.formikProps.setFieldValue('lastName', '');
		this.setState({
			confirming: false,
			searchFinalized: false,
		});
	}

	renderNationalIDField = () => {

		const nationalIDInputConfirm = () => {
			return (
				<>
				<button 
					type="button" 
					onClick={this.confirm}
					className="confirm-button"
				>
					<FormattedMessage id="nav.item.confirm" defaultMessage="Confirm"/>
				</button>
				<button 
					type="button" 
					onClick={this.reject}
					className="reject-button"
				>
					<FormattedMessage id="nav.item.reject" defaultMessage="Reject"/>
				</button>
				</>
			);
		};

		const errorMessage = () => {
			return (!this.state.searching && this.state.searchFailed && this.state.displayFailureReason ? 
				<FormattedMessage id={this.state.failureReason} defaultMessage="Error" /> 
				:
				<></>
			);
		};

		const getInstructions = () => {
			return (
				<div className="col-lg-8">
				{(!this.state.searchFinalized || this.state.displayFailureReason) && 
					<div className="instruction">
					<FormattedMessage id="nationalId.search.instructions.begin" defaultMessage="Please enter your National ID and click the magnifying glass"/> 
					</div>
				}
				{this.state.confirming && 
					<>{nationalIDInputConfirm()}</>
				} 
				{(this.state.searchFinalized && !this.state.displayFailureReason && ! this.state.confirming) && 
					<div className="instruction">
						<FormattedMessage id="nationalId.search.instructions.continue" defaultMessage="Thank you, please continue to fill out the form" />
					</div>
				} 

				</div>
			);
		};

		const nationalIDInput = () => {
			return (
			<div className="row">
				<div className="col-lg-4 form-group">
					<MyTextInput
						label={<FormattedMessage id="nav.item.nationalId" defaultMessage="National ID" />}
						name="nationalID"
						type="text"
						requireField={true}
						icon={<FontAwesomeIcon icon={faSearch}/>}
						iconClickable={true}
						iconOnClick={e => {
							this.checkNationalIdOnServer(this.props.formikProps.values.nationalID)
						}}
						additionalErrorMessage={errorMessage()}
						// disabled={this.state.searching || this.state.confirming} 
					/>
					
					{this.state.searching && (
						<CircularProgress
						size={24}
						/>
					)}
				</div>
				<div className="col-lg-8 form-group">
					<label>&nbsp;</label>
					{getInstructions()}
				</div>
			</div>
			);
		};

		return nationalIDInput();
	}

	render() {
		return <div>
			<div className="step" id="step3">
				<div id="personalInformation" className="section">
					{this.props.formikProps.values.travellerType === 'resident' &&
						this.renderNationalIDField()
					}
					<div className="row">
						<div className="col-lg-2 form-group">
							<Field name="title">
								{({ field, form, meta }) =>
									<MySelect 
									// disabled={this.state.searching || this.state.confirming} 
									label={<FormattedMessage id="nav.item.title" defaultMessage="Title" />}
										name="title" form={form} placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										requireField={true}
										options={
											[
												{ "value": "mr", "label": this.props.intl.formatMessage({ id: 'nav.item.title.option.mr' }) },
												{ "value": "mrs", "label": this.props.intl.formatMessage({ id: 'nav.item.title.option.mrs' }) },
												{ "value": "ms", "label": this.props.intl.formatMessage({ id: 'nav.item.title.option.ms' }) },
												{ "value": "miss", "label": this.props.intl.formatMessage({ id: 'nav.item.title.option.miss' }) },
												{ "value": "dr", "label": this.props.intl.formatMessage({ id: 'nav.item.title.option.dr' }) },
											]}
											disabled={this.props.disabled}
									/>
								}
							</Field>
						</div>
						<div className="col-lg-4 form-group">

							<MyTextInput
								label={<FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />}
								name="lastName"
								requireField={true}
								type="text"
								disabled={this.props.disabled}
								// disabled={this.state.searching || this.state.confirming} 
							/>
						</div>
						<div className="col-lg-4 form-group">
							<MyTextInput
								label={<FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />}
								name="firstName"
								requireField={true}
								type="text"
								disabled={this.props.disabled}
								// disabled={this.state.searching || this.state.confirming} 
							/>
						</div>
						<div className="col-lg-2 form-group">

							<MyTextInput
								label={<FormattedMessage id="nav.item.middleInitial" defaultMessage="Middle Initial" />}
								name="middleInitial"
								type="text"
								disabled={this.props.disabled}
								// disabled={this.state.searching || this.state.confirming} 
							/>
						</div>
					</div>
					<div className="row">
						<div className="col-lg-2 form-group">
							<Field name="sex">
								{({ field, form, meta }) =>
									<MySelect 
									// disabled={this.state.searching || this.state.confirming} 
									label={<FormattedMessage id="nav.item.sex" defaultMessage="Sex" />}
										name={field.name} form={form} placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										requireField={true}
										options={
											[
												{ "value": "male", "label": this.props.intl.formatMessage({ id: 'nav.item.sex.option.male' }) },
												{ "value": "female", "label": this.props.intl.formatMessage({ id: 'nav.item.sex.option.female' }) },
											]}
										disabled={this.props.disabled}
									/>
								}
							</Field>
						</div>
						<div className="col-lg-3 form-group">
							<MyTextInput
								label={<FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />}
								name="dateOfBirth"
								type="date"
								placeholder={this.props.intl.formatMessage({ id: 'date.format' })}
								requireField={true}
								max={dateInputToday()}
								disabled={this.props.disabled}
								// disabled={this.state.searching || this.state.confirming} 
							/>
						</div>
						<div className="col-lg-4 form-group">
						<MyTextInput
							label={<FormattedMessage id="nav.item.profession" defaultMessage="Profession" />}
							name="profession"
							type="text"
							disabled={this.props.disabled}
							// disabled={this.state.searching || this.state.confirming} 
						/>
					</div>
				  </div>
				</div>
			</div>
		</div>
	}

}
export default Step3