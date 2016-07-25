package cl.json;

import android.app.Activity;
import android.content.Intent;
import android.content.ActivityNotFoundException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Callback;

public class RNShareModule extends ReactContextBaseJavaModule implements ActivityEventListener {

  private Callback callback;

  public static final int RC_SHARE = 9101;

  public RNShareModule(final ReactApplicationContext reactContext) {
      super(reactContext);
      reactContext.addActivityEventListener(this);
  }

  @Override
  public String getName() {
    return "RNShare";
  }

  @ReactMethod
  public void open(ReadableMap options, Callback callback) {
    Intent shareIntent = createShareIntent(options);
    Intent intentChooser = createIntentChooser(options, shareIntent);
    this.callback = callback;

    try {
      getCurrentActivity().startActivityForResult(intentChooser, RC_SHARE);
    } catch (ActivityNotFoundException ex) {
    }
  }

  /**
   * Creates an {@link Intent} to be shared from a set of {@link ReadableMap} options
   * @param {@link ReadableMap} options
   * @return {@link Intent} intent
   */
  private Intent createShareIntent(ReadableMap options) {
    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    intent.setType("text/plain");
    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

    if (hasValidKey("share_subject", options)) {
      intent.putExtra(Intent.EXTRA_SUBJECT, options.getString("share_subject"));
    }

    if (hasValidKey("share_URL", options)) {
      intent.putExtra(Intent.EXTRA_TEXT, options.getString("share_URL"));
    }

    return intent;
  }

  /**
   * Creates an {@link Intent} representing an intent chooser
   * @param {@link ReadableMap} options
   * @param {@link Intent} intent to share
   * @return {@link Intent} intent
   */
  private Intent createIntentChooser(ReadableMap options, Intent intent) {
    String title = "Share";
    if (hasValidKey("title", options)) {
      title = options.getString("title");
    }

    Intent chooser = Intent.createChooser(intent, title);
    // chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    chooser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

    return chooser;
  }

  /**
   * Checks if a given key is valid
   * @param @{link String} key
   * @param @{link ReadableMap} options
   * @return boolean representing whether the key exists and has a value
   */
  private boolean hasValidKey(String key, ReadableMap options) {
    return options.hasKey(key) && !options.isNull(key);
  }

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
    if (requestCode == RC_SHARE && resultCode == Activity.RESULT_OK) {
      if (this.callback != null) {
        String intentData = "";
        if (intent != null) {
          intentData = intent.getData().toString();
        }
        this.callback.invoke("RequestCode: " + requestCode + "\nResult: " + resultCode + "\nIntentData: " + intentData);
      }
    }
  }

}
