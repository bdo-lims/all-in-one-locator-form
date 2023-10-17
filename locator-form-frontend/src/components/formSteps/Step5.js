import React from "react"
import { FormattedMessage } from 'react-intl'
import { MyRadioInputGroup } from '../inputs/MyInputs'

class Step5 extends React.Component {

	render() {
		return <div>

			<div className="step" id="step5">
				<div id="sufferingInformation" className="section">
					<div className="row">
						<div className="col-lg-12 ">
							<h6>
								<FormattedMessage id="nav.item.areYouSufferingFrom" defaultMessage="Have you experienced any of the following within the past 14 days?" />
							</h6>
						</div>
					</div>
					<div className="row align-items-end">
						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.fever" defaultMessage="Fever" />}
								name="fever"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>

						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.soreThroat" defaultMessage="Sore Throat" />}
								name="soreThroat"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>
						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.jointPain" defaultMessage="Joint Pain" />}
								name="jointPain"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>
						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.cough" defaultMessage="Cough" />}
								name="cough"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>
						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.breathingDifficulties" defaultMessage="Breathing Difficulties" />}
								name="breathingDifficulties"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>
						<div className="col-xl-2 col-lg-4 form-group ">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.rash" defaultMessage="Rash" />}
								name="rash"
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
						</div>
					</div>
					<div className="row align-items-end">
								<div className="col-xl-3 col-lg-4 form-group ">
								<MyRadioInputGroup
									label={<FormattedMessage id="nav.item.smellOrTaste" defaultMessage="Loss of sense of smell or taste" />}
									name="smellOrTaste"
									options={[
										{ key: 'nav.item.symptoms.option.yes', value: 'true' },
										{ key: 'nav.item.symptoms.option.no', value: 'false'}
									]}
									disabled={this.props.disabled}
								/>
							   </div>
									<div className="col-xl-3 col-lg-4 form-group ">
									<MyRadioInputGroup
										label={<FormattedMessage id="nav.item.contact" defaultMessage="Possible contact with COVID 19" />}
										name="contact"
										options={[
											{ key: 'nav.item.symptoms.option.yes', value: 'true' },
											{ key: 'nav.item.symptoms.option.no', value: 'false'}
										]}
										disabled={this.props.disabled}
									/>
								</div>
								<div className="col-lg-6 form-group ">
								<MyRadioInputGroup
									label={<FormattedMessage id="nav.item.hadCovidBefore" defaultMessage="Have you tested positive for Covid-19 in the past 7 days?" />}
									name="hadCovidBefore"
									options={[
										{ key: 'nav.item.symptoms.option.yes', value: 'true' },
										{ key: 'nav.item.symptoms.option.no', value: 'false'}
									]}
									disabled={this.props.disabled}
								/>
							</div>
					</div>
				</div>
			</div>
		</div>
	}

}
export default Step5