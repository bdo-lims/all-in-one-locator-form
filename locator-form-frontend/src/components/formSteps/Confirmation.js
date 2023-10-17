import React from "react"
import { FormattedMessage } from 'react-intl'
import { MyCheckbox } from '../inputs/MyInputs'
import Summary from './Summary'



class Confirmation extends React.Component {

	render() {
		return <div>
			<Summary formikProps={this.props.formikProps}/>
			<div className="step" id="step10">
				<div id="acceptInformation" className="section">
					<div className="row">
						<div className="col-lg-12 ">
							<MyCheckbox
								className="required-field" name="acceptedTerms">
								<FormattedMessage id="nav.item.declareInformation" defaultMessage="I/Guardian declare that the information I/Guardian have given is true and complete. I/Guardian understand that I/Guardian shall commit an offence if I/Guardian fail to fill the form or knowingly submit false information." />
							</MyCheckbox>
						</div>
					</div>
				</div>
			</div>
			
		</div>
	}

}
export default Confirmation