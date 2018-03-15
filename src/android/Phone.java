package org.apache.cordova.phone;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

public class Phone extends CordovaPlugin {
  public static final String TAG = "Phone";

  public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
  public static final int READ_PHONE_STATE_REQ_CODE = 0;
  public static final int PERMISSION_DENIED_ERROR = 20;

  private CallStateListener callStateListener;
  private TelephonyManager telephonyManager;
  private CallbackContext callbackContext;

  /**
   * Constructor.
   */
  public Phone() {
  }

  /**
   * Sets the context of the Command. This can then be used to do things like
   * get file paths associated with the Activity.
   *
   * @param cordova The context of the main Activity.
   * @param webView The CordovaWebView Cordova is running in.
   */
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.d(TAG, "Initializing plugin.");
    callStateListener = new CallStateListener();
    telephonyManager = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
  }

  /**
   * Executes the request and returns PluginResult.
   *
   * @param action            The action to execute.
   * @param args              JSONArry of arguments for the plugin.
   * @param callbackContext   The callback id used when calling back into JavaScript.
   * @return                  True if the action was valid, false if not.
   */
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if ("getCallerPhoneNumber".equals(action)) {
      this.callbackContext = callbackContext;
      if (cordova.hasPermission(READ_PHONE_STATE)) {
        setCallStateListener();
      } else {
        cordova.requestPermission(this, READ_PHONE_STATE_REQ_CODE, READ_PHONE_STATE);
      }
      return true;
    }

    if ("echo".equals(action)) {
      String phrase = args.getString(0);
      Toast.makeText(webView.getContext(), phrase, Toast.LENGTH_LONG).show();
      return true;
    }

    return false;
  }

  //--------------------------------------------------------------------------
  // LOCAL METHODS
  //--------------------------------------------------------------------------
  protected void setCallStateListener() {
    telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    callStateListener.setCallbackContext(this.callbackContext);
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
      throws JSONException {
    for (int r : grantResults) {
      if (r == PackageManager.PERMISSION_DENIED) {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
        return;
      }
    }
    switch (requestCode) {
    case READ_PHONE_STATE_REQ_CODE:
      setCallStateListener();
      break;
    }
  }
}

class CallStateListener extends PhoneStateListener {

  private CallbackContext callbackContext;

  public void setCallbackContext(CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  public void onCallStateChanged(int state, String incomingNumber) {
    super.onCallStateChanged(state, incomingNumber);

    if (callbackContext == null)
      return;

    JSONObject result = new JSONObject();
    try {
      result.put("incomingNumber", incomingNumber);
      switch (state) {
      case TelephonyManager.CALL_STATE_IDLE:
        result.put("state", "idle");
        break;
      case TelephonyManager.CALL_STATE_OFFHOOK:
        result.put("state", "offhook");
        break;

      case TelephonyManager.CALL_STATE_RINGING:
        result.put("state", "riging");
        break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
    pluginResult.setKeepCallback(true);

    callbackContext.sendPluginResult(pluginResult);
  }
}