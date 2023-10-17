import React from 'react'
import Header from './Header'
import Footer from './Footer'

export default function Layout(props) {
  const { children } = props
  return (
    <>
      <div className="d-flex flex-column min-vh-100">
      <Header onChangeLanguage={props.onChangeLanguage} isLoggedIn={props.isLoggedIn} logout={props.logout} keycloak={props.keycloak} />
      {children}
      <Footer/>
      </div>
    </>
  )
}