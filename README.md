# Cordova Phone Plugin

A cordova plugin for getting information, status about phone in the device.

**This project only support android device and function for getting the incoming phone number yet.**

## Usage
Value `result` has next properties:
* incomingNumber (ex. 01011112222)
* formattedIncomingNumber (ex. 010-1111-2222)
* state (It have one of value in "idle", "offhook",
"riging")
```
phone.getCallerPhoneNumber((result) => {
  phone.echo(result.incomingNumber, function(){}, function(){});
}, () => {
  console.log('Fail to get phonen number!');
});
```
