import React from "react"
import { FormattedMessage } from 'react-intl'
import { MyRadioInputGroup } from '../inputs/MyInputs'

// workaround for conditional validation in permanent address
const onInputChange = (e, form) => {
	form.handleChange(e)
	if (e.target.value === 'resident') {
		form.setFieldValue('countryOfBirth', 'MU')
		form.setFieldValue('nationality', 'MU')
		form.setFieldValue('emergencyContact.country', 'MU')
		form.setFieldValue('permanentAddress.country', 'MU')
		form.setFieldValue('countryOfPassportIssue', 'MU')
		form.setFieldValue('passengerNationality', ['MAURITIAN'])
	} else {
		form.setFieldValue('countryOfBirth', '')
		form.setFieldValue('nationality', '')
		form.setFieldValue('emergencyContact.country', '')
		form.setFieldValue('permanentAddress.country', '')
		form.setFieldValue('countryOfPassportIssue', '')
		form.setFieldValue('passengerNationality', '')	 
	}
	form.setFieldValue('permanentAddress.travellerType', e.target.value)
	form.setFieldValue('travellerType', e.target.value)
}

class Step1 extends React.Component {

	render() {
		return <>
			<div className="step" id="step1">
				<div id="passengerTypeInformation" className="section">
					<div className="row">
						<div className="col-lg-10 form-group">
							<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.travellerTypeQuestion" defaultMessage="Are you a Citizen, or Non-Citizen of Mauritius?" />}
								name="travellerType"
								onInputChange={e => {
									onInputChange(e, this.props.formikProps)
								}}
								requireField={true}
								disabled={this.props.disabled}
								options={[
									{ key: 'nav.item.resident', value: 'resident' },
									{ key: 'nav.item.nonresident', value: 'nonresident'}
								]}
							/>
						</div>
					</div>
				</div>
			</div>
		</>
	}
}


export default Step1