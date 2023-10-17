import * as Yup from 'yup'
import { isValidPhoneNumber } from 'react-phone-number-input'
import { parse, isDate } from "date-fns";


function isBlankOrValidPhoneNumber(phoneNumber) {
	if (phoneNumber === undefined || phoneNumber === '+1' || phoneNumber === '') {
		return true;
	} else {
		return isValidPhoneNumber(phoneNumber);
	}
}

function isBlankOrValidMauritiusPhoneNumber(phoneNumber) {
	if (phoneNumber === undefined || phoneNumber.startsWith('+230') || phoneNumber === '') {
		return true;
	} else {
	    return false;
	}
}

function parseDateString(value, originalValue) {
	const parsedDate = isDate(originalValue)
    	? originalValue
    	: originalValue.length === 10 
			? parse(originalValue, "yyyy-MM-dd", new Date()) 
			: originalValue;

  return parsedDate;
}

function parseTimeString(value, originalValue) {
	if (originalValue === null) {
		return '';
	} else if (originalValue.length === 5 & originalValue.length !== 0) {
		return parse(originalValue, "HH:mm", new Date()).getHours() + ":" + parse(originalValue, "HH:mm", new Date()).getMinutes() ;
	} else {
		return new Date();
	}
}

const today = new Date();
today.setHours(0,0,0,0);

const yesterday = new Date();
yesterday.setDate(yesterday.getDate() - 1);
yesterday.setHours(0,0,0,0);

export const step1Validation = {
		travellerType: Yup.string()
		.oneOf(
			['resident', 'nonresident'],
			'error.invalid.selection')
		.required('error.required'),
}

export const step2Validation = {
	travellerType: Yup.string()
		.oneOf(
			['resident', 'nonresident'],
			'error.invalid.selection')
		.required('error.required'),
	airlineName: Yup.string()
		.max(50, 'error.dateInputTodaychar.max.exceeded')
		.required('error.required'),
	flightNumber: Yup.string()
		.max(15, 'error.char.max.exceeded')
		.required('error.required'),
	seatNumber: Yup.string()
		.max(15, 'error.char.max.exceeded'),
	arrivalDate: Yup.date().transform(parseDateString)
		.typeError("error.date.invalidformat")
		.min(yesterday, "error.date.past")
		.required('error.required'),
    arrivalTime: Yup.string().transform(parseTimeString)
		.typeError("error.time.invalidformat")
		.notOneOf(['NaN:NaN']),
	visitPurpose: Yup.string()
		.required('error.required')
		.when('travellerType', {
			is: 'resident',
			then: Yup.string()
				.oneOf(
					['citizen', 'crew_for_resident'],
					'error.invalid.selection'
				)
		})
		.when('travellerType', {
			is: 'nonresident',
			then: Yup.string()
				.oneOf(
					['business', 'study', 'wedding', 'visit_holiday', 'sport', 'spouse_of_mauritian' ,'resident_permit_holder', 'occupation_permit_holder'],
					'error.invalid.selection'
				)
		}),
		
};

export const step3Validation =  {
		travellerType: Yup.string()
		.oneOf(
			['resident', 'nonresident'],
			'error.invalid.selection')
		.required('error.required'),
	title: Yup.string()
		.oneOf(
			['mr', 'mrs', 'ms', 'miss', 'dr', 'other'],
			'error.invalid.selection'
		)
		.required('error.required'),
	firstName: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),
	lastName: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),
	middleInitial: Yup.string()
		.max(3, 'error.char.max.exceeded'),
	sex: Yup.string()
		.oneOf(
			['male', 'female'],
			'error.invalid.selection'
		)
		.required('error.required'),
	dateOfBirth: Yup.date().transform(parseDateString)
					.typeError("error.date.invalidformat")
					.max(today, "error.date.future")
					.required('error.required'),
	nationalID: Yup.string()
			.nullable(true)
			.when('travellerType', {
				is: 'resident',
				then: Yup.string().required('error.required')
			}),
};

export const step4Validation = {
		portOfEmbarkation: Yup.string()
		.max(50, 'error.char.max.exceeded'),
};

export const step5Validation = {
		hadCovidBefore: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
	fever: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
		// .required('error.required'),
	soreThroat: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
		// .required('error.required'),
	jointPain: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
		// .required('error.required'),
	cough: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
		// .required('error.required'),
	breathingDifficulties: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true),
		// .required('error.required'),
	rash: Yup.string()
		.oneOf(
			['true', 'false', null],
			'error.invalid.selection'
		)
		.nullable(true)
		// .required('error.required'),
};

export const step6Validation = {
		vaccinated: Yup.string()
		.oneOf(
				['true', 'false'],
			'error.invalid.selection'),
//		.required('error.required'),
		
		firstVaccineName: Yup.string()
		.max(50, 'error.char.max.exceeded'),
//		.required('error.required'),
	
		dateOfFirstDose: Yup.date().transform(parseDateString)
		.typeError("error.date.invalidformat")
		.max(today, "error.date.future"),
//		.required('error.required'),
		
		secondVaccineName: Yup.string()
		.max(50, 'error.char.max.exceeded'),
//		.required('error.required'),
            		
     	dateOfSecondDose: Yup.date().transform(parseDateString)
   		.typeError("error.date.invalidformat")
   		.max(today, "error.date.future")
        .when("dateOfFirstDose",
               (dateOfFirstDose, Yup) => dateOfFirstDose && Yup.min(dateOfFirstDose , "Date of second dose cannot be before date of first dose")),
};

export const step7Validation = {
		mobilePhone: Yup.string()
		.test('is-phone',
			'error.phone.invalid',
			value => isBlankOrValidPhoneNumber(value)
		),
	    fixedPhone: Yup.string()
		.test('is-phone',
			'error.phone.invalid',
			value => isBlankOrValidPhoneNumber(value)
		),
		businessPhone: Yup.string()
		.test('is-phone',
			'error.phone.invalid',
			value => isBlankOrValidPhoneNumber(value)
		),
	email: Yup.string()
		.email('error.email.invalid')
		.required('error.required'),
	confirmEmail: Yup.string().when('email', {
		is: email => (email && email.length > 0 ? true : false),
		then: Yup.string()
			.oneOf([Yup.ref('email')], "error.email.doesnotmatch")
			.required('error.required')
	}),

	countryOfBirth: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),
   
	passengerNationality: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),
		
	countryOfPassportIssue: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),
		
	passportNumber: Yup.string()
		.max(20, 'error.char.max.exceeded')
		.required('error.required'),
		
	passportExpiryDate: Yup.date().transform(parseDateString)
		.typeError("error.date.invalidformat")
		.min(today, "error.date.past")
		.required('error.required'),	
		
};

export const step8Validation = {
		permanentAddress: Yup.object().shape({
			travellerType: Yup.string()
			.oneOf(
				['resident', 'nonresident'],
				'error.invalid.selection')
			.required('error.required'),
			numberAndStreet: Yup.string()
				.max(50, 'error.char.max.exceeded')
				.when('travellerType', {
					is: 'resident',
					then: Yup.string().required('error.required')
				}),
			apartmentNumber: Yup.string()
				.max(20, 'error.char.max.exceeded'),
			city: Yup.string()
				.max(50, 'error.char.max.exceeded'),
//				.when('travellerType', {
//					is: 'resident',
//					then: Yup.string().required('error.required')
//				}),
			stateProvince: Yup.string()
				.max(50, 'error.char.max.exceeded'),
			country: Yup.string()
				.max(50, 'error.char.max.exceeded')
				.when('travellerType', {
					is: 'resident',
					then: Yup.string().required('error.required')
				}),
			zipPostalCode: Yup.string()
				.max(20, 'error.char.max.exceeded'),
				// .when('travellerType', {
				// 	is: 'resident',
				// 	then: Yup.string().required('error.required')
				// }),
		}),
		travellerType: Yup.string()
			.oneOf(
				['resident', 'nonresident'],
				'error.invalid.selection')
			.required('error.required'),
		lengthOfStay: Yup.string()
			.matches('^[0-9]*$', 'error.lengthOfStay.noninteger')
			.when('travellerType', {
				is: 'nonresident',
				then: Yup.string().required('error.required')
			}),
		temporaryAddress: Yup.object().shape({
			hotelName: Yup.string()
				.max(70, 'error.char.max.exceeded'),
			numberAndStreet: Yup.string()
				.max(80, 'error.char.max.exceeded'),
			   // .required('error.required'),
			apartmentNumber: Yup.string()
				.max(20, 'error.char.max.exceeded'),
			city: Yup.string()
				.max(50, 'error.char.max.exceeded'),
				// .required('error.required'),
			stateProvince: Yup.string()
				.max(50, 'error.char.max.exceeded'),
				// .required('error.required'),
			country: Yup.string()
				.max(50, 'error.char.max.exceeded'),
				// .required('error.required'),
			localPhone: Yup.string()
				.test('is-mauritius',
					'error.phone.invalid.mauritius',
					value => isBlankOrValidMauritiusPhoneNumber(value)
				)
				.test('is-phone',
					'error.phone.invalid',
					value => isBlankOrValidPhoneNumber(value)
				)
				.required('error.required'),
			quarantineSite: Yup.string()
				.max(80, 'error.char.max.exceeded'),		
		}),
		contactPerson: Yup.object().shape({
			lastName: Yup.string()
				.max(50, 'error.char.max.exceeded'),
			firstName: Yup.string()
				.max(50, 'error.char.max.exceeded'),
			address: Yup.string()
				.max(50, 'error.char.max.exceeded'),
			mobilePhone: Yup.string()
				.test('is-phone',
					'error.phone.invalid',
					value => isBlankOrValidPhoneNumber(value)
				),
			email: Yup.string()
				.email('error.email.invalid')	
		}),
	};

export const step9Validation = {
		emergencyContact: Yup.object().shape({
			lastName: Yup.string()
				.max(50, 'error.char.max.exceeded')
				.required('error.required'),
			firstName: Yup.string()
				.max(50, 'error.char.max.exceeded')
				.required('error.required'),
			address: Yup.string()
				.max(50, 'error.char.max.exceeded')
				.required('error.required'),
			country: Yup.string()
				.max(50, 'error.char.max.exceeded'),
				//.required('error.required'),
			mobilePhone: Yup.string()
				.test('is-phone',
					'error.phone.invalid',
					value => isBlankOrValidPhoneNumber(value)
				).required('error.required'),
		}),
	};

export const step10Validation = {
		familyTravelCompanions: Yup.array()
		.of(
			Yup.object().shape({
				lastName: Yup.string()
					.required('error.required'),
				firstName: Yup.string()
					.required('error.required'),
				middleInitial: Yup.string()
					.max(3, 'error.char.max.exceeded'),
				// seatNumber: Yup.string()
				// 	.required('error.required'),
				dateOfBirth: Yup.date().transform(parseDateString)
					.typeError("error.date.invalidformat")
					.max(today, "error.date.future")
					.required('error.required'),
				sex: Yup.string()
					.oneOf(
						['male', 'female'],
						'error.invalid.selection'
					)
					.required('error.required'),
				nationality: Yup.string()
					.required('error.required'),
				passportNumber: Yup.string()
					.required('error.required'),
			})
		),

	nonFamilyTravelCompanions: Yup.array()
		.of(
			Yup.object().shape({
				lastName: Yup.string()
					.required('error.required'),
				firstName: Yup.string()
					.required('error.required'),
				middleInitial: Yup.string()
					.max(3, 'error.char.max.exceeded'),
				// seatNumber: Yup.string()
				// 	.required('error.required'),
				dateOfBirth: Yup.date().transform(parseDateString)
					.typeError("error.date.invalidformat")
					.max(today, "error.date.future")
					.required('error.required'),
				sex: Yup.string()
					.oneOf(
						['male', 'female'],
						'error.invalid.selection'
					)
					.required('error.required'),
				nationality: Yup.string()
					.required('error.required'),
				passportNumber: Yup.string()
					.required('error.required'),
			})
		),
};

export const step11Validation = {
		acceptedTerms: Yup.boolean()
		.required('error.required')
		.oneOf([true], 'error.terms.unaccepted'),
};

export const healthDesk = {
	healthOffice: Yup.string()
		.max(50, 'error.char.max.exceeded'),
	locality: Yup.string()
		.max(50, 'error.char.max.exceeded')
		.required('error.required'),	

	arrivalDate: Yup.date().transform(parseDateString)
		.typeError("error.date.invalidformat")
		.required('error.required')
		.when('arrivalDateOverride', {
			is: false,
			then: Yup.date().transform(parseDateString)
				.max(today, "error.date.future")
				.min(yesterday, "error.date.past")
		}),
	
	testKitId: Yup.string()
		.matches( process.env.REACT_APP_TEST_KIT_REGEX ? `${process.env.REACT_APP_TEST_KIT_REGEX}` : "^[0-9]{20}$", 'error.pattern.match.testkit')
		.test('checkDuplicateTestKit', 'error.duplicate', async function (searchValue) {
			if (!searchValue) {
				return true;
			}
			const unique = await fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/swab/servicerequest/${searchValue}`, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${localStorage.getItem("react-token")}`,
				},
			}).then(async response => {
				if (response.status === 404) {
					return true;
				} else if (response.ok) {
					return false;
				} else {
					throw new Error("couldn't reach the backend service to see if test kit is unique")
				}
			}).catch(err => {
				console.log(err)
				return false;
			});
			return unique;
		}),

	reviewed: Yup.boolean()
		.required('error.required')
		.oneOf([true], 'error.required'),
};

export const validationSchemaSteps = [
	//step 1
	Yup.object().shape(step1Validation),
	//step 2
	Yup.object().shape(step2Validation),
	//step 3
	Yup.object().shape(step3Validation),
	//step 4
	Yup.object().shape(step4Validation),
	//step 5
	Yup.object().shape(step5Validation),
	//step 11
	Yup.object().shape(step6Validation),
	//step 6
	Yup.object().shape(step7Validation),
	//step 7
	Yup.object().shape(step8Validation),
	//step 8
	Yup.object().shape(step9Validation),
	//step 9
	// Yup.object().shape(step10Validation),
	//step 10
	Yup.object().shape(step11Validation),

]

export const healthDeskValidationSchema = Yup.object().shape({...step1Validation, ...step2Validation, ...step3Validation, ...step4Validation, ...step5Validation, ...step6Validation, ...step7Validation, ...step8Validation, ...step9Validation, ...step10Validation, ...step11Validation, ...healthDesk});

export const pioValidationSchema = Yup.object().shape({...step1Validation, ...step2Validation, ...step3Validation, ...step4Validation, ...step5Validation, ...step6Validation, ...step7Validation, ...step8Validation, ...step9Validation, ...step10Validation, ...step11Validation});
