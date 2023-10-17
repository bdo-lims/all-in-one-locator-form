import React from "react"
import 'bootstrap/dist/css/bootstrap.css'
import './styles.css'
import { FormattedMessage, injectIntl } from 'react-intl'
import { faLanguage, faSignOutAlt } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { withRouter } from "react-router-dom"

class Header extends React.Component {
	
	constructor(props) {
	    super(props);
	    this.state = { keycloak: props.keycloak};
	  }

  logo = () => {
    return (<>
      <picture>
        {/* <source srcset={`images/logo-${this.props.intl.locale}-dark.png`} media="(prefers-color-scheme: dark)" /> */}
        <img className="logo" src={`images/logo-${this.props.intl.locale}.png`} alt="logo" />
      </picture>
    </>
    )
  }
  
  render() {
    return (
      <>
        <nav className="navbar navbar-expand-lg py-0 px-0 navbar-1 sticky-top">
          <div className="navbar-brand">
            <div className="my-navbar-button div-link">
              <a href="https://health.govmu.org/" target="_blank" rel="noopener noreferrer" >
                <span className="div-link-span"></span>
              </a>
              <div id="mynav-icon">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
              </div>
              <div id="mynav-item">
                <FormattedMessage id="nav.item.healthwebsite" defaultMessage="Ministry of Health and Wellness" />
              </div>
            </div>
          </div>
          <ul className="navbar-nav ml-auto navbar-right">
          {this.props.isLoggedIn() && <li className="nav-item">
        	<button type="button" className="btn language-select-button" onClick={this.props.logout}>
        		<FontAwesomeIcon id="sign-out" icon={faSignOutAlt} size="1x" />
      		</button>
          </li>
          }
            <li className="nav-item dropdown hover-dropdown languagepicker">
              <button type="button" className="btn language-select-button" data-toggle="dropdown">
                <FontAwesomeIcon id="language-icon" icon={faLanguage} size="2x" />
              </button>
              <div className="dropdown-menu">
                <button className="dropdown-item language-button"
                  lang="en"
                  onClick={(e) => {
                    this.props.onChangeLanguage(e)
                  }}
                >EN</button>
                <button className="dropdown-item language-button"
                  lang="fr"
                  onClick={(e) => {
                    this.props.onChangeLanguage(e)
                  }}
                >FR</button>
              </div>
            </li>
          </ul>
        </nav>
        <nav id="navbar-2" className="navbar navbar-expand-lg navbar-2 sticky-top">

          <div className="navbar-brand">
            {this.logo()}
          </div>
        </nav>
      </>
    )
  }
}

export default withRouter(injectIntl(Header))