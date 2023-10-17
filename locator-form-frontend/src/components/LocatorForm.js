import React from 'react'
import { Formik, Form } from 'formik'
import { FormattedMessage, injectIntl } from 'react-intl'
import { Step1, Step2, Step3, Step4, Step5, Step11, Step6, Step7, Step8, Step9, Step10,Confirmation, Success } from './formSteps'
import { validationSchemaSteps } from './formModel/validationSchema'
import formInitialValues from './formModel/formInitialValues'
import ReCAPTCHA from "react-google-recaptcha";
import {
  createMuiTheme,
  MobileStepper,
  CircularProgress, 
  MuiThemeProvider
} from '@material-ui/core'

// const muiTheme = createMuiTheme({
//     overrides: {
//         MuiStepIcon: {
//             root: {
//                 color: '#000000', // or 'rgba(0, 0, 0, 1)'
//                 '&$active': {
//                     color: '#00aded',
//                 },
//                 '&$completed': {
//                     color: '#00aded',
//                 },
//             },
//         },
//     }
// });

const muiMobileTheme = createMuiTheme({
  overrides: {
    // MuiMobileStepper: {
    //   root :{
    //     '@media (prefers-color-scheme: dark)': {
    //     'background-color': '#1e1e1e'
    //     },
    //   }
    // },
    MuiLinearProgress: {
      root: {
        maxWidth: "100%",
        flexGrow: 1
      },
      progress: {
        width: "75%"
      },
      colorPrimary: {
        'background-color': '#00800050',
      },
      barColorPrimary: {
        'background-color': '#008000',
      },
    },
  }
})

const steps = [
  'nav.item.step.passengerType',
  'nav.item.step.flight',
  'nav.item.step.personalInfo',
  'nav.item.step.recentTravel',
  'nav.item.step.health',
  'nav.item.step.vaccine',
  'nav.item.step.contactInfo',
  'nav.item.step.addresses',
  'nav.item.step.emergencyContact',
  // 'nav.item.step.travelCompanion',
  'nav.item.step.confirmation',
]

class LocatorForm extends React.Component {

  constructor(props) {
    super(props)
    this.topOfQuestionsRef = React.createRef();
    this.state = {
      activeStep: 0,
      submitErrorKey: '',
      isSubmitting: false,
      submitSuccess: false,
      summaryAccessInfo: {},
      recaptchaVerified: false,
      recaptchaToken: '',
    }
  }

  componentDidMount() {
    var Tawk_API = Tawk_API || {},
            Tawk_LoadStart = new Date();
        (function() {
            var s1 = document.createElement("script"),
                s0 = document.getElementsByTagName("script")[0];
            s1.async = true;
            s1.src = 'https://embed.tawk.to/61cd5b53c82c976b71c413d4/1fo52pg1a';
            s1.charset = 'UTF-8';
            s1.setAttribute('crossorigin', '*');
            s0.parentNode.insertBefore(s1, s0);
        })();
  }

  onSubmitStep = () => {
    return steps.length - 1 === this.state.activeStep;
  }

  _renderStepContent(step, formikProps) {
    switch (step) {
      case 0:
        return <Step1 formikProps={formikProps} intl={this.props.intl} />
      case 1:
        return <Step2 formikProps={formikProps} intl={this.props.intl} />
      case 2:
        return <Step3 formikProps={formikProps} intl={this.props.intl} />
      case 3:
        return <Step4 formikProps={formikProps} intl={this.props.intl} />
      case 4:
        return <Step5 formikProps={formikProps} intl={this.props.intl} />
      case 5:
        return <Step6 formikProps={formikProps} intl={this.props.intl} />
      case 6:
        return <Step7 formikProps={formikProps} intl={this.props.intl} />
      case 7:
        return <Step8 formikProps={formikProps} intl={this.props.intl} />
      case 8:
        return <Step9 formikProps={formikProps} intl={this.props.intl} />
      // case 9:
        // return <Step10 formikProps={formikProps} intl={this.props.intl} />
      case 9:
        return <Confirmation formikProps={formikProps} intl={this.props.intl} /> 
      case 10:
        return <Success formikProps={formikProps} intl={this.props.intl} summaryAccessInfo={this.state.summaryAccessInfo} />
      default:
        return <div>Not Found</div>
    }
  }

