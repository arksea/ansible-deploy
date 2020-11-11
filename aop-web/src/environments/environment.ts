// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

function getApiAddr(): string {
    let uri = document.documentURI
    let n = uri.indexOf('//')
    //let localAddr = 'http://localhost:8080'
    let localAddr = 'http://10.79.186.29:8062'
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
  production: false,
  accountApiUrl: getApiAddr(),
  apiUrl: getApiAddr()
}

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
