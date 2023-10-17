import React from "react"
import { FormattedMessage } from 'react-intl'
import { MyRadioInputGroup } from '../inputs/MyInputs'
import { Field } from 'formik'
import { MyTextInput, MySelect, dateInputToday } from '../inputs/MyInputs'
import { vaccines } from '../data/vaccines'

class Step6 extends React.Component {

	render() {
		return <div>

			<div className="step" id="step6">
				<div id="vaccine" className="section">
					<div className="row align-items-end">
					<div className="col-lg-10 form-group">
								<MyRadioInputGroup
								label={<FormattedMessage id="nav.item.vaccinated" defaultMessage="Vaccinated?" />}
								name="vaccinated"
								requireField={false}
								options={[
									{ key: 'nav.item.symptoms.option.yes', value: 'true' },
									{ key: 'nav.item.symptoms.option.no', value: 'false'}
								]}
								disabled={this.props.disabled}
							/>
							</div>
						</div>
						<div className="row">
						  <div className="col-lg-4 form-group">
								<Field name="firstVaccineName">
								{({ field, form, meta }) =>
									<MySelect label={<FormattedMessage id="nav.item.firstVaccine" defaultMessage="Name of First Vaccine" />}
										name={field.name} 
										form={form}
										requireField={false} 
										isSearchable={true}
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										options={vaccines}
										disabled={this.props.disabled}
									/>
								}
							  </Field>
						  </div>
						
						  <div className="col-lg-4 form-group">
									<MyTextInput
									label={<FormattedMessage id="nav.item.dateOfFirstDose" defaultMessage="Date Of First Dose" />}
									name="dateOfFirstDose"
									requireField={false}
									type="date"
									placeholder={this.props.intl.formatMessage({ id: 'date.format' })}
									max={dateInputToday()}
									disabled={this.props.disabled}
								/>
						  </div>
						</div>
						<div className="row">
						  <div className="col-lg-4 form-group">
								<Field name="secondVaccineName">
								{({ field, form, meta }) =>
									<MySelect label={<FormattedMessage id="nav.item.secondVaccine" defaultMessage="Name of Second Vaccine" />}
										name={field.name} 
										form={form}
										requireField={false} 
										isSearchable={true}
										placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
										options={vaccines}
										disabled={this.props.disabled}
									/>
								}
							  </Field>
						  </div>
						  <div className="col-lg-4 form-group">
									<MyTextInput
									label={<FormattedMessage id="nav.item.dateOfSecondDose" defaultMessage="Date Of Second Dose" />}
									name="dateOfSecondDose"
									requireField={false}
									type="date"
									placeholder={this.props.intl.formatMessage({ id: 'date.format' })}
									max={dateInputToday()}
									disabled={this.props.disabled}
								/>
						  </div>
						</div>
					</div>
				</div>
			</div>
	}

}
export default Step6