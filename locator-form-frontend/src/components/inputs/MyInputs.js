import React from "react";
import { useField } from 'formik';
import styled from "@emotion/styled";
import { FormattedMessage } from 'react-intl';
import MultiCapableSelect from "./MultiCapableSelect"
import { PhoneInputField } from './PhoneInputField'
import TimePicker from 'rc-time-picker';
import moment from 'moment';


// Styled components ....

export const StyledSelect = styled(MultiCapableSelect)`
	  color: var(--blue);	
//	  width : 150px;
    `;

export const StyledPhoneInput = styled(PhoneInputField)`
	  color: var(--blue);	
//	  width : 150px;
    `;
    
export const StyledErrorMessage = styled.div` 
	  font-size: 12px;
	  color: var(--red-600);
//	  width: 150px;
	  margin-top: 0.25rem;
	  &:before {
	    content: " ";
	    font-size: 10px;
	  }
	`;

export const StyledLabel = styled.label`
	  margin-top: 1rem;	
  `;

export const StyledFieldSet =styled.fieldset`
   border: 2px solid;
   padding: 10px;	
`;

export const StyledLegend =styled.legend`
  margin: 10px;
  width: auto;	
  font-size: 17px; 
  padding: 5px;
  color: #3366ff;
`;

export const datetimeNow = () => {
  var now = new Date();

  var year = now.getFullYear();
  var month = now.getMonth() + 1;
  var day = now.getDate();
  var hours = now.getHours();
  var minutes = now.getMinutes();
  var seconds = now.getSeconds();

  if (month < 10)
    month = '0' + month.toString();
  if (day < 10)
    day = '0' + day.toString();

  var minDate = year + '-' + month + '-' + day + 'T' + hours + ':' + minutes + ':' + seconds;
  return minDate;
}
  
export const dateInputToday = () => {
  var now = new Date()

  var month = now.getMonth() + 1
  var day = now.getDate()
  var year = now.getFullYear()

  if (month < 10)
    month = '0' + month.toString()
  if (day < 10)
    day = '0' + day.toString()

  var minDate = year + '-' + month + '-' + day
  return minDate
}

export const dateInputYesterday = () => {
		var now = new Date()

		var month = now.getMonth() + 1
		var day = now.getDate() - 1;
		var year = now.getFullYear()

		if (month < 10)
			month = '0' + month.toString()
		if (day < 10)
			day = '0' + day.toString()

		var minDate = year + '-' + month + '-' + day
		return minDate
}

export const MyTextInput = ({ label, ...props }) => {
  // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
  // which we can spread on <input> and alse replace ErrorMessage
  // entirely.
  // <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
  const [field, meta] = useField(props);
  return (
    <>
      <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
      <div className="input-group">
        <input className="text-input form-control" 
          onKeyPress={e => { e.which === 13 && e.preventDefault() }} 
          {...field} 
          {...props} />
        {props.iconClickable &&
          <span className="input-group-btn">
            <button className="input-icon-button" 
            type="button"
            onClick={e => {
              props.iconOnClick(e)
						}}>{props.icon}</button>
          </span>
        }
      </div>
      {!props.iconClickable &&
        <i>{props.icon}</i>
      }
      <div className="error">
        <StyledErrorMessage>
          {props.additionalErrorMessage}
        </StyledErrorMessage>
      </div>
      {(meta.touched || props.displayErrorBeforeTouched) && meta.error ? (
        <div className="error">
          <StyledErrorMessage>
            <FormattedMessage id={meta.error} defaultMessage={meta.error} />
          </StyledErrorMessage>
        </div>
      ) : null}
    </>
  );
};

export const MyRadioInputGroup = ({ label, ...props }) => {
  // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
  // which we can spread on <input> and alse replace ErrorMessage
  // entirely.
  // <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
  const [field, meta] = useField(props);
  
  return (
    <>
      <StyledLabel 
        className={`${props.requireField ? 'required-field' : ''} input-label`} 
        htmlFor={props.id || props.name}
      >
        {label}
      </StyledLabel>
      {props.options.map(option => {
        let inputChange = field.onChange;
        if (props.onInputChange) {
          inputChange = props.onInputChange
        }
        return (
          <React.Fragment key={option.key}> 
            <label htmlFor={option.value}>
            <input type="radio" 
              // className="radio-button"
              id={option.value}
              {...field}
              value={option.value}
              onChange={inputChange}
              checked={field.value === option.value}
              disabled={props.disabled}
            />
            <FormattedMessage id={option.key} defaultMessage={option.value} />
          </label>
          </React.Fragment>
        )
      })}
      {/* {Object.keys(props.values).map(value =>
        <label key={value}>
          <Field name={props.name} type="radio" value={value} className="form-control radio-button" {...props}/>
          <FormattedMessage id={props.values[value]} defaultMessage={props.values[value]} />
        </label>
          )
      } */}
      {meta.touched && meta.error ? (
        <div className="error">
          <StyledErrorMessage>
            <FormattedMessage id={meta.error} defaultMessage={meta.error} />
          </StyledErrorMessage>
        </div>
      ) : null}
    </>
  );
};

