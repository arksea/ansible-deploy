function getApiAddr(): string {
  let uri = document.documentURI
  let n = uri.indexOf('//')
  let localAddr = 'http://localhost:8080'
  let addr = localAddr
  if (n > 0) {
    n = uri.indexOf('/', n+2)
  } else {
    n = uri.indexOf('/')
  }
  if (n > 0) {
    addr = uri.substring(0, n)
  }
  return addr.endsWith(':4200') ? localAddr : addr
}
export const environment = {
  production: true,
  accountApiUrl: getApiAddr(),
  apiUrl: getApiAddr()
}
