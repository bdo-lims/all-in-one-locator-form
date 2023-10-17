import React from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import IdleTimer from 'react-idle-timer'
import { confirmAlert } from 'react-confirm-alert'; 
import 'react-confirm-alert/src/react-confirm-alert.css' // Import css

const accessTokenTimeout = 1000 * 60 * 1; // milliseconds between refreshing access token (disabled once idle)
const accessTokenTimeoutBuffer = 1000 * 10; // buffer for refreshing acces token (subtracted from accessTokenTimeout)
const accessTokenNumRetries = 3; // retry for refreshing tokens
const idleTimeout = 1000 * 60 * 15 ; // milliseconds until idle warning will appear
const idleWarningTimeout = 1000 * 60 * 1; // milliseconds until logout is automatically processed from idle warning

class SecureRoute extends React.Component {

	constructor(props) {
	    super(props);
		this.idleTimer = null
	    this.state = { 
	    	      authenticated: false,
				  isIdle: false,
				  refreshTimeoutSet: false,
				  requiredPermission: props.requiredPermission,
	    }
	  }
	
	  componentDidMount() {
		  const keycloak = this.props.keycloak;
		  keycloak.init({onLoad: 'login-required'}).then(async authenticated => {
		        if (authenticated) {
				    console.info("Authenticated");
				    this.setState({ authenticated: true });
			        this.props.onAuth();
			        localStorage.setItem("react-token", keycloak.token);
			        localStorage.setItem("react-refresh-token", keycloak.refreshToken);
					
					this.setRefreshTokenTimer(keycloak);
				}
			}).catch(err => {
				  console.error("Authenticated Failed");
		    });  
	
	  }

	  handleOnAction = (event) => {
	  }
	
	  handleOnActive = (event) => {
		console.log('user is active', event)
		this.setState({isIdle: false});
	  }
	
	  handleOnIdle = (event) => {
		console.log('user is idle', event)
		this.setState({isIdle: true});

		const timer = () => setTimeout(() => { 
			this.props.logout();
		}, idleWarningTimeout);
		const timeoutEventID = timer();

		const options = {
			title: 'Still there?',
			message: 'user session is about to time out',
			buttons: [
			  {
				label: 'Yes',
				onClick: () => { 
					clearTimeout(timeoutEventID);
					this.refreshToken();
				}
			  }
			],
		}
		confirmAlert(options);
	  }

	  setRefreshTokenTimer = () =>  {
		  this.setState({refreshTimeoutSet: true});
		setTimeout(() => {
			this.setState({refreshTimeoutSet: false});
			if (!this.state.isIdle) {
				this.refreshToken();
			}
		}, accessTokenTimeout - (accessTokenTimeoutBuffer));
	  }

	  refreshToken = (retries=accessTokenNumRetries) => {
		this.props.keycloak.updateToken(accessTokenTimeout / 1000).then(async refreshed => {
			if (refreshed){
				console.log('refreshed '+ new Date());
				localStorage.setItem("react-token", this.props.keycloak.token);
				localStorage.setItem("react-refresh-token", this.props.keycloak.refreshToken);
			} else {
				console.log('not refreshed ' + new Date());
				throw new Error("couln't refresh token");
			}
		  }).catch(err => {
			console.error('Failed to refresh token ' + new Date());
			if (retries > 0) {
			   this.refreshToken(retries - 1);
			}
		  });
		if (!this.state.refreshTimeoutSet) {
			this.setRefreshTokenTimer();
		}
	  }
	  
	  render() {
		      if (!this.state.authenticated) {
				return (<div>Not authenticated</div>);
				} else if (this.state.requiredPermission && !this.props.keycloak.hasRealmRole(this.state.requiredPermission)) {
					return (<div>Not allowed to access page</div>);
				} else {
					return (
							<>
							<IdleTimer
							  ref={ref => { this.idleTimer = ref }}
							  timeout={idleTimeout}
							  onActive={this.handleOnActive}
							  onIdle={this.handleOnIdle}
							  onAction={this.handleOnAction}
							  debounce={250}
							  />
						  <Route {...this.props}/>
						  </>
					);
				}
	  }
}

export default SecureRoute;
