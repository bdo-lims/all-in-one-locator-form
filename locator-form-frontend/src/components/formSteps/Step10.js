import React from "react"
import { FormattedMessage } from 'react-intl'
import { Field, FieldArray } from 'formik'
import { MyTextInput, MySelect, dateInputToday } from '../inputs/MyInputs'
import { countriesList } from '../data/countries.js'

class Step10 extends React.Component {

	render() {
		return <div>
			<div className="step" id="step10">
				<div id="travelCompanionsInformation" className="section">
					<div className="row">
						<div className="col-lg-12 ">
							<h6 className="notation"> <FormattedMessage id="nav.item.travelCompanionsNotation" defaultMessage="If you fill the information about your travel companions on this locator form, individual locator forms will not be required for these companions. Barcodes will be generated for each companion" /></h6>
							<h5> <FormattedMessage id="nav.item.travelCompanionsFamily" defaultMessage="Travel Companions Family" /></h5>
						</div>
					</div>
					<FieldArray
						name="familyTravelCompanions"
						render={({ remove, push }) => (

							<div className="travelCompanion">
								{this.props.formikProps.values.familyTravelCompanions.length > 0 &&
									this.props.formikProps.values.familyTravelCompanions.map((comp, index) => (
										<div key={index}>

											<div className="row">
												<div className="col-lg-4 form-group ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.lastName`}>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-4 form-group ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.firstName`}
													>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-2 form-group ">
													<Field name={`familyTravelCompanions.${index}.sex`}>
														{({ field, form, meta }) =>
															<MySelect
																requireField={true} label={<FormattedMessage id="nav.item.sex" defaultMessage="Sex" />}
																name={field.name} form={form} placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
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
											</div>
											<div className="row">
												<div className="col-lg-2 ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.seatNumber`}
													>
														{({ field, form, meta }) =>
															<MyTextInput
																label={<FormattedMessage id="nav.item.seat" defaultMessage="Seat" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.dateOfBirth`}>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />}
																name={field.name}
																type="date"
																placeholder={this.props.intl.formatMessage({ id: 'date.format' })}
																max={dateInputToday()}
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>

												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.nationality`}
													>
														{({ field, form, meta }) =>
															<MySelect form={form}
																name={field.name}
																options={countriesList}
																isSearchable={true}
																requireField={true}
																placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
																label={<FormattedMessage id="nav.item.nationality" defaultMessage="Nationality"/>}
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`familyTravelCompanions.${index}.passportNumber`}>
														{({ field, form, meta }) =>
															<MyTextInput
																label={<FormattedMessage id="nav.item.passportNumber" defaultMessage="Passport Number" />}
																name={field.name}
																requireField={true}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-1 ">
													<button
														type="button"
														className="remove-button"
														onClick={() => remove(index)}
													>
														X
				</button>
												</div>
											</div>
										</div>

									))}

								<button
									type="button"
									className="add-button"
									onClick={() => push({
										lastName: this.props.formikProps.values.lastName,
										firstName: "",
										middleInitial: "",
										seatNumber: "",
										dateOfBirth: "",
										sex: "",
										nationality: this.props.formikProps.values.nationality,
										passportNumber: "",
									})}
								>
									<FormattedMessage id="nav.item.addTravelCompanionFamily" defaultMessage="Add Travel Companion Family" />
								</button>
							</div>
						)
						}
					/>
				</div >
				<div id="nonFamilyTravelCompanionInformation" className="section">

					<div className="row">
						<div className="col-lg-12 ">
							<h5> <FormattedMessage id="nav.item.travelCompanionsNonFamily" defaultMessage="Travel Companions Non-Family" /></h5>
						</div>
					</div>
					<FieldArray
						name="nonFamilyTravelCompanions"
						render={({ remove, push }) => (

							<div className="travelCompanion">
								{this.props.formikProps.values.nonFamilyTravelCompanions.length > 0 &&
									this.props.formikProps.values.nonFamilyTravelCompanions.map((comp, index) => (
										<div key={index}>

											<div className="row">
												<div className="col-lg-4 form-group ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.lastName`}>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-4 form-group ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.firstName`}
													>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-2 form-group ">
													<Field name={`nonFamilyTravelCompanions.${index}.sex`}>
														{({ field, form, meta }) =>
															<MySelect
																requireField={true} label={<FormattedMessage id="nav.item.sex" defaultMessage="Sex" />}
																name={field.name} form={form} placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
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
											</div>
											<div className="row">
												<div className="col-lg-2 ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.seatNumber`}
													>
														{({ field, form, meta }) =>
															<MyTextInput
																label={<FormattedMessage id="nav.item.seat" defaultMessage="Seat" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.dateOfBirth`}>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />}
																name={field.name}
																type="date"
																placeholder={this.props.intl.formatMessage({ id: 'date.format' })}
																max={dateInputToday()}
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>

												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.nationality`}
													>
														{({ field, form, meta }) =>
															<MySelect form={form}
																requireField={true}
																name={field.name}
																options={countriesList}
																isSearchable={true}
																placeholder={this.props.intl.formatMessage({ id: 'nav.item.select.placeholder' })}
																label={<FormattedMessage id="nav.item.nationality" defaultMessage="Nationality" />}
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-3 form-group ">
													<Field className="form-control"
														name={`nonFamilyTravelCompanions.${index}.passportNumber`}>
														{({ field, form, meta }) =>
															<MyTextInput
																requireField={true}
																label={<FormattedMessage id="nav.item.passportNumber" defaultMessage="Passport Number" />}
																name={field.name}
																type="text"
																disabled={this.props.disabled}
															/>
														}
													</Field>
												</div>
												<div className="col-lg-1">
													<button
														type="button"
														className="remove-button"
														onClick={() => remove(index)}
													>
														X
								</button>
												</div>
											</div>
										</div>

									))}
								<div className="row">
									<div className="col-lg-12 ">
										<button
											type="button"
											className="add-button"
											onClick={() => push({
												lastName: "",
												firstName: "",
												middleInitial: "",
												seatNumber: "",
												dateOfBirth: "",
												sex: "",
												nationality: this.props.formikProps.values.nationality,
												passportNumber: "",
											})}
										>
											<FormattedMessage id="nav.item.addTravelCompanionNonFamily" defaultMessage="Add Travel Companion Non-Family" />
										</button>
									</div>
								</div>
							</div>
						)}
					/>
				</div >
			</div>
		</div>
	}

}
export default Step10