import React from "react";
import Select from "react-select";

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

export const MultiCapableSelect = ({
  field,
  form: { setFieldValue, setFieldTouched },
  options,
  placeholder,
  isMulti = false,
  isSearchable = false,
  ...props
}) => {

  const onChange = (option) => {
    setFieldValue(field.name, 
      isMulti ? 
        option !== null ? 
          option.map((item) => item.value) 
          : []  
        : option.value );
  };

  const getValue = () => {
    if (options) {
      return isMulti
        ? options.filter(option => field.value.indexOf(option.value) >= 0)
        : options.find(option => option.value === field.value);
    } else {
      return isMulti ? [] : ("");
    }
  };

  return (
    <Select
        {...field}
        {...props}
      name={field.name}
      value={getValue()}
      onChange={async e => {
        await onChange(e); 
        setFieldTouched(field.name);
      }}
      placeholder={placeholder}
      options={options}
      isSearchable={isSearchable}
      isDisabled={props.disabled}
      onBlur={(e) => {
        e.preventDefault();
        setFieldTouched (field.name)
        // e.target.name = field.name;
        // field.onBlur(e);
      }}
      isMulti={isMulti}
    />
  );
};

export default MultiCapableSelect;
