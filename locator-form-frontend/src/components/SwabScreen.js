import React from 'react'
import { Field, Formik, Form } from 'formik'
import { FormattedMessage, injectIntl } from 'react-intl'
import { SwabSearchBar } from './SearchBar';

class SwabScreen extends React.Component {

	constructor(props) {
		super(props)
		this.state = {
			submitErrorKey: '',
			isSubmitting: false,
			formValues: {},
			formKey: '1',
			displayPassengerDetails: false
		}
	}

	searchSuccess = (locatorForm, key) => {
		this.setState({ displayPassengerDetails: true, formValues: locatorForm, formKey: key });
	}

	searchFail = () => {
		this.setState({ displayPassengerDetails: false, formValues: {}});
	}

	render() {
		return (
			<>
				<div className="container-fluid d-flex min-vh-100 flex-column content">
					<div className="row dark-row">
						<div className="col-lg-11 ">
						</div>
						<div className="col-lg-1 ">
							<button style={{ display: 'none' }} id="logout-button" type="button" onClick={this.logout}>
								<FormattedMessage id="nav.item.logout" defaultMessage="logout" />
							</button>
						</div>
					</div>
					<div className="row dark-row">
						<div className="col-lg-12 ">
							<div className="container pt-3">
								<div className="container">
									<h3 className="question-header">
										<FormattedMessage id="nav.item.header.swabscreen" defaultMessage="Swab Screen" />
									</h3>
								</div>
							</div>
						</div>
					</div>
					<div className="row light-row flex-grow-1">
						<div className="col-lg-12 ">
							<div className="container pt-3">
								{this.props.keycloak.hasRealmRole('swab-screen-user') &&
									<SwabSearchBar onSearchSuccess={this.searchSuccess} onSearchFail={this.searchFail} intl={this.props.intl} />
								}
							</div>
						</div>
					</div>

					{this.state.displayPassengerDetails && (
						<div className="row light-row flex-grow-1">
							<div className="col-lg-12">
								<div className="container mt-0">
									<div className="row">
										<div className="col-lg-6 form-group">
											<div class="card">
												<div class="card-header"><span className="confirm-field"><FormattedMessage id="nav.item.testkit.number" defaultMessage="Test Kit Number" />: </span><span className="confirm-value">{this.state.formValues.testKidNumber}</span></div>
												<div class="card-header"><span className="confirm-field"><FormattedMessage id="nav.item.passengername" defaultMessage="Passenger Name" />: </span><span className="confirm-value">{this.state.formValues.passengerName}</span></div>
												<div class="card-header"><span className="confirm-field"><FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Passenger Date Of Birth" />: </span><span className="confirm-value"> {this.state.formValues.passengerDob}</span></div>
											</div>																
										</div>
									</div>
								</div>
							</div>
												
						</div>
					)}
					
				</div>
			</>
		);
	}
}

export default injectIntl(SwabScreen)