  submitForm = (values) => {
    this.setState({ isSubmitting: true })
    var json = JSON.stringify(values)
    console.log(json)
    fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/locator-form?recaptchaToken=${this.state.recaptchaToken}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: json
    }).then(async response => {
      this.setState({ submitting: false })
      const summaryAccessInfo = await response.json()
      if (response.ok) {
        this.onSuccess(summaryAccessInfo)
        this.setState({ activeStep: this.state.activeStep + 1 })
      } else {
        throw new Error("didn't receive ok")
      }
    }).catch(err => {
      this.setState({ isSubmitting: false })
      console.log(err)
      this.setState({ 'submitErrorKey': 'error.submit' })
    })
  }

  handleSubmit = (values, actions) => {
    if (this.onSubmitStep()) {
      this.submitForm(values);
    } else {
      this.setState({ 
        activeStep: this.state.activeStep + 1,
      })
      var json = JSON.stringify(values)
      console.log(json);
      actions.setTouched({});
      actions.setSubmitting(false);
      this.scrollToTopOfQuestionsRef();
    }
  }

  _handleBack = (formikProps) => {
    this.setState({ 
      activeStep: this.state.activeStep - 1 ,
    })
    formikProps.setErrors({})
  }

  scrollToTopOfPage = () => {
    window.scrollTo(0, 0)
  }

  scrollToTopOfQuestionsRef = () => {
    // not working for some reason on step 4,6,8,9
  //   const inputElements = document.getElementsByTagName('input')
  // if (inputElements.length > 0) {
  //   inputElements.item(0).focus();
  // }
    console.log('scrollto: ' + this.topOfQuestionsRef.current.offsetTop - 125)
    window.scrollTo(0, this.topOfQuestionsRef.current.offsetTop - 125)
    // window.scrollTo(0, 0)
  }

  onSuccess = (summaryAccessInfo) => {
    this.setState({ 'submitSuccess': true, 'summaryAccessInfo': summaryAccessInfo })
    this.scrollToTopOfPage();
  }

  onCaptchaChange = (value) => {
    if (value === null) {
      this.setState({recaptchaVerified: false, recaptchaToken: ''});
    } else {
      this.setState({recaptchaVerified: true, recaptchaToken: value});
    }
  }

  render() {
    const currentValidationShema = validationSchemaSteps[this.state.activeStep];
    // const  currentValidationShema = this.state.activeStep === 11 - 1 ? validationSchemaSteps[this.state.activeStep] : validationSchemaSteps[20];
    console.log('step: ' + this.state.activeStep)
    return (
      <>
        <div className="container-fluid d-flex min-vh-100 flex-column content">
          <div className="row dark-row">
              <div className="col-lg-12 ">
                <div className="container pt-3">
                  {this.state.activeStep !== 10 && 
                	<div className="container">  
                  		<h3 className="question-header">
                  		<FormattedMessage id="nav.item.header" defaultMessage="Mauritius All-in-One Travel Digital Form" /></h3>
                  		<FormattedMessage id="nav.item.topOfForm" values={{
                        p: (...chunks) => <p>{chunks}</p>,
                        mohLink: <a target='_blank' href='https://health.govmu.org/Pages/default.aspx'>https://health.govmu.org/Pages/default.aspx</a>,
                        mohEmail: <a href = 'mailto: airport-ho@govmu.org'>airport-ho@govmu.org</a>
                      }}/>
                    </div>
                  }
                </div>
              </div>
          </div>
          <div className="row light-row flex-grow-1" ref={this.topOfQuestionsRef}>
              <div className="col-lg-12 ">
          <div className="container pt-3">
        {this.state.activeStep < steps.length &&
          <MuiThemeProvider theme={muiMobileTheme}>
            <MobileStepper variant="progress" className="stepper" steps={steps.length} activeStep={this.state.activeStep} position="static" />
          </MuiThemeProvider>}
        {/* <MuiThemeProvider theme={muiTheme}>
        <Stepper alternativeLabel className="stepper" activeStep={this.state.activeStep} >
          {steps.map((labelKey, index) => (
            <Step key={labelKey}>
              <StepLabel>{this.state.activeStep === index && <FormattedMessage id={labelKey}  />}</StepLabel>
            </Step>
          ))}
        </Stepper>
        </MuiThemeProvider> */}
        <Formik
          initialValues={formInitialValues}
          validationSchema={currentValidationShema}
          onSubmit={this.handleSubmit}
        >{formikProps => (
          <Form>
            <div className="row">
              <div className="col-lg-12 ">
                {this.state.activeStep < steps.length &&
                  <h4 className="question-header">
                      <FormattedMessage id={steps[this.state.activeStep]} />
                  </h4>
                }
              </div>
            </div>
            <div className="questions" id="questions">
              {this._renderStepContent(this.state.activeStep, formikProps)}
              {this.onSubmitStep() && 
                <div className="row captcha">
                  <div className="col-lg-12 ">
                    <ReCAPTCHA
                      sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY}
                      onChange={this.onCaptchaChange}
                    />
                  </div>
                </div>
                  
              }
              {this.state.activeStep < steps.length &&
                <div >
                  <button
                    disabled={this.state.activeStep === 0}
                    type="button"
                    className="back-button"
                    onClick={() => this._handleBack(formikProps)}>
                    <FormattedMessage id="nav.item.back" defaultMessage="Back" />
                  </button>
                  <button
                    disabled={this.state.isSubmitting || !formikProps.isValid || !formikProps.dirty || (this.onSubmitStep() && !this.state.recaptchaVerified) }
                    type="submit"
                    className={this.onSubmitStep() ? 'confirm-button' : 'next-button'}
                  >
                    {this.onSubmitStep() ? <FormattedMessage id="nav.item.submit" defaultMessage="Submit" /> : <FormattedMessage id="nav.item.next" defaultMessage="Next" />}
                  </button>
                  {this.state.isSubmitting && (
                    <CircularProgress
                      size={24}
                    />
                  )}
                  {this.state.submitErrorKey &&
                    <div className="error"><FormattedMessage id={this.state.submitErrorKey} defaultMessage="Error"/></div>
                  }
                </div>}
            </div>
          </Form >
        )}
        </Formik>
      </div>
      </div>
      </div>
      </div>
      </>
    )
  }
}

export default injectIntl(LocatorForm)