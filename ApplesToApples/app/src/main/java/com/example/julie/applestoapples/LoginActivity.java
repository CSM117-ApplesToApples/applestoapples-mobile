package com.example.julie.applestoapples;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int CREATE_GAME = 0;
    private static final int JOIN_GAME = 1;

    /**
     * A dummy authentication store containing known user names and groups.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "vern:12345", "vern2:45678"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private EditText mGroupIdView;
   private View mLoginFormView;
    private TextView mBackView;

    private int createOrJoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        populateAutoComplete();

        mGroupIdView = (EditText) findViewById(R.id.groupId);
        mGroupIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mCreateGameButton = (Button) findViewById(R.id.create_game_button);
        mCreateGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getLogin(CREATE_GAME);
            }
        });

        Button mJoinGameButton = (Button) findViewById(R.id.join_game_button);
        mJoinGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getLogin(JOIN_GAME);
            }
        });

        Button mBeginGameButton = (Button) findViewById(R.id.begin_game_button);
        mBeginGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mBackView = (TextView) findViewById(R.id.back_text);
        mBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mNameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * When "join game" or "create game" buttons are pushed, displays field to enter name
     * (and game ID - only when joining an existing game), in addition to "begin game" button.
     */
    private void getLogin(int type) {
      createOrJoin = type;
       findViewById(R.id.join_game_button).setVisibility(View.GONE);
        findViewById(R.id.create_game_button).setVisibility(View.GONE);
        mBackView.setVisibility(View.VISIBLE);
        mNameView.setVisibility(View.VISIBLE);
      if (type == JOIN_GAME)
            mGroupIdView.setVisibility(View.VISIBLE);
        findViewById(R.id.begin_game_button).setVisibility(View.VISIBLE);
  }

    public void goBack() {
        mBackView.setVisibility(View.GONE);
        mNameView.setVisibility(View.GONE);
        mGroupIdView.setVisibility(View.GONE);
        findViewById(R.id.begin_game_button).setVisibility(View.GONE);
         findViewById(R.id.join_game_button).setVisibility(View.VISIBLE);
        findViewById(R.id.create_game_button).setVisibility(View.VISIBLE);
    }

   public int getCreateOrJoin() {
        return createOrJoin;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mGroupIdView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String groupId = mGroupIdView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        

        // Check for a valid group ID, if the user entered one.
       if (createOrJoin == JOIN_GAME && (TextUtils.isEmpty(groupId) || !isGroupIdValid(groupId))) {
            mGroupIdView.setError(getString(R.string.error_invalid_groupId));
            focusView = mGroupIdView;
            cancel = true;
        }

        // Check for a valid name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            Log.e("debugging", "login canceled");
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(name, groupId, createOrJoin);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isGroupIdValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
          //  mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mNameView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mGroupId;
        private final int mGameType;
        Player mLoginPlayer;

        UserLoginTask(String name, String groupId, int createOrJoin) {
            mName = name;
            mGroupId = groupId;
            mGameType = createOrJoin;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.



            // TODO: register the new account here.
            System.out.println("-------Calling HTTP Handler--------");
            Log.i("UserLoginTask", mName);
            Log.i("UserLoginTask", mGroupId);
            if (mGameType == JOIN_GAME)
                return Http_Handler.joinGroup(this, mName, mGroupId);
            else
                return Http_Handler.createGroup(this, mName);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                //main.putExtra("username", mEmail);
                main.putExtra("groupID", mGroupId);
                main.putExtra("player", mLoginPlayer);
                startActivity(main);
            } else {
                mGroupIdView.setError("Something went wrong when trying to sign in");
                mGroupIdView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