export const MyHiddenInput = ({ label, ...props }) => {
  // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
  // which we can spread on <input> and alse replace ErrorMessage
  // entirely.
  // <StyledLabel htmlFor={props.id || props.name}>{label}</StyledLabel>
  const [field] = useField(props);
  return (
    <>
      <input type="hidden" className="text-input form-control" {...field} {...props} />
    </>
  );
};

export const MyCheckbox = ({ children, ...props }) => {
  const [field, meta] = useField({ ...props, type: "checkbox" });
  return (
    <>
      <label className="checkbox">
        <input {...field} {...props} type="checkbox" className="form-control" />
        {props.checkboxDescription}
      </label>
        {children}
      {meta.touched && meta.error ? (
        <div className="error"><StyledErrorMessage><FormattedMessage id={meta.error} defaultMessage={meta.error} /></StyledErrorMessage></div>
      ) : null}
    </>
  );
};

export const MySelect = ({ label, options, isMulti, isSearchable,  form, ...props }) => {
  // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
  // which we can spread on <input> and alse replace ErrorMessage
  // entirely.
  const [field, meta] = useField(props);
  return (
    <>
      <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
      <StyledSelect {...field} {...props}
        options={options}
        field={field}
        form={form}
        name={field.name}
		isMulti={isMulti}
		isSearchable={isSearchable}
		placeholder={props.placeholder} />
      {meta.touched && meta.error ? (
        <div className="error"><StyledErrorMessage><FormattedMessage id={meta.error} defaultMessage={meta.error} /></StyledErrorMessage></div>
      ) : null}
    </>
  );
};

export const MyPhoneInput = ({ label, options, ...props }) => {
  // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
  // which we can spread on <input> and alse replace ErrorMessage
  // entirely.
  const [field, meta] = useField(props);
  return (
    <>
      <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
      <StyledPhoneInput {...field} {...props}
        field={field}
        form={props.form}
        name={field.name}
        className="form-control"
         />
      {meta.touched && meta.error ? (
        <div className="error"><StyledErrorMessage><FormattedMessage id={meta.error} defaultMessage={meta.error} /></StyledErrorMessage></div>
      ) : null}
    </>
  );
};

const MyTimePicker = ({ 
  field,
  form: { setFieldValue, setFieldTouched },
  ...props }) => {
  const [meta] = useField(props);
  const format = "HH:mm";
  const value = field.value;
  
  return(
        <TimePicker 
          showSecond={false}
          style={{"border": "0px", "padding": "0"}}
          id="arrival-time"
          className="form-control" 
          onChange={e => {
            console.log(e)
            setFieldValue(field.name, e && e.format(format));
          }}
          onClose={()=> {
            setFieldTouched(field.name);
          }} 
          defaultValue={field.value ? moment(field.value, 'HH:mm') : null}
          format={format}
          allowEmpty
          disabled={props.disabled}
      />
    )
  }

  export const StyledTimePicker = styled(MyTimePicker)`
  color: var(--blue);	
  //	  width : 150px;
  `;

export const MyTimeInput = ({ label,
  field,
  placeholder,
  ...props }) => {
  const [meta] = useField(props);
  const format = "HH:mm";

  return(
  <>
      <StyledLabel className={`${props.requireField ? 'required-field' : ''} input-label`} htmlFor={props.id || props.name}>{label}</StyledLabel>
      <div className="input-group">
      <StyledTimePicker {...field} {...props}
        field={field}
        form={props.form}
        disabled={props.disabled}
      />
        </div>  
        {meta.touched && meta.error ? (
          <div className="error"><StyledErrorMessage><FormattedMessage id={meta.error} defaultMessage={meta.error} /></StyledErrorMessage></div>
        ) : null}
    </>
  )
}