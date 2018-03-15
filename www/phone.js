var argscheck = require('cordova/argscheck');
var channel = require('cordova/channel');
var utils = require('cordova/utils');
var exec = require('cordova/exec');
var cordova = require('cordova');

channel.createSticky('onCordovaInfoReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onCordovaInfoReady');

/**
 * This represents the mobile device, and provides properties for inspecting the model, version, UUID of the
 * phone, etc.
 * @constructor
 */
function Phone() {
  var me = this;

  channel.onCordovaReady.subscribe(function () {
    channel.onCordovaInfoReady.fire();
  });
}

/**
 * Get device info
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} failCallback The function to call when there is an fail getting the heading data. (OPTIONAL)
 */
Phone.prototype = {
  getCallerPhoneNumber: function (successCallback, failCallback) {
    execute(successCallback, failCallback, 'Phone', 'getCallerPhoneNumber', []);
  },
  echo: function (phrase, successCallback, failCallback ) {
    execute(successCallback, failCallback, 'Phone', 'echo', [phrase]);
  }
};

/**
 * @callback successCallback
 * @callback failCallback
 * @param {string} service
 * @param {string} action
 * @param {Object[]} args
 */
function execute(successCallback, failCallback, service, action, args) {
  // Check function type of callback funtions by 'fF'.
  argscheck.checkArgs('fF', service + '.' + action, [successCallback, failCallback]);
  exec(successCallback, failCallback, service, action, args);
}

module.exports = new Phone();
