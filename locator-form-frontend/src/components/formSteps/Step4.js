import React from "react"
import { FormattedMessage } from 'react-intl'
import { Field } from 'formik'
import { MyTextInput, MySelect } from '../inputs/MyInputs'
import { countriesList } from '../data/countries.js'

class Step4 extends React.Component {

	render() {
		return <div>

			<div className="step" id="step4">
				<div id="healthInformation" className="section">
					<div className="row">
						<div className="col-lg-4 form-group ">
							<Field name="countriesVisited">
								{({ field, form, meta }) =>
									<MySelect form={form}
										name={field.name}
										options={countriesList}
										isMulti={true}
										isSearchable={true}
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										label={<FormattedMessage id="nav.item.countriesVisited" defaultMessage="Countries visited during last 6 months" />}
										disabled={this.props.disabled}
									/>}
							</Field>
						</div>
						<div className="col-lg-4 form-group ">
							<MyTextInput
								label={<FormattedMessage id="nav.item.portOfEmbarkation" defaultMessage="Port Of Embarkation" />}
								name="portOfEmbarkation"
								type="text"
								disabled={this.props.disabled}
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	}

}
export default Step4