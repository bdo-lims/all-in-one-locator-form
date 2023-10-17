import React from "react"
import { FormattedMessage } from 'react-intl'
import Summary from './Summary'
import { CircularProgress } from '@material-ui/core'
import {isMobile, isIE} from 'react-device-detect';
// import Barcode from 'react-barcode'
// import { jsPDF } from 'jspdf'
// import html2canvas from 'html2canvas'

class Success extends React.Component {

	constructor(props) {
		super(props)
		let [accessInfo] = props.summaryAccessInfo
		this.state = {
			printing: false,
			accessId: accessInfo.id,
			accessPass: accessInfo.pass
		}
	}

	downloadFile = async () => {
		const link = document.createElement('a');
		var file = window.URL.createObjectURL(await this.fetchPDFAsPromise());
		link.href = file;
		link.download = `locator-form-summary-${+new Date()}.pdf`;
  		link.click();
		this.setState({ printing: false });
		setTimeout(function () {
			window.URL.revokeObjectURL(file);
		}, 100);
	}

	downloadFileIE = async () => {
		var blob = await this.fetchPDFAsPromise();
		this.setState({ printing: false });
		// IE
		if (window.navigator && window.navigator.msSaveOrOpenBlob) {
			window.navigator.msSaveOrOpenBlob(blob, `locator-form-summary-${+new Date()}.pdf`);
			return;
		}

	}

	openFileInNewTab = () => {
		var myWindow = window.open("", "_blank")
		this.writeFileIntoWindow(this.fetchPDFAsPromise(), myWindow)
	}

	writeFileIntoWindow = async (blobPromise, myWindow) => {
		var blob = await blobPromise;
		var file = window.URL.createObjectURL(blob);
		myWindow.location.href = file;
		this.setState({ printing: false });
		setTimeout(function () {
			window.URL.revokeObjectURL(file);
		}, 1000);
	}

	fetchPDFAsPromise = async () => {
		return fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/summary/${this.state.accessId}`, {
			method: 'GET',
			cache: 'no-cache',
			headers: {
				Authorization: this.state.accessPass
			},
		}).then(async response => {
			return await response.blob();
		}).catch(err => {
			console.log(err);
			this.setState({ printing: false });
		})
	}

	printSummaryPDF = () => {
		// window.open(`${process.env.REACT_APP_DATA_IMPORT_API}/summary/${this.state.accessId}`, "_blank");
		this.setState({ printing: true })
		if (isMobile) {
			this.downloadFile();
		} else if (isIE) {
			this.downloadFileIE();
		} else {
			this.openFileInNewTab();
		}


		// window.print()

		// var json = JSON.stringify(values)
		// fetch(`${process.env.REACT_APP_DATA_IMPORT_API}/summaryDownload`, {
		//   method: 'POST',
		//   headers: {
		//     'Content-Type': 'application/json',
		//   },
		//   body: json
		// }).then(async response => {
		//   const pdf = await response.blob()
		//   if (response.ok) {
		//     this.onSuccess(labelContentPairs)
		//     this.setState({ activeStep: this.state.activeStep + 1 })
		//   } else {
		//     throw new Error("didn't receive ok")
		//   }
		// }).catch(err => {
		//   this.setState({ isSubmitting: false })
		//   console.log(err)
		//   this.setState({ 'submitErrorKey': 'error.submit' })
		// })

		// 	var pdf = new jsPDF('p', 'mm', 'letter');

		// 	html2canvas(document.getElementById('full-summary')).then((canvas) => {
		// 		const imgData = canvas.toDataURL('image/png');
		// 		var imgWidth = 210; 
		// 		var pageHeight = 295;  
		// 		var imgHeight = canvas.height * imgWidth / canvas.width;
		// 		var heightLeft = imgHeight;
		// 		var position = 19;

		// 		pdf.addImage(imgData, 'PNG', 19, position, imgWidth - (19 * 2), imgHeight );
		// 		heightLeft -= pageHeight;

		// 		while (heightLeft >= 0) {
		// 			position = heightLeft - imgHeight ;
		// 			pdf.addPage();
		// 			pdf.addImage(imgData, 'PNG', 19, position, imgWidth - (19 * 2), imgHeight + (19));
		// 			heightLeft -= pageHeight;
		// 		}
		// 		pdf.save("locatorForm.pdf");
		// 	});
	}

	render() {
		return (<div>
			{/* <iframe id="ifmcontentstoprint" style={{height: '0px', width: '0px', position: 'absolute'}}></iframe> */}
			<div className="row no-print">
				<div className="col-lg-12 success-large text-center" style={{ whiteSpace: 'pre-wrap' }}>
					<FormattedMessage id="submit.success.msg" />
				</div>
			</div>
			<div className="row no-print">
				<div className="col-lg-12 success-large text-center">
					<button type="button" className="confirm-button" onClick={this.printSummaryPDF}><FormattedMessage id="summary.print.button" defaultMessage="Print Summary" /></button>
					{this.state.printing && (
						<CircularProgress
							size={24}
						/>
					)}
				</div>
			</div>
			<div id="full-summary">
				<Summary formikProps={this.props.formikProps} />
				{/* {Object.entries(this.props.labelContentPairs).map(([accessInfo, labelContentPair]) => {
					return (
						<div className="row print-page-break-after">
							<div className="col-lg-12 print-no-break">
								<div className="barcode">
									<h5>{labelContentPair.label}:</h5>
									<Barcode value={labelContentPair.barcodeContent} />
								</div>
							</div>
						</div>);
				})} */}
			</div>
		</div>
		)
	}

}
export default Success