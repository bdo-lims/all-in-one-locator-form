import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Layout from './components/layout/Layout';
import { LocatorForm } from "./components";
import { HealthDesk } from "./components";
import { Support } from "./components";
import { SwabScreen } from "./components";
import { SecureRoute } from "./components/security";
import { IntlProvider } from 'react-intl';
import Keycloak from 'keycloak-js';

import messages_en from './i18n/en.json';
import messages_fr from './i18n/fr.json';

let i18nConfig = {
  locale: navigator.language.split(/[-_]/)[0],
  defaultLocale: 'en',
  messages: messages_en,
};

class App extends React.Component {

  constructor(props) {
    super(props);
    const keycloak = Keycloak('/resources/keycloak-config.json');
    this.state = { 
    	      authenticated: false,
    	      keycloak: keycloak}
    i18nConfig.locale = localStorage.getItem('locale') || navigator.language.split(/[-_]/)[0];
    switch (i18nConfig.locale) {
      case 'en': i18nConfig.messages = messages_en; break;
      case 'fr': i18nConfig.messages = messages_fr; break;
      default: i18nConfig.messages = messages_en; break;
    }
  }
  
  onAuth = () => {
	  this.setState({authenticated: true});
  }
  
  logout = () => {
	  this.state.keycloak.logout({redirectUri: window.location.href}).then((success) => {
          console.log("--> log: logout success ", success );
    	  this.setState({authenticated: false});
  }).catch((error) => {
          console.log("--> log: logout error ", error );
  });
  }
  
  isLoggedIn = () => {
	  return this.state.authenticated;
  }

  changeLanguage = (lang) => {
    switch (lang) {
      case 'en': i18nConfig.messages = messages_en; break;
      case 'fr': i18nConfig.messages = messages_fr; break;
      default: i18nConfig.messages = messages_en; break;
    }
    i18nConfig.locale = lang;
    this.setState({ locale: lang });
    localStorage.setItem('locale', lang);
  }

  onChangeLanguage = (e) => {
    e.preventDefault();
    let lang = e.target.lang;
    this.changeLanguage(lang);
  }

  render() {
    return (
      <IntlProvider
        locale={i18nConfig.locale}
        key={i18nConfig.locale}
        defaultLocale={i18nConfig.defaultLocale}
        messages={i18nConfig.messages}
      >
        <>
          <Router>
            <Layout onChangeLanguage={this.onChangeLanguage} logout={this.logout} isLoggedIn={this.isLoggedIn} >
                <Switch>
                <Route path="/" exact component={LocatorForm} />
                <SecureRoute path="/health-desk" exact component={() => <HealthDesk keycloak={this.state.keycloak}/>} keycloak={this.state.keycloak} onAuth={this.onAuth} logout={this.logout} isLoggedIn={this.isLoggedIn}/>
                <SecureRoute path="/support" exact component={() => <Support keycloak={this.state.keycloak}/>} keycloak={this.state.keycloak} requiredPermission="support" onAuth={this.onAuth} logout={this.logout} isLoggedIn={this.isLoggedIn}/>
                <SecureRoute path="/swab-screen" exact component={() => <SwabScreen keycloak={this.state.keycloak}/>} keycloak={this.state.keycloak} onAuth={this.onAuth} logout={this.logout} isLoggedIn={this.isLoggedIn}/>
                </Switch>
            </Layout>
            {/* <Footer /> */}
          </Router>
          </>
      </IntlProvider>
    );
          }
}

export default App;
