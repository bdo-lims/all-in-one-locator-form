import React from "react"
import { FormattedMessage } from 'react-intl'
import { getCountryFromCode } from '../data/countries.js'



class Summary extends React.Component {

	render() {

		const { travellerType, airlineName, flightNumber, seatNumber, arrivalDate, arrivalTime ,title, firstName, lastName, middleInitial,
			sex,dateOfBirth, profession, lengthOfStay,countriesVisited, portOfEmbarkation, hadCovidBefore, fever, soreThroat, jointPain,
			cough, breathingDifficulties, rash, smellOrTaste, contact, visitPurpose, 
			vaccinated, firstVaccineName, secondVaccineName, dateOfFirstDose, dateOfSecondDose,
			mobilePhone, fixedPhone, businessPhone, email, passportNumber, passportExpiryDate, 
			nationality, countryOfBirth, countryOfPassportIssue,  permanentAddress, temporaryAddress, 
			emergencyContact, familyTravelCompanions, nonFamilyTravelCompanions, passengerNationality ,contactPerson
		} = this.props.formikProps.values;

		const convertBoolean = (value) => {
			if (value == 'true') {
				return "Yes"
			} else if (value == 'false') {
				return "No"
			} else{
				return value
			};
		}

		const convertResdentToCitzen = (value) => {
			if (value == 'resident') {
				return "Citizen"
			} else if (value == 'nonresident') {
				return "Non Citizen"
			} else{
				return value
			};
		}

		const removeUndercore = (value) => {
			return value.replace(/_/g, ' ');
		}
		return <div>

			<div id="travellerTypeInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.travellersection" defaultMessage="Traveller" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-12 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.travellerType" defaultMessage="Passenger Type" />: </span><span className="confirm-value">{convertResdentToCitzen(travellerType)}</span>
					</div>
				</div>
			</div>

			<div id="flightInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.flightInformation" defaultMessage="Flight Information" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.airline" defaultMessage="Airline" />: </span><span className="confirm-value">{airlineName}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.flightNumber" defaultMessage="Flight" />: </span><span className="confirm-value">{flightNumber}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.seat" defaultMessage="Seat" />: </span><span className="confirm-value">{seatNumber}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.dateOfArrival" defaultMessage="Date Of Arrival" />: </span><span className="confirm-value">{arrivalDate}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.timeOfArrival" defaultMessage="Time Of Arrival" />: </span><span className="confirm-value">{arrivalTime}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.purposeOfVisit" defaultMessage="Purpose of Visit" />: </span><span className="confirm-value">{removeUndercore(visitPurpose)}</span>
					</div>
				</div>
			</div>
			<div id="personalInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.personalInformation" defaultMessage="Personal Information" /> </h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-2 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.title" defaultMessage="Title" />: </span><span className="confirm-value">{title}</span>
					</div>
					<div className="col-lg-4 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />: </span><span className="confirm-value">{lastName}</span>
					</div>
					<div className="col-lg-4 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />: </span><span className="confirm-value">{firstName}</span>
					</div>
					<div className="col-lg-2 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.middleInitial" defaultMessage="Middle Initial" />: </span><span className="confirm-value">{middleInitial}</span>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-2 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.sex" defaultMessage="Sex" />: </span><span className="confirm-value">{sex}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />: </span><span className="confirm-value">{dateOfBirth}</span>
					</div>
					<div className="col-lg-3 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.profession" defaultMessage="Profession" />: </span><span className="confirm-value">{profession}</span>
				   </div>
				</div>
			</div>
			<div id="healthInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.healthInformation" defaultMessage="Health Information" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.proposedLengthOfStay" defaultMessage="Proposed Length of Stay in Mauritius (days)" />: </span><span className="confirm-value">{lengthOfStay}</span>
					</div>

					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.countriesVisited" defaultMessage="Countries visited during last 6 months" />: </span><span className="confirm-value">
							{countriesVisited.map((option, index) => {
								return  (
								<React.Fragment key={option}>
									{index !== 0 && ', '}
									{getCountryFromCode(option)}
								</React.Fragment>
								)
							})}
						</span>
					</div>
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.portOfEmbarkation" defaultMessage="Port Of Embarkation" />: </span><span className="confirm-value">{portOfEmbarkation}</span>
					</div>
				</div>
			</div>
			<div id="sufferingInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.areYouSufferingFrom" defaultMessage="Have you experienced any of the following within the past 14 days?" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.fever" defaultMessage="Fever" />: </span><span className="confirm-value">{convertBoolean(fever)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.soreThroat" defaultMessage="Sore Throat" />: </span><span className="confirm-value">{convertBoolean(soreThroat)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.jointPain" defaultMessage="Joint Pain" />: </span><span className="confirm-value">{convertBoolean(jointPain)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.cough" defaultMessage="Cough" />: </span><span className="confirm-value">{convertBoolean(cough)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.breathingdifficulties" defaultMessage="Breathing Difficulties" />: </span><span className="confirm-value">{convertBoolean(breathingDifficulties)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.rash" defaultMessage="Rash" />: </span><span className="confirm-value">{convertBoolean(rash)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.smellOrTaste" defaultMessage="Loss of sense of smell or taste" />: </span><span className="confirm-value">{convertBoolean(smellOrTaste)}</span>
					</div>
					<div className="col-xl-2 col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.contact" defaultMessage="Possible contact with COVID 19" />: </span><span className="confirm-value">{convertBoolean(contact)}</span>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-12 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.hadCovidBefore" defaultMessage="Have you tested positive for Covid-19 in the past 7 days?" />: </span><span className="confirm-value">{convertBoolean(hadCovidBefore)}</span>
					</div>
				</div>
			</div>
			
			<div id="vaccine" className="section">
			<div className="row">
				<div className="col-lg-12 ">
					<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.vaccinated" defaultMessage="Vaccinated" /></h5>
				</div>
			</div>
			<div className="row">
				<div className="col-lg-12 form-group">
					<span className="confirm-field"><FormattedMessage id="nav.item.vaccinated" defaultMessage="Vaccinated" />: </span><span className="confirm-value">{convertBoolean(vaccinated)}</span>
				</div>
			</div>
			
			<div className="row">
			<div className="col-lg-12 ">
				<span className="confirm-field"><FormattedMessage id="nav.item.firstVaccineName" defaultMessage="Name of First Vaccine" />: </span><span className="confirm-value">{firstVaccineName}</span>
			</div>
			<div className="col-lg-12 ">
				<span className="confirm-field"><FormattedMessage id="nav.item.dateOfFirstDose" defaultMessage="Date of First Dose" />: </span><span className="confirm-value">{dateOfFirstDose}</span>
			</div>
		</div>
		
		<div className="row">
		<div className="col-lg-12 ">
			<span className="confirm-field"><FormattedMessage id="nav.item.secondVaccineName" defaultMessage="Name of Second Vaccine" />: </span><span className="confirm-value">{secondVaccineName}</span>
		</div>
		<div className="col-lg-12 ">
			<span className="confirm-field"><FormattedMessage id="nav.item.dateOfSecondDose" defaultMessage="Date of Second Dose" />: </span><span className="confirm-value">{dateOfSecondDose}</span>
		</div>
	</div>
			
		</div>
			
			
			<div id="contactInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.phoneNumbers" defaultMessage="Phone Number(s) Where you can be reached if needed? Include country code and city code." /> </h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.mobilePhone" defaultMessage="Mobile Phone" />: </span><span className="confirm-value">{mobilePhone}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.fixedPhone" defaultMessage="Fixed Phone" />: </span><span className="confirm-value">{fixedPhone}</span>
					</div>
					<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.businessPhone" defaultMessage="Business Phone" />: </span><span className="confirm-value">{businessPhone}</span>
				</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.emailAddress" defaultMessage="Email Address" />: </span><span className="confirm-value">{email}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.nationality" defaultMessage="Nationality" />: </span>
						<span className="confirm-value">
							{passengerNationality && passengerNationality.map((option, index) => {
								return  (
								<React.Fragment key={option}>
									{index !== 0 && ', '}
									{option}
								</React.Fragment>
								)
							})}
						</span>
					</div>
				</div>
				  <div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.countryOfBirth" defaultMessage="Country of Birth" />: </span>
						<span className="confirm-value">
							{getCountryFromCode(countryOfBirth)}
						</span>
					</div>
					<div className="col-lg-4 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.countryOfPassportIssue" defaultMessage="Country of Passport Issue" />: </span>
					<span className="confirm-value">
						{getCountryFromCode(countryOfPassportIssue)}
					</span>
				</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.passportNumber" defaultMessage="Passport Number" />: </span><span className="confirm-value">{passportNumber}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.passportExpiryDate" defaultMessage="Date Of Expiry" />: </span><span className="confirm-value">{passportExpiryDate}</span>
					</div>
				</div>
			</div >
			<div id="permanentAddressInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.permanent Address" defaultMessage="Permanent Address" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-4 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.numberAndStreet" defaultMessage="Number and Street" />: </span><span className="confirm-value">{permanentAddress.numberAndStreet}</span>
					</div>
					<div className="col-lg-4 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.apartmentNumber" defaultMessage="Apartment Number" />: </span><span className="confirm-value">{permanentAddress.apartmentNumber}</span>
					</div>
					<div className="col-lg-4 form-group">
						<span className="confirm-field"><FormattedMessage id="nav.item.city" defaultMessage="City" />: </span><span className="confirm-value">{permanentAddress.city}</span>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.state/Province" defaultMessage="State/Province" />: </span><span className="confirm-value">{permanentAddress.stateProvince}</span>
					</div>
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.country" defaultMessage="Country" />: </span>
						<span className="confirm-value">
							{getCountryFromCode(permanentAddress.country)}
						</span>
					</div>
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.zipPostalCode" defaultMessage="Zip/Postal Code" />: </span><span className="confirm-value">{permanentAddress.zipPostalCode}</span>
					</div>
				</div>
			</div>
			<div id="temporaryAddressInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.temporaryAddress" defaultMessage="Temporary Address in Mauritius" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.hotelName" defaultMessage="Hotel Name" />: </span><span className="confirm-value">{temporaryAddress.hotelName}</span>
					</div>
					<div className="col-lg-4 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.numberAndStreet" defaultMessage="Number and Street" />: </span><span className="confirm-value">{temporaryAddress.numberAndStreet}</span>
					</div>
					<div className="col-lg-2 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.apartmentNumber" defaultMessage="Apartment Number" />: </span><span className="confirm-value">{temporaryAddress.apartmentNumber}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.localPhone" defaultMessage="Telephone No. in Mauritius" />: </span><span className="confirm-value">{temporaryAddress.localPhone}</span>
					</div>
				</div>
				<div className="row">
				   <div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.quarantineSite" defaultMessage="Quarantine Site" />: </span><span className="confirm-value">{temporaryAddress.quarantineSite}</span>
					</div>
					{/* <div className="col-lg-6 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.city" defaultMessage="City" />: </span><span className="confirm-value">{temporaryAddress.city}</span>
					</div> */}
					{/* <div className="col-lg-6 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.district" defaultMessage="District" />: </span><span className="confirm-value">{temporaryAddress.stateProvince}</span>
					</div> */}
					{/* <div className="col-lg-4 form-group ">
				<MySelect
					label={<FormattedMessage id="nav.item.country" defaultMessage="Country" />: {temporaryAddress.country"
				>
					<option value=""></option>
					<MyCountryOptions/>
				</MySelect>
				</div> */}
					{/* <div className="col-lg-4 form-group ">
				<span className="confirm-field"><FormattedMessage id="nav.item.zipPostalCode" defaultMessage="Zip/Postal Code" />: </span><span className="confirm-value">{temporaryAddress.zipPostalCode}</span>
				</div> */}
				</div>
			</div>
			<div id="contactPersonInformation" className="section">
			    <div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.contactPerson" defaultMessage="Contact person in Mauritius" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />: </span><span className="confirm-value">{contactPerson.lastName}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />: </span><span className="confirm-value">{contactPerson.firstName}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.address" defaultMessage="Address" />: </span><span className="confirm-value">{contactPerson.address}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.emailAddress" defaultMessage="Email Address" />: </span><span className="confirm-value">{contactPerson.email}</span>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-6 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.mobilePhone" defaultMessage="Mobile Phone" />: </span><span className="confirm-value">{contactPerson.mobilePhone}</span>
					</div>
				</div>
			</div>
			<div id="emergencyContactInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.emergencyContact" defaultMessage="Emergency Contact Information of someone who can reach you during the next 30 days" /></h5>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />: </span><span className="confirm-value">{emergencyContact.lastName}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />: </span><span className="confirm-value">{emergencyContact.firstName}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.address" defaultMessage="Address" />: </span><span className="confirm-value">{emergencyContact.address}</span>
					</div>
					<div className="col-lg-3 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.country" defaultMessage="Country" />: </span>
						<span className="confirm-value">
							{getCountryFromCode(emergencyContact.country)}
						</span>
					</div>
				</div>
				<div className="row">
					<div className="col-lg-6 form-group ">
						<span className="confirm-field"><FormattedMessage id="nav.item.mobilePhone" defaultMessage="Mobile Phone" />: </span><span className="confirm-value">{emergencyContact.mobilePhone}</span>
					</div>
				</div>
			</div>
				{familyTravelCompanions.length > 0 &&
			<div id="travelCompanionsInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.travelCompanionsFamily" defaultMessage="Travel Companions Family" /></h5>
					</div>
				</div>
				{familyTravelCompanions.map((companion) => {
					return <>
					<div className="row">
											<div className="col-lg-4 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />: </span><span className="confirm-value">{companion.lastName}</span>
					</div>
											<div className="col-lg-4 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />: </span><span className="confirm-value">{companion.firstName}</span>
					</div>
											<div className="col-lg-2 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.sex" defaultMessage="Sex" />: </span><span className="confirm-value">{companion.sex}</span>
					</div>
											<div className="col-lg-2 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.seat" defaultMessage="Seat" />: </span><span className="confirm-value">{companion.seatNumber}</span>
					</div>

					</div>
					<div className="row">
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />: </span><span className="confirm-value">{companion.dateOfBirth}</span>
					</div>
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.nationality" defaultMessage="Nationality" />: </span>
					<span className="confirm-value">
						{getCountryFromCode(companion.nationality)}
					</span>
					</div>
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.passportNumber" defaultMessage="Passport Number" />: </span><span className="confirm-value">{companion.passportNumber}</span>
					</div>
					</div>
					</>;
				})}
				</div>
	} 
				{nonFamilyTravelCompanions.length > 0 &&
			<div id="nonFamilyTravelCompanionInformation" className="section">
				<div className="row">
					<div className="col-lg-12 ">
						<h5 className="confirm-section-header"> <FormattedMessage id="nav.item.travelCompanionsNonFamily" defaultMessage="Travel Companions Non-Family" /></h5>
					</div>
				</div>
				{nonFamilyTravelCompanions.map((companion) => {
					return <>
					<div className="row">
											<div className="col-lg-4 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.lastFamilyName" defaultMessage="Last (Family) Name" />: </span><span className="confirm-value">{companion.lastName}</span>
					</div>
											<div className="col-lg-4 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.firstGivenName" defaultMessage="First (Given) Name" />: </span><span className="confirm-value">{companion.firstName}</span>
					</div>
											<div className="col-lg-2 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.sex" defaultMessage="Sex" />: </span><span className="confirm-value">{companion.sex}</span>
					</div>
											<div className="col-lg-2 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.seat" defaultMessage="Seat" />: </span><span className="confirm-value">{companion.seatNumber}</span>
					</div>

					</div>
					<div className="row">
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.dateOfBirth" defaultMessage="Date Of Birth" />: </span><span className="confirm-value">{companion.dateOfBirth}</span>
					</div>
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.nationality" defaultMessage="Nationality" />: </span>
					<span className="confirm-value">
						{getCountryFromCode(companion.nationality)}
					</span>
					</div>
											<div className="col-lg-3 form-group ">
					<span className="confirm-field"><FormattedMessage id="nav.item.passportNumber" defaultMessage="Passport Number" />: </span><span className="confirm-value">{companion.passportNumber}</span>
					</div>
					</div>
					</>;
				})}
				</div>
	}
		</div>
	}

}
export default Summary