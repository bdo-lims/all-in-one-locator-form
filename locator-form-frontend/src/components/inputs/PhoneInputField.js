import React from "react";
import 'react-phone-number-input/style.css'
import PhoneInput from 'react-phone-number-input'

// interface Option {
//   label: string;
//   value: string;
// }

// interface CustomSelectProps extends FieldProps {
//   options: OptionsType<Option>;
//   isMulti?: boolean;
//   className?: string;
//   placeholder?: string;
// }

export const PhoneInputField = ({
	field,
	form: { touched, errors, setFieldValue },
	options,
	isMulti,
	placeholder,
	defaultCountryCode,
	...props
}) => {
	const onChange = (phoneNumber) => {
		setFieldValue(
			field.name,
			phoneNumber
		);
	};

	const getValue = () => {
		return field.value;
	};

	let defaultCountry = "US"
	if (defaultCountryCode) {
		defaultCountry = defaultCountryCode
	}

	return (
		<PhoneInput
			{...field}
			{...props}
			name={field.name}
			international={true}
			defaultCountry={defaultCountry}
			limitMaxLength={true}
			value={getValue()}
			onChange={onChange}
			placeholder={placeholder}
		/>
	);
};

export default PhoneInputField;